/*
 * Copyright 2005-2019 by BerryWorks Software, LLC. All rights reserved.
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

import com.berryworks.edireader.error.ErrorMessages;
import com.berryworks.edireader.tokenizer.EDITokenizer;
import com.berryworks.edireader.tokenizer.Token;
import com.berryworks.edireader.util.ContentHandlerBase64Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.util.List;

import static com.berryworks.edireader.util.FixedLength.emptyIfNull;
import static com.berryworks.edireader.util.FixedLength.isPresent;

/**
 * Reads and parses EDIFACT EDI interchanges. This class is not normally
 * constructed explicitly from outside the package, although it is declared
 * public for special cases. The recommended use of this class is to first
 * establish an EDIReader using one of the factory techniques; when the
 * EDIReader is called upon to parse the EDI data, it determines which EDI
 * standard applies and internally constructs the proper subclass to continue
 * with parsing.
 */
public class EdifactReader extends StandardReader {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    protected static final int ELEMENTS_IN_SEGMENT_MAXIMUM = 50;
    protected static final int ELEMENTS_IN_UNB_MAXIMUM = 30;

    protected boolean ungExplicit;
    protected boolean witnessedUNA;

    @Override
    protected Token recognizeBeginning() throws IOException, SAXException {
        Token t = getTokenizer().nextToken();
        if (t.getType() == Token.TokenType.SEGMENT_START) {
            String segType = t.getValue();
            if ("UNA".equals(segType)) {
                witnessedUNA = true;
                // We've already examined this UNA in the preview
                getTokenizer().skipSegment();
                t = getTokenizer().nextToken();
                if (t.getType() == Token.TokenType.SEGMENT_START) {
                    segType = t.getValue();
                } else {
                    EDISyntaxException se = new EDISyntaxException(INVALID_UNA, getTokenizer());
                    logger.warn(se.getMessage());
                    throw se;
                }
            }
            if (!"UNB".equals(segType)) {
                if (witnessedUNA) {
                    String msg = "Mandatory UNB segment was not recognized after UNA. Terminator problem?";
                    logger.warn(msg);
                    throw new EDISyntaxException(msg);
                }
                EDISyntaxException se = new EDISyntaxException(FIRST_SEGMENT_MUST_BE_UNA_OR_UNB, getTokenizer());
                logger.warn(se.getMessage());
                throw se;
            }
        } else {
            logger.warn(FIRST_SEGMENT_MUST_BE_UNA_OR_UNB);
            throw new EDISyntaxException(FIRST_SEGMENT_MUST_BE_UNA_OR_UNB);
        }
        return t;
    }

