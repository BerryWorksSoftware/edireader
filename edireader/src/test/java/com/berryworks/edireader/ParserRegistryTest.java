/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader;

import org.junit.Test;

import static org.junit.Assert.*;

public class ParserRegistryTest {

    @Test
    public void testInitialConditions() {
        assertNullOrJunkReader(ParserRegistry.get("xyz"));

        assertTrue(ParserRegistry.get("ISA") instanceof AnsiReader);
        assertNullOrJunkReader(ParserRegistry.get("IS"));
        assertNullOrJunkReader(ParserRegistry.get("I"));

        assertTrue(ParserRegistry.get("UNA") instanceof EdifactReader);
        assertTrue(ParserRegistry.get("UNB") instanceof EdifactReader);
        assertNullOrJunkReader(ParserRegistry.get("UN"));
        assertNullOrJunkReader(ParserRegistry.get("U"));
    }

    @Test
    public void testCustomParser() {
        ParserRegistry.register("xy", "com.xxx.yyy");
        assertNullOrJunkReader(ParserRegistry.get("xy"));

        EDIReader customParser = new ABCReader();
        ParserRegistry.register("ab", customParser.getClass().getName());
        EDIReader registeredParser = ParserRegistry.get("abc");
        assertNotNull(registeredParser);
        assertTrue(registeredParser instanceof ABCReader);
        assertEquals("com.berryworks.edireader.ABCReader", registeredParser.getClass().getName());
    }

    @Test
    public void testEmptyStringDefaultParser() {
        EDIReader catchAllParser = new JunkReader();
        ParserRegistry.register("", catchAllParser.getClass().getName());

        EDIReader customParser = new ABCReader();
        ParserRegistry.register("ab", customParser.getClass().getName());
        assertTrue(ParserRegistry.get("abc") instanceof ABCReader);

        assertTrue(ParserRegistry.get("UNA") instanceof EdifactReader);
        assertTrue(ParserRegistry.get("ISA") instanceof AnsiReader);

        EDIReader registeredParser = ParserRegistry.get("junk");
        assertNotNull(registeredParser);
        assertTrue(registeredParser instanceof JunkReader);
        assertEquals("com.berryworks.edireader.JunkReader", registeredParser.getClass().getName());
    }

    @Test
    public void testEdifactWithControl() {
        assertTrue(ParserRegistry.get("UNA") instanceof EdifactReaderWithCONTRL);
    }

    private void assertNullOrJunkReader(EDIReader parser) {
        if (parser == null) return;
        if (parser instanceof JunkReader) return;
        assertNull(parser);
    }

}
