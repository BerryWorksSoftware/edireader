package com.berryworks.edireader.util;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class xmlFormatterTest {
    private final static String EOL = System.getProperty("line.separator");

    private XmlFormatter xmlFormatter;

    @Test
    public void canFormatTrivialDocument() throws IOException {
        StringWriter writer = new StringWriter();
        xmlFormatter = new XmlFormatter(writer);
        xmlFormatter.write("<root></root>");
        xmlFormatter.close();
        assertEquals("<root>" + EOL + "</root>", writer.toString());
    }

    @Test
    public void canFormatSimpleDocument() throws IOException {
        StringWriter writer = new StringWriter();
        xmlFormatter = new XmlFormatter(writer);
        xmlFormatter.write("<root><a>A</a><b>B</b></root>");
        xmlFormatter.close();
        assertEquals("" +
                        "<root>" + EOL +
                        "    <a>A</a>" + EOL +
                        "    <b>B</b>" + EOL +
                        "</root>",
                writer.toString());
    }

    @Test
    public void canFormatNestedElements() throws IOException {
        StringWriter writer = new StringWriter();
        xmlFormatter = new XmlFormatter(writer);
        xmlFormatter.write("<root><a><b>B1</b></a><a><b>B2</b></a></root>");
        xmlFormatter.close();
        assertEquals("" +
                        "<root>" + EOL +
                        "    <a>" + EOL +
                        "        <b>B1</b>" + EOL +
                        "    </a>" + EOL +
                        "    <a>" + EOL +
                        "        <b>B2</b>" + EOL +
                        "    </a>" + EOL +
                        "</root>",
                writer.toString());
    }

    @Test
    public void canFormatAttributes() throws IOException {
        StringWriter writer = new StringWriter();
        xmlFormatter = new XmlFormatter(writer);
        xmlFormatter.write("<root><a x=\"X\" y=\"Y\">A</a></root>");
        xmlFormatter.close();
        assertEquals("" +
                        "<root>" + EOL +
                        "    <a x=\"X\" y=\"Y\">A</a>" + EOL +
                        "</root>",
                writer.toString());
    }
}