    /**
     * Parse Edifact interchange ( UNB to UNZ )
     *
     * @param token parsed token that caused this method to be called
     * @return token most recently parsed by this method
     * @throws SAXException for problem emitting SAX events
     * @throws IOException  for problem reading EDI data
     */
    @Override
    protected Token parseInterchange(Token token) throws SAXException,
            IOException {
        getInterchangeAttributes().clear();
        getInterchangeAttributes().addCDATA(getXMLTags().getStandard(), EDIStandard.EDIFACT.getDisplayName());
        setGroupCount(0);
        List<String> compositeList;

        /*
          Syntax identifier : version (example: UNOA:2 )
         */
        compositeList = getTokenizer().nextCompositeElement();
        String syntaxIdentifier = getSubElement(compositeList, 0);
        String syntaxVersionNumber = getSubElement(compositeList, 1);
        if (!syntaxIdentifier.isEmpty()) {
            getInterchangeAttributes().addCDATA(getXMLTags().getSyntaxIdentifier(),
                    syntaxIdentifier);
            if (!syntaxVersionNumber.isEmpty()) {
                getInterchangeAttributes().addCDATA(getXMLTags().getSyntaxVersion(),
                        syntaxVersionNumber);
            }
        }

        /*
          Sender address
         */
        compositeList = getTokenizer().nextCompositeElement();
        String fromId = getSubElement(compositeList, 0);
        String fromQual = getSubElement(compositeList, 1);
        String fromExtra = getSubElement(compositeList, 2);

        /*
          Receiver address
         */
        compositeList = getTokenizer().nextCompositeElement();
        String toId = getSubElement(compositeList, 0);
        String toQual = getSubElement(compositeList, 1);
        String toExtra = getSubElement(compositeList, 2);

        /*
          Date and time (UNB0401 and UNB0402)
         */
        compositeList = getTokenizer().nextCompositeElement();
        String date = getSubElement(compositeList, 0);
        String time = getSubElement(compositeList, 1);
        getInterchangeAttributes().addCDATA(getXMLTags().getDate(), date);
        getInterchangeAttributes().addCDATA(getXMLTags().getTime(), time);

        /*
          Control number (UNB05)
         */
        setInterchangeControlNumber(getTokenizer().nextSimpleValue());
        getInterchangeAttributes().addCDATA(getXMLTags().getControl(), getInterchangeControlNumber());

        remainderOfUNB();

        /*
          Decimal notation

          The character used for decimal notation in numbers.
          For example, the value 3.14159 is expressed using "."
          for decimal notation. Another character sometimes used
          for this purpose is "," (comma).
         */
        getInterchangeAttributes().addCDATA(
                getXMLTags().getDecimal(),
                String.valueOf(getDecimalMark()));

        startInterchange(getInterchangeAttributes());

        generatedSenderAndReceiver(fromId, fromQual, fromExtra, toId, toQual, toExtra);

        label:
        while (true) {
            ungExplicit = true;
            token = getTokenizer().nextToken();
            if (token.getType() != Token.TokenType.SEGMENT_START) {
                EDISyntaxException se = new EDISyntaxException("Invalid beginning of UNG|UNH|UNZ segment", getTokenizer());
                logger.warn(se.getMessage());
                throw se;
            }
            String sType = token.getValue();
            switch (sType) {
                case "UNG":
                    setGroupCount(1 + getGroupCount());
                    parseFunctionalGroup(token);
                    break;
                case "UNH":
                    impliedFunctionalGroup(token);
                    break;
                case "UNZ":
                    break label;
                default:
                    EDISyntaxException se = new EDISyntaxException(UNEXPECTED_SEGMENT_IN_CONTEXT, "UNH, UNZ, or UNG", sType, getTokenizer());
                    logger.warn(se.getMessage());
                    throw se;
            }
        }

        checkGroupCount(getGroupCount(), getTokenizer().nextIntValue(), COUNT_UNZ);
        checkInterchangeControlNumber(getInterchangeControlNumber(), getTokenizer().nextSimpleValue(), CONTROL_NUMBER_UNZ);
        endInterchange();

        return getTokenizer().skipSegment();
    }

    protected void remainderOfUNB() throws IOException, EDISyntaxException {

        if (hitEndOfSegment(getXMLTags().getRecipientReference())
            || hitEndOfSegment(getXMLTags().getApplicationReference())
            || hitEndOfSegment(getXMLTags().getProcessingPriority())
            || hitEndOfSegment(getXMLTags().getAcknowledgementRequest())
            || hitEndOfSegment(getXMLTags().getInterchangeAgreementIdentifier())
            || hitEndOfSegment(getXMLTags().getTestIndicator()))
            return;

        while (getTokenizer().nextToken().getType() != Token.TokenType.SEGMENT_END) {
            if (getTokenizer().getElementInSegmentCount() > ELEMENTS_IN_UNB_MAXIMUM) {
                EDISyntaxException se = new EDISyntaxException("Too many ("
                                                               + getTokenizer().getElementInSegmentCount()
                                                               + ") elements for a UNB. Segment terminator problem?",
                        getTokenizer());
                logger.warn(se.getMessage());
                throw se;
            }
        }
    }

    protected boolean hitEndOfSegment(String attributeName)
            throws EDISyntaxException, IOException {
        Token token = getTokenizer().nextToken();
        if (token.getType() == Token.TokenType.SEGMENT_END) {
            return true;
        } else if (token.getType() == Token.TokenType.SIMPLE) {
            getInterchangeAttributes().addCDATA(attributeName, token.getValue());
        }
        return false;
    }

