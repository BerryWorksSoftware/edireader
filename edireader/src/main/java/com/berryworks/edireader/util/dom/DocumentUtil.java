/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides convenience methods for using the W3C DOM API in an EDI context.
 * <p/>
 * These method are written using the EDIReader and DOM APIs and simply encapsulate
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
        XMLReader ediReader = new EDIReader();
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
            if (children == null || children.size() < 1)
                throw new RuntimeException("Cannot position to <" + p + ">");
            e = children.get(0);
        }
        return e;
    }

    public static List<Element> getChildren(Element element, String tag) {
        List<Element> result = new ArrayList<>();
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType() && node.getNodeName().equals(tag))
                result.add((Element) node);
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


}
