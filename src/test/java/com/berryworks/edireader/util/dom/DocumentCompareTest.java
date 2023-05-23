package com.berryworks.edireader.util.dom;

import com.berryworks.edireader.benchmark.EDITestData;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.StringReader;

import static com.berryworks.edireader.util.dom.DocumentUtil.compare;
import static com.berryworks.edireader.util.dom.DocumentUtil.getInstance;
import static org.junit.Assert.*;

public class DocumentCompareTest {
    private Document documentA, documentB;

    @Before
    public void setUp() throws Exception {
        String ediA = EDITestData.getAnsiInterchange();
        documentA = getInstance().buildDocumentFromEdi(new InputSource(new StringReader(ediA)));
        String ediB = ediA
                .replaceAll("000042460", "000042461")
                .replace("07141005162", "07141005163");
        assertNotEquals(ediA, ediB);
        documentB = getInstance().buildDocumentFromEdi(new InputSource(new StringReader(ediB)));
    }

    @Test
    public void compareDocumentWithItself() {
        assertNull(compare(documentA, documentA));
    }

    @Test
    public void compareDocumentWithNull() {
        assertEquals("First Document is null", compare(null, documentA));
        assertEquals("Second Document is null", compare(documentA, null));
    }

    @Test
    public void compareDocumentWithAnother() {
        assertEquals("element value mismatch;" +
                        "element value mismatch;" +
                        "element value mismatch;" +
                        "element value mismatch;" +
                        "element value mismatch;" +
                        "element value mismatch;" +
                        "attribute value mismatch;",
                compare(documentA, documentB));
    }

}