    /**
     * Parse Edifact group (UNG to UNE)
     *
     * @param token parsed token that caused this method to be called
     * @return token most recently parsed by this method
     * @throws SAXException for problem emitting SAX events
     * @throws IOException  for problem reading EDI data
     */
    protected Token parseFunctionalGroup(Token token) throws SAXException,
            IOException {
        int docCount = 0;

        getGroupAttributes().clear();
        // Group type. For example: INVOIC
        getGroupAttributes().addCDATA("GroupType", getTokenizer().nextSimpleValue());
        List<String> compositeList;
        // Application sender
        compositeList = getTokenizer().nextCompositeElement();
        String sender = getSubElement(compositeList, 0);
        getGroupAttributes().addCDATA(getXMLTags().getApplSender(), sender);
        String senderQualifier = getSubElement(compositeList, 1);
        if (isPresent(senderQualifier)) {
            getGroupAttributes().addCDATA(getXMLTags().getApplSenderQualifier(), senderQualifier);
        }
        // Application receiver
        compositeList = getTokenizer().nextCompositeElement();
        String receiver = getSubElement(compositeList, 0);
        getGroupAttributes().addCDATA(getXMLTags().getApplReceiver(), receiver);
        String receiverQualifier = getSubElement(compositeList, 1);
        if (isPresent(receiverQualifier)) {
            getGroupAttributes().addCDATA(getXMLTags().getApplReceiverQualifier(), receiverQualifier);
        }
        // Date and time
        compositeList = getTokenizer().nextCompositeElement();
        String date = getSubElement(compositeList, 0);
        String time = getSubElement(compositeList, 1);
        getGroupAttributes().addCDATA(getXMLTags().getDate(), date);
        getGroupAttributes().addCDATA(getXMLTags().getTime(), time);
        // Control number
        setGroupControlNumber(getTokenizer().nextSimpleValue());
        getGroupAttributes().addCDATA(getXMLTags().getControl(), getGroupControlNumber());
        // Standard Code. For example: UN
        getGroupAttributes().addCDATA("StandardCode", getTokenizer().nextSimpleValue());
        // Standard Version. For example: D02B
        compositeList = getTokenizer().nextCompositeElement();
        String version = getSubElement(compositeList, 0);
        String release = getSubElement(compositeList, 1);
        getGroupAttributes().addCDATA(getXMLTags().getStandardVersion(), version + release);
        startElement(getXMLTags().getGroupTag(), getGroupAttributes());
        getTokenizer().skipSegment();

        label:
        while (true) {
            token = getTokenizer().nextToken();
            if (token.getType() != Token.TokenType.SEGMENT_START) {
                EDISyntaxException se = new EDISyntaxException("Invalid beginning of UNH|UNE segment", getTokenizer().getSegmentCount());
                logger.warn(se.getMessage());
                throw se;
            }
            String sType = token.getValue();
            switch (sType) {
                case "UNH":
                    docCount++;
                    parseDocument(token);
                    break;
                case "UNE":
                    break label;
                default:
                    EDISyntaxException se = new EDISyntaxException("Expected UNE or UNH segment instead of " + sType, getTokenizer());
                    logger.warn(se.getMessage());
                    throw se;
            }
        }

        checkTransactionCount(docCount, getTokenizer().nextIntValue(), COUNT_UNE);
        checkGroupControlNumber(getGroupControlNumber(), getTokenizer().nextSimpleValue(), CONTROL_NUMBER_UNE);
        endElement(getXMLTags().getGroupTag());
        return getTokenizer().skipSegment();
    }

    /**
     * Handle implied Edifact group (UNG to UNE)
     *
     * @param token parsed token that caused this method to be called
     * @return token most recently parsed by this method
     * @throws SAXException for problem emitting SAX events
     * @throws IOException  for problem reading EDI data
     */
    protected Token impliedFunctionalGroup(Token token) throws SAXException,
            IOException {
        getGroupAttributes().clear();
        startElement(getXMLTags().getGroupTag(), getGroupAttributes());
        label:
        while (true) {
            if (token.getType() != Token.TokenType.SEGMENT_START) {
                EDISyntaxException se = new EDISyntaxException("Invalid beginning of UNH|UNZ segment",
                        getTokenizer().getSegmentCount());
                logger.warn(se.getMessage());
                throw se;
            }
            String sType = token.getValue();
            switch (sType) {
                case "UNH":
                    setGroupCount(1 + getGroupCount());
                    parseDocument(token);
                    token = getTokenizer().nextToken();
                    break;
                case "UNZ":
                    getTokenizer().ungetToken();
                    break label;
                default:
                    EDISyntaxException se = new EDISyntaxException(UNEXPECTED_SEGMENT_IN_CONTEXT,
                            "UNH or UNZ", sType, getTokenizer());
                    logger.warn(se.getMessage());
                    throw se;
            }
        }

        endElement(getXMLTags().getGroupTag());
        return (token);
    }

