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
public interface XMLTags
{

  public abstract String getRootTag();

  public abstract String getInterchangeTag();

  public abstract String getSenderTag();

  public abstract String getReceiverTag();

  public abstract String getAddressTag();

  public abstract String getGroupTag();

  public abstract String getDocumentTag();

  public abstract String getLoopTag();

  public abstract String getSegTag();

  public abstract String getElementTag();

  public abstract String getCompositeTag();

  public abstract String getSubElementTag();

  public abstract String getAddendaTag();

  public abstract String getPackageTag();

  public abstract String getIdAttribute();

  public abstract String getQualifierAttribute();

  public abstract String getAddressExtraAttribute();

  public abstract String getSubElementSequence();

  public abstract String getCompositeIndicator();

  public abstract String getControl();

  public abstract String getRecipientReference();

  public abstract String getApplicationReference();

  public abstract String getAssociation();

  public abstract String getProcessingPriority();

  public abstract String getProcessingId();

  public abstract String getAcknowledgementRequest();

  public abstract String getInterchangeAgreementIdentifier();

  public abstract String getTestIndicator();

  public abstract String getTime();

  public abstract String getDate();

  public abstract String getApplReceiver();

  public abstract String getApplSender();

  public abstract String getGroupType();

  public abstract String getStandardVersion();

  public abstract String getStandardCode();

  public abstract String getSyntaxIdentifier();

  public abstract String getSyntaxVersion();

  public abstract String getStandard();

  public abstract String getName();

  public abstract String getDocumentType();

  public abstract String getMessageVersion();

  public abstract String getMessageType();

  public abstract String getEvent();

  public abstract String getMessageRelease();

  public abstract String getSecurity();

  public abstract String getAgency();

  public abstract String getAccessReference();

  public abstract String getDecimal();

  public abstract String getPriority();

  public abstract String getServiceClassCode();

  public abstract String getServiceClassDesc();

  public abstract String getCompanyName();

  public abstract String getDiscretionaryData();

  public abstract String getStandardEntryClass();

  public abstract String getStandardEntryClassDesc();

  public abstract String getCompanyEntryDesc();

  public abstract String getCompanyDescriptiveDate();

  public abstract String getEffectiveEntryDate();

  public abstract String getOriginatorStatusCode();

  public abstract String getOriginatingIdentity();

  public abstract String getBatchNumber();

  public abstract String getTransactionCode();

  public abstract String getRDFI();

  public abstract String getCheckDigit();

  public abstract String getDFIAccountNumber();

  public abstract String getAmount();

  public abstract String getIdentificationNumber();

  public abstract String getReceiverName();

  public abstract String getAddendaIndicator();

  public abstract String getTraceNumber();

  public abstract String getEntryTraceNumber();

  public abstract String getAddendaType();

  public abstract String getPaymentInformation();

  public abstract String getAuthorizationQual();

  public abstract String getAuthorization();

  public abstract String getSecurityQual();

  public abstract String getAcknowledgementCode();

  public abstract String getNotCode();

  public String getFileIdModifier();

  public String getStandardsId();

  public String getVersion();

  public String getRelease();

  public String getAcknowledgementTag();

}