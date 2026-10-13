package com.berryworks.edireader.tradacoms;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.demo.EDItoXML;
import com.berryworks.edireader.tokenizer.Token;
import com.berryworks.edireader.tokenizer.Tokenizer;
import com.berryworks.edireader.util.FileUtil;
import com.berryworks.edireader.util.PathFinder;
import com.berryworks.edireader.util.dom.DocumentUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.Stack;

import static org.junit.Assert.*;

public class TradacomsReaderTest {

    public static final String ACKHDR_2ACKMNT = "" +
            "STX=ANA:1+5050085515550:HARPERCOLLINS PUBLISHERS LIMITED+5030670169474:BOOKS ETC LTD+190924:142655+000000046++ACKHDR+B'" +
            "MHD=1+ACKHDR:4'" +
            "TYP=ACKMNT+4'FIL=46+1+190924'" +
            "MTR=4'" +
            "MHD=2+ACKMNT:4'" +
            "CLO=5030670172566::0172561+BOOKS ETC LTD+:UNIT 4, GRESHAM INDUSTRIAL ESTATE:ALDERSHOT:GB:GU12 4YD'" +
            "AOR=4333981:001772571299:190821:190821'" +
            "ALD=1+9780008300401:0008300402+++1+1+0+40749+CARIBBEAN WALL MAP MFO'AGD=1+1+1'" +
            "DNB=1+1+54:IP+082:4333981'DNB=1+2+55:01'" +
            "MTR=8'" +
            "MHD=3+ACKMNT:4'" +
            "CLO=5030670172566::0172561+BOOKS ETC LTD+:UNIT 4, GRESHAM INDUSTRIAL ESTATE:ALDERSHOT:GB:GU12 4YD'" +
            "AOR=4333989:001772571385:190821:190821'" +
            "ALD=1+9780008283179:0008283176+++1+1+0+45849+ASK POLICEMAN PB:HELEN SIMPSON'" +
            "AGD=1+1+1'" +
            "DNB=1+1+54:IP+082:4333989'" +
            "DNB=1+2+55:01'" +
            "MTR=8'" +
            "END=3'";
    public static final String ACKHDR_1ACKMNT = "" +
            "STX=ANA:1+5050085515550:HARPERCOLLINS PUBLISHERS LIMITED+5030670169474:BOOKS ETC LTD+190924:142655+000000046++ACKHDR+B'" +
            "MHD=1+ACKHDR:4'" +
            "TYP=ACKMNT+4'FIL=46+1+190924'" +
            "MTR=4'" +
            "MHD=2+ACKMNT:4'" +
            "CLO=5030670172566::0172561+BOOKS ETC LTD+:UNIT 4, GRESHAM INDUSTRIAL ESTATE:ALDERSHOT:GB:GU12 4YD'" +
            "AOR=4333981:001772571299:190821:190821'" +
            "ALD=1+9780008300401:0008300402+++1+1+0+40749+CARIBBEAN WALL MAP MFO'AGD=1+1+1'" +
            "DNB=1+1+54:IP+082:4333981'DNB=1+2+55:01'" +
            "MTR=8'" +
            "END=2'";
    private static String prefix;
    private EDIReader tradacomsReader;

    @Before
    public void setUp() {
        prefix = PathFinder.confirmDirectory("edireader", "src/test/resources/tradacoms/");
        tradacomsReader = new TradacomsReader();
    }

    @Test
    public void canRecognizeSegments() throws IOException, SAXException {
        InputSource inputSource = new InputSource(new FileReader(prefix + "ORDHDR.edi"));
        tradacomsReader = EDIReaderFactory.createEDIReader(inputSource);
        assertTrue(tradacomsReader instanceof TradacomsReader);

        // This configuring of the tokenizer is normally done in the parse() method
        // but we are not calling parse() in this test.
        Tokenizer tokenizer = tradacomsReader.getTokenizer();
        tokenizer.setDelimiter(tradacomsReader.getDelimiter());
        tokenizer.setSubDelimiter(tradacomsReader.getSubDelimiter());
        tokenizer.setRelease(tradacomsReader.getRelease());
        tokenizer.setRepetitionSeparator(tradacomsReader.getRepetitionSeparator());
        tokenizer.setTerminator(tradacomsReader.getTerminator());

        assertEquals('=', tokenizer.getDelimiter());
        assertEquals(':', tokenizer.getSubDelimiter());
        assertEquals('\'', tokenizer.getTerminator());

        String segmentType;
        segmentType = tokenizer.nextSegment();
        assertEquals("STX", segmentType);

        segmentType = tokenizer.nextSegment();
        assertEquals("MHD", segmentType);

        segmentType = tokenizer.nextSegment();
        assertEquals("TYP", segmentType);

        segmentType = tokenizer.nextSegment();
        assertEquals("SDT", segmentType);
    }