    /**
     * Parse Edifact Message (UNH to UNT)
     *
     * @param token parsed token that triggered call to this method
     * @return last token parsed
     * @throws IOException  if problem reading EDI data
     * @throws SAXException if invalid EDI is detected
     */
    protected Token parseDocument(Token token) throws SAXException,
            IOException {
        String control;
        String messageType = "";
        String messageVersion = "";
        String messageRelease = "";
        int segCount = 2;

        getDocumentAttributes().clear();
        getDocumentAttributes().addCDATA(getXMLTags().getControl(),
                control = getTokenizer().nextSimpleValue());
        List<String> v = getTokenizer().nextCompositeElement();
        if (v != null) {
            int n = v.size();
            String s = v.get(0);
            if (s != null) {
                messageType = s;
                logger.debug("Parsing {} message", messageType);
                getDocumentAttributes().addCDATA(getXMLTags().getDocumentType(), messageType);
            }
            if (n > 1) {
                s = v.get(1);
                if (s != null) {
                    messageVersion = s;
                    getDocumentAttributes().addCDATA(getXMLTags().getMessageVersion(), messageVersion);
                }
            }
            if (n > 2) {
                s = v.get(2);
                if (s != null) {
                    messageRelease = s;
                    getDocumentAttributes().addCDATA(getXMLTags().getMessageRelease(), messageRelease);
                }
            }
            if (n > 3) {
                s = v.get(3);
                if (s != null) {
                    getDocumentAttributes().addCDATA(getXMLTags().getAgency(), s);
                }
            }
            if (n > 4) {
                s = v.get(4);
                if (s != null) {
                    getDocumentAttributes().addCDATA(getXMLTags().getAssociation(), s);
                }
            }
        }

        String accessReference = getTokenizer().nextSimpleValue(false, true);
        if (!emptyIfNull(accessReference).isEmpty()) {
            getDocumentAttributes().addCDATA(getXMLTags().getAccessReference(), accessReference);
        }

        PluginController pluginController =
                getPluginControllerFactory().create(EDIStandard.EDIFACT.name(), messageType, messageVersion, messageRelease, getTokenizer());

        if (pluginController.isEnabled())
            getDocumentAttributes().addCDATA(getXMLTags().getName(), pluginController.getDocumentName());

        startMessage(getDocumentAttributes());

        String segmentType;
        while (!(segmentType = getTokenizer().nextSegment()).equals("UNT")) {
            segCount++;

            if ("UNO".equals(segmentType)) {
                parseUNOUNPSequence();
                segCount++;
                continue;
            }

            parseSegment(pluginController, segmentType);
        }

        int toClose = pluginController.getNestingLevel();
        for (; toClose > 0; toClose--) {
            endElement(getXMLTags().getLoopTag());

        }

        checkSegmentCount(segCount, getTokenizer().nextIntValue(), COUNT_UNT);
        checkTransactionControlNumber(control, getTokenizer().nextSimpleValue(), CONTROL_NUMBER_UNT);
        endElement(getXMLTags().getDocumentTag());

        /*
         * Skip over this UNT segment and return the SEGMENT_END token
         */
        return getTokenizer().skipSegment();
    }

