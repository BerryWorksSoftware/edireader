package com.berryworks.edireader;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class AnsiReaderTest {

    public static final String EDI_SAMPLE =
            "ISA*00*          *00*          *ZZ*D00111         *ZZ*0055           *030603*1337*U*00401*000000121*0*T*:^\n" +
                    "GS*HP*D00111*0055*20030603*1337*1210001*X*004010X091A1^\n" +
                    "ST*870*0000001^\n" +
                    "BSR*4*PA*SUPPLIER CONFIRMATION NUMBER*CCYYMMDD^\n" +
                    "REF*MR*12345^\n" +
                    "SE*4*0000001^\n" +
                    "GE*1*1210001^\n" +
                    "IEA*1*000000121^\n";
    public static final String EDI_TA1_SAMPLE =
            "ISA*00*          *00*          *ZZ*D00111         *ZZ*0055           *030603*1337*U*00401*000000121*0*T*:$\n" +
                    "TA1*1*2*3*R*abc$\n" +
                    "GS*HP*D00111*0055*20030603*1337*1210001*X*004010X091A1$\n" +
                    "ST*870*0000001$\n" +
                    "BSR*4*PA*SUPPLIER CONFIRMATION NUMBER*CCYYMMDD$\n" +
                    "REF*MR*12345$\n" +
                    "SE*4*0000001$\n" +
                    "GE*1*1210001$\n" +
                    "IEA*1*000000121$\n";
    private AnsiReader ansiReader;
    private CountingHandler countingHandler;

    @Before
    public void setUp() {
        ansiReader = new AnsiReader();
        countingHandler = new CountingHandler();
        ansiReader.setContentHandler(countingHandler);
    }

    @Test
    public void canParseSmallSample() throws IOException, SAXException {
        ansiReader.parse(new InputSource(new StringReader(EDI_SAMPLE)));

        assertEquals(286, ansiReader.getCharCount());
        assertEquals(1, ansiReader.getGroupCount());
        assertEquals('*', ansiReader.getDelimiter());
        assertEquals(':', ansiReader.getSubDelimiter());
        assertEquals('^', ansiReader.getTerminator());
        assertEquals("\n", ansiReader.getTerminatorSuffix());
        assertEquals(16, countingHandler.getElementCount());
    }

    @Test
    public void canParseTA1() throws IOException, SAXException {
        ansiReader.parse(new InputSource(new StringReader(EDI_TA1_SAMPLE)));

        assertEquals(303, ansiReader.getCharCount());
        assertEquals(1, ansiReader.getGroupCount());
        assertEquals('*', ansiReader.getDelimiter());
        assertEquals(':', ansiReader.getSubDelimiter());
        assertEquals('$', ansiReader.getTerminator());
        assertEquals("\n", ansiReader.getTerminatorSuffix());
        assertEquals(17, countingHandler.getElementCount());
    }


    private class CountingHandler extends DefaultHandler {
        private int elementCount;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            elementCount++;
        }

        public int getElementCount() {
            return elementCount;
        }
    }
}
