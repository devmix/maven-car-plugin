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

import com.github.devmix.esb.car.plugin.Constants;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author Sergey Grachev
 */
public final class RegistryArtifactsBuilderTest {

    public static void main(final String[] args) throws MojoFailureException {
        final ArtifactsListBuilder artifactsListBuilder = new ArtifactsListBuilder();
        final String configDir = RegistryArtifactsBuilderTest.class.getResource("/registry")
                .getFile()/*.substring(1)*/;

        RegistryArtifactsBuilder.newInstance()
                .artifactsList(artifactsListBuilder)
                .outputDirectory("/mnt/disks/work/projects/esb/maven-car-plugin/out")
                .configDir(configDir)
                .serverRole(Constants.SERVER_ROLE_ENTERPRISE_SERVICE_BUS)
                .version("1.0.0")
                .allInOne(true).allInOneName("res")
                .build();

        System.out.println(artifactsListBuilder);
    }
}
