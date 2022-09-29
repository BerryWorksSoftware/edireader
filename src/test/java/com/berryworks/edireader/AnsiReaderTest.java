package com.berryworks.edireader;

import com.berryworks.edireader.error.*;
import com.berryworks.edireader.util.sax.EDIReaderSAXAdapter;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static com.berryworks.edireader.util.Conversion.ediToxml;
import static org.junit.Assert.*;

public class AnsiReaderTest {

    private static final String EDI_SAMPLE =
            "ISA*00*          *00*          *ZZ*D00111         *ZZ*0055           *030603*1337*U*00401*000000121*0*T*:^\n" +
                    "GS*HP*D00111*0055*20030603*1337*1210001*X*004010X091A1^\n" +
                    "ST*870*0000001^\n" +
                    "BSR*4*PA*SUPPLIER CONFIRMATION NUMBER*CCYYMMDD^\n" +
                    "REF*MR*12345^\n" +
                    "SE*4*0000001^\n" +
                    "GE*1*1210001^\n" +
                    "IEA*1*000000121^\n";
    private static final String EDI_TA1_SAMPLE =
            "ISA*00*          *00*          *ZZ*D00111         *ZZ*0055           *030603*1337*U*00401*000000121*0*T*:$\n" +
                    "TA1*1*2*3*R*abc$\n" +
                    "GS*HP*D00111*0055*20030603*1337*1210001*X*004010X091A1$\n" +
                    "ST*870*0000001$\n" +
                    "BSR*4*PA*SUPPLIER CONFIRMATION NUMBER*CCYYMMDD$\n" +
                    "REF*MR*12345$\n" +
                    "SE*4*0000001$\n" +
                    "GE*1*1210001$\n" +
                    "IEA*1*000000121$\n";
    private static final String EDI_BIN_SAMPLE =
            "ISA*00*          *00*          *ZZ*D00111         *ZZ*0055           *030603*1337*U*00401*000000121*0*T*:^\n" +
                    "GS*HP*D00111*0055*20030603*1337*1210001*X*004010X091A1^\n" +
                    "ST*870*0000001^\n" +
                    "BSR*4*PA*SUPPLIER CONFIRMATION NUMBER*CCYYMMDD^\n" +
                    "BIN*10*1234567890^\n" +
                    "SE*4*0000001^\n" +
                    "GE*1*1210001^\n" +
                    "IEA*1*000000121^\n";
    private static final String EDI_SAMPLE_5010 =
            "ISA*00*          *00*          *ZZ*D00111         *ZZ*0055           *030603*1337*~*00501*000000121*0*T*:^\n" +
                    "GS*HP*D00111*0055*20030603*1337*1210001*X*005010X091A1^\n" +
                    "ST*870*0000001^\n" +
                    "BSR*4*PA*SUPPLIER CONFIRMATION NUMBER*CCYYMMDD^\n" +
                    "REF*MR*12345^\n" +
                    "SE*4*0000001^\n" +
                    "GE*1*1210001^\n" +
                    "IEA*1*000000121^\n";
    private static final String EDI_SAMPLE_WITH_TRIMMED_ISA =
            "ISA*00*NA*00*NA*ZZ*NA*ZZ*ABC*150415*1332*^*00701*000000001*0*T*:~\n" +
                    "GS*HP*D00111*0055*20030603*1337*1210001*X*004010X091A1~\n" +
                    "ST*870*0000001~\n" +
                    "BSR*4*PA*SUPPLIER CONFIRMATION NUMBER*CCYYMMDD~\n" +
                    "REF*MR*12345~\n" +
                    "SE*4*0000001~\n" +
                    "GE*1*1210001~\n" +
                    "IEA*1*000000001~\n";


    private AnsiReader ansiReader;
    private MyContentHandler myContentHandler;

    @Before
    public void setUp() {
        ansiReader = new AnsiReader();
        myContentHandler = new MyContentHandler();
        ansiReader.setContentHandler(myContentHandler);
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
        assertEquals(16, myContentHandler.getElementCount());
        assertEquals(2, myContentHandler.getSegmentCountWithoutSTandSE());
    }

