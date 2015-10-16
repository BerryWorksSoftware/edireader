/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 */
package com.berryworks.edireader.tokenizer;

import com.berryworks.edireader.EDISyntaxException;
import org.junit.Test;

import java.io.*;
import java.util.List;

import static org.junit.Assert.*;

public class TestEDITokenizer {

    private Tokenizer tokenizer;

    @Test
    public void testNextToken() throws Exception {

        tokenizer = new EDITokenizer(new StringReader(
                "abc-def-ghi!j--kl-mnop!q-123-123x!"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('!');
        tokenizer.setDelimiter('-');
        assertEquals(0, tokenizer.getCharCount());
        assertEquals(0, tokenizer.getSegmentCharCount());
        Token token;

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals("abc00", token.getElementId());
        assertEquals(0, token.getIndex());
        assertEquals("abc", token.getValue());
        assertEquals("abc", token.getSegmentType());
        assertEquals(4, tokenizer.getCharCount());
        assertEquals(4, tokenizer.getSegmentCharCount());
        assertTrue(tokenizer.hasMoreTokens());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //     ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("abc01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals("def", token.getValue());
        assertEquals("abc", token.getSegmentType());
        assertEquals(8, tokenizer.getCharCount());
        assertEquals(8, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //         ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("abc02", token.getElementId());
        assertEquals(2, token.getIndex());
        assertEquals("ghi", token.getValue());
        assertEquals("abc", token.getSegmentType());
        assertEquals(11, tokenizer.getCharCount());
        assertEquals(11, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //            ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals("abc", token.getSegmentType());
        assertEquals(12, tokenizer.getCharCount());
        assertEquals(12, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //             ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(0, token.getIndex());
        assertEquals("j00", token.getElementId());
        assertEquals("j", token.getValue());
        assertEquals("j", token.getSegmentType());
        assertEquals(14, tokenizer.getCharCount());
        assertEquals(2, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //              ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.EMPTY, token.getType());
        assertEquals("j01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals("j", token.getSegmentType());
        assertEquals(15, tokenizer.getCharCount());
        assertEquals(3, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //                ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("j02", token.getElementId());
        assertEquals(2, token.getIndex());
        assertEquals("kl", token.getValue());
        assertEquals("j", token.getSegmentType());
        assertEquals(18, tokenizer.getCharCount());
        assertEquals(6, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //                   ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("j03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("mnop", token.getValue());
        assertEquals("j", token.getSegmentType());
        assertEquals(22, tokenizer.getCharCount());
        assertEquals(10, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //                       ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals(3, token.getIndex());
        assertEquals("j", token.getSegmentType());
        assertEquals(23, tokenizer.getCharCount());
        assertEquals(11, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //                        ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals("q00", token.getElementId());
        assertEquals(0, token.getIndex());
        assertEquals("q", token.getValue());
        assertEquals("q", token.getSegmentType());
        assertEquals(25, tokenizer.getCharCount());
        assertEquals(2, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //                          ^
        int i = tokenizer.nextIntValue();
        assertEquals(i, 123);
        assertEquals(29, tokenizer.getCharCount());
        assertEquals(6, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //                              ^
        try {
            tokenizer.nextIntValue();
            fail("did not throw exception as expected for nextIntValue()");
        } catch (EDISyntaxException e) {
            assertEquals(33, tokenizer.getCharCount());
            assertEquals(10, tokenizer.getSegmentCharCount());
        }

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //                                  ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals("q", token.getSegmentType());
        assertEquals(34, tokenizer.getCharCount());
        assertEquals(11, tokenizer.getSegmentCharCount());
        assertFalse(tokenizer.hasMoreTokens());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        //                                   ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.END_OF_DATA, token.getType());
        assertEquals(36, tokenizer.getCharCount());
        assertFalse(tokenizer.hasMoreTokens());
    }

    @Test
    public void testSubElements() throws Exception {

        tokenizer = new EDITokenizer(new StringReader(
                "abc-def.ghij..k-l.m!abc-..n.o-p-q...-r...!"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('!');
        tokenizer.setDelimiter('-');
        tokenizer.setSubDelimiter('.');
        Token token;

        // abc-def.ghij..k-l.m! ...
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(0, token.getIndex());
        assertEquals(0, token.getIndex());
        assertEquals("abc", token.getValue());
        assertEquals(4, tokenizer.getCharCount());
        assertEquals(4, tokenizer.getSegmentCharCount());

        // abc-def.ghij..k-l.m! ...
        //     ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("abc01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertEquals("def", token.getValue());
        assertEquals("abc", token.getSegmentType());
        assertTrue(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(0, token.getSubIndex());
        assertEquals(8, tokenizer.getCharCount());
        assertEquals(8, tokenizer.getSegmentCharCount());

        // abc-def.ghij..k-l.m! ...
        //         ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("abc01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals(1, token.getSubIndex());
        assertEquals("ghij", token.getValue());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(13, tokenizer.getCharCount());
        assertEquals(13, tokenizer.getSegmentCharCount());

        // abc-def.ghij..k-l.m! ...
        //              ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(1, token.getIndex());
        assertEquals(2, token.getSubIndex());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(14, tokenizer.getCharCount());
        assertEquals(14, tokenizer.getSegmentCharCount());

        // abc-def.ghij..k-l.m! ...
        //               ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(1, token.getIndex());
        assertEquals(3, token.getSubIndex());
        assertEquals("k", token.getValue());
        assertFalse(token.isFirst());
        assertTrue(token.isLast());
        assertEquals(3, token.getSubIndex());
        assertEquals(16, tokenizer.getCharCount());
        assertEquals(16, tokenizer.getSegmentCharCount());

        // abc-def.ghij..k-l.m! ...
        //                 ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertEquals("l", token.getValue());
        assertTrue(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(0, token.getSubIndex());
        assertEquals(18, tokenizer.getCharCount());
        assertEquals(18, tokenizer.getSegmentCharCount());

        // ... -l.m!abc-..n.o-p-q...-r...!
        //        ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals(1, token.getSubIndex());
        assertEquals("m", token.getValue());
        assertFalse(token.isFirst());
        assertTrue(token.isLast());
        assertEquals(1, token.getSubIndex());
        assertEquals(19, tokenizer.getCharCount());
        assertEquals(19, tokenizer.getSegmentCharCount());

        // ... -l.m!abc-..n.o-p-q...-r...!
        //         ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals(20, tokenizer.getCharCount());
        assertEquals(20, tokenizer.getSegmentCharCount());

        // ... -l.m!abc-..n.o-p-q...-r...!
        //          ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(0, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertEquals("abc", token.getValue());
        assertEquals(24, tokenizer.getCharCount());
        assertEquals(4, tokenizer.getSegmentCharCount());

        // ... -l.m!abc-..n.o-p-q...-r...!
        //              ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(1, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertTrue(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(25, tokenizer.getCharCount());
        assertEquals(5, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //          ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(1, token.getIndex());
        assertEquals(1, token.getSubIndex());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(26, tokenizer.getCharCount());
        assertEquals(6, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //           ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(1, token.getIndex());
        assertEquals(2, token.getSubIndex());
        assertEquals("n", token.getValue());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(2, token.getSubIndex());
        assertEquals(28, tokenizer.getCharCount());
        assertEquals(8, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //             ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(1, token.getIndex());
        assertEquals(3, token.getSubIndex());
        assertEquals("o", token.getValue());
        assertFalse(token.isFirst());
        assertTrue(token.isLast());
        assertEquals(3, token.getSubIndex());
        assertEquals(30, tokenizer.getCharCount());
        assertEquals(10, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //               ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertEquals("p", token.getValue());
        assertEquals(32, tokenizer.getCharCount());
        assertEquals(12, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                 ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(3, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertEquals("q", token.getValue());
        assertTrue(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(0, token.getSubIndex());
        assertEquals(34, tokenizer.getCharCount());
        assertEquals(14, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                  ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(3, token.getIndex());
        assertEquals(1, token.getSubIndex());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(35, tokenizer.getCharCount());
        assertEquals(15, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                   ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(3, token.getIndex());
        assertEquals(2, token.getSubIndex());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(36, tokenizer.getCharCount());
        assertEquals(16, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                    ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(3, token.getIndex());
        assertEquals(3, token.getSubIndex());
        assertFalse(token.isFirst());
        assertTrue(token.isLast());
        assertEquals(37, tokenizer.getCharCount());
        assertEquals(17, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                      ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(4, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertEquals("r", token.getValue());
        assertTrue(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(0, token.getSubIndex());
        assertEquals(39, tokenizer.getCharCount());
        assertEquals(19, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                       ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(4, token.getIndex());
        assertEquals(1, token.getSubIndex());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(40, tokenizer.getCharCount());
        assertEquals(20, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                        ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(4, token.getIndex());
        assertEquals(2, token.getSubIndex());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());
        assertEquals(41, tokenizer.getCharCount());
        assertEquals(21, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                         ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(4, token.getIndex());
        assertEquals(3, token.getSubIndex());
        assertFalse(token.isFirst());
        assertTrue(token.isLast());
        assertEquals(41, tokenizer.getCharCount());
        assertEquals(21, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                          ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals(42, tokenizer.getCharCount());
        assertEquals(22, tokenizer.getSegmentCharCount());

        // ... abc-..n.o-p-q...-r...!
        //                           ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.END_OF_DATA, token.getType());
        assertEquals(43, tokenizer.getCharCount());
    }

    @Test
    public void testNextSimpleValue() throws Exception {

        tokenizer = new EDITokenizer(new StringReader("SEG|S1||S2||S2a||S3a^S3b|S4$"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('$');
        tokenizer.setDelimiter('|');
        tokenizer.setSubDelimiter('^');
        Token token;
        String simpleValue;

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(0, token.getIndex());
        assertEquals("SEG", token.getValue());

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        assertNotNull(simpleValue = tokenizer.nextSimpleValue(true));
        assertEquals("S1", simpleValue);

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        try {
            tokenizer.nextSimpleValue(true);
            fail("Excepcted a syntax exception to be thrown");
        } catch (EDISyntaxException e) {
            // ignore
        }

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        assertNotNull(simpleValue = tokenizer.nextSimpleValue(false));
        assertEquals("S2", simpleValue);

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        assertNotNull(simpleValue = tokenizer.nextSimpleValue(false));
        assertEquals("", simpleValue);

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        assertNotNull(simpleValue = tokenizer.nextSimpleValue());
        assertEquals("S2a", simpleValue);

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        try {
            tokenizer.nextSimpleValue();
            fail("Excepcted a syntax exception to be thrown");
        } catch (EDISyntaxException e) {
            // ignore
        }

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        try {
            tokenizer.nextSimpleValue(true);
            fail("Excepcted a syntax exception to be thrown");
        } catch (EDISyntaxException e) {
            // ignore
        }
        tokenizer.nextToken();

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        assertNotNull(simpleValue = tokenizer.nextSimpleValue());
        assertEquals("S4", simpleValue);

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        try {
            tokenizer.nextSimpleValue(true);
            fail("Excepcted a syntax exception to be thrown");
        } catch (EDISyntaxException e) {
            // ignore
        }

        // SEG|S1||S2||S2a||S3a^S3b|S4$
        // ^
        try {
            tokenizer.nextSimpleValue(true);
            fail("Excepcted a syntax exception to be thrown");
        } catch (EDISyntaxException e) {
            // ignore
        }

    }

    @Test
    public void testNextCompositeElements() throws Exception {

        tokenizer = new EDITokenizer(new StringReader(
                "UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER ID:ZZ+970101:1050+00000000000916++ORDERS\'"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('\'');
        tokenizer.setDelimiter('+');
        tokenizer.setSubDelimiter(':');
        Token token;
        List<String> composite;

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(0, token.getIndex());
        assertEquals("UNB", token.getValue());

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // ---- -
        assertNotNull(composite = tokenizer.nextCompositeElement());
        assertEquals(2, composite.size());
        assertEquals("UNOB", composite.get(0));
        assertEquals("1", composite.get(1));

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // --------- -- -------
        assertNotNull(composite = tokenizer.nextCompositeElement());
        assertEquals(3, composite.size());
        assertEquals("003897733", composite.get(0));
        assertEquals("01", composite.get(1));
        assertEquals("MFGB-PO", composite.get(2));

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // ---------- --
        assertNotNull(composite = tokenizer.nextCompositeElement());
        assertEquals(2, composite.size());
        assertEquals("PARTNER ID", composite.get(0));
        assertEquals("ZZ", composite.get(1));

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // ------ ----
        assertNotNull(composite = tokenizer.nextCompositeElement());
        assertEquals(2, composite.size());
        assertEquals("970101", composite.get(0));
        assertEquals("1050", composite.get(1));

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals(5, token.getIndex());
        assertEquals("00000000000916", token.getValue());

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.EMPTY, token.getType());
        assertEquals(6, token.getIndex());

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals(7, token.getIndex());
        assertEquals("ORDERS", token.getValue());

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());

        // UNB+UNOB:1+003897733:01:MFGB-PO+PARTNER
        // ID:ZZ+970101:1050+00000000000916++ORDERS'
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.END_OF_DATA, token.getType());

        // Now try a different style

        tokenizer = new EDITokenizer(new StringReader("SEG|C1^^|C2^^|C3^^|C4||C5^\n"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('\n');
        tokenizer.setDelimiter('|');
        tokenizer.setSubDelimiter('^');
        tokenizer.setRepetitionSeparator('~');

        // SEG|C1^^|C2^^|C3^^|C4||C5^\n
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(0, token.getIndex());
        assertEquals("SEG", token.getValue());
        assertEquals("SEG00", token.getElementId());
        assertEquals("SEG", token.getSegmentType());

        // SEG|C1^^|C2^^|C3^^|C4||C5^\n
        // ^..
        composite = tokenizer.nextCompositeElement();
        assertNotNull(composite);
        assertEquals(3, composite.size());
        assertEquals("C1", composite.get(0));
        assertEquals(0, composite.get(1).length());
        assertEquals(0, composite.get(2).length());

        // SEG|C1^^|C2^^|C3^^|C4||C5^\n
        // ^..
        composite = tokenizer.nextCompositeElement();
        assertNotNull(composite);
        assertEquals(3, composite.size());
        assertEquals("C2", composite.get(0));
        assertEquals(0, composite.get(1).length());
        assertEquals(0, composite.get(2).length());

        // SEG|C1^^|C2^^|C3^^|C4||C5^\n
        // ^..
        composite = tokenizer.nextCompositeElement();
        assertNotNull(composite);
        assertEquals(3, composite.size());
        assertEquals("C3", composite.get(0));
        assertEquals(0, composite.get(1).length());
        assertEquals(0, composite.get(2).length());

        // SEG|C1^^|C2^^|C3^^|C4||C5^\n
        // ^
        composite = tokenizer.nextCompositeElement();
        assertNotNull(composite);
        assertEquals(1, composite.size());
        assertEquals("C4", composite.get(0));

        // SEG|C1^^|C2^^|C3^^|C4||C5^\n
        // ^
        composite = tokenizer.nextCompositeElement();
        assertNotNull(composite);
        assertEquals(0, composite.size());

        // SEG|C1^^|C2^^|C3^^|C4||C5^\n
        // ^
        composite = tokenizer.nextCompositeElement();
        assertNotNull(composite);
        assertEquals(2, composite.size());
        assertEquals("C5", composite.get(0));
        assertEquals(0, composite.get(1).length());

        // SEG|C1^^|C2^^|C3^^|C4||C5^\n
        // ^.
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());

        // SEG|C1^^|C2^^|C3^^|C4||C5^\n
        // ^
        assertNotNull(token = tokenizer.nextToken());
        assertEquals(Token.TokenType.END_OF_DATA, token.getType());
    }

    /**
     * Test the use of a release character. The term "release character" here
     * refers specifically to a single character that hides nay special meaning
     * of the character immediately follow. This is the sytle used in EDIFACT.
     *
     * @throws Exception DOCUMENT ME!
     */
    @Test
    public void testReleaseCharacter() throws Exception {

        tokenizer = new EDITokenizer(new StringReader("abc-def=-ghi!j-=-kl-=!!q-123!"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('!');
        tokenizer.setRelease('=');
        tokenizer.setDelimiter('-');
        Token token;

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(token.getElementId(), "abc00");
        assertEquals(token.getIndex(), 0);
        assertEquals(token.getValue(), "abc");
        assertEquals(token.getSegmentType(), "abc");

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^......
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("abc01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals("def-ghi", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals(token.getSegmentType(), "abc");

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(token.getIndex(), 0);
        assertEquals("j00", token.getElementId());
        assertEquals("j", token.getValue());
        assertEquals("j", token.getSegmentType());

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^....
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("j01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals("-kl", token.getValue());
        assertEquals("j", token.getSegmentType());

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^.
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("j02", token.getElementId());
        assertEquals(2, token.getIndex());
        assertEquals("!", token.getValue());
        assertEquals("j", token.getSegmentType());

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals(token.getSegmentType(), "j");

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals("q00", token.getElementId());
        assertEquals(token.getIndex(), 0);
        assertEquals(token.getValue(), "q");
        assertEquals(token.getSegmentType(), "q");

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^
        int i = tokenizer.nextIntValue();
        assertEquals(i, 123);

        // abc-def=-ghi!j-=-kl-=!!q-123!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals(token.getSegmentType(), "q");

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.END_OF_DATA, token.getType());
    }

    /**
     * Similar to release characters, escape sequences allow an otherwise
     * special character to appear as normal data. Unlike release characters,
     * escape sequences begin and end with a special character, known as an
     * escape character.
     *
     * @throws Exception Description of the Exception
     */
    @Test
    public void testEscapeSequences() throws Exception {

        tokenizer = new EDITokenizer(new StringReader("abc-def=F=ghi!"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('!');
        tokenizer.setDelimiter('-');
        Token token;

        // abc-def=F=ghi!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(token.getElementId(), "abc00");
        assertEquals(token.getIndex(), 0);
        assertEquals(token.getValue(), "abc");
        assertEquals(token.getSegmentType(), "abc");

        // abc-def=F=ghi!
        // ^.......
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("abc01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals("def=F=ghi", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def=F=ghi!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals("abc", token.getSegmentType());

        // abc-def=F=ghi!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.END_OF_DATA, token.getType());

    }

    @Test
    public void testRepeatSeparator() throws Exception {

        tokenizer = new EDITokenizer(new StringReader("abc-def*ghi-j*j2*j3-k*l*m1:m2*:n2*:o2-p:*q!"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('!');
        tokenizer.setRelease('=');
        tokenizer.setRepetitionSeparator('*');
        tokenizer.setDelimiter('-');
        tokenizer.setSubDelimiter(':');
        Token token;

        // abc-def*ghi-j*j2*j3-k*l*m1:m2*:n2*:o2!
        // ^..
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals("abc00", token.getElementId());
        assertEquals(0, token.getIndex());
        assertEquals("abc", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j*j2*j3-k*l*m1:m2*:n2*:o2!
        //     ^..
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("abc01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals("def", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j*j2*j3-k*l*m1:m2*:n2*:o2!
        //         ^..
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals(1, token.getIndex());
        assertEquals("abc01", token.getElementId());
        assertEquals("ghi", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j*j2*j3-k*l*m1:m2*:n2*:o2!
        //             ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals("abc02", token.getElementId());
        assertEquals("j", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def~ghi-j*j2*j3-...
        //               ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals("abc02", token.getElementId());
        assertEquals("j2", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def~ghi-j*j2*j3-...
        //                  ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals("abc02", token.getElementId());
        assertEquals("j3", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j*j2*j3-k*l*m1:m2*:n2*:o2!
        //                     ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("abc03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("k", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j*j2*j3-k*l*m1:m2*:n2*:o2!
        //                       ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("abc03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("l", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*n1:n2!
        //                   ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("abc03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertEquals("m1", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*...
        //                      ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("abc03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals(1, token.getSubIndex());
        assertEquals("m2", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*:n2*:o2!
        //                         ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals("abc03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*:n2*:o2!
        //                          ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("abc03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("n2", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*:n2*:o2!
        //                            ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals("abc03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*:n2*:o2!
        //                              ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("abc03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("o2", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*:n2*:o2-p:*q!
        //                                 ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("abc04", token.getElementId());
        assertEquals(4, token.getIndex());
        assertEquals("p", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*:n2*:o2-p:*q!
        //                                  ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals("abc04", token.getElementId());
        assertEquals(4, token.getIndex());
        assertEquals("", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*:n2*:o2-p:*q!
        //                                    ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("abc04", token.getElementId());
        assertEquals(4, token.getIndex());
        assertEquals("q", token.getValue());
        assertEquals("abc", token.getSegmentType());

        // abc-def*ghi-j-k*l*m1:m2*:n2*:o2-p:*q!
        //                                     ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());
        assertEquals(token.getSegmentType(), "abc");

        // abc-def*ghi-j-k*l*m1:m2*:n2*:o2!
        //                                ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.END_OF_DATA, token.getType());

    }

    @Test
    public void testRepetitionSpecialCases() throws Exception {

        tokenizer = new EDITokenizer(new StringReader("PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|\n"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('\n');
        tokenizer.setRelease('=');
        tokenizer.setRepetitionSeparator('~');
        tokenizer.setDelimiter('|');
        tokenizer.setSubDelimiter('^');
        Token token;

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals("PID00", token.getElementId());
        assertEquals(0, token.getIndex());
        assertEquals("PID", token.getValue());
        assertEquals("PID", token.getSegmentType());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //    ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("PID01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals("1", token.getValue());
        assertEquals("PID", token.getSegmentType());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //           ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals("PID02", token.getElementId());
        assertEquals("HNE9133073356", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertTrue(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                   ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals("PID02", token.getElementId());
        assertEquals("", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                    ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals("PID02", token.getElementId());
        assertEquals("", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                      ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(2, token.getIndex());
        assertEquals("PID02", token.getElementId());
        assertEquals("NE", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                         ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("PID02", token.getElementId());
        assertEquals(2, token.getIndex());
        assertEquals("PE", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertTrue(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                            ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.EMPTY, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertEquals("", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertTrue(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                 ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals(0, token.getSubIndex());
        assertEquals("000000007", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertTrue(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                      ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals(1, token.getSubIndex());
        assertEquals("", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                       ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals(2, token.getSubIndex());
        assertEquals("", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                           ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("ST01K", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                               ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("MR", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertTrue(token.isLast());


        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                                     ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("00456789", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertTrue(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                                          ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                                           ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                                              ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("ST01", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertFalse(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                                                  ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("PID03", token.getElementId());
        assertEquals(3, token.getIndex());
        assertEquals("PI", token.getValue());
        assertEquals("PID", token.getSegmentType());
        assertFalse(token.isFirst());
        assertTrue(token.isLast());

        //PID|1|HNE9133073356^^^NE^PE|~000000007^^^ST01K^MR~00456789^^^ST01^PI|
        //                                                                    ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());

    }

    @Test
    public void testRecorder() throws EDISyntaxException, IOException {

        tokenizer = new EDITokenizer(new StringReader("abc-def-ghi!j--kl-mnop!q-123-123x!"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('!');
        tokenizer.setDelimiter('-');
        assertEquals(0, tokenizer.getCharCount());
        assertEquals(0, tokenizer.getSegmentCharCount());
        Token token;

        // At this point, there should be nothing recorded.
        assertEquals(0, tokenizer.getRecording().length());
        tokenizer.setRecorder(true);

        // Now let the tokenizer see some data.
        //
        // abc-def-ghi!j--kl-mnop!q-123-123x!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(token.getElementId(), "abc00");
        assertEquals(token.getIndex(), 0);
        assertEquals(token.getValue(), "abc");
        assertEquals(token.getSegmentType(), "abc");
        assertEquals(4, tokenizer.getCharCount());
        assertEquals(4, tokenizer.getSegmentCharCount());

        // abc-def-ghi!j--kl-mnop!q-123-123x!
        // ^
        token = tokenizer.nextToken();
        assertNotNull(token);
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("abc01", token.getElementId());
        assertEquals(1, token.getIndex());
        assertEquals("def", token.getValue());
        assertEquals("abc", token.getSegmentType());
        assertEquals(8, tokenizer.getCharCount());
        assertEquals(8, tokenizer.getSegmentCharCount());

        assertEquals(8, tokenizer.getRecording().length());
        // assertEquals("abc-def", tokenizer.getRecording());
    }

    @Test
    public void testElementTooLong() throws Exception {

        tokenizer = new EDITokenizer(new StringReader(
                "abcdefghiiiiiiiiiiiiiiiiiiiii!jklmnop-q!"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('!');
        tokenizer.setDelimiter('-');

        try {
            tokenizer.nextToken();
        } catch (EDISyntaxException e) {
            assertEquals(1, e.getErrorSegmentNumber());
            assertEquals(1, e.getErrorElementNumber());
        }
    }

    @Test
    public void testToString() throws Exception {

        tokenizer = new EDITokenizer(new StringReader(
                "abc"));

        tokenizer.nextToken();
        assertEquals("tokenizer state: segmentCount=1 charCount=4 segTokenCount=1 segCharCount=4 currentToken=Token type=SEGMENT_START value=abc index=0 segment=abc buffer.limit=0 buffer.position=0",
                tokenizer.toString());
    }

    @Test
    public void testNextSegment() throws Exception {

        tokenizer = new EDITokenizer(new StringReader(
                "a-b-c!d-e-f-g-h-i-j-k-l-m-n-o-p-q-r-s-t-u-v-w-x-y-z-1-2-3-4-5-6-7-8-9-0!"));
        assertNotNull(tokenizer);
        tokenizer.setTerminator('!');
        tokenizer.setDelimiter('-');

        tokenizer.nextToken();
        String segType = tokenizer.nextSegment();
        assertEquals("d", segType);

        // Try a potential "runaway" condition where the
        // end of segment is not found after some number
        // of elements.
        try {
            tokenizer.nextSegment();
            fail("failed to detect too many elements");
        } catch (EDISyntaxException e) {
            assertEquals(2, e.getErrorSegmentNumber());
            assertEquals(32, e.getErrorElementNumber());
        }
    }

    @Test
    public void testGetBufferred() throws Exception {

        tokenizer = new EDITokenizer(new StringReader("abcdefghijklmnopqrstuvwxyz"));

        char[] returnValue;
        returnValue = tokenizer.getBuffered();
        // Nothing has been read, so nothing is buffered
        assertEquals(0, returnValue.length);

        // Look ahead at the next 10 chars
        char[] lookahead = tokenizer.lookahead(10);
        assertEquals(10, lookahead.length);
        assertEquals('a', lookahead[0]);
        assertEquals('j', lookahead[9]);

        // Now the entire 26 letter sequence has been buffered
        returnValue = tokenizer.getBuffered();
        assertEquals(26, returnValue.length);
        assertEquals('a', returnValue[0]);

        // Consume a few chars of the input
        char[] consumed = tokenizer.getChars(5);
        assertEquals(5, consumed.length);
        assertEquals('a', consumed[0]);
        assertEquals('e', consumed[consumed.length - 1]);

        // Those first few chars are no longer buffered
        returnValue = tokenizer.getBuffered();
        assertEquals(21, returnValue.length);
        assertEquals('f', returnValue[0]);

        // Consume a few more ...
        consumed = tokenizer.getChars(7);
        assertEquals(7, consumed.length);
        assertEquals('f', consumed[0]);
        assertEquals('l', consumed[consumed.length - 1]);
        returnValue = tokenizer.getBuffered();
        assertEquals(14, returnValue.length);
        assertEquals('m', returnValue[0]);

        // Consume all the rest
        consumed = tokenizer.getChars(14);
        assertEquals(14, consumed.length);
        assertEquals('m', consumed[0]);
        assertEquals('z', consumed[consumed.length - 1]);
        returnValue = tokenizer.getBuffered();
        assertEquals(0, returnValue.length);
        // We've read all the data, but have not actually hit the eof
        assertFalse(tokenizer.isEndOfData());

        // Unget a char, then get it again
        tokenizer.ungetChar();
        returnValue = tokenizer.getBuffered();
        assertEquals(1, returnValue.length);
        assertEquals('z', returnValue[0]);
        char[] chars = tokenizer.getChars(1);
        assertEquals(1, chars.length);
        assertEquals('z', chars[0]);
        returnValue = tokenizer.getBuffered();
        assertEquals(0, returnValue.length);
        assertFalse(tokenizer.isEndOfData());

        // Try to get another
        tokenizer.getChar();
        // Now we have hit the end
        assertTrue(tokenizer.isEndOfData());
        returnValue = tokenizer.getBuffered();
        assertEquals(0, returnValue.length);

        // Once end of data has become true, it remains true, and
        // ungetChar no longer influences what getBuffered() returns. and
        // and getChar(n) throws an exception
        tokenizer.ungetChar();
        assertTrue(tokenizer.isEndOfData());
        returnValue = tokenizer.getBuffered();
        assertEquals(0, returnValue.length);

        try {
            tokenizer.getChars(1);
            fail("");
        } catch (EDISyntaxException ignore) {

        }

    }

    @Test
    public void testLookahead() throws IOException, EDISyntaxException {
        final String str = "abcdefghijklmnopqrstuvwxyz";
        EDITokenizer tokenizer = new EDITokenizer(new StringReader(str));

        char[] returnValue;
        returnValue = tokenizer.getBuffered();
        // Nothing has been read, so nothing is buffered
        assertEquals(0, returnValue.length);

        char[] lookahead;

        lookahead = tokenizer.lookahead(1);
        assertEquals(1, lookahead.length);
        assertEquals('a', lookahead[0]);

        lookahead = tokenizer.lookahead(2);
        assertEquals(2, lookahead.length);
        assertEquals('a', lookahead[0]);
        assertEquals('b', lookahead[1]);

        lookahead = tokenizer.lookahead(4);
        assertEquals(4, lookahead.length);
        assertEquals('a', lookahead[0]);
        assertEquals('b', lookahead[1]);
        assertEquals('c', lookahead[2]);
        assertEquals('d', lookahead[3]);

        lookahead = tokenizer.lookahead(26);
        assertEquals(26, lookahead.length);
        assertEquals('a', lookahead[0]);
        assertEquals('b', lookahead[1]);
        assertEquals('c', lookahead[2]);
        assertEquals('d', lookahead[3]);
        assertEquals('z', lookahead[25]);

        // Now consume a char of the input
        char[] consumed = tokenizer.getChars(1);
        assertEquals(1, consumed.length);
        assertEquals('a', consumed[0]);

        // Notice that if you use lookahead to view beyond
        // the end of data, you see '?' chars.
        lookahead = tokenizer.lookahead(27);
        assertEquals(27, lookahead.length);
        assertEquals('b', lookahead[0]);
        assertEquals('c', lookahead[1]);
        assertEquals('d', lookahead[2]);
        assertEquals('z', lookahead[24]);
        assertEquals('?', lookahead[25]);
        assertEquals('?', lookahead[26]);

        // Look ahead at the next 100 chars, even though fewer than 100 actually exist
        lookahead = tokenizer.lookahead(100);
        assertEquals(100, lookahead.length);//We should get an array of that size anyway, padded with '?'s
        assertEquals(str.substring(1), new String(lookahead, 0, str.length() - 1));
        assertEquals('b', lookahead[0]);
        assertEquals('c', lookahead[1]);
        assertEquals('?', lookahead[str.length() - 1]);
        assertEquals('?', lookahead[str.length()]);
        assertEquals('?', lookahead[99]);

        returnValue = tokenizer.getBuffered();
        // Now the entire string has been buffered
        assertEquals(str.length() -1, returnValue.length);
        assertEquals('b', returnValue[0]);
        assertEquals('z', returnValue[str.length() - 2]);

        // lookahead should produce exactly same result as above
        lookahead = tokenizer.lookahead(100);
        assertEquals(100, lookahead.length);
        assertEquals(str.substring(1), new String(lookahead, 0, str.length() - 1));
        assertEquals('b', lookahead[0]);
        assertEquals('c', lookahead[1]);
        assertEquals('?', lookahead[str.length() - 1]);
        assertEquals('?', lookahead[str.length()]);
        assertEquals('?', lookahead[99]);
    }

    @Test
    public void testPiped() throws Exception {
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader();
        reader.connect(writer);
        String testData = "Hello, World";
        Emitter emitter = new Emitter(writer, testData);
        Thread emitterThread = new Thread(emitter);
        emitterThread.start();
        char[] buf = new char[testData.length()];
        int n = reader.read(buf);
        assertEquals(testData.length(), n);
    }

    class Emitter implements Runnable {
        final Writer writer;
        final String testData;

        public Emitter(Writer writer, String testData) {
            this.writer = writer;
            this.testData = testData;
        }

        public void run() {
            try {
                writer.write(testData);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                System.err.println("Emitter.run() threw: " + e);
            }
        }
    }

}
