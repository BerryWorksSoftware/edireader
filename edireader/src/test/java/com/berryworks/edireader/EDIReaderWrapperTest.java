package com.berryworks.edireader;

import com.berryworks.edireader.error.EDISyntaxExceptionHandler;
import com.berryworks.edireader.tokenizer.Tokenizer;
import org.junit.Test;
import org.xml.sax.*;

import java.io.IOException;
import java.io.Writer;

import static org.junit.Assert.*;

public class EDIReaderWrapperTest {

    EDIReaderWrapper wrapper;
    final MockEDIReader ediReader = new MockEDIReader();

    @Test
    public void testWrapper() throws IOException, SAXException {
        wrapper = new EDIReaderWrapper(ediReader);
        assertSame(ediReader, wrapper.getWrappedEDIReader());

        assertFalse(ediReader.isCalled("preview"));
        wrapper.preview();
        assertTrue(ediReader.isCalled("preview"));

        assertFalse(ediReader.isCalled("parse"));
        wrapper.parse((InputSource) null);
        assertTrue(ediReader.isCalled("parse"));

        assertFalse(ediReader.isCalled("getXMLTags"));
        wrapper.getXMLTags();
        assertTrue(ediReader.isCalled("getXMLTags"));
        assertFalse(ediReader.isCalled("setXMLTags"));
        wrapper.setXMLTags(null);
        assertTrue(ediReader.isCalled("setXMLTags"));

        assertFalse(ediReader.isCalled("getTerminator"));
        wrapper.getTerminator();
        assertTrue(ediReader.isCalled("getTerminator"));
        assertFalse(ediReader.isCalled("setTerminator"));
        wrapper.setTerminator(' ');
        assertTrue(ediReader.isCalled("setTerminator"));

        assertFalse(ediReader.isCalled("getTerminatorSuffix"));
        wrapper.getTerminatorSuffix();
        assertTrue(ediReader.isCalled("getTerminatorSuffix"));
        assertFalse(ediReader.isCalled("setTerminatorSuffix"));
        wrapper.setTerminatorSuffix(null);
        assertTrue(ediReader.isCalled("setTerminatorSuffix"));

        assertFalse(ediReader.isCalled("getDelimiter"));
        wrapper.getDelimiter();
        assertTrue(ediReader.isCalled("getDelimiter"));
        assertFalse(ediReader.isCalled("setDelimiter"));
        wrapper.setDelimiter(' ');
        assertTrue(ediReader.isCalled("setDelimiter"));

        assertFalse(ediReader.isCalled("getSubDelimiter"));
        wrapper.getSubDelimiter();
        assertTrue(ediReader.isCalled("getSubDelimiter"));
        assertFalse(ediReader.isCalled("setSubDelimiter"));
        wrapper.setSubDelimiter(' ');
        assertTrue(ediReader.isCalled("setSubDelimiter"));

        assertFalse(ediReader.isCalled("getSubSubDelimiter"));
        wrapper.getSubSubDelimiter();
        assertTrue(ediReader.isCalled("getSubSubDelimiter"));
        assertFalse(ediReader.isCalled("setSubSubDelimiter"));
        wrapper.setSubSubDelimiter(' ');
        assertTrue(ediReader.isCalled("setSubSubDelimiter"));

        assertFalse(ediReader.isCalled("getRelease"));
        wrapper.getRelease();
        assertTrue(ediReader.isCalled("getRelease"));
        assertFalse(ediReader.isCalled("setRelease"));
        wrapper.setRelease(' ');
        assertTrue(ediReader.isCalled("setRelease"));
        assertFalse(ediReader.isCalled("getReleaseCharacter"));
        wrapper.getReleaseCharacter();
        assertTrue(ediReader.isCalled("getReleaseCharacter"));
        assertFalse(ediReader.isCalled("isReleaseCharacterDefined"));
        wrapper.isReleaseCharacterDefined();
        assertTrue(ediReader.isCalled("isReleaseCharacterDefined"));

        assertFalse(ediReader.isCalled("getDecimalMark"));
        wrapper.getDecimalMark();
        assertTrue(ediReader.isCalled("getDecimalMark"));
        assertFalse(ediReader.isCalled("setDecimalMark"));
        wrapper.setDecimalMark(' ');
        assertTrue(ediReader.isCalled("setDecimalMark"));

        assertFalse(ediReader.isCalled("getRepetitionSeparator"));
        wrapper.getRepetitionSeparator();
        assertTrue(ediReader.isCalled("getRepetitionSeparator"));
        assertFalse(ediReader.isCalled("setRepetitionSeparator"));
        wrapper.setRepetitionSeparator(' ');
        assertTrue(ediReader.isCalled("setRepetitionSeparator"));

        assertFalse(ediReader.isCalled("getTokenizer"));
        wrapper.getTokenizer();
        assertTrue(ediReader.isCalled("getTokenizer"));
        assertFalse(ediReader.isCalled("setTokenizer"));
        wrapper.setTokenizer(null);
        assertTrue(ediReader.isCalled("setTokenizer"));

        assertFalse(ediReader.isCalled("setCopyWriter"));
        wrapper.setCopyWriter(null);
        assertTrue(ediReader.isCalled("setCopyWriter"));

        assertFalse(ediReader.isCalled("getAcknowledgmentSyntaxDescriptor"));
        wrapper.getAcknowledgmentSyntaxDescriptor();
        assertTrue(ediReader.isCalled("getAcknowledgmentSyntaxDescriptor"));
        assertFalse(ediReader.isCalled("setAcknowledgmentSyntaxDescriptor"));
        wrapper.setAcknowledgmentSyntaxDescriptor(null);
        assertTrue(ediReader.isCalled("setAcknowledgmentSyntaxDescriptor"));


        assertFalse(ediReader.isCalled("getTransactionCallback"));
        wrapper.getTransactionCallback();
        assertTrue(ediReader.isCalled("getTransactionCallback"));
        assertFalse(ediReader.isCalled("setTransactionCallback"));
        wrapper.setTransactionCallback(null);
        assertTrue(ediReader.isCalled("setTransactionCallback"));

        assertFalse(ediReader.isCalled("getSyntaxExceptionHandler"));
        wrapper.getSyntaxExceptionHandler();
        assertTrue(ediReader.isCalled("getSyntaxExceptionHandler"));
        assertFalse(ediReader.isCalled("setSyntaxExceptionHandler"));
        wrapper.setSyntaxExceptionHandler(null);
        assertTrue(ediReader.isCalled("setSyntaxExceptionHandler"));

        assertFalse(ediReader.isCalled("getContentHandler"));
        wrapper.getContentHandler();
        assertTrue(ediReader.isCalled("getContentHandler"));
        assertFalse(ediReader.isCalled("setContentHandler"));
        wrapper.setContentHandler(null);
        assertTrue(ediReader.isCalled("setContentHandler"));

        assertFalse(ediReader.isCalled("getFeature"));
        wrapper.getFeature(null);
        assertTrue(ediReader.isCalled("getFeature"));
        assertFalse(ediReader.isCalled("setFeature"));
        wrapper.setFeature(null, false);
        assertTrue(ediReader.isCalled("setFeature"));

        assertFalse(ediReader.isCalled("getProperty"));
        wrapper.getProperty(null);
        assertTrue(ediReader.isCalled("getProperty"));
        assertFalse(ediReader.isCalled("setProperty"));
        wrapper.setProperty(null, false);
        assertTrue(ediReader.isCalled("setProperty"));

        assertFalse(ediReader.isCalled("getFirstSegment"));
        wrapper.getFirstSegment();
        assertTrue(ediReader.isCalled("getFirstSegment"));
        assertFalse(ediReader.isCalled("setFirstSegment"));
        wrapper.setFirstSegment(null);
        assertTrue(ediReader.isCalled("setFirstSegment"));

    }

