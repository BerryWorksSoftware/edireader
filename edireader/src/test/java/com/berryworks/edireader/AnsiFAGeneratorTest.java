package com.berryworks.edireader;

import com.berryworks.edireader.util.BranchingWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

public class AnsiFAGeneratorTest {

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
    public void canBuildPreambleWithFixedLengthISA() throws IOException {
        final String original = "ISA^00^          ^00^          ^01^007941230      ^ZZ^8145           ^100903^0143^U^00401^500009740^0^P^|~";
        final String expected = "ISA^00^          ^00^          ^ZZ^8145           ^01^007941230      ^999999^9999^U^00401^500009740^0^P^|~";
        generator._generateAcknowledgementPreamble(original, "group sender", "group receiver", 8, "group version");
        Assert.assertEquals(expected.substring(0, 105), maskDate(output.toString().substring(0, 105)));
    }

    @Test
    public void canBuildPreambleWithVariableLengthISA() throws IOException {
        final String original = "ISA^00^          ^00^          ^01^007941230      ^ZZ^8145      ^100903^0143^U^00401^500009740^0^P^|~";
        final String expected = "ISA^00^          ^00^          ^ZZ^8145           ^01^007941230      ^999999^9999^U^00401^500009740^0^P^|~";
        generator._generateAcknowledgementPreamble(original, "group sender", "group receiver", 8, "group version");
        Assert.assertEquals(expected.substring(0, 105), maskDate(output.toString().substring(0, 105)));
    }

    @Test
    public void canBuildPreambleWithReallyOddDelimiter() throws IOException {
        final String original = "ISAx00x      x00x       x01x007941230   xZZx8145      x100903x0143xUx00401x500009740x0xPx|~";
        final String expected = "ISAx00x          x00x          xZZx8145           x01x007941230      x999999x9999xUx00401x500009740x0xPx|~";
        syntaxDescriptor.setDelimiter('x');
        generator._generateAcknowledgementPreamble(original, "group sender", "group receiver", 8, "group version");
        Assert.assertEquals(expected.substring(0, 105), maskDate(output.toString().substring(0, 105)));
    }

    private String maskDate(String isa) {
        final char delimiter = isa.charAt(3);
        final String firstPart = isa.substring(0, 70);
        final String lastPart = isa.substring(81);
        return firstPart + "999999" + delimiter + "9999" + lastPart;
    }

    class MyAnsiFAGenerator extends AnsiFAGenerator {

        public MyAnsiFAGenerator(StandardReader ansiReader, BranchingWriter ackStream) {
            super(ansiReader, ackStream);
        }

        public void _generateAcknowledgementPreamble(String firstSegment,
                                                     String groupSender, String groupReceiver, int groupDateLength,
                                                     String groupVersion) throws IOException {
            generateAcknowledgementPreamble(firstSegment, groupSender, groupReceiver, groupDateLength, groupVersion);
        }

    }
}
