/*
 * Copyright 2005-2022 by BerryWorks Software, LLC. All rights reserved.
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

package com.berryworks.edireader.splitter;

import com.berryworks.edireader.*;
import com.berryworks.edireader.error.EDISyntaxExceptionHandler;
import com.berryworks.edireader.error.RecoverableSyntaxException;
import com.berryworks.edireader.filter.EdiReaderFilter;
import com.berryworks.edireader.plugin.AbstractPluginControllerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

import static com.berryworks.edireader.util.FixedLength.isPresent;

/**
 * Splits an EDI interchange containing multiple
 * documents into a series of interchanges containing one document each.
 * <p>
 * This implementation of a SAX ContentHandler filters the SAX
 * calls produced by parsing an interchange into an equivalent sequence of
 * SAX calls that make each EDI document appear to have been in an interchange
 * containing only that one document.
 */
public class SplittingHandler extends DefaultHandler {
    protected final String interchangeTagName = XMLTags.INTERCHANGE;
    protected final String senderTagName = XMLTags.SENDER;
    protected final String receiverTagName = XMLTags.RECEIVER;
    protected final String addressTagName = XMLTags.ADDRESS;

    protected final HandlerFactory handlerFactory;
    protected SplittingLevel level;
    protected ContentHandler contentHandler;
    protected boolean pendingDocumentClose;
    protected boolean senderAddress;
    protected Attributes interchangeAttributes;
    protected Attributes senderAttributes;
    protected Attributes receiverAttributes;
    protected Attributes groupAttributes;
    protected Attributes documentAttributes;
    protected final Attributes emptyAttributes = new AttributesImpl();
    protected int transactionsInInterchangeCount;
    protected int transactionsInGroupCount;
    protected int segmentCount;
    protected int transactionCountLimit;
    protected int segmentCountLimit;
    protected AbstractPluginControllerFactory pluginControllerFactory;
    protected EdiReaderFilter filter;
    protected SyntaxDescriptor syntaxDescriptor;