    @Test
    public void canRecognizeElementsWithinSegment() throws IOException, SAXException {
        InputSource inputSource = new InputSource(new FileReader(prefix + "ORDHDR.edi"));
        tradacomsReader = EDIReaderFactory.createEDIReader(inputSource);

        // This configuring of the tokenizer is normally done in the parse() method
        // but we are not calling parse() in this test.
        Tokenizer tokenizer = tradacomsReader.getTokenizer();
        tokenizer.setDelimiter(tradacomsReader.getDelimiter());
        tokenizer.setSubDelimiter(tradacomsReader.getSubDelimiter());
        tokenizer.setRelease(tradacomsReader.getRelease());
        tokenizer.setRepetitionSeparator(tradacomsReader.getRepetitionSeparator());
        tokenizer.setTerminator(tradacomsReader.getTerminator());

        assertEquals("STX", tokenizer.nextSegment());
        assertEquals("MHD", tokenizer.nextSegment());
        assertEquals("TYP", tokenizer.nextSegment());

        // Now switch delimiters
        tokenizer.setDelimiter('+');
        Token token;

        token = tokenizer.nextToken();
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("0430", token.getValue());

        token = tokenizer.nextToken();
        assertEquals(Token.TokenType.SIMPLE, token.getType());
        assertEquals("NEW-ORDERS", token.getValue());

        token = tokenizer.nextToken();
        assertEquals(Token.TokenType.SEGMENT_END, token.getType());

        tokenizer.setDelimiter('=');
        assertEquals("SDT", tokenizer.nextSegment());
    }

    @Test
    public void canBuildDocumentFromEdi() throws Exception {
        InputSource inputSource = new InputSource(new FileReader(prefix + "ORDHDR.edi"));
        Document document = DocumentUtil.getInstance().buildDocumentFromEdi(inputSource);
    }

