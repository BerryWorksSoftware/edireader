/*
 * Copyright 2005-2026 by BerryWorks Software. All rights reserved.
 */
package com.berryworks.edireader.hl7;

import com.berryworks.edireader.*;
import com.berryworks.edireader.plugin.CompositeAwarePlugin;
import com.berryworks.edireader.tokenizer.Token;
import com.berryworks.edireader.util.NameConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import static com.berryworks.edireader.util.FixedLength.isPresent;

/**
 * Reads and parses HL7 messages.
 * <p>
 * This class is not normally constructed explicitly from outside the package,
 * although it is declared public for special cases. The recommended use of this
 * class is to first establish an EDIReader using one of the factory techniques;
 * when the EDIReader is called upon to parse the EDI data, it determines which
 * EDI standard applies and internally constructs the proper subclass to
 * continue with parsing. Within this framework, HL7 is considered an example of
 * an EDI standard, although it is not usually called that in normal discussion.
 * <p>
 * The EDIReader core parsers and framework were designed with a primary goal of providing
 * an abstraction of X12 and EDIFACT as a series of SAX events, emphasizing their commonality
 * as far as possible. A secondary goal is to provide a framework for supporting other EDI-like standard
 * such as HL7 and TRADACOMS. We map HL7 header fields onto the established EDIReader abstractions
 * and augment with HL7-specific as necessary. No information is lost.
 * <pre>
 * MSH|^~\&amp;|SendingApp|SendingFacility|ReceivingApp|ReceivingFacility|DateTime|Security|MessageType|ControlID|ProcessingID|Version|
 *
 * SenderApp         -&gt; &lt;group AppSender="..."/&gt;  (used with X12 and/or EDIFACT)
 * SendingFacility   -&gt; &lt;group SendingFacility="..."/&gt;
 * ReceivingApp      -&gt; &lt;group ApplReceiver="..."/&gt; (used with X12 and/or EDIFACT)
 * ReceivingFacility -&gt; &lt;group ReceivingFacility="..."/&gt;
 * DateTime           -&gt; &lt;group Date="..." Time="..."/&gt; (used with X12 and/or EDIFACT)
 * Security           -&gt; &lt;group Security="..."/&gt; (used with X12 and/or EDIFACT)
 * MessageType, 1st sub-element -&gt; &lt;group DocType="..."/&gt;
 *                                  &lt;transaction DocType="..."/&gt;
 * MessageType, 2nd sub-element -&gt; &lt;group Event="..."/&gt;
 *                                  &lt;transaction Event="..."/&gt;
 * ControlID          -&gt; &lt;group Control="..."/&gt;
 * ProcessingID       -&gt; &lt;group ProcessingID="..."/&gt;
 * Version            -&gt; &lt;group SyntaxVersion="..."/&gt;
 * </pre>
 */
public class HL7Reader extends StandardReader {
    private static final Logger logger = LoggerFactory.getLogger(HL7Reader.class);
    private String messageType;
    private CompositeAwarePlugin compositeAwarePlugin;

    // These next two items deal with the special case where the HL7 data has the form of a composite
    // but the plugin (and potentially an XSD) says it is not a composite according to the HL7 specifications.
    private boolean nonCompositeAccordingToPlugin;
    private final StringBuilder fauxComposite = new StringBuilder();

