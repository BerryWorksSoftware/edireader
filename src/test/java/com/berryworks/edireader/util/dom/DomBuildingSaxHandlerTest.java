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
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNull;


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

        assertNull(DocumentUtil.compare(baselineDom, dom));
    }

}
