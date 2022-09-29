package com.berryworks.edireader;

import com.berryworks.edireader.benchmark.EDITestData;
import com.berryworks.edireader.util.BranchingWriter;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AnsiFAGeneratorTest {

    public static final String TEST_DATA_997 = "" +
            "ISA~00~          ~00~          ~ZZ~58401          ~ZZ~04000          ~999999~9999~U~00204~000038449~0~P~<$" +
            "GS~FA~58401~04000~999999~9999~000038449~X~002040CHRY$" +
            "ST~997~0001$" +
            "AK1~AG~38327$" +
            "AK2~824~000042460$AK5~A$" +
            "AK9~A~1~1~1$SE~6~0001$" +
            "GE~1~000038449$" +
            "IEA~1~000038449$";
    public static final String TEST_DATA_997_WITH_TA1 = "" +
            "ISA~00~          ~00~          ~ZZ~58401          ~ZZ~04000          ~999999~9999~U~00204~000038449~0~P~<$" +
            "TA1~000038449~040714~1003~A~000$" +
            "GS~FA~58401~04000~999999~9999~000038449~X~002040CHRY$" +
            "ST~997~0001$" +
            "AK1~AG~38327$" +
            "AK2~824~000042460$AK5~A$" +
            "AK9~A~1~1~1$SE~6~0001$" +
            "GE~1~000038449$" +
            "IEA~1~000038449$";
    private MyAnsiFAGenerator generator;
    private StringWriter output;
    private BranchingWriter ackStream;
    private StandardReader ansiReader;
    private SyntaxDescriptor syntaxDescriptor;

    @Before
    public void setUp() {
        output = new StringWriter();
        ackStream = new BranchingWriter(output);
        ansiReader = new AnsiReader();
        syntaxDescriptor = new SyntaxDescriptor();
        syntaxDescriptor.setDelimiter('^');
        ansiReader.setAcknowledgmentSyntaxDescriptor(syntaxDescriptor);
        generator = new MyAnsiFAGenerator(ansiReader, ackStream);
    }

    @Test
    public void canGenerate997() throws IOException, SAXException {
        ansiReader = new AnsiReader();
        ansiReader.setContentHandler(new DefaultHandler());
        ansiReader.setAcknowledgment(ackStream);
        ansiReader.parse(EDITestData.getAnsiInputSource());
        assertLikeness(TEST_DATA_997, output.toString());
    }

    @Test
    public void canGenerate997WithTA1() throws IOException, SAXException {
        ansiReader = new AnsiReader();
        ansiReader.setContentHandler(new DefaultHandler());
        ansiReader.setAcknowledgment(ackStream);
        ansiReader.setInterchangeAcknowledgment(true);
        ansiReader.parse(EDITestData.getAnsiInputSource());
        assertLikeness(TEST_DATA_997_WITH_TA1, output.toString());
    }

    @Test
    public void canGenerate997FromVariableLengthISAInput() throws IOException, SAXException {
        ansiReader = new AnsiReader();
        ansiReader.setContentHandler(new DefaultHandler());
        ansiReader.setAcknowledgment(ackStream);
        String ansiInterchange = EDITestData.getAnsiInterchange();
        ansiInterchange = ansiInterchange.replaceAll("58401 {4}", "58401");
        final InputSource inputSource = new InputSource(new StringReader(ansiInterchange));
        ansiReader.setSyntaxExceptionHandler(syntaxException -> true);
        ansiReader.parse(inputSource);
        assertLikeness(TEST_DATA_997, output.toString());
    }

    @Test
    public void canGenerate997WithDelimiterDifferentThanInput() throws IOException, SAXException {
        ansiReader = new AnsiReader();
        ansiReader.setContentHandler(new DefaultHandler());
        final SyntaxDescriptor syntaxDescriptor = new SyntaxDescriptor();
        syntaxDescriptor.setDelimiter('-');
        ansiReader.setAcknowledgment(ackStream, syntaxDescriptor);

        ansiReader.parse(EDITestData.getAnsiInputSource());
//        assertEquals(TEST_DATA_997, output.toString());
        assertLikeness(TEST_DATA_997.replaceAll("~", "-"), output.toString());
    }


    @Test
    public void canBuildPreambleWithFixedLengthISA() throws IOException {
        final String original = "ISA^00^          ^00^          ^01^007941230      ^ZZ^8145           ^100903^0143^U^00401^500009740^0^P^|~";
        final String expected = "ISA^00^          ^00^          ^ZZ^8145           ^01^007941230      ^999999^9999^U^00401^500009740^0^P^|~";
        generator._generateAcknowledgementPreamble(original, "group sender", "group receiver", 8, "group version");
        assertEquals(expected.substring(0, 105), maskDate(output.toString().substring(0, 105)));
    }

    @Test
    public void canBuildPreambleWithVariableLengthISA() throws IOException {
        final String original = "ISA^00^          ^00^          ^01^007941230      ^ZZ^8145      ^100903^0143^U^00401^500009740^0^P^|~";
        final String expected = "ISA^00^          ^00^          ^ZZ^8145           ^01^007941230      ^999999^9999^U^00401^500009740^0^P^|~";
        generator._generateAcknowledgementPreamble(original, "group sender", "group receiver", 8, "group version");
        assertEquals(expected.substring(0, 105), maskDate(output.toString().substring(0, 105)));
    }

    @Test
    public void canBuildPreambleWithReallyOddDelimiter() throws IOException {
        final String original = "ISAx00x      x00x       x01x007941230   xZZx8145      x100903x0143xUx00401x500009740x0xPx|~";
        final String expected = "ISAx00x          x00x          xZZx8145           x01x007941230      x999999x9999xUx00401x500009740x0xPx|~";
        syntaxDescriptor.setDelimiter('x');
        generator._generateAcknowledgementPreamble(original, "group sender", "group receiver", 8, "group version");
        assertEquals(expected.substring(0, 105), maskDate(output.toString().substring(0, 105)));
    }

    private String maskDate(String isa) {
        final char delimiter = isa.charAt(3);
        final String firstPart = isa.substring(0, 70);
        final String lastPart = isa.substring(81);
        return firstPart + "999999" + delimiter + "9999" + lastPart;
    }

    static class MyAnsiFAGenerator extends AnsiFAGenerator {

        public MyAnsiFAGenerator(StandardReader ansiReader, BranchingWriter ackStream) {
            super(ansiReader, ackStream);
        }

        public void _generateAcknowledgementPreamble(String firstSegment,
                                                     String groupSender, String groupReceiver, int groupDateLength,
                                                     String groupVersion) throws IOException {
            generateAcknowledgementPreamble(firstSegment, groupSender, groupReceiver, groupDateLength, groupVersion);
        }
    }

    private void assertLikeness(String expected, String actual) {
        assertEquals("Wrong length", expected.length(), actual.length());
        for (int i = 0; i < expected.length(); i++) {
            final char expectedChar = expected.charAt(i);
            final char actualChar = actual.charAt(i);
            if (expectedChar == actualChar) {
                continue;
            }
            if (expectedChar == '9') {
                // 9 is interpreted as a wildcard matching any digit
                assertTrue("Digit expected at index " + i, Character.isDigit(actualChar));
                continue;
            }
            assertEquals("Character mismatch at index " + i, String.valueOf(expectedChar), String.valueOf(actualChar));
        }
    }

}
