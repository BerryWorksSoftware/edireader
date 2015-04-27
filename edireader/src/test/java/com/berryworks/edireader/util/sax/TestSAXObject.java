/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.benchmark.EDITestData;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;

import static org.junit.Assert.assertEquals;


public class TestSAXObject {
    private InputSource inputSource;
    private ContentHandler saxObjectHandler;
    private InputStream pipedInputStream;
    private MyHandler finalHandler;

    @Before
    public void setUp() throws IOException {
        inputSource = EDITestData.getAnsiInputSource();
        OutputStream pipedOutputStream = new PipedOutputStream();
        pipedInputStream = new PipedInputStream((PipedOutputStream) pipedOutputStream);
        saxObjectHandler = new SAXObjectHandler(pipedOutputStream);
        finalHandler = new MyHandler();
    }

    @Test
    public void testPassThroughPipe() throws IOException, SAXException, InterruptedException {
        Thread readerThread = new Thread(new MyReaderThread(pipedInputStream));
        readerThread.start();

        EDIReader parser = EDIReaderFactory.createEDIReader(inputSource);
        parser.setContentHandler(saxObjectHandler);
        parser.parse(inputSource);
        ((SAXObjectHandler) saxObjectHandler).markEndOfStream();
        readerThread.join();

        assertEquals(1, finalHandler.getDocumentCount());
        assertEquals(125, finalHandler.getElementCount());
        assertEquals(138, finalHandler.getAttributeCount());
        assertEquals(331, finalHandler.getCharCount());
    }

    class MyReaderThread implements Runnable {
        private final XMLReader reader;
        private final InputSource inputSource;

        public MyReaderThread(InputStream inputStream) {
            reader = new SAXObjectReader();
            reader.setContentHandler(finalHandler);
            inputSource = new InputSource(inputStream);
        }

        public void run() {
            try {
                while (true)
                    reader.parse(inputSource);
            } catch (EOFException e) {
//                System.err.println("caught EOFException");
            } catch (IOException | SAXException e) {
                e.printStackTrace();
            }
        }
    }

    class MyHandler extends DefaultHandler {

        private int documentCount;
        private int elementCount;
        private int attributeCount;
        private int charCount;

        public void startDocument() throws SAXException {
            documentCount++;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            elementCount++;
            attributeCount += attributes.getLength();
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        public void characters(char ch[], int start, int length) throws SAXException {
            charCount += length;
        }

        public int getDocumentCount() {
            return documentCount;
        }

        public int getElementCount() {
            return elementCount;
        }

        public int getAttributeCount() {
            return attributeCount;
        }

        public int getCharCount() {
            return charCount;
        }
    }

}
