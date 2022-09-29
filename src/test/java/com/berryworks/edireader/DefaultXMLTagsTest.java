package com.berryworks.edireader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultXMLTagsTest {
    @Test
    public void testDefaultTags() {
        DefaultXMLTags tags = new DefaultXMLTags();

        assertEquals("ediroot", tags.getRootTag());
        assertEquals("interchange", tags.getInterchangeTag());
        assertEquals("sender", tags.getSenderTag());
        assertEquals("receiver", tags.getReceiverTag());
        assertEquals("address", tags.getAddressTag());
        assertEquals("acknowledgement", tags.getAcknowledgementTag());
        assertEquals("group", tags.getGroupTag());
        assertEquals("transaction", tags.getDocumentTag());
        assertEquals("loop", tags.getLoopTag());
        assertEquals("segment", tags.getSegTag());
        assertEquals("element", tags.getElementTag());
        assertEquals("composite", tags.getCompositeTag());
        assertEquals("subelement", tags.getSubElementTag());
        assertEquals("addenda", tags.getAddendaTag());
        assertEquals("package", tags.getPackageTag());
        assertEquals("Id", tags.getIdAttribute());
        assertEquals("Qual", tags.getQualifierAttribute());
        assertEquals("Extra", tags.getAddressExtraAttribute());
        assertEquals("Sequence", tags.getSubElementSequence());
        assertEquals("Composite", tags.getCompositeIndicator());
        assertEquals("Control", tags.getControl());
        assertEquals("RecipientRef", tags.getRecipientReference());
        assertEquals("ApplRef", tags.getApplicationReference());
        assertEquals("Association", tags.getAssociation());
        assertEquals("Priority", tags.getPriority());
        assertEquals("ProcessingId", tags.getProcessingId());
        assertEquals("AckRequest", tags.getAcknowledgementRequest());
        assertEquals("AgreementIdentifier", tags.getInterchangeAgreementIdentifier());
        assertEquals("TestIndicator", tags.getTestIndicator());
        assertEquals("Time", tags.getTime());
        assertEquals("Date", tags.getDate());
        assertEquals("ApplReceiver", tags.getApplReceiver());
        assertEquals("ApplSender", tags.getApplSender());
        assertEquals("GroupType", tags.getGroupType());
        assertEquals("StandardVersion", tags.getStandardVersion());
        assertEquals("StandardCode", tags.getStandardCode());
        assertEquals("SyntaxId", tags.getSyntaxIdentifier());
        assertEquals("SyntaxVersion", tags.getSyntaxVersion());
        assertEquals("Standard", tags.getStandard());
        assertEquals("Name", tags.getName());
        assertEquals("DocType", tags.getDocumentType());
        assertEquals("Version", tags.getVersion());
        assertEquals("Type", tags.getMessageType());
        assertEquals("Event", tags.getEvent());
        assertEquals("Security", tags.getSecurity());
        assertEquals("Release", tags.getMessageRelease());
        assertEquals("Agency", tags.getAgency());
        assertEquals("AccessReference", tags.getAccessReference());
        assertEquals("Decimal", tags.getDecimal());
        assertEquals("Priority", tags.getPriority());
        assertEquals("FileModifier", tags.getFileIdModifier());
        assertEquals("ServiceClassCode", tags.getServiceClassCode());
        assertEquals("ServiceClassDesc", tags.getServiceClassDesc());
        assertEquals("CompanyName", tags.getCompanyName());
        assertEquals("DiscretionaryData", tags.getDiscretionaryData());
        assertEquals("StandardEntryClass", tags.getStandardEntryClass());
        assertEquals("StandardEntryClassDesc", tags.getStandardEntryClassDesc());
        assertEquals("CompanyEntryDesc", tags.getCompanyEntryDesc());
        assertEquals("CompanyDescriptiveDate", tags.getCompanyDescriptiveDate());
        assertEquals("EffectiveEntryDate", tags.getEffectiveEntryDate());
        assertEquals("OriginatorStatus", tags.getOriginatorStatusCode());
        assertEquals("OriginatingIdentity", tags.getOriginatingIdentity());
        assertEquals("BatchNumber", tags.getBatchNumber());
        assertEquals("TransactionCode", tags.getTransactionCode());
        assertEquals("RDFI", tags.getRDFI());
        assertEquals("CheckDigit", tags.getCheckDigit());
        assertEquals("AccountNumber", tags.getDFIAccountNumber());
        assertEquals("Amount", tags.getAmount());
        assertEquals("IdentificationNumber", tags.getIdentificationNumber());
        assertEquals("ReceiverName", tags.getReceiverName());
        assertEquals("AddendaIndicator", tags.getAddendaIndicator());
        assertEquals("TraceNumber", tags.getTraceNumber());
        assertEquals("EntryTraceNumber", tags.getEntryTraceNumber());
        assertEquals("AddendaType", tags.getAddendaType());
        assertEquals("PaymentInformation", tags.getPaymentInformation());
        assertEquals("StandardsId", tags.getStandardsId());
        assertEquals("Version", tags.getVersion());
        assertEquals("Release", tags.getRelease());
        assertEquals("AuthorizationQual", tags.getAuthorizationQual());
        assertEquals("Authorization", tags.getAuthorization());
        assertEquals("SecurityQual", tags.getSecurityQual());
        assertEquals("AcknowledgementCode", tags.getAcknowledgementCode());
        assertEquals("NoteCode", tags.getNotCode());
    }
}