    protected void parseUNOUNPSequence() throws SAXException, IOException {
        String lengthField = "";
        int length;
        try {
            lengthField = parseStringFromNextElement();
            length = Integer.parseInt(lengthField);
        } catch (EDISyntaxException e) {
            EDISyntaxException se = new EDISyntaxException(ErrorMessages.MISSING_UNO_LENGTH, getTokenizer());
            logger.warn(se.getMessage());
            throw se;
        } catch (NumberFormatException e) {
            EDISyntaxException se = new EDISyntaxException("UNO object length must be numeric instead of " + lengthField, getTokenizer());
            logger.warn(se.getMessage());
            throw se;
        }

        String packageReference = parseStringFromNextElement();

        getTokenizer().skipSegment();

        char[] dataObject = getTokenizer().getChars(length);

        Token token = getTokenizer().nextToken();

        if (token.getType() == Token.TokenType.SEGMENT_START && "UNP".equals(token.getSegmentType())) {
            String unpLengthField = parseStringFromNextElement();

            int unpLength;
            try {
                unpLength = Integer.parseInt(unpLengthField);
            } catch (NumberFormatException e) {
                EDISyntaxException se = new EDISyntaxException("UNP object length must be numeric instead of " + unpLengthField);
                logger.warn(se.getMessage());
                throw se;
            }
            if (length != unpLength) {
                EDISyntaxException se = new EDISyntaxException(ErrorMessages.MISMATCHED_UNP_LENGTH, length, unpLength, getTokenizer());
                logger.warn(se.getMessage());
                throw se;
            }

            String unpPackageReference = parseStringFromNextElement();
            if (unpPackageReference == null || !unpPackageReference.equals(packageReference)) {
                EDISyntaxException se = new EDISyntaxException(ErrorMessages.MISMATCHED_PACKAGE_REF, packageReference, unpPackageReference, getTokenizer());
                logger.warn(se.getMessage());
                throw se;
            }

            getTokenizer().skipSegment();
        } else {
            EDISyntaxException se = new EDISyntaxException(ErrorMessages.MISSING_UNP);
            logger.warn(se.getMessage());
            throw se;
        }

        getDocumentAttributes().clear();
        getDocumentAttributes().addCDATA(getXMLTags().getIdAttribute(), packageReference);
        startElement(getXMLTags().getPackageTag(), getDocumentAttributes());
        new ContentHandlerBase64Encoder().encode(dataObject, getContentHandler());
        endElement(getXMLTags().getPackageTag());
    }

    /**
     * Preview the EDI input before attempting to tokenize it in order to
     * discover syntactic details including segment terminator and element
     * delimiter. Upon return, the input stream must be re-positioned so that
     * the tokenizer can read from the beginning of the interchange.
     *
     * @throws EDISyntaxException if invalid EDI is detected
     * @throws IOException        for problem reading EDI data
     */
    @Override
    public void preview() throws EDISyntaxException, IOException {
        char[] buf = getTokenizer().lookahead(PREVIEW_LENGTH);

        if (!(buf[0] == 'U' && buf[1] == 'N')) {
            EDISyntaxException se = new EDISyntaxException("EDIFACT interchange must begin with UN");
            logger.warn(se.getMessage());
            throw se;
        }

        if (isPreviewed()) {
            EDISyntaxException se = new EDISyntaxException("Internal error: EDIFACT interchange previewed more than once");
            logger.warn(se.getMessage());
            throw se;
        }

        // Now we establish subDelimiter, delimiter, release, and terminator.
        // If there is a UNA segment, we get the values from that. If there
        // is no UNA, then we look to the UNB and use the defaults associated
        // with the Syntax Code found there. However, if the release char
        // in a UNA is space, then it means "not specified" and that there
        // is no release character at all (i.e., no release character
        // processing for this interchange). This is not the only
        // reasonable interpretation of the EDIFACT standards, but
        // one that is commonly used.
        //
        // So our approach will be to react to a UNA if one is there and
        // note which of the 4 attributes are established. If one or more
        // of the 4 is not established, then and only then do we shift the
        // buffer and look at the UNB to establish those attributes not
        // yet established.
        boolean subDelimiterDetermined = false;
        boolean delimiterDetermined = false;
        boolean releaseDetermined = false;
        boolean decimalMarkDetermined = false;
        boolean terminatorDetermined = false;
        boolean terminatorSuffixDetermined = false;

        setTerminatorSuffix("");

        if (buf[2] == 'A') {

            // UNA......
            // 012345678

            setSubDelimiter(buf[3]);
            subDelimiterDetermined = true;
            setDelimiter(buf[4]);
            delimiterDetermined = true;
            setDecimalMark(buf[5]);
            decimalMarkDetermined = true;

            if (buf[6] == ' ') {
                // no release processing
                setRelease(-1);
            } else {
                setRelease(buf[6]);
            }
            releaseDetermined = true;

            if (buf[7] == ' ') {
                // no repetition character specified
                setRepetitionSeparator('\000');
            } else {
                setRepetitionSeparator(buf[7]);
            }
            setTerminator(buf[8]);
            terminatorDetermined = true;
            terminatorSuffixDetermined = shiftUNBoverUNA(buf);
        }

        previewUNB(buf, delimiterDetermined, subDelimiterDetermined,
                decimalMarkDetermined, releaseDetermined,
                terminatorDetermined, terminatorSuffixDetermined);
        setPreviewed(true);
    }

