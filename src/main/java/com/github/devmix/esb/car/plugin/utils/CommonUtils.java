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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author Sergey Grachev
 */
public final class CommonUtils {

    private CommonUtils() {
    }

    public static String extensionOf(final String fileName) {
        final int dot = fileName.lastIndexOf(".");
        return dot >= 0 ? fileName.substring(dot + 1) : fileName;
    }

    public static String removeFileExtension(final String fileName) {
        if (fileName == null) {
            throw new IllegalArgumentException("Filename is null");
        }
        final int i = fileName.lastIndexOf(".");
        return i == -1 ? fileName : fileName.substring(0, i);
    }

    public static ObjectMapper mapperOf(final String fileName) {
        final String lowerCase = fileName.toLowerCase();
        if (lowerCase.endsWith(".yaml")) {
            return new ObjectMapper(new YAMLFactory());
        } else if (lowerCase.endsWith(".xml")) {
            return new ObjectMapper(new XmlFactory());
        } else {
            return new ObjectMapper(new JsonFactory());
        }
    }
}
