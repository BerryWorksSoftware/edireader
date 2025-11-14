/*
 * Copyright 2005-2025 by BerryWorks Software, LLC. All rights reserved.
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

import com.berryworks.edireader.util.DateTimeGenerator;
import org.xml.sax.Attributes;

import java.io.IOException;
import java.io.Writer;

/**
 * A delegate for generating an interchange containing control messages
 * acknowledging the transaction parsed by EdifactReader.
 */
public class EdifactCONTRLGenerator extends ReplyGenerator {

    public static final String INDICATION_OF_RECEIPT = "8";
    public static final String NEGATIVE_ACKNOWLEDGMENT = "4";
    public static final String POSITIVE_ACKNOWLEDGMENT = "7";
    private final Writer ackStream;
    private char delimiter;
    private char subDelimiter;
    private String segmentTerminator;
    private String syntaxIdentifier;
    private String versionNumber;
    private String interchangeControlNumber;
    private int segmentCount;

    private String interchangeSender, interchangeSenderQualifier,
            interchangeRecipient, interchangeRecipientQualifier;

    private String generatedInterchangeControlNumber;

    private int generatedMessageNumber;

    private boolean generated;

    public EdifactCONTRLGenerator(StandardReader standardReader,
                                  Writer ackStream) {
        this.standardReader = standardReader;
        this.ackStream = ackStream;
    }

