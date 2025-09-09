package com.berryworks.edireader;

import com.berryworks.edireader.util.sax.EDIReaderSAXAdapter;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class EdifactEncodingTest {

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
        ediReader.parse(reader);
        assertEquals("GENERAL WIDGET COMPANY", handler.getNad04());

        new ByteArrayOutputStream();
    }

    @Test
    public void test_UNOE() throws IOException, SAXException {
        StringReader reader = new StringReader(EDIFACT_UNOE);
        ediReader = EDIReaderFactory.createEDIReader(reader);
        ediReader.setContentHandler(handler);
        ediReader.parse(reader);
        assertEquals("Рыба текст", handler.getNad04());
    }

    public static void main(String[] args) {
        String text = "Рыба текст"; // Example string with Cyrillic characters

        try {
            // Create a ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            // Write string bytes into the stream (UTF-8 encoding)
            baos.write(text.getBytes(StandardCharsets.UTF_8));

            // Get the byte array
            byte[] byteArray = baos.toByteArray();

            // Print the byte array values
            System.out.println("Byte array:");
            for (byte b : byteArray) {
                System.out.print(b + " ");
            }

            // Cleanup
            baos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
