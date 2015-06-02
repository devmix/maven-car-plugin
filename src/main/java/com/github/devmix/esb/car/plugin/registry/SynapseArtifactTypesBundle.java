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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.devmix.esb.car.plugin.utils.CommonUtils;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class SynapseArtifactTypesBundle {

    private static final Log log = new SystemStreamLog();
    private static final Meta NULL = new Meta();
    private static final MapType TYPE = MapType.construct(
            LinkedHashMap.class, SimpleType.construct(String.class),
            SimpleType.construct(Meta.class));

    private final Map<String, Meta> list = new LinkedHashMap<>();

    public SynapseArtifactTypesBundle putTypes(final String file) {
        final ObjectMapper mapper = CommonUtils.mapperOf(file);
        try {
            final Map<String, Meta> map = mapper.readValue(new File(file), TYPE);
            list.putAll(map);
        } catch (final IOException e) {
            log.error(e);
        }
        return this;
    }

    public SynapseArtifactTypesBundle putTypes(final URL file) {
        final ObjectMapper mapper = CommonUtils.mapperOf(file.getFile());
        try {
            final Map<String, Meta> map = mapper.readValue(file, TYPE);
            list.putAll(map);
        } catch (final IOException e) {
            log.error(e);
        }
        return this;
    }

    public Meta of(final String type) {
        final Meta meta = list.get(type);
        return meta == null ? NULL : meta;
    }

    public int size() {
        return list.size();
    }

    public boolean isNotSupported(final String type) {
        return !list.containsKey(type);
    }

    public static final class Meta {

        private String type;
        private int priority;

        public String getType() {
            return type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(final int priority) {
            this.priority = priority;
        }
    }
}
