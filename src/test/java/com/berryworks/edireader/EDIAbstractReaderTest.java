/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader;

import com.berryworks.edireader.benchmark.EDITestData;
import com.berryworks.edireader.error.ErrorMessages;
import com.berryworks.edireader.util.BranchingWriter;
import com.berryworks.edireader.util.VerboseTestCase;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.*;

public class EDIAbstractReaderTest extends VerboseTestCase {

    EDIReader reader;

    InputSource inputSource;

    @Before
    public void setUp() {
        reader = null;
        inputSource = null;
    }

    @Test
    public void testSyntaxElements() throws Exception {

        // ANSI
        reader = EDIReaderFactory.createEDIReader(EDITestData
                .getAnsiInputSource());
        assertNotNull(reader);
        assertTrue("Failed to create an AnsiReader",
                reader instanceof AnsiReader);
        assertEquals('~', reader.getDelimiter());
        assertEquals('<', reader.getSubDelimiter());
        assertEquals('$', reader.getTerminator());
        assertEquals("", reader.getTerminatorSuffix());

        // These next ones aren't set to anything in particular for ANSI.
        // We call them mainly for path coverage purposes.
        reader.getSubSubDelimiter();
        reader.getRelease();
        reader.getRepetitionSeparator();

        // EDIFACT
        reader = EDIReaderFactory.createEDIReader(EDITestData
                .getEdifactInputSource());
        assertNotNull(reader);
        assertTrue("Failed to create an EdifactReader",
                reader instanceof EdifactReader);
        assertEquals('+', reader.getDelimiter());
        assertEquals(':', reader.getSubDelimiter());
        assertEquals('\'', reader.getTerminator());
//        assertEquals("", reader.getTerminatorSuffix()); // This one is tricky due to Windows and non-Windows newlines
        assertEquals('?', reader.getRelease());

        // These next ones aren't set to anything in particular for EDIFACT.
        // We call them mainly for path coverage purposes.
        reader.getSubSubDelimiter();
        reader.getRepetitionSeparator();

        if (verbose) System.out.println(reader.toString());
    }

    @Test
    public void testParse() throws Exception {

        // ANSI
        inputSource = EDITestData.getAnsiInputSource();
        reader = EDIReaderFactory.createEDIReader(inputSource);
        assertNotNull(reader.getTokenizer());

        try {
            reader.parse("");
            fail("should have thrown an exception");
        } catch (SAXException ignore) {
        }

        reader.parse(inputSource);

        // EDIFACT
        inputSource = EDITestData.getEdifactInputSource();
        reader = EDIReaderFactory.createEDIReader(inputSource);
        assertNotNull(reader.getTokenizer());
        reader.setContentHandler(new DefaultHandler());

        try {
            reader.parse("");
            fail("should have thrown an exception");
        } catch (SAXException ignore) {
        }

        assertEquals(0, reader.getCharCount());
        assertEquals(0, reader.getSegmentCharCount());
        reader.parse(inputSource);
        assertEquals(EDITestData.getEdifactInterchange().length(), reader.getCharCount());
        assertEquals(17, reader.getSegmentCharCount());

        // Not EDI and not XML
        inputSource = new InputSource(new StringReader("this is not edi"));
        try {
            EDIReaderFactory.createEDIReader(inputSource);
        } catch (EDISyntaxException e) {
            assertEquals("No supported EDI standard interchange begins with thi", e.getMessage());
        }
    }

    @Test
    public void testParseXMLWhenExpectingEDI() throws Exception {

        inputSource = new InputSource(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
        try {
            EDIReaderFactory.createEDIReader(inputSource);
            fail("should have thrown an exception");
        } catch (EDISyntaxException e) {
            assertEquals(ErrorMessages.XML_INSTEAD_OF_EDI, e.getMessage());
        }

    }

    @Test
    public void testCopyWriter() throws Exception {

        // Create a reader from ANSI data
        String ediData = EDITestData.getAnsiInterchange();
        inputSource = new InputSource(new StringReader(ediData));

        reader = EDIReaderFactory.createEDIReader(inputSource);

        StringWriter sw = new StringWriter();
        reader.setCopyWriter(sw);
        reader.parse(inputSource);

        // Now compare the copy to the original
        assertEquals(ediData, sw.toString());

    }

    @Test
    public void testUnsupportedProperty() throws Exception {

        reader = EDIReaderFactory.createEDIReader(EDITestData
                .getAnsiInputSource());
        assertNull(reader.getProperty("p"));
    }

    @Test(expected = SAXNotSupportedException.class)
    public void testUnsupportedFeature() throws Exception {

        reader = EDIReaderFactory.createEDIReader(EDITestData
                .getAnsiInputSource());
        reader.getFeature("p");
    }

    @Test(expected = SAXNotSupportedException.class)
    public void testUnsupportedLocale() throws Exception {

        reader = EDIReaderFactory.createEDIReader(EDITestData
                .getAnsiInputSource());
        reader.setLocale(null);
    }

    @Test
    public void testHandlersEtc() throws Exception {

        reader = EDIReaderFactory.createEDIReader(EDITestData
                .getAnsiInputSource());

        assertNull(reader.getDTDHandler());
        assertNull(reader.getErrorHandler());
        assertNull(reader.getEntityResolver());

        AnEntityResolver er = new AnEntityResolver();
        reader.setEntityResolver(er);
        assertSame(er, reader.getEntityResolver());

        ErrorHandler eh = new AnErrorHandler();
        reader.setErrorHandler(eh);
        assertSame(eh, reader.getErrorHandler());

        ContentHandler ch = new DefaultHandler();
        reader.setContentHandler(ch);
        assertSame(ch, reader.getContentHandler());

        BranchingWriter ackStream = new BranchingWriter(new StringWriter());
        reader.setAckStream(ackStream);
        assertSame(ackStream, reader.getAckStream());
    }

    @Test
    public void testSetCopyWriter() throws Exception {
        reader = EDIReaderFactory.createEDIReader(EDITestData.getAnsiInputSource());

        Writer w = new StringWriter();
        reader.setCopyWriter(w);
        reader.setTokenizer(null);
        reader.setCopyWriter(w);
    }

    static class AnErrorHandler implements ErrorHandler {
        public void error(SAXParseException exception) throws SAXException {
        }

        public void fatalError(SAXParseException exception) throws SAXException {
        }

        public void warning(SAXParseException exception) throws SAXException {
        }

    }

    static class AnEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId)
                throws SAXException, IOException {
            return null;
        }

    }

}
