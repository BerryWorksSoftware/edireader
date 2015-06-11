/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader;

import com.berryworks.edireader.plugin.LoopDescriptor;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoopDescriptorTest {
    private Plugin plugin;

    @Before
    public void setUp() {
        plugin = new MockPlugin();
    }

    @Test
    public void testBasics() {
        assertEquals("dt", plugin.getDocumentType());
        assertEquals("dn", plugin.getDocumentName());
        assertNull(plugin.query("x", null, -1));
    }

    @Test
    public void testEquals() {
        String context1 = "current";
        //noinspection RedundantStringConstructorCall
        String context2 = new String("current");

        assertTrue(context1.equals(context2));
        //noinspection StringEquality
        assertFalse(context1 == context2);

        LoopDescriptor d1 = new LoopDescriptor("name", "firstSegment", 1, context1);
        LoopDescriptor d2 = new LoopDescriptor("name", "firstSegment", 1, context2);
        assertEquals(d1, d1);
        assertEquals(d1, d2);

        LoopDescriptor d3 = new LoopDescriptor("namex", "firstSegment", 1, context1);
        LoopDescriptor d4 = new LoopDescriptor("name", "firstSegmentx", 1, context1);
        LoopDescriptor d5 = new LoopDescriptor("name", "firstSegment", 2, context1);
        LoopDescriptor d6 = new LoopDescriptor("name", "firstSegment", 1, "contextx");
        assertFalse(d1.equals(d3));
        assertFalse(d1.equals(d4));
        assertFalse(d1.equals(d5));
        assertFalse(d1.equals(d6));

    }

    @Test
    public void testTransition() {
        LoopDescriptor[] loops = {
                new LoopDescriptor("A", "seg1", 1, "*"),
                new LoopDescriptor("*", "N1", 1, "N1"),
                new LoopDescriptor("LX", "LX", 1, "*"),
                new LoopDescriptor("N1", "N1", 2, "LX"),
                new LoopDescriptor("*", "P1", 1, "*"),
                new LoopDescriptor("L5", "L5", 2, "*"),
                new LoopDescriptor("L1", "L1", 3, "*"),
                new LoopDescriptor("*", "L3", 0, "*")
        };
        ((MockPlugin) plugin).setDescriptors(loops);
        plugin.prepare();

        plugin.debug(false);
        LoopDescriptor response = plugin.query("seg1", null, -1);
        assertNotNull(response);
        assertEquals("A", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("seg1", "A", 1);
        assertNotNull(response);
        assertEquals("A", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("LX", null, 0);
        assertNotNull(response);
        assertEquals("LX", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("N1", "LX", 1);
        assertNotNull(response);
        assertEquals("N1", response.getName());
        assertEquals(2, response.getNestingLevel());

        response = plugin.query("L1", "N1", 2);
        assertNotNull(response);
        assertEquals("L1", response.getName());
        assertEquals(3, response.getNestingLevel());

        response = plugin.query("L1", "L1", 3);
        assertNotNull(response);
        assertEquals("L1", response.getName());
        assertEquals(3, response.getNestingLevel());
    }

    @Test
    public void testLevelSensitive() {
        LoopDescriptor[] loops = {
                new LoopDescriptor("A", "seg1", 1, "*"),
                new LoopDescriptor("B", "seg2", 2, "A"),
                new LoopDescriptor("C", "seg3", 3, 2),
        };
        ((MockPlugin) plugin).setDescriptors(loops);
        plugin.prepare();

        plugin.debug(false);
        LoopDescriptor response;

        response = plugin.query("seg1", null, 0);
        assertNotNull(response);
        assertEquals("A", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("seg2", "A", 1);
        assertNotNull(response);
        assertEquals("B", response.getName());
        assertEquals(2, response.getNestingLevel());

        response = plugin.query("seg3", "B", 2);
        assertNotNull(response);
        assertEquals("C", response.getName());
        assertEquals(3, response.getNestingLevel());

        response = plugin.query("seg3", "C", 3);
        assertNull(response);
    }

    @Test
    public void testStackContextQuery() {
        LoopDescriptor[] loops = {
                new LoopDescriptor("A", "seg1", 1),
                new LoopDescriptor("B", "seg2", 2, "/A"),
                new LoopDescriptor("C", "seg3", 3, "/A/B"),
        };
        ((MockPlugin) plugin).setDescriptors(loops);
        plugin.prepare();

        plugin.debug(false);
        LoopDescriptor response;

        response = plugin.query("seg1", null, 0);
        assertNotNull(response);
        assertEquals("A", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("seg2", "/A", 1);
        assertNotNull(response);
        assertEquals("B", response.getName());
        assertEquals(2, response.getNestingLevel());

        response = plugin.query("seg3", "/A/B", 2);
        assertNotNull(response);
        assertEquals("C", response.getName());
        assertEquals(3, response.getNestingLevel());

        response = plugin.query("seg3", "C", 3);
        assertNull(response);
    }

    private class MockPlugin extends Plugin {

        public MockPlugin() {
            super("dt", "dn");
        }

        public void setDescriptors(LoopDescriptor[] loops) {
            this.loops = loops;
        }

    }

}
