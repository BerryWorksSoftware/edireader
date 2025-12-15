package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIAttributes;
import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ContextAwareSaxAdapterTest {

    private MyContextAwareSaxAdapter adapter;
    private EDIAttributes attributes;

    @Before
    public void setUp() {
        adapter = new MyContextAwareSaxAdapter();
        attributes = new EDIAttributes();
    }

    @Test
    public void basics() throws SAXException {
        adapter.startElement(null, "A", null, attributes);
        assertContext(adapter, null, null, "A");

        addCharacters(adapter, "aaa");
        // The ContextAwareSaxAdapter is buffering data, waiting until it can call
        // start() with all the data
        assertContext(adapter, null, null, "A");

        addCharacters(adapter, " aaa");
        // The ContextAwareSaxAdapter is buffering data, waiting until it can call
        // start() with all the data
        assertContext(adapter, null, null, "A");

        adapter.startElement(null, "B", null, attributes);
        // The startElement() means that no more data will be arriving for the element,
        // so the pending data is delivered on a call to start() to the ContextAwareSaxAdapter subclass.
        assertContext(adapter, "(A", "aaa aaa|", "A", "B");

        addCharacters(adapter, "bbb");
        // The new data for the b element is buffered
        assertContext(adapter, "(A", "aaa aaa|", "A", "B");

        adapter.endElement(null, "B", null);
        // There will be no more data for the b element.
        assertContext(adapter, "(A(BB)", "aaa aaa|bbb|", "A");

        adapter.endElement(null, "A", null);
        assertContext(adapter, "(A(BB)A)", "aaa aaa|bbb|", null);
    }

    @Test
    public void dataTrimmingIsTheDefault() throws SAXException {
        adapter.startElement(null, "A", null, attributes);
        assertContext(adapter, null, null, "A");

        addCharacters(adapter, "aaa");
        addCharacters(adapter, " aaa ");
        assertContext(adapter, null, null, "A");

        adapter.startElement(null, "B", null, attributes);
        addCharacters(adapter, "bbb   ");
        assertContext(adapter, "(A", "aaa aaa|", "A", "B");

        adapter.endElement(null, "B", null);
        assertContext(adapter, "(A(BB)", "aaa aaa|bbb|", "A");

        adapter.endElement(null, "A", null);
        assertContext(adapter, "(A(BB)A)", "aaa aaa|bbb|", null);
    }

    @Test
    public void dataTrimmingCanBeDisabled() throws SAXException {
        adapter = new MyContextAwareSaxAdapter(false);
        adapter.startElement(null, "A", null, attributes);
        assertContext(adapter, null, null, "A");

        addCharacters(adapter, "aaa");
        addCharacters(adapter, " aaa ");
        assertContext(adapter, null, null, "A");

        adapter.startElement(null, "B", null, attributes);
        addCharacters(adapter, "bbb   ");
        assertContext(adapter, "(A", "aaa aaa |", "A", "B");

        adapter.endElement(null, "B", null);
        assertContext(adapter, "(A(BB)", "aaa aaa |bbb   |", "A");

        adapter.endElement(null, "A", null);
        assertContext(adapter, "(A(BB)A)", "aaa aaa |bbb   |", null);
    }

    @Test
    public void canHandleEdi() throws SAXException, IOException {
        EDIReader ediReader;

        // First with only a counting handler
        ediReader = EDIReaderFactory.createEDIReader(new StringReader(SAMPLE_850));
        MyCountingHandler countingHandler = new MyCountingHandler();
        ediReader.setContentHandler(countingHandler);
        ediReader.parse();
        assertEquals(1032, countingHandler.getCount());

        // Then with only a ContextAwareSaxAdapter
        ediReader = EDIReaderFactory.createEDIReader(new StringReader(SAMPLE_850));
        MyContextAwareSaxAdapter contextAwareAdapter = new MyContextAwareSaxAdapter();
        ediReader.setContentHandler(contextAwareAdapter);
        ediReader.parse();
        assertTrue(contextAwareAdapter
                .getStartsAndEnds()
                .startsWith("(ediroot(interchange(sender(addressaddress)sender)(receiver(addressaddress)receiver)(group"));

        // Then with both
        ediReader = EDIReaderFactory.createEDIReader(new StringReader(SAMPLE_850));
        contextAwareAdapter = new MyContextAwareSaxAdapter();
        countingHandler = new MyCountingHandler();
        contextAwareAdapter.setContentHandler(countingHandler);
        ediReader.setContentHandler(contextAwareAdapter);
        ediReader.parse();
        assertTrue(contextAwareAdapter
                .getStartsAndEnds()
                .startsWith("(ediroot(interchange(sender(addressaddress)sender)(receiver(addressaddress)receiver)(group"));
        assertEquals(1032, countingHandler.getCount());
    }

    private void addCharacters(ContextAwareSaxAdapter adapter, String data) throws SAXException {
        adapter.characters(data.toCharArray(), 0, data.length());
    }

    private void assertContext(MyContextAwareSaxAdapter adapter, String startsAndEnds, String data, String... names) {
        List<String> context = adapter.getContext();
        if (names == null) {
            assertEquals(0, context.size());
        } else {
            assertEquals(names.length, context.size());
            for (int i = 0; i < context.size(); i++) {
                assertEquals(names[i], context.get(i));
            }
        }

        if (startsAndEnds == null) startsAndEnds = "";
        assertEquals(startsAndEnds, adapter.getStartsAndEnds());

        if (data == null) data = "";
        assertEquals(data, adapter.getData());
    }

    private static class MyContextAwareSaxAdapter extends ContextAwareSaxAdapter {

        private final StringBuilder data = new StringBuilder();
        private final StringBuilder sequence = new StringBuilder();

        public MyContextAwareSaxAdapter(boolean isTrimmingEnabled) {
            super(isTrimmingEnabled);
        }

        public MyContextAwareSaxAdapter() {
            this(true);
        }

        public String getData() {
            return data.toString();
        }

        public String getStartsAndEnds() {
            return sequence.toString();
        }

        @Override
        public void start(String uri, String name, String data, EDIAttributes attributes) throws SAXException {
            this.data.append(data).append('|');
            this.sequence.append('(').append(name);
        }

        @Override
        public void end(String uri, String name) throws SAXException {
            this.sequence.append(name).append(')');
        }
    }

    static final String SAMPLE_850 = """
            ISA*00*          *00*          *ZZ*0011223456     *ZZ*999999999      *990320*0157*U*00300*000000015*0*P*~$
            GS*PO*0011223456*999999999*950120*0147*5*X*004010$
            ST*850*000000001$
            BEG*00*SA*95018017***950118$
            N1*SE*UNIVERSAL WIDGETS$
            N3*375 PLYMOUTH PARK*SUITE 205$
            N4*IRVING*TX*75061$
            N1*ST*JIT MANUFACTURING$
            N3*BUILDING 3B*2001 ENTERPRISE PARK$
            N4*JUAREZ*CH**MEX$
            N1*AK*JIT MANUFACTURING$
            N3*400 INDUSTRIAL PARKWAY$
            N4*INDUSTRIAL AIRPORT*KS*66030$
            N1*BT*JIT MANUFACTURING$
            N2*ACCOUNTS PAYABLE DEPARTMENT$
            N3*400 INDUSTRIAL PARKWAY$
            N4*INDUSTRIAL AIRPORT*KS*66030$
            PO1*001*4*EA*330*TE*IN*525*VN*X357-W2$
            PID*F****HIGH PERFORMANCE WIDGET$
            SCH*4*EA****002*950322$
            CTT*1*1$
            SE*20*000000001$
            ST*850*000000002$
            BEG*00*SA*95018017***950118$
            N1*SE*UNIVERSAL WIDGETS$
            N3*375 PLYMOUTH PARK*SUITE 205$
            N4*IRVING*TX*75061$
            N1*ST*JIT MANUFACTURING$
            N3*BUILDING 3B*2001 ENTERPRISE PARK$
            N4*JUAREZ*CH**MEX$
            N1*AK*JIT MANUFACTURING$
            N3*400 INDUSTRIAL PARKWAY$
            N4*INDUSTRIAL AIRPORT*KS*66030$
            N1*BT*JIT MANUFACTURING$
            N2*ACCOUNTS PAYABLE DEPARTMENT$
            N3*400 INDUSTRIAL PARKWAY$
            N4*INDUSTRIAL AIRPORT*KS*66030$
            PO1*001*4*EA*330*TE*IN*525*VN*X357-W2$
            PID*F****HIGH PERFORMANCE WIDGET$
            SCH*4*EA****002*950322$
            CTT*1*1$
            SE*20*000000002$
            GE*2*5$
            IEA*1*000000015$
            """;

    private class MyCountingHandler implements ContentHandler {
        private int total;

        public int getTotal() {
            return total;
        }

        @Override
        public void setDocumentLocator(Locator locator) {
        }

        @Override
        public void startDocument() throws SAXException {
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startPrefixMapping(String s, String s1) throws SAXException {
        }

        @Override
        public void endPrefixMapping(String s) throws SAXException {
        }

        @Override
        public void startElement(String s, String s1, String s2, Attributes attributes) throws SAXException {
            total++;
        }

        @Override
        public void endElement(String s, String s1, String s2) throws SAXException {
            total++;
        }

        @Override
        public void characters(char[] chars, int i, int i1) throws SAXException {
            total += i1;
        }

        @Override
        public void ignorableWhitespace(char[] chars, int i, int i1) throws SAXException {

        }

        @Override
        public void processingInstruction(String s, String s1) throws SAXException {

        }

        @Override
        public void skippedEntity(String s) throws SAXException {

        }

        public int getCount() {
            return total;
        }
    }
}
