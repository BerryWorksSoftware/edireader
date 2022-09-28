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

/**
 * XML tags used when generating XML from EDI
 */
public interface XMLTags {

    String getRootTag();

    String getInterchangeTag();

    String getSenderTag();

    String getReceiverTag();

    String getAddressTag();

    String getGroupTag();

    String getDocumentTag();

    String getLoopTag();

    String getSegTag();

    String getElementTag();

    String getCompositeTag();

    String getSubElementTag();

    String getAddendaTag();

    String getPackageTag();

    String getIdAttribute();

    String getQualifierAttribute();

    String getAddressExtraAttribute();

    String getSubElementSequence();

    String getCompositeIndicator();

    String getControl();

    String getRecipientReference();

    String getApplicationReference();

    String getAssociation();

    String getProcessingPriority();

    String getProcessingId();

    String getAcknowledgementRequest();

    String getInterchangeAgreementIdentifier();

    String getTestIndicator();

    String getTime();

    String getDate();

    String getApplReceiver();

    String getApplReceiverQualifier();

    String getApplSender();

    String getApplSenderQualifier();

    String getGroupType();

    String getStandardVersion();

    String getStandardCode();

    String getSyntaxIdentifier();

    String getSyntaxVersion();

    String getStandard();

    String getName();

    String getDocumentType();

    String getMessageVersion();

    String getMessageType();

    String getEvent();

    String getMessageRelease();

    String getSecurity();

    String getAgency();

    String getAccessReference();

    String getDecimal();

    String getPriority();

    String getServiceClassCode();

    String getServiceClassDesc();

    String getCompanyName();

    String getDiscretionaryData();

    String getStandardEntryClass();

    String getStandardEntryClassDesc();

    String getCompanyEntryDesc();

    String getCompanyDescriptiveDate();

    String getEffectiveEntryDate();

    String getOriginatorStatusCode();

    String getOriginatingIdentity();

    String getBatchNumber();

    String getTransactionCode();

    String getRDFI();

    String getCheckDigit();

    String getDFIAccountNumber();

    String getAmount();

    String getIdentificationNumber();

    String getReceiverName();

    String getAddendaIndicator();

    String getTraceNumber();

    String getEntryTraceNumber();

    String getAddendaType();

    String getPaymentInformation();

    String getAuthorizationQual();

    String getAuthorization();

    String getSecurityQual();

    String getAcknowledgementCode();

    String getNotCode();

    String getFileIdModifier();

    String getStandardsId();

    String getVersion();

    String getRelease();

    String getAcknowledgementTag();

    String getTransmissionType();

    String getBinNumber();

    String getTransactionCount();

    String getVendor();

    String getServiceProviderIdQualifier();

    String getServiceProviderId();

    String getDescription();

    String getRepetitionSeparator();

    String getElementDelimiter();

    String getSubElementDelimiter();

    String getSegmentTerminator();
}