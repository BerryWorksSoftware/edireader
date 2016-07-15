/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader;

import com.berryworks.edireader.plugin.LoopDescriptor;
import org.junit.Test;

import static com.berryworks.edireader.Plugin.ANY_CONTEXT;
import static com.berryworks.edireader.Plugin.INITIAL_CONTEXT;
import static org.junit.Assert.*;

public class LoopDescriptorTest {

    private LoopDescriptor loopDescriptor;

    @Test
    public void canConstruct() {
        loopDescriptor = new LoopDescriptor("LoopName", "FirstSegment", 3, "CurrentLoop");
        assertEquals("LoopName", loopDescriptor.getName());
        assertEquals("FirstSegment", loopDescriptor.getFirstSegment());
        assertEquals(3, loopDescriptor.getNestingLevel());
        assertEquals(-1, loopDescriptor.getLevelContext());
        assertEquals("CurrentLoop", loopDescriptor.getLoopContext());
        assertFalse(loopDescriptor.isAnyContext());
        assertEquals(
                "loop LoopName at nesting level 3: encountering segment FirstSegment while currently in loop CurrentLoop",
                loopDescriptor.toString());
    }

    @Test
    public void canConstructWithDefaultContext() {
        loopDescriptor = new LoopDescriptor("LoopName", "FirstSegment", 2);
        assertEquals("LoopName", loopDescriptor.getName());
        assertEquals("FirstSegment", loopDescriptor.getFirstSegment());
        assertEquals(2, loopDescriptor.getNestingLevel());
        assertEquals(-1, loopDescriptor.getLevelContext());
        assertEquals(ANY_CONTEXT, loopDescriptor.getLoopContext());
        assertTrue(loopDescriptor.isAnyContext());
        assertEquals(
                "loop LoopName at nesting level 2: encountering segment FirstSegment anytime",
                loopDescriptor.toString());
    }

    @Test
    public void canConstructWithDefaultNestingLevel() {
        loopDescriptor = new LoopDescriptor("LoopName", "FirstSegment");
        assertEquals("LoopName", loopDescriptor.getName());
        assertEquals("FirstSegment", loopDescriptor.getFirstSegment());
        assertEquals(1, loopDescriptor.getNestingLevel());
        assertEquals(-1, loopDescriptor.getLevelContext());
        assertEquals(INITIAL_CONTEXT, loopDescriptor.getLoopContext());
        assertFalse(loopDescriptor.isAnyContext());
        assertEquals(
                "loop LoopName at nesting level 1: encountering segment FirstSegment while outside any loop",
                loopDescriptor.toString());
    }

    @Test
    public void canConstructWithLevelContext() {
        loopDescriptor = new LoopDescriptor("LoopName", "FirstSegment", 3, 4);
        assertEquals("LoopName", loopDescriptor.getName());
        assertEquals("FirstSegment", loopDescriptor.getFirstSegment());
        assertEquals(3, loopDescriptor.getNestingLevel());
        assertEquals(4, loopDescriptor.getLevelContext());
        assertEquals(ANY_CONTEXT, loopDescriptor.getLoopContext());
        assertTrue(loopDescriptor.isAnyContext());
        assertEquals(
                "loop LoopName at nesting level 3: encountering segment FirstSegment anytime while current at nesting level 4",
                loopDescriptor.toString());
    }

    @Test
    public void testEquals() {
        loopDescriptor = new LoopDescriptor("name", "firstSegment", 1, ANY_CONTEXT);
        assertEquals(loopDescriptor, loopDescriptor);
        assertNotEquals(loopDescriptor, "A String");
        assertNotEquals(loopDescriptor, null);

        LoopDescriptor d2 = new LoopDescriptor("name", "firstSegment", 1, ANY_CONTEXT);
        LoopDescriptor d3 = new LoopDescriptor("namex", "firstSegment", 1, INITIAL_CONTEXT);
        LoopDescriptor d4 = new LoopDescriptor("name", "firstSegmentx", 1, INITIAL_CONTEXT);
        LoopDescriptor d5 = new LoopDescriptor("name", "firstSegment", 2, INITIAL_CONTEXT);
        LoopDescriptor d6 = new LoopDescriptor("name", "firstSegment", 1, "contextx");

        assertEquals(loopDescriptor, d2);
        assertNotEquals(loopDescriptor, d3);
        assertNotEquals(loopDescriptor, d4);
        assertNotEquals(loopDescriptor, d5);
        assertNotEquals(loopDescriptor, d6);
    }

    @Test
    public void testHashCode() {
        loopDescriptor = new LoopDescriptor("name", "firstSegment", 1, ANY_CONTEXT);
        LoopDescriptor d2 = new LoopDescriptor("name", "firstSegment", 1, ANY_CONTEXT);
        LoopDescriptor d3 = new LoopDescriptor("namex", "firstSegment", 1, INITIAL_CONTEXT);
        assertEquals(loopDescriptor.hashCode(), d2.hashCode());
        assertNotEquals(loopDescriptor.hashCode(), d3.hashCode());
    }

}
