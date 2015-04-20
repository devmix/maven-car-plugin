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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class DependenciesBuilder {

    public static final Comparator<Dependency> DEPENDENCY_COMPARATOR = new Comparator<Dependency>() {
        @Override
        public int compare(final Dependency o1, final Dependency o2) {
            return Integer.compare(o1.priority, o2.priority);
        }
    };

    private final List<Dependency> list = new LinkedList<>();

    public void add(final String artifactName, final String version, final String serverRole, final boolean include,
                    final String type) {
        list.add(new Dependency(artifactName, version, serverRole, include, ArtifactUtils.priorityOf(type)));
    }

    public List<Dependency> ordered() {
        Collections.sort(list, DEPENDENCY_COMPARATOR);
        return Collections.unmodifiableList(list);
    }

    public static final class Dependency {

        public final String artifactName;
        public final String version;
        public final String serverRole;
        public final boolean include;
        public final int priority;

        private Dependency(final String artifactName, final String version, final String serverRole, final boolean include,
                           final int priority) {
            this.artifactName = artifactName;
            this.version = version;
            this.serverRole = serverRole;
            this.include = include;
            this.priority = priority;
        }
    }
}