    /**
     * Shift the buffer to look at the UNB. This is a little tricky because we
     * don't know exactly how many bytes to shift. We need to find the first U
     * soon after the end of the UNA segment. Remember, there might be
     * whitespace chars between the terminator and the UNB. Take note of this
     * whitespace, saving it as a terminatorSuffix, so that a segment could be
     * generated with matching whitespace conventions.
     *
     * @param buf buffer containing chars to be shifted
     * @return true if a terminator suffix was recognized
     */
    private boolean shiftUNBoverUNA(char[] buf) {
        boolean terminatorSuffixDetermined = false;
        int nShift = 9;
        for (int j = 9; j < 14; j++) {
            // buf[9] is the 1st char after UNA terminator
            if (Character.isLetter(buf[j])) {
                nShift = j;
                break;
            }
            setTerminatorSuffix(getTerminatorSuffix() + buf[j]);
            terminatorSuffixDetermined = true;
        }
        for (int j = 0; j < buf.length - nShift; j++) {
            buf[j] = buf[j + nShift];
        }
        return terminatorSuffixDetermined;
    }

    private void previewUNB(char[] buf, boolean delimiterDetermined,
                            boolean subDelimiterDetermined, boolean decimalMarkDetermined, boolean releaseDetermined,
                            boolean terminatorDetermined, boolean terminatorSuffixDetermined)
            throws EDISyntaxException {

        // UNB+UNOA...
        // 01234567

        if (buf[2] != 'B') {
            EDISyntaxException se = new EDISyntaxException("Required UNB segment not found in EDIFACT interchange");
            logger.warn(se.getMessage());
            throw se;
        }

        Charset designatedCharset;
        InputSource inputSource;
        InputStream byteStream;
        String msg;

        inputSource = getInputSource();
        if (inputSource == null) {
            msg = "EDIFACT parser must have an InputSource.";
            EDISyntaxException se = new EDISyntaxException(msg);
            logger.warn(se.getMessage());
            throw se;
        }

        EDITokenizer newTokenizer;
        Reader replacementReader;
        char[] preRead;
        char syntaxIdentifier = buf[7];
        switch (syntaxIdentifier) {
            case 'A': // UNOA
                byteStream = inputSource.getByteStream();
                if (byteStream == null) {
                    // We already have a character stream and a Reader ready to use.
                    setSyntaxCharacters(buf, delimiterDetermined, subDelimiterDetermined, decimalMarkDetermined, releaseDetermined, terminatorDetermined, syntaxIdentifier);
                    break;
                } else {
                    // We have a byte stream that needs to be decoded as ISO-8859-1.
                    // The preview was already decoded.
                    designatedCharset = Charset.forName("ISO-8859-1");
                    replacementReader = new InputStreamReader(byteStream, designatedCharset);
                    preRead = getPreviewString().toCharArray();
                    newTokenizer = new EDITokenizer(replacementReader, preRead);
                    setInputReader(replacementReader);
                    setTokenizer(newTokenizer);
                }
            case 'C': // UNOC
                designatedCharset = Charset.forName("ISO-8859-5");
                byteStream = inputSource.getByteStream();
                if (byteStream == null) {
                    msg = "EDIFACT parser with UNB+UNOE must be created with a byte stream InputSource.";
                    EDISyntaxException se = new EDISyntaxException(msg);
                    logger.warn(se.getMessage());
                    throw se;
                }
                replacementReader = new InputStreamReader(byteStream, designatedCharset);
                preRead = getPreviewString().toCharArray();
                newTokenizer = new EDITokenizer(replacementReader, preRead);
                setInputReader(replacementReader);
                setTokenizer(newTokenizer);
                setSyntaxCharacters(buf, delimiterDetermined, subDelimiterDetermined, decimalMarkDetermined, releaseDetermined, terminatorDetermined, syntaxIdentifier);
                break;
            case 'D': // UNOD
                designatedCharset = Charset.forName("ISO-8859-2");
                byteStream = inputSource.getByteStream();
                if (byteStream == null) {
                    msg = "EDIFACT parser with UNB+UNOD must be created with a byte stream InputSource.";
                    EDISyntaxException se = new EDISyntaxException(msg);
                    logger.warn(se.getMessage());
                    throw se;
                }
                replacementReader = new InputStreamReader(byteStream, designatedCharset);
                preRead = getPreviewString().toCharArray();
                newTokenizer = new EDITokenizer(replacementReader, preRead);
                setInputReader(replacementReader);
                setTokenizer(newTokenizer);
                setSyntaxCharacters(buf, delimiterDetermined, subDelimiterDetermined, decimalMarkDetermined, releaseDetermined, terminatorDetermined, syntaxIdentifier);
                break;
            case 'E': // UNOE
                designatedCharset = Charset.forName("ISO-8859-5");
                byteStream = inputSource.getByteStream();
                if (byteStream == null) {
                    msg = "EDIFACT parser with UNB+UNOE must be created with a byte stream InputSource.";
                    EDISyntaxException se = new EDISyntaxException(msg);
                    logger.warn(se.getMessage());
                    throw se;
                }
                replacementReader = new InputStreamReader(byteStream, designatedCharset);
                preRead = getPreviewString().toCharArray();
                newTokenizer = new EDITokenizer(replacementReader, preRead);
                setInputReader(replacementReader);
                setTokenizer(newTokenizer);
                setSyntaxCharacters(buf, delimiterDetermined, subDelimiterDetermined, decimalMarkDetermined, releaseDetermined, terminatorDetermined, syntaxIdentifier);
                break;
            case 'B':
                if (!delimiterDetermined && buf[3] == '+') {
                    // Strange data. It seems that there was no UNA to determine the syntax characters, and
                    // the UNB segment looked like UNB:UNOB+....
                    // The B in UNOB says that the syntax characters are supposed to be hex !D, 1F, and 1C,
                    // but actual delimiter appears to be a +. Which one should we believe?
                    // Let's believe the actual data for the delimiter, and guess that if the delimiter is a +
                    // then the other two will be the values that traditionally go with a +. We achieve this
                    // by simply falling through this case.
                } else {

                    if (!delimiterDetermined) {
                        setDelimiter('\u001D');
                        delimiterDetermined = true;
                    }

                    if (!subDelimiterDetermined) {
                        setSubDelimiter('\u001F');
                        subDelimiterDetermined = true;
                    }

                    if (!terminatorDetermined) {
                        setTerminator('\u001C');
                        terminatorDetermined = true;
                    }

                    setRelease(-1);
                    releaseDetermined = true;

                    setRepetitionSeparator('\u0019');
                }
                // Deliberately fall into the sequence below
            default:
                setSyntaxCharacters(buf, delimiterDetermined, subDelimiterDetermined, decimalMarkDetermined, releaseDetermined, terminatorDetermined, syntaxIdentifier);
                break;
        }

        if (!terminatorSuffixDetermined)
            // We still have not observed a terminator suffix
            // following the first terminator in the interchange.
            // Therefore, we must scan the buffer until we see the
            // segment terminator, and then note suffix characters
            // following.
            setTerminatorSuffix(scanForSuffix(buf, 3));
    }

