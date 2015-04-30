/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
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

public class DefaultXMLTags implements XMLTags {
    private static final XMLTags theInstance = new DefaultXMLTags();

    public DefaultXMLTags() {
    }

    public static XMLTags getInstance() {
        return theInstance;
    }

    @Override
    public String getRootTag() {
        return "ediroot";
    }

    @Override
    public String getInterchangeTag() {
        return "interchange";
    }

    @Override
    public String getSenderTag() {
        return "sender";
    }

    @Override
    public String getReceiverTag() {
        return "receiver";
    }

    @Override
    public String getAddressTag() {
        return "address";
    }

    @Override
    public String getAcknowledgementTag() {
        return "acknowledgement";
    }

    @Override
    public String getGroupTag() {
        return "group";
    }

    @Override
    public String getDocumentTag() {
        return "transaction";
    }

    @Override
    public String getLoopTag() {
        return "loop";
    }

    @Override
    public String getSegTag() {
        return "segment";
    }

    @Override
    public String getElementTag() {
        return "element";
    }

    @Override
    public String getCompositeTag() {
        return "composite";
    }

    @Override
    public String getSubElementTag() {
        return "subelement";
    }

    @Override
    public String getAddendaTag() {
        return "addenda";
    }

    @Override
    public String getPackageTag() {
        return "package";
    }

    @Override
    public String getIdAttribute() {
        return "Id";
    }

    @Override
    public String getQualifierAttribute() {
        return "Qual";
    }

    @Override
    public String getAddressExtraAttribute() {
        return "Extra";
    }

    @Override
    public String getSubElementSequence() {
        return "Sequence";
    }

    @Override
    public String getCompositeIndicator() {
        return "Composite";
    }

    @Override
    public String getControl() {
        return "Control";
    }

    @Override
    public String getRecipientReference() {
        return "RecipientRef";
    }

    @Override
    public String getApplicationReference() {
        return "ApplRef";
    }

    @Override
    public String getAssociation() {
        return "Association";
    }

    @Override
    public String getProcessingPriority() {
        return "Priority";
    }

    @Override
    public String getProcessingId() {
        return "ProcessingId";
    }

    @Override
    public String getAcknowledgementRequest() {
        return "AckRequest";
    }

    @Override
    public String getInterchangeAgreementIdentifier() {
        return "AgreementIdentifier";
    }

    @Override
    public String getTestIndicator() {
        return "TestIndicator";
    }

    @Override
    public String getTime() {
        return "Time";
    }

    @Override
    public String getDate() {
        return "Date";
    }

    @Override
    public String getApplReceiver() {
        return "ApplReceiver";
    }

    @Override
    public String getApplSender() {
        return "ApplSender";
    }

    @Override
    public String getGroupType() {
        return "GroupType";
    }

    @Override
    public String getStandardVersion() {
        return "StandardVersion";
    }

    @Override
    public String getStandardCode() {
        return "StandardCode";
    }

    @Override
    public String getSyntaxIdentifier() {
        return "SyntaxId";
    }

    @Override
    public String getSyntaxVersion() {
        return "SyntaxVersion";
    }

    @Override
    public String getStandard() {
        return "Standard";
    }

    @Override
    public String getName() {
        return "Name";
    }

    @Override
    public String getDocumentType() {
        return "DocType";
    }

    @Override
    public String getMessageVersion() {
        return "Version";
    }

    @Override
    public String getMessageType() {
        return "Type";
    }

    @Override
    public String getEvent() {
        return "Event";
    }

    @Override
    public String getSecurity() {
        return "Security";
    }

    @Override
    public String getMessageRelease() {
        return "Release";
    }

    @Override
    public String getAgency() {
        return "Agency";
    }

    @Override
    public String getAccessReference() {
        return "AccessReference";
    }

    @Override
    public String getDecimal() {
        return "Decimal";
    }

    @Override
    public String getPriority() {
        return "Priority";
    }

    @Override
    public String getFileIdModifier() {
        return "FileModifier";
    }

    @Override
    public String getServiceClassCode() {
        return "ServiceClassCode";
    }

    @Override
    public String getServiceClassDesc() {
        return "ServiceClassDesc";
    }

    @Override
    public String getCompanyName() {
        return "CompanyName";
    }

    @Override
    public String getDiscretionaryData() {
        return "DiscretionaryData";
    }

    @Override
    public String getStandardEntryClass() {
        return "StandardEntryClass";
    }

    @Override
    public String getStandardEntryClassDesc() {
        return "StandardEntryClassDesc";
    }

    @Override
    public String getCompanyEntryDesc() {
        return "CompanyEntryDesc";
    }

    @Override
    public String getCompanyDescriptiveDate() {
        return "CompanyDescriptiveDate";
    }

    @Override
    public String getEffectiveEntryDate() {
        return "EffectiveEntryDate";
    }

    @Override
    public String getOriginatorStatusCode() {
        return "OriginatorStatus";
    }

    @Override
    public String getOriginatingIdentity() {
        return "OriginatingIdentity";
    }

    @Override
    public String getBatchNumber() {
        return "BatchNumber";
    }

    @Override
    public String getTransactionCode() {
        return "TransactionCode";
    }

    @Override
    public String getRDFI() {
        return "RDFI";
    }

    @Override
    public String getCheckDigit() {
        return "CheckDigit";
    }

    @Override
    public String getDFIAccountNumber() {
        return "AccountNumber";
    }

    @Override
    public String getAmount() {
        return "Amount";
    }

    @Override
    public String getIdentificationNumber() {
        return "IdentificationNumber";
    }

    @Override
    public String getReceiverName() {
        return "ReceiverName";
    }

    @Override
    public String getAddendaIndicator() {
        return "AddendaIndicator";
    }

    @Override
    public String getTraceNumber() {
        return "TraceNumber";
    }

    @Override
    public String getEntryTraceNumber() {
        return "EntryTraceNumber";
    }

    @Override
    public String getAddendaType() {
        return "AddendaType";
    }

    @Override
    public String getPaymentInformation() {
        return "PaymentInformation";
    }

    @Override
    public String getStandardsId() {
        return "StandardsId";
    }

    @Override
    public String getVersion() {
        return "Version";
    }

    @Override
    public String getRelease() {
        return "Release";
    }

    @Override
    public String getAuthorizationQual() {
        return "AuthorizationQual";
    }

    @Override
    public String getAuthorization() {
        return "Authorization";
    }

    @Override
    public String getSecurityQual() {
        return "SecurityQual";
    }

    @Override
    public String getAcknowledgementCode() {
        return "AcknowledgementCode";
    }

    @Override
    public String getNotCode() {
        return "NoteCode";
    }

    @Override
    public String getTransmissionType() {
        return "TransmissionType";
    }

    @Override
    public String getBinNumber() {
        return "BINNumber";
    }

    @Override
    public String getTransactionCount() {
        return "TransactionCount";
    }

    @Override
    public String getVendor() {
        return "Vendor";
    }

    @Override
    public String getServiceProviderIdQualifier() {
        return "ServiceProviderIdQualifier";
    }

    @Override
    public String getServiceProviderId() {
        return "ServiceProviderId";
    }

    @Override
    public String getDescription() {
        return "Description";
    }
}
