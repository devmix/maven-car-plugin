/*
 * (C) Copyright 2015 Sergey Grachev (sergey.grachev@yahoo.com)
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.github.devmix.esb.car.plugin.builders;

import com.github.devmix.esb.car.plugin.registry.RegistryMediaTypesBundle;
import com.github.devmix.esb.car.plugin.utils.CommonUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.utils.StringUtils;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Not thread-safe
 *
 * @author Sergey Grachev
 */
public final class RegistryArtifactsBuilder extends AbstractArtifactsBuilder<RegistryArtifactsBuilder> {

    public static final String RESOURCES_TYPE = "registry";
    public static final String REGISTRY_INFO_XML = "registry-info.xml";

    private boolean allInOne;
    private String allInOneName;
    private Map<String, String> artifactsMediaTypes;
    private RegistryMediaTypesBundle mediaTypes;

    private RegistryArtifactsBuilder() {
    }

    public static RegistryArtifactsBuilder newInstance() {
        return new RegistryArtifactsBuilder();
    }

    public RegistryArtifactsBuilder allInOne(final boolean allInOne) {
        this.allInOne = allInOne;
        return this;
    }

    public RegistryArtifactsBuilder allInOneName(final String allInOneName) {
        this.allInOneName = allInOneName;
        return this;
    }

    public RegistryArtifactsBuilder mediaTypes(final RegistryMediaTypesBundle mediaTypes) {
        this.mediaTypes = mediaTypes;
        return this;
    }

