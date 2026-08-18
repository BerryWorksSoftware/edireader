/*
 * Copyright 2005-2026 by BerryWorks Software, LLC. All rights reserved.
 */
package com.berryworks.edireader.hl7;

import java.util.HashMap;
import java.util.Map;

/**
 * Table 0003 from HL7 Specifications mapping Event Types to descriptions.
 */
public class Table03 {
    protected static Map<String, String> theTable;
    protected static Table03 theInstance = new Table03();

    private Table03() {
        if (theTable != null) {
            return;
        }
        theTable = new HashMap<>(150);
        theTable.put("A01", "Admit / visit notification");
        theTable.put("A02", "Transfer a patient");
        theTable.put("A03", "Discharge/end visit");
        theTable.put("A04", "Register a patient");
        theTable.put("A05", "Pre-admit a patient");
        theTable.put("A06", "Change an outpatient to an inpatient");
        theTable.put("A07", "Change an inpatient to an outpatient");
        theTable.put("A08", "Update patient information");
        theTable.put("A09", "Patient departing - tracking");
        theTable.put("A10", "Patient arriving - tracking");
        theTable.put("A11", "Cancel admit/visit notification");
        theTable.put("A12", "Cancel transfer");
        theTable.put("A13", "Cancel discharge/end visit");
        theTable.put("A14", "Pending admit");
        theTable.put("A15", "Pending transfer");
        theTable.put("A16", "Pending discharge");
        theTable.put("A17", "Swap patients");
        theTable.put("A18", "Merge patient information");
        theTable.put("A19", "Patient query");
        theTable.put("A20", "Bed status update");
        theTable.put("A21", "Patient goes on a leave of absence");
        theTable.put("A22", "Patient returns from a leave of absence");
        theTable.put("A23", "Delete a patient record");
        theTable.put("A24", "Link patient information");
        theTable.put("A25", "Cancel pending discharge");
        theTable.put("A26", "Cancel pending transfer");
        theTable.put("A27", "Cancel pending admit");
        theTable.put("A28", "Add person information");
        theTable.put("A29", "Delete person information");
        theTable.put("A30", "Merge person information");
        theTable.put("A31", "Update person information");
        theTable.put("A32", "Cancel patient arriving - tracking");
        theTable.put("A33", "Cancel patient departing - tracking");
        theTable.put("A34", "Merge patient information - patient ID only");
        theTable.put("A35", "Merge patient information - account number only");
        theTable.put("A36",
                "Merge patient information - patient ID and account number");
        theTable.put("A37", "Unlink patient information");
        theTable.put("A38", "Cancel pre-admit");
        theTable.put("A39", "Merge person - external ID");
        theTable.put("A40", "Merge patient - internal ID");
        theTable.put("A41", "Merge account - patient account number");
        theTable.put("A42", "Merge visit - visit number");
        theTable.put("A43", "Move patient information - internal ID");
        theTable
                .put("A44", "Move account information - patient account number");
        theTable.put("A45", "Move visit information - visit number");
        theTable.put("A46", "Change external ID");
        theTable.put("A47", "Change internal ID");
        theTable.put("A48", "Change alternate patient ID");
        theTable.put("A49", "Change patient account number");
        theTable.put("A50", "Change visit number");
        theTable.put("A51", "Change alternate visit ID");
        theTable.put("C01", "Register a patient on a clinical trial");
        theTable
                .put("C02",
                        "Cancel a patient registration on clinical trial (for clerical mistakes only)");
        theTable.put("C03", "Correct/update registration information");
        theTable.put("C04", "Patient has gone off a clinical trial");
        theTable.put("C05", "Patient enters phase of clinical trial");
        theTable.put("C06",
                "Cancel patient entering a phase (clerical mistake)");
        theTable.put("C07", "Correct/update phase information");
        theTable.put("C08", "Patient has gone off phase of clinical trial");
        theTable.put("C09",
                "Automated time intervals for reporting, like monthly");
        theTable.put("C10", "Patient completes the clinical trial");
        theTable.put("C11", "Patient completes a phase of the clinical trial");
        theTable.put("C12",
                "Update/correction of patient order/result information");
        theTable.put("CNQ", "Cancel query");
        theTable.put("G01", "Patient goal");
        theTable.put("I01", "Request for insurance information");
        theTable
                .put("I02", "Request/receipt of patient selection display list");
        theTable.put("I03", "Request/receipt of patient selection list");
        theTable.put("I04", "Request for patient demographic data");
        theTable.put("I05", "Request for patient clinical information");
        theTable.put("I06", "Request/receipt of clinical data listing");
        theTable.put("I07", "Unsolicited insurance information");
        theTable.put("I08", "Request for treatment authorization information");
        theTable.put("I09", "Request for modification to an authorization");
        theTable.put("I10", "Request for resubmission of an authorization");
        theTable.put("I11", "Request for cancellation of an authorization");
        theTable.put("I12", "Patient referral");
        theTable.put("I13", "Modify patient referral");
        theTable.put("I14", "Cancel patient referral");
        theTable.put("I15", "Request patient referral status");
        theTable
                .put("M01",
                        "Master file not otherwise specified (for backward compatibility only)");
        theTable.put("M02", "Master file - Staff Practitioner");
        theTable
                .put("M03",
                        "Master file - Test/Observation (for backward compatibility only)");
        theTable.put("M04", "Master files charge description");
        theTable.put("M05", "Patient location master file");
        theTable.put("M06",
                "Clinical study with phases and schedules master file");
        theTable.put("M07",
                "Clinical study without phases but with schedules master file");
        theTable.put("M08", "Test/observation (Numeric) master file");
        theTable.put("M09", "Test/Observation (Categorical) master file");
        theTable.put("M10", "Test /observation batteries master file");
        theTable.put("M11", "Test/calculated observations master file");
        theTable.put("O01", "Order message (also RDE, RDS, RGV, RAS)");
        theTable.put("O02", "Order response (also RRE, RRD, RRG, RRA)");
        theTable.put("P01", "Add patient accounts");
        theTable.put("P02", "Purge patient accounts");
        theTable.put("P03", "Post detail financial transaction");
        theTable.put("P04", "Generate bill and A/R statements");
        theTable.put("P05", "Update account");
        theTable.put("P06", "End account");
        theTable.put("P07",
                "Unsolicited initial individual product experience report");
        theTable.put("P08",
                "Unsolicited update individual product experience report");
        theTable.put("P09", "Summary product experience report");
        theTable.put("PC1", "PC/ Problem Add");
        theTable.put("PC2", "PC/ Problem Update");
        theTable.put("PC3", "PC/ Problem Delete");
        theTable.put("PC4", "PC/ Problem Query");
        theTable.put("PC5", "PC/ Problem Response");
        theTable.put("PC6", "PC/ Goal Add");
        theTable.put("PC7", "PC/ Goal Update");
        theTable.put("PC8", "PC/ Goal Delete");
        theTable.put("PC9", "PC/ Goal Query");
        theTable.put("PCA", "PC/ Goal Response");
        theTable.put("PCB", "PC/ Pathway (Problem-Oriented) Add");
        theTable.put("PCC", "PC/ Pathway (Problem-Oriented) Update");
        theTable.put("PCD", "PC/ Pathway (Problem-Oriented) Delete");
        theTable.put("PCE", "PC/ Pathway (Problem-Oriented) Query");
        theTable.put("PCF", "PC/ Pathway (Problem-Oriented) Query Response");
        theTable.put("PCG", "PC/ Pathway (Goal-Oriented) Add");
        theTable.put("PCH", "PC/ Pathway (Goal-Oriented) Update");
        theTable.put("PCJ", "PC/ Pathway (Goal-Oriented) Delete");
        theTable.put("PCK", "PC/ Pathway (Goal-Oriented) Query");
        theTable.put("PCL", "PC/ Pathway (Goal-Oriented) Query Response");
        theTable.put("Q01", "Query sent for immediate response");
        theTable.put("Q02", "Query sent for deferred response");
        theTable.put("Q03", "Deferred response to a query");
        theTable.put("Q05", "Unsolicited display update message");
        theTable.put("Q06", "Query for order status");
        theTable.put("R01",
                "Unsolicited transmission of an observation message");
        theTable.put("R02", "Query for results of observation");
        theTable
                .put(
                        "R03",
                        "QRY/DSR Display-oriented results, query/unsol. update (for backward compatibility only)");
        theTable.put("R04",
                "Response to query; transmission of requested observation");
        theTable.put("R05", "QRY/DSR-query for display results");
        theTable.put("R06", "UDM-unsolicited update/display results");
        theTable.put("RAR",
                "Pharmacy administration information query response");
        theTable.put("RDR", "Pharmacy dispense information query response");
        theTable
                .put("RER", "Pharmacy encoded order information query response");
        theTable.put("RGR", "Pharmacy dose information query response");
        theTable.put("ROR", "Pharmacy prescription order query response");
        theTable.put("S01", "Request new appointment booking");
        theTable.put("S02", "Request appointment rescheduling");
        theTable.put("S03", "Request appointment modification");
        theTable.put("S04", "Request appointment cancellation");
        theTable.put("S05", "Request appointment discontinuation");
        theTable.put("S06", "Request appointment deletion");
        theTable.put("S07",
                "Request addition of service/resource on appointment");
        theTable.put("S08",
                "Request modification of service/resource on appointment");
        theTable.put("S09",
                "Request cancellation of service/resource on appointment");
        theTable.put("S10",
                "Request discontinuation of service/resource on appointment");
        theTable.put("S11",
                "Request deletion of service/resource on appointment");
        theTable.put("S12", "Notification of new appointment booking");
        theTable.put("S13", "Notification of appointment rescheduling");
        theTable.put("S14", "Notification of appointment modification");
        theTable.put("S15", "Notification of appointment cancellation");
        theTable.put("S16", "Notification of appointment discontinuation");
        theTable.put("S17", "Notification of appointment deletion");
        theTable.put("S18",
                "Notification of addition of service/resource on appointment");
        theTable
                .put("S19",
                        "Notification of modification of service/resource on appointment");
        theTable
                .put("S20",
                        "Notification of cancellation of service/resource on appointment");
        theTable
                .put("S21",
                        "Notification of discontinuation of service/resource on appointment");
        theTable.put("S22",
                "Notification of deletion of service/resource on appointment");
        theTable.put("S23", "Notification of blocked schedule time slot(s)");
        theTable.put("S24",
                "Notification of opened (unblocked) schedule time slot(s)");
        theTable.put("S25", "Schedule query message and response");
        theTable
                .put("S26",
                        "Notification that patient did not show up for schedule appointment");
        theTable.put("T01", "Original document notification");
        theTable.put("T02", "Original document notification and content");
        theTable.put("T03", "Document status change notification");
        theTable.put("T04", "Document status change notification and content");
        theTable.put("T05", "Document addendum notification");
        theTable.put("T06", "Document addendum notification and content");
        theTable.put("T07", "Document edit notification");
        theTable.put("T08", "Document edit notification and content");
        theTable.put("T09", "Document replacement notification");
        theTable.put("T10", "Document replacement notification and content");
        theTable.put("T11", "Document cancel notification");
        theTable.put("T12", "Document query");
        theTable.put("V01", "Query for vaccination record");
        theTable.put("V02",
                "Response to vaccination query returning multiple PID matches");
        theTable.put("V03", "Vaccination record response");
        theTable.put("V04", "Unsolicited vaccination record update");
        theTable
                .put("W01",
                        "Waveform result, unsolicited transmission of requested information");
        theTable.put("W02", "Waveform result, response to query");
        theTable.put("X01", "Product experience");
    }

    public static String getText(String code) {
        return theTable.get(code);
    }
}
