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

import com.berryworks.edireader.error.ISA16SubElementDelimiterException;
import com.berryworks.edireader.error.ISAFixedLengthException;
import com.berryworks.edireader.error.MissingMandatoryElementException;
import com.berryworks.edireader.error.RecoverableSyntaxException;
import com.berryworks.edireader.tokenizer.Token;
import com.berryworks.edireader.util.ContentHandlerBase64Encoder;
import com.berryworks.edireader.util.FixedLength;
import com.berryworks.edireader.util.sax.QueuedContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

import static com.berryworks.edireader.tokenizer.Token.TokenType.SEGMENT_END;
import static com.berryworks.edireader.tokenizer.Token.TokenType.SEGMENT_START;
import static com.berryworks.edireader.util.EdiVersionUtil.isX12VersionBefore;
import static java.lang.Character.*;

/**
 * Reads and parses ANSI X.12 EDI interchanges. This class is not normally
 * constructed explicitly from outside the package, although it is declared
 * public for special cases. The recommended use of this class is to first
 * establish an EDIReader using one of the factory techniques; when the
 * EDIReader is called upon to parse the EDI data, it determines which EDI
 * standard applies and internally constructs the proper subclass to continue
 * with parsing.
 */
public class AnsiReader extends StandardReader {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    /**
     * Group-level function code (for example: PO) GS01
     */
    protected String groupFunctionCode;

    /**
     * Group-level application sender GS02
     */
    protected String groupSender;

    /**
     * Group-level application receiver GS03
     */
    protected String groupReceiver;

    /**
     * Group-level date (for example: 20040410 or 040410) GS04
     */
    protected String groupDate;

    /**
     * Group-level version (for example: 003040)
     */
    protected String groupVersion;


    @Override
    protected Token recognizeBeginning() throws IOException, EDISyntaxException {
        Token t = getTokenizer().nextToken();
        if ((t.getType() != SEGMENT_START) || (!t.valueEquals("ISA"))) {
            logger.warn(X12_MISSING_ISA);
            throw new EDISyntaxException(X12_MISSING_ISA);
        }
        return t;
    }