    @Test
    public void supportsOptionToObserveSyntaxCharacters() throws IOException, SAXException {
        // Baseline: default behavior, without the option
        assertFalse(ansiReader.isIncludeSyntaxCharacters());
        ansiReader.parse(new InputSource(new StringReader(EDI_SAMPLE)));
        assertNull(myContentHandler.getInterchangeAttributes().getValue("SegmentTerminator"));
        assertNull(myContentHandler.getInterchangeAttributes().getValue("ElementDelimiter"));
        assertNull(myContentHandler.getInterchangeAttributes().getValue("SubElementDelimiter"));
        assertNull(myContentHandler.getInterchangeAttributes().getValue("RepetitionSeparator"));

        // Now with the option enabled, for a version 4010 interchange without a repetition separator
        setUp();
        ansiReader.setIncludeSyntaxCharacters(true);
        ansiReader.parse(new InputSource(new StringReader(EDI_SAMPLE)));
        assertEquals("^", myContentHandler.getInterchangeAttributes().getValue("SegmentTerminator"));
        assertEquals("*", myContentHandler.getInterchangeAttributes().getValue("ElementDelimiter"));
        assertEquals(":", myContentHandler.getInterchangeAttributes().getValue("SubElementDelimiter"));
        assertNull(myContentHandler.getInterchangeAttributes().getValue("RepetitionSeparator"));

        // Again with the option enabled, for a version 5010 interchange with a repetition separator
        setUp();
        ansiReader.setIncludeSyntaxCharacters(true);
        ansiReader.parse(new InputSource(new StringReader(EDI_SAMPLE_5010)));
        assertEquals("^", myContentHandler.getInterchangeAttributes().getValue("SegmentTerminator"));
        assertEquals("*", myContentHandler.getInterchangeAttributes().getValue("ElementDelimiter"));
        assertEquals(":", myContentHandler.getInterchangeAttributes().getValue("SubElementDelimiter"));
        assertEquals("~", myContentHandler.getInterchangeAttributes().getValue("RepetitionSeparator"));
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
        assertEquals(17, myContentHandler.getElementCount());
        assertEquals(2, myContentHandler.getSegmentCountWithoutSTandSE());
    }

    @Test
    public void canParseBIN() throws IOException, SAXException {
        ansiReader.parse(new InputSource(new StringReader(EDI_BIN_SAMPLE)));

        assertEquals(291, ansiReader.getCharCount());
        assertEquals(14, myContentHandler.getElementCount());
        assertEquals(2, myContentHandler.getSegmentCountWithoutSTandSE());
    }

