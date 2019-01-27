package com.berryworks.edireader;

import org.junit.Test;
import org.xml.sax.*;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

public class EDIReaderTest {

    EDIReader ediReader;

    @Test
    public void isAwareOfSyntaxCharacters_X12_5010() throws IOException, SAXException {
        ediReader = new EDIReader();
        ediReader.setContentHandler(new MyContentHandler());

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

    private class MyContentHandler implements ContentHandler {
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
        public void startPrefixMapping(String prefix, String uri) throws SAXException {
        }

        @Override
        public void endPrefixMapping(String prefix) throws SAXException {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }

        @Override
        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        }

        @Override
        public void processingInstruction(String target, String data) throws SAXException {
        }

        @Override
        public void skippedEntity(String name) throws SAXException {
        }
    }
}