    /**
     * Parse ANSI Interchange (ISA ...)
     */
    @Override
    protected Token parseInterchange(Token token) throws SAXException, IOException {
        setGroupCount(0);

        getInterchangeAttributes().clear();
        getInterchangeAttributes().addCDATA(getXMLTags().getStandard(), EDIStandard.ANSI.getDisplayName());

        String authQual = checkFixedLength("ISA01", nextField(), 2);
        String authInfo = checkFixedLength("ISA02", nextField(), 10);
        String securityQual = checkFixedLength("ISA03", nextField(), 2);
        String securityInfo = checkFixedLength("ISA04", nextField(), 10);

        getInterchangeAttributes().addCDATA(getXMLTags().getAuthorizationQual(), authQual);
        getInterchangeAttributes().addCDATA(getXMLTags().getAuthorization(), authInfo);
        getInterchangeAttributes().addCDATA(getXMLTags().getSecurityQual(), securityQual);
        getInterchangeAttributes().addCDATA(getXMLTags().getSecurity(), securityInfo);

        String fromQual = checkFixedLength("ISA05", nextField(), 2);
        process("ISA05", fromQual);

        String fromId = checkFixedLength("ISA06", nextField(), 15);
        process("ISA06", fromId);

        String toQual = checkFixedLength("ISA07", nextField(), 2);
        process("ISA07", toQual);

        String toId = checkFixedLength("ISA08", nextField(), 15);
        process("ISA08", toId);

        String controlDate = checkFixedLength("ISA09", nextField(), 6);
        getInterchangeAttributes().addCDATA(getXMLTags().getDate(), controlDate);

        String controlTime = checkFixedLength("ISA10", nextField(), 4);
        getInterchangeAttributes().addCDATA(getXMLTags().getTime(), controlTime);

        // The standards id, typically "U", through version 4010
        // The repetition character, version 4020 and later
        int separator = getTokenizer().getRepetitionSeparator();
        if (separator == -1) {
            // No repetition char is in effect. It is therefore safe to interpret this next
            // element as the standardsId used through version 4010 of ANSI X12.
            String standardsId = checkFixedLength("ISA11", nextField(), 1);
            getInterchangeAttributes().addCDATA(getXMLTags().getStandardsId(), standardsId);
        } else {
            // A repetition char is in effect, presumably previewed from this ISA segment we
            // are now parsing. Therefore, we treat this field in accordance with version 4020
            // or later where it designates a repetition character instead of a standardsId.
            // Temporarily disable the repetition char so that we can parse over this element
            // as normal data.
            getTokenizer().setRepetitionSeparator(-1);
            checkFixedLength("ISA11", nextField(), 1);
            getTokenizer().setRepetitionSeparator(separator);
        }

        String versionId = checkFixedLength("ISA12", nextField(), 5);
        getInterchangeAttributes().addCDATA(getXMLTags().getVersion(), versionId);

        String controlNumber = checkFixedLength("ISA13", nextField(), 9);
        process("ISA13", controlNumber);
        setInterchangeControlNumber(controlNumber);
        getInterchangeAttributes().addCDATA(getXMLTags().getControl(), getInterchangeControlNumber());

        String ackRequest = checkFixedLength("ISA14", nextField(), 1);
        getInterchangeAttributes().addCDATA(getXMLTags().getAcknowledgementRequest(), ackRequest);

        String testIndicator = checkFixedLength("ISA15", nextField(), 1);
        getInterchangeAttributes().addCDATA(getXMLTags().getTestIndicator(), testIndicator);

        // We should have already noted ISA16, the sub-element delimiter, when we previewed this interchange.
        // If one was not established, then report it as a recoverable syntax error.
        if (getSubDelimiter() == '\000') {
            RecoverableSyntaxException syntaxException = new ISA16SubElementDelimiterException();
            if (!recover(syntaxException)) {
                throw syntaxException;
            }
        }

        // At this point, we have parsed all the elements we need from the ISA, but we will keep looking
        // for more elements before the end of the segment. If we do not find the segment end soon,
        // then there is a serious issue with the segment terminator. Better to report this now
        // instead of attempting to parse more segments of this interchange.
        while (getTokenizer().nextToken().getType() != SEGMENT_END)
            if (getTokenizer().getElementInSegmentCount() > 30) {
                EDISyntaxException se = new EDISyntaxException(TOO_MANY_ISA_FIELDS, getTokenizer());
                logger.warn(se.getMessage());
                throw se;
            }

        if (isIncludeSyntaxCharacters()) {
            // Provide critical syntax characters as attributes
            final char repetitionSeparator = getRepetitionSeparator();
            if (repetitionSeparator > 0) {
                getInterchangeAttributes().addCDATA(getXMLTags().getRepetitionSeparator(), String.valueOf(repetitionSeparator));
            }
            getInterchangeAttributes().addCDATA(getXMLTags().getElementDelimiter(), String.valueOf(getDelimiter()));
            getInterchangeAttributes().addCDATA(getXMLTags().getSubElementDelimiter(), String.valueOf(getSubDelimiter()));
            getInterchangeAttributes().addCDATA(getXMLTags().getSegmentTerminator(), String.valueOf(getTerminator()));
        }

        // Now make the callbacks to the ContentHandler
        startInterchange(getInterchangeAttributes());

        getInterchangeAttributes().clear();
        startElement(getXMLTags().getSenderTag(), getInterchangeAttributes());
        getInterchangeAttributes().addCDATA(getXMLTags().getIdAttribute(), fromId);
        getInterchangeAttributes().addCDATA(getXMLTags().getQualifierAttribute(),
                fromQual);
        startSenderAddress(getInterchangeAttributes());
        endElement(getXMLTags().getAddressTag());
        endElement(getXMLTags().getSenderTag());

        getInterchangeAttributes().clear();
        startElement(getXMLTags().getReceiverTag(), getInterchangeAttributes());
        getInterchangeAttributes().addCDATA(getXMLTags().getIdAttribute(), toId);
        getInterchangeAttributes().addCDATA(getXMLTags().getQualifierAttribute(),
                toQual);
        startReceiverAddress(getInterchangeAttributes());
        endElement(getXMLTags().getAddressTag());
        endElement(getXMLTags().getReceiverTag());

        label:
        while (true) {
            token = getTokenizer().nextToken();
            if (token.getType() != SEGMENT_START) {
                EDISyntaxException se = new EDISyntaxException(INVALID_BEGINNING_OF_SEGMENT, getTokenizer().getSegmentCount());
                logger.warn(se.getMessage());
                throw se;
            }
            String sType = token.getValue();
            switch (sType) {
                case "GS":
                    setGroupCount(1 + getGroupCount());
                    parseFunctionalGroup(token);
                    break;
                case "TA1":
                    parseTA1(token);
                    break;
                case "IEA":
                    break label;
                default:
                    EDISyntaxException se = new EDISyntaxException(UNEXPECTED_SEGMENT_IN_CONTEXT, "IEA or GS", sType, getTokenizer());
                    logger.warn(se.getMessage());
                    throw se;
            }
        }

        checkGroupCount(getGroupCount(), getTokenizer().nextIntValue(true), COUNT_IEA);
        String ieaControlNumber = nextField();
        if (ieaControlNumber == null) {
            ieaControlNumber = "(omitted)";
        }
        checkInterchangeControlNumber(getInterchangeControlNumber(), ieaControlNumber, CONTROL_NUMBER_IEA);
        getAckGenerator().generateAcknowledgementWrapup();
        getAlternateAckGenerator().generateAcknowledgementWrapup();
        endInterchange();
        return (getTokenizer().skipSegment());
    }

