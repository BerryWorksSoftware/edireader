/*
 * Copyright 2005-2023 by BerryWorks Software, LLC. All rights reserved.
 *
 * This file is part of EDIReader. You may obtain a license for its use directly from
 * BerryWorks Software, and you may also choose to use this software under the terms of the
 * GPL version 3. Other products in the EDIReader software suite are available only by licensing
 * with BerryWorks. Only those files bearing the GPL statement below are available under the GPL.
 *
 * EDIReader is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * EDIReader is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with EDIReader.  If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.berryworks.edireader.util.dom;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.plugin.PluginControllerFactoryInterface;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides convenience methods for using the W3C DOM API in an EDI context.
 * <p>
 * These methods are written using the EDIReader and DOM APIs and simply encapsulate
 * functional sequences that may be useful for testing and other purposes.
 */
public class DocumentUtil {

    private static final DocumentUtil instance = new DocumentUtil();

    private DocumentUtil() {
    }

    /**
     * Returns the instance of the singleton.
     *
     * @return the instance
     */
    public static DocumentUtil getInstance() {
        return instance;
    }

    /**
     * Build a DOM from EDI input.
     *
     * @param inputSource EDI input
     * @return Document representing parsed EDI content
     * @throws Exception if problem reading or parsing input
     */
    public synchronized Document buildDocumentFromEdi(InputSource inputSource) throws Exception {
        return buildDocumentFromEdi(inputSource, null);
    }

    public synchronized Document buildDocumentFromEdi(Reader inputReader) throws Exception {
        return buildDocumentFromEdi(new InputSource(inputReader), null);
    }

    public synchronized Document buildDocumentFromEdi(Reader inputReader, PluginControllerFactoryInterface factory) throws Exception {
        return buildDocumentFromEdi(new InputSource(inputReader), factory);
    }

    public synchronized Document buildDocumentFromEdi(InputSource inputSource, PluginControllerFactoryInterface factory) throws Exception {
        EDIReader ediReader = new EDIReader();
        if (factory != null) {
            ediReader.setPluginControllerFactory(factory);
        }
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        DOMResult domResult = new DOMResult();
        transformer.transform(new SAXSource(ediReader, inputSource), domResult);
        Document document = (Document) domResult.getNode();
        if (document == null)
            throw new RuntimeException("transform produced null document");
        return document;
    }

    /**
     * Build a DOM from XML input.
     *
     * @param inputSource XML input
     * @return Document containing parsed XML content
     * @throws Exception if problem reading or parsing input
     */
    public synchronized Document buildDocumentFromXml(InputSource inputSource) throws Exception {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputSource);
        if (document == null)
            throw new RuntimeException("parse produced null document");
        return document;
    }

    public synchronized void writeXML(Document document, Writer writer) throws TransformerException {
        StreamResult streamResult = new StreamResult();
        streamResult.setWriter(writer);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), streamResult);
    }

    public static Element position(Element origin, String[] path) {
        Element e = origin;
        for (String p : path) {
            List<Element> children = getChildren(e, p);
            if (children == null || children.isEmpty())
                throw new RuntimeException("Cannot position to <" + p + ">");
            e = children.get(0);
        }
        return e;
    }

    public static List<Element> getChildren(Element element) {
        return getChildren(element, null);
    }

    public static List<Element> getChildren(Element element, String tag) {
        List<Element> result = new ArrayList<>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                // tag == null is considered a wildcard
                if (tag == null || tag.equals(node.getNodeName())) {
                    result.add((Element) node);
                }
            }
        }
        return result;
    }

    public static Element getFirstChild(Element element, String tag) {
        Element result = null;
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType() && node.getNodeName().equals(tag)) {
                result = (Element) node;
                break;
            }
        }
        return result;
    }

    public static List<Element> getChildrenHavingAttribute(Element element, String tag, String attrName, String attrValue) {
        List<Element> result = new ArrayList<>();
        if (attrName == null || attrValue == null) return result;

        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType() && node.getNodeName().equals(tag)) {
                Element e = (Element) node;
                if (attrValue.equals(e.getAttribute(attrName))) {
                    result.add(e);
                }
            }
        }
        return result;
    }

    /**
     * Compares two Documents with respect to the content and attributes
     * populated via EDIReader.
     *
     * @param documentA first document for comparison
     * @param documentB second document, for comparison with the first
     * @return null if there are no significant differences, or a String representation of the differences
     */
    public static String compare(Document documentA, Document documentB) {
        if (documentA == null) return "First Document is null";
        if (documentB == null) return "Second Document is null";
        String difference = compareElements(documentA.getDocumentElement(), documentB.getDocumentElement());
        return difference;
    }

    public static String compareElements(Element elementA, Element elementB) {
        StringBuilder sb = new StringBuilder();
        if (!elementA.getNodeName().equals(elementB.getNodeName())) sb.append("element NodeName mismatch;");

        String textContentA = elementA.getTextContent();
        String textContentB = elementB.getTextContent();
        if (textContentA == null) {
            if (textContentB != null) {
                sb.append("element value missing from document A;");
            }
        } else {
            if (textContentB == null) {
                sb.append("element value missing from document B;");
            } else {
                if (!textContentA.equals(textContentB)) sb.append("element value mismatch;");
            }
        }

        NodeList childNodesB = elementB.getChildNodes();
        NodeList childNodesA = elementA.getChildNodes();
        if (childNodesA.getLength() != childNodesB.getLength()) sb.append("number of child nodes mismatch;");
        for (int i = 0; i < childNodesA.getLength(); i++) {
            Node childNodeA = childNodesA.item(i);
            Node childNodeB = childNodesB.item(i);

            short type = childNodeA.getNodeType();
            if (childNodeA.getNodeType() != childNodeB.getNodeType()) sb.append("child node type mismatch;");

            if (type == Element.ELEMENT_NODE) {
                String compareElements = compareElements((Element) childNodeA, (Element) childNodeB);
                if (compareElements != null) {
                    sb.append(compareElements);
                }
            }
        }

        NamedNodeMap attributesA = elementA.getAttributes();
        NamedNodeMap attributesB = elementB.getAttributes();
        if (attributesA.getLength() != attributesB.getLength()) sb.append("number of attributes mismatch;");
        for (int i = 0; i < attributesA.getLength(); i++) {
            Attr attrA = (Attr) attributesA.item(i);
            String name = attrA.getName();
            String value = attrA.getValue();

            Node attributeNodeB = attributesB.getNamedItem(name);
            if (attributeNodeB == null) sb.append("missing attribute;");
            if (!value.equals(attributeNodeB.getNodeValue())) {
                sb.append("attribute value mismatch;");
            }
        }

        return sb.length() == 0 ? null : sb.toString();
    }

}
