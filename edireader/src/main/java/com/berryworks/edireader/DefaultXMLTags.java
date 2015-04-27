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

public class DefaultXMLTags implements XMLTags
{
  private static final XMLTags theInstance = new DefaultXMLTags();

  public DefaultXMLTags()
  {
  }

  public String getRootTag()
  {
    return "ediroot";
  }

  public String getInterchangeTag()
  {
    return "interchange";
  }

  public String getSenderTag()
  {
    return "sender";
  }

  public String getReceiverTag()
  {
    return "receiver";
  }

  public String getAddressTag()
  {
    return "address";
  }

  public String getAcknowledgementTag()
  {
    return "acknowledgement";
  }

  public String getGroupTag()
  {
    return "group";
  }

  public String getDocumentTag()
  {
    return "transaction";
  }

  public String getLoopTag()
  {
    return "loop";
  }

  public String getSegTag()
  {
    return "segment";
  }

  public String getElementTag()
  {
    return "element";
  }

  public String getCompositeTag()
  {
    return "composite";
  }

  public String getSubElementTag()
  {
    return "subelement";
  }

  public String getAddendaTag()
  {
    return "addenda";
  }

  public String getPackageTag()
  {
    return "package";
  }

  public String getIdAttribute()
  {
    return "Id";
  }

  public String getQualifierAttribute()
  {
    return "Qual";
  }

  public String getAddressExtraAttribute()
  {
    return "Extra";
  }

  public String getSubElementSequence()
  {
    return "Sequence";
  }

  public String getCompositeIndicator()
  {
    return "Composite";
  }

  public String getControl()
  {
    return "Control";
  }

  public String getRecipientReference()
  {
    return "RecipientRef";
  }

  public String getApplicationReference()
  {
    return "ApplRef";
  }

  public String getAssociation()
  {
    return "Association";
  }

  public String getProcessingPriority()
  {
    return "Priority";
  }

  public String getProcessingId()
  {
    return "ProcessingId";
  }

  public String getAcknowledgementRequest()
  {
    return "AckRequest";
  }

  public String getInterchangeAgreementIdentifier()
  {
    return "AgreementIdentifier";
  }

  public String getTestIndicator()
  {
    return "TestIndicator";
  }

  public String getTime()
  {
    return "Time";
  }

  public String getDate()
  {
    return "Date";
  }

  public String getApplReceiver()
  {
    return "ApplReceiver";
  }

  public String getApplSender()
  {
    return "ApplSender";
  }

  public String getGroupType()
  {
    return "GroupType";
  }

  public String getStandardVersion()
  {
    return "StandardVersion";
  }

  public String getStandardCode()
  {
    return "StandardCode";
  }

  public String getSyntaxIdentifier()
  {
    return "SyntaxId";
  }

  public String getSyntaxVersion()
  {
    return "SyntaxVersion";
  }

  public String getStandard()
  {
    return "Standard";
  }

  public String getName()
  {
    return "Name";
  }

  public String getDocumentType()
  {
    return "DocType";
  }

  public String getMessageVersion()
  {
    return "Version";
  }

  public String getMessageType()
  {
    return "Type";
  }

  public String getEvent()
  {
    return "Event";
  }

  public String getSecurity()
  {
    return "Security";
  }

  public String getMessageRelease()
  {
    return "Release";
  }

  public String getAgency()
  {
    return "Agency";
  }

  public String getAccessReference()
  {
    return "AccessReference";
  }

  public String getDecimal()
  {
    return "Decimal";
  }

  public String getPriority()
  {
    return "Priority";
  }

  public String getFileIdModifier()
  {
    return "FileModifier";
  }

  public String getServiceClassCode()
  {
    return "ServiceClassCode";
  }

  public String getServiceClassDesc()
  {
    return "ServiceClassDesc";
  }

  public String getCompanyName()
  {
    return "CompanyName";
  }

  public String getDiscretionaryData()
  {
    return "DiscretionaryData";
  }

  public String getStandardEntryClass()
  {
    return "StandardEntryClass";
  }

  public String getStandardEntryClassDesc()
  {
    return "StandardEntryClassDesc";
  }

  public String getCompanyEntryDesc()
  {
    return "CompanyEntryDesc";
  }

  public String getCompanyDescriptiveDate()
  {
    return "CompanyDescriptiveDate";
  }

  public String getEffectiveEntryDate()
  {
    return "EffectiveEntryDate";
  }

  public String getOriginatorStatusCode()
  {
    return "OriginatorStatus";
  }

  public String getOriginatingIdentity()
  {
    return "OriginatingIdentity";
  }

  public String getBatchNumber()
  {
    return "BatchNumber";
  }

  public String getTransactionCode()
  {
    return "TransactionCode";
  }

  public String getRDFI()
  {
    return "RDFI";
  }

  public String getCheckDigit()
  {
    return "CheckDigit";
  }

  public String getDFIAccountNumber()
  {
    return "AccountNumber";
  }

  public String getAmount()
  {
    return "Amount";
  }

  public String getIdentificationNumber()
  {
    return "IdentificationNumber";
  }

  public String getReceiverName()
  {
    return "ReceiverName";
  }

  public String getAddendaIndicator()
  {
    return "AddendaIndicator";
  }

  public String getTraceNumber()
  {
    return "TraceNumber";
  }

  public String getEntryTraceNumber()
  {
    return "EntryTraceNumber";
  }

  public String getAddendaType()
  {
    return "AddendaType";
  }

  public String getPaymentInformation()
  {
    return "PaymentInformation";
  }

  public String getStandardsId()
  {
    return "StandardsId";
  }

  public String getVersion()
  {
    return "Version";
  }

  public String getRelease()
  {
    return "Release";
  }

  public String getAuthorizationQual()
  {
    return "AuthorizationQual";
  }

  public String getAuthorization()
  {
    return "Authorization";
  }

  public String getSecurityQual()
  {
    return "SecurityQual";
  }

  public String getAcknowledgementCode()
  {
    return "AcknowledgementCode";
  }

  public String getNotCode()
  {
    return "NoteCode";
  }


  public static XMLTags getInstance()
  {
    return theInstance;
  }
}