    public void generateAcknowledgmentHeader(Attributes attributes) {
        if (ackStream == null)
            return;

        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            String value = attributes.getValue(i);
            if (standardReader.getXMLTags().getControl().equals(name))
                interchangeControlNumber = value;
            else if (standardReader.getXMLTags().getSyntaxIdentifier().equals(name))
                syntaxIdentifier = value;
            else if (standardReader.getXMLTags().getSyntaxVersion().equals(name))
                versionNumber = value;
        }
    }

    public void setSender(Attributes attributes) {
        if (ackStream == null)
            return;
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            String value = attributes.getValue(i);
            if (standardReader.getXMLTags().getIdAttribute().equals(name))
                interchangeSender = value;
            else if (standardReader.getXMLTags().getQualifierAttribute().equals(name))
                interchangeSenderQualifier = value;
        }
    }

    public void setReceiver(Attributes attributes) {
        if (ackStream == null) {
            return;
        }
        for (int i = 0; i < attributes.getLength(); i++) {
            String name = attributes.getLocalName(i);
            String value = attributes.getValue(i);
            if (standardReader.getXMLTags().getIdAttribute().equals(name))
                interchangeRecipient = value;
            else if (standardReader.getXMLTags().getQualifierAttribute().equals(name))
                interchangeRecipientQualifier = value;
        }
    }

    @Override
    public void generateAcknowledgmentHeader(String xsyntaxIdentifier,
                                             String xversionNumber, String xsender, String xsenderQualifier,
                                             String xrecipient, String xrecipientQualifier, String xcontrolNumber) {
    }

    public void generateTransactionAcknowledgment(Attributes attributes) throws IOException {
        // These values are just defaults that get replaced so that they match the interchange being acknowledged.
        String messageVersionNumber = "D";
        String messageReleaseNumber = "97B";
        String controllingAgency = "UN";

        if (ackStream == null) {
            return;
        }
        XMLTags xmlTags = standardReader.getXMLTags();
        if (!generated) {
            generated = true;

            // This is where we could check to make sure to avoid
            // generating a CONTRL message for a CONTRL message.

            delimiter = standardReader.getDelimiter();
            subDelimiter = standardReader.getSubDelimiter();
            char terminator = standardReader.getTerminator();
            String terminatorSuffix = standardReader.getTerminatorSuffix();
            segmentTerminator = terminator + terminatorSuffix;
            char repetitionCharacter = standardReader.getRepetitionSeparator();
            if (repetitionCharacter == '\000')
                repetitionCharacter = ' ';

            String dateAndTime;
            if (controlDateAndTimeOverride == null) {
                dateAndTime = DateTimeGenerator.generate(subDelimiter);
            } else
                dateAndTime = controlDateAndTimeOverride;

            if (standardReader instanceof EdifactReaderWithCONTRL) {
                EdifactReaderWithCONTRL reader = (EdifactReaderWithCONTRL) standardReader;
                if (reader.isUNA()) {
                    int ri = standardReader.getRelease();
                    char r = ri < 0 ? ' ' : (char) ri;
                    ackStream.write("UNA" + subDelimiter + delimiter
                                    + reader.getDecimalMark() + r + repetitionCharacter + segmentTerminator);
                }
            }

            generatedInterchangeControlNumber = interchangeControlNumber;

            ackStream.write("UNB" + delimiter + syntaxIdentifier + subDelimiter
                            + versionNumber + delimiter + interchangeRecipient
                            + subDelimiter + interchangeRecipientQualifier + delimiter
                            + interchangeSender + subDelimiter
                            + interchangeSenderQualifier + delimiter + dateAndTime
                            + delimiter + generatedInterchangeControlNumber);
            ackStream.write(segmentTerminator);

            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getLocalName(i);
                String value = attributes.getValue(i);
                if (xmlTags.getMessageVersion().equals(name))
                    messageVersionNumber = value;
                else if (xmlTags.getMessageRelease().equals(name))
                    messageReleaseNumber = value;
                else if (xmlTags.getAgency().equals(name))
                    controllingAgency = value;
            }

            // Generate the UNH for the CONTRL message
            ackStream.write("UNH" + delimiter + (++generatedMessageNumber)
                            + delimiter + "CONTRL" + subDelimiter
                            + messageVersionNumber + subDelimiter
                            + messageReleaseNumber + subDelimiter + controllingAgency);
            ackStream.write(segmentTerminator);
            segmentCount++;

            // Generate the UCI to acknowledge the interchange.
            ackStream.write("UCI" + delimiter + interchangeControlNumber
                            + delimiter + interchangeSender + subDelimiter
                            + interchangeSenderQualifier + delimiter
                            + interchangeRecipient + subDelimiter
                            + interchangeRecipientQualifier + delimiter
                            + INDICATION_OF_RECEIPT);
            ackStream.write(segmentTerminator);
            segmentCount++;
        }

        // Generate UCM to acknowledge this message (or not)
        boolean includeUCM = false;
        if (includeUCM) {
            String messageReference, messageType, mvn, release, agency, association;
            messageReference = attributes.getValue(xmlTags.getControl());
            if (messageReference != null) {
                ackStream.write("UCM" + delimiter + messageReference);
                messageType = attributes.getValue(xmlTags.getDocumentType());
                if (messageType != null) {
                    ackStream.write(delimiter + messageType);
                    mvn = attributes.getValue(xmlTags.getVersion());
                    if (mvn != null) {
                        ackStream.write(subDelimiter + mvn);
                        release = attributes.getValue(xmlTags.getRelease());
                        if (release != null) {
                            ackStream.write(subDelimiter + release);
                            agency = attributes.getValue(xmlTags.getAgency());
                            if (agency != null) {
                                ackStream.write(subDelimiter + agency);
                                association = attributes.getValue(xmlTags.getAssociation());
                                if (association != null) {
                                    ackStream.write(subDelimiter + association);
                                }
                            }
                        }
                    }
                }
                ackStream.write(delimiter + INDICATION_OF_RECEIPT + segmentTerminator);
                segmentCount++;
            }
        }
    }

    @Override
    public void generateTransactionAcknowledgment(String transactionCode,
                                                  String controlNumber) throws IOException {
    }

    @Override
    public void generateGroupAcknowledgmentTrailer(int docCount)
            throws IOException {
    }

    @Override
    public void generateNegativeACK() {
    }

    @Override
    public void generateAcknowledgementWrapup() throws IOException {
        if (ackStream == null) {
            return;
        }

        // Generate the UNT to match the UNH
        segmentCount++;
        ackStream.write("UNT" + delimiter + segmentCount + delimiter
                        + generatedMessageNumber);
        ackStream.write(segmentTerminator);

        // Finish with a UNZ corresponding to the UNB
        ackStream.write("UNZ" + delimiter + "1" + delimiter
                        + generatedInterchangeControlNumber);
        ackStream.write(segmentTerminator);
        ackStream.close();
    }

    @Override
    public void generateAcknowledgmentHeader(String firstSegment,
                                             String groupSender, String groupReceiver, int i,
                                             String groupVersion, String groupFunctionCode,
                                             String groupControlNumber) throws IOException {
    }

}
