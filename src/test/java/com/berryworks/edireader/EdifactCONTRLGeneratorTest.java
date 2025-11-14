package com.berryworks.edireader;

import com.berryworks.edireader.benchmark.EDITestData;
import com.berryworks.edireader.util.BranchingWriter;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static com.berryworks.edireader.util.MaskingTool.mask;
import static org.junit.Assert.assertEquals;

public class EdifactCONTRLGeneratorTest {

    private EdifactCONTRLGenerator generator;

    @Test
    public void canGenerateCONTRL_1message_NoErrors() throws IOException, SAXException {

        String ediText = EDITestData.getEdifactInterchange();
        EDIReader ediReader = EDIReaderFactory.createEDIReader(new StringReader(ediText));
        StringWriter ackWriter = new StringWriter();
        ediReader.setAckStream(new BranchingWriter(ackWriter));
        ediReader.parse();

        String expected = """
                UNB+IATA:1+REUAGT82AGENT/LHR01:PIMA+REUAIR08DLH:PIMA+??????:????+841F60UNZ'
                UNH+1+CONTRL:90:1:IA'
                UCI+841F60UNZ+REUAIR08DLH:PIMA+REUAGT82AGENT/LHR01:PIMA+8'
                UNT+3+1'
                UNZ+1+841F60UNZ'
                """;
        String actual = ackWriter.toString();
        assertEquals(expected, mask(actual, expected));
    }

    @Test
    public void canGenerateCONTRL_2messages_NoErrors() throws IOException, SAXException {

        String ediText = EDITestData.getEdifactInterchange(2);
        EDIReader ediReader = EDIReaderFactory.createEDIReader(new StringReader(ediText));
        StringWriter ackWriter = new StringWriter();
        ediReader.setAckStream(new BranchingWriter(ackWriter));
        ediReader.parse();

        String expected = """
                UNB+IATA:1+REUAGT82AGENT/LHR01:PIMA+REUAIR08DLH:PIMA+??????:????+841F60UNZ'
                UNH+1+CONTRL:90:1:IA'
                UCI+841F60UNZ+REUAIR08DLH:PIMA+REUAGT82AGENT/LHR01:PIMA+8'
                UNT+3+1'
                UNZ+1+841F60UNZ'
                """;
        String actual = ackWriter.toString();
        assertEquals(asLines(expected), asLines(mask(actual, expected)));
    }

    private String asLines(String wrappedSegments) {
        return wrappedSegments.replace("'", "'\n");
    }
}
