package com.berryworks.edireader.util;

import com.berryworks.edireader.benchmark.EDITestData;
import org.junit.Test;

import static org.junit.Assert.*;

public class EdiProberTest {

    @Test
    public void canProbeEarlyX12NoSuffix() {
        EdiProber ediProber = new EdiProber();
        String edi = EDITestData.getAnsiInterchange();
        assertTrue(ediProber.probe(edi));
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
        String edi = EDITestData.getAnsiInterchange();
        edi = edi.replace("$", "$\n");
        assertTrue(ediProber.probe(edi));
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
        String edi = EDITestData.getAnsiInterchange();
        edi = edi.replace("$", "$\r\n");
        assertTrue(ediProber.probe(edi));
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
        String edi = EDITestData.getEdifactInterchange();
        edi = edi.replace("$", "$\n");
        assertTrue(ediProber.probe(edi));
        assertEquals("+", ediProber.getDelimiter());
        assertEquals(":", ediProber.getSubDelimiter());
        assertEquals("'", ediProber.getSegmentTerminator());
        assertEquals("\n", ediProber.getSegmentTerminatorSuffix());
        assertNull(ediProber.getRepetitionDelimiter());
        assertEquals("?", ediProber.getReleaseCharacter());
    }

}
