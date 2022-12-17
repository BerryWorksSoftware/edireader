package com.berryworks.edireader;

import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class ProcessEnvelopeTest {

    @Test
    public void canProcessAddressesInX12Envelope() throws IOException, SAXException {
        MyAnsiReader myAnsiReader = new MyAnsiReader();
        myAnsiReader.setContentHandler(new DefaultHandler());
        myAnsiReader.parse(new InputSource(new StringReader("" +
                "ISA~00~          ~00~          ~AA~BBBB           ~CC~DDDD           ~220810~0941~U~00204~000038449~0~P~<$\n" +
                "GS~FA~EEEEE~FFFFF~220810~0941~000038449~X~002040CHRY$\n" +
                "ST~997~0001$\n" +
                "SE~2~0001$\n" +
                "GE~1~000038449$\n" +
                "IEA~1~000038449$\n")));
        assertEquals("ISA05=AA,ISA06=BBBB           ,ISA07=CC,ISA08=DDDD           ,ISA13=000038449," +
                        "GS02=EEEEE,GS03=FFFFF,GS06=000038449,GS08=002040CHRY," +
                        "ST02=0001",
                myAnsiReader.getProcessed());
    }

    private static class MyAnsiReader extends AnsiReader {
        private final StringBuilder processed = new StringBuilder();

        @Override
        protected void process(String ediElement, String value) throws SAXException {
            if (processed.length() > 0) processed.append(',');
            processed.append(ediElement).append('=').append(value);
        }

        public String getProcessed() {
            return processed.toString();
        }
    }
}
