package com.berryworks.edireader.tokenizer;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDISyntaxException;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.berryworks.edireader.tokenizer.Token.TokenType.*;
import static org.junit.Assert.*;

public class Hl7EscapeTest {
    private Tokenizer tokenizer(String source) {
        return new EDITokenizer(new StringReader(source)).setDelimiter('|')
                .setSubDelimiter('^').setSubSubDelimiter('&').setRepetitionSeparator('~')
                .setRelease('\\').setTerminator('\r').setHl7Escaping(true);
    }

    @Test public void decodesDelimitersWithoutCreatingStructure() throws Exception {
        var t = tokenizer("SEG|\\F\\\\S\\\\R\\\\T\\\\E\\|A\\F\\B^\\S\\&\\T\\\r");
        assertEquals(SEGMENT_START, t.nextToken().getType());
        var value = t.nextToken();
        assertEquals(SIMPLE, value.getType());
        assertEquals("|^~&\\", value.getValue());
        value = t.nextToken();
        assertEquals(SUB_ELEMENT, value.getType());
        assertEquals("A|B", value.getValue());
        value = t.nextToken();
        assertEquals(SUB_SUB_ELEMENT, value.getType());
        assertEquals("^", value.getValue());
        value = t.nextToken();
        assertEquals(SUB_SUB_ELEMENT, value.getType());
        assertEquals("&", value.getValue());
    }

    @Test public void preservesOtherCompleteSequences() throws Exception {
        var t = tokenizer("SEG|\\H\\text\\N\\\\X4142\\\\Zlocal\\\\C2842\\\r");
        t.nextToken();
        assertEquals("\\H\\text\\N\\\\X4142\\\\Zlocal\\\\C2842\\", t.nextToken().getValue());
    }

    @Test public void detectsUnterminatedSequencesAtSegmentEndAndEof() throws Exception {
        for (String source : List.of("SEG|\\F\rNEXT|value\r", "SEG|abc\\F")) {
            var t = tokenizer(source);
            t.nextToken();
            var error = assertThrows(EDISyntaxException.class, t::nextToken);
            assertTrue(error.getMessage().contains("Unterminated HL7 escape"));
        }
    }

    @Test public void crossesBufferBoundaryAndCountsOriginalCharacters() throws Exception {
        String value = "a".repeat(994) + "\\F\\" + "tail";
        String source = "SEG|" + value + "\r";
        var t = tokenizer(source);
        t.nextToken();
        // Recording starts with the already-read field separator after SEG.
        t.setRecorder(true);
        assertEquals("a".repeat(994) + "|tail", t.nextToken().getValue());
        assertEquals(SEGMENT_END, t.nextToken().getType());
        assertEquals(source.length(), t.getCharCount());
        t.nextToken();
        assertEquals(source.substring(3), t.getRecording());
    }

    @Test public void parsesMshDeclarationAndCustomEscapesEndToEnd() throws Exception {
        for (boolean custom : List.of(false, true)) {
            String source = "MSH|^~\\&|APP\\F\\NAME||||202610111200||ADT^A04|1|P|2.2\rPID|\\R\\\r";
            if (custom) source = source.replace('|', '*').replace('^', '$').replace('~', '%')
                    .replace('\\', '!').replace('&', '@');
            List<String> values = new ArrayList<>();
            List<String> senders = new ArrayList<>();
            var parser = new EDIReader();
            parser.setContentHandler(new DefaultHandler() {
                @Override public void startElement(String uri, String name, String qName, org.xml.sax.Attributes attributes) {
                    if (attributes.getValue("ApplSender") != null) senders.add(attributes.getValue("ApplSender"));
                }
                @Override public void characters(char[] chars, int start, int length) {
                    values.add(new String(chars, start, length));
                }
            });
            parser.parse(new InputSource(new StringReader(source)));
            assertTrue(values.contains(custom ? "%" : "~"));
            assertTrue(senders.contains(custom ? "APP*NAME" : "APP|NAME"));
        }
    }
}
