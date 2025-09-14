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
import com.berryworks.edireader.tokenizer.EDITokenizer;
import com.berryworks.edireader.tokenizer.Tokenizer;
import com.berryworks.edireader.util.BranchingWriter;
import org.xml.sax.*;

import java.io.*;
import java.util.Locale;


/**
 * An adaptor for XMLReader providing default implementations of several methods
 * to simplify each of the EDIReader classes that have XMLReader as an ancestor.
 */
public abstract class EDIAbstractReader implements XMLReader {

    protected static final String BERRYWORKS_NAMESPACE = "http://www.berryworkssoftware.com/2008/edireader";
    public static final int PREVIEW_LENGTH = 128;

    /**
     * The ContentHandler for this XMLReader
     */
    private ContentHandler contentHandler;

    /**
     * The first PREVIEW_LENGTH bytes of the EDI input, read during
     */
    private static String previewString;

    /**
     * The tokenizer used by this EDIAbstractReader
     */
    private Tokenizer tokenizer;

    /**
     * The Reader providing the EDI input. Typically assigned by createEDIReader in EDIReaderFactory.
     */
    private Reader inputReader;
    private InputSource inputSource;

    private EDISyntaxExceptionHandler syntaxExceptionHandler;

    private ErrorHandler errorHandler;

    private EntityResolver entityResolver;

    /**
     * Character marking the boundary between fields
     */
    private char delimiter;

    /**
     * Character marking the boundary between sub-fields
     */
    private char subDelimiter;

    /**
     * Character marking the boundary between sub-sub-fields
     */
    private char subSubDelimiter;

    /**
     * Character used as a decimal point ("." or ",")
     */
    private char decimalMark;

    /**
     * Character marking the boundary between repeating fields
     */
    private char repetitionSeparator;

    /**
     * Character marking the boundary between segments
     */
    private char terminator;

    /**
     * The byte value used as a release or escape character.
     */
    private int release;

    /**
     * Whitespace characters observed to follow the formal segment terminator.
     */
    private String terminatorSuffix;

    /**
     * Where the functional acknowledgements are (optionally) written.
     */
    private BranchingWriter ackStream;

    /**
     * Where the alternate acknowledgements are (optionally) written.
     */
    private BranchingWriter alternateAckStream;

    /**
     * If acknowledgements are being written, should an interchange acknowledgement be included?
     * For ANSI X12, this would be a TA1 segment after the ISA.
     */
    private boolean interchangeAcknowledgment;

    /**
     * If acknowledgements are being written, should they be group level only?
     * For ANSI X12, this would mean 997s without AK2/AK5 transaction level detail..
     */
    private boolean groupAcknowledgment;

    /**
     * Used when producing a copy of the parsed input is needed.
     */
    private Writer copyWriter;

    /**
     * May contain a copy of the initial segment of the interchange.
     */
    private String firstSegment;

    /**
     * XML attributes relating to the EDI interchange
     */
    private final EDIAttributes interchangeAttributes = new EDIAttributes();

    /**
     * Empty attributes object for convenience in starting elements having no attributes
     */
    private final EDIAttributes noAttributes = new EDIAttributes();

    /**
     * XML attributes relating to the EDI structure that ANSI X12 calls a
     * functional group. In EDIFACT, this corresponds to the UNG/UNE structure.
     */
    private final EDIAttributes groupAttributes = new EDIAttributes();

    /**
     * XML attributes relating to the document. In ANSI X12 terminology this
     * would be the Transaction Set (ST/SE). In EDIFACT, it would be a Message
     * (UNH/UNT)..
     */
    private final EDIAttributes documentAttributes = new EDIAttributes();

    private boolean previewed;

    private boolean externalXmlDocumentStart;

    /**
     * The generated XML will declare a namespace if true
     */
    private boolean namespaceEnabled;

    /**
     * If true, the generated XML will include attributes expressing syntax characters used in the
     * EDI input (delimiter, terminator, etc.)
     */
    private boolean includeSyntaxCharacters;

    /**
     * If true, the generated XML will keep EDI elements that contain only spaces as data.
     * If false, which is the default, any element with only spaces between delimiters is treated as if the delimiters
     * were adjacent. In other words, "*a*b*  *d*" is normally treated exactly the same as "*a*b**d*" but this default
     * behaviour may be changed, in which case the series of spaces is treated as a data value for the element.
     * One reason, perhaps the only reason, you might want to do this is to perform strict validation checking after
     * parsing.
     */
    private boolean keepSpacesOnlyElements;

