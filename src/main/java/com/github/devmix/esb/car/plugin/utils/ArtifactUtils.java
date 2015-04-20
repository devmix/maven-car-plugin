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

        // registry/resource

        // --- 2

        TYPE_TO_DESCRIPTOR.put("local-entries", new Descriptor(0, "let", "synapse/local-entry"));

        // --- 3

        // lib/synapse/mediator

        // --- 4

        TYPE_TO_DESCRIPTOR.put("message-stores", new Descriptor(1, "mst", "synapse/message-store"));

        // --- 5

        // jaggery/app
        // bpel/workflow
        // lib/registry/filter
        // webapp/jaxws
        // lib/library/bundle
        // service/dataservice
        // cep/bucket
        TYPE_TO_DESCRIPTOR.put("proxy-services", new Descriptor(3, "prx", "synapse/proxy-service"));
        // carbon/application
        // lib/dataservice/validator
        TYPE_TO_DESCRIPTOR.put("endpoints", new Descriptor(6, "ept", "synapse/endpoint"));
        // web/application
        // lib/carbon/ui
        // service/axis2
        TYPE_TO_DESCRIPTOR.put("sequences", new Descriptor(5, "seq", "synapse/sequence"));
        // synapse/configuration
        TYPE_TO_DESCRIPTOR.put("api", new Descriptor(4, "api", "synapse/api"));
        TYPE_TO_DESCRIPTOR.put("templates", new Descriptor(7, "tpl", "synapse/template"));
        // synapse/sequenceTemplate
        // synapse/endpointTemplate
        // synapse/event-source
        TYPE_TO_DESCRIPTOR.put("message-processors", new Descriptor(2, "mps", "synapse/message-processors"));
        // synapse/priority-executor
        // wso2/gadget
        // lib/registry/handlers
        // service/rule
        // service/meta
        // jaggery/app

        // --- 6
        TYPE_TO_DESCRIPTOR.put("tasks", new Descriptor(8, "tsk", "synapse/task"));
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
