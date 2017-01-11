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

package com.berryworks.edireader.splitter;

import com.berryworks.edireader.*;
import com.berryworks.edireader.error.EDISyntaxExceptionHandler;
import com.berryworks.edireader.error.RecoverableSyntaxException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

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
    private final HandlerFactory handlerFactory;
    private final XMLTags xmlTags = DefaultXMLTags.getInstance();
    private final String interchangeTagName = xmlTags.getInterchangeTag();
    private final String senderTagName = xmlTags.getSenderTag();
    private final String receiverTagName = xmlTags.getReceiverTag();
    private final String addressTagName = xmlTags.getAddressTag();

    private ContentHandler contentHandler;
    private boolean pendingDocumentClose;
    private boolean senderAddress;
    private Attributes interchangeAttributes;
    private Attributes senderAttributes;
    private Attributes receiverAttributes;
    private Attributes groupAttributes;
    private Attributes documentAttributes;
    private final Attributes emptyAttributes = new AttributesImpl();
    private int transactionCountLimit;
    private int transactionsInGroupCount;


    public SplittingHandler(HandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    public void split(InputSource inputSource) throws IOException, SAXException {
        char[] leftOver = null;
        EDIReader parser;
        while ((parser = EDIReaderFactory.createEDIReader(inputSource, leftOver)) != null) {
            parser.setContentHandler(this);
            parser.setSyntaxExceptionHandler(new MyErrorHandler());
            parser.parse(inputSource);
            leftOver = parser.getTokenizer().getBuffered();
        }
        handlerFactory.markEndOfStream();
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
        closingDetails.setSenderQualifier(senderAttributes.getValue(xmlTags.getQualifierAttribute()));
        closingDetails.setSenderId(senderAttributes.getValue(xmlTags.getIdAttribute()));
        closingDetails.setReceiverQualifier(receiverAttributes.getValue(xmlTags.getQualifierAttribute()));
        closingDetails.setReceiverId(receiverAttributes.getValue(xmlTags.getIdAttribute()));
        closingDetails.setInterchangeControlNumber(interchangeAttributes.getValue(xmlTags.getControl()));
        closingDetails.setGroupSender(groupAttributes.getValue(xmlTags.getApplSender()));
        closingDetails.setGroupReceiver(groupAttributes.getValue(xmlTags.getApplReceiver()));
        closingDetails.setGroupControlNumber(groupAttributes.getValue(xmlTags.getControl()));
        closingDetails.setDocumentControlNumber(documentAttributes.getValue(xmlTags.getControl()));
        closingDetails.setDocumentType(documentAttributes.getValue(xmlTags.getDocumentType()));
        closingDetails.setVersion(groupAttributes.getValue(xmlTags.getStandardVersion()));
        try {
            handlerFactory.closeDocument(closingDetails);
        } catch (Exception e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (xmlTags.getInterchangeTag().equals(localName)) {
            interchangeAttributes = new EDIAttributes(attributes);

        } else if (xmlTags.getSenderTag().equals(localName)) {
            senderAddress = true;

        } else if (xmlTags.getReceiverTag().equals(localName)) {
            senderAddress = false;

        } else if (xmlTags.getAddressTag().equals(localName)) {
            if (senderAddress) {
                senderAttributes = new EDIAttributes(attributes);
            } else {
                receiverAttributes = new EDIAttributes(attributes);
            }

        } else if (xmlTags.getGroupTag().equals(localName)) {
            transactionsInGroupCount = 0;

            if (pendingDocumentClose) {
                generateArtificialBoundaryForNewGroup();
                pendingDocumentClose = false;
            }
            groupAttributes = new EDIAttributes(attributes);
            documentAttributes = null;

        } else if (xmlTags.getDocumentTag().equals(localName)) {
            if (pendingDocumentClose) {
                generateArtificialBoundaryForNewDocument();
                pendingDocumentClose = false;
            }
            documentAttributes = new EDIAttributes(attributes);
        }

        contentHandler.startElement(uri, localName, qName, attributes);
    }

    private void generateArtificialBoundaryForNewDocument() throws SAXException {
        pendingDocumentClose = false;

        // Close off the current group
        String groupTagName = xmlTags.getGroupTag();
        endElement("", groupTagName, groupTagName);

        generateArtificialBoundaryForNewGroup();

        // Open an new group
        startElement("", groupTagName, groupTagName, groupAttributes);
    }

    private void generateArtificialBoundaryForNewGroup() throws SAXException {
        pendingDocumentClose = false;

        // Close off the current interchange, transaction, root, and XML document
        endElement("", interchangeTagName, interchangeTagName);
        String rootTagName = xmlTags.getRootTag();
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

        if (xmlTags.getDocumentTag().equals(localName)) {
            transactionsInGroupCount++;
            if (transactionsInGroupCount >= transactionCountLimit) {
                pendingDocumentClose = true;
            }


        } else if (xmlTags.getInterchangeTag().equals(localName)) {
            pendingDocumentClose = false;
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        contentHandler.characters(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        contentHandler.processingInstruction(target, data);
    }

    public void setTransactionCountLimit(int transactionCountLimit) {
        this.transactionCountLimit = transactionCountLimit;
    }


    private class MyErrorHandler implements EDISyntaxExceptionHandler {

        @Override
        public boolean process(RecoverableSyntaxException syntaxException) {
            // Return true to indicate that we want parsing to continue.
            return true;
        }
    }
}
