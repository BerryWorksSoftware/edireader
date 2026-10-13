package com.berryworks.edireader.tradacoms;

import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.PluginController;
import com.berryworks.edireader.StandardReader;
import com.berryworks.edireader.XMLTags;
import com.berryworks.edireader.error.GroupCountException;
import com.berryworks.edireader.error.InterchangeControlNumberException;
import com.berryworks.edireader.error.SegmentCountException;
import com.berryworks.edireader.tokenizer.Token;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

public class TradacomsReader extends StandardReader {

    private int messageCount;
    private String toId;
    private PluginController pluginController;
//    private boolean isXmlDocumentEnded;

    @Override
    protected Token recognizeBeginning() throws IOException, SAXException {
        Token t = getTokenizer().nextToken();
        if (t.getType() == Token.TokenType.SEGMENT_START) {
            String segType = t.getValue();
            if (!"STX".equals(segType)) {
                throw new EDISyntaxException(TRADACOMS_MISSING_STX, getTokenizer());
            }
        } else {
            throw new EDISyntaxException(TRADACOMS_MISSING_STX);
        }
        return t;
    }

    @Override
    protected Token parseInterchange(Token t) throws SAXException, IOException {
        getInterchangeAttributes().clear();
        getInterchangeAttributes().addCDATA(XMLTags.STANDARD, "TRADACOMS");

        List<String> compositeList;
        configureTokenizerForElementsInSegment(true);

        /*
          Syntax identifier : version (example: ANA:1 )
         */
        compositeList = getTokenizer().nextCompositeElement();
        String syntaxIdentifier = getSubElement(compositeList, 0);
        String syntaxVersionNumber = getSubElement(compositeList, 1);
        if (!syntaxIdentifier.isEmpty()) {
            getInterchangeAttributes().addCDATA(XMLTags.SYNTAX_IDENTIFIER,
                    syntaxIdentifier);
            if (!syntaxVersionNumber.isEmpty()) {
                getInterchangeAttributes().addCDATA(XMLTags.SYNTAX_VERSION,
                        syntaxVersionNumber);
            }
        }

        /*
          Sender address
         */
        compositeList = getTokenizer().nextCompositeElement();
        String fromId = getSubElement(compositeList, 0);
        String fromQual = getSubElement(compositeList, 1);

        /*
          Receiver address
         */
        compositeList = getTokenizer().nextCompositeElement();
        toId = getSubElement(compositeList, 0);
        String toQual = getSubElement(compositeList, 1);

        /*
          Date and time
         */
        compositeList = getTokenizer().nextCompositeElement();
        String date = getSubElement(compositeList, 0);
        String time = getSubElement(compositeList, 1);
        getInterchangeAttributes().addCDATA(XMLTags.DATE, date);
        getInterchangeAttributes().addCDATA(XMLTags.TIME, time);

        /*
          Control number
         */
        setInterchangeControlNumber(getTokenizer().nextSimpleValue());
        getInterchangeAttributes().addCDATA(XMLTags.CONTROL, getInterchangeControlNumber());

        String rRef = getTokenizer().nextSimpleValue(false, true);
        if (rRef != null) {
            // We haven't hit the end of the segment yet.
            if (!rRef.isEmpty()) {
                getInterchangeAttributes().addCDATA(XMLTags.RECIPIENT_REFERENCE, rRef);
            }
            String aRef = getTokenizer().nextSimpleValue(false, true);
            if (aRef != null) {
                // We still haven't hit the end of the segment.
                if (!aRef.isEmpty()) {
                    getInterchangeAttributes().addCDATA(XMLTags.APPLICATION_REFERENCE, aRef);
                }
                String priority = getTokenizer().nextSimpleValue(false, true);
                if (priority != null) {
                    // Yet another value in the segment. This should be the last.
                    if (!priority.isEmpty()) {
                        getInterchangeAttributes().addCDATA(XMLTags.PRIORITY, priority);
                    }
                    getTokenizer().skipSegment();
                }
            }
        }

        startInterchange(getInterchangeAttributes());
        generatedSenderAndReceiver(fromId, fromQual, null, toId, toQual, null);

        configureTokenizerForElementsInSegment(false);

        label:
        while (true) {
            Token token = getTokenizer().nextToken();
            if (token.getType() != Token.TokenType.SEGMENT_START) {
                throw new EDISyntaxException(
                        "Invalid beginning of MHD|BAT segment", getTokenizer());
            }
            String sType = token.getValue();
            switch (sType) {
                case "BAT":
                    parseFunctionalGroup(token);
                    break;
                case "MHD":
                    impliedFunctionalGroup(token);
                    break;
                case "END":
                    break label;
                default:
                    throw new EDISyntaxException(UNEXPECTED_SEGMENT_IN_CONTEXT,
                            "MHD, BAT, or END", sType, getTokenizer());
            }
        }

        int end01 = getTokenizer().nextIntValue();
        if (end01 != messageCount) {
            GroupCountException countException = new GroupCountException(COUNT_END, messageCount, end01, getTokenizer());
            setSyntaxException(countException);
            if (!recover(countException))
                throw countException;
        }

        endInterchange();

        return getTokenizer().skipSegment();
    }

