package com.berryworks.edireader.util;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class XmlFormatterTest {
    private final static String EOL = System.getProperty("line.separator");

    private XmlFormatter xmlFormatter;

    @Test
    public void canFormatTrivialDocument() throws IOException {
        StringWriter writer = new StringWriter();
        xmlFormatter = new XmlFormatter(writer);
        xmlFormatter.write("<root></root>");
        xmlFormatter.close();
        assertEquals("""
                <root>
                </root>""", writer.toString());
    }

    @Test
    public void canFormatSimpleDocument() throws IOException {
        StringWriter writer = new StringWriter();
        xmlFormatter = new XmlFormatter(writer);
        xmlFormatter.write("<root><a>A</a><b>B</b></root>");
        xmlFormatter.close();
        assertEquals("""
                        <root>
                            <a>A</a>
                            <b>B</b>
                        </root>""",
                writer.toString());
    }

    @Test
    public void canFormatWithSlashWithinData() throws IOException {
        StringWriter writer = new StringWriter();
        xmlFormatter = new XmlFormatter(writer);
        xmlFormatter.write("<root><a>A/A</a><b>/B/</b></root>");
        xmlFormatter.close();
        assertEquals("""
                        <root>
                            <a>A/A</a>
                            <b>/B/</b>
                        </root>""",
                writer.toString());
    }

    @Test
    public void canFormatNestedElements() throws IOException {
        StringWriter writer = new StringWriter();
        // Notice that the close() is not needed with the try-with-resources, since the XmlFormatter is automatically closed.
        try (XmlFormatter xmlFormatter = new XmlFormatter(writer)) {
            xmlFormatter.write("<root><a><b>B1</b></a><a><b>B2</b></a></root>");
        }
        assertEquals("""
                        <root>
                            <a>
                                <b>B1</b>
                            </a>
                            <a>
                                <b>B2</b>
                            </a>
                        </root>""",
                writer.toString());
    }

    @Test
    public void canFormatAttributes() throws IOException {
        StringWriter writer = new StringWriter();
        xmlFormatter = new XmlFormatter(writer);
        xmlFormatter.write("""
                <root><a x="X" y='Y/Y'>A</a><a x='X' y="Y/Y">A</a><a x="X" y="Y/Y"><b z='Z/Z'>B</b></a></root>""");
        xmlFormatter.close();
        assertEquals("""
                        <root>
                            <a x="X" y='Y/Y'>A</a>
                            <a x='X' y="Y/Y">A</a>
                            <a x="X" y="Y/Y">
                                <b z='Z/Z'>B</b>
                            </a>
                        </root>""",
                writer.toString());
    }

    @Test
    public void canFormatX12Sample() throws IOException {
        StringWriter writer = new StringWriter();
        xmlFormatter = new XmlFormatter(writer);
        // Notice that the XML input has no line breaks or indentation.
        try (XmlFormatter xmlFormatter = new XmlFormatter(writer)) {
            xmlFormatter.write(
                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<ediroot>" +
                    "<interchange Standard=\"ANSI X.12\" AuthorizationQual=\"00\" Authorization=\"          \" SecurityQual=\"00\" Security=\"          \" Date=\"090122\" Time=\"0519\" StandardsId=\"U\" Version=\"00204\" Control=\"000015712\" AckRequest=\"0\" TestIndicator=\"P\">" +
                    "<sender><address Id=\"04000          \" Qual=\"ZZ\"/></sender>" +
                    "<receiver><address Id=\"58401          \" Qual=\"ZZ\"/></receiver>" +
                    "<group GroupType=\"AG\" ApplSender=\"11111\" ApplReceiver=\"22222\" Date=\"20090122\" Time=\"0519\" Control=\"000001\" StandardCode=\"X\" StandardVersion=\"005010\">" +
                    "<transaction DocType=\"824\" Name=\"Application Advice\" Control=\"000001\">" +
                    "<segment Id=\"BGN\"><element Id=\"BGN01\">00</element><element Id=\"BGN02\">Lorem</element><element Id=\"BGN03\">20191002</element><element Id=\"BGN04\">Lorem</element></segment><loop Id=\"N1-1000\"><segment Id=\"N1\"><element Id=\"N101\">02</element><element Id=\"N102\">Lorem</element><element Id=\"N103\">Lorem</element><element Id=\"N104\">Lorem</element></segment></loop><loop Id=\"N1-1000\"><segment Id=\"N1\"><element Id=\"N101\">01</element><element Id=\"N102\">Lorem</element><element Id=\"N103\">Lorem</element><element Id=\"N104\">Lorem</element></segment></loop><loop Id=\"N1-1000\"><segment Id=\"N1\"><element Id=\"N101\">02</element><element Id=\"N102\">Lorem</element><element Id=\"N103\">Lorem</element><element Id=\"N104\">Lorem</element></segment></loop><loop Id=\"N1-1000\"><segment Id=\"N1\"><element Id=\"N101\">01</element><element Id=\"N102\">Lorem</element><element Id=\"N103\">Lorem</element><element Id=\"N104\">Lorem</element></segment></loop><loop Id=\"OTI-2000\"><segment Id=\"OTI\"><element Id=\"OTI01\">Lorem</element><element Id=\"OTI02\">Lorem</element><element Id=\"OTI03\">Lorem</element><element Id=\"OTI04\">Lorem</element><element Id=\"OTI05\">Lorem</element><element Id=\"OTI06\">Lorem</element><element Id=\"OTI07\">Lorem</element><element Id=\"OTI08\">Lorem</element><element Id=\"OTI09\">Lorem</element><element Id=\"OTI10\">Lorem</element><element Id=\"OTI11\">Lorem</element><element Id=\"OTI12\">Lorem</element><element Id=\"OTI13\">Lorem</element><element Id=\"OTI14\">Lorem</element><element Id=\"OTI15\">Lorem</element><element Id=\"OTI16\">Lorem</element><element Id=\"OTI17\">Lorem</element></segment></loop><loop Id=\"OTI-2000\"><segment Id=\"OTI\"><element Id=\"OTI01\">Lorem</element><element Id=\"OTI02\">Lorem</element><element Id=\"OTI03\">Lorem</element><element Id=\"OTI04\">Lorem</element><element Id=\"OTI05\">Lorem</element><element Id=\"OTI06\">Lorem</element><element Id=\"OTI07\">Lorem</element><element Id=\"OTI08\">Lorem</element><element Id=\"OTI09\">Lorem</element><element Id=\"OTI10\">Lorem</element><element Id=\"OTI11\">Lorem</element><element Id=\"OTI12\">Lorem</element><element Id=\"OTI13\">Lorem</element><element Id=\"OTI14\">Lorem</element><element Id=\"OTI15\">Lorem</element><element Id=\"OTI16\">Lorem</element><element Id=\"OTI17\">Lorem</element></segment><segment Id=\"REF\"><element Id=\"REF01\">Lorem</element><element Id=\"REF02\">Lorem</element><element Id=\"REF03\">Lorem</element></segment><segment Id=\"REF\"><element Id=\"REF01\">Lorem</element><element Id=\"REF02\">Lorem</element><element Id=\"REF03\">Lorem</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">Lorem</element><element Id=\"DTM02\">Lorem</element><element Id=\"DTM03\">Lorem</element><element Id=\"DTM04\">Lorem</element></segment><segment Id=\"DTM\"><element Id=\"DTM01\">Lorem</element><element Id=\"DTM02\">Lorem</element><element Id=\"DTM03\">Lorem</element><element Id=\"DTM04\">Lorem</element></segment></loop>" +
                    "</transaction>" +
                    "</group>" +
                    "</interchange>" +
                    "</ediroot>");
        }
        assertEquals("""
                <?xml version="1.0" encoding="UTF-8"?>
                <ediroot>
                    <interchange Standard="ANSI X.12" AuthorizationQual="00" Authorization="          " SecurityQual="00" Security="          " Date="090122" Time="0519" StandardsId="U" Version="00204" Control="000015712" AckRequest="0" TestIndicator="P">
                        <sender>
                            <address Id="04000          " Qual="ZZ"/>
                        </sender>
                        <receiver>
                            <address Id="58401          " Qual="ZZ"/>
                        </receiver>
                        <group GroupType="AG" ApplSender="11111" ApplReceiver="22222" Date="20090122" Time="0519" Control="000001" StandardCode="X" StandardVersion="005010">
                            <transaction DocType="824" Name="Application Advice" Control="000001">
                                <segment Id="BGN">
                                    <element Id="BGN01">00</element>
                                    <element Id="BGN02">Lorem</element>
                                    <element Id="BGN03">20191002</element>
                                    <element Id="BGN04">Lorem</element>
                                </segment>
                                <loop Id="N1-1000">
                                    <segment Id="N1">
                                        <element Id="N101">02</element>
                                        <element Id="N102">Lorem</element>
                                        <element Id="N103">Lorem</element>
                                        <element Id="N104">Lorem</element>
                                    </segment>
                                </loop>
                                <loop Id="N1-1000">
                                    <segment Id="N1">
                                        <element Id="N101">01</element>
                                        <element Id="N102">Lorem</element>
                                        <element Id="N103">Lorem</element>
                                        <element Id="N104">Lorem</element>
                                    </segment>
                                </loop>
                                <loop Id="N1-1000">
                                    <segment Id="N1">
                                        <element Id="N101">02</element>
                                        <element Id="N102">Lorem</element>
                                        <element Id="N103">Lorem</element>
                                        <element Id="N104">Lorem</element>
                                    </segment>
                                </loop>
                                <loop Id="N1-1000">
                                    <segment Id="N1">
                                        <element Id="N101">01</element>
                                        <element Id="N102">Lorem</element>
                                        <element Id="N103">Lorem</element>
                                        <element Id="N104">Lorem</element>
                                    </segment>
                                </loop>
                                <loop Id="OTI-2000">
                                    <segment Id="OTI">
                                        <element Id="OTI01">Lorem</element>
                                        <element Id="OTI02">Lorem</element>
                                        <element Id="OTI03">Lorem</element>
                                        <element Id="OTI04">Lorem</element>
                                        <element Id="OTI05">Lorem</element>
                                        <element Id="OTI06">Lorem</element>
                                        <element Id="OTI07">Lorem</element>
                                        <element Id="OTI08">Lorem</element>
                                        <element Id="OTI09">Lorem</element>
                                        <element Id="OTI10">Lorem</element>
                                        <element Id="OTI11">Lorem</element>
                                        <element Id="OTI12">Lorem</element>
                                        <element Id="OTI13">Lorem</element>
                                        <element Id="OTI14">Lorem</element>
                                        <element Id="OTI15">Lorem</element>
                                        <element Id="OTI16">Lorem</element>
                                        <element Id="OTI17">Lorem</element>
                                    </segment>
                                </loop>
                                <loop Id="OTI-2000">
                                    <segment Id="OTI">
                                        <element Id="OTI01">Lorem</element>
                                        <element Id="OTI02">Lorem</element>
                                        <element Id="OTI03">Lorem</element>
                                        <element Id="OTI04">Lorem</element>
                                        <element Id="OTI05">Lorem</element>
                                        <element Id="OTI06">Lorem</element>
                                        <element Id="OTI07">Lorem</element>
                                        <element Id="OTI08">Lorem</element>
                                        <element Id="OTI09">Lorem</element>
                                        <element Id="OTI10">Lorem</element>
                                        <element Id="OTI11">Lorem</element>
                                        <element Id="OTI12">Lorem</element>
                                        <element Id="OTI13">Lorem</element>
                                        <element Id="OTI14">Lorem</element>
                                        <element Id="OTI15">Lorem</element>
                                        <element Id="OTI16">Lorem</element>
                                        <element Id="OTI17">Lorem</element>
                                    </segment>
                                    <segment Id="REF">
                                        <element Id="REF01">Lorem</element>
                                        <element Id="REF02">Lorem</element>
                                        <element Id="REF03">Lorem</element>
                                    </segment>
                                    <segment Id="REF">
                                        <element Id="REF01">Lorem</element>
                                        <element Id="REF02">Lorem</element>
                                        <element Id="REF03">Lorem</element>
                                    </segment>
                                    <segment Id="DTM">
                                        <element Id="DTM01">Lorem</element>
                                        <element Id="DTM02">Lorem</element>
                                        <element Id="DTM03">Lorem</element>
                                        <element Id="DTM04">Lorem</element>
                                    </segment>
                                    <segment Id="DTM">
                                        <element Id="DTM01">Lorem</element>
                                        <element Id="DTM02">Lorem</element>
                                        <element Id="DTM03">Lorem</element>
                                        <element Id="DTM04">Lorem</element>
                                    </segment>
                                </loop>
                            </transaction>
                        </group>
                    </interchange>
                </ediroot>""", writer.toString());
    }

}
