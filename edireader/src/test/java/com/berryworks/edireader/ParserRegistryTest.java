/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader;

import org.junit.Test;

import static org.junit.Assert.*;

public class ParserRegistryTest {

    @Test
    public void testInitialConditions() {
        assertNull(ParserRegistry.get("xyz"));

        assertTrue(ParserRegistry.get("ISA") instanceof AnsiReader);
        assertNull(ParserRegistry.get("IS"));
        assertNull(ParserRegistry.get("I"));

        assertTrue(ParserRegistry.get("UNA") instanceof EdifactReader);
        assertTrue(ParserRegistry.get("UNB") instanceof EdifactReader);
        assertNull(ParserRegistry.get("UN"));
        assertNull(ParserRegistry.get("U"));
    }

    @Test
    public void testCustomParser() {
        ParserRegistry.register("xy", "com.xxx.yyy");
        assertNull(ParserRegistry.get("xy"));

        EDIReader customParser = new ABCReader();
        ParserRegistry.register("ab", customParser.getClass().getName());
        EDIReader registeredParser = ParserRegistry.get("abc");
        assertNotNull(registeredParser);
        assertTrue(registeredParser instanceof ABCReader);
        assertEquals("com.berryworks.edireader.ABCReader", registeredParser.getClass().getName());
    }
    
    @Test
    public void testEdifactWithControl() {
        assertTrue(ParserRegistry.get("UNA") instanceof EdifactReaderWithCONTRL);
    }
    
}