    @Override
    public void parse(InputSource source) throws SAXException, IOException {
        if (source == null) {
            throw new IOException("parse called with null InputSource");
        }
        if (getContentHandler() == null) {
            throw new IOException("parse called with null getContentHandler()");
        }

        if (!isExternalXmlDocumentStart())
            startXMLDocument();

        parseSetup(source);

        getTokenizer().setDelimiter(getDelimiter());
        getTokenizer().setSubDelimiter(getSubDelimiter());
        getTokenizer().setSubSubDelimiter(getSubSubDelimiter());
        getTokenizer().setRelease(getRelease());
        getTokenizer().setRepetitionSeparator(getRepetitionSeparator());
        getTokenizer().setTerminator(getTerminator());

        EDIAttributes attributes = new EDIAttributes();
        attributes.addCDATA(getXMLTags().getStandard(), "HL7");
        startInterchange(attributes);

        char[] lookahead = getTokenizer().lookahead(1);
        if (lookahead[0] == '\013') {
            getTokenizer().getChar();
        }

        Token t = getTokenizer().nextToken();
        while (t.getType() == Token.TokenType.SEGMENT_START) {
            String segType = t.getValue();
            if (segType.equals("MSH")) {
                logger.debug("HL7 message starting with MSH segment");
                t = parseInterchange(t);
            } else {
                throw new EDISyntaxException("Improperly formed MSH segment",
                        getTokenizer());
            }
        }
        if (t.getType() != Token.TokenType.END_OF_DATA) {
            throw new EDISyntaxException("Unable to detect MSH segment",
                    getTokenizer());
        }

        endInterchange();

        if (!isExternalXmlDocumentStart())
            endXMLDocument();
    }

    /**
     * Parse HL7 message ( MSH ... )
     *
     * @param token the SEGMENT_START token for the MSH
     * @return SEGMENT_END token following the last segment in the message
     * @throws SAXException for HL7 syntax errors
     * @throws IOException  for IO errors
     */
    @Override
    protected Token parseInterchange(Token token) throws SAXException,
            IOException {

        getInterchangeAttributes().clear();

        // The first several fields were processed in a preview of this data to
        // identify this as HL7 data and to establish the delimiters,
        // terminator, and escape characters. So we do not need to carefully
        // interpret them again. Call nextToken() 3 times to position beyond the
        // delimiters and such. The first time for the MSH segment start field,
        // and twice for the delimiters. Remember that the repetitionSeparator
        // appearing with the delimiters cause it to be two repeating fields
        // instead of a single field.
        getTokenizer().nextToken();
        getTokenizer().nextToken();
        getTokenizer().nextToken();

        // Sending application and facility MSH fields 3 and 4
        // These fields may be simple tokens, and they may be a composite
        // where the first subelement in the composite is the value of interest.
        // Therefore, we call nextCompositeElement() which will work in either
        // case.
        List<String> list = getTokenizer().nextCompositeElement();
        String sendingApplication = !list.isEmpty() ? list.get(0) : "";
        getInterchangeAttributes().addCDATA(getXMLTags().getApplSender(), sendingApplication);

        list = getTokenizer().nextCompositeElement();
        String sendingFacility = !list.isEmpty() ? list.get(0) : "";
        getInterchangeAttributes().addCDATA("SendingFacility", sendingFacility);

        // Receiving application and facility MSH fields 5 and 6
        list = getTokenizer().nextCompositeElement();
        if (!list.isEmpty()) {
            getInterchangeAttributes().addCDATA(
                    getXMLTags().getApplReceiver(),
                    list.get(0));
        }

        list = getTokenizer().nextCompositeElement();
        if (!list.isEmpty()) {
            getInterchangeAttributes().addCDATA(
                    "ReceivingFacility",
                    list.get(0));
        }

        // Date/time of message MSH field 7
        processDateAndTime();

        // Security MSH field 8
        String security = getTokenizer().nextSimpleValue(false);
        if ((security != null) && (!security.isEmpty())) {
            getInterchangeAttributes().addCDATA(getXMLTags().getSecurity(), security);
        }

        // Message type MSH field 9
        list = getTokenizer().nextCompositeElement();
        messageType = list.isEmpty() ? null : list.get(0);
        String eventType = list.size() > 1 ? list.get(1) : null;

        if ((messageType != null) && (!messageType.isEmpty())) {
            getInterchangeAttributes().addCDATA(getXMLTags().getMessageType(), messageType);
            String text = Table76.getText(messageType);
            if (text != null) {
                getInterchangeAttributes().addCDATA("TypeDesc", text);
            }
        }
        if ((eventType != null) && (!eventType.isEmpty())) {
            getInterchangeAttributes().addCDATA(getXMLTags().getEvent(), eventType);
            String text = Table03.getText(eventType);
            if (text != null) {
                getInterchangeAttributes().addCDATA("EventDesc", text);
            }
        }

        // Message Control ID MSH field 10
        setInterchangeControlNumber(getTokenizer().nextSimpleValue(false));
        if (getInterchangeControlNumber() == null)
            setInterchangeControlNumber("");
        if (!getInterchangeControlNumber().isEmpty()) {
            getInterchangeAttributes().addCDATA(getXMLTags().getControl(), getInterchangeControlNumber());
        }

        // Processing ID MSH field 11
        list = getTokenizer().nextCompositeElement();
        if (list.isEmpty()) {
            throw new EDISyntaxException("Invalid Processing ID", getTokenizer());
        }
        String processingID = list.get(0);
        if ((processingID != null) && (!processingID.isEmpty())) {
            getInterchangeAttributes().addCDATA(getXMLTags().getProcessingId(), processingID);
        }

        // Version ID MSH field 12
        String versionID = getTokenizer().nextSimpleValue();
        if ((versionID != null) && (!versionID.isEmpty())) {
            getInterchangeAttributes().addCDATA(getXMLTags().getSyntaxVersion(), versionID);
        }

        // Look at the remaining (optional) fields in the MSH segment, starting with field 13
        int fieldNumber = 12;
        while (true) {
            token = getTokenizer().nextToken();
            if ((token.getType() == Token.TokenType.SEGMENT_END)
                    || (token.getType() == Token.TokenType.END_OF_DATA)) {
                break;
            }
            if (getTokenizer().getElementInSegmentCount() > 30) {
                throw new EDISyntaxException(
                        "Too many elements for a MSH (Segment terminator problem?)",
                        getTokenizer());
            }
            fieldNumber++;
            if (token.getType() != Token.TokenType.SIMPLE) continue;
            switch (fieldNumber) {
                case 13:
                    String msh13 = token.getValue();
                    if (isPresent(msh13)) {
                        getInterchangeAttributes().addCDATA("sequenceNumber", msh13);
                    }
                    break;
                case 14:
                    String msh14 = token.getValue();
                    if (isPresent(msh14)) {
                        getInterchangeAttributes().addCDATA("continuationPointer", msh14);
                    }
                    break;
                case 15:
                    String ackType = token.getValue();
                    if (isPresent(ackType)) {
                        getInterchangeAttributes().addCDATA(getXMLTags().getAcknowledgementRequest(), ackType);
                    }
                    break;
            }
        }


        getAckGenerator().generateAcknowledgmentHeader("", "", "", "", "", "", getInterchangeControlNumber());
        startElement(getXMLTags().getGroupTag(), getInterchangeAttributes());

        if (token.getType() != Token.TokenType.END_OF_DATA) {
            token = getTokenizer().nextToken();
        }
        if (token.getType() == Token.TokenType.SEGMENT_START) {
            token = parseDocument(token);
        } else if (token.getType() != Token.TokenType.END_OF_DATA) {
            throw new EDISyntaxException("Invalid beginning of an HL7 segment",
                    getTokenizer());
        }

        getAckGenerator().generateAcknowledgementWrapup();
        endElement(getXMLTags().getGroupTag());

        return (token);
    }

