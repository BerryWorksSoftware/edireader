package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIAttributes;
import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.tokenizer.EDITokenizer;
import com.berryworks.edireader.tokenizer.Token;
import com.berryworks.edireader.tokenizer.Tokenizer;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class ElementCoordinatesTest {

    private EDIReader ediReader;
    private ElementCoordinates coordinates;

    @Before
    public void setUp() throws EDISyntaxException, IOException {
        ediReader = EDIReaderFactory.createEDIReader(new StringReader("""
                MSH|^~\\&|REGISTRATION|GENERAL_HOSPITAL|EHR|GENERAL_HOSPITAL|20260905083000||ADT^A01^ADT_A01|MSG00001|P|2.7
                ZXX|A~B^C&D^E^~G|
                """));
        coordinates = new ElementCoordinates(ediReader);
    }

    @Test
    public void basics() throws SAXException, IOException {
        Tokenizer tokenizer = new EDITokenizer(new StringReader("""
                AIP||B|MICHAEL^Bennett^Michael T.^^^^^^&&NPI|
                """))
                .setDelimiter('|').setSubDelimiter('^').setSubSubDelimiter('&').setTerminator('\n');

        Token token = tokenizer.nextToken();
        coordinates.focus(token);
        assertEquals(Token.TokenType.SEGMENT_START, token.getType());
        assertEquals(0, coordinates.getIndex());
        assertEquals(0, coordinates.getSubIndex());
        assertEquals(0, coordinates.getSubSubIndex());

        // Element 1
        coordinates.focus(tokenizer.nextToken());
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

        coordinates.startSubElement(new EDIAttributes());
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
    public void pidSegment() throws SAXException, IOException {
        Tokenizer tokenizer = new EDITokenizer(new StringReader("""
                PID|||20084571^^^^PT~76432^^^^PI~20084571^^^^MR~20084571^^^^AN|76432|Martinez^Robert^^^Mr.||19620417|M||White|4217 N Maplewood^^Chicago^IL^60618||(773) 555-0147^PRN^PH|^WPN^PH|English|U||20084571||||Not Hispanic or Latino||||||||N||||||||||Home|
                """))
                .setDelimiter('|').setSubDelimiter('^').setSubSubDelimiter('&').setTerminator('\n');

        Token token;
        ElementCoordinates coordinates = new ElementCoordinates(ediReader);

        while ((token = tokenizer.nextToken()).getType() != Token.TokenType.END_OF_DATA) {
            coordinates.focus(token);
            System.out.println(coordinates);
        }

    }


    @Test
    public void cannotEndWithoutStart() throws SAXException, IOException {
        Tokenizer tokenizer = new EDITokenizer(new StringReader("SEG|one|twoA^twoB||^fourB|five"))
                .setDelimiter('|').setSubDelimiter('^').setSubSubDelimiter('&').setTerminator('\n');

        Token token = tokenizer.nextToken();
        coordinates.focus(token);
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

        // empty element
        token = tokenizer.nextToken();
        assertEquals("", token.getValue());
        coordinates.focus(token);
        try {
            coordinates.endElement();
            fail();
        } catch (Exception e) {
            assertEquals("Element not started", e.getMessage());
        }
        coordinates.startElement();
        coordinates.endElement();

        // fourB (with no fourA)
        token = tokenizer.nextToken();
        coordinates.focus(token);
        assertEquals(Token.TokenType.SUB_EMPTY, token.getType());
        assertEquals("", token.getValue());
        coordinates.focus(token);
        try {
            coordinates.endSubElement();
            fail();
        } catch (Exception e) {
            assertEquals("Sub-element not started", e.getMessage());
        }
        coordinates.startElement();
        coordinates.endElement();

        token = tokenizer.nextToken();
        coordinates.focus(token);
        assertEquals(Token.TokenType.SUB_ELEMENT, token.getType());
        assertEquals("fourB", token.getValue());
        coordinates.focus(token);
        try {
            coordinates.endSubElement();
            fail();
        } catch (Exception e) {
            assertEquals("Sub-element not started", e.getMessage());
        }

    }

    @Test
    public void cannotStartTwice() throws SAXException, IOException {
        Tokenizer tokenizer = new EDITokenizer(new StringReader("SEG|one|twoA^twoB||^fourB|five"))
                .setDelimiter('|').setSubDelimiter('^').setSubSubDelimiter('&').setTerminator('\n');

        Token token = tokenizer.nextToken();
        coordinates.focus(token);
        assertEquals("SEG", token.getValue());

        // one
        token = tokenizer.nextToken();
        assertEquals("one", token.getValue());
        coordinates.focus(token);
        coordinates.startElement();
        try {
            coordinates.startElement();
            fail();
        } catch (Exception e) {
            assertEquals("Element started twice", e.getMessage());
        }
        coordinates.endElement();

        // twoA
        token = tokenizer.nextToken();
        assertEquals("twoA", token.getValue());
        coordinates.focus(token);
        coordinates.startElement();
        coordinates.startSubElement(new EDIAttributes());
        try {
            coordinates.startSubElement(new EDIAttributes());
            fail();
        } catch (Exception e) {
            assertEquals("Sub-element started twice", e.getMessage());
        }
        coordinates.endSubElement();
        coordinates.endElement();

    }

}