    private String nextField() throws SAXException, IOException {
        return getTokenizer().nextSimpleValue(false, true);
    }

    /**
     * Checks an ISA field value for compliance with the known fixed length.
     * If the value is of the right length, then this simply returns that value unchanged.
     * If the value is not the right length, then the method does one of two things;
     * it either returns the value trimmed or padded at the end to the correct length, or
     * it throws an ISAFixedLengthException. The decision between the two options is based on
     * whether there is an EDISyntaxExceptionHandler in place. If not, the exception is throw;
     * if so, then that handler gets to decide whether to throw the exception.
     */
    private String checkFixedLength(String elementName, String value, int expectedLength) throws EDISyntaxException {
        if (value == null) {
            throw new EDISyntaxException(ISA_SEGMENT_HAS_TOO_FEW_FIELDS, getTokenizer());
        } else if (value.length() != expectedLength) {
            RecoverableSyntaxException re = new ISAFixedLengthException(elementName, expectedLength, value.length(), getTokenizer());
            setSyntaxException(re);
            if (!recover(re)) {
                throw re;
            }
            value = FixedLength.valueOf(value, expectedLength);
        }
        return value;
    }

    protected void parseTA1(Token token) throws SAXException, IOException {

        EDIAttributes attributes = new EDIAttributes();

        String acknowledgedControlNumber = getTokenizer().nextSimpleValue();
        attributes.addCDATA(getXMLTags().getControl(), acknowledgedControlNumber);

        String acknowledgedDate = getTokenizer().nextSimpleValue();
        attributes.addCDATA(getXMLTags().getDate(), acknowledgedDate);

        String acknowledgedTime = getTokenizer().nextSimpleValue();
        attributes.addCDATA(getXMLTags().getTime(), acknowledgedTime);

        String code = getTokenizer().nextSimpleValue();
        attributes.addCDATA(getXMLTags().getAcknowledgementCode(), code);

        String note = nextField();
        if (note != null) {
            if (note.length() > 0) {
                attributes.addCDATA(getXMLTags().getNotCode(), note);
            }
            getTokenizer().skipSegment();
        }
        startElement(getXMLTags().getAcknowledgementTag(), attributes);
        endElement(getXMLTags().getAcknowledgementTag());
    }

