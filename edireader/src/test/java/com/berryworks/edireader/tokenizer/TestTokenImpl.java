/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.tokenizer;


import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.*;

public class TestTokenImpl {

    Token token;

    @Before
    public void setUp() {
        token = new TokenImpl(new EDITokenizer(new StringReader("")));
    }

    @Test
    public void testAppend() {
        assertEquals(0, token.getIndex());

        token.append('a');
        assertEquals(0, token.getIndex());
        assertEquals('a', token.getValueChars()[0]);
        assertEquals(1, token.getValueLength());
        assertEquals("a", token.getValue());

        token.append('b');
        assertEquals(0, token.getIndex());
        assertEquals('a', token.getValueChars()[0]);
        assertEquals('b', token.getValueChars()[1]);
        assertEquals(2, token.getValueLength());
        assertEquals("ab", token.getValue());
    }

    @Test
    public void testReset() {
        token.append('a');
        token.append('b');
        assertEquals(2, token.getValueLength());
        assertEquals("ab", token.getValue());

        token.resetValue();
        assertEquals("", token.getValue());
        assertEquals(0, token.getValueLength());
    }

    @Test
    public void testLargerValue() {
        token.append('a');
        token.append('b');
        token.append('c');
        token.append('d');
        token.append('e');
        token.append('f');
        token.append('g');
        token.append('h');
        token.append('i');
        token.append('j');
        token.append('k');
        token.append('l');
        assertEquals("abcdefghijkl", token.getValue());
        assertEquals(12, token.getValueLength());
    }

    @Test
    public void testContainsNonSpace() {
        assertFalse(token.containsNonSpace());

        token.append(' ');
        assertFalse(token.containsNonSpace());

        token.append(' ');
        assertFalse(token.containsNonSpace());

        token.append('x');
        assertTrue(token.containsNonSpace());

        token.resetValue();
        assertFalse(token.containsNonSpace());

        token.append('1');
        assertTrue(token.containsNonSpace());
    }
}
