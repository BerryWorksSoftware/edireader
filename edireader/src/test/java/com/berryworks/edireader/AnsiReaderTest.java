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
import static org.junit.Assert.fail;

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
    public static final String EDI_BIN_SAMPLE =
            "ISA*00*          *00*          *ZZ*D00111         *ZZ*0055           *030603*1337*U*00401*000000121*0*T*:^\n" +
                    "GS*HP*D00111*0055*20030603*1337*1210001*X*004010X091A1^\n" +
                    "ST*870*0000001^\n" +
                    "BSR*4*PA*SUPPLIER CONFIRMATION NUMBER*CCYYMMDD^\n" +
                    "BIN*10*1234567890^\n" +
                    "SE*4*0000001^\n" +
                    "GE*1*1210001^\n" +
                    "IEA*1*000000121^\n";

    //    BIN*10*1234567890
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

    @Test
    public void canParseBIN() throws IOException, SAXException {
        ansiReader.parse(new InputSource(new StringReader(EDI_BIN_SAMPLE)));

        assertEquals(291, ansiReader.getCharCount());
        assertEquals(14, countingHandler.getElementCount());
    }

    @Test
    public void detectsGroupCountError() throws IOException {
        String ediText = EDI_SAMPLE.replace("IEA*1*", "IEA*44*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Functional group count error not detected");
        } catch (SAXException e) {
            assertEquals("Functional group count error in IEA segment. Expected 1 instead of 44 at segment 8, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsInterchangeControlNumberError() throws IOException {
        String ediText = EDI_SAMPLE.replace("IEA*1*000000121", "IEA*1*000000921");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Interchange control number error not detected");
        } catch (SAXException e) {
            assertEquals(
                    "Control number error in IEA segment. Expected 000000121 instead of 000000921 at segment 8, field 3",
                    e.getMessage());
        }
    }

    @Test
    public void detectsTransactionCountError() throws IOException {
        String ediText = EDI_SAMPLE.replace("GE*1*", "GE*44*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Transaction count error not detected");
        } catch (SAXException e) {
            assertEquals("Transaction count error in GE segment. Expected 1 instead of 44 at segment 7, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsGroupControlNumberError() throws IOException {
        String ediText = EDI_SAMPLE.replace("GE*1*1210001^", "GE*1*9210001^");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Group control number error not detected");
        } catch (SAXException e) {
            assertEquals(
                    "Control number error in GE segment. Expected 1210001 instead of 9210001 at segment 7, field 3",
                    e.getMessage());
        }
    }

    @Test
    public void detectsSegmentCountError() throws IOException {
        String ediText = EDI_SAMPLE.replace("SE*4*", "SE*44*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Segment count error not detected");
        } catch (SAXException e) {
            assertEquals("Segment count error in SE segment. Expected 4 instead of 44 at segment 6, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsTransactionControlNumberError() throws IOException {
        String ediText = EDI_SAMPLE.replace("SE*4*0000001", "SE*4*1111111");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Transaction control number error not detected");
        } catch (SAXException e) {
            assertEquals(
                    "Control number error in SE segment. Expected 0000001 instead of 1111111 at segment 6, field 3",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISAFixedLengthError() throws IOException {
        String ediText = EDI_SAMPLE.replace("ISA*00*", "ISA*0000*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fiexed length field error not detected");
        } catch (SAXException e) {
            assertEquals(
                    "Incorrect length of fixed-length ISA field. Expected 2 instead of 4 at segment 1, field 2",
                    e.getMessage());
        }
    }

    @Test
    public void detectsGSMandatoryFieldError() throws IOException {
        String ediText = EDI_SAMPLE.replace("*1337*1", "**1");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("GS mandatory field error not detected");
        } catch (SAXException e) {
            assertEquals(
                    "Mandatory element missing. Expected at least one non-space character instead of (empty) at segment 2, field 6",
                    e.getMessage());
        }
    }

    @Test
    public void detectsBiNSegmentLengthError() throws IOException {
        String ediText = EDI_BIN_SAMPLE.replace("BIN*10", "BIN*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("BIN length error not detected");
        } catch (SAXException e) {
            assertEquals(
                    "BIN segment missing mandatory length field at segment 5, field 2",
                    e.getMessage());
        }
    }

    @Test
    public void detectsBiNSegmentLengthNumericError() throws IOException {
        String ediText = EDI_BIN_SAMPLE.replace("BIN*10", "BIN*xx");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("BIN numeric length error not detected");
        } catch (SAXException e) {
            assertEquals(
                    "BIN object length must be numeric instead of xx at segment 5, field 2",
                    e.getMessage());
        }
    }

    @Test
    public void detectsMissingSEError() throws IOException {
        String ediText = EDI_SAMPLE.replace("SE*4*0000001", "SEE*4*0000001");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Missing SE error not detected");
        } catch (SAXException e) {
            assertEquals(
                    "Transaction must be terminated with an SE segment at segment 7, field 1",
                    e.getMessage());
        }
    }

    @Test
    public void detectsMissingGEError() throws IOException {
        String ediText = EDI_SAMPLE.replace("GE*1*1210001", "xGE*1*1210001");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Missing GE error not detected");
        } catch (SAXException e) {
            assertEquals(
                    "Unexpected segment type in this context. Expected GE or ST instead of xGE at segment 7, field 1",
                    e.getMessage());
        }
    }

    @Test
    public void detectsInvalidSegmentStartError() throws IOException {
        String ediText = EDI_SAMPLE.replace("GS*HP*", "*HP*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Invalid segment start not detected");
        } catch (SAXException e) {
            assertEquals(
                    "Invalid beginning of segment at segment 1",
                    e.getMessage());
        }
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
