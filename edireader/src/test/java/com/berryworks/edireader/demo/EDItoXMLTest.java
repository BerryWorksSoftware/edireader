package com.berryworks.edireader.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.berryworks.edireader.benchmark.EDITestData;
import com.berryworks.edireader.test.utils.TestResources;

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
        ediToXml = new EDItoXML();
    }

    @Test
    public void canUseEDItoXML() throws IOException, SAXException {
        String ansiInterchange = EDITestData.getAnsiInterchange();
        StringReader reader = new StringReader(ansiInterchange);
        StringWriter writer = new StringWriter();
        ediToXml.setInputReader(reader);
        ediToXml.setXmlOutputWriter(writer);
        ediToXml.run();
    }

    @Test
    public void withLeadingSpacesInData() {
        StringReader reader = new StringReader(TINY_INTERCHANGE);
        StringWriter writer = new StringWriter();
        ediToXml.setInputReader(reader);
        ediToXml.setXmlOutputWriter(writer);
        ediToXml.run();

        String xmlText = writer.toString();
        int indexOf = xmlText.indexOf("07141005162");
        String neighborhood = xmlText.substring(indexOf - 7, indexOf + 21);
        Assert.assertEquals("GN02\"> 07141005162 </element", neighborhood);

        // Do it again, with the indenting option enabled
        reader = new StringReader(TINY_INTERCHANGE);
        writer = new StringWriter();
        ediToXml = new EDItoXML();
        ediToXml.setInputReader(reader);
        ediToXml.setXmlOutputWriter(writer);
        ediToXml.setIndent(true);
        ediToXml.run();

        xmlText = writer.toString();
        indexOf = xmlText.indexOf("07141005162");
        neighborhood = xmlText.substring(indexOf - 7, indexOf + 21);
        Assert.assertEquals("GN02\"> 07141005162 </element", neighborhood);
    }

    @Test
    public void canIndent() throws Exception {
        StringReader reader = new StringReader(TINY_INTERCHANGE);
        StringWriter writer = new StringWriter();
        ediToXml.setInputReader(reader);
        ediToXml.setXmlOutputWriter(writer);
        ediToXml.setIndent(true);
        ediToXml.run();

        String xmlText = writer.toString();
        String benchmark = TestResources.getAsString("EDItoXMLTest.canIndent.xml");
               
        assertTrue(xmlText.length() == benchmark.length());
        assertEquals(benchmark, xmlText);
    }

}
