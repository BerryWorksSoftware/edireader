package com.berryworks.edireader;

import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

public class EdifactEncodingTest {

    private EDIReader ediReader;

    @Test
    public void baseline() throws IOException, SAXException {
        StringReader reader = new StringReader(EDIFACT_UNOA);
        ediReader = EDIReaderFactory.createEDIReader(reader);
        ediReader.parse(reader);
    }

    private static final String EDIFACT_UNOA = """
            UNB+UNOA:1+005435656:1+006415160CFS:1+000210:1434+00000000000778+rref+aref+p+a+cid+t'
            UNH+00000000000117+INVOIC:D:97B:UN'
            BGM+380+342459+9'
            NAD+SE+005435656::16++GENERAL WIDGET COMPANY'
            UNT+4+00000000000117'
            UNZ+1+00000000000778'
            """;
}
