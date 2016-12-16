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

import com.berryworks.edireader.error.ErrorMessages;
import com.berryworks.edireader.error.RecoverableSyntaxException;
import com.berryworks.edireader.plugin.PluginControllerFactory;
import com.berryworks.edireader.plugin.PluginControllerFactoryInterface;
import com.berryworks.edireader.tokenizer.Token;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import static com.berryworks.edireader.util.FixedLength.isPresent;

/**
 * Common parent class to several EDIReader subclasses that provide for the
 * parsing of specific EDI standards. This common parent provides an opportunity
 * to factor and share common concepts and logic.
 */
public abstract class StandardReader extends EDIReader {

    /**
     * Interchange Control Number
     */
    private String interchangeControlNumber;

    /**
     * Group-level control number
     */
    private String groupControlNumber;

    private int groupCount;
    private int documentCount;
    private ReplyGenerator ackGenerator;
    private ReplyGenerator alternateAckGenerator;
    private RecoverableSyntaxException syntaxException;
    private PluginControllerFactoryInterface pluginControllerFactory;
    protected PluginController segmentPluginController;

    protected abstract Token recognizeBeginning() throws IOException, SAXException;

    protected abstract Token parseInterchange(Token t) throws SAXException,
            IOException;

    @Override
    public void parse(InputSource source) throws SAXException, IOException {
        if (source == null)
            throw new IOException("parse called with null InputSource");
        if (getContentHandler() == null)
            throw new IOException("parse called with null ContentHandler");

        if (!isExternalXmlDocumentStart())
            startXMLDocument();

        parseSetup(source);

        getTokenizer().setDelimiter(getDelimiter());
        getTokenizer().setSubDelimiter(getSubDelimiter());
        getTokenizer().setRelease(getRelease());
        getTokenizer().setRepetitionSeparator(getRepetitionSeparator());
        getTokenizer().setTerminator(getTerminator());

        try {
            parseInterchange(recognizeBeginning());
        } catch (EDISyntaxException e) {
            if (ackGenerator != null)
                ackGenerator.generateNegativeACK();
            if (alternateAckGenerator != null)
                alternateAckGenerator.generateNegativeACK();
            throw e;
        }

        if (!isExternalXmlDocumentStart())
            endXMLDocument();

    }

    /**
     * Issue SAX calls on behalf of an EDI element. The token passed as an
     * argument is first token of a field.
     *
     * @param t the parsed token
     * @throws SAXException for problem emitting SAX events
     */
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

            case SUB_ELEMENT:

                attributes = getDocumentAttributes();

                if (t.isFirst()) {
                    attributes.clear();
                    attributes.addCDATA(getXMLTags().getIdAttribute(), elementId);
                    attributes.addCDATA(getXMLTags().getCompositeIndicator(), "yes");
                    startElement(getXMLTags().getElementTag(), attributes);
                }

                attributes.clear();
                attributes.addAttribute(
                        "",
                        getXMLTags().getSubElementSequence(),
                        getXMLTags().getSubElementSequence(),
                        "CDATA", String.valueOf(1 + t.getSubIndex()));
                startElement(getXMLTags().getSubElementTag(), attributes);
                getContentHandler().characters(t.getValueChars(), 0, t.getValueLength());
                endElement(getXMLTags().getSubElementTag());

                if (t.isLast()) {
                    endElement(getXMLTags().getElementTag());
                }
                break;

            case SUB_EMPTY:

