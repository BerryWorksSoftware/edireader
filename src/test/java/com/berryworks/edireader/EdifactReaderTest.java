package com.berryworks.edireader;

import com.berryworks.edireader.benchmark.EDITestData;
import com.berryworks.edireader.error.*;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static com.berryworks.edireader.util.Conversion.ediToxml;
import static org.junit.Assert.*;

public class EdifactReaderTest {

    public static final String EDIFACT_WITH_GROUP = "UNB+IATA:1+REUAIR08DLH:PIMA+REUAGT82AGENT/LHR01:PIMA+941027:1520+841F60UNZ+RREF+APR+L+1'"
            + "UNG+INVOIC+LOCK:02+CBP-ACE-TEST:02+041013:1901+16+UN+D:98A:'"
            + "UNH+1+DCQCKI:90:1:IA+841F60'"
            + "LOR+SR:GVA'"
            + "UNT+3+1'"
            + "UNE+1+16'"
            + "UNZ+1+841F60UNZ+1+30077'";
    private EdifactReader edifactReader;

    @Test
    public void producesXmlForSimpleCase() throws IOException, SAXException, TransformerException {
        edifactReader = new EdifactReader();
        StringReader reader = new StringReader(
                "UNB+IATA:1+REUAIR08DLH:PIMA+REUAGT82AGENT/LHR01:PIMA+941027:1520+841F60UNZ+RREF+APR"
                        + "+L+1'"
                        + "UNH+1+DCQCKI:90:1:IA+841F60'"
                        + "LOR+SR:GVA'"
                        + "FDQ+DL+573+890701+ATL+MIA++SR+120+8907011300+8907011655+ZRH+ATL'"
                        + "PPD+MEIER+F:Y++BARBARAMRS+MILLER:JOHN'"
                        + "PRD+Y'"
                        + "PSD+N'"
                        + "PBD+2:22'"
                        + "UNT+8+1'"
                        + "UNZ+1+841F60UNZ+1+30077'");
        StringWriter writer = new StringWriter();
        ediToxml(reader, writer, edifactReader);
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<ediroot>" +
                        "<interchange Standard=\"EDIFACT\" SyntaxId=\"IATA\" SyntaxVersion=\"1\" Date=\"941027\" Time=\"1520\" Control=\"841F60UNZ\" RecipientRef=\"RREF\" ApplRef=\"APR\" Priority=\"L\" AckRequest=\"1\" Decimal=\".\">" +
                        "<sender><address Id=\"REUAIR08DLH\" Qual=\"PIMA\"/></sender>" +
                        "<receiver><address Id=\"REUAGT82AGENT/LHR01\" Qual=\"PIMA\"/></receiver>" +
                        "<group>" +
                        "<transaction Control=\"1\" DocType=\"DCQCKI\" Version=\"90\" Release=\"1\" Agency=\"IA\" AccessReference=\"841F60\">" +
                        "<segment Id=\"LOR\"><element Id=\"LOR01\" Composite=\"yes\"><subelement Sequence=\"1\">SR</subelement><subelement Sequence=\"2\">GVA</subelement></element></segment>" +
                        "<segment Id=\"FDQ\"><element Id=\"FDQ01\">DL</element><element Id=\"FDQ02\">573</element><element Id=\"FDQ03\">890701</element><element Id=\"FDQ04\">ATL</element><element Id=\"FDQ05\">MIA</element><element Id=\"FDQ07\">SR</element><element Id=\"FDQ08\">120</element><element Id=\"FDQ09\">8907011300</element><element Id=\"FDQ10\">8907011655</element><element Id=\"FDQ11\">ZRH</element><element Id=\"FDQ12\">ATL</element></segment>" +
                        "<segment Id=\"PPD\"><element Id=\"PPD01\">MEIER</element><element Id=\"PPD02\" Composite=\"yes\"><subelement Sequence=\"1\">F</subelement><subelement Sequence=\"2\">Y</subelement></element><element Id=\"PPD04\">BARBARAMRS</element><element Id=\"PPD05\" Composite=\"yes\"><subelement Sequence=\"1\">MILLER</subelement><subelement Sequence=\"2\">JOHN</subelement></element></segment>" +
                        "<segment Id=\"PRD\"><element Id=\"PRD01\">Y</element></segment>" +
                        "<segment Id=\"PSD\"><element Id=\"PSD01\">N</element></segment>" +
                        "<segment Id=\"PBD\"><element Id=\"PBD01\" Composite=\"yes\"><subelement Sequence=\"1\">2</subelement><subelement Sequence=\"2\">22</subelement></element></segment>" +
                        "</transaction>" +
                        "</group>" +
                        "</interchange>" +
                        "</ediroot>",
                writer.toString());
    }

    @Test
    public void producesXmlForAnORDRSP_EANCOM_example() throws IOException, SAXException, TransformerException {
        edifactReader = new EdifactReader();
        StringReader reader = new StringReader(
                "UNB+UNOB:1+SENDER1:16:ZZUK+RECEIVER1:01:ZZUK+071101:1701+131++ORDRSP++1++1'\n" +
                        "UNH+ME000001+ORDRSP:D:01B:UN:EAN009'\n" +
                        "BGM+231+ORSP12856+4'\n" +
                        "DTM+137:20020330:102'\n" +
                        "RFF+ON:652744'\n" +
                        "DTM+171:20020325:102'\n" +
                        "NAD+BY+5412345000013::9'\n" +
                        "RFF+VA:452282'\n" +
                        "NAD+SU+4012345500004::9'\n" +
                        "RFF+VA:87765432'\n" +
                        "LIN+1+5+3312345501102:SRV'\n" +
                        "LIN+2+3+3312345501003:SRV'\n" +
                        "PIA+1+ABC1234:SA'\n" +
                        "IMD+C++TU::9'\n" +
                        "QTY+21:48'\n" +
                        "DTM+2:20020910:102'\n" +
                        "MOA+203:26400'\n" +
                        "PRI+AAA:550:CT:AAA'\n" +
                        "PAC+4+1+CS'\n" +
                        "TAX+7+VAT+++:::17.5+S'\n" +
                        "MOA+124:4620'\n" +
                        "TDT+20++30++31'\n" +
                        "LIN+3+7+3312345501096:SRV'\n" +
                        "UNS+S'\n" +
                        "CNT+2:3'\n" +
                        "UNT+25+ME000001'\n" +
                        "UNZ+1+131'");
        StringWriter writer = new StringWriter();
        ediToxml(reader, writer, edifactReader);
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<ediroot>" +
                        "<interchange Standard=\"EDIFACT\" SyntaxId=\"UNOB\" SyntaxVersion=\"1\" Date=\"071101\" Time=\"1701\" Control=\"131\" ApplRef=\"ORDRSP\" AckRequest=\"1\" TestIndicator=\"1\" Decimal=\".\">" +
                        "<sender><address Id=\"SENDER1\" Qual=\"16\" Extra=\"ZZUK\"/></sender>" +
                        "<receiver><address Id=\"RECEIVER1\" Qual=\"01\" Extra=\"ZZUK\"/></receiver>" +
                        "<group>" +
                        "<transaction Control=\"ME000001\" DocType=\"ORDRSP\" Version=\"D\" Release=\"01B\" Agency=\"UN\" Association=\"EAN009\">" +
                        "<segment Id=\"BGM\"><element Id=\"BGM01\">231</element><element Id=\"BGM02\">ORSP12856</element><element Id=\"BGM03\">4</element></segment>" +
                        "<segment Id=\"DTM\"><element Id=\"DTM01\" Composite=\"yes\"><subelement Sequence=\"1\">137</subelement><subelement Sequence=\"2\">20020330</subelement><subelement Sequence=\"3\">102</subelement></element></segment>" +
                        "<segment Id=\"RFF\"><element Id=\"RFF01\" Composite=\"yes\"><subelement Sequence=\"1\">ON</subelement><subelement Sequence=\"2\">652744</subelement></element></segment>" +
                        "<segment Id=\"DTM\"><element Id=\"DTM01\" Composite=\"yes\"><subelement Sequence=\"1\">171</subelement><subelement Sequence=\"2\">20020325</subelement><subelement Sequence=\"3\">102</subelement></element></segment>" +
                        "<segment Id=\"NAD\"><element Id=\"NAD01\">BY</element><element Id=\"NAD02\" Composite=\"yes\"><subelement Sequence=\"1\">5412345000013</subelement><subelement Sequence=\"3\">9</subelement></element></segment>" +
                        "<segment Id=\"RFF\"><element Id=\"RFF01\" Composite=\"yes\"><subelement Sequence=\"1\">VA</subelement><subelement Sequence=\"2\">452282</subelement></element></segment>" +
                        "<segment Id=\"NAD\"><element Id=\"NAD01\">SU</element><element Id=\"NAD02\" Composite=\"yes\"><subelement Sequence=\"1\">4012345500004</subelement><subelement Sequence=\"3\">9</subelement></element></segment>" +
                        "<segment Id=\"RFF\"><element Id=\"RFF01\" Composite=\"yes\"><subelement Sequence=\"1\">VA</subelement><subelement Sequence=\"2\">87765432</subelement></element></segment>" +
                        "<segment Id=\"LIN\"><element Id=\"LIN01\">1</element><element Id=\"LIN02\">5</element><element Id=\"LIN03\" Composite=\"yes\"><subelement Sequence=\"1\">3312345501102</subelement><subelement Sequence=\"2\">SRV</subelement></element></segment>" +
                        "<segment Id=\"LIN\"><element Id=\"LIN01\">2</element><element Id=\"LIN02\">3</element><element Id=\"LIN03\" Composite=\"yes\"><subelement Sequence=\"1\">3312345501003</subelement><subelement Sequence=\"2\">SRV</subelement></element></segment>" +
                        "<segment Id=\"PIA\"><element Id=\"PIA01\">1</element><element Id=\"PIA02\" Composite=\"yes\"><subelement Sequence=\"1\">ABC1234</subelement><subelement Sequence=\"2\">SA</subelement></element></segment>" +
                        "<segment Id=\"IMD\"><element Id=\"IMD01\">C</element><element Id=\"IMD03\" Composite=\"yes\"><subelement Sequence=\"1\">TU</subelement><subelement Sequence=\"3\">9</subelement></element></segment>" +
                        "<segment Id=\"QTY\"><element Id=\"QTY01\" Composite=\"yes\"><subelement Sequence=\"1\">21</subelement><subelement Sequence=\"2\">48</subelement></element></segment>" +
                        "<segment Id=\"DTM\"><element Id=\"DTM01\" Composite=\"yes\"><subelement Sequence=\"1\">2</subelement><subelement Sequence=\"2\">20020910</subelement><subelement Sequence=\"3\">102</subelement></element></segment>" +
                        "<segment Id=\"MOA\"><element Id=\"MOA01\" Composite=\"yes\"><subelement Sequence=\"1\">203</subelement><subelement Sequence=\"2\">26400</subelement></element></segment>" +
                        "<segment Id=\"PRI\"><element Id=\"PRI01\" Composite=\"yes\"><subelement Sequence=\"1\">AAA</subelement><subelement Sequence=\"2\">550</subelement><subelement Sequence=\"3\">CT</subelement><subelement Sequence=\"4\">AAA</subelement></element></segment>" +
                        "<segment Id=\"PAC\"><element Id=\"PAC01\">4</element><element Id=\"PAC02\">1</element><element Id=\"PAC03\">CS</element></segment>" +
                        "<segment Id=\"TAX\"><element Id=\"TAX01\">7</element><element Id=\"TAX02\">VAT</element><element Id=\"TAX05\" Composite=\"yes\"><subelement Sequence=\"4\">17.5</subelement></element><element Id=\"TAX06\">S</element></segment>" +
                        "<segment Id=\"MOA\"><element Id=\"MOA01\" Composite=\"yes\"><subelement Sequence=\"1\">124</subelement><subelement Sequence=\"2\">4620</subelement></element></segment>" +
                        "<segment Id=\"TDT\"><element Id=\"TDT01\">20</element><element Id=\"TDT03\">30</element><element Id=\"TDT05\">31</element></segment>" +
                        "<segment Id=\"LIN\"><element Id=\"LIN01\">3</element><element Id=\"LIN02\">7</element><element Id=\"LIN03\" Composite=\"yes\"><subelement Sequence=\"1\">3312345501096</subelement><subelement Sequence=\"2\">SRV</subelement></element></segment>" +
                        "<segment Id=\"UNS\"><element Id=\"UNS01\">S</element></segment>" +
                        "<segment Id=\"CNT\"><element Id=\"CNT01\" Composite=\"yes\"><subelement Sequence=\"1\">2</subelement><subelement Sequence=\"2\">3</subelement></element></segment>" +
                        "</transaction>" +
                        "</group>" +
                        "</interchange>" +
                        "</ediroot>",
                writer.toString());
    }

    @Test
    public void acceptsAnyLetterAsSyntaxIdentifierInUNO() throws IOException, SAXException, TransformerException {
        edifactReader = new EdifactReader();
        StringReader reader = new StringReader(
                "UNB+UNOL:1+SENDER1:16:ZZUK+RECEIVER1:01:ZZUK+071101:1701+131++ORDRSP++1++1'\n" +
                        "UNH+ME000001+ORDRSP:D:01B:UN:EAN009'\n" +
                        "BGM+231+ORSP12856+4'\n" +
                        "UNT+3+ME000001'\n" +
                        "UNZ+1+131'");
        StringWriter writer = new StringWriter();
        ediToxml(reader, writer, edifactReader);
        assertTrue(writer.toString().startsWith(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<ediroot>" +
                        "<interchange Standard=\"EDIFACT\" SyntaxId=\"UNOL\" SyntaxVersion=\"1\""));
    }

    @Test
    public void producesXmlForFunctionalGroup() throws IOException, SAXException, TransformerException {
        edifactReader = new EdifactReader();
        StringReader ediInput = new StringReader(EDIFACT_WITH_GROUP);
        StringWriter writer = new StringWriter();
        ediToxml(ediInput, writer, edifactReader);
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<ediroot>" +
                        "<interchange Standard=\"EDIFACT\" SyntaxId=\"IATA\" SyntaxVersion=\"1\" Date=\"941027\" Time=\"1520\" Control=\"841F60UNZ\" RecipientRef=\"RREF\" ApplRef=\"APR\" Priority=\"L\" AckRequest=\"1\" Decimal=\".\">" +
                        "<sender><address Id=\"REUAIR08DLH\" Qual=\"PIMA\"/></sender>" +
                        "<receiver><address Id=\"REUAGT82AGENT/LHR01\" Qual=\"PIMA\"/></receiver>" +
                        "<group GroupType=\"INVOIC\" ApplSender=\"LOCK\" ApplSenderQual=\"02\" ApplReceiver=\"CBP-ACE-TEST\" ApplReceiverQual=\"02\" Date=\"041013\" Time=\"1901\" Control=\"16\" StandardCode=\"UN\" StandardVersion=\"D98A\">" +
                        "<transaction Control=\"1\" DocType=\"DCQCKI\" Version=\"90\" Release=\"1\" Agency=\"IA\" AccessReference=\"841F60\">" +
                        "<segment Id=\"LOR\"><element Id=\"LOR01\" Composite=\"yes\"><subelement Sequence=\"1\">SR</subelement><subelement Sequence=\"2\">GVA</subelement></element></segment>" +
                        "</transaction>" +
                        "</group>" +
                        "</interchange>" +
                        "</ediroot>",
                writer.toString());
    }

    @Test
    public void detectsGroupCountError() throws IOException, SAXException {
        String ediText = EDITestData.getEdifactInterchange().replace("UNZ+1+", "UNZ+11+");
        edifactReader = new EdifactReader();
        edifactReader.setContentHandler(new DefaultHandler());
        try {
            edifactReader.parse(new StringReader(ediText));
            fail("Functional group count error not detected");
        } catch (GroupCountException e) {
            assertEquals("Functional group count error in UNZ segment. Expected 1 instead of 11 at segment 10, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsInterchangeControlNumberError() throws IOException, SAXException {
        String ediText = EDITestData.getEdifactInterchange().replace("UNZ+1+841F60UNZ", "UNZ+1+not841F60UNZ");
        edifactReader = new EdifactReader();
        edifactReader.setContentHandler(new DefaultHandler());
        try {
            edifactReader.parse(new StringReader(ediText));
            fail("Interchange control number error not detected");
        } catch (InterchangeControlNumberException e) {
            assertEquals(
                    "Control number error in UNZ segment. Expected 841F60UNZ instead of not841F60UNZ at segment 10, field 3",
                    e.getMessage());
        }
    }

    @Test
    public void detectsTransactionCountError() throws IOException, SAXException {
        String ediText = EDIFACT_WITH_GROUP.replace("UNE+1+", "UNE+111+");
        edifactReader = new EdifactReader();
        edifactReader.setContentHandler(new DefaultHandler());
        try {
            edifactReader.parse(new StringReader(ediText));
            fail("Transaction count error not detected");
        } catch (TransactionCountException e) {
            assertEquals("Message count error in UNE segment. Expected 1 instead of 111 at segment 6, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsGroupControlNumberError() throws IOException, SAXException {
        String ediText = EDIFACT_WITH_GROUP.replace("UNE+1+16", "UNE+1+99");
        edifactReader = new EdifactReader();
        edifactReader.setContentHandler(new DefaultHandler());
        try {
            edifactReader.parse(new StringReader(ediText));
            fail("Group control number error not detected");
        } catch (GroupControlNumberException e) {
            assertEquals(
                    "Control number error in UNE segment. Expected 16 instead of 99 at segment 6, field 3",
                    e.getMessage());
        }
    }

    @Test
    public void detectsSegmentCountError() throws IOException, SAXException {
        String ediText = EDITestData.getEdifactInterchange().replace("UNT+8+", "UNT+88+");
        edifactReader = new EdifactReader();
        edifactReader.setContentHandler(new DefaultHandler());
        try {
            edifactReader.parse(new StringReader(ediText));
            fail("Segment count error not detected");
        } catch (SegmentCountException e) {
            assertEquals("Segment count error in UNT segment. Expected 8 instead of 88 at segment 9, field 2", e.getMessage());
        }
    }

    @Test
    public void detectsTransactionControlNumberError() throws IOException, SAXException {
        String ediText = EDITestData.getEdifactInterchange().replace("UNT+8+1'", "UNT+8+11'");
        edifactReader = new EdifactReader();
        edifactReader.setContentHandler(new DefaultHandler());
        try {
            edifactReader.parse(new StringReader(ediText));
            fail("Transaction control number error not detected");
        } catch (TransactionControlNumberException e) {
            assertEquals(
                    "Control number error in UNT segment. Expected 1 instead of 11 at segment 9, field 3",
                    e.getMessage());
        }
    }

}