    @Test
    public void detectsGroupCountError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("IEA*1*", "IEA*44*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Functional group count error not detected");
        } catch (GroupCountException e) {
            assertEquals("Functional group count error in IEA segment. Expected 1 instead of 44 at segment 8, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsInterchangeControlNumberError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("IEA*1*000000121", "IEA*1*000000921");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Interchange control number error not detected");
        } catch (InterchangeControlNumberException e) {
            assertEquals(
                    "Control number error in IEA segment. Expected 000000121 instead of 000000921 at segment 8, field 3",
                    e.getMessage());
        }
    }

    @Test
    public void detectsTransactionCountError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("GE*1*", "GE*44*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Transaction count error not detected");
        } catch (TransactionCountException e) {
            assertEquals("Transaction count error in GE segment. Expected 1 instead of 44 at segment 7, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsGroupControlNumberError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("GE*1*1210001^", "GE*1*9210001^");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Group control number error not detected");
        } catch (GroupControlNumberException e) {
            assertEquals(
                    "Control number error in GE segment. Expected 1210001 instead of 9210001 at segment 7, field 3",
                    e.getMessage());
        }
    }

    @Test
    public void detectsSegmentCountError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("SE*4*", "SE*44*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Segment count error not detected");
        } catch (SegmentCountException e) {
            assertEquals("Segment count error in SE segment. Expected 4 instead of 44 at segment 6, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsTransactionControlNumberError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("SE*4*0000001", "SE*4*1111111");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Transaction control number error not detected");
        } catch (TransactionControlNumberException e) {
            assertEquals(
                    "Control number error in SE segment. Expected 0000001 instead of 1111111 at segment 6, field 3",
                    e.getMessage());
        }
    }

    @Test
    public void correctsISA01FixedLength() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("ISA*00*", "ISA*0000*");
        // Tell the parser to keep going after a recoverable error
        ansiReader.setSyntaxExceptionHandler(syntaxException -> true);
        // The parsed value should have been trimmer to a length of 2
        ansiReader.setContentHandler(new MyContentHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                super.startElement(uri, localName, qName, attributes);
                if ("interchange".equals(localName)) {
                    assertEquals(2, attributes.getValue("AuthorizationQual").length());
                }
            }
        });
        ansiReader.parse(new InputSource(new StringReader(ediText)));
    }

    @Test
    public void detectsISA01FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("ISA*00*", "ISA*0000*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA01 has incorrect length. Expected 2 instead of 4 at segment 1, field 2",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA02FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("ISA*00*          *", "ISA*00*         *");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA02 has incorrect length. Expected 10 instead of 9 at segment 1, field 3",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA03FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("ISA*00*          *00*", "ISA*00*          *000*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA03 has incorrect length. Expected 2 instead of 3 at segment 1, field 4",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA04FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("ISA*00*          *00*          *", "ISA*00*          *00*           *");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA04 has incorrect length. Expected 10 instead of 11 at segment 1, field 5",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA05FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*ZZ*D00111         *", "*ZZ *D00111         *");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA05 has incorrect length. Expected 2 instead of 3 at segment 1, field 6",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA06FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*ZZ*D00111         *", "*ZZ*D00111*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA06 has incorrect length. Expected 15 instead of 6 at segment 1, field 7",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA07FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*ZZ*0055           *", "*Z*0055           *");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA07 has incorrect length. Expected 2 instead of 1 at segment 1, field 8",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA08FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*ZZ*0055           *", "*ZZ**");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA08 has incorrect length. Expected 15 instead of 0 at segment 1, field 9",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA09FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*030603*", "*20030603*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA09 has incorrect length. Expected 6 instead of 8 at segment 1, field 10",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA10FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*1337*", "*abc*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA10 has incorrect length. Expected 4 instead of 3 at segment 1, field 11",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA11FixedLengthError_StandardsId() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*U*00401*", "*UU*00401*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA11 has incorrect length. Expected 1 instead of 2 at segment 1, field 12",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA11FixedLengthError_RepetitionSeparator() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*U*00401*", "*++*00501*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA11 has incorrect length. Expected 1 instead of 2 at segment 1, field 12",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA12FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*U*00401*", "*U*00440011*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA12 has incorrect length. Expected 5 instead of 8 at segment 1, field 13",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA13FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*000000121*", "*121*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA13 has incorrect length. Expected 9 instead of 3 at segment 1, field 14",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA14FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*0*", "*000*");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA14 has incorrect length. Expected 1 instead of 3 at segment 1, field 15",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA15FixedLengthError() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*T*", "**");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA fixed length field error not detected");
        } catch (RecoverableSyntaxException e) {
            assertSame(ISAFixedLengthException.class, e.getClass());
            assertEquals(
                    "ISA fixed-length field ISA15 has incorrect length. Expected 1 instead of 0 at segment 1, field 16",
                    e.getMessage());
        }
    }

    @Test
    public void detectsISA16Error_space() throws IOException, SAXException {
        // A space is not acceptable
        String ediText = EDI_SAMPLE.replace("*0*T*:^", "*0*T* ^");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA16 (sub-element delimiter) problem not detected");
        } catch (RecoverableSyntaxException e) {
            assertEquals("Invalid ISA16 sub-element delimiter", e.getMessage());
        }
    }

    @Test
    public void detectsISA16Error_digit() throws IOException, SAXException {
        // A space is not acceptable
        String ediText = EDI_SAMPLE.replace("*0*T*:^", "*0*T*0^");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA16 (sub-element delimiter) problem not detected");
        } catch (EDISyntaxException e) {
            assertEquals("Invalid ISA16 sub-element delimiter", e.getMessage());
        }
    }

    @Test
    public void detectsISA16Error_letter() throws IOException, SAXException {
        // A space is not acceptable
        String ediText = EDI_SAMPLE.replace("*0*T*:^", "*0*T*A^");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA16 (sub-element delimiter) problem not detected");
        } catch (EDISyntaxException e) {
            assertEquals("Invalid ISA16 sub-element delimiter", e.getMessage());
        }
    }

    @Test
    public void detectsISA16Error_empty() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*0*T*:^", "*0*T^*");
        // The element delimiter after the T is missing, so the parser cannot reliably detect the segment terminator.
        // This is not a recoverable error!
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA16 (sub-element delimiter) problem not detected");
        } catch (EDISyntaxException e) {
            assertEquals("Invalid segment terminator", e.getMessage());
        }
    }

    @Test
    public void detectsISA16Error_omitted() throws IOException, SAXException {
        String ediText = EDI_SAMPLE.replace("*0*T*:^", "*0*T^");
        // The element delimiter after the T is missing, so the parser cannot reliably detect the segment terminator.
        // This is not a recoverable error!
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("ISA16 (sub-element delimiter) problem not detected");
        } catch (EDISyntaxException e) {
            assertEquals("Invalid segment terminator", e.getMessage());
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
            assertEquals("BIN object length must be numeric instead of xx at segment 5, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsMissingSEError() throws IOException {
        String ediText = EDI_SAMPLE.replace("SE*4*0000001", "SEE*4*0000001");
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Missing SE error not detected");
        } catch (SAXException e) {
            assertEquals("Transaction must be terminated with an SE segment at segment 7, field 1", e.getMessage());
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
            assertEquals("Invalid beginning of segment at segment 1", e.getMessage());
        }
    }

    @Test
    public void detectsSegmentTerminatorProblem() throws IOException {
        //                ISA*00*          *00*          *ZZ*D00111         *ZZ*0055           *030603*1337*U*00401*000000121*0*T*:$
        String ediText = "ISA*00*          *00*          *ZZ*ENS_EDI        *ZZ*UB920128       *161208*1130*^*00501*014684581*1*P*:\n" +
                "GS*HP*ENS_EDI*UB920128*20161208*1130*014684581*X*005010X221A1";
        try {
            ansiReader.parse(new InputSource(new StringReader(ediText)));
            fail("Invalid segment start not detected");
        } catch (SAXException e) {
            assertEquals("Invalid beginning of segment at segment 2", e.getMessage());
        }
    }

    @Test
    public void allowsVariableLengthForSomeIsaElements() throws IOException, SAXException {
        ansiReader.setSyntaxExceptionHandler(new EDISyntaxExceptionHandler() {
            @Override
            public boolean process(RecoverableSyntaxException syntaxException) {
                return true;
            }
        });
        ansiReader.parse(new InputSource(new StringReader(EDI_SAMPLE_WITH_TRIMMED_ISA)));

        assertEquals(245, ansiReader.getCharCount());
        assertEquals(1, ansiReader.getGroupCount());
        assertEquals('*', ansiReader.getDelimiter());
        assertEquals(':', ansiReader.getSubDelimiter());
        assertEquals('~', ansiReader.getTerminator());
        assertEquals("\n", ansiReader.getTerminatorSuffix());
        assertEquals(16, myContentHandler.getElementCount());
        assertEquals(2, myContentHandler.getSegmentCountWithoutSTandSE());
    }

    @Test
    public void producesXmlForSimpleCase() throws TransformerException {
        ansiReader = new AnsiReader();
        StringReader reader = new StringReader(EDI_SAMPLE);
        StringWriter writer = new StringWriter();
        ediToxml(reader, writer, ansiReader);
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<ediroot>" +
                        "<interchange Standard=\"ANSI X.12\" AuthorizationQual=\"00\" Authorization=\"          \" SecurityQual=\"00\" Security=\"          \" Date=\"030603\" Time=\"1337\" StandardsId=\"U\" Version=\"00401\" Control=\"000000121\" AckRequest=\"0\" TestIndicator=\"T\">" +
                        "<sender><address Id=\"D00111         \" Qual=\"ZZ\"/></sender>" +
                        "<receiver><address Id=\"0055           \" Qual=\"ZZ\"/></receiver>" +
                        "<group GroupType=\"HP\" ApplSender=\"D00111\" ApplReceiver=\"0055\" Date=\"20030603\" Time=\"1337\" Control=\"1210001\" StandardCode=\"X\" StandardVersion=\"004010X091A1\">" +
                        "<transaction DocType=\"870\" Name=\"Order Status Report\" Control=\"0000001\">" +
                        "<segment Id=\"BSR\"><element Id=\"BSR01\">4</element><element Id=\"BSR02\">PA</element><element Id=\"BSR03\">SUPPLIER CONFIRMATION NUMBER</element><element Id=\"BSR04\">CCYYMMDD</element></segment>" +
                        "<segment Id=\"REF\"><element Id=\"REF01\">MR</element><element Id=\"REF02\">12345</element></segment>" +
                        "</transaction>" +
                        "</group>" +
                        "</interchange>" +
                        "</ediroot>",
                writer.toString());

    }

    @Test
    public void recognizesEnvelopeSegments() {
        assertTrue(AnsiReader.isEnvelopeSegment("ISA"));
        assertTrue(AnsiReader.isEnvelopeSegment("GS"));
        assertTrue(AnsiReader.isEnvelopeSegment("ST"));
        assertTrue(AnsiReader.isEnvelopeSegment("SE"));
        assertTrue(AnsiReader.isEnvelopeSegment("GE"));
        assertTrue(AnsiReader.isEnvelopeSegment("IEA"));
        assertTrue(AnsiReader.isEnvelopeSegment("TA1"));
        assertFalse(AnsiReader.isEnvelopeSegment("REF"));
    }

    private static class MyContentHandler extends EDIReaderSAXAdapter {
        private int segmentCount, elementCount;
        private Attributes interchangeAttributes;

        MyContentHandler() {
            super(new DefaultXMLTags());
        }

        @Override
        protected void beginInterchange(int charCount, int segmentCharCount, Attributes attributes) {
            interchangeAttributes = new EDIAttributes(attributes);
        }

        @Override
        protected void beginFirstSegment(Attributes atts) {
            segmentCount++;
        }

        @Override
        protected void beginAnotherSegment(Attributes atts) {
            segmentCount++;
        }

        @Override
        protected void beginBinaryPackage(Attributes atts) {
            segmentCount++;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            elementCount++;
        }

        Attributes getInterchangeAttributes() {
            return interchangeAttributes;
        }

        int getSegmentCountWithoutSTandSE() {
            return segmentCount;
        }

        int getElementCount() {
            return elementCount;
        }
    }
}