    private void processDateAndTime() throws SAXException, IOException {
        String dateAndTime = getTokenizer().nextSimpleValue();
        if (dateAndTime != null && dateAndTime.length() >= 8) {
            getInterchangeAttributes().addCDATA(getXMLTags().getDate(), dateAndTime.substring(0, 8));
            if (dateAndTime.length() >= 12) {
                getInterchangeAttributes().addCDATA(getXMLTags().getTime(), dateAndTime.substring(8));
            }
        }
    }

    /**
     * Parse HL7 Message (MSH ... )
     *
     * @param token the SEGMENT_START token
     * @return token most recently parsed by this method
     * @throws SAXException for problem emitting SAX events
     * @throws IOException  for problem reading EDI data
     */
    protected Token parseDocument(Token token) throws SAXException,
            IOException {

        getDocumentAttributes().clear();
        String groupType = getInterchangeAttributes().getValue("", "Type");
        String groupEvent = getInterchangeAttributes().getValue("", "Event");
        String controlTag = getXMLTags().getControl();
        String control = getInterchangeAttributes().getValue("", controlTag);
        getDocumentAttributes().addCDATA(getXMLTags().getMessageType(), groupType);
        getDocumentAttributes().addCDATA("Event", groupEvent);
        getDocumentAttributes().addCDATA(controlTag, control);
        startMessage(getDocumentAttributes());

        PluginController pluginController = getPluginControllerFactory().create("HL7", messageType, getTokenizer());
        if (pluginController.isEnabled()) {
            getDocumentAttributes().addCDATA("Name", pluginController.getDocumentName());
            Plugin plugin = pluginController.getPlugin();
            if (plugin instanceof CompositeAwarePlugin) {
                compositeAwarePlugin = (CompositeAwarePlugin) plugin;
            }
        }

        // Loop for each remaining segment in the HL7 message,
        // terminated by either end of data or an MSH segment marking the
        // beginning
        // of a new message.
        Token t = token;
        Token.TokenType tokenType;
        while ((tokenType = t.getType()) != Token.TokenType.END_OF_DATA) {
            if (tokenType != Token.TokenType.SEGMENT_START) {
                throw new EDISyntaxException(
                        "Expected the start of a segment or the end of data ("
                                + tokenType + ")", getTokenizer());
            }

            String segmentType = t.getSegmentType();
            if (segmentType.equals("MSH")) {
                break;
            }
            logger.debug("parsing HL7 segment {}", segmentType);

            if (pluginController.transition(segmentType)) {
                // First close off any loops that were closed as the result of
                // the transition
                int toClose = pluginController.closedCount();
                for (; toClose > 0; toClose--) {
                    // ... </loop>
                    endElement(getXMLTags().getLoopTag());
                }

                String s = pluginController.getLoopEntered();
                if (!pluginController.isResumed()) {
                    getDocumentAttributes().clear();
                    getDocumentAttributes().addCDATA(getXMLTags().getIdAttribute(), s);
                    // <loop> ...
                    startElement(getXMLTags().getLoopTag(), getDocumentAttributes());
                }
            }

            // <segment> ... </segment>
            getDocumentAttributes().clear();
            getDocumentAttributes().addCDATA(getXMLTags().getIdAttribute(), segmentType);
            startElement(getXMLTags().getSegTag(), getDocumentAttributes());

            // Loop for each field in the HL7 segment, terminated by the segment
            // terminator. An end of data is also accepted in lieu of a segment terminator
            // to accommodate a slightly malformed file message that is missing the segment
            // terminator on the last segment.
            innerLoop:
            while (true) {
                t = getTokenizer().nextToken();
                switch (t.getType()) {
                    case END_OF_DATA:
                        // break outerLoop;
                    case SEGMENT_END:
                        break innerLoop;
                    default:
                        parseSegmentElement(t);
                }
            }
            endElement(getXMLTags().getSegTag());
            if (t.getType() != Token.TokenType.END_OF_DATA) {
                t = getTokenizer().nextToken();
            }
        }

        int toClose = pluginController.getNestingLevel();

        for (; toClose > 0; toClose--) {
            // ... </loop>
            endElement(getXMLTags().getLoopTag());
        }

        getAckGenerator().generateTransactionAcknowledgment(null, null);
        endElement(getXMLTags().getDocumentTag());

        return (t);
    }