    /**
     * Parse ANSI Functional Group (GS .. GE)
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
        getGroupAttributes().addCDATA(getXMLTags().getGroupType(),
                groupFunctionCode = getTokenizer().nextSimpleValue(false));
        groupSender = getTokenizer().nextSimpleValue(false);
        process("GS02", groupSender);
        groupReceiver = getTokenizer().nextSimpleValue(false);
        process("GS03", groupReceiver);
        groupDate = getTokenizer().nextSimpleValue(false);
        getGroupAttributes().addCDATA(getXMLTags().getApplSender(), groupSender);
        getGroupAttributes().addCDATA(getXMLTags().getApplReceiver(), groupReceiver);
        getGroupAttributes().addCDATA(getXMLTags().getDate(), groupDate);

        String value = "";
        try {
            value = getTokenizer().nextSimpleValue();
        } catch (EDISyntaxException e) {
            if (e.getMessage().startsWith("Mandatory")) {
                MissingMandatoryElementException missingMandatoryElementException =
                        new MissingMandatoryElementException(MANDATORY_ELEMENT_MISSING,
                                "at least one non-space character", "(empty)", getTokenizer());
                setSyntaxException(missingMandatoryElementException);
                if (!recover(missingMandatoryElementException)) {
                    logger.warn(missingMandatoryElementException.getMessage());
                    throw missingMandatoryElementException;
                }
            } else {
                logger.warn(e.getMessage());
                throw e;
            }

        }
        getGroupAttributes().addCDATA(getXMLTags().getTime(), value);

        String groupControlNumber = getTokenizer().nextSimpleValue(false);
        setGroupControlNumber(groupControlNumber);
        process("GS06", groupControlNumber);
        getGroupAttributes().addCDATA(getXMLTags().getControl(), getGroupControlNumber());
        getGroupAttributes().addCDATA(getXMLTags().getStandardCode(), getTokenizer().nextSimpleValue(false));

        // Handle the groupVersion at the end of the segment. This is a bit tricky since
        // the groupVersion may be omitted causing us to encounter the end of segment earlier
        // than expected.
        Token t = getTokenizer().nextToken();
        if (t.getType() == SEGMENT_END) {
            groupVersion = "";
        } else {
            groupVersion = t.getValue();
            if (isX12VersionBefore(groupVersion, 4020)) {
                getTokenizer().setRepetitionSeparator(-1);
            }
            getGroupAttributes().addCDATA(getXMLTags().getStandardVersion(), groupVersion);
            process("GS08", groupVersion);
            getTokenizer().skipSegment();
        }

        startElement(getXMLTags().getGroupTag(), getGroupAttributes());

        int groupDateLength = versionSpecificGroupDateLength(groupVersion);
        getAckGenerator().generateAcknowledgmentHeader(getFirstSegment(),
                groupSender, groupReceiver, groupDateLength, groupVersion,
                groupFunctionCode, getGroupControlNumber());
        getAlternateAckGenerator().generateAcknowledgmentHeader(getFirstSegment(),
                groupSender, groupReceiver, groupDateLength, groupVersion,
                groupFunctionCode, getGroupControlNumber());

        label:
        while (true) {
            token = getTokenizer().nextToken();
            if (token.getType() != SEGMENT_START) {
                EDISyntaxException se = new EDISyntaxException(INVALID_BEGINNING_OF_SEGMENT, getTokenizer().getSegmentCount());
                logger.warn(se.getMessage());
                throw se;
            }
            String sType = token.getValue();
            switch (sType) {
                case "ST":
                    docCount++;
                    parseDocument(token);
                    break;
                case "GE":
                    break label;
                default:
                    EDISyntaxException se = new EDISyntaxException(UNEXPECTED_SEGMENT_IN_CONTEXT, "GE or ST", sType, getTokenizer());
                    logger.warn(se.getMessage());
                    throw se;
            }
        }

        // Check GE trailer segment for this functional group
        checkTransactionCount(docCount, getTokenizer().nextIntValue(true), COUNT_GE);
        groupControlNumber = nextField();
        if (groupControlNumber == null) {
            groupControlNumber = "(omitted)";
        }
        checkGroupControlNumber(getGroupControlNumber(), groupControlNumber, CONTROL_NUMBER_GE);

        endElement(getXMLTags().getGroupTag());
        getAckGenerator().generateGroupAcknowledgmentTrailer(docCount);
        getAlternateAckGenerator().generateGroupAcknowledgmentTrailer(docCount);
        return (getTokenizer().skipSegment());
    }

    protected void process(String ediElement, String value) throws SAXException {
    }

    /**
     * Parse ANSI Document/Transaction Set (ST .. SE)
     *
     * @param token parsed token that caused this method to be called
     * @return token most recently parsed by this method
     * @throws SAXException for problem emitting SAX events
     * @throws IOException  for problem reading EDI data
     */
    protected Token parseDocument(Token token) throws SAXException,
            IOException {
        String controlNumber;
        String documentType;
        Token t;
        int segCount = 2;

        if (getTransactionCallback() != null)
            getTransactionCallback().startTransaction(token.getValue());

        getDocumentAttributes().clear();
        getDocumentAttributes().addCDATA(getXMLTags().getDocumentType(),
                documentType = getTokenizer().nextSimpleValue(false));

        logger.debug("Parsing {} transaction", documentType);

        String version = groupVersion;
        if (version.length() > 6) version = version.substring(0, 6);
        String code = getGroupAttributes().getValue(getXMLTags().getStandardCode());

        PluginController pluginController =
                getPluginControllerFactory().create("ANSI", documentType, code, version, getTokenizer());

        boolean wrapped = wrapContentHandlerIfNeeded(pluginController);
        if (pluginController.isEnabled())
            getDocumentAttributes().addCDATA(getXMLTags().getName(), pluginController.getDocumentName());

        controlNumber = nextField();
        boolean hitSegmentEnd = false;
        if (controlNumber == null) {
            // Edge case. The segment terminator immediately followed the ST01 document type element.
            // That means we've already hit the SEGMENT_END;
            hitSegmentEnd = true;
            controlNumber = "";
        }
        getDocumentAttributes().addCDATA(getXMLTags().getControl(), controlNumber);
        process("ST02", controlNumber);

        if (!hitSegmentEnd) {
            Token st03Token = getTokenizer().nextToken();
            switch (st03Token.getType()) {
                case SEGMENT_END:
                    break;
                case SIMPLE:
                    getDocumentAttributes().addCDATA(getXMLTags().getMessageVersion(), st03Token.getValue());
                default:
                    getTokenizer().skipSegment();
            }
        }

        startMessage(getDocumentAttributes());

        String segmentType;
        while (!(segmentType = getTokenizer().nextSegment()).equals("SE")) {
            if (isEnvelopeSegment(segmentType)) {
                EDISyntaxException se = new EDISyntaxException(SE_MISSING, getTokenizer());
                logger.warn(se.getMessage());
                throw se;
            }
            segCount++;

            if ("BIN".equals(segmentType)) {
                parseBINSequence();
            } else {
                parseSegment(pluginController, segmentType);
            }
        }

        if (wrapped)
            unwrapContentHandler(pluginController);

        int toClose = pluginController.getNestingLevel();
        for (; toClose > 0; toClose--)
            endElement(getXMLTags().getLoopTag());

        checkSegmentCount(segCount, getTokenizer().nextIntValue(true), COUNT_SE);
        checkTransactionControlNumber(controlNumber, nextField(), CONTROL_NUMBER_SE);
        getAckGenerator().generateTransactionAcknowledgment(documentType, controlNumber);
        getAlternateAckGenerator().generateTransactionAcknowledgment(documentType, controlNumber);
        endElement(getXMLTags().getDocumentTag());

        // Skip over this SE segment
        // return the SEGMENT_END token
        t = getTokenizer().skipSegment();

        if (getTransactionCallback() != null)
            getTransactionCallback().endTransaction();

        return t;
    }

