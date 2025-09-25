package com.berryworks.edireader;

import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class SyntaxDetectionTest {

    EDIReader ediReader;

    @Test
    public void x12() throws EDISyntaxException, IOException {
        assertSyntaxDetails(new StringReader(X12_FRAGMENT), '~', '<', (char) 0, ' ', 0, '$', "\n");
    }

    @Test
    public void edifact() throws EDISyntaxException, IOException {
        assertSyntaxDetails(new StringReader(EDIFACT_UNA_FRAGMENT), '+', ':', '.', '?', 0, '\'', "\n");
    }

    private void assertSyntaxDetails(Reader reader, char delimiter, char subDelimiter, char decimal, char release, int repetition, char terminator, String terminatorSuffix) throws EDISyntaxException, IOException {
        assertSyntaxDetails(new InputSource(reader), delimiter, subDelimiter, decimal, release, repetition, terminator, terminatorSuffix);
    }

    private void assertSyntaxDetails(InputSource inputSource, char delimiter, char subDelimiter, char decimal, char release, int repetition, char terminator, String terminatorSuffix) throws EDISyntaxException, IOException {
        ediReader = EDIReaderFactory.createEDIReader(inputSource);
        assertEquals("Delimiter issue", delimiter, ediReader.getDelimiter());
        assertEquals("Sub-delimiter issue", subDelimiter, ediReader.getSubDelimiter());
        assertEquals("Decimal mark issue", decimal, ediReader.getDecimalMark());
        assertEquals("Release character issue", release, ediReader.getReleaseCharacter());
        assertEquals("Repetition separator issue", repetition, ediReader.getRepetitionSeparator());
        assertEquals("Terminator issue", terminator, ediReader.getTerminator());
        assertEquals("Terminator suffix issue", terminatorSuffix, ediReader.getTerminatorSuffix());
    }

    static final String X12_FRAGMENT = """
            ISA~00~          ~00~          ~ZZ~58401          ~ZZ~04000          ~220810~0941~U~00204~000038449~0~P~<$
            GS~FA~58401~04000~220810~0941~000038449~X~002040CHRY$
            ST~997~0001$
            """;

    static final String EDIFACT_UNA_FRAGMENT = """
            UNA:+.? '
            UNB+UNOA:1+005435656:1+006415160CFS:1+000210:1434+00000000000778+rref+aref+p+a+cid+t'
            UNH+00000000000117+INVOIC:D:97B:UN'
            BGM+380+342459+9'
            """;
}
