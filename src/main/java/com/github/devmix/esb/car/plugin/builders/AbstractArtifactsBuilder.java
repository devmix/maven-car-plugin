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

import com.github.devmix.esb.car.plugin.registry.SynapseArtifactTypesBundle;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.utils.StringUtils;

/**
 * @author Sergey Grachev
 */
@SuppressWarnings("unchecked")
abstract class AbstractArtifactsBuilder<B> {

    protected ArtifactsListBuilder artifactsList;
    protected String outputDirectory;
    protected String configDir;
    protected String serverRole;
    protected String version;
    protected SynapseArtifactTypesBundle artifactTypes;

    public B artifactsList(final ArtifactsListBuilder artifactsList) {
        this.artifactsList = artifactsList;
        return (B) this;
    }

    public B outputDirectory(final String outputDirectory) {
        this.outputDirectory = outputDirectory;
        return (B) this;
    }

    public B configDir(final String configDir) {
        this.configDir = configDir;
        return (B) this;
    }

    public B serverRole(final String serverRole) {
        this.serverRole = serverRole;
        return (B) this;
    }

    public B version(final String version) {
        this.version = version;
        return (B) this;
    }

    public B artifactTypes(final SynapseArtifactTypesBundle artifactTypes) {
        this.artifactTypes = artifactTypes;
        return (B) this;
    }

    protected void check() throws MojoFailureException {
        if (StringUtils.isBlank(outputDirectory)) {
            throw new MojoFailureException("outputDirectory is empty");
        }
        if (StringUtils.isBlank(configDir)) {
            throw new MojoFailureException("configDir is empty");
        }
        if (StringUtils.isBlank(serverRole)) {
            throw new MojoFailureException("serverRole is empty");
        }
        if (StringUtils.isBlank(version)) {
            throw new MojoFailureException("version is empty");
        }
    }
}