    private boolean wrapContentHandlerIfNeeded(PluginController pluginController) {
        boolean result = false;
        ContentHandler contentHandler = getContentHandler();
        if (contentHandler instanceof QueuedContentHandler) {
            // If it is already queued, then no need to wrap it
        } else {
            if (pluginController.isQueuedContentHandlerRequired()) {
                setContentHandler(new QueuedContentHandler(contentHandler, 10, getTokenizer()));
                result = true;
            }
        }
        return result;
    }

    private void unwrapContentHandler(PluginController pluginController) {
        ContentHandler contentHandler = getContentHandler();
        if (contentHandler instanceof QueuedContentHandler) {
            QueuedContentHandler queuedContentHandler = (QueuedContentHandler) contentHandler;
            try {
                queuedContentHandler.drainQueue();
            } catch (SAXException ignore) {
            }
            setContentHandler(queuedContentHandler.getWrappedContentHandler());
        }
    }

    protected int versionSpecificGroupDateLength(String groupVersion) {
        // The proper length of the GS04 date in an X12 function group varies with the X12 version.
        // Beginning with 004010, the maximum length is 8; before that it is 6.
        // We want to generate an acknowledgment of the same version as the EDI input, but with the correct
        // length of the GS04 date field even if it was wrong in the input.
        return (isX12VersionBefore(groupVersion, 4010)) ? 6 : 8;
    }

