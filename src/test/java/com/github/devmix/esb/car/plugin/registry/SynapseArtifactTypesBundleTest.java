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

package com.github.devmix.esb.car.plugin.registry;

import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public class SynapseArtifactTypesBundleTest {

    @Test
    public void testYaml() {
        final SynapseArtifactTypesBundle bundle = new SynapseArtifactTypesBundle()
                .putTypes(this.getClass().getResource("/synapse-artifact-types.yaml"));

        assertThat(bundle.size()).isEqualTo(10);
        assertType(bundle.of("registry"), 100, "registry/resource");
        assertType(bundle.of("local-entries"), 200, "synapse/local-entry");
    }

    private void assertType(final SynapseArtifactTypesBundle.Meta meta, final int priority, final String synapseType) {
        assertThat(meta.getPriority()).isEqualTo(priority);
        assertThat(meta.getType()).isEqualTo(synapseType);
    }
}
