package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIAttributes;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class ContextAwareSaxAdapterTest {

    private MyContextAwareSaxAdapter adapter;
    private EDIAttributes attributes;

    @Before
    public void setUp() {
        adapter = new MyContextAwareSaxAdapter();
        attributes = new EDIAttributes();
    }

    @Test
    public void basics() throws SAXException {
        adapter.startElement(null, "A", null, attributes);
        assertContext(adapter, null, null, "A");

        addCharacters(adapter, "aaa");
        // The ContextAwareSaxAdapter is buffering data, waiting until it can call
        // start() with all the data
        assertContext(adapter, null,null, "A");

        addCharacters(adapter, " aaa");
        // The ContextAwareSaxAdapter is buffering data, waiting until it can call
        // start() with all the data
        assertContext(adapter, null,null, "A");

        adapter.startElement(null, "B", null, attributes);
        // The startElement() means that no more data will be arriving for the a element,
        // so the pending data is delivered on a call to start() to the ContextAwareSaxAdapter subclass.
        assertContext(adapter, "(A","aaa aaa|", "A", "B");

        addCharacters(adapter, "bbb");
        // The new data for the b element is buffered
        assertContext(adapter, "(A","aaa aaa|", "A", "B");

        adapter.endElement(null, "B", null);
        // There will be no more data for the b element.
        assertContext(adapter, "(A(BB)","aaa aaa|bbb|", "A");

        adapter.endElement(null, "A", null);
        assertContext(adapter, "(A(BB)A)","aaa aaa|bbb|", null);
    }

    @Test
    public void dataTrimmingIsTheDefault() throws SAXException {
        adapter.startElement(null, "A", null, attributes);
        assertContext(adapter, null, null, "A");

        addCharacters(adapter, "aaa");
        addCharacters(adapter, " aaa ");
        assertContext(adapter, null,null, "A");

        adapter.startElement(null, "B", null, attributes);
        addCharacters(adapter, "bbb   ");
        assertContext(adapter, "(A","aaa aaa|", "A", "B");

        adapter.endElement(null, "B", null);
        assertContext(adapter, "(A(BB)","aaa aaa|bbb|", "A");

        adapter.endElement(null, "A", null);
        assertContext(adapter, "(A(BB)A)","aaa aaa|bbb|", null);
    }

    @Test
    public void dataTrimmingCanBeDisabled() throws SAXException {
        adapter = new MyContextAwareSaxAdapter(false);
        adapter.startElement(null, "A", null, attributes);
        assertContext(adapter, null, null, "A");

        addCharacters(adapter, "aaa");
        addCharacters(adapter, " aaa ");
        assertContext(adapter, null,null, "A");

        adapter.startElement(null, "B", null, attributes);
        addCharacters(adapter, "bbb   ");
        assertContext(adapter, "(A","aaa aaa |", "A", "B");

        adapter.endElement(null, "B", null);
        assertContext(adapter, "(A(BB)","aaa aaa |bbb   |", "A");

        adapter.endElement(null, "A", null);
        assertContext(adapter, "(A(BB)A)","aaa aaa |bbb   |", null);
    }

    private void addCharacters(ContextAwareSaxAdapter adapter, String data) throws SAXException {
        adapter.characters(data.toCharArray(), 0, data.length());
    }

    private void assertContext(MyContextAwareSaxAdapter adapter, String startsAndEnds, String data, String... names) {
        List<String> context = adapter.getContext();
        if (names == null) {
            assertEquals(0, context.size());
        } else {
            assertEquals(names.length, context.size());
            for (int i = 0; i < context.size(); i++) {
                assertEquals(names[i], context.get(i));
            }
        }

        if (startsAndEnds == null) startsAndEnds = "";
        assertEquals(startsAndEnds, adapter.getStartsAndEnds());

        if (data == null) data = "";
        assertEquals(data, adapter.getData());
    }

    private class MyContextAwareSaxAdapter extends ContextAwareSaxAdapter {

        private StringBuilder data = new StringBuilder();
        private StringBuilder sequence = new StringBuilder();

        public MyContextAwareSaxAdapter(boolean isTrimmingEnabled) {
            super(isTrimmingEnabled);
        }

        public MyContextAwareSaxAdapter() {
            this(true);
        }

        public String getData() {
            return data.toString();
        }

        public String getStartsAndEnds() {
            return sequence.toString();
        }

        @Override
        public void start(String uri, String name, String data, EDIAttributes attributes) throws SAXException {
            this.data.append(data).append('|');
            this.sequence.append('(').append(name);
        }

        @Override
        public void end(String uri, String name) throws SAXException {
            this.sequence.append(name).append(')');
        }
    }
}