    private void setSyntaxCharacters(char[] buf, boolean delimiterDetermined, boolean subDelimiterDetermined, boolean decimalMarkDetermined, boolean releaseDetermined, boolean terminatorDetermined, char syntaxIdentifier) throws EDISyntaxException {
        if (!Character.isLetter(syntaxIdentifier)) {
            EDISyntaxException se = new EDISyntaxException("Unknown Syntax Identifier in UNB segment: " + new String(buf, 4, 4));
            logger.warn(se.getMessage());
            throw se;
        }

        if (!delimiterDetermined)
            setDelimiter('+');

        if (buf[3] != getDelimiter()) {
            EDISyntaxException se = new EDISyntaxException("Expected data element separator after UNB segment tag");
            logger.warn(se.getMessage());
            throw se;
        }

        if (!terminatorDetermined)
            setTerminator('\'');

        if (!subDelimiterDetermined)
            setSubDelimiter(':');

        if (!decimalMarkDetermined)
            setDecimalMark('.');

        if (!releaseDetermined)
            setRelease('?');
    }

    protected String scanForSuffix(char[] buffer, int index) {
        StringBuilder suffix = new StringBuilder();
        for (int i = index; i < buffer.length; i++) {
            if (buffer[i] == getTerminator()) {
                for (int j = 1; j < 3; j++) {
                    i++;
                    if (i < buffer.length && !Character.isLetter(buffer[i])) {
                        suffix.append(buffer[i]);
                    }
                }
                break;
            }
        }
        return suffix.toString();
    }

    public boolean isUNA() {
        return witnessedUNA;
    }
}
