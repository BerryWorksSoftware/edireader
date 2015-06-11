package com.berryworks.edireader;

import com.berryworks.edireader.tokenizer.Token;
import com.berryworks.edireader.tokenizer.TokenImpl;
import com.berryworks.edireader.util.sax.ContextAwareSaxAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

public class StandardReaderTest {

    private StandardReader reader;

    @Test
    public void segmentElement_simple() throws SAXException {
        reader = new DefaultStandardReader();
        final MyContentHandler handler = new MyContentHandler();
        reader.setContentHandler(handler);

//      ...|abc||123^^^||
        reader.parseSegmentElement(new MyToken(Token.TokenType.SIMPLE, "abc"));
        reader.parseSegmentElement(new MyToken(Token.TokenType.EMPTY, null));
        reader.parseSegmentElement(new MyToken(Token.TokenType.SUB_ELEMENT, "123", true, false));
        reader.parseSegmentElement(new MyToken(Token.TokenType.SUB_EMPTY, null, false, false));
        reader.parseSegmentElement(new MyToken(Token.TokenType.SUB_EMPTY, null, false, false));
        reader.parseSegmentElement(new MyToken(Token.TokenType.SUB_EMPTY, null, false, true));
        reader.parseSegmentElement(new MyToken(Token.TokenType.EMPTY, null));
        Assert.assertEquals("start(element, abc) end(element) start(element, null) start(subelement, 123) end(subelement) end(element)", handler.getLog());
    }

    private class DefaultStandardReader extends StandardReader {
        @Override
        protected Token recognizeBeginning() throws IOException, SAXException {
            return null;
        }

        @Override
        protected Token parseInterchange(Token t) throws SAXException, IOException {
            return null;
        }
    }

    private class MyToken extends TokenImpl {
        private boolean isFirst;
        private boolean isLast;

        public MyToken(TokenType type, String value) {
            this(type, value, false, false);
        }

        public MyToken(TokenType type, String value, boolean isFirst, boolean isLast) {
            super(null);
            setType(type);
            if (value != null)
                for (char c : value.toCharArray()) {
                    append(c);
                }
            this.isFirst = isFirst;
            this.isLast = isLast;
        }

        @Override
        public boolean isFirst() {
            return isFirst;
        }

        @Override
        public boolean isLast() {
            return isLast;
        }
    }

    private class MyContentHandler extends ContextAwareSaxAdapter {
        private String name;
        private String data;
        private String log = "";

        @Override
        public void start(String uri, String name, String data, EDIAttributes attributes) throws SAXException {
            this.name = name;
            this.data = data;
            log += "start(" + name + ", " + data + ") ";
        }

        @Override
        public void end(String uri, String name) throws SAXException {
            this.name = name;
            log += "end(" + name + ") ";
        }

        public String getLog() {
            return log.trim();
        }
    }
}