    @Test(expected = RuntimeException.class)
    public void testDefaultConstructor() {
        new EDIReaderWrapper();
    }

    static class MockEDIReader extends EDIReader {

        private String called = "|";

        @Override
        public void preview() throws EDISyntaxException, IOException {
            called("preview");
        }

        @Override
        public void parse(InputSource source) throws SAXException, IOException {
            called("parse");
        }

        @Override
        public XMLTags getXMLTags() {
            called("getXMLTags");
            return null;
        }

        @Override
        public void setXMLTags(XMLTags tags) {
            called("setXMLTags");
        }

        @Override
        public char getTerminator() {
            called("getTerminator");
            return ' ';
        }

        @Override
        public void setTerminator(char terminator) {
            called("setTerminator");
        }

        @Override
        public String getTerminatorSuffix() {
            called("getTerminatorSuffix");
            return null;
        }

        @Override
        public void setTerminatorSuffix(String terminatorSuffix) {
            called("setTerminatorSuffix");
        }

        @Override
        public char getDelimiter() {
            called("getDelimiter");
            return ' ';
        }

        @Override
        public void setDelimiter(char delimiter) {
            called("setDelimiter");
        }

        @Override
        public char getSubDelimiter() {
            called("getSubDelimiter");
            return ' ';
        }