    @Test
    public void canBuildInvoiceDocumentFromEdi() throws Exception {
        InputSource inputSource = new InputSource(new FileReader(prefix + "INVOIC-2.edi"));
        DocumentUtil util = DocumentUtil.getInstance();
        Document document = util.buildDocumentFromEdi(inputSource);
        StringWriter writer = new StringWriter();
        util.writeXML(document, writer);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
                        "<ediroot>" +
                        "<interchange ApplRef=\"INVFIL\" Control=\"620456\" Date=\"190604\" Priority=\"B\" Standard=\"TRADACOMS\" SyntaxId=\"ANA\" SyntaxVersion=\"1\" Time=\"080244\">" +
                        "<sender><address Id=\"5050085059689\" Qual=\"HARPERCOLLINS PUBLISHERS LIMITED\"/></sender>" +
                        "<receiver><address Id=\"9999999999999\" Qual=\"TRADINGPARTNER\"/></receiver>" +
                        "<group>" +
                        "<transaction DocType=\"INVFIL\" Version=\"9\">" +
                        "<loop Id=\"INVFIL\">" +
                        "<segment Id=\"TYP\"><element Id=\"TYP01\">0700</element></segment>" +
                        "<segment Id=\"SDT\"><element Id=\"SDT01\">5050085059689</element><element Id=\"SDT02\">HARPERCOLLINS PUBLISHERS LIMITED</element><element Composite=\"yes\" Id=\"SDT03\"><subelement Sequence=\"1\">WESTERHILL ROAD</subelement><subelement Sequence=\"2\">GLASGOW</subelement><subelement Sequence=\"5\">G64 2QT</subelement></element><element Id=\"SDT04\">259639706</element></segment><segment Id=\"CDT\"><element Id=\"CDT01\">9999999999999</element><element Id=\"CDT02\">TRADINGPARTNER</element><element Composite=\"yes\" Id=\"CDT03\"><subelement Sequence=\"1\">A</subelement><subelement Sequence=\"2\">B</subelement><subelement Sequence=\"3\">C</subelement><subelement Sequence=\"5\">D</subelement></element></segment>" +
                        "<segment Id=\"FIL\"><element Id=\"FIL01\">458</element><element Id=\"FIL02\">1</element><element Id=\"FIL03\">190604</element></segment><segment Id=\"FDT\"><element Id=\"FDT01\">190604</element><element Id=\"FDT02\">190604</element></segment>" +
                        "</loop>" +
                        "<loop Id=\"INVOIC\"><segment Id=\"CLO\"><element Composite=\"yes\" Id=\"CLO01\"><subelement Sequence=\"1\">9999999999999</subelement><subelement Sequence=\"3\">8888888</subelement></element><element Id=\"CLO02\">TRADINGPARTNER</element><element Composite=\"yes\" Id=\"CLO03\"><subelement Sequence=\"1\">A</subelement><subelement Sequence=\"2\">B</subelement><subelement Sequence=\"3\">C</subelement><subelement Sequence=\"4\">UNITED KINGDOM</subelement><subelement Sequence=\"5\">D</subelement></element></segment>" +
                        "<segment Id=\"IRF\"><element Id=\"IRF01\">104502769</element><element Id=\"IRF02\">190604</element><element Id=\"IRF03\">190604</element></segment><segment Id=\"PYT\"><element Id=\"PYT01\">1</element><element Id=\"PYT02\">60 DAYS MONTH END</element><element Composite=\"yes\" Id=\"PYT03\"><subelement Sequence=\"1\">190831</subelement><subelement Sequence=\"2\">0000</subelement></element><element Id=\"PYT04\">88</element></segment><segment Id=\"DNA\"><element Id=\"DNA01\">1</element><element Composite=\"yes\" Id=\"DNA03\"><subelement Sequence=\"1\">073</subelement><subelement Sequence=\"2\">GBP</subelement><subelement Sequence=\"3\">979</subelement><subelement Sequence=\"4\">INVN</subelement></element></segment><segment Id=\"ODD\"><element Id=\"ODD01\">1</element><element Composite=\"yes\" Id=\"ODD02\"><subelement Sequence=\"1\">6788259</subelement><subelement Sequence=\"2\">001661470157</subelement><subelement Sequence=\"3\">190604</subelement><subelement Sequence=\"4\">190604</subelement></element><element Composite=\"yes\" Id=\"ODD03\"><subelement Sequence=\"1\">104502769</subelement><subelement Sequence=\"2\">190604</subelement></element><element Id=\"ODD04\">1</element></segment><segment Id=\"ILD\"><element Id=\"ILD01\">1</element><element Id=\"ILD02\">1</element><element Composite=\"yes\" Id=\"ILD03\"><subelement Sequence=\"1\">9780061934711</subelement><subelement Sequence=\"2\">0061934712</subelement></element><element Composite=\"yes\" Id=\"ILD06\"><subelement Sequence=\"1\">1</subelement><subelement Sequence=\"2\">0272</subelement><subelement Sequence=\"3\">KG</subelement></element><element Composite=\"yes\" Id=\"ILD07\"><subelement Sequence=\"1\">1</subelement><subelement Sequence=\"2\">0272</subelement><subelement Sequence=\"3\">KG</subelement></element><element Id=\"ILD08\">38445</element><element Id=\"ILD09\">38400</element><element Id=\"ILD10\">Z</element><element Id=\"ILD11\">0000</element><element Composite=\"yes\" Id=\"ILD14\"><subelement Sequence=\"1\">FLETCHER, DONNA</subelement><subelement Sequence=\"2\">BOUND TO A WARRIOR PB</subelement></element><element Id=\"ILD15\">69900</element><element Id=\"ILD17\">69900</element><element Id=\"ILD18\">31500</element><element Id=\"ILD19\">45000</element></segment>" +
                        "<segment Id=\"DNC\"><element Id=\"DNC01\">1</element><element Id=\"DNC02\">1</element><element Id=\"DNC03\">1</element><element Composite=\"yes\" Id=\"DNC05\"><subelement Sequence=\"1\">082</subelement><subelement Sequence=\"2\">6788259/00001</subelement></element></segment><segment Id=\"STL\"><element Id=\"STL01\">1</element><element Id=\"STL02\">Z</element><element Id=\"STL03\">0000</element><element Id=\"STL04\">1</element><element Id=\"STL05\">384</element><element Id=\"STL10\">384</element><element Id=\"STL12\">384</element><element Id=\"STL13\">000</element><element Id=\"STL14\">384</element><element Id=\"STL15\">384</element></segment><segment Id=\"TLR\"><element Id=\"TLR01\">1</element><element Id=\"TLR02\">384</element><element Id=\"TLR07\">384</element><element Id=\"TLR09\">384</element><element Id=\"TLR10\">000</element><element Id=\"TLR11\">384</element><element Id=\"TLR12\">384</element></segment>" +
                        "</loop>" +
                        "<loop Id=\"VATTLR\">" +
                        "<segment Id=\"VRS\"><element Id=\"VRS01\">1</element><element Id=\"VRS02\">Z</element><element Id=\"VRS03\">0000</element><element Id=\"VRS04\">384</element><element Id=\"VRS05\">384</element><element Id=\"VRS06\">000</element><element Id=\"VRS07\">384</element><element Id=\"VRS08\">384</element></segment>" +
                        "</loop>" +
                        "<loop Id=\"INVTLR\"><segment Id=\"TOT\"><element Id=\"TOT01\">384</element><element Id=\"TOT02\">384</element><element Id=\"TOT03\">000</element><element Id=\"TOT04\">384</element><element Id=\"TOT05\">384</element><element Id=\"TOT06\">1</element></segment>" +
                        "</loop>" +
                        "</transaction>" +
                        "</group>" +
                        "</interchange>" +
                        "</ediroot>",
                writer.toString());
    }

    @Test
    public void detectCountErrorInMTR() throws Exception {
        InputSource inputSource = new InputSource(new FileReader(prefix + "Error-MTR-count.edi"));
        try {
            Document document = DocumentUtil.getInstance().buildDocumentFromEdi(inputSource);
            fail();
        } catch (Exception e) {
            assertEquals("Segment count error in MTR segment. Expected 6 instead of 66 at segment 7, field 2",
                    e.getCause().getMessage());
        }
    }

    @Test
    public void detectCountErrorInEND() throws Exception {
        InputSource inputSource = new InputSource(new FileReader(prefix + "Error-END-count.edi"));
        try {
            Document document = DocumentUtil.getInstance().buildDocumentFromEdi(inputSource);
            fail();
        } catch (Exception e) {
            assertEquals("Message count error in END segment. Expected 3 instead of 33 at segment 18, field 2",
                    e.getCause().getMessage());
        }
    }

    @Test
    public void detectSequenceErrorInMHD() throws Exception {
        InputSource inputSource = new InputSource(new FileReader(prefix + "Error-MHD-sequence.edi"));
        try {
            Document document = DocumentUtil.getInstance().buildDocumentFromEdi(inputSource);
            fail();
        } catch (Exception e) {
            assertEquals("Message sequence error in MHD segment. Expected 3 instead of 2 at segment 24, field 2",
                    e.getCause().getMessage());
        }
    }

    @Test
    public void detectControlNumberErrorInRSG() throws Exception {
        InputSource inputSource = new InputSource(new FileReader(prefix + "Error-RSG-ControlNumber-match.edi"));
        try {
            Document document = DocumentUtil.getInstance().buildDocumentFromEdi(inputSource);
            fail();
        } catch (Exception e) {
            assertEquals("Sender's transmission reference error in RSG reconciliation segment. Expected 100052 instead of 200052 at segment 31, field 2",
                    e.getCause().getMessage());
        }
    }

    @Test
    public void detectToAdressErrorInRSG() throws Exception {
        InputSource inputSource = new InputSource(new FileReader(prefix + "Error-RSG-Recipient-match.edi"));
        try {
            Document document = DocumentUtil.getInstance().buildDocumentFromEdi(inputSource);
            fail();
        } catch (Exception e) {
            assertEquals("Recipient error in RSG reconciliation segment. Expected 5030670123456 instead of 55030670123456 at segment 31, field 3",
                    e.getCause().getMessage());
        }
    }

    @Test
    public void testORDHDRtoXML() throws IOException {
        Reader ediInput = new FileReader(prefix + "ORDHDR.edi");
        StringWriter outputWriter = new StringWriter();
        EDItoXML ediToXML = new EDItoXML();
        ediToXML.setInputReader(ediInput);
        ediToXML.setXmlOutputWriter(outputWriter);
        ediToXML.setIndent(true);
        ediToXML.run();

        String xmlText = outputWriter.toString();
        String expectedXml = FileUtil.fileToString(prefix + "ORDHDR.xml");
        assertEquals(expectedXml, xmlText);
    }

    @Test
    public void testORDHDRwithContentHandler() throws IOException, SAXException {
        Reader ediInput = new FileReader(prefix + "ORDHDR.edi");
        TradacomsReader tradacomsReader = new TradacomsReader();
        tradacomsReader.setContentHandler(new MyHandlerWithStack());
        tradacomsReader.parse(ediInput);
    }

    @Test
    public void testACKHDRwithContentHandler() throws IOException, SAXException {
        Reader ediInput = new StringReader(ACKHDR_2ACKMNT);
        TradacomsReader tradacomsReader = new TradacomsReader();
        tradacomsReader.setContentHandler(new MyHandlerWithStack());
        tradacomsReader.parse(ediInput);
    }

    @Test
    public void testPPRHDRtoXML() throws IOException {
        Reader ediInput = new FileReader(prefix + "PPRHDR.edi");
        StringWriter outputWriter = new StringWriter();
        EDItoXML ediToXML = new EDItoXML();
        ediToXML.setInputReader(ediInput);
        ediToXML.setXmlOutputWriter(outputWriter);
        ediToXML.setIndent(true);
        ediToXML.run();

        String xmlText = outputWriter.toString();
        String expectedXml = FileUtil.fileToString(prefix + "PPRHDR.xml");
        assertEquals(expectedXml, xmlText);
    }

    @Ignore
    @Test
    public void testACKHDRtoXML() throws IOException {
        Reader ediInput = new FileReader(prefix + "ACKHDR.edi");
        StringWriter outputWriter = new StringWriter();
        EDItoXML ediToXML = new EDItoXML();
        ediToXML.setInputReader(ediInput);
        ediToXML.setXmlOutputWriter(outputWriter);
        ediToXML.setIndent(true);
        ediToXML.run();

        String xmlText = outputWriter.toString();
        String expectedXml = FileUtil.fileToString(prefix + "ACKHDR.xml");
        assertEquals(expectedXml, xmlText);
    }

    private String findResourcesDirectory() {
        String userDir = System.getProperty("user.dir");
        String pathToResourceDirectory = userDir + "/src/test/resources";
        File resourceDirectory = new File(pathToResourceDirectory);
        assertTrue(resourceDirectory.exists() && resourceDirectory.isDirectory());
        return pathToResourceDirectory;
    }

    private class MyHandlerWithStack extends DefaultHandler {
        private Stack<String> stack = new Stack<>();

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            String id = attributes.getValue("Id");
            stack.push(localName + (id == null ? "" : " " + id));
            if (isOfInterest(localName)) {
                System.out.println("Push " + localName + " " + id);
            }
        }

        private boolean isOfInterest(String localName) {
            return false;
//            return "loop".equals(localName); // || "segment".equals(localName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            String peek = stack.peek();
            if (isOfInterest(localName)) {
                System.out.println(" Pop " + peek);
            }
            if (!peek.startsWith(localName)) {
                throw new RuntimeException("Stack problem. Ready to pop " + localName + " but the stack has " + peek);
            }
            stack.pop();
        }
    }
}
