/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.tokenizer;

import org.junit.Test;

import java.nio.CharBuffer;

import static org.junit.Assert.assertEquals;


public class TestCharBufferBasics {

    CharBuffer buffer;

    @Test
    public void testBasics() {

        char[] wrappedArray = new char[20];

        buffer = CharBuffer.wrap(wrappedArray);

        assertEquals(0, buffer.position());
        assertEquals(20, buffer.length());
        assertEquals(20, buffer.limit());
        assertEquals(20, buffer.capacity());

        buffer.put('a');
        assertEquals(1, buffer.position());
        assertEquals(19, buffer.length());
        assertEquals(20, buffer.limit());

        buffer.put(new char[]{'b', 'c'});
        assertEquals(3, buffer.position());
        assertEquals(17, buffer.length());
        assertEquals(20, buffer.limit());
    }

    @Test
    public void testPutThenGet() {

        char[] wrappedArray = new char[20];

        buffer = CharBuffer.wrap(wrappedArray);

        buffer.put(new char[]{'a', 'b', 'c', 'd'});
        assertEquals(4, buffer.position());
        assertEquals(16, buffer.length());
        assertEquals(20, buffer.limit());

        buffer.flip();
        assertEquals(0, buffer.position());
        assertEquals(4, buffer.length());
        assertEquals(4, buffer.limit());

        assertEquals('a', buffer.get());
        assertEquals(1, buffer.position());
        assertEquals(3, buffer.length());
        assertEquals(4, buffer.limit());

        char[] array = new char[]{'x', 'y'};
        buffer.get(array);
        assertEquals('b', array[0]);
        assertEquals('c', array[1]);
        assertEquals(3, buffer.position());
        assertEquals(1, buffer.length());
        assertEquals(4, buffer.limit());
    }

    @Test
    public void testClear() {

        char[] wrappedArray = new char[20];

        buffer = CharBuffer.wrap(wrappedArray);

        buffer.put(new char[]{'a', 'b', 'c', 'd'});
        assertEquals(4, buffer.position());
        assertEquals(16, buffer.length());
        assertEquals(20, buffer.limit());

        buffer.flip();
        assertEquals(0, buffer.position());
        assertEquals(4, buffer.length());
        assertEquals(4, buffer.limit());

        assertEquals('a', buffer.get());
        assertEquals(1, buffer.position());
        assertEquals(3, buffer.length());
        assertEquals(4, buffer.limit());

        buffer.clear();
        assertEquals(0, buffer.position());
        assertEquals(20, buffer.length());
        assertEquals(20, buffer.limit());
    }

    @Test
    public void testCompact() {

        char[] wrappedArray = new char[20];

        buffer = CharBuffer.wrap(wrappedArray);

        buffer.put(new char[]{'a', 'b', 'c', 'd'});
        assertEquals(4, buffer.position());
        assertEquals(16, buffer.length());
        assertEquals(20, buffer.limit());

        buffer.flip();
        assertEquals(0, buffer.position());
        assertEquals(4, buffer.length());
        assertEquals(4, buffer.limit());

        assertEquals('a', buffer.get());
        assertEquals(1, buffer.position());
        assertEquals(3, buffer.length());
        assertEquals(4, buffer.limit());

        buffer.compact();
        assertEquals(3, buffer.position());
        assertEquals(17, buffer.length());
        assertEquals(20, buffer.limit());

        buffer.put(new char[]{'e', 'f', 'g'});
        assertEquals(6, buffer.position());
        assertEquals(14, buffer.length());
        assertEquals(20, buffer.limit());

        buffer.flip();
        assertEquals(0, buffer.position());
        assertEquals(6, buffer.length());
        assertEquals(6, buffer.limit());
    }


}