    public SplittingHandler(HandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    public void split(InputSource inputSource) throws IOException, SAXException {
        char[] leftOver = null;
        EDIReader parser;
        while ((parser = EDIReaderFactory.createEDIReader(inputSource, leftOver)) != null) {
            noteSyntaxDetails(parser);
            parser.setContentHandler(this);
            parser.setSyntaxExceptionHandler(new MyErrorHandler());
            if (pluginControllerFactory != null) {
                parser.setPluginControllerFactory(pluginControllerFactory);
            }
            if (filter != null) {
                final EDIReader f = filter.filter(parser);
                f.parse(inputSource);
            } else {
                parser.parse(inputSource);
            }
            leftOver = parser.getTokenizer().getBuffered();
        }
        handlerFactory.markEndOfStream();
    }

    protected void noteSyntaxDetails(EDIReader parser) {
        syntaxDescriptor = new SyntaxDescriptor();
        syntaxDescriptor.setDelimiter(parser.getDelimiter());
        syntaxDescriptor.setSubDelimiter(parser.getSubDelimiter());
        syntaxDescriptor.setTerminator(parser.getTerminator());
        syntaxDescriptor.setTerminatorSuffix(parser.getTerminatorSuffix());
        syntaxDescriptor.setRelease(parser.getRelease());
        syntaxDescriptor.setDecimalMark(parser.getDecimalMark());
        syntaxDescriptor.setRepetitionSeparator(parser.getRepetitionSeparator());
        syntaxDescriptor.setSubSubDelimiter(parser.getSubSubDelimiter());
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            contentHandler = handlerFactory.createDocument();
        } catch (Exception e) {
            throw new SAXException(e);
        }
        contentHandler.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        contentHandler.endDocument();
        ClosingDetails closingDetails = new ClosingDetails();
        closingDetails.setSenderQualifier(senderAttributes.getValue(XMLTags.QUALIFIER));
        closingDetails.setSenderId(senderAttributes.getValue(XMLTags.ID));
        closingDetails.setReceiverQualifier(receiverAttributes.getValue(XMLTags.QUALIFIER));
        closingDetails.setReceiverId(receiverAttributes.getValue(XMLTags.ID));
        closingDetails.setInterchangeControlNumber(interchangeAttributes.getValue(XMLTags.CONTROL));
        closingDetails.setTestIndicator(interchangeAttributes.getValue(XMLTags.TEST_INDICATOR));
        closingDetails.setGroupSender(groupAttributes.getValue(XMLTags.APPL_SENDER));
        closingDetails.setGroupReceiver(groupAttributes.getValue(XMLTags.APPL_RECEIVER));
        closingDetails.setGroupControlNumber(groupAttributes.getValue(XMLTags.CONTROL));
        closingDetails.setDocumentControlNumber(documentAttributes.getValue(XMLTags.CONTROL));
        closingDetails.setDocumentType(documentAttributes.getValue(XMLTags.DOCUMENT_TYPE));
        String version = groupAttributes.getValue(XMLTags.STANDARD_VERSION);
        if (!isPresent(version)) {
            // If there was no version available from the group, such as 005010 from the GS,
            // then this may be an EDIFACT scenario and the most interesting version is from
            // the UNH, such as 97A.
            version = documentAttributes.getValue(XMLTags.RELEASE);
        }
        closingDetails.setVersion(version);
        try {
            handlerFactory.closeDocument(closingDetails);
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (XMLTags.INTERCHANGE.equals(localName)) {
            transactionsInInterchangeCount = 0;
            segmentCount = 0;
            interchangeAttributes = new EDIAttributes(attributes);

        } else if (XMLTags.SENDER.equals(localName)) {
            senderAddress = true;

        } else if (XMLTags.RECEIVER.equals(localName)) {
            senderAddress = false;

        } else if (XMLTags.ADDRESS.equals(localName)) {
            if (senderAddress) {
                senderAttributes = new EDIAttributes(attributes);
            } else {
                receiverAttributes = new EDIAttributes(attributes);
            }

        } else if (XMLTags.GROUP.equals(localName)) {
            transactionsInGroupCount = 0;

            if (pendingDocumentClose) {
                generateArtificialBoundaryForNewGroup();
                pendingDocumentClose = false;
            }
            groupAttributes = new EDIAttributes(attributes);
            documentAttributes = null;

        } else if (XMLTags.DOCUMENT.equals(localName)) {
            if (pendingDocumentClose) {
                generateArtificialBoundaryForNewDocument();
                pendingDocumentClose = false;
            }
            documentAttributes = new EDIAttributes(attributes);

        } else if (XMLTags.SEGMENT.equals(localName)) {
            segmentCount++;
        }

        contentHandler.startElement(uri, localName, qName, attributes);
    }

    protected void generateArtificialBoundaryForNewDocument() throws SAXException {
        pendingDocumentClose = false;

        // Close off the current group
        String groupTagName = XMLTags.GROUP;
        endElement("", groupTagName, groupTagName);

        generateArtificialBoundaryForNewGroup();

        // Open a new group
        startElement("", groupTagName, groupTagName, groupAttributes);
    }

    protected void generateArtificialBoundaryForNewGroup() throws SAXException {
        pendingDocumentClose = false;

        // Close off the current interchange, transaction, root, and XML document
        endElement("", interchangeTagName, interchangeTagName);
        String rootTagName = XMLTags.ROOT;
        endElement("", rootTagName, rootTagName);
        endDocument();

        // Open up new ones
        startDocument();
        startElement("", rootTagName, rootTagName, emptyAttributes);
        startElement("", interchangeTagName, interchangeTagName, interchangeAttributes);

        // Take care of the <sender> and <receiver>
        startElement("", senderTagName, senderTagName, emptyAttributes);
        startElement("", addressTagName, addressTagName, senderAttributes);
        endElement("", addressTagName, addressTagName);
        endElement("", senderTagName, senderTagName);
        startElement("", receiverTagName, receiverTagName, emptyAttributes);
        startElement("", addressTagName, addressTagName, receiverAttributes);
        endElement("", addressTagName, addressTagName);
        endElement("", receiverTagName, receiverTagName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        contentHandler.endElement(uri, localName, qName);

        if (XMLTags.DOCUMENT.equals(localName)) {
            transactionsInInterchangeCount++;
            transactionsInGroupCount++;

            if (transactionsInInterchangeCount >= transactionCountLimit) {
                // The default transaction count limit is 1
                pendingDocumentClose = true;
            } else if (segmentCountLimit > 0 && segmentCount >= segmentCountLimit) {
                // The default segment count limit is infinity (unlimited)
                pendingDocumentClose = true;
            }

        } else if (XMLTags.INTERCHANGE.equals(localName)) {
            pendingDocumentClose = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        contentHandler.characters(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        contentHandler.processingInstruction(target, data);
    }

    public void setTransactionCountLimit(int transactionCountLimit) {
        this.transactionCountLimit = transactionCountLimit;
    }

    public void setSegmentCountLimit(int limit) {
        this.segmentCountLimit = limit;
    }

    public void setPluginControllerFactory(AbstractPluginControllerFactory pluginControllerFactory) {
        this.pluginControllerFactory = pluginControllerFactory;
    }

    public AbstractPluginControllerFactory getPluginControllerFactory() {
        return pluginControllerFactory;
    }

    public void setFilter(EdiReaderFilter filter) {
        this.filter = filter;
    }

    public EdiReaderFilter getFilter() {
        return filter;
    }

    public HandlerFactory getHandlerFactory() {
        return handlerFactory;
    }

    public SyntaxDescriptor getSyntaxDescriptor() {
        return syntaxDescriptor;
    }

    public SplittingLevel getLevel() {
        return level;
    }

    public void setLevel(SplittingLevel level) {
        this.level = level;
    }

    protected static class MyErrorHandler implements EDISyntaxExceptionHandler {

        @Override
        public boolean process(RecoverableSyntaxException syntaxException) {
            // Return true to indicate that we want parsing to continue.
            return true;
        }
    }
}
