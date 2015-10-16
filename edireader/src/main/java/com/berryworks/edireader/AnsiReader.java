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

import com.berryworks.edireader.error.*;
import com.berryworks.edireader.tokenizer.Token;
import com.berryworks.edireader.util.ContentHandlerBase64Encoder;
import com.berryworks.edireader.util.FixedLength;
import com.berryworks.edireader.util.sax.QueuedContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;

import static com.berryworks.edireader.tokenizer.Token.TokenType.SEGMENT_END;
import static com.berryworks.edireader.tokenizer.Token.TokenType.SEGMENT_START;

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
    /**
     * Group-level function code (for example: PO)
     */
    protected String groupFunctionCode;

    /**
     * Group-level application sender
     */
    protected String groupSender;

    /**
     * Group-level application receiver
     */
    protected String groupReceiver;

    /**
     * Group-level version (for example: 003040)
     */
    protected String groupVersion;

    /**
     * Group-level date (for example: 20040410 or 040410)
     */
    protected String groupDate;


    @Override
    protected Token recognizeBeginning() throws IOException,
            EDISyntaxException {
        Token t = getTokenizer().nextToken();
        if ((t.getType() != SEGMENT_START) || (!t.valueEquals("ISA"))) {
            throw new EDISyntaxException(X12_MISSING_ISA);
        }
        return t;
    }

    /**
     * Parse ANSI Interchange (ISA ...)
     */
    @Override
    protected Token parseInterchange(Token token) throws SAXException,
            IOException {
        setGroupCount(0);

        getInterchangeAttributes().clear();
        getInterchangeAttributes().addCDATA(getXMLTags().getStandard(), "ANSI X.12");

        String authQual = getFixedLengthISAField(2);
        String authInfo = getFixedLengthISAField(10);
        String securityQual = getFixedLengthISAField(2);
        String securityInfo = getFixedLengthISAField(10);

        getInterchangeAttributes().addCDATA(getXMLTags().getAuthorizationQual(), authQual);
        getInterchangeAttributes().addCDATA(getXMLTags().getAuthorization(), authInfo);
        getInterchangeAttributes().addCDATA(getXMLTags().getSecurityQual(), securityQual);
        getInterchangeAttributes().addCDATA(getXMLTags().getSecurity(), securityInfo);

        String fromQual = getFixedLengthISAField(2);
        process("ISA05", fromQual);
        String fromId = getFixedLengthISAField(15, false);
        process("ISA06", fromId);
        String toQual = getFixedLengthISAField(2);
        process("ISA07", toQual);
        String toId = getFixedLengthISAField(15, false);
        process("ISA08", fromId);
        getInterchangeAttributes().addCDATA(
                getXMLTags().getDate(), getTokenizer().nextSimpleValue());

        // Control time - relax the check for length of exactly 4
        String controlTime = getTokenizer().nextSimpleValue();
        getInterchangeAttributes().addCDATA(getXMLTags().getTime(), controlTime);

        // The standards id, typically "U", through version 4010
        // The repetition character, version 4020 and later
        int separator = getTokenizer().getRepetitionSeparator();
        if (separator == -1) {
            // No repetition char is in effect. It is therefore safe to interpret this next
            // element as the standardsId used through version 4010 of ANSI X12.
            String standardsId = getFixedLengthISAField(1);
            getInterchangeAttributes().addCDATA(getXMLTags().getStandardsId(), standardsId);
        } else {
            // A repetition char is in effect, presumably previewed from this ISA segment we
            // are now parsing. Therefore, we treat this field in accordance with version 4020
            // or later where it repetition character instead of a standardsId.
            // Temporarily disable the repetition char so that we can parse over this element
            // as normal data.
            getTokenizer().setRepetitionSeparator(-1);
            getFixedLengthISAField(1);
            getTokenizer().setRepetitionSeparator(separator);
        }

        String versionId = getFixedLengthISAField(5);
        getInterchangeAttributes().addCDATA(getXMLTags().getVersion(), versionId);
        setInterchangeControlNumber(getFixedLengthISAField(9));
        getInterchangeAttributes().addCDATA(getXMLTags().getControl(),
                getInterchangeControlNumber());


        // Acknowledgement Request
        String ackRequest = getTokenizer().nextSimpleValue();
        getInterchangeAttributes().addCDATA(getXMLTags().getAcknowledgementRequest(), ackRequest);

        // Test Indicator
        String testIndicator = getTokenizer().nextSimpleValue();
        getInterchangeAttributes().addCDATA(getXMLTags().getTestIndicator(), testIndicator);

        // Go ahead and parse tokens until the end of the segment is reached
        while (getTokenizer().nextToken().getType() != SEGMENT_END)
            if (getTokenizer().getElementInSegmentCount() > 30)
                throw new EDISyntaxException(TOO_MANY_ISA_FIELDS, getTokenizer());

        // Now make the the callbacks to the ContentHandler
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
            if (token.getType() != SEGMENT_START)
                throw new EDISyntaxException(INVALID_BEGINNING_OF_SEGMENT,
                        getTokenizer().getSegmentCount());
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
                    throw new EDISyntaxException(UNEXPECTED_SEGMENT_IN_CONTEXT,
                            "IEA or GS", sType, getTokenizer());
            }
        }

        int n;
        if (getGroupCount() != (n = getTokenizer().nextIntValue())) {
            GroupCountException countException = new GroupCountException(COUNT_IEA, getGroupCount(), n, getTokenizer());
            setSyntaxException(countException);
            if (!recover(countException))
                throw countException;
        }
        String s;
        if (!(s = getTokenizer().nextSimpleValue()).equals(getInterchangeControlNumber())) {
            InterchangeControlNumberException interchangeControlNumberException =
                    new InterchangeControlNumberException(CONTROL_NUMBER_IEA, getInterchangeControlNumber(), s, getTokenizer());
            setSyntaxException(interchangeControlNumberException);
            if (!recover(interchangeControlNumberException))
                throw interchangeControlNumberException;
        }

        getAckGenerator().generateAcknowledgementWrapup();
        getAlternateAckGenerator().generateAcknowledgementWrapup();
        endInterchange();
        return (getTokenizer().skipSegment());
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

        String note = getTokenizer().nextSimpleValue(false, true);
        if (note != null) {
            if (note.length() > 0) {
                attributes.addCDATA(getXMLTags().getNotCode(), note);
            }
            getTokenizer().skipSegment();
        }
        startElement(getXMLTags().getAcknowledgementTag(), attributes);
        endElement(getXMLTags().getAcknowledgementTag());
    }

    protected String getFixedLengthISAField(int expectedLength)
            throws IOException, SAXException {
        return getFixedLengthISAField(expectedLength, true);
    }

    protected String getFixedLengthISAField(int expectedLength, boolean enforce)
            throws SAXException, IOException {
        String field = getTokenizer().nextSimpleValue();

        if (field.length() != expectedLength) {
            if (enforce)
                throw new EDISyntaxException(ISA_FIELD_WIDTH, expectedLength, field
                        .length(), getTokenizer());
            else
                field = FixedLength.valueOf(field, expectedLength);
        }

        return field;
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
                groupFunctionCode = getTokenizer().nextSimpleValue());
        groupSender = getTokenizer().nextSimpleValue(false);
        process("GS02", groupSender);
        groupReceiver = getTokenizer().nextSimpleValue(false);
        process("GS03", groupReceiver);
        groupDate = getTokenizer().nextSimpleValue();
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
                if (!recover(missingMandatoryElementException))
                    throw missingMandatoryElementException;
            } else
                throw e;

        }
        getGroupAttributes().addCDATA(getXMLTags().getTime(), value);

        setGroupControlNumber(getTokenizer().nextSimpleValue());
        getGroupAttributes().addCDATA(getXMLTags().getControl(), getGroupControlNumber());
        getGroupAttributes().addCDATA(getXMLTags().getStandardCode(), getTokenizer().nextSimpleValue());

        // Handle the groupVersion at the end of the segment. This is a bit tricky since
        // the groupVersion may be omitted causing us to encounter the end of segment earlier
        // than expected.
        Token t = getTokenizer().nextToken();
        if (t.getType() == SEGMENT_END) {
            groupVersion = "";
        } else {
            groupVersion = t.getValue();
            getGroupAttributes().addCDATA(getXMLTags().getStandardVersion(), groupVersion);
            getTokenizer().skipSegment();
        }

        startElement(getXMLTags().getGroupTag(), getGroupAttributes());

        getAckGenerator().generateAcknowledgmentHeader(getFirstSegment(),
                groupSender, groupReceiver, groupDate.length(), groupVersion,
                groupFunctionCode, getGroupControlNumber());
        getAlternateAckGenerator().generateAcknowledgmentHeader(getFirstSegment(),
                groupSender, groupReceiver, groupDate.length(), groupVersion,
                groupFunctionCode, getGroupControlNumber());

        label:
        while (true) {
            token = getTokenizer().nextToken();
            if (token.getType() != SEGMENT_START)
                throw new EDISyntaxException(INVALID_BEGINNING_OF_SEGMENT,
                        getTokenizer().getSegmentCount());

            String sType = token.getValue();
            switch (sType) {
                case "ST":
                    docCount++;
                    parseDocument(token);
                    break;
                case "GE":
                    break label;
                default:
                    throw new EDISyntaxException(UNEXPECTED_SEGMENT_IN_CONTEXT,
                            "GE or ST", sType, getTokenizer());
            }
        }

        int n;
        if (docCount != (n = getTokenizer().nextIntValue())) {
            TransactionCountException countException = new TransactionCountException(COUNT_GE, docCount, n, getTokenizer());
            setSyntaxException(countException);
            if (!recover(countException))
                throw countException;
        }
        String s;
        if (!(s = getTokenizer().nextSimpleValue()).equals(getGroupControlNumber())) {
            GroupControlNumberException groupControlNumberException = new GroupControlNumberException(
                    CONTROL_NUMBER_GE, getGroupControlNumber(), s, getTokenizer());
            setSyntaxException(groupControlNumberException);
            if (!recover(groupControlNumberException))
                throw groupControlNumberException;
        }

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
        String control;
        String documentType;
        Token t;
        int segCount = 2;

        if (getTransactionCallback() != null)
            getTransactionCallback().startTransaction(token.getValue());

        getDocumentAttributes().clear();
        getDocumentAttributes().addCDATA(getXMLTags().getDocumentType(),
                documentType = getTokenizer().nextSimpleValue());
        PluginController.setDebug(debug);

        String version = groupVersion;
        if (version.length() > 6) version = version.substring(0, 6);
        String code = getGroupAttributes().getValue(getXMLTags().getStandardCode());

        PluginController pluginController =
                getPluginControllerFactory().create("ANSI", documentType, code, version, getTokenizer());

        boolean wrapped = wrapContentHandlerIfNeeded(pluginController);
        if (pluginController.isEnabled())
            getDocumentAttributes().addCDATA(getXMLTags().getName(), pluginController.getDocumentName());
        getDocumentAttributes().addCDATA(getXMLTags().getControl(), control = getTokenizer().nextSimpleValue());

        Token st03Token = getTokenizer().nextToken();
        switch (st03Token.getType()) {
            case SEGMENT_END:
                break;
            case SIMPLE:
                getDocumentAttributes().addCDATA(getXMLTags().getMessageVersion(), st03Token.getValue());
            default:
                getTokenizer().skipSegment();
        }

        startMessage(getDocumentAttributes());

        String segmentType;
        while (!(segmentType = getTokenizer().nextSegment()).equals("SE")) {
            if (isEnvelopeSegment(segmentType))
                throw new EDISyntaxException(ErrorMessages.SE_MISSING, getTokenizer());

            segCount++;

            if ("BIN".equals(segmentType)) {
                parseBINSequence();
                continue;
            }

            parseSegment(pluginController, segmentType);
        }

        if (wrapped)
            unwrapContentHandler(pluginController);

        int toClose = pluginController.getNestingLevel();
        for (; toClose > 0; toClose--)
            endElement(getXMLTags().getLoopTag());

        int n;
        if (segCount != (n = getTokenizer().nextIntValue())) {
            SegmentCountException countException = new SegmentCountException(COUNT_SE, segCount, n, getTokenizer());
            setSyntaxException(countException);
            if (!recover(countException))
                throw countException;
        }

        String s;
        if (!(s = getTokenizer().nextSimpleValue()).equals(control)) {
            TransactionControlNumberException transactionControlNumberException = new TransactionControlNumberException(CONTROL_NUMBER_SE, control, s, getTokenizer());
            setSyntaxException(transactionControlNumberException);
            if (!recover(transactionControlNumberException))
                throw transactionControlNumberException;
        }

        getAckGenerator().generateTransactionAcknowledgment(documentType, control);
        getAlternateAckGenerator().generateTransactionAcknowledgment(documentType, control);
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

    private boolean isEnvelopeSegment(String segmentType) {
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
            throw new EDISyntaxException(ErrorMessages.MISSING_UNO_LENGTH, getTokenizer());
        } catch (NumberFormatException e) {
            throw new EDISyntaxException("BIN object length must be numeric instead of " + lengthField, getTokenizer());
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
        if (isPreviewed())
            throw new EDISyntaxException(INTERNAL_ERROR_MULTIPLE_EOFS);

        // No release character is supported for ANSI X.12
        setRelease(-1);

        char[] buf = getTokenizer().lookahead(128);
        if ((buf == null) || (buf.length < 128))
            throw new EDISyntaxException(INCOMPLETE_X12);

        if (!(buf[0] == 'I' && buf[1] == 'S' && buf[2] == 'A'))
            throw new EDISyntaxException(X12_MISSING_ISA);

        // ISA*
        // ...^ (offset 3)
        char c = buf[3];
        setDelimiter(c);

        int indexOf16thFieldSeparator = indexOf(c, buf, 16);
        if (indexOf16thFieldSeparator < 0)
            throw new EDISyntaxException(ISA_SEGMENT_HAS_TOO_FEW_FIELDS);
        c = buf[indexOf16thFieldSeparator + 1];
        if (isAcceptable(c))
            setSubDelimiter(c);
        c = buf[indexOf16thFieldSeparator + 2];
        if (isAcceptable(c))
            setTerminator(c);
        else
            throw new EDISyntaxException(INVALID_SEGMENT_TERMINATOR);
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
        return getDelimiter() != c;
    }

    protected static String findTerminatorSuffix(char[] buf, int i, int j) {
        StringBuilder result = new StringBuilder();
        for (int n = i; n < j && !Character.isLetter(buf[n]); n++)
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
