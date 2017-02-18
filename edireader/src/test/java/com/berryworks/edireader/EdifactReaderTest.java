package com.berryworks.edireader;

import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static com.berryworks.edireader.util.Conversion.ediToxml;
import static org.junit.Assert.assertEquals;

public class EdifactReaderTest {

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
    public void producesXmlForFunctionalGroup() throws IOException, SAXException, TransformerException {
        edifactReader = new EdifactReader();
        StringReader ediInput = new StringReader(
                "UNB+IATA:1+REUAIR08DLH:PIMA+REUAGT82AGENT/LHR01:PIMA+941027:1520+841F60UNZ+RREF+APR"
                        + "+L+1'"
                        + "UNG+INVOIC+BTS-SENDER+RECEIVE-PARTNER+141024:2231+201410242231+UN+D:96A'"
                        + "UNH+1+DCQCKI:90:1:IA+841F60'"
                        + "LOR+SR:GVA'"
                        + "UNT+3+1'"
                        + "UNE+1+201410242231'"
                        + "UNZ+1+841F60UNZ+1+30077'");
        StringWriter writer = new StringWriter();
        ediToxml(ediInput, writer, edifactReader);
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<ediroot>" +
                        "<interchange Standard=\"EDIFACT\" SyntaxId=\"IATA\" SyntaxVersion=\"1\" Date=\"941027\" Time=\"1520\" Control=\"841F60UNZ\" RecipientRef=\"RREF\" ApplRef=\"APR\" Priority=\"L\" AckRequest=\"1\" Decimal=\".\">" +
                        "<sender><address Id=\"REUAIR08DLH\" Qual=\"PIMA\"/></sender>" +
                        "<receiver><address Id=\"REUAGT82AGENT/LHR01\" Qual=\"PIMA\"/></receiver>" +
                        "<group GroupType=\"INVOIC\" Date=\"141024\" Time=\"2231\" Control=\"201410242231\" StandardCode=\"UN\">" +
                        "<transaction Control=\"1\" DocType=\"DCQCKI\" Version=\"90\" Release=\"1\" Agency=\"IA\" AccessReference=\"841F60\">" +
                        "<segment Id=\"LOR\"><element Id=\"LOR01\" Composite=\"yes\"><subelement Sequence=\"1\">SR</subelement><subelement Sequence=\"2\">GVA</subelement></element></segment>" +
                        "</transaction>" +
                        "</group>" +
                        "</interchange>" +
                        "</ediroot>",
                writer.toString());
    }
}