                if (t.isFirst()) {
                    attributes = getDocumentAttributes();
                    attributes.clear();
                    attributes.addCDATA(getXMLTags().getIdAttribute(), elementId);
                    attributes.addCDATA(getXMLTags().getCompositeIndicator(), "yes");
                    startElement(getXMLTags().getElementTag(), attributes);
                }
                if (t.isLast()) {
                    endElement(getXMLTags().getElementTag());
                }
                break;
        }
    }

    /**
     * Set an override value to be used whenever generating a control date and
     * time. This method is used for automated testing.
     *
     * @param overrideValue to be used in lieu of current date and time
     */

    public void setControlDateAndTime(String overrideValue) {
        ReplyGenerator generator = getAckGenerator();
        if (generator != null) {
            generator.setControlDateAndTime(overrideValue);
        }

        generator = getAlternateAckGenerator();
        if (generator != null) {
            generator.setControlDateAndTime(overrideValue);
        }
    }

    protected boolean recover(RecoverableSyntaxException e) {
        return getSyntaxExceptionHandler() != null && getSyntaxExceptionHandler().process(e);
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }

    public String getInterchangeControlNumber() {
        return interchangeControlNumber;
    }

    public void setInterchangeControlNumber(String interchangeControlNumber) {
        this.interchangeControlNumber = interchangeControlNumber;
    }

    public String getGroupControlNumber() {
        return groupControlNumber;
    }

    public void setGroupControlNumber(String groupControlNumber) {
        this.groupControlNumber = groupControlNumber;
    }

    public int getDocumentCount() {
        return documentCount;
    }

    public void setDocumentCount(int documentCount) {
        this.documentCount = documentCount;
    }

    public RecoverableSyntaxException getSyntaxException() {
        return syntaxException;
    }

    public void setSyntaxException(RecoverableSyntaxException syntaxException) {
        this.syntaxException = syntaxException;
    }

    protected String parseStringFromNextElement() throws IOException, EDISyntaxException {
        List<String> v = getTokenizer().nextCompositeElement();
        if (isPresent(v)) {
            String obj = v.get(0);
            if (obj != null)
                return obj;
        }
        throw new EDISyntaxException(ErrorMessages.MANDATORY_ELEMENT_MISSING, getTokenizer());
    }

    public ReplyGenerator getAckGenerator() {
        return ackGenerator;
    }

    public void setAckGenerator(ReplyGenerator generator) {
        this.ackGenerator = generator;
    }

    public ReplyGenerator getAlternateAckGenerator() {
        return alternateAckGenerator;
    }

    public void setAlternateAckGenerator(ReplyGenerator generator) {
        this.alternateAckGenerator = generator;
    }

    public PluginControllerFactoryInterface getPluginControllerFactory() {
        // Lazy load
        if (pluginControllerFactory == null) {
            pluginControllerFactory = new PluginControllerFactory();
        }
        return pluginControllerFactory;
    }

    @Override
    public void setPluginControllerFactory(PluginControllerFactoryInterface pluginControllerFactory) {
        this.pluginControllerFactory = pluginControllerFactory;
    }

    protected void parseSegment(PluginController pluginController, String segmentType) throws SAXException, IOException {
        segmentPluginController = pluginController;
        if (pluginController.transition(segmentType)) {
            // First close off any loops that were closed as the result of
            // the transition
            int toClose = pluginController.closedCount();
            if (debug)
                trace("closing " + toClose + " loops");
            for (; toClose > 0; toClose--)
                endElement(getXMLTags().getLoopTag());

            String s = pluginController.getLoopEntered();
            if (pluginController.isResumed()) {
                // We are resuming some outer loop, so we do not
                // start a new instance of the loop.
            } else {
                getDocumentAttributes().clear();
                getDocumentAttributes().addCDATA(getXMLTags().getIdAttribute(), s);
                startElement(getXMLTags().getLoopTag(), getDocumentAttributes());
            }
        }

        getDocumentAttributes().clear();
        getDocumentAttributes().addCDATA(getXMLTags().getIdAttribute(), segmentType);
        startElement(getXMLTags().getSegTag(), getDocumentAttributes());
        if (segmentPluginController != null)
            segmentPluginController.noteBeginningOfSegment(getContentHandler(), segmentType);

        Token t;
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
        if (segmentPluginController != null)
            segmentPluginController.noteEndOfSegment(getContentHandler(), segmentType);
        endElement(getXMLTags().getSegTag());
    }

    protected void startInterchange(EDIAttributes attributes)
            throws SAXException {
        startElement(getXMLTags().getInterchangeTag(), attributes);
    }

    protected void endInterchange() throws SAXException {
        endElement(getXMLTags().getInterchangeTag());
    }

    protected void startMessage(EDIAttributes attributes) throws SAXException {
        startElement(getXMLTags().getDocumentTag(), attributes);
    }

    protected String getSubElement(List<String> compositeList, int i) {
        String result = "";
        try {
            result = compositeList.get(i);
        } catch (IndexOutOfBoundsException e) {
            // ignore
        }
        return result;
    }

    protected void generatedSenderAndReceiver(String fromId, String fromQual, String fromExtra, String toId, String toQual, String toExtra) throws SAXException {
        getInterchangeAttributes().clear();
        startElement(getXMLTags().getSenderTag(), getInterchangeAttributes());
        getInterchangeAttributes().addCDATA(getXMLTags().getIdAttribute(), fromId);
        getInterchangeAttributes().addCDATA(getXMLTags().getQualifierAttribute(),
                fromQual);
        if (isPresent(fromExtra)) {
            getInterchangeAttributes().addCDATA("Extra", fromExtra);
        }
        startSenderAddress(getInterchangeAttributes());
        endElement(getXMLTags().getAddressTag());
        endElement(getXMLTags().getSenderTag());

        getInterchangeAttributes().clear();
        startElement(getXMLTags().getReceiverTag(), getInterchangeAttributes());
        getInterchangeAttributes().addCDATA(getXMLTags().getIdAttribute(), toId);
        getInterchangeAttributes().addCDATA(getXMLTags().getQualifierAttribute(),
                toQual);
        if (isPresent(toExtra)) {
            getInterchangeAttributes().addCDATA(getXMLTags()
                    .getAddressExtraAttribute(), toExtra);
        }
        startReceiverAddress(getInterchangeAttributes());
        endElement(getXMLTags().getAddressTag());
        endElement(getXMLTags().getReceiverTag());
    }

    protected void startSenderAddress(EDIAttributes attributes)
            throws SAXException {
        startElement(getXMLTags().getAddressTag(), attributes);
    }

    protected void startReceiverAddress(EDIAttributes attributes)
            throws SAXException {
        startElement(getXMLTags().getAddressTag(), attributes);
    }
}
