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

package com.github.devmix.esb.car.plugin;

import com.github.devmix.esb.car.plugin.builders.ArtifactsListBuilder;
import com.github.devmix.esb.car.plugin.builders.RegistryArtifactsBuilder;
import com.github.devmix.esb.car.plugin.builders.SynapseConfigArtifactsBuilder;
import com.github.devmix.esb.car.plugin.builders.XmlBuilder;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Sergey Grachev
 */
@Mojo(name = "car", defaultPhase = LifecyclePhase.PACKAGE)
public class CarMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.version}", required = true)
    private String version;

    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private String outputDirectory;

    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private String targetDir;

    @Parameter(defaultValue = "${project.basedir}/src/main/synapse-config", required = false)
    private String synapseConfigDir;

    @Parameter(defaultValue = "${project.basedir}/src/main/resources", required = false)
    private String registryConfigDir;

    @Parameter(defaultValue = "${project.name}", required = true)
    private String applicationName;

    @Parameter(defaultValue = Constants.SERVER_ROLE_ENTERPRISE_SERVICE_BUS, required = true)
    private String serverRole;

    /**
     * Override default behavior and create all registry resource in one artifact
     */
    @Parameter(defaultValue = "true")
    private boolean registryAllInOneArtifact;

    /**
     * Name of all-in-one artifact for registry resources. See <code>registryAllInOneArtifact</code> property
     */
    @Parameter(defaultValue = "resources")
    private String registryAllInOneArtifactName;

    @Parameter(defaultValue = "${project.name}_${project.version}", required = true)
    private String carName;

    @Component(role = org.codehaus.plexus.archiver.Archiver.class, hint = "jar")
    private JarArchiver jarArchiver;

    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession session;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Parameter
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    private final ArtifactsListBuilder artifactsListBuilder = new ArtifactsListBuilder();

    @Override
    public void execute() throws MojoFailureException {
        try {
            Files.createDirectories(Paths.get(outputDirectory));

            SynapseConfigArtifactsBuilder.newInstance()
                    .artifactsList(artifactsListBuilder)
                    .outputDirectory(outputDirectory)
                    .configDir(synapseConfigDir)
                    .serverRole(serverRole)
                    .version(version)
                    .build();

            RegistryArtifactsBuilder.newInstance()
                    .artifactsList(artifactsListBuilder)
                    .outputDirectory(outputDirectory)
                    .configDir(registryConfigDir)
                    .serverRole(serverRole)
                    .version(version)
                    .allInOne(registryAllInOneArtifact)
                    .allInOneName(registryAllInOneArtifactName)
                    .build();

            createArtifactsXml();
            createCar();
        } catch (final Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void createCar() throws MojoFailureException {
        final File carFile = new File(targetDir, carName + ".car");
        final MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(jarArchiver);
        archiver.setOutputFile(carFile);
        archiver.getArchiver().addDirectory(new File(outputDirectory));
        try {
            archiver.createArchive(session, project, archive);
        } catch (final Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
        project.getArtifact().setFile(carFile);
    }

    private void createArtifactsXml() throws IOException, XmlBuilder.XmlBuildException {
        final XmlBuilder artifactsXml = new XmlBuilder();
        final XmlBuilder.Node artifactNode = artifactsXml.node("artifacts").node("artifact")
                .attr("name", applicationName)
                .attr("version", version)
                .attr("type", "carbon/application").node();

        for (final ArtifactsListBuilder.Dependency dependency : artifactsListBuilder.ordered()) {
            artifactNode.node("dependency")
                    .attr("artifact", dependency.artifactName)
                    .attr("version", dependency.version)
                    .attr("include", String.valueOf(dependency.include))
                    .attr("serverRole", dependency.serverRole);
        }

        final Path artifactsFileName = Paths.get(outputDirectory, "artifacts.xml");
        try (FileOutputStream fis = new FileOutputStream(artifactsFileName.toFile())) {
            fis.write(artifactsXml.asString().getBytes());
        }
    }
}