    public static boolean isEnvelopeSegment(String segmentType) {
        return "ISA".equals(segmentType) || "GS".equals(segmentType) || "ST".equals(segmentType) ||
                "SE".equals(segmentType) || "GE".equals(segmentType) || "IEA".equals(segmentType) ||
                "TA1".equals(segmentType);
    }

    protected void parseBINSequence() throws SAXException, IOException {
        String lengthField = "";
        int length;
        try {
            lengthField = parseStringFromNextElement();
            length = Integer.parseInt(lengthField);
        } catch (EDISyntaxException e) {
            EDISyntaxException se = new EDISyntaxException(MISSING_BIN_LENGTH, getTokenizer());
            logger.warn(se.getMessage());
            throw se;
        } catch (NumberFormatException e) {
            EDISyntaxException se = new EDISyntaxException("BIN object length must be numeric instead of " + lengthField, getTokenizer());
            logger.warn(se.getMessage());
            throw se;
        }

        char[] dataObject = getTokenizer().getChars(length);
        getTokenizer().nextToken();

        getDocumentAttributes().clear();
        startElement(getXMLTags().getPackageTag(), getDocumentAttributes());
        new ContentHandlerBase64Encoder().encode(dataObject, getContentHandler());
        endElement(getXMLTags().getPackageTag());
    }


