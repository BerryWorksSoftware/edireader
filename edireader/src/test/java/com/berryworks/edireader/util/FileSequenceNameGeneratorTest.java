package com.berryworks.edireader.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FileSequenceNameGeneratorTest {

    private FileSequenceNameGenerator generator;

    @Test
    public void basics() {
        generator = new FileSequenceNameGenerator("abc000def");
        assertEquals("abc000def", generator.getFilenamePattern());
        assertEquals("abc001def", generator.generateName());
        assertEquals("abc002def", generator.generateName());
        assertEquals("abc003def", generator.generateName());
        assertEquals("abc004def", generator.generateName());
    }

    @Test
    public void requiresSequenceNumber() {
        try {
            generator = new FileSequenceNameGenerator("abc");
            fail();
        } catch (RuntimeException e) {
            assertEquals("Filename pattern must include a sequence one or more '0's.", e.getMessage());
        }
    }
}
