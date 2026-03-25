package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIStandard;
import com.berryworks.edireader.benchmark.EDITestData;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import static com.berryworks.edireader.EDIReaderTest.INVOIC_97B_NO_SUFFIX;
import static com.berryworks.edireader.EdifactReaderTest.EDIFACT_WITH_GROUP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class EdiProberTest {

    @Test
    public void canProbeEarlyX12NoSuffix() throws IOException, SAXException {
        EdiProber ediProber = new EdiProber();
        ediProber.probe(EDITestData.getAnsiInterchange());

        assertEquals(EDIStandard.ANSI, ediProber.getStandard());
        assertEquals("000038449", ediProber.getInterchangeControl());
        assertEquals("ZZ", ediProber.getSenderQualifier());
        assertEquals("04000          ", ediProber.getSenderId());
        assertEquals("ZZ", ediProber.getReceiverQualifier());
        assertEquals("58401          ", ediProber.getReceiverId());
        assertEquals("38327", ediProber.getFunctionalGroupControl());
        assertEquals("000042460", ediProber.getDocumentControl());
        assertEquals("002040CHRY", ediProber.getVersion());
        assertEquals("824", ediProber.getDocumentType());
        assertEquals("~", ediProber.getDelimiter());
        assertEquals("<", ediProber.getSubDelimiter());
        assertEquals("$", ediProber.getSegmentTerminator());
        assertEquals("", ediProber.getSegmentTerminatorSuffix());
        assertNull(ediProber.getRepetitionDelimiter());
        assertNull(ediProber.getReleaseCharacter());
    }

    @Test
    public void canProbeEarlyX12WithSuffix_LF() throws IOException, SAXException {
        EdiProber ediProber = new EdiProber();
        ediProber.probe(EDITestData.getAnsiInterchange().replace("$", "$\n"));

        assertEquals(EDIStandard.ANSI, ediProber.getStandard());
        assertEquals("000038449", ediProber.getInterchangeControl());
        assertEquals("38327", ediProber.getFunctionalGroupControl());
        assertEquals("000042460", ediProber.getDocumentControl());
        assertEquals("002040CHRY", ediProber.getVersion());
        assertEquals("824", ediProber.getDocumentType());
        assertEquals("~", ediProber.getDelimiter());
        assertEquals("<", ediProber.getSubDelimiter());
        assertEquals("$", ediProber.getSegmentTerminator());
        assertEquals("\n", ediProber.getSegmentTerminatorSuffix());
        assertNull(ediProber.getRepetitionDelimiter());
        assertNull(ediProber.getReleaseCharacter());
    }

    @Test
    public void canProbeEarlyX12WithSuffix_CRLF() throws IOException, SAXException {
        EdiProber ediProber = new EdiProber();
        ediProber.probe(EDITestData.getAnsiInterchange().replace("$", "$\r\n"));

        assertEquals(EDIStandard.ANSI, ediProber.getStandard());
        assertEquals("000038449", ediProber.getInterchangeControl());
        assertEquals("38327", ediProber.getFunctionalGroupControl());
        assertEquals("000042460", ediProber.getDocumentControl());
        assertEquals("002040CHRY", ediProber.getVersion());
        assertEquals("824", ediProber.getDocumentType());
        assertEquals("~", ediProber.getDelimiter());
        assertEquals("<", ediProber.getSubDelimiter());
        assertEquals("$", ediProber.getSegmentTerminator());
        assertEquals("\r\n", ediProber.getSegmentTerminatorSuffix());
        assertNull(ediProber.getRepetitionDelimiter());
        assertNull(ediProber.getReleaseCharacter());
    }

    @Test
    public void canProbeEDIFACT() throws IOException, SAXException {
        EdiProber ediProber = new EdiProber();
        ediProber.probe(INVOIC_97B_NO_SUFFIX);

        assertEquals(EDIStandard.EDIFACT, ediProber.getStandard());
        assertEquals("00000000000778", ediProber.getInterchangeControl());
        assertNull(ediProber.getFunctionalGroupControl());
        assertEquals("00000000000117", ediProber.getDocumentControl());
        assertEquals("97B", ediProber.getVersion());
        assertEquals("INVOIC", ediProber.getDocumentType());
        assertEquals("+", ediProber.getDelimiter());
        assertEquals(":", ediProber.getSubDelimiter());
        assertEquals("'", ediProber.getSegmentTerminator());
        assertEquals("", ediProber.getSegmentTerminatorSuffix());
        assertNull(ediProber.getRepetitionDelimiter());
        assertEquals("?", ediProber.getReleaseCharacter());
    }

    @Test
    public void canProbeEDIFACT_withUNG() throws IOException, SAXException {
        EdiProber ediProber = new EdiProber();
        ediProber.probe(EDIFACT_WITH_GROUP);

        assertEquals(EDIStandard.EDIFACT, ediProber.getStandard());
        assertEquals("841F60UNZ", ediProber.getInterchangeControl());
        assertEquals("16", ediProber.getFunctionalGroupControl());
        assertEquals("1", ediProber.getDocumentControl());
        assertEquals("D98A", ediProber.getVersion());
        assertEquals("DCQCKI", ediProber.getDocumentType());
        assertEquals("+", ediProber.getDelimiter());
        assertEquals(":", ediProber.getSubDelimiter());
        assertEquals("'", ediProber.getSegmentTerminator());
        assertEquals("", ediProber.getSegmentTerminatorSuffix());
        assertNull(ediProber.getRepetitionDelimiter());
        assertEquals("?", ediProber.getReleaseCharacter());
    }

    @Test
    public void probeJunk() throws IOException {
        EdiProber ediProber = new EdiProber();
        try {
            ediProber.probe("non-EDI");
        } catch (SAXException e) {
            assertEquals("No supported EDI standard interchange begins with non", e.getMessage());
        }
    }

    @Test
    public void probeFileDoesNotExist() throws IOException, SAXException {
        EdiProber ediProber = new EdiProber();
        try {
            ediProber.probe(new File("non-existent-file.txt"));
        } catch (IOException e) {
            assertEquals("non-existent-file.txt (No such file or directory)", e.getMessage());
        }
    }
}