    private SyntaxDescriptor acknowledgmentSyntaxDescriptor;

    private TransactionCallback transactionCallback;

    public void parse() throws IOException, SAXException {
        parse(inputReader);
    }

    /**
     * A convenience method allowing EDI to be parsed from a Reader without needing to
     * explicitly construct an InputSource.
     *
     * @param reader the Reader providing the EDI input
     */
    public void parse(Reader reader) throws IOException, SAXException {
        parse(new InputSource(reader));
    }

    /**
     * A convenience method allowing EDI to be parsed from a String without needing to
     * explicitly construct an InputSource.
     *
     * @param edi the EDI String
     */
    public void parseEdi(String edi) throws IOException, SAXException {
        parse(new InputSource(new StringReader(edi)));
    }

    /**
     * Gets the character marking the boundary between segments
     *
     * @return The terminator value
     */
    public char getTerminator() {
        return terminator;
    }

    /**
     * Gets the short String of 'whitespace' characters that follows the
     * terminator.
     *
     * @return The terminator value
     */
    public String getTerminatorSuffix() {
        return terminatorSuffix;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public void setSubDelimiter(char subDelimiter) {
        this.subDelimiter = subDelimiter;
    }

    public void setSubSubDelimiter(char subSubDelimiter) {
        this.subSubDelimiter = subSubDelimiter;
    }

    public void setDecimalMark(char decimalMark) {
        this.decimalMark = decimalMark;
    }

    public void setRepetitionSeparator(char repetitionSeparator) {
        this.repetitionSeparator = repetitionSeparator;
    }

    public void setTerminator(char terminator) {
        this.terminator = terminator;
    }

    public void setRelease(int release) {
        this.release = release;
    }

    public void setTerminatorSuffix(String terminatorSuffix) {
        this.terminatorSuffix = terminatorSuffix;
    }

    /**
     * Gets the character marking the boundary between fields
     *
     * @return The delimiter value
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Gets the character marking the boundary between sub-fields. Subfields may
     * be called by different names in different EDI standards.
     *
     * @return The subDelimiter value
     */
    public char getSubDelimiter() {
        return subDelimiter;
    }

    /**
     * Gets the character used in release/escape sequences.
     * Exactly how this character is used may differ between standards.
     * In ANSI, there is no release mechanism. When no release character
     * is available, the int value -1 is returned, otherwise a char
     * value is returned via the int.
     *
     * @return The release char value or -1 if none
     */
    public int getRelease() {
        return release;
    }

    public char getReleaseCharacter() {
        return isReleaseCharacterDefined() ? (char) release : ' ';
    }

    public boolean isReleaseCharacterDefined() {
        return release != -1;
    }

    /**
     * Gets the character used as the decimal point in currency.
     * This is the period (".") in the USA and many other countries,
     * but can also be the comma (",").
     *
     * @return mark
     */
    public char getDecimalMark() {
        return decimalMark;
    }

    /**
     * Gets the character marking the boundary between sub-sub-fields.
     * Sub-sub-fields are not used in ANSI or EDIFACT, but appear in HL7.
     *
     * @return The subSubDelimiter value
     */
    public char getSubSubDelimiter() {
        return subSubDelimiter;
    }

    /**
     * Gets the character marking the boundary between repeating fields.
     *
     * @return The repetitionSeparator value
     */
    public char getRepetitionSeparator() {
        return repetitionSeparator;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(Tokenizer t) {
        tokenizer = t;
    }

    public void setCopyWriter(Writer writer) {
        if (tokenizer != null)
            tokenizer.setWriter(writer);
    }

    protected static Reader createReader(InputSource source) throws IOException {
        Reader theReader;
        if (source == null)
            throw new IOException("createReader called with null InputSource");

        // We expect the InputSource to provide either a character stream or a byte stream.
        theReader = source.getCharacterStream();
        if (theReader == null) {
            InputStream inputStream = source.getByteStream();
            if (inputStream != null) {
                // We have a byte stream. Read PREVIEW_LENGTH bytes to String with ISO-8859-1 encoding,
                // and be prepared to create a Reader using a different character set depending on what we see in the UNB.
                byte[] previewBytes = new byte[PREVIEW_LENGTH];
                try {
                    int read = inputStream.read(previewBytes);
                    if (read == -1)
                        throw new IOException("No bytes available from InputSource ByteStream");
                } catch (IOException e) {
                    throw new IOException("Problem reading from InputSource ByteStream: " + e.getMessage());
                }
                previewString = new String(previewBytes, "ISO-8859-1");

                theReader = new StringReader(previewString);
            } else {
                String systemId = source.getSystemId();
                if (systemId != null) {
                    // try to establish inputReader using the SystemId
                    if (systemId.startsWith("file:"))
                        // systemId names a file
                        theReader = new FileReader(systemId.substring(5));
                    else
                        // some kind of URL not yet supported
                        throw new IOException("InputSource using SystemId ("
                                              + systemId + ") not yet supported");
                } else
                    // getCharacterStream(), getByteStream(), and
                    // getSystemId() all return null
                    throw new IOException(
                            "Cannot get ByteStream, CharacterStream, or SystemId from EDI InputSource");
            }
        } else {
            // We have a character stream.
        }

        return theReader;
    }

    /**
     * Prepare the parser for its parse method to be called. This involves
     * previewing some of the interchange to discover syntactic details, and
     * making sure a tokenizer is in place. The preview method, of course,
     * varies with each EDI standard.
     *
     * @param source provides read access to the EDI data
     * @throws EDISyntaxException if invalid EDI is detected
     * @throws IOException        if problem reading source
     */
    protected void parseSetup(InputSource source) throws EDISyntaxException,
            IOException {
        Reader inputReader = createReader(source);

        if (tokenizer == null)
            setTokenizer(new EDITokenizer(inputReader));

        if (!previewed) {
            preview();
            previewed = true;
        }

        if (copyWriter != null)
            tokenizer.setWriter(copyWriter);
    }

    /**
     * Preview the EDI interchange to discover syntactic details that will be
     * useful to know before the actual parse method is called.
     *
     * @throws IOException        for problem reading EDI data
     * @throws EDISyntaxException if invalid EDI is detected
     */
    public abstract void preview() throws EDISyntaxException, IOException;

    /**
     * Indicate that functional acknowledgments are to be generated by
     * designating a Writer. This method should be called before calling parse()
     * if acknowledgments are desired.
     * <p>
     * For example, in ANSI X12 this provides for the generation of 997s.
     *
     * @param writer The new acknowledgment value
     */
    public void setAcknowledgment(Writer writer) {
        ackStream = (writer == null) ? null : new BranchingWriter(writer);
    }

    /**
     * Indicate that an alternative type of acknowledgments are to be generated by
     * designating a Writer. This method should be called before calling parse()
     * if acknowledgments are desired.
     * <p>
     * For example, in ANSI X12 this provides for the generation of 999s.
     *
     * @param writer The new acknowledgment value
     */
    public void setAlternateAcknowledgment(Writer writer) {
        alternateAckStream = (writer == null) ? null : new BranchingWriter(writer);
    }

    public void setAcknowledgment(Writer writer, SyntaxDescriptor syntaxDescriptor) {
        setAcknowledgment(writer);
        setAcknowledgmentSyntaxDescriptor(syntaxDescriptor);
    }

    public boolean isInterchangeAcknowledgment() {
        return interchangeAcknowledgment;
    }

    public void setInterchangeAcknowledgment(boolean interchangeAcknowledgment) {
        this.interchangeAcknowledgment = interchangeAcknowledgment;
    }

    public boolean isGroupAcknowledgment() {
        return groupAcknowledgment;
    }

    public void setGroupAcknowledgment(boolean groupAcknowledgment) {
        this.groupAcknowledgment = groupAcknowledgment;
    }

    public SyntaxDescriptor getAcknowledgmentSyntaxDescriptor() {
        return acknowledgmentSyntaxDescriptor;
    }

    public void setAcknowledgmentSyntaxDescriptor(SyntaxDescriptor syntaxDescriptor) {
        acknowledgmentSyntaxDescriptor = syntaxDescriptor;
    }

    public TransactionCallback getTransactionCallback() {
        return transactionCallback;
    }

    public void setTransactionCallback(TransactionCallback transactionCallback) {
        this.transactionCallback = transactionCallback;
    }

    public EDISyntaxExceptionHandler getSyntaxExceptionHandler() {
        return syntaxExceptionHandler;
    }

    public void setSyntaxExceptionHandler(EDISyntaxExceptionHandler syntaxExceptionHandler) {
        this.syntaxExceptionHandler = syntaxExceptionHandler;
    }

    public boolean isNamespaceEnabled() {
        return namespaceEnabled;
    }

    public void setNamespaceEnabled(boolean namespaceEnabled) {
        this.namespaceEnabled = namespaceEnabled;
    }

    public boolean isIncludeSyntaxCharacters() {
        return includeSyntaxCharacters;
    }

    public void setIncludeSyntaxCharacters(boolean includeSyntaxCharacters) {
        this.includeSyntaxCharacters = includeSyntaxCharacters;
    }

    public boolean isKeepSpacesOnlyElements() {
        return keepSpacesOnlyElements;
    }

    public void setKeepSpacesOnlyElements(boolean keepSpacesOnlyElements) {
        this.keepSpacesOnlyElements = keepSpacesOnlyElements;
    }

    public boolean isExternalXmlDocumentStart() {
        return externalXmlDocumentStart;
    }

    public void setExternalXmlDocumentStart(boolean externalXmlDocumentStart) {
        this.externalXmlDocumentStart = externalXmlDocumentStart;
    }

    public void setLocale(Locale locale) throws SAXException {
        throw new SAXNotSupportedException("setLocale not supported");
    }

    public void setEntityResolver(EntityResolver resolver) {
        entityResolver = resolver;
    }

    public void setDTDHandler(DTDHandler handler) {
    }

    public void setErrorHandler(ErrorHandler handler) {
        errorHandler = handler;
    }

    /**
     * Parse the EDI interchange. Each subclass must override this method.
     */
    public void parse(String systemId) throws SAXException, IOException {
        throw new SAXException("parse(systemId) not supported");
    }

    public Reader getInputReader() {
        return inputReader;
    }

    public void setInputReader(Reader inputReader) {
        this.inputReader = inputReader;
    }

    public InputSource getInputSource() {
        return inputSource;
    }

    public void setInputSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    public static String getPreviewString() {
        return previewString;
    }

    public void setContentHandler(ContentHandler handler) {
        contentHandler = handler;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void setFeature(String name, boolean value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        throw new SAXNotSupportedException("Not yet implemented");
    }

    public void setProperty(String name, Object value)
            throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    public Object getProperty(String name) throws SAXNotRecognizedException,
            SAXNotSupportedException {
        return null;
    }

    public EDIAttributes getDocumentAttributes() {
        return documentAttributes;
    }

    public EDIAttributes getInterchangeAttributes() {
        return interchangeAttributes;
    }

    public EDIAttributes getGroupAttributes() {
        return groupAttributes;
    }

    public BranchingWriter getAckStream() {
        return ackStream;
    }

    public void setAckStream(BranchingWriter ackStream) {
        this.ackStream = ackStream;
    }

    public BranchingWriter getAlternateAckStream() {
        return alternateAckStream;
    }

    public boolean isPreviewed() {
        return previewed;
    }

    public void setPreviewed(boolean previewed) {
        this.previewed = previewed;
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public DTDHandler getDTDHandler() {
        return null;
    }

    public EntityResolver getEntityResolver() {
        return entityResolver;
    }

    public int getCharCount() {
        return tokenizer == null ? 0 : tokenizer.getCharCount();
    }

    public int getSegmentCharCount() {
        return tokenizer == null ? 0 : tokenizer.getSegmentCharCount();
    }

    public String getFirstSegment() {
        return firstSegment;
    }

    public void setFirstSegment(String firstSegment) {
        this.firstSegment = firstSegment;
    }

    @Override
    public String toString() {
        String lineBreak = System.getProperty("line.separator");
        return lineBreak + "EDIReader summary:" + lineBreak +
               " class: " + getClass().getName() + lineBreak +
               " delimiter: " + getDelimiter() + lineBreak +
               " subDelimiter: " + getSubDelimiter() + lineBreak +
               " subSubDelimiter: " + getSubSubDelimiter() + lineBreak +
               " repetitionSeparator: " + getRepetitionSeparator() + lineBreak +
               " terminator: " + getTerminator() + lineBreak +
               " terminatorSuffix: " + getTerminatorSuffix() + lineBreak +
               " charCount: " + getCharCount() + lineBreak +
               " segmentCharCount: " + getSegmentCharCount() + lineBreak;
    }
}
