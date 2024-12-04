package com.berryworks.edireader;

import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ProcessEnvelopeTest {

    @Test
    public void canProcessAddressesInX12Envelope() throws IOException, SAXException {
        MyAnsiReader myAnsiReader = new MyAnsiReader();
        myAnsiReader.parseEdi("""
                ISA~00~          ~00~          ~AA~BBBB           ~CC~DDDD           ~220810~0941~U~00204~000038449~0~P~<$
                GS~FA~EEEEE~FFFFF~220810~0941~000038449~X~002040CHRY$
                ST~997~0001$
                SE~2~0001$
                GE~1~000038449$
                IEA~1~000038449$
                """);
        assertEquals("ISA05=AA,ISA06=BBBB           ,ISA07=CC,ISA08=DDDD           ,ISA13=000038449," +
                        "GS02=EEEEE,GS03=FFFFF,GS06=000038449,GS08=002040CHRY," +
                        "ST02=0001",
                myAnsiReader.getProcessed());
    }

    private static class MyAnsiReader extends AnsiReader {
        private final StringBuilder processed = new StringBuilder();

        @Override
        protected void process(String ediElement, String value) throws SAXException {
            if (!processed.isEmpty()) processed.append(',');
            processed.append(ediElement).append('=').append(value);
        }

        public String getProcessed() {
            return processed.toString();
        }
    }
}
