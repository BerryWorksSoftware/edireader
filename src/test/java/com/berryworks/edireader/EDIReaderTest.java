package com.berryworks.edireader;

import com.berryworks.edireader.util.sax.EDIReaderSAXAdapter;
import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class EDIReaderTest {

    EDIReader ediReader;

    @Test
    public void isAwareOfSyntaxCharacters_X12_5010() throws IOException, SAXException {
        ediReader = new EDIReader();
        final MyContentHandler contentHandler = new MyContentHandler();
        ediReader.setContentHandler(contentHandler);
        ediReader.setIncludeSyntaxCharacters(true);

        // X12 version 5010 and later having repetition separator in ISA11
        ediReader.parse(new InputSource(new StringReader("" +
                "ISA*00*          *00*          *ZZ*AAAA           *01*BBBB           *090825*0903*:*00501*000007629*0*T*>~\n" +
                "GS*SM*XXXXXXXXX*XXXX*20090825*0903*7629*X*005010~\n" +
                "ST*204*7629~\n" +
                "B2**XXXX**159771**PP~\n" +
                "B2A*00*LT~\n" +
                "SE*4*7629~\n" +
                "GE*1*7629~\n" +
                "IEA*1*000007629~\n")));
        assertEquals('*', ediReader.getDelimiter());
        assertEquals('>', ediReader.getSubDelimiter());
        assertEquals(':', ediReader.getRepetitionSeparator());
        assertEquals('~', ediReader.getTerminator());
        assertEquals("\n", ediReader.getTerminatorSuffix());
        assertEquals("~", contentHandler.getInterchangeAttributes().getValue("SegmentTerminator"));
        assertEquals("*", contentHandler.getInterchangeAttributes().getValue("ElementDelimiter"));
        assertEquals(">", contentHandler.getInterchangeAttributes().getValue("SubElementDelimiter"));
        assertEquals(":", contentHandler.getInterchangeAttributes().getValue("RepetitionSeparator"));

    }

    @Test
    public void isAwareOfSyntaxCharacters_X12_4010() throws IOException, SAXException {
        ediReader = new EDIReader();
        ediReader.setContentHandler(new MyContentHandler());

        // X12 version 4010 and earlier having standards Id, typically "U", in ISA11
        ediReader.parse(new InputSource(new StringReader("" +
                "ISA*00*          *00*          *ZZ*AAAA           *01*BBBB           *090825*0903*U*00401*000007629*0*T*>~\r\n" +
                "GS*SM*XXXXXXXXX*XXXX*20090825*0903*7629*X*004010~\r\n" +
                "ST*204*7629~\r\n" +
                "B2**XXXX**159771**PP~\r\n" +
                "B2A*00*LT~\r\n" +
                "SE*4*7629~\r\n" +
                "GE*1*7629~\r\n" +
                "IEA*1*000007629~\r\n")));
        assertEquals('*', ediReader.getDelimiter());
        assertEquals('>', ediReader.getSubDelimiter());
        assertEquals(0, ediReader.getRepetitionSeparator());
        assertEquals('~', ediReader.getTerminator());
        assertEquals("\r\n", ediReader.getTerminatorSuffix());
    }

    @Test
    public void isAwareOfSyntaxCharacters_EDIFACT_WithoutUNA() throws IOException, SAXException {
        ediReader = new EDIReader();
        ediReader.setContentHandler(new MyContentHandler());

        // EDIFACT without UNA segment
        ediReader.parse(new InputSource(new StringReader("" +
                "UNB+UNOA:1+005435656:1+006415160CFS:1+000210:1434+00000000000778+rref+aref+p+a+cid+t'" +
                "UNH+00000000000117+INVOIC:D:97B:UN'" +
                "BGM+380+342459+9'" +
                "DTM+3:20000202:102'" +
                "RFF+ON:608133'" +
                "NAD+BY+006415160::16++CUMMINS ENGINE'" +
                "NAD+SE+005435656::16++GENERAL WIDGET COMPANY'" +
                "CUX+1:USD'" +
                "LIN+1++157870:IN'" +
                "IMD+F++:::WIDGET'" +
                "QTY+47:1020:EA'" +
                "PRI+INV:1.179'" +
                "LIN+2++157871:IN'" +
                "IMD+F++:::DIFFERENT WIDGET'" +
                "QTY+47:20:BX'" +
                "PRI+INV:20.5'" +
                "UNT+16+00000000000117'" +
                "UNZ+1+00000000000778'"
        )));
        assertEquals('+', ediReader.getDelimiter());
        assertEquals(':', ediReader.getSubDelimiter());
        assertEquals('.', ediReader.getDecimalMark());
        assertEquals('?', ediReader.getReleaseCharacter());
        assertEquals(0, ediReader.getRepetitionSeparator());
        assertEquals('\'', ediReader.getTerminator());
        assertEquals("", ediReader.getTerminatorSuffix());
    }

    @Test
    public void isAwareOfSyntaxCharacters_EDIFACT_WithUNA() throws IOException, SAXException {
        ediReader = new EDIReader();
        ediReader.setContentHandler(new MyContentHandler());

        // EDIFACT with UNA segment
        ediReader.parse(new InputSource(new StringReader("" +
                "UNA:*.? '" +
                "UNB*UNOA:1*005435656:1*006415160CFS:1*000210:1434*00000000000778*rref*aref*p*a*cid*t'" +
                "UNH*00000000000117*INVOIC:D:97B:UN'" +
                "BGM*380*342459*9'" +
                "DTM*3:20000202:102'" +
                "RFF*ON:608133'" +
                "NAD*BY*006415160::16**CUMMINS ENGINE'" +
                "NAD*SE*005435656::16**GENERAL WIDGET COMPANY'" +
                "CUX*1:USD'" +
                "LIN*1**157870:IN'" +
                "IMD*F**:::WIDGET'" +
                "QTY*47:1020:EA'" +
                "PRI*INV:1.179'" +
                "LIN*2**157871:IN'" +
                "IMD*F**:::DIFFERENT WIDGET'" +
                "QTY*47:20:BX'" +
                "PRI*INV:20.5'" +
                "UNT*16*00000000000117'" +
                "UNZ*1*00000000000778'"
        )));
        assertEquals('*', ediReader.getDelimiter());
        assertEquals(':', ediReader.getSubDelimiter());
        assertEquals('.', ediReader.getDecimalMark());
        assertEquals('?', ediReader.getReleaseCharacter());
        assertEquals(0, ediReader.getRepetitionSeparator());
        assertEquals('\'', ediReader.getTerminator());
        assertEquals("", ediReader.getTerminatorSuffix());
    }

    @Test
    public void spacesOnlyElementsIgnoredByDefault() throws IOException, SAXException {
        ediReader = new EDIReader();
        MyContentHandler contentHandler = new MyContentHandler();
        ediReader.setContentHandler(contentHandler);
        ediReader.parse(new InputSource(new StringReader("" +
                "ISA*00*          *00*          *ZZ*AAAA           *01*BBBB           *090825*0903*:*00501*000007629*0*T*>~\n" +
                "GS*SM*XXXXXXXXX*XXXX*20090825*0903*7629*X*005010~\n" +
                "ST*204*7629~\n" +
                "B2*   *XXXX **159771**PP~\n" +
                "B2A*00*LT~\n" +
                "SE*4*7629~\n" +
                "GE*1*7629~\n" +
                "IEA*1*000007629~\n")));
        assertEquals(0, contentHandler.numberOfSpacesOnlyElements());

        // Now do it again, explicitly setting the default
        ediReader = new EDIReader();
        contentHandler = new MyContentHandler();
        ediReader.setContentHandler(contentHandler);
        ediReader.setIncludeSyntaxCharacters(true);
        ediReader.setKeepSpacesOnlyElements(false);
        ediReader.parse(new InputSource(new StringReader("" +
                "ISA*00*          *00*          *ZZ*AAAA           *01*BBBB           *090825*0903*:*00501*000007629*0*T*>~\n" +
                "GS*SM*XXXXXXXXX*XXXX*20090825*0903*7629*X*005010~\n" +
                "ST*204*7629~\n" +
                "B2*   *XXXX **159771**PP~\n" +
                "B2A*00*LT~\n" +
                "SE*4*7629~\n" +
                "GE*1*7629~\n" +
                "IEA*1*000007629~\n")));
        assertEquals(0, contentHandler.numberOfSpacesOnlyElements());

        // Now do it again, asking to keep the elements containing only spaces
        ediReader = new EDIReader();
        contentHandler = new MyContentHandler();
        ediReader.setContentHandler(contentHandler);
        ediReader.setIncludeSyntaxCharacters(true);
        ediReader.setKeepSpacesOnlyElements(true);
        ediReader.parse(new InputSource(new StringReader("" +
                "ISA*00*          *00*          *ZZ*AAAA           *01*BBBB           *090825*0903*:*00501*000007629*0*T*>~\n" +
                "GS*SM*XXXXXXXXX*XXXX*20090825*0903*7629*X*005010~\n" +
                "ST*204*7629~\n" +
                "B2*   *XXXX **159771**PP~\n" +
                "B2A*00*LT~\n" +
                "SE*4*7629~\n" +
                "GE*1*7629~\n" +
                "IEA*1*000007629~\n")));
        assertEquals(1, contentHandler.numberOfSpacesOnlyElements());
    }

    private static class MyContentHandler extends EDIReaderSAXAdapter {
        private int segmentCount, elementCount;
        private Attributes interchangeAttributes;
        private int spacesOnlyElements;

        public MyContentHandler() {
            super(new DefaultXMLTags());
        }

        @Override
        protected void beginInterchange(int charCount, int segmentCharCount, Attributes attributes) {
            interchangeAttributes = new EDIAttributes(attributes);
        }

        @Override
        protected void beginFirstSegment(Attributes atts) {
            segmentCount++;
        }

        @Override
        protected void beginAnotherSegment(Attributes atts) {
            segmentCount++;
        }

        @Override
        protected void beginBinaryPackage(Attributes atts) {
            segmentCount++;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            elementCount++;
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            String data = new String(ch, start, length);
            if (data.length() > 0 && data.trim().length() == 0) {
                spacesOnlyElements += 1;
            }
        }

        public int numberOfSpacesOnlyElements() {
            return spacesOnlyElements;
        }

        Attributes getInterchangeAttributes() {
            return interchangeAttributes;
        }

        int getSegmentCountWithoutSTandSE() {
            return segmentCount;
        }

        int getElementCount() {
            return elementCount;
        }
    }
}