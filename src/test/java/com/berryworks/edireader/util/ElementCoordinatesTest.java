package com.berryworks.edireader.util;

import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.tokenizer.EDITokenizer;
import com.berryworks.edireader.tokenizer.Token;
import com.berryworks.edireader.tokenizer.Tokenizer;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class ElementCoordinatesTest {

    public static final String SEGMENT = """
            AIP||B|MICHAEL^Bennett^Michael T.^^^^^^&&NPI|
            """;

    @Test
    public void basics() throws EDISyntaxException, IOException {
        Tokenizer tokenizer = new EDITokenizer(new StringReader(SEGMENT))
                .setDelimiter('|').setSubDelimiter('^').setSubSubDelimiter('&').setTerminator('\n');

        Token token = tokenizer.nextToken();
        ElementCoordinates coordinates = new ElementCoordinates(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(0, coordinates.getIndex());
        assertEquals(0, coordinates.getSubIndex());
        assertEquals(0, coordinates.getSubSubIndex());

        // Element 1
        coordinates = new ElementCoordinates(tokenizer.nextToken());
        assertEquals(Token.TokenType.EMPTY, token.getType());
        assertEquals(1, coordinates.getIndex());
        assertEquals(0, coordinates.getSubIndex());
        assertEquals(0, coordinates.getSubSubIndex());

        // Element 2
        coordinates.focus(tokenizer.nextToken());
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals(2, coordinates.getIndex());
        assertEquals(0, coordinates.getSubIndex());
        assertEquals(0, coordinates.getSubSubIndex());

        assertFalse(coordinates.isElementStarted());
        assertFalse(coordinates.isElementEnded());

        coordinates.startElement();
        assertTrue(coordinates.isElementStarted());
        assertFalse(coordinates.isElementEnded());

        coordinates.endElement();
        assertTrue(coordinates.isElementStarted());
        assertTrue(coordinates.isElementEnded());


        // Element 3, Sub-element 0
        coordinates.focus(tokenizer.nextToken());
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals(3, coordinates.getIndex());
        assertEquals(0, coordinates.getSubIndex());
        assertEquals(0, coordinates.getSubSubIndex());
        assertFalse(coordinates.isElementStarted());
        assertFalse(coordinates.isElementEnded());
        assertFalse(coordinates.isSubElementStarted());
        assertFalse(coordinates.isSubElementEnded());

        coordinates.startElement();
        assertTrue(coordinates.isElementStarted());
        assertFalse(coordinates.isElementEnded());
        assertFalse(coordinates.isSubElementStarted());
        assertFalse(coordinates.isSubElementEnded());

        coordinates.startSubElement();
        assertTrue(coordinates.isSubElementStarted());
        assertFalse(coordinates.isSubElementEnded());

        coordinates.endSubElement();
        assertTrue(coordinates.isSubElementStarted());
        assertTrue(coordinates.isSubElementEnded());

        coordinates.endElement();
        assertTrue(coordinates.isElementStarted());
        assertTrue(coordinates.isElementEnded());
    }

    @Test
    public void cannotEndWithoutStart() throws EDISyntaxException, IOException {
        Tokenizer tokenizer = new EDITokenizer(new StringReader("SEG|one|twoA^twoB||^fourB|five"))
                .setDelimiter('|').setSubDelimiter('^').setSubSubDelimiter('&').setTerminator('\n');

        Token token = tokenizer.nextToken();
        ElementCoordinates coordinates = new ElementCoordinates(token);
        assertEquals("SEG", token.getValue());

        // one
        token = tokenizer.nextToken();
        assertEquals("one", token.getValue());
        coordinates.focus(token);
        try {
            coordinates.endElement();
            fail();
        } catch (Exception e) {
            assertEquals("Element not started", e.getMessage());
        }
        coordinates.startElement();
        coordinates.endElement();

        // twoA
        token = tokenizer.nextToken();
        assertEquals("twoA", token.getValue());
        coordinates.focus(token);
        try {
            coordinates.endSubElement();
            fail();
        } catch (Exception e) {
            assertEquals("Sub-element not started", e.getMessage());
        }
        coordinates.startElement();
        coordinates.endElement();

        // twoB
        token = tokenizer.nextToken();
        assertEquals("twoB", token.getValue());
        coordinates.focus(token);
        try {
            coordinates.endSubElement();
            fail();
        } catch (Exception e) {
            assertEquals("Sub-element not started", e.getMessage());
        }
        coordinates.startElement();
        coordinates.endElement();

    }
}