    /**
     * Issue SAX calls on behalf of an EDI element. The token passed as an
     * argument is first token of a field.
     *
     * @param t the parsed token
     * @throws SAXException for problem emitting SAX events
     */
    @Override
    protected void parseSegmentElement(Token t) throws SAXException {
        EDIAttributes attributes;

        String elementId = t.getElementId();
        switch (t.getType()) {

            case SIMPLE:

                // Take a quick exit for empty fields, a very common case
                if (t.getValueLength() == 0 || !t.containsNonSpace())
                    return;

                attributes = getDocumentAttributes();
                attributes.clear();
                attributes.addCDATA(getXMLTags().getIdAttribute(), elementId);
                startElement(getXMLTags().getElementTag(), attributes);
                getContentHandler().characters(t.getValueChars(), 0, t.getValueLength());
                endElement(getXMLTags().getElementTag());
                if (segmentPluginController != null)
                    segmentPluginController.noteElement(getContentHandler(), elementId, t.getValueChars(), 0, t.getValueLength());
                break;

            case EMPTY:
                if (isKeepEmptyElements()) {
                    attributes = getDocumentAttributes();
                    attributes.clear();
                    attributes.addCDATA(getXMLTags().getIdAttribute(), elementId);
                    startElement(getXMLTags().getElementTag(), attributes);
                    endElement(getXMLTags().getElementTag());
                    if (segmentPluginController != null)
                        segmentPluginController.noteElement(getContentHandler(), elementId, t.getValueChars(), 0, t.getValueLength());
                }
                break;

            case SUB_ELEMENT:
                attributes = getDocumentAttributes();
                attributes.clear();
                if (t.isFirst()) {
                    nonCompositeAccordingToPlugin = isNonCompositeAccordingToPlugin(elementId);
                    if (!nonCompositeAccordingToPlugin) {
                        // Normal case
                        attributes.addCDATA(getXMLTags().getIdAttribute(), elementId);
                        attributes.addCDATA(getXMLTags().getCompositeIndicator(), "yes");
                        startElement(getXMLTags().getElementTag(), attributes);
                    }
                }

                if (nonCompositeAccordingToPlugin) {
                    String subElementData = String.valueOf(t.getValueChars(), 0, t.getValueLength());
                    fauxComposite.append(subElementData).append("^");
                } else {
                    // Normal case
                    attributes.clear();
                    attributes.addAttribute("", getXMLTags().getSubElementSequence(),
                            getXMLTags().getSubElementSequence(), "CDATA", String.valueOf(1 + t.getSubIndex()));
                    startElement(getXMLTags().getSubElementTag(), attributes);
                    getContentHandler().characters(t.getValueChars(), 0, t.getValueLength());
                    endElement(getXMLTags().getSubElementTag());
                }

                if (t.isLast()) {
                    if (nonCompositeAccordingToPlugin) {
                        fauxComposite.setLength(fauxComposite.length() - 1); // Drop the last ^
                        String data = fauxComposite.toString();
                        // Mimic what we do for a normal simple element
                        attributes.clear();
                        attributes.addCDATA(getXMLTags().getIdAttribute(), elementId);
                        startElement(getXMLTags().getElementTag(), attributes);
                        getContentHandler().characters(data.toCharArray(), 0, data.length());
                        endElement(getXMLTags().getElementTag());
                        if (segmentPluginController != null)
                            segmentPluginController.noteElement(getContentHandler(), elementId, t.getValueChars(), 0, t.getValueLength());
                        fauxComposite.setLength(0);
                    } else {
                        endElement(getXMLTags().getElementTag());
                        nonCompositeAccordingToPlugin = false;
                    }
                }
                break;

            case SUB_EMPTY:
                attributes = getDocumentAttributes();
                if (t.isFirst()) {
                    nonCompositeAccordingToPlugin = isNonCompositeAccordingToPlugin(elementId);
                    if (!nonCompositeAccordingToPlugin) {
                        // Normal case
                        attributes.clear();
                        attributes.addCDATA(getXMLTags().getIdAttribute(), elementId);
                        attributes.addCDATA(getXMLTags().getCompositeIndicator(), "yes");
                        startElement(getXMLTags().getElementTag(), attributes);
                    }
                }

                if (nonCompositeAccordingToPlugin) {
                    fauxComposite.append("^");
                }
                if (t.isLast()) {
                    if (nonCompositeAccordingToPlugin) {
                        // Mimic what we do for a normal simple element
                        attributes.clear();
                        attributes.addCDATA(getXMLTags().getIdAttribute(), elementId);
                        startElement(getXMLTags().getElementTag(), attributes);
                        // Remove trailing ^s
                        while (true) {
                            if (fauxComposite.isEmpty()) break;
                            char lastChar = fauxComposite.charAt(fauxComposite.length() - 1);
                            if ('^' == lastChar) {
                                fauxComposite.setLength(fauxComposite.length() - 1);
                                continue;
                            }
                            break;
                        }
                        String data = fauxComposite.toString();
                        getContentHandler().characters(data.toCharArray(), 0, data.length());
                        endElement(getXMLTags().getElementTag());
                        if (segmentPluginController != null)
                            segmentPluginController.noteElement(getContentHandler(), elementId, t.getValueChars(), 0, t.getValueLength());
                        fauxComposite.setLength(0);

                    } else {
                        // Normal case
                        endElement(getXMLTags().getElementTag());
                        nonCompositeAccordingToPlugin = false;
                    }
                }
                break;
            case SUB_SUB_EMPTY:
                System.out.println("... sub-sub-empty");
                break;
            case SUB_SUB_ELEMENT:
                System.out.println("... sub-sub-element");
                attributes = getDocumentAttributes();
                attributes.clear();
                String sequence = String.valueOf(9);
                attributes.addCDATA("Sequence", sequence);
                String tag = "subsubelement";
                startElement(tag, attributes);
                String data = "(data)";
                getContentHandler().characters(data.toCharArray(), 0, data.length());
                endElement(tag);
                break;
        }
    }

