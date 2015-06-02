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
public class RegistryMediaTypesBundleTest {

    @Test
    public void testJson() {
        final RegistryMediaTypesBundle bundle = new RegistryMediaTypesBundle()
                .putTypes(this.getClass().getResource("/registry-media-types.json"));

        assertThat(bundle.size()).isEqualTo(7);
        assertThat(bundle.of("js").getType()).isEqualTo("application/javascript");
        assertThat(bundle.of("sql").getType()).isEqualTo("");
    }

    @Test
    public void testXml() {
        final RegistryMediaTypesBundle bundle = new RegistryMediaTypesBundle()
                .putTypes(this.getClass().getResource("/registry-media-types.xml"));

        assertThat(bundle.size()).isEqualTo(7);
        assertThat(bundle.of("js").getType()).isEqualTo("application/javascript");
        assertThat(bundle.of("sql").getType()).isNullOrEmpty();
    }

    @Test
    public void testYaml() {
        final RegistryMediaTypesBundle bundle = new RegistryMediaTypesBundle()
                .putTypes(this.getClass().getResource("/registry-media-types.yaml"));

        assertThat(bundle.size()).isEqualTo(7);
        assertThat(bundle.of("js").getType()).isEqualTo("application/javascript");
        assertThat(bundle.of("sql").getType()).isNullOrEmpty();
    }

    @Test
    public void testOverride() {
        final RegistryMediaTypesBundle bundle = new RegistryMediaTypesBundle()
                .putTypes(this.getClass().getResource("/registry-media-types.json"))
                .putTypes(this.getClass().getResource("/registry-media-types-override.json"));

        assertThat(bundle.size()).isEqualTo(7);
        assertThat(bundle.of("js").getType()).isEqualTo("1");
        assertThat(bundle.of("sql").getType()).isEqualTo("");
        assertThat(bundle.of("css").getType()).isEqualTo("2");
    }
}
