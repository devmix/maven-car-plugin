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

package com.github.devmix.esb.car.plugin.utils;

import org.apache.maven.shared.utils.StringUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Grachev
 */
public final class ArtifactUtils {

    private static final Map<String, Descriptor> TYPE_TO_DESCRIPTOR = new HashMap<>(9);

    static {
        // --- 1

        TYPE_TO_DESCRIPTOR.put("registry", new Descriptor(100, "rgr", "registry/resource"));

        // --- 2

        TYPE_TO_DESCRIPTOR.put("local-entries", new Descriptor(200, "let", "synapse/local-entry"));

        // --- 3

        // lib/synapse/mediator

        // --- 4

        TYPE_TO_DESCRIPTOR.put("message-stores", new Descriptor(400, "mst", "synapse/message-store"));

        // --- 5

        // jaggery/app
        // bpel/workflow
        // lib/registry/filter
        // webapp/jaxws
        // lib/library/bundle
        // service/dataservice
        // cep/bucket
        TYPE_TO_DESCRIPTOR.put("proxy-services", new Descriptor(500, "prx", "synapse/proxy-service"));
        // carbon/application
        // lib/dataservice/validator
        TYPE_TO_DESCRIPTOR.put("endpoints", new Descriptor(501, "ept", "synapse/endpoint"));
        // web/application
        // lib/carbon/ui
        // service/axis2
        TYPE_TO_DESCRIPTOR.put("sequences", new Descriptor(502, "seq", "synapse/sequence"));
        // synapse/configuration
        TYPE_TO_DESCRIPTOR.put("api", new Descriptor(504, "api", "synapse/api"));
        TYPE_TO_DESCRIPTOR.put("templates", new Descriptor(503, "tpl", "synapse/template"));
        // synapse/sequenceTemplate
        // synapse/endpointTemplate
        // synapse/event-source
        TYPE_TO_DESCRIPTOR.put("message-processors", new Descriptor(504, "mps", "synapse/message-processors"));
        // synapse/priority-executor
        // wso2/gadget
        // lib/registry/handlers
        // service/rule
        // service/meta
        // jaggery/app

        // --- 6

        TYPE_TO_DESCRIPTOR.put("tasks", new Descriptor(600, "tsk", "synapse/task"));
    }

    private static final Map<String, String> EXT_TO_MEDIA_TYPE = new HashMap<>(9);

    static {
        EXT_TO_MEDIA_TYPE.put("js", "application/javascript");
        EXT_TO_MEDIA_TYPE.put("css", "text/css");
        EXT_TO_MEDIA_TYPE.put("html", "text/html");
        EXT_TO_MEDIA_TYPE.put("sql", "");
        EXT_TO_MEDIA_TYPE.put("xsl", "application/xsl+xml");
        EXT_TO_MEDIA_TYPE.put("xslt", "application/xslt+xml");
        EXT_TO_MEDIA_TYPE.put("groovy", "text/x-groovy");
    }

    private ArtifactUtils() {
    }

    public static String removeFileExtension(final String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("Filename is null");
        }
        final int i = fileName.lastIndexOf(".");
        return i == -1 ? fileName : fileName.substring(0, i);
    }

    public static String getExtension(final String fileName) {
        final int dot = fileName.lastIndexOf(".");
        return fileName.substring(dot + 1);
    }

    @Nullable
    public static String synapseTypeOf(final String artifactType) {
        return isSupported(artifactType) ? TYPE_TO_DESCRIPTOR.get(artifactType).synapseType : null;
    }

    public static int priorityOf(final String artifactType) {
        return isSupported(artifactType) ? TYPE_TO_DESCRIPTOR.get(artifactType).dependencyPriority : 0;
    }

    public static boolean isSupported(final String artifactType) {
        return TYPE_TO_DESCRIPTOR.containsKey(artifactType);
    }

    @Nullable
    public static String mediaTypeOf(final String file) {
        final String ext = getExtension(file);
        if (StringUtils.isBlank(ext)) {
            return null;
        }
        return EXT_TO_MEDIA_TYPE.get(ext.toLowerCase());
    }

    private static final class Descriptor {

        private final String shortType;
        private final String synapseType;
        private final int dependencyPriority;

        public Descriptor(final int dependencyPriority, final String shortType, final String synapseType) {
            this.shortType = shortType;
            this.synapseType = synapseType;
            this.dependencyPriority = dependencyPriority;
        }
    }
}
