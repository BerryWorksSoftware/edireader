/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandLineTest {

    private CommandLine commandLine;
    private String args[];

    @Before
    public void setUp() {
        args = new String[]{"x", "y", "-a", "A", "42", "-b", "B", "-c", "7"};
        commandLine = new CommandLine(args);
    }

    @Test
    public void testPosition() {
        assertEquals(6, commandLine.size());

        assertEquals("x", commandLine.getPosition(0));
        assertEquals("y", commandLine.getPosition(1));
        assertEquals(42, commandLine.getPositionAsInt(2));

        assertNull(commandLine.getPosition(3));
        assertEquals("defaultValue", commandLine.getPosition(3, "defaultValue"));
    }

    @Test
    public void testPositionWithNullDefaultValue() {

        assertEquals("y", commandLine.getPosition(1, null));
        try {
            commandLine.getPosition(3, null);
            fail();
        } catch (RuntimeException expected) {
            assertEquals("Required argument missing at position 3", expected.getMessage());
        }
    }

}
