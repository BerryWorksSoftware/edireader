package com.berryworks.edireader;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class TransactionCallbackTest {

    private InputSource inputSource;

    @Before
    public void setUp() {
        inputSource = new InputSource(new StringReader(X12_SAMPLE));
    }

    @Test
    public void baseline() throws SAXException, IOException {
        EDIReader parser = EDIReaderFactory.createEDIReader(inputSource);
        parser.setTransactionCallback(new TransactionCallback() {
        });
        parser.parse(inputSource);
    }

    @Test
    public void canLearnSizes() throws SAXException, IOException {
        EDIReader parser = EDIReaderFactory.createEDIReader(inputSource);
        MyCallback callback = new MyCallback(parser) {
        };
        parser.setTransactionCallback(callback);
        parser.parse(inputSource);
        assertEquals("" +
                     "transaction, 997, 004010, 0001, 38|" +
                     "transaction, 997, 004010ABC, 0002, 49|" +
                     "group, FA, 004010, 0038449, 149|" +
                     "interchange, X12, 00204, 000038449, 273|", callback.getLog());
    }

    private static class MyCallback implements TransactionCallback {
        private final EDIReader parser;
        private final StringBuilder sb = new StringBuilder();

        public MyCallback(EDIReader parser) {
            this.parser = parser;
        }

        @Override
        public void end(String ediUnit, String identifier, String version, String controlNumber, long size) {
            sb.append(String.format("%s, %s, %s, %s, %s|", ediUnit, identifier, version, controlNumber, size));
        }

        public String getLog() {
            return sb.toString();
        }
    }

    private final static String X12_SAMPLE = """
            ISA~00~          ~00~          ~ZZ~58401          ~ZZ~04000          ~220810~0941~U~00204~000038449~0~P~<$
            GS~FA~58401~04000~220810~0941~0038449~X~004010$
            ST~997~0001$
            AK1~AG~38327$
            SE~3~0001$
            ST~997~0002~004010ABC$
            AK1~AG~383277$
            SE~3~0002$
            GE~2~0038449$
            IEA~1~000038449$
            """;
}
