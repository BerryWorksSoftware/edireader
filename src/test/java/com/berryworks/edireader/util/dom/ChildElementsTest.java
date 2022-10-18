package com.berryworks.edireader.util.dom;

import com.berryworks.edireader.benchmark.EDITestData;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ListIterator;

import static org.junit.Assert.assertEquals;

public class ChildElementsTest {

    private Element transactionElement;

    @Before
    public void setUp() throws Exception {
        Document dom = DocumentUtil.getInstance().buildDocumentFromEdi(EDITestData.getAnsiInputSource());
        transactionElement = DocumentUtil.position(dom.getDocumentElement(), new String[]{"interchange", "group", "transaction"});
    }

    @Test
    public void canIterateOverAllChildren() {
        ChildElements children = new ChildElements(transactionElement);
        ListIterator<Element> iterator = children.listIterator();
        int count = 0;
        while (iterator.hasNext()) {
            Element childElement = iterator.next();
            count++;
        }
        assertEquals(9, count);
    }

    @Test
    public void canIterateOverAllChildrenHavingTag() {
        // Count the <segment> children
        ChildElements children = new ChildElements(transactionElement, "segment");
        ListIterator<Element> iterator = children.listIterator();
        int count = 0;
        while (iterator.hasNext()) {
            Element childElement = iterator.next();
            assertEquals("segment", childElement.getTagName());
            count++;
        }
        assertEquals(1, count);

        // Count the <loop> children
        iterator = new ChildElements(transactionElement, "loop").listIterator();
        count = 0;
        while (iterator.hasNext()) {
            Element childElement = iterator.next();
            assertEquals("loop", childElement.getTagName());
            count++;
        }
        assertEquals(8, count);
    }
}
