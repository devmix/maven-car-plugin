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

import com.github.devmix.esb.car.plugin.utils.ArtifactUtils;
import org.apache.maven.plugin.MojoFailureException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;

/**
 * @author Sergey Grachev
 */
public final class SynapseConfigArtifactsBuilder extends AbstractArtifactsBuilder<SynapseConfigArtifactsBuilder> {

    private SynapseConfigArtifactsBuilder() {
    }

    public static SynapseConfigArtifactsBuilder newInstance() {
        return new SynapseConfigArtifactsBuilder();
    }

    public void build() throws MojoFailureException {
        check();
        try {
            createArtifacts();
        } catch (final IOException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void createArtifacts() throws IOException, MojoFailureException {
        final Path sourcesDir = Paths.get(configDir);
        if (!Files.exists(sourcesDir)) {
            return;
        }

        Files.createDirectories(Paths.get(outputDirectory));
        try (DirectoryStream<Path> confStream = Files.newDirectoryStream(sourcesDir)) {
            for (final Path path : confStream) {
                final String type = path.getFileName().toString();
                createArtifactsOf(type, path);
            }
        }
    }

    private void createArtifactsOf(final String type, final Path fromPath) throws IOException, MojoFailureException {
        if (!ArtifactUtils.isSupported(type)) {
            throw new MojoFailureException("Unsupported type of artifact - " + type);
        }

        try (DirectoryStream<Path> artifactStream = Files.newDirectoryStream(fromPath)) {
            for (final Path file : artifactStream) {
                createArtifactOf(type, file);
            }
        }
    }

    private void createArtifactOf(final String type, final Path fromFile) throws IOException, MojoFailureException {
        final String artifactName = ArtifactUtils.removeFileExtension(fromFile.getFileName().toString());
        final String artifactFileName = artifactName + "-" + version + ".xml";
        final Path artifactDir = Paths.get(outputDirectory, artifactName + "_" + version);
        final Path artifactFile = Paths.get(artifactDir.toString(), artifactFileName);
        final Path artifactMetaFile = Paths.get(artifactDir.toString(), "artifact.xml");
        if (!Files.exists(artifactDir)) {
            Files.createDirectory(artifactDir);
        }

        Files.copy(fromFile, artifactFile, StandardCopyOption.REPLACE_EXISTING);

        try {
            final String xml = new XmlBuilder().node("artifact")
                    .attr("name", artifactName)
                    .attr("version", version)
                    .attr("type", ArtifactUtils.synapseTypeOf(type))
                    .attr("serverRole", serverRole)
                    .node("file").content(artifactFileName)
                    .builder().asString();
            try (FileOutputStream fis = new FileOutputStream(artifactMetaFile.toFile())) {
                fis.write(xml.getBytes());
            }
        } catch (final XmlBuilder.XmlBuildException e) {
            throw new MojoFailureException("Can't create artifact.xml", e);
        }

        artifactsList.add(artifactName, version, serverRole, true, type);
    }
}
