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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Grachev
 */
public final class XmlBuilder {

    private final Node root = new Node(null, "root");

    public XmlBuilder() {
    }

    public String asString() throws XmlBuildException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder;
        final Transformer transformer;
        try {
            documentBuilder = factory.newDocumentBuilder();
            final Document document = documentBuilder.newDocument();
            root.createTo(null, document);

            transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            final StringWriter output = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(output));

            return output.toString();
        } catch (final Exception e) {
            e.printStackTrace();
            throw new XmlBuildException(e);
        }
    }

    public Node node(final String name) {
        return root.node(name);
    }

    public final class Node {

        private final Node owner;
        private final List<Node> nodes = new LinkedList<>();
        private final List<Attr> attrs = new LinkedList<>();
        private final String name;
        @Nullable
        private Content content;

        public Node(final Node owner, final String name) {
            this.owner = owner;
            this.name = name;
        }

        public Attr attr(final String name, final String value) {
            final Attr attr = new Attr(this, name, value);
            attrs.add(attr);
            return new Attr(this, name, value);
        }

        public Node node(final String name) {
            final Node node = new Node(this, name);
            nodes.add(node);
            return node;
        }

        public Node parent() {
            return owner;
        }

        public XmlBuilder builder() {
            return XmlBuilder.this;
        }

        public Content content(final String text) {
            content = new Content(text, this);
            return content;
        }

        public void createTo(@Nullable final Element element, final Document document) {
            if (this == root) {
                for (final Node node : nodes) {
                    node.createTo(null, document);
                }
            } else {
                final Element e = document.createElement(name);
                if (element == null) {
                    document.appendChild(e);
                } else {
                    element.appendChild(e);
                }
                for (final Attr attr : attrs) {
                    e.setAttribute(attr.name, attr.value);
                }
                if (content != null) {
                    e.setTextContent(content.text);
                }
                for (final Node node : nodes) {
                    node.createTo(e, document);
                }
            }
        }
    }

    public final class Attr {
        private final Node owner;
        private final String name;
        private final String value;

        public Attr(final Node owner, final String name, final String value) {
            this.owner = owner;
            this.name = name;
            this.value = value;
        }

        public Attr attr(final String name, final String value) {
            return owner.attr(name, value);
        }

        public Node node(final String name) {
            return owner.node(name);
        }

        public Node node() {
            return owner;
        }
    }

    public final class Content {

        private final String text;
        private final Node owner;

        public Content(final String text, final Node owner) {
            this.text = text;
            this.owner = owner;
        }

        public XmlBuilder.Node parent() {
            return owner.parent();
        }

        public XmlBuilder builder() {
            return XmlBuilder.this;
        }
    }

    public static final class XmlBuildException extends Exception {

        public XmlBuildException(final Throwable throwable) {
            super(throwable);
        }
    }
}
