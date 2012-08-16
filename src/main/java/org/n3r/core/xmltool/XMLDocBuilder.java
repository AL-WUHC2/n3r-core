/**
 * Copyright (C) 2008 Mathieu Carbou <mathieu.carbou@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.n3r.core.xmltool;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 */
public final class XMLDocBuilder {

    private final XMLDocDefinition definition;

    private XMLDocBuilder(XMLDocDefinition definition) {
        this.definition = definition;
    }

    /**
     * Add a namespace to the document that will be created
     *
     * @param prefix       The prefix of the namespace
     * @param namespaceURI The URI of the namespace
     * @return this
     */
    public XMLDocBuilder addNamespace(String prefix, String namespaceURI) {
        definition.addNamespace(prefix, namespaceURI);
        return this;
    }

    /**
     * Set the default namespace to use in the document declaration. I.e.
     * <code>&lt;html xmlns="http://www.w3.org/1999/xhtml"></code>
     *
     * @param defaultNamespaceURI URI to use as default when tags are not prefixed
     * @return this
     */
    public XMLDocBuilder addDefaultNamespace(String defaultNamespaceURI) {
        definition.addDefaultNamespace(defaultNamespaceURI);
        return this;
    }

    /**
     * Create a root node for this XML document
     *
     * @param tagName Name of the element
     * @return XMLDoc instance to build and navigate in the document
     */
    public XMLTag addRoot(String tagName) {
        return create(definition.createRoot(tagName));
    }

    private static XMLTag create(final XMLDocDefinition def) {
        def.normalize();
        final XMLTag doc = new XMLDoc(def);
        InvocationHandler handler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    Object o = method.invoke(doc, args);
                    if(needsNormalization(method.getName())) {
                        def.normalize();
                    }
                    return o;
                }
                catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        };
        return (XMLTag) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{XMLTag.class},
                handler);
    }

    private static boolean needsNormalization(String name) {
        return name.startsWith("add") || name.startsWith("set") || name.startsWith("delete") || name.startsWith("rename");
    }

    static XMLDocBuilder newDocument(boolean ignoreNamespaces) {
        return new XMLDocBuilder(new XMLDocDefinition(
                newDocumentBuilder(ignoreNamespaces).newDocument(),
                ignoreNamespaces));
    }

    static XMLTag from(File file, boolean ignoreNamespaces) {
        try {
            return from(new BufferedInputStream(new FileInputStream(file)), ignoreNamespaces);
        } catch (FileNotFoundException e) {
            throw new XMLDocumentException(e.getMessage(), e);
        }
    }

    static XMLTag from(URL xmlLocation, boolean ignoreNamespaces) {
        try {
            return from(new BufferedInputStream(xmlLocation.openStream()), ignoreNamespaces);
        }
        catch (IOException e) {
            throw new XMLDocumentException(e.getMessage(), e);
        }
    }

    static XMLTag from(String xmlData, boolean ignoreNamespaces) {
        return from(new StringReader(xmlData), ignoreNamespaces);
    }

    static XMLTag from(Reader reader, boolean ignoreNamespaces) {
        try {
            return from(new InputSource(reader), ignoreNamespaces);
        } finally {
            close(reader);
        }
    }

    static XMLTag from(InputStream is, boolean ignoreNamespaces) {
        try {
            return from(new InputSource(is), ignoreNamespaces);
        } finally {
            close(is);
        }
    }

    static XMLTag from(InputSource source, boolean ignoreNamespaces) {
        try {
            return from(newDocumentBuilder(ignoreNamespaces).parse(source), ignoreNamespaces);
        }
        catch (Exception e) {
            throw new XMLDocumentException("Error creating XMLDoc. Please verify that the input source can be read and is well formed", e);
        }
    }

    static XMLTag from(Node node, boolean ignoreNamespaces) {
        return create(new XMLDocDefinition(node, ignoreNamespaces));
    }

    static XMLTag from(XMLTag tag, boolean ignoreNamespaces) {
        Document newDoc = newDocumentBuilder(ignoreNamespaces).newDocument();
        newDoc.appendChild(newDoc.importNode(tag.toDocument().getDocumentElement(), true));
        return from(newDoc, ignoreNamespaces);
    }

    static XMLTag fromCurrentTag(XMLTag tag, boolean ignoreNamespaces) {
        Document newDoc = newDocumentBuilder(ignoreNamespaces).newDocument();
        newDoc.appendChild(newDoc.importNode(tag.getCurrentTag(), true));
        return from(newDoc, ignoreNamespaces);
    }

    static XMLTag from(Source source, boolean ignoreNamespaces) {
        DOMResult result = new DOMResult();
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(source, result);
        } catch(Exception e) {
            throw new XMLDocumentException("Error creating XMLDoc. Please verify that the input source can be read and is well formed", e);
        }
        return from(result.getNode(), ignoreNamespaces);
    }

    private static void close(Closeable c) {
        try {
            c.close();
        }
        catch (IOException ignored) {
        }
    }

    private static DocumentBuilder newDocumentBuilder(boolean ignoreNamespaces) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(!ignoreNamespaces);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new XMLErrorHandler(true));
            builder.setEntityResolver(CachedEntityResolver.instance);
            return builder;
        }
        catch (ParserConfigurationException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}