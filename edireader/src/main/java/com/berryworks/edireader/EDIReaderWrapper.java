/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 *
 * This file is part of EDIReader. You may obtain a license for its use directly from
 * BerryWorks Software, and you may also choose to use this software under the terms of the
 * GPL version 3. Other products in the EDIReader software suite are available only by licensing
 * with BerryWorks. Only those files bearing the GPL statement below are available under the GPL.
 *
 * EDIReader is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * EDIReader is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with EDIReader.  If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.berryworks.edireader;

import com.berryworks.edireader.error.EDISyntaxExceptionHandler;
import com.berryworks.edireader.tokenizer.Tokenizer;
import com.berryworks.edireader.util.BranchingWriter;
import org.xml.sax.*;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;

/**
 * This EDIReader subclass wraps an EDIReader delegate, providing an opportunity to decorate
 * the delegate with additional features.
 */
public class EDIReaderWrapper extends EDIReader {

    private EDIReader wrappedEDIReader;

    public EDIReaderWrapper(EDIReader wrappedEdiReader) {
        super();
        this.wrappedEDIReader = wrappedEdiReader;
    }

    public EDIReaderWrapper() {
        throw new RuntimeException("EDIReaderWrapper default construction not allowed");
    }

    public EDIReader getWrappedEDIReader() {
        return wrappedEDIReader;
    }

    @Override
    public void preview() throws EDISyntaxException, IOException {
        wrappedEDIReader.preview();
    }

    @Override
    public void parse(InputSource source) throws SAXException, IOException {
        wrappedEDIReader.parse(source);
    }

    @Override
    public void setXMLTags(XMLTags tags) {
        wrappedEDIReader.setXMLTags(tags);
    }

    @Override
    public XMLTags getXMLTags() {
        return wrappedEDIReader.getXMLTags();
    }

    @Override
    public char getTerminator() {
        return wrappedEDIReader.getTerminator();
    }

    @Override
    public String getTerminatorSuffix() {
        return wrappedEDIReader.getTerminatorSuffix();
    }

    @Override
    public char getDelimiter() {
        return wrappedEDIReader.getDelimiter();
    }

    @Override
    public char getSubDelimiter() {
        return wrappedEDIReader.getSubDelimiter();
    }

    @Override
    public int getRelease() {
        return wrappedEDIReader.getRelease();
    }

    @Override
    public char getReleaseCharacter() {
        return wrappedEDIReader.getReleaseCharacter();
    }

    @Override
    public boolean isReleaseCharacterDefined() {
        return wrappedEDIReader.isReleaseCharacterDefined();
    }

    @Override
    public char getDecimalMark() {
        return wrappedEDIReader.getDecimalMark();
    }

    @Override
    public char getSubSubDelimiter() {
        return wrappedEDIReader.getSubSubDelimiter();
    }

    @Override
    public char getRepetitionSeparator() {
        return wrappedEDIReader.getRepetitionSeparator();
    }

    @Override
    public Tokenizer getTokenizer() {
        return wrappedEDIReader.getTokenizer();
    }

    @Override
    public void setTokenizer(Tokenizer t) {
        wrappedEDIReader.setTokenizer(t);
    }

    @Override
    public void setCopyWriter(Writer writer) {
        wrappedEDIReader.setCopyWriter(writer);
    }

    @Override
    protected void parseSetup(InputSource source) throws EDISyntaxException, IOException {
        wrappedEDIReader.parseSetup(source);
    }

    @Override
    public void setAcknowledgment(Writer writer) {
        wrappedEDIReader.setAcknowledgment(writer);
    }

    @Override
    public void setAcknowledgment(Writer writer, SyntaxDescriptor syntaxDescriptor) {
        wrappedEDIReader.setAcknowledgment(writer, syntaxDescriptor);
    }

    @Override
    public SyntaxDescriptor getAcknowledgmentSyntaxDescriptor() {
        return wrappedEDIReader.getAcknowledgmentSyntaxDescriptor();
    }

    @Override
    public void setAcknowledgmentSyntaxDescriptor(SyntaxDescriptor syntaxDescriptor) {
        wrappedEDIReader.setAcknowledgmentSyntaxDescriptor(syntaxDescriptor);
    }

    @Override
    public TransactionCallback getTransactionCallback() {
        return wrappedEDIReader.getTransactionCallback();
    }

