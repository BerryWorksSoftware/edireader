package com.berryworks.edireader;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class SyntaxDetectionTest {

    EDIReader ediReader;

    @Test
    public void x12_chars() throws SAXException, IOException {
        assertSyntaxDetails(X12_FRAGMENT_A, '~', '<', (char) 0, ' ', 0, '$', "\n");
        assertSyntaxDetails(X12_FRAGMENT_B, '+', '^', (char) 0, ' ', 0, '!', "\n");
    }

    @Test
    public void x12_bytes() throws SAXException, IOException {
        assertSyntaxDetails(X12_FRAGMENT_A.getBytes(StandardCharsets.ISO_8859_1), '~', '<', (char) 0, ' ', 0, '$', "\n");
        assertSyntaxDetails(X12_FRAGMENT_B.getBytes(StandardCharsets.ISO_8859_1), '+', '^', (char) 0, ' ', 0, '!', "\n");
    }

    @Test
    public void edifact() throws SAXException, IOException {
        assertSyntaxDetails(EDIFACT_UNA_FRAGMENT_A, '+', ':', '.', '?', 0, '\'', "\n");
        assertSyntaxDetails(EDIFACT_UNA_FRAGMENT_B, '*', '-', '.', '?', 0, '\'', "\n");
    }

    private void assertSyntaxDetails(String edi, char delimiter, char subDelimiter, char decimal, char release, int repetition, char terminator, String terminatorSuffix) throws SAXException, IOException {
        assertSyntaxDetails(new InputSource(new StringReader(edi)), delimiter, subDelimiter, decimal, release, repetition, terminator, terminatorSuffix);
    }

    private void assertSyntaxDetails(byte[] edi, char delimiter, char subDelimiter, char decimal, char release, int repetition, char terminator, String terminatorSuffix) throws SAXException, IOException {
        assertSyntaxDetails(new InputSource(new ByteArrayInputStream(edi)), delimiter, subDelimiter, decimal, release, repetition, terminator, terminatorSuffix);
    }

    private void assertSyntaxDetails(InputSource inputSource, char delimiter, char subDelimiter, char decimal, char release, int repetition, char terminator, String terminatorSuffix) throws SAXException, IOException {
        ediReader = EDIReaderFactory.createEDIReader(inputSource);
        try {
            ediReader.parse();
        } catch (EDISyntaxException e) {
            if (e.getMessage().startsWith("Unexpected end of data")) {
                // That's to be expected
            } else {
                throw new RuntimeException(e);
            }
        }
        assertEquals("Delimiter issue", delimiter, ediReader.getDelimiter());
        assertEquals("Sub-delimiter issue", subDelimiter, ediReader.getSubDelimiter());
        assertEquals("Decimal mark issue", decimal, ediReader.getDecimalMark());
        assertEquals("Release character issue", release, ediReader.getReleaseCharacter());
        assertEquals("Repetition separator issue", repetition, ediReader.getRepetitionSeparator());
        assertEquals("Terminator issue", terminator, ediReader.getTerminator());
        assertEquals("Terminator suffix issue", terminatorSuffix, ediReader.getTerminatorSuffix());
        // Now consider the tokenizer within the EDIReader
        assertEquals("Delimiter issue", delimiter, ediReader.getTokenizer().getDelimiter());
        assertEquals("Sub-delimiter issue", subDelimiter, ediReader.getTokenizer().getSubDelimiter());
//        assertEquals("Repetition separator issue", repetition, ediReader.getTokenizer().getRepetitionSeparator());
        assertEquals("Terminator issue", terminator, ediReader.getTokenizer().getTerminator());
    }

    static final String X12_FRAGMENT_A = """
            ISA~00~          ~00~          ~ZZ~58401          ~ZZ~04000          ~220810~0941~U~00204~000038449~0~P~<$
            GS~FA~58401~04000~220810~0941~000038449~X~002040CHRY$
            ST~997~0001$
            """;

    static final String X12_FRAGMENT_B = """
            ISA+00+          +00+          +ZZ+58401          +ZZ+04000          +220810+0941+U+00204+000038449+0+P+^!
            GS+FA+58401+04000+220810+0941+000038449+X+002040CHRY!
            ST+997+0001!
            """;

    static final String EDIFACT_UNA_FRAGMENT_A = """
            UNA:+.? '
            UNB+UNOA:1+005435656:1+006415160CFS:1+000210:1434+00000000000778+rref+aref+p+a+cid+t'
            UNH+00000000000117+INVOIC:D:97B:UN'
            BGM+380+342459+9'
            """;

    static final String EDIFACT_UNA_FRAGMENT_B = """
            UNA-*.? '
            UNB*UNOA-1*005435656-1*006415160CFS-1*000210-1434*00000000000778*rref*aref*p*a*cid*t'
            UNH*00000000000117*INVOIC-D-97B-UN'
            BGM*380*342459*9'
            """;
}