        @Override
        public void setSubDelimiter(char delimiter) {
            called("setSubDelimiter");
        }

        @Override
        public char getSubSubDelimiter() {
            called("getSubSubDelimiter");
            return ' ';
        }

        @Override
        public void setSubSubDelimiter(char delimiter) {
            called("setSubSubDelimiter");
        }

        @Override
        public int getRelease() {
            called("getRelease");
            return ' ';
        }

        @Override
        public void setRelease(int n) {
            called("setRelease");
        }

        @Override
        public char getReleaseCharacter() {
            called("getReleaseCharacter");
            return ' ';
        }

        @Override
        public boolean isReleaseCharacterDefined() {
            called("isReleaseCharacterDefined");
            return false;
        }

        @Override
        public void setDecimalMark(char decimalMark) {
            called("setDecimalMark");
        }

        @Override
        public char getDecimalMark() {
            called("getDecimalMark");
            return ' ';
        }

        @Override
        public char getRepetitionSeparator() {
            called("getRepetitionSeparator");
            return ' ';
        }

        @Override
        public void setRepetitionSeparator(char repetitionSeparator) {
            called("setRepetitionSeparator");
        }

        @Override
        public Tokenizer getTokenizer() {
            called("getTokenizer");
            return null;
        }

        @Override
        public void setCopyWriter(Writer writer) {
            called("setCopyWriter");
        }

        @Override
        public void setTokenizer(Tokenizer t) {
            called("setTokenizer");
        }

        @Override
        public SyntaxDescriptor getAcknowledgmentSyntaxDescriptor() {
            called("getAcknowledgmentSyntaxDescriptor");
            return null;
        }

        @Override
        public void setAcknowledgmentSyntaxDescriptor(SyntaxDescriptor syntaxDescriptor) {
            called("setAcknowledgmentSyntaxDescriptor");
        }

        @Override
        public TransactionCallback getTransactionCallback() {
            called("getTransactionCallback");
            return null;
        }

        @Override
        public EDISyntaxExceptionHandler getSyntaxExceptionHandler() {
            called("getSyntaxExceptionHandler");
            return null;
        }

        @Override
        public void setSyntaxExceptionHandler(EDISyntaxExceptionHandler syntaxExceptionHandler) {
            called("setSyntaxExceptionHandler");
        }

        @Override
        public void setTransactionCallback(TransactionCallback transactionCallback) {
            called("setTransactionCallback");
        }

        @Override
        public ContentHandler getContentHandler() {
            called("getContentHandler");
            return null;
        }

        @Override
        public void setContentHandler(ContentHandler handler) {
            called("setContentHandler");
        }

        @Override
        public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            called("getFeature");
            return false;
        }

        @Override
        public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
            called("setFeature");
        }

        @Override
        public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
            called("getProperty");
            return false;
        }

        @Override
        public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
            called("setProperty");
        }

        @Override
        public String getFirstSegment() {
            called("getFirstSegment");
            return null;
        }

        @Override
        public void setFirstSegment(String firstSegment) {
            called("setFirstSegment");
        }

        public boolean isCalled(String method) {
            return called.contains('|' + method + '|');
        }

        private void called(String method) {
            called += method + '|';
        }

    }
}