    private Token parseFunctionalGroup(Token token) throws EDISyntaxException {
        throw new EDISyntaxException("TRADACOMS batch structure via BAT segment not yet supported", getTokenizer());
    }

    protected Token impliedFunctionalGroup(Token token) throws SAXException,
            IOException {
        getGroupAttributes().clear();
        startElement(XMLTags.GROUP, getGroupAttributes());
        label:
        while (true) {
            if (token.getType() != Token.TokenType.SEGMENT_START) {
                throw new EDISyntaxException(
                        "Invalid beginning of MHD|END segment",
                        getTokenizer().getSegmentCount());
            }
            String sType = token.getValue();
            switch (sType) {
                case "MHD":
                    token = parseDocument(token);
                    break;
                case "END":
                    getTokenizer().ungetToken();
                    break label;
                default:
                    throw new EDISyntaxException(UNEXPECTED_SEGMENT_IN_CONTEXT,
                            "MHD or END", sType, getTokenizer());
            }
        }

        endElement(XMLTags.GROUP);
        return (token);
    }

    /**
     * Parse a TRADACOMS document. For example, an MHD/MTR ORDHDR pair,
     * followed by one or more MHD/MTR ORDERS pairs, followed by a closing
     * MHD/MTR ORDTLR pair are considered to be a single document, represented
     * in the XML with a transaction. Each MHD/MTR structure
     * (a TRADACOMS message) is then reflected in the XML with a loop
     * with an Id attribute of ORDHDR, ORDERS, or ORDTLR as appropriate.
     * <p>
     * Note that loop elements are usually handled with the help of a plugin,
     * but for TRADACOMS loops are created natively in the parser based on
     * knowledge of the MHD and MTR. This does not preclude other loops within
     * a message from being handled with a plugin.
     *
     * @param token starting token
     * @return Token the last token that was parsed
     * @throws SAXException exception condition arising from use of SAX API
     * @throws IOException  exception condition arising from IO
     */
    protected Token parseDocument(Token token) throws SAXException,
            IOException {
        {
            getDocumentAttributes().clear();
            boolean pendingTransaction = true;

            while (true) {
                parseTradacomMessage(pendingTransaction, token);
                pendingTransaction = false;
                String segmentType = getTokenizer().nextSegment();
                if ("END".equals(segmentType)) {
                    break;
                } else if (!"MHD".equals(segmentType)) {
                    throw new RuntimeException("expected an MHD or an END");
                }
            }

            endElement(XMLTags.DOCUMENT);

            getTokenizer().ungetToken();
            return (getTokenizer().nextToken());
        }
    }

