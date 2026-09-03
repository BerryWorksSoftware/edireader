/*
 * Copyright 2005-2026 by BerryWorks Software. All rights reserved.
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
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with EDIReader. If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.berryworks.edireader;

/**
 * XML element and attribute names used when generating XML from EDI.
 */
public final class XMLTags {

    private XMLTags() {
        // Utility class
    }

    public static final String ROOT = "ediroot";

    public static final String INTERCHANGE = "interchange";
    public static final String SENDER = "sender";
    public static final String RECEIVER = "receiver";
    public static final String ADDRESS = "address";
    public static final String ACKNOWLEDGEMENT = "acknowledgement";
    public static final String GROUP = "group";
    public static final String DOCUMENT = "transaction";
    public static final String LOOP = "loop";
    public static final String SEGMENT = "segment";
    public static final String ELEMENT = "element";
    public static final String COMPOSITE = "Composite";
    public static final String SUB_ELEMENT = "subelement";
    public static final String SUB_SUB_ELEMENT = "subsubelement";
    public static final String ADDENDA = "addenda";
    public static final String PACKAGE = "package";

    public static final String ID = "Id";
    public static final String QUALIFIER = "Qual";
    public static final String ADDRESS_EXTRA = "Extra";
    public static final String SUB_ELEMENT_SEQUENCE = "Sequence";
    public static final String COMPOSITE_INDICATOR = "Composite";

    public static final String CONTROL = "Control";
    public static final String RECIPIENT_REFERENCE = "RecipientRef";
    public static final String APPLICATION_REFERENCE = "ApplRef";
    public static final String ASSOCIATION = "Association";
    public static final String PROCESSING_PRIORITY = "Priority";
    public static final String PROCESSING_ID = "ProcessingId";
    public static final String ACKNOWLEDGEMENT_REQUEST = "AckRequest";
    public static final String INTERCHANGE_AGREEMENT_IDENTIFIER = "AgreementIdentifier";
    public static final String TEST_INDICATOR = "TestIndicator";
    public static final String TIME = "Time";
    public static final String DATE = "Date";

    public static final String APPL_RECEIVER = "ApplReceiver";
    public static final String APPL_RECEIVER_QUALIFIER = "ApplReceiverQual";
    public static final String APPL_SENDER = "ApplSender";
    public static final String APPL_SENDER_QUALIFIER = "ApplSenderQual";

    public static final String GROUP_TYPE = "GroupType";
    public static final String STANDARD_VERSION = "StandardVersion";
    public static final String STANDARD_CODE = "StandardCode";
    public static final String SYNTAX_IDENTIFIER = "SyntaxId";
    public static final String SYNTAX_VERSION = "SyntaxVersion";
    public static final String STANDARD = "Standard";
    public static final String NAME = "Name";

    public static final String DOCUMENT_TYPE = "DocType";
    public static final String MESSAGE_VERSION = "Version";
    public static final String MESSAGE_TYPE = "Type";
    public static final String EVENT = "Event";
    public static final String MESSAGE_RELEASE = "Release";

    public static final String SECURITY = "Security";
    public static final String AGENCY = "Agency";
    public static final String ACCESS_REFERENCE = "AccessReference";
    public static final String DECIMAL = "Decimal";
    public static final String PRIORITY = "Priority";

    public static final String FILE_ID_MODIFIER = "FileModifier";

    public static final String SERVICE_CLASS_CODE = "ServiceClassCode";
    public static final String SERVICE_CLASS_DESC = "ServiceClassDesc";

    public static final String COMPANY_NAME = "CompanyName";
    public static final String DISCRETIONARY_DATA = "DiscretionaryData";
    public static final String STANDARD_ENTRY_CLASS = "StandardEntryClass";
    public static final String STANDARD_ENTRY_CLASS_DESC = "StandardEntryClassDesc";
    public static final String COMPANY_ENTRY_DESC = "CompanyEntryDesc";
    public static final String COMPANY_DESCRIPTIVE_DATE = "CompanyDescriptiveDate";
    public static final String EFFECTIVE_ENTRY_DATE = "EffectiveEntryDate";
    public static final String ORIGINATOR_STATUS_CODE = "OriginatorStatus";
    public static final String ORIGINATING_IDENTITY = "OriginatingIdentity";

    public static final String BATCH_NUMBER = "BatchNumber";
    public static final String TRANSACTION_CODE = "TransactionCode";
    public static final String RDFI = "RDFI";
    public static final String CHECK_DIGIT = "CheckDigit";
    public static final String DFI_ACCOUNT_NUMBER = "AccountNumber";
    public static final String AMOUNT = "Amount";
    public static final String IDENTIFICATION_NUMBER = "IdentificationNumber";
    public static final String RECEIVER_NAME = "ReceiverName";
    public static final String ADDENDA_INDICATOR = "AddendaIndicator";
    public static final String TRACE_NUMBER = "TraceNumber";
    public static final String ENTRY_TRACE_NUMBER = "EntryTraceNumber";
    public static final String ADDENDA_TYPE = "AddendaType";
    public static final String PAYMENT_INFORMATION = "PaymentInformation";

    public static final String STANDARDS_ID = "StandardsId";
    public static final String VERSION = "Version";
    public static final String RELEASE = "Release";

    public static final String AUTHORIZATION_QUALIFIER = "AuthorizationQual";
    public static final String AUTHORIZATION = "Authorization";
    public static final String SECURITY_QUALIFIER = "SecurityQual";
    public static final String ACKNOWLEDGEMENT_CODE = "AcknowledgementCode";
    public static final String NOTE_CODE = "NoteCode";
    public static final String TRANSMISSION_TYPE = "TransmissionType";
    public static final String BIN_NUMBER = "BINNumber";
    public static final String TRANSACTION_COUNT = "TransactionCount";
    public static final String VENDOR = "Vendor";

    public static final String SERVICE_PROVIDER_ID_QUALIFIER =
            "ServiceProviderIdQualifier";
    public static final String SERVICE_PROVIDER_ID = "ServiceProviderId";

    public static final String DESCRIPTION = "Description";

    public static final String REPETITION_SEPARATOR = "RepetitionSeparator";
    public static final String ELEMENT_DELIMITER = "ElementDelimiter";
    public static final String SUB_ELEMENT_DELIMITER = "SubElementDelimiter";
    public static final String SEGMENT_TERMINATOR = "SegmentTerminator";
}