package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.*;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static com.berryworks.edireader.util.FixedLength.isPresent;
import static org.junit.Assert.assertEquals;

public class EDIReaderSAXAdapterTest {

    private MyAdapater adapter;

    @Test
    public void basics() throws SAXException {
        XMLTags xmlTags = new DefaultXMLTags();
        adapter = new MyAdapater();

        EDIAttributes attributes = new EDIAttributes();
        attributes.addCDATA("A", "a");

        adapter.startElement("", "interchange", "interchange", attributes);
        assertEquals("Interchange.", adapter.getTrace());

        adapter.startElement("", "group", "group", attributes);
        adapter.startElement("", "transaction", "transaction", attributes);
        assertEquals("Interchange.Group.Transaction.", adapter.getTrace());

        adapter.startElement("", "segment", "segment", attributes);
        adapter.startElement("", "element", "element", attributes);
        assertEquals("Interchange.Group.Transaction.FirstSegment.Element.", adapter.getTrace());

        adapter.characters("abc".toCharArray(), 0, 3);
        adapter.endElement("", "element", "element");
        assertEquals("Interchange.Group.Transaction.FirstSegment.Element.e:abc.", adapter.getTrace());

        adapter.startElement("", "subelement", "subelement", attributes);
        adapter.characters("def".toCharArray(), 0, 3);
        adapter.endElement("", "subelement", "subelement");
        assertEquals("Interchange.Group.Transaction.FirstSegment.Element.e:abc.SubElement.s:def.", adapter.getTrace());
    }

    @Test
    public void detectFirstSegmentInGroup() throws SAXException, IOException {
        Reader edi = new StringReader("" +
                "ISA*00*          *00*          *ZZ*0011223456     *ZZ*999999999      *990320*0157*U*00300*000000015*0*P*~$\n" +
                "GS*PO*0011223456*999999999*950120*0147*5*X*004010$\n" +
                "ST*850*000000001$\n" +
                "BEG*00*SA*95018017***950118$\n" +
                "N1*SE*UNIVERSAL WIDGETS$\n" +
                "N3*375 PLYMOUTH PARK*SUITE 205$\n" +
                "N4*IRVING*TX*75061$\n" +
                "N1*ST*JIT MANUFACTURING$\n" +
                "N3*BUILDING 3B*2001 ENTERPRISE PARK$\n" +
                "N4*JUAREZ*CH**MEX$\n" +
                "N1*AK*JIT MANUFACTURING$\n" +
                "N3*400 INDUSTRIAL PARKWAY$\n" +
                "N4*INDUSTRIAL AIRPORT*KS*66030$\n" +
                "N1*BT*JIT MANUFACTURING$\n" +
                "N2*ACCOUNTS PAYABLE DEPARTMENT$\n" +
                "N3*400 INDUSTRIAL PARKWAY$\n" +
                "N4*INDUSTRIAL AIRPORT*KS*66030$\n" +
                "PO1*001*4*EA*330*TE*IN*525*VN*X357-W2$\n" +
                "PID*F****HIGH PERFORMANCE WIDGET$\n" +
                "SCH*4*EA****002*950322$\n" +
                "CTT*1*1$\n" +
                "SE*20*000000001$\n" +
                "ST*850*000000002$\n" +
                "BEG*00*SA*95018017***950118$\n" +
                "N1*SE*UNIVERSAL WIDGETS$\n" +
                "N3*375 PLYMOUTH PARK*SUITE 205$\n" +
                "N4*IRVING*TX*75061$\n" +
                "N1*ST*JIT MANUFACTURING$\n" +
                "N3*BUILDING 3B*2001 ENTERPRISE PARK$\n" +
                "N4*JUAREZ*CH**MEX$\n" +
                "N1*AK*JIT MANUFACTURING$\n" +
                "N3*400 INDUSTRIAL PARKWAY$\n" +
                "N4*INDUSTRIAL AIRPORT*KS*66030$\n" +
                "N1*BT*JIT MANUFACTURING$\n" +
                "N2*ACCOUNTS PAYABLE DEPARTMENT$\n" +
                "N3*400 INDUSTRIAL PARKWAY$\n" +
                "N4*INDUSTRIAL AIRPORT*KS*66030$\n" +
                "PO1*001*4*EA*330*TE*IN*525*VN*X357-W2$\n" +
                "PID*F****HIGH PERFORMANCE WIDGET$\n" +
                "SCH*4*EA****002*950322$\n" +
                "CTT*1*1$\n" +
                "SE*20*000000002$\n" +
                "GE*2*5$\n" +
                "IEA*1*000000015$\n");
        EDIReader ediReader = EDIReaderFactory.createEDIReader(edi);
        MyAdapater handler = new MyAdapater(false);
        ediReader.setContentHandler(handler);
        ediReader.parse(edi);
        assertEquals("Interchange.Group.Transaction." +
                        "FirstSegment-BEG." +
                        "Loop.FirstSegment-N1.AnotherSegment-N3.AnotherSegment-N4." +
                        "Loop.FirstSegment-N1.AnotherSegment-N3.AnotherSegment-N4." +
                        "Loop.FirstSegment-N1.AnotherSegment-N3.AnotherSegment-N4." +
                        "Loop.FirstSegment-N1.AnotherSegment-N2.AnotherSegment-N3.AnotherSegment-N4." +
                        "Loop.FirstSegment-PO1." +
                        "Loop.FirstSegment-PID." +
                        "Loop.FirstSegment-SCH." +
                        "Loop.FirstSegment-CTT." +
                        "Transaction." +
                        "FirstSegment-BEG." +
                        "Loop.FirstSegment-N1.AnotherSegment-N3.AnotherSegment-N4." +
                        "Loop.FirstSegment-N1.AnotherSegment-N3.AnotherSegment-N4." +
                        "Loop.FirstSegment-N1.AnotherSegment-N3.AnotherSegment-N4." +
                        "Loop.FirstSegment-N1.AnotherSegment-N2.AnotherSegment-N3.AnotherSegment-N4." +
                        "Loop.FirstSegment-PO1." +
                        "Loop.FirstSegment-PID." +
                        "Loop.FirstSegment-SCH." +
                        "Loop.FirstSegment-CTT.",
                handler.getTrace());
    }

    static class MyAdapater extends EDIReaderSAXAdapter {

        private final StringBuilder builder = new StringBuilder();
        private final boolean showElements;

        public MyAdapater(boolean showElements) {
            this.showElements = showElements;
        }

        public MyAdapater() {
            this(true);
        }

        @Override
        protected void beginInterchange(int charCount, int segmentCharCount, Attributes attributes) {
            builder.append("Interchange.");
        }

        @Override
        protected void beginExplicitGroup(int charCount, int segmentCharCount, Attributes attributes) {
            builder.append("Group.");
        }

        @Override
        protected void beginDocument(int charCount, int segmentCharCount, Attributes attributes) {
            builder.append("Transaction.");
        }

        @Override
        protected void beginSegmentGroup(String loopName, Attributes atts) {
            builder.append("Loop.");
        }

        @Override
        protected void beginFirstSegment(Attributes atts) {
            String id = atts.getValue("Id");
            if (isPresent(id)) {
                builder.append("FirstSegment-").append(id).append(".");
            } else {
                builder.append("FirstSegment.");
            }
        }

        @Override
        protected void beginAnotherSegment(Attributes atts) {
            String id = atts.getValue("Id");
            if (isPresent(id)) {
                builder.append("AnotherSegment-").append(id).append(".");
            } else {
                builder.append("AnotherSegment.");
            }
        }

        @Override
        protected void beginSegmentElement(Attributes atts) {
            if (showElements) builder.append("Element.");
        }

        @Override
        protected void endSegmentElement(String elementString) {
            if (showElements) builder.append("e:").append(elementString).append('.');
        }

        @Override
        protected void beginSegmentSubElement(Attributes atts) {
            if (showElements) builder.append("SubElement.");
        }

        @Override
        protected void endSegmentSubElement(String subElementString) {
            if (showElements) builder.append("s:").append(subElementString).append('.');
        }

//        @Override
//        public void characters(char[] ch, int start, int length) throws SAXException {
//            builder.append("e:").append(ch, start, length).append('.');
//        }

        public String getTrace() {
            return builder.toString();
        }
    }
}
