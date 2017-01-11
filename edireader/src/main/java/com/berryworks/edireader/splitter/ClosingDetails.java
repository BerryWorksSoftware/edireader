/*
 * Copyright 2005-2017 by BerryWorks Software, LLC. All rights reserved.
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

public class ClosingDetails {
    private String senderQualifier;
    private String senderId;
    private String receiverQualifier;
    private String receiverId;
    private String interchangeControlNumber;
    private String groupSender;
    private String groupReceiver;
    private String groupControlNumber;
    private String documentControlNumber;
    private String documentType;
    private String version;

    public String getSenderQualifier() {
        return senderQualifier;
    }

    public void setSenderQualifier(String senderQualifier) {
        this.senderQualifier = senderQualifier;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverQualifier() {
        return receiverQualifier;
    }

    public void setReceiverQualifier(String receiverQualifier) {
        this.receiverQualifier = receiverQualifier;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getInterchangeControlNumber() {
        return interchangeControlNumber;
    }

    public void setInterchangeControlNumber(String interchangeControlNumber) {
        this.interchangeControlNumber = interchangeControlNumber;
    }

    public String getGroupSender() {
        return groupSender;
    }

    public void setGroupSender(String groupSender) {
        this.groupSender = groupSender;
    }

    public String getGroupReceiver() {
        return groupReceiver;
    }

    public void setGroupReceiver(String groupReceiver) {
        this.groupReceiver = groupReceiver;
    }

    public String getGroupControlNumber() {
        return groupControlNumber;
    }

    public void setGroupControlNumber(String groupControlNumber) {
        this.groupControlNumber = groupControlNumber;
    }

    public String getDocumentControlNumber() {
        return documentControlNumber;
    }

    public void setDocumentControlNumber(String documentControlNumber) {
        this.documentControlNumber = documentControlNumber;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
