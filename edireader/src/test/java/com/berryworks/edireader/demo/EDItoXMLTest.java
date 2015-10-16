package com.berryworks.edireader.demo;

import com.berryworks.edireader.benchmark.EDITestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class EDItoXMLTest {

    private static final String TINY_INTERCHANGE =
            "ISA~00~          ~00~          ~ZZ~04000          ~ZZ~58401          ~040714~1003~U~00204~000038449~0~P~<$" +
                    "GS~AG~04000~58401~040714~1003~38327~X~002040CHRY$" +
                    "ST~824~000042460$" +
                    "BGN~11~ 07141005162 ~040714~1003$" +
                    "SE~3~000042460$" +
                    "GE~1~38327$" +
                    "IEA~1~000038449$\n";

    private EDItoXML ediToXml;

    @Before
    public void setUp() {
    }

    @Test
    public void canUseEDItoXML() throws IOException, SAXException {
        String ansiInterchange = EDITestData.getAnsiInterchange();
        StringReader reader = new StringReader(ansiInterchange);
        StringWriter writer = new StringWriter();
        ediToXml = new EDItoXML(reader, writer);
        ediToXml.run();
    }

    @Test
    public void withLeadingSpacesInData() {
        StringReader reader = new StringReader(TINY_INTERCHANGE);
        StringWriter writer = new StringWriter();
        ediToXml = new EDItoXML(reader, writer);
        ediToXml.run();

        String xmlText = writer.toString();
        int indexOf = xmlText.indexOf("07141005162");
        String neighborhood = xmlText.substring(indexOf - 7, indexOf + 21);
        Assert.assertEquals("GN02\"> 07141005162 </element", neighborhood);

        // Do it again, with the indenting option enabled
        reader = new StringReader(TINY_INTERCHANGE);
        writer = new StringWriter();
        ediToXml = new EDItoXML(reader, writer);
        ediToXml.setIndent(true);
        ediToXml.run();

        xmlText = writer.toString();
        indexOf = xmlText.indexOf("07141005162");
        neighborhood = xmlText.substring(indexOf - 7, indexOf + 21);
        Assert.assertEquals("GN02\"> 07141005162 </element", neighborhood);
    }

    @Test
    public void canIndent() {
        StringReader reader = new StringReader(TINY_INTERCHANGE);
        StringWriter writer = new StringWriter();

        ediToXml = new EDItoXML(reader, writer);
        ediToXml.setIndent(true);
        ediToXml.run();

        String xmlText = writer.toString();
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<ediroot>\n" +
                "    <interchange Standard=\"ANSI X.12\" AuthorizationQual=\"00\" Authorization=\"          \" SecurityQual=\"00\" Security=\"          \" Date=\"040714\" Time=\"1003\" StandardsId=\"U\" Version=\"00204\" Control=\"000038449\" AckRequest=\"0\" TestIndicator=\"P\">\n" +
                "        <sender>\n" +
                "            <address Id=\"04000          \" Qual=\"ZZ\"/>\n" +
                "        </sender>\n" +
                "        <receiver>\n" +
                "            <address Id=\"58401          \" Qual=\"ZZ\"/>\n" +
                "        </receiver>\n" +
                "        <group GroupType=\"AG\" ApplSender=\"04000\" ApplReceiver=\"58401\" Date=\"040714\" Time=\"1003\" Control=\"38327\" StandardCode=\"X\" StandardVersion=\"002040CHRY\">\n" +
                "            <transaction DocType=\"824\" Name=\"Application Advice\" Control=\"000042460\">\n" +
                "                <segment Id=\"BGN\">\n" +
                "                    <element Id=\"BGN01\">11</element>\n" +
                "                    <element Id=\"BGN02\"> 07141005162 </element>\n" +
                "                    <element Id=\"BGN03\">040714</element>\n" +
                "                    <element Id=\"BGN04\">1003</element>\n" +
                "                </segment>\n" +
                "            </transaction>\n" +
                "        </group>\n" +
                "    </interchange>\n" +
                "</ediroot>", xmlText);
    }

}