    protected Token parseTradacomMessage(boolean pendingTransaction, Token token) throws SAXException,
            IOException {
        {
            // Parse the sequence number, the message type, and version in the MHD segment
            // For example: MHD=2+ORDERS:9'

            configureTokenizerForElementsInSegment(true);

            // Check the sequence number to make sure it matches the observed message count
            int sequence = getTokenizer().nextIntValue();
            if (sequence != ++messageCount) {
                SegmentCountException sequenceException =
                        new SegmentCountException(SEQUENCE_MHD, messageCount, sequence, getTokenizer());
                setSyntaxException(sequenceException);
                if (!recover(sequenceException))
                    throw sequenceException;
            }

            Token t = getTokenizer().nextToken();
            String msgType = null;
            String version = null;
            if (t.getType() == Token.TokenType.SUB_ELEMENT) {
                msgType = t.getValue();
                t = getTokenizer().nextToken();
                if (t.getType() == Token.TokenType.SUB_ELEMENT) {
                    version = t.getValue();
                }
            }
            configureTokenizerForElementsInSegment(false);

            if (pendingTransaction) {
                // This is where we will take care of a pending <transaction>, now that we've looked ahead
                // to discover the docType and version from the first MHD.
                if (msgType != null && !msgType.isEmpty()) {
                    getDocumentAttributes().addCDATA(XMLTags.DOCUMENT_TYPE, msgType);
                    if (version != null && !version.isEmpty())
                        getDocumentAttributes().addCDATA(XMLTags.VERSION, version);
                }
                startMessage(getDocumentAttributes());

                // We want to use the same plugin for all the MHD messages that comprise the single
                // transaction. Therefore, we create the plugin controller now based on the the type
                // noted in the first MHD.
                pluginController = getPluginControllerFactory().create("TRADACOMS", msgType, version, "release", getTokenizer());
            }

            // Start a <loop> with the message type (ORDHDR, ORDERS, ACKMNT, ...)
            getDocumentAttributes().clear();
            getDocumentAttributes().addCDATA(XMLTags.ID, msgType);

            if (pluginController != null && pluginController.isEnabled()) {
                pluginController.reset();
                getDocumentAttributes().addCDATA(XMLTags.NAME, pluginController.getDocumentName());
            }

            String loopTag = XMLTags.LOOP;
            startElement(loopTag, getDocumentAttributes());

            // Handle segments until we hit an MTR
            int segCount = 2;
            String segmentType;
            while (!(segmentType = getTokenizer().nextSegment()).equals("MTR")) {
                segCount++;
                configureTokenizerForElementsInSegment(true);
                if ("RSG".equals(segmentType))
//                    parseSegment(pluginController, segmentType);
                    parseSegmentRSG(toId);
                else
                    parseSegment(pluginController, segmentType);
                configureTokenizerForElementsInSegment(false);
            }

            // Close any loops for the segments
            int toClose = pluginController == null ? 0 : pluginController.getNestingLevel();
            for (; toClose > 0; toClose--) {
                endElement(loopTag);
            }

            int n;
            if (segCount != (n = getTokenizer().nextIntValue())) {
                SegmentCountException countException = new SegmentCountException(COUNT_MTR, segCount, n, getTokenizer());
                setSyntaxException(countException);
                if (!recover(countException))
                    throw countException;
            }

            // Close the <loop> for the message type, the one that was explicitly started above.
            endElement(loopTag);

            // Skip over this MTR segment and return the SEGMENT_END token
            return getTokenizer().skipSegment();
        }
    }

    private void parseSegmentRSG(String rsg02Expected) throws SAXException, IOException {
        String segmentType = "RSG";

        getDocumentAttributes().clear();
        getDocumentAttributes().addCDATA(XMLTags.ID, segmentType);
        startElement(XMLTags.SEGMENT, getDocumentAttributes());

        Token t;

        t = getTokenizer().nextToken();
        if (t.getType() == Token.TokenType.SIMPLE) {
            String rsg01 = t.getValue();
            if (!getInterchangeControlNumber().equals(rsg01)) {
                throw new InterchangeControlNumberException(CONTROL_NUMBER_RSG, getInterchangeControlNumber(), rsg01, getTokenizer());
            }

            parseSegmentElement(t);
            t = getTokenizer().nextToken();
            if (t.getType() == Token.TokenType.SIMPLE) {
                String rsg02 = t.getValue();
                if (!rsg02Expected.equals(rsg02)) {
                    throw new InterchangeControlNumberException(RECIPIENT_RSG, rsg02Expected, rsg02, getTokenizer());
                }
                parseSegmentElement(t);
            }
        }
        while ((t = getTokenizer().nextToken()).getType() != Token.TokenType.SEGMENT_END) {

            switch (t.getType()) {
                case SIMPLE:
                case EMPTY:
                case SUB_ELEMENT:
                case SUB_EMPTY:
                    break;

                case END_OF_DATA:
                    throw new EDISyntaxException(UNEXPECTED_EOF, getTokenizer());

                default:
                    throw new EDISyntaxException(MALFORMED_EDI_SEGMENT, getTokenizer());

            }

            parseSegmentElement(t);
        }
        endElement(XMLTags.SEGMENT);
    }

    private void configureTokenizerForElementsInSegment(boolean withinSegment) {
        getTokenizer().setDelimiter(withinSegment ? '+' : '=');
    }

    @Override
    public void preview() throws EDISyntaxException, IOException {
        setTerminator('\'');
        setDelimiter('=');
        setSubDelimiter(':');
        setRelease('?');
        setDecimalMark('.');
        setTerminatorSuffix("");

        char[] buf = getTokenizer().lookahead(128);
        String preview = String.valueOf(buf);
        int index = preview.indexOf("MHD=");
        if (index > 1 && buf[index - 1] == '\n') {
            setTerminatorSuffix(buf[index - 2] == '\r' ? "\r\n" : "\n");
        }

        setPreviewed(true);
    }
}