    private boolean isNonCompositeAccordingToPlugin(String elementId) {
        if (compositeAwarePlugin == null) return false;
        String segmentName = elementId.length() > 3 ?
                elementId.substring(0, elementId.length() - 2) :
                "???";
        int position = NameConverter.valueOf2TrailingDigits(elementId);
        return !compositeAwarePlugin.isComposite(segmentName, position);
    }

    /**
     * Preview the EDI input before attempting to tokenize it in order to
     * discover syntactic details including segment terminator and element
     * delimiter. Upon return, the input stream must be re-positioned so that
     * the templateTokenizer can read from the beginning of the interchange.
     *
     * @throws EDISyntaxException if invalid EDI is detected
     * @throws IOException        for problem reading EDI data
     */
    @Override
    public void preview() throws EDISyntaxException, IOException {
        final int lookaheadSize = 240;
        char[] buf = getTokenizer().lookahead(lookaheadSize);
        if ((buf == null) || (buf.length < 12)) {
            throw new EDISyntaxException(INCOMPLETE_HL7_MESSAGE);
        }

        int mshOffset = 0;
        if (buf[0] == '\013') {
            // Ignore start of message character before the MSH
            mshOffset++;
        }

        if (!(buf[mshOffset] == 'M' && buf[mshOffset + 1] == 'S' && buf[mshOffset + 2] == 'H')) {
            throw new EDISyntaxException("HL7 message must begin with MSH");
        }

        // Now we establish subDelimiter, delimiter, release, etc.

        setDelimiter(buf[mshOffset + 3]);
        setSubDelimiter(buf[mshOffset + 4]);
        setRepetitionSeparator(buf[mshOffset + 5]);
        setRelease(buf[mshOffset + 6]);
        setSubSubDelimiter(buf[mshOffset + 7]);

        // The standard specifies that a carriage return, \r, is always
        // the terminator. However, we will accept a newline ( \n or linefeed)
        // as an acceptable terminator as well. We will also accept \r\n pairs
        // terminating each segment, treating the \r as the actual terminator
        // and
        // \n as a terminator suffix.
        //
        // We will scan characters within the lookahead buffer and the first
        // instance of
        // either \r or \n will become the terminator for this message. If
        // neither is noticed
        // within the lookahead buffer, then just go with \r.
        setTerminator('\r');
        setTerminatorSuffix("");
        for (int i = mshOffset + 10; i < buf.length; i++) {
            if (buf[i] == '\r') {
                setTerminator('\r');
                if (i + 1 < buf.length && buf[i + 1] == '\n') {
                    setTerminatorSuffix("\n");
                }
                break;
            } else if (buf[i] == '\n') {
                setTerminator('\n');
                break;
            }
        }
    }

    @Override
    public ReplyGenerator getAckGenerator() {
        if (super.getAckGenerator() == null) {
            setAckGenerator(new HL7AckGenerator(this, getAckStream()));
        }
        return super.getAckGenerator();
    }

    @Override
    protected Token recognizeBeginning() throws IOException, SAXException {
        return null;
    }

}
