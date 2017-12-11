package com.berryworks.edireader;

import org.junit.Test;

import static org.junit.Assert.*;

public class EDIStandardTest {

    @Test
    public void correctNumberOfValues() {
        assertEquals(7, EDIStandard.values().length);
    }

    @Test
    public void canX12Variations() {
        assertSame(EDIStandard.ANSI, EDIStandard.select("X.12"));
        assertSame(EDIStandard.ANSI, EDIStandard.select("ANSI"));
        assertSame(EDIStandard.ANSI, EDIStandard.select("X12"));
        assertSame(EDIStandard.ANSI, EDIStandard.select("x.12"));
        assertSame(EDIStandard.ANSI, EDIStandard.select("ansi"));
        assertSame(EDIStandard.ANSI, EDIStandard.select("x12"));
    }

    @Test
    public void returnsNullWhenNoMatch() {
        assertNull(EDIStandard.select("???"));
    }

    @Test
    public void canSelect() {
        assertSame(EDIStandard.ANSI, EDIStandard.select("ANSI"));
        assertSame(EDIStandard.EDIFACT, EDIStandard.select("EDIFACT"));
    }
}
