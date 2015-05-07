/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util.dom;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.benchmark.EDITestData;
import com.berryworks.edireader.util.VerboseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;


public class DomBuildingSaxHandlerTest extends VerboseTestCase {
    private InputSource inputSource;
    private ContentHandler saxHandler;
    private InputStream inputStream;

    @Before
    public void setUp() throws IOException, ParserConfigurationException {
        inputSource = EDITestData.getAnsiInputSource();
        saxHandler = new DomBuildingSaxHandler();
    }

    @Test
    public void testBuildDom() throws Exception {

        // As a baseline, build the DOM without using DomBuildingSaxHandler
        Document baselineDom = DocumentUtil.getInstance().buildDocumentFromEdi(inputSource);

        // Now build an equivalent DOM using DomBuildingSaxHandler
        inputSource = EDITestData.getAnsiInputSource();
        EDIReader parser = EDIReaderFactory.createEDIReader(inputSource);
        parser.setContentHandler(saxHandler);
        parser.parse(inputSource);
        Document dom = ((DomBuildingSaxHandler) saxHandler).getDocument();

        assertDomsAreEquivalent(baselineDom, dom);

    }

    private void assertDomsAreEquivalent(Document baselineDom, Document dom) {

        if (baselineDom == null || dom == null)
            fail("One or both DOMs are null");

        Element baselineRoot = baselineDom.getDocumentElement();
        Element root = dom.getDocumentElement();

        if (baselineRoot == null || root == null)
            fail("One or both DocumentElements are null");

        compareElements(baselineRoot, root);
    }

    private void compareElements(Element baseline, Element element) {
        if (verbose) trace("comparing " + baseline.getNodeName() + " with " + element.getNodeName());
        assertEquals("Node names do not match", baseline.getNodeName(), element.getNodeName());

        NodeList childNodes = element.getChildNodes();
        NodeList baselineRootChildNodes = baseline.getChildNodes();
        assertEquals("Number of child nodes does not match", baselineRootChildNodes.getLength(), childNodes.getLength());
        for (int i = 0; i < baselineRootChildNodes.getLength(); i++) {
            Node baselineNode = baselineRootChildNodes.item(i);
            Node childNode = childNodes.item(i);

            short type = baselineNode.getNodeType();
            assertEquals("Node types do not match", type, childNode.getNodeType());

            if (type == Element.ELEMENT_NODE) {
                compareElements((Element) baselineNode, (Element) childNode);
            }
        }

        NamedNodeMap baselineAttributes = baseline.getAttributes();
        NamedNodeMap attributes = element.getAttributes();
        assertEquals("Nodes do not have the same number of attributes", baselineAttributes.getLength(), attributes.getLength());
        for (int i = 0; i < baselineAttributes.getLength(); i++) {
            Node baselineNode = baselineAttributes.item(i);
            Node childNode = attributes.item(i);

            Attr attr = (Attr) baselineNode;
            String name = attr.getName();
            String value = attr.getValue();
            if (verbose) trace("attribute " + name + " has value " + value);

            Node node = attributes.getNamedItem(name);
            assertNotNull("Missing attribute: " + name, node);
            assertEquals("Non-matching value", value, node.getNodeValue());
        }
    }

}