    public void build() throws MojoFailureException {
        check();

        if (!Files.exists(Paths.get(configDir))) {
            return;
        }

        try {
            this.artifactsMediaTypes = readArtifactsTypesList();
            if (allInOne) {
                createAllInOneRegistryArtifacts();
            } else {
                createRegistryArtifacts();
            }
        } catch (final IOException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void createRegistryArtifacts() throws IOException, MojoFailureException {
        Files.createDirectories(Paths.get(outputDirectory));
        try (DirectoryStream<Path> confStream = Files.newDirectoryStream(Paths.get(configDir))) {
            for (final Path path : confStream) {
                if (Files.isDirectory(path)) {
                    createArtifacts(path);
                }
            }
        }
    }

    private void createArtifacts(final Path root) throws IOException, MojoFailureException {
        try (DirectoryStream<Path> artifactsStream = Files.newDirectoryStream(root)) {
            for (final Path path : artifactsStream) {
                if (Files.isDirectory(path)) {
                    createArtifacts(path);
                } else {
                    createArtifact(path);
                }
            }
        }
    }

    private void createArtifact(final Path fromFile) throws IOException, MojoFailureException {
        final String fileName = fromFile.getFileName().toString();
        final String registryFile = registryFileOf(fromFile);
        final String name = CommonUtils.removeFileExtension(fileName);
        final Path dir = Paths.get(outputDirectory, name + "_" + version);
        final Path resourcesDir = resourcesDirOf(dir);
        final Path file = Paths.get(resourcesDir.toString(), fileName);

        if (!Files.exists(resourcesDir)) {
            Files.createDirectories(resourcesDir);
        }

        Files.copy(fromFile, file, StandardCopyOption.REPLACE_EXISTING);

        createArtifactXml(name, dir);

        try {
            final String mediaType = detectMediaType(registryFile);
            if (StringUtils.isBlank(mediaType)) {
                throw new MojoFailureException("Unknown media type for " + registryFile);
            }
            final String registryPath = registryPathOf(fromFile);
            final Path registryInfoFile = Paths.get(dir.toString(), REGISTRY_INFO_XML);
            final String xml = new XmlBuilder().node("resources").node("item")
                    .node("file").content(fileName).parent()
                    .node("path").content("/" + registryPath).parent()
                    .node("mediaType").content(mediaType)
                    .builder().asString();
            try (FileOutputStream fis = new FileOutputStream(registryInfoFile.toFile())) {
                fis.write(xml.getBytes());
            }
        } catch (final XmlBuilder.XmlBuildException e) {
            throw new MojoFailureException("Can't create " + REGISTRY_INFO_XML, e);
        }

        artifactsList.add(name, version, serverRole, true, artifactTypes.of(RESOURCES_TYPE).getPriority());
    }

    private void createAllInOneRegistryArtifacts() throws IOException, MojoFailureException {
        final Path dir = Paths.get(outputDirectory, allInOneName + "_" + version);
        final Path registryInfoXml = Paths.get(dir.toString(), REGISTRY_INFO_XML);

        boolean hasArtifacts = false;
        final XmlBuilder.Node registryInfoNode = new XmlBuilder().node("resources");
        try (DirectoryStream<Path> confStream = Files.newDirectoryStream(Paths.get(configDir))) {
            for (final Path path : confStream) {
                if (Files.isDirectory(path)) {
                    hasArtifacts |= createAllInOneArtifacts(path, registryInfoNode);
                }
            }
        }

        if (hasArtifacts) {
            createArtifactXml(allInOneName, dir);

            try (FileOutputStream fis = new FileOutputStream(registryInfoXml.toFile())) {
                try {
                    fis.write(registryInfoNode.builder().asString().getBytes());
                } catch (final XmlBuilder.XmlBuildException e) {
                    throw new MojoFailureException("Can't create " + REGISTRY_INFO_XML, e);
                }
            }

            artifactsList.add(
                    allInOneName, version, serverRole, true, artifactTypes.of(RESOURCES_TYPE).getPriority());
        }
    }

    private boolean createAllInOneArtifacts(final Path root, final XmlBuilder.Node registryInfoNode) throws IOException, MojoFailureException {
        boolean hasArtifacts = false;
        try (DirectoryStream<Path> artifactsStream = Files.newDirectoryStream(root)) {
            for (final Path path : artifactsStream) {
                if (Files.isDirectory(path)) {
                    hasArtifacts |= createAllInOneArtifacts(path, registryInfoNode);
                } else {
                    hasArtifacts |= createAllInOneArtifact(path, registryInfoNode);
                }
            }
        }
        return hasArtifacts;
    }

    private boolean createAllInOneArtifact(final Path fromFile, final XmlBuilder.Node registryInfoNode) throws IOException, MojoFailureException {
        final String fileName = fromFile.getFileName().toString();
        final String registryFile = registryFileOf(fromFile);
        final Path dir = Paths.get(outputDirectory, allInOneName + "_" + version);
        final Path resourcesDir = resourcesDirOf(dir);
        final Path file = Paths.get(resourcesDir.toString(), fileName);

        if (!Files.exists(resourcesDir)) {
            Files.createDirectories(resourcesDir);
        }

        Files.copy(fromFile, file, StandardCopyOption.REPLACE_EXISTING);

        final String mediaType = detectMediaType(registryFile);

        final String registryPath = registryPathOf(fromFile);

        registryInfoNode.node("item")
                .node("file").content(fileName).parent()
                .node("path").content("/" + registryPath).parent()
                .node("mediaType").content(mediaType);

        return true;
    }

    private void createArtifactXml(final String name, final Path dir) throws IOException, MojoFailureException {
        try {
            final Path metaFile = Paths.get(dir.toString(), "artifact.xml");
            final String xml = new XmlBuilder().node("artifact")
                    .attr("name", name)
                    .attr("version", version)
                    .attr("type", artifactTypes.of(RESOURCES_TYPE).getType())
                    .attr("serverRole", serverRole)
                    .node("file").content(REGISTRY_INFO_XML)
                    .builder().asString();
            try (FileOutputStream fis = new FileOutputStream(metaFile.toFile())) {
                fis.write(xml.getBytes());
            }
        } catch (final XmlBuilder.XmlBuildException e) {
            throw new MojoFailureException("Can't create artifact.xml", e);
        }
    }

    private Map<String, String> readArtifactsTypesList() throws IOException {
        final Path file = Paths.get(configDir, "artifacts.list");
        if (!Files.exists(file)) {
            return Collections.emptyMap();
        }
        try (BufferedReader in = new BufferedReader(new FileReader(file.toFile()))) {
            final Map<String, String> result = new HashMap<>();

            String line;
            String type = "";
            while ((line = in.readLine()) != null) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }

                line = line.trim();

                if (line.charAt(0) == ':') {
                    type = line.substring(1);
                } else {
                    result.put(line, type);
                }
            }

            return result;
        }
    }

    @Nullable
    private String detectMediaType(final String registryFile) {
        final String predefined = artifactsMediaTypes.get(registryFile);
        if (!StringUtils.isBlank(predefined)) {
            return predefined;
        }
        return mediaTypes != null ? mediaTypes.of(registryFile).getType() : null;
    }

    private String registryFileOf(final Path fromFile) {
        return fromFile.toString().substring(configDir.length() + 1).replaceAll("\\\\", "/");
    }

    private String registryPathOf(final Path fromFile) {
        return registryFileOf(fromFile.getParent());
    }

    private Path resourcesDirOf(final Path dir) {
        return Paths.get(dir.toString(), "resources");
    }
}
