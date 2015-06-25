/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util;

import com.berryworks.edireader.DefaultXMLTags;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.benchmark.EDITestData;
import com.berryworks.edireader.demo.EDISplitter;
import com.berryworks.edireader.splitter.HandlerFactory;
import com.berryworks.edireader.splitter.SplittingHandler;
import com.berryworks.edireader.util.dom.DocumentUtil;
import com.berryworks.edireader.util.sax.SAXObjectHandler;
import com.berryworks.edireader.util.sax.SAXObjectReader;
import org.custommonkey.xmlunit.Diff;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import java.io.*;

import static org.junit.Assert.*;

public class SplitterTest extends VerboseTestCase {

    private Reader inputReader;

    @Test
    public void testOneInterchangeOneDocument() throws Exception {
        inputReader = new StringReader(EDITestData.getAnsiInterchange());
        MyHandlerFactory factory = new MyHandlerFactory();
        new SplittingHandler(factory).split(new InputSource(inputReader));
        assertEquals(1, factory.getCreateCalls());
        assertEquals(332, factory.getSAXEventsWritten());
        assertEquals(1, factory.getInterchangeCount());
        assertEquals(1, factory.getGroupCount());
        assertEquals(1, factory.getTransactionCount());

        factory.getReaderThread().join();
        assertEquals(331, factory.getSAXEventsRead());
    }

    @Test
    public void testOneInterchangeTwoGroupsOneDocumentEach() throws Exception {
        inputReader = new StringReader(EDITestData.getAnsiInterchange(2, 1));
        MyHandlerFactory factory = new MyHandlerFactory();
        new SplittingHandler(factory).split(new InputSource(inputReader));
        assertEquals(2, factory.getCreateCalls());
        assertEquals(664, factory.getSAXEventsWritten());
        assertEquals(2, factory.getInterchangeCount());
        assertEquals(2, factory.getGroupCount());
        assertEquals(2, factory.getTransactionCount());

        factory.getReaderThread().join();
        assertEquals(662, factory.getSAXEventsRead());
    }

    @Test
    public void testOneInterchangeOneDocumentIntoDOM() throws Exception {
        inputReader = new StringReader(EDITestData.getAnsiInterchange());
        MyDomHandlerFactory factory = new MyDomHandlerFactory();
        new SplittingHandler(factory).split(new InputSource(inputReader));
        assertEquals(1, factory.getCreateCalls());
        assertEquals(332, factory.getSAXEventsWritten());

        factory.shutdown();

        Document controlDom = generateDOM(EDITestData.getAnsiInputSource());
        Diff diff = new Diff(controlDom, factory.getDom());
        boolean identical = diff.identical();
        if (!identical) {
            fail(diff.toString());
        }
    }

