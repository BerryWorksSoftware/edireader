package com.berryworks.edireader;

import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class EDIReaderFactoryTest {

    @Test
    public void x12UsingReader() throws SAXException, IOException {
        StringReader reader = new StringReader(EDI_SAMPLE);
        EDIReader ediReader = EDIReaderFactory.createEDIReader(reader);
        ediReader.parse(reader);
        int charCount = ediReader.getCharCount();
        assertEquals(286, charCount);
    }

    @Test
    public void x12UsingInputStream() throws SAXException, IOException {
//        StringReader reader = new StringReader(EDI_SAMPLE);
        byte[] bytes = EDI_SAMPLE.getBytes(StandardCharsets.ISO_8859_1);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        EDIReader ediReader = EDIReaderFactory.createEDIReader(inputStream);
        ediReader.parse();
        int charCount = ediReader.getCharCount();
        assertEquals(286, charCount);
    }

    private static final String EDI_SAMPLE =
            """
                    ISA*00*          *00*          *ZZ*D00111         *ZZ*0055           *030603*1337*U*00401*000000121*0*T*:^
                    GS*HP*D00111*0055*20030603*1337*1210001*X*004010X091A1^
                    ST*870*0000001^
                    BSR*4*PA*SUPPLIER CONFIRMATION NUMBER*CCYYMMDD^
                    REF*MR*12345^
                    SE*4*0000001^
                    GE*1*1210001^
                    IEA*1*000000121^
                    """;

}