    /**
     * Preview the ANSI X.12 input before attempting to tokenize it in order to
     * discover syntactic details including segment terminator and field
     * delimiter. Upon return, the input stream has been re-positioned so that
     * the tokenizer can read from the beginning of the interchange.
     *
     * @throws EDISyntaxException if invalid EDI is detected
     * @throws IOException        for problem reading EDI data
     */
    @Override
    public void preview() throws EDISyntaxException, IOException {
        if (isPreviewed()) {
            logger.warn(INTERNAL_ERROR_MULTIPLE_EOFS);
            throw new EDISyntaxException(INTERNAL_ERROR_MULTIPLE_EOFS);
        }
        // No release character is supported for ANSI X.12
        setRelease(-1);

        char[] buf = getTokenizer().lookahead(128);
        if ((buf == null) || (buf.length < 128)) {
            logger.warn(INCOMPLETE_X12);
            throw new EDISyntaxException(INCOMPLETE_X12);
        }

        if (!(buf[0] == 'I' && buf[1] == 'S' && buf[2] == 'A')) {
            logger.warn(X12_MISSING_ISA);
            throw new EDISyntaxException(X12_MISSING_ISA);
        }

        // ISA*
        // ...^ (offset 3)
        char c = buf[3];
        setDelimiter(c);

        int indexOf16thFieldSeparator = indexOf(c, buf, 16);
        if (indexOf16thFieldSeparator < 0) {
            logger.warn(ISA_SEGMENT_HAS_TOO_FEW_FIELDS);
            throw new EDISyntaxException(ISA_SEGMENT_HAS_TOO_FEW_FIELDS);
        }

        // Determine the sub-element delimiter
        c = buf[indexOf16thFieldSeparator + 1];
        if (isAcceptable(c))
            setSubDelimiter(c);
        else {
            // Continue with it unset, allowing an invalid value to be treated as a recoverable syntax error.
            logger.warn(INVALID_SUB_ELEMENT_DELIMITER);
        }

        // Determine the segment terminator
        c = buf[indexOf16thFieldSeparator + 2];
        if (isAcceptable(c))
            setTerminator(c);
        else {
            logger.warn(INVALID_SEGMENT_TERMINATOR);
            // This is not recoverable.
            throw new EDISyntaxException(INVALID_SEGMENT_TERMINATOR);
        }
        setTerminatorSuffix(findTerminatorSuffix(buf, indexOf16thFieldSeparator + 3, 128));

        // Determine the repetition character. This changed in 4.6.5 to support repetition chars
        // introduced in version 4020 of the ANSI X12 standard.
        int indexOf11thFieldSeparator = indexOf(getDelimiter(), buf, 11);
        char repetitionChar = buf[indexOf11thFieldSeparator + 1];
        if (Character.isLetterOrDigit(repetitionChar) ||
                repetitionChar == getTerminator() ||
                repetitionChar == getDelimiter() ||
                Character.isWhitespace(repetitionChar)) {
            // This is not a suitable repetition character.
            // It may be desirable to further check to see if the version number
            // in the next ISA element indicates 4020 or later; if not, then
            // repetition characters were not used.
            setRepetitionSeparator('\000');
        } else {
            setRepetitionSeparator(repetitionChar);
        }

        setFirstSegment(new String(buf, 0, indexOf16thFieldSeparator + 3));

        setPreviewed(true);
    }

    private static int indexOf(char c, char[] buf, int n) {
        int result = -1;
        int count = 0;
        for (int i = 0; i < buf.length; i++) {
            if (buf[i] == c) {
                count++;
                if (count == n) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    protected boolean isAcceptable(char c) {
        if (isSpaceChar(c) || isLetter(c) || isDigit(c)) {
            // It cannot be a letter or digit or space
            return false;
        }
        if (getDelimiter() == c) {
            // It cannot be the same as the element delimiter
            return false;
        }
        if (getRepetitionSeparator() != '\000' && getRepetitionSeparator() == c) {
            // It cannot be the same as the repetition separator
            return false;
        }
        if (getSubDelimiter() != '\000' && getSubDelimiter() == c) {
            // It cannot be the same as the sub-element delimiter
            return false;
        }
        if (getTerminator() != '\000' && getTerminator() == c) {
            // It cannot be the same as the segment terminator
            return false;
        }
        return true;
    }

    protected static String findTerminatorSuffix(char[] buf, int i, int j) {
        StringBuilder result = new StringBuilder();
        for (int n = i; n < j && !isLetter(buf[n]); n++)
            result.append(buf[n]);
        return result.toString();
    }

    @Override
    public ReplyGenerator getAckGenerator() {
        if (super.getAckGenerator() == null)
            setAckGenerator(new AnsiFAGenerator(this, getAckStream()));
        return super.getAckGenerator();
    }

    @Override
    public ReplyGenerator getAlternateAckGenerator() {
        if (super.getAlternateAckGenerator() == null)
            setAlternateAckGenerator(new Ansi999Generator(this, getAlternateAckStream()));
        return super.getAlternateAckGenerator();
    }

}