    private Document generateDOM(InputSource inputSource) throws IOException, EDISyntaxException, TransformerException {
        XMLReader ediReader = EDIReaderFactory.createEDIReader(inputSource);
        SAXSource source = new SAXSource(ediReader, inputSource);
        DOMResult domResult = new DOMResult();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, domResult);
        return (Document) domResult.getNode();
    }

    @Test
    public void testTwoInterchangeOneDocumentEach() throws Exception {
        String ansiInterchange = EDITestData.getAnsiInterchange();
        ansiInterchange = ansiInterchange + ansiInterchange;
        inputReader = new StringReader(ansiInterchange);
        MyHandlerFactory factory = new MyHandlerFactory();
        new SplittingHandler(factory).split(new InputSource(inputReader));
        assertEquals(2, factory.getCreateCalls());
        assertEquals(664, factory.getSAXEventsWritten());
        factory.getReaderThread().join();
        assertEquals(662, factory.getSAXEventsRead());
        assertEquals(2, factory.getInterchangeCount());
        assertEquals(2, factory.getGroupCount());
        assertEquals(2, factory.getTransactionCount());
    }

    @Test
    public void testTwoInterchangeTwoDocumentsEach() throws Exception {
        String ansiInterchange = EDITestData.getAnsiInterchange(2);
        ansiInterchange = ansiInterchange + ansiInterchange;
        MyHandlerFactory factory = new MyHandlerFactory();
        new SplittingHandler(factory).split(new InputSource(new StringReader(ansiInterchange)));
        assertEquals(4, factory.getCreateCalls());
        assertEquals(1328, factory.getSAXEventsWritten());
        factory.getReaderThread().join();
        assertEquals(1324, factory.getSAXEventsRead());
        assertEquals(4, factory.getInterchangeCount());
        assertEquals(4, factory.getGroupCount());
        assertEquals(4, factory.getTransactionCount());
    }

    @Test
    public void testTwoInterchangeTwoDocumentsEachIntoDOMs() throws Exception {
        String ansiInterchange = EDITestData.getAnsiInterchange(2);
        ansiInterchange = ansiInterchange + ansiInterchange;
        inputReader = new StringReader(ansiInterchange);
        MyDomHandlerFactory factory = new MyDomHandlerFactory();
        new SplittingHandler(factory).split(new InputSource(inputReader));

        assertEquals(4, factory.getCreateCalls());
        assertEquals(1328, factory.getSAXEventsWritten());

        factory.shutdown();

        Document controlDom = generateDOM(EDITestData.getAnsiInputSource());
        Diff diff = new Diff(controlDom, factory.getDom());
        boolean identical = diff.identical();
        if (!identical) {
            fail(diff.toString());
        }
    }

    @Test
    public void testMain() throws Exception {

        PrintStream systemOut = System.out;
        final ByteArrayOutputStream redirectedOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(redirectedOut));


        FileUtil.stringToFile(EDITestData.getAnsiInterchange(2), "toSplit.edi");

        String args[] = new String[]{"toSplit.edi", "-o", getTestresultsPath() + "x12/split-0000.xml"};
        EDISplitter.main(args);

        System.setOut(systemOut);

        assertEquals("\nEDI input parsed into 2 XML output files\n", redirectedOut.toString());

        assertEquals(2, EDISplitter.getCount());

        File file = new File(getTestresultsPath() + "x12/split-0002.xml");
        assertNotNull(file);
        assertTrue(file.exists());

        InputSource inputSource = new InputSource(new FileReader(file));
        DocumentUtil util = DocumentUtil.getInstance();
        Document dom = util.buildDocumentFromXml(inputSource);
        assertNotNull(dom);

        Element element = DocumentUtil.position(dom.getDocumentElement(), new String[]{"interchange"});
        String controlAttribute = element.getAttribute("Control");
        assertEquals("000038449", controlAttribute);

        element = DocumentUtil.position(element, new String[]{"sender", "address"});
        String qualAttribute = element.getAttribute("Qual");
        assertEquals("ZZ", qualAttribute);
    }


    class MyHandlerFactory implements HandlerFactory {
        private int createCalls;
        private SAXObjectHandler handler;
        private InputStream pipedInputStream;
        private Runnable readerRunnable;
        private Thread readerThread;
        private int interchangeCount, groupCount, transactionCount;

        public MyHandlerFactory() {
            try {
                OutputStream pipedOutputStream = new PipedOutputStream();
                pipedInputStream = new PipedInputStream((PipedOutputStream) pipedOutputStream);
//                outputStream = new FileOutputStream("queue.ser");
                handler = new SAXObjectHandler(pipedOutputStream);

                readerRunnable = createRunnable(pipedInputStream);
                readerThread = new Thread(readerRunnable);
                readerThread.start();

            } catch (IOException e) {
                throw new RuntimeException("Unable to construct SAXObjectHandler", e);
            }
        }

        protected Runnable createRunnable(InputStream stream) {
            return new MyReaderThread(this, stream);
        }

        @Override
        public void markEndOfStream() throws IOException {
            handler.markEndOfStream();
        }

        @Override
        public ContentHandler createDocument() {
            createCalls++;
            return handler;
        }

        @Override
        public void closeDocument(
                String senderQualifier, String senderId,
                String receiverQualifier, String receiverId,
                String interchangeControlNumber, String groupControlNumber, String documentControlNumber,
                String documentType) throws IOException {
        }

        public int getCreateCalls() {
            return createCalls;
        }

        public int getSAXEventsWritten() {
            return handler.getSAXEventsWritten();
        }

        public int getSAXEventsRead() {
            return ((MyReaderThread) readerRunnable).getSAXEventsWritten();
        }

        public Thread getReaderThread() {
            return readerThread;
        }

        public void close() throws IOException {
            System.err.println("closing pipedInputStream");
            pipedInputStream.close();
        }

        public int getInterchangeCount() {
            return interchangeCount;
        }

        public void incrementInterchangeCount() {
            interchangeCount++;
        }

        public int getGroupCount() {
            return groupCount;
        }

        public void incrementGroupCount() {
            groupCount++;
        }

        public int getTransactionCount() {
            return transactionCount;
        }

        public void incrementTransactionCount() {
            transactionCount++;
        }

    }

    class MyDomHandlerFactory extends MyHandlerFactory {
        private MyTransformThread transformThread;

        protected Runnable createRunnable(InputStream stream) {
            transformThread = new MyTransformThread(this, stream);
            return transformThread;
        }

        public Document getDom() {
            return transformThread.getDom();
        }

        public void shutdown() throws InterruptedException {
            transformThread.shutdown();
            getReaderThread().join();
        }
    }

    class MyReaderThread implements Runnable {
        protected final XMLReader reader;
        protected final InputSource inputSource;
        protected final InputStream inputStream;
        private MyHandler finalHandler;

        public MyReaderThread(MyHandlerFactory handlerFactory, InputStream inputStream) {
            reader = new SAXObjectReader();
            finalHandler = new MyHandler(handlerFactory);
            reader.setContentHandler(finalHandler);
            this.inputStream = inputStream;
            inputSource = new InputSource(inputStream);
        }

        public void run() {
            try {
                while (true)
                    reader.parse(inputSource);
            } catch (EOFException ignored) {
            } catch (IOException | SAXException e) {
                System.err.println(getClass().getName() + " caught " + e);
            }
            if (verbose) trace(getClass().getName() + " run() complete");
        }

        public int getSAXEventsWritten() {
            return finalHandler.getSAXEventsRead();
        }

    }

    class MyTransformThread extends MyReaderThread {

        private Document document;
        private volatile boolean shutdown = false;

        public MyTransformThread(MyHandlerFactory factory, InputStream inputStream) {
            super(factory, inputStream);
        }

        public void run() {

            try {
                while (!shutdown) {
                    if (verbose) trace("available in transformer input stream: " + inputStream.available());
                    DOMResult domResult = new DOMResult();
                    SAXSource source = new SAXSource(reader, inputSource);
                    if (verbose) trace("calling transform");
                    TransformerFactory.newInstance().newTransformer().transform(source, domResult);
                    if (verbose) trace("return from transform");
                    if (document == null)
                        document = (Document) domResult.getNode();
                }
            } catch (Exception e) {
                if (verbose) trace(getClass().getName() + " caught " + e);
                if (verbose) trace(getClass().getName() + " run() complete");
            }
        }

        public Document getDom() {
            return document;
        }

        public void shutdown() {
            shutdown = true;
        }
    }


    class MyHandler extends DefaultHandler {

        private int documentCount;
        private int elementCount;
        private int attributeCount;
        private int charCount;
        private int sAXEventsRead;
        private final MyHandlerFactory handlerFactory;

        public MyHandler(MyHandlerFactory handlerFactory) {
            this.handlerFactory = handlerFactory;
        }

        public void startDocument() throws SAXException {
            documentCount++;
            sAXEventsRead++;
        }

        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            elementCount++;
            attributeCount += attributes.getLength();
            sAXEventsRead++;
            if (DefaultXMLTags.getInstance().getInterchangeTag().equals(qName)) {
                handlerFactory.incrementInterchangeCount();
            } else if (DefaultXMLTags.getInstance().getGroupTag().equals(qName)) {
                handlerFactory.incrementGroupCount();
            } else if (DefaultXMLTags.getInstance().getDocumentTag().equals(qName)) {
                handlerFactory.incrementTransactionCount();
            }
        }

        public void endElement(String uri, String localName, String qName) throws SAXException {
            sAXEventsRead++;
        }

        public void characters(char ch[], int start, int length) throws SAXException {
            charCount += length;
            sAXEventsRead++;
        }

        public int getSAXEventsRead() {
            return sAXEventsRead;
        }

    }


}