    @Override
    public void setTransactionCallback(TransactionCallback transactionCallback) {
        wrappedEDIReader.setTransactionCallback(transactionCallback);
    }

    @Override
    public EDISyntaxExceptionHandler getSyntaxExceptionHandler() {
        return wrappedEDIReader.getSyntaxExceptionHandler();
    }

    @Override
    public void setSyntaxExceptionHandler(EDISyntaxExceptionHandler syntaxExceptionHandler) {
        wrappedEDIReader.setSyntaxExceptionHandler(syntaxExceptionHandler);
    }

    @Override
    public boolean isNamespaceEnabled() {
        return wrappedEDIReader.isNamespaceEnabled();
    }

    @Override
    public void setNamespaceEnabled(boolean namespaceEnabled) {
        wrappedEDIReader.setNamespaceEnabled(namespaceEnabled);
    }

    @Override
    public void setLocale(Locale locale) throws SAXException {
        wrappedEDIReader.setLocale(locale);
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        wrappedEDIReader.setEntityResolver(resolver);
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        wrappedEDIReader.setDTDHandler(handler);
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        wrappedEDIReader.setErrorHandler(handler);
    }

    @Override
    public void parse(String systemId) throws SAXException, IOException {
        wrappedEDIReader.parse(systemId);
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        wrappedEDIReader.setContentHandler(handler);
    }

    @Override
    public ContentHandler getContentHandler() {
        return wrappedEDIReader.getContentHandler();
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        wrappedEDIReader.setFeature(name, value);
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return wrappedEDIReader.getFeature(name);
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        wrappedEDIReader.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return wrappedEDIReader.getProperty(name);
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return wrappedEDIReader.getErrorHandler();
    }

    @Override
    public DTDHandler getDTDHandler() {
        return wrappedEDIReader.getDTDHandler();
    }

    @Override
    public EntityResolver getEntityResolver() {
        return wrappedEDIReader.getEntityResolver();
    }

    @Override
    public int getCharCount() {
        return wrappedEDIReader.getCharCount();
    }

    @Override
    public int getSegmentCharCount() {
        return wrappedEDIReader.getSegmentCharCount();
    }

    @Override
    public void setDelimiter(char delimiter) {
        wrappedEDIReader.setDelimiter(delimiter);
    }

    @Override
    public void setSubDelimiter(char subDelimiter) {
        wrappedEDIReader.setSubDelimiter(subDelimiter);
    }

    @Override
    public void setSubSubDelimiter(char subSubDelimiter) {
        wrappedEDIReader.setSubSubDelimiter(subSubDelimiter);
    }

    @Override
    public void setDecimalMark(char decimalMark) {
        wrappedEDIReader.setDecimalMark(decimalMark);
    }

    @Override
    public void setRepetitionSeparator(char repetitionSeparator) {
        wrappedEDIReader.setRepetitionSeparator(repetitionSeparator);
    }

    @Override
    public void setTerminator(char terminator) {
        wrappedEDIReader.setTerminator(terminator);
    }

    @Override
    public void setRelease(int release) {
        wrappedEDIReader.setRelease(release);
    }

    @Override
    public void setTerminatorSuffix(String terminatorSuffix) {
        wrappedEDIReader.setTerminatorSuffix(terminatorSuffix);
    }

    @Override
    public EDIAttributes getDocumentAttributes() {
        return wrappedEDIReader.getDocumentAttributes();
    }

    @Override
    public EDIAttributes getInterchangeAttributes() {
        return wrappedEDIReader.getInterchangeAttributes();
    }

    @Override
    public EDIAttributes getGroupAttributes() {
        return wrappedEDIReader.getGroupAttributes();
    }

    @Override
    public BranchingWriter getAckStream() {
        return wrappedEDIReader.getAckStream();
    }

    @Override
    public void setAckStream(BranchingWriter ackStream) {
        wrappedEDIReader.setAckStream(ackStream);
    }

    @Override
    public boolean isPreviewed() {
        return wrappedEDIReader.isPreviewed();
    }

    @Override
    public void setPreviewed(boolean previewed) {
        wrappedEDIReader.setPreviewed(previewed);
    }

    @Override
    public String getFirstSegment() {
        return wrappedEDIReader.getFirstSegment();
    }

    @Override
    public void setFirstSegment(String firstSegment) {
        wrappedEDIReader.setFirstSegment(firstSegment);
    }

    @Override
    public String toString() {
        return wrappedEDIReader.toString();
    }
}
