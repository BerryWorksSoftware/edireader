package com.berryworks.edireader;

import com.berryworks.edireader.util.sax.EDIReaderSAXAdapter;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class EdifactEncodingTest {

    public static final Charset ISO_8859_5 = Charset.forName("ISO-8859-5");
    private EDIReader ediReader;
    private MyContentHandler handler;

    @Before
    public void setUp() {
        handler = new MyContentHandler();
    }

    @Test
    public void baseline() throws IOException, SAXException {
        StringReader reader = new StringReader(EDIFACT_UNOA);
        ediReader = EDIReaderFactory.createEDIReader(reader);
        ediReader.setContentHandler(handler);
        ediReader.parse();
        assertEquals("GENERAL WIDGET COMPANY", handler.getNad04());
    }

    @Test
    public void baseline_asBytes() throws IOException, SAXException {
        InputStream inputStream = new ByteArrayInputStream(EDIFACT_UNOA.getBytes(StandardCharsets.UTF_8));
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        ediReader = EDIReaderFactory.createEDIReader(new InputSource(reader));
        ediReader.setContentHandler(handler);
        ediReader.parse();
        assertEquals("GENERAL WIDGET COMPANY", handler.getNad04());
    }

    @Test
    public void baseline_asByteStream() throws IOException, SAXException {
        InputStream inputStream = new ByteArrayInputStream(EDIFACT_UNOA.getBytes(StandardCharsets.UTF_8));
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        ediReader = EDIReaderFactory.createEDIReader(new InputSource(inputStream));
        ediReader.setContentHandler(handler);
        ediReader.parse();
        assertEquals("GENERAL WIDGET COMPANY", handler.getNad04());
    }

    @Test
    public void unoE() throws IOException, SAXException {
        StringReader reader = new StringReader(EDIFACT_UNOE);
        ediReader = EDIReaderFactory.createEDIReader(reader);
        ediReader.setContentHandler(handler);
        ediReader.parse();
        assertEquals("Рыба текст", handler.getNad04());
    }

    @Test
    public void unoE_asBytes() throws IOException, SAXException {
        byte[] bytes = EDIFACT_UNOE.getBytes(ISO_8859_5);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        System.out.println("Input has " + EDIFACT_UNOE.length() + " characters and " + bytes.length + " bytes");
        Reader reader = new InputStreamReader(inputStream, ISO_8859_5);
        ediReader = EDIReaderFactory.createEDIReader(new InputSource(reader));
        ediReader.setContentHandler(handler);
        ediReader.parse();
        assertEquals("Рыба текст", handler.getNad04());
    }

    @Test
    public void experiment() throws IOException {
        // Start with in InputStream of bytes, encoded as ISO-8859-5
        byte[] bytes = EDIFACT_UNOE.getBytes(ISO_8859_5);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        System.out.println("available bytes: " + inputStream.available());

        // Read a few bytes directly from the InputStream, and interpret them as ISO-8859-1
        byte[] prefixBytes = new byte[10];
        int bytesRead = inputStream.read(prefixBytes);
        String prefixString = new String(prefixBytes, 0, bytesRead, StandardCharsets.ISO_8859_1);
        System.out.println("Prefix read as ISO-8859-1: " + prefixString);
        System.out.println("available bytes: " + inputStream.available());

        // Now read the rest of the InputStream as ISO-8859-5
        Reader reader_8859_5 = new InputStreamReader(inputStream, ISO_8859_5);
        char[] cbuf = new char[1000];
        int n = reader_8859_5.read(cbuf);
        String remainder = new String(cbuf, 0, n);
        String suffix = remainder.substring(remainder.length() - 50);
        System.out.println("Remaining " + remainder.length() + " bytes read as ISO-8859-5 characters: " +
                           remainder.substring(0, 50) + " ... " + suffix);
        System.out.println("available bytes: " + inputStream.available());
    }


    private static final String EDIFACT_UNOA = """
            UNB+UNOA:1+005435656:1+006415160CFS:1+000210:1434+00000000000778+rref+aref+p+a+cid+t'
            UNH+00000000000117+INVOIC:D:97B:UN'
            BGM+380+342459+9'
            NAD+SE+005435656::16++GENERAL WIDGET COMPANY'
            UNT+4+00000000000117'
            UNZ+1+00000000000778'
            """;

    private static final String EDIFACT_UNOE = """
            UNB+UNOE:1+005435656:1+006415160CFS:1+000210:1434+00000000000778+rref+aref+p+a+cid+t'
            UNH+00000000000117+INVOIC:D:97B:UN'
            BGM+380+342459+9'
            NAD+SE+005435656::16++Рыба текст'
            UNT+4+00000000000117'
            UNZ+1+00000000000778'
            """;

    private static class MyContentHandler extends EDIReaderSAXAdapter {

        private boolean armed;
        private String nad04;

        @Override
        protected void beginSegmentElement(Attributes atts) {
            String id = atts.getValue("Id");
            armed = "NAD04".equals(id);
        }

        @Override
        protected void endSegmentElement(String value) {
            if (armed) nad04 = value;
        }

        public String getNad04() {
            return nad04;
        }
    }
}
