/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.util;


import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;

public class TestBranchingWriter {

    @Test
    public void testSimpleWrite() throws IOException {
        StringWriter sw = new StringWriter();

        String testString = "abc";

        try (Writer writer = new BranchingWriter(sw)) {
            writer.write(testString);
        }

        assertEquals(testString, sw.toString());
    }

    @Test
    public void testBranch() throws IOException {
        StringWriter sw = new StringWriter();

        BranchingWriter writer = new BranchingWriter(sw);
        writer.write("Part1/");

        writer.writeTrunk("Part2trunk/");
        writer.writeBranch("Part2branch/");

        writer.write("Part3");
        writer.closeUsingBranch();

        assertEquals("Part1/Part2branch/Part3", sw.toString());
    }

    @Test
    public void testTrunk() throws IOException {
        StringWriter sw = new StringWriter();

        try (BranchingWriter writer = new BranchingWriter(sw)) {
            writer.write("Part1/");

            writer.writeTrunk("Part2trunk/");
            writer.writeBranch("Part2branch/");

            writer.write("Part3");
        }

        assertEquals("Part1/Part2trunk/Part3", sw.toString());
    }
}
