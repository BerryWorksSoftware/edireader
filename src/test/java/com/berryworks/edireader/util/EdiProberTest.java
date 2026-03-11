package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIStandard;
import com.berryworks.edireader.benchmark.EDITestData;
import org.junit.Test;

import static com.berryworks.edireader.EDIReaderTest.INVOIC_97B_NO_SUFFIX;
import static org.junit.Assert.*;

public class EdiProberTest {

    @Test
    public void canProbeEarlyX12NoSuffix() {
        EdiProber ediProber = new EdiProber();
        assertTrue(ediProber.probe(EDITestData.getAnsiInterchange()));

        assertEquals(EDIStandard.ANSI, ediProber.getStandard());
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
    public void canProbeEarlyX12WithSuffix_LF() {
        EdiProber ediProber = new EdiProber();
        assertTrue(ediProber.probe(EDITestData.getAnsiInterchange().replace("$", "$\n")));

        assertEquals(EDIStandard.ANSI, ediProber.getStandard());
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
    public void canProbeEarlyX12WithSuffix_CRLF() {
        EdiProber ediProber = new EdiProber();
        assertTrue(ediProber.probe(EDITestData.getAnsiInterchange().replace("$", "$\r\n")));

        assertEquals(EDIStandard.ANSI, ediProber.getStandard());
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
    public void canProbeEDIFACT() {
        EdiProber ediProber = new EdiProber();
        assertTrue(ediProber.probe(INVOIC_97B_NO_SUFFIX));

        assertEquals(EDIStandard.EDIFACT, ediProber.getStandard());
        assertEquals("97B", ediProber.getVersion());
        assertEquals("INVOIC", ediProber.getDocumentType());
        assertEquals("+", ediProber.getDelimiter());
        assertEquals(":", ediProber.getSubDelimiter());
        assertEquals("'", ediProber.getSegmentTerminator());
        assertEquals("", ediProber.getSegmentTerminatorSuffix());
        assertNull(ediProber.getRepetitionDelimiter());
        assertEquals("?", ediProber.getReleaseCharacter());
    }

}
