/*
 * Copyright 2005-2026 by BerryWorks Software, LLC. All rights reserved.
 */
package com.berryworks.edireader.hl7;

import java.util.HashMap;
import java.util.Map;

/**
 * Table 0076 from HL7 Specifications
 * mapping Message Types to descriptions.
 */
public class Table76
{
  protected static final Map<String, String> theTable = new HashMap<>(150);

  static
  {
    theTable.put("ACK", "General acknowledgment message");
    theTable.put("ADR", "ADT response");
    theTable.put("ADT", "ADT message");
    theTable.put("ARD", "Ancillary RPT (display)");
    theTable.put("BAR", "Add/change billing account");
    theTable.put("CSU", "Unsolicited clinical study data");
    theTable.put("DFT", "Detail financial transaction");
    theTable.put("DSR", "Display response");
    theTable.put("EDR", "Enhanced display response");
    theTable.put("ERP", "Event replay response");
    theTable.put("ERQ", "Event replay query");
    theTable.put("EQQ", "Embedded query language query");
    theTable.put("MCF", "Delayed acknowledgment");
    theTable.put("MDM", "Documentation message");
    theTable.put("MFN", "Master files notification");
    theTable.put("MFK", "Master files application acknowledgement");
    theTable.put("MFD", "Master files delayed application acknowledgement");
    theTable.put("MFQ", "Master files query");
    theTable.put("MFR", "Master files query response");
    theTable.put("ORF", "Observ. result/record response");
    theTable.put("ORM", "Order message");
    theTable.put("ORR", "Order acknowledgement message");
    theTable.put("ORU", "Observ result/unsolicited");
    theTable.put("OSQ", "Order status query");
    theTable.put("OSR", "Order status response");
    theTable.put("QRY", "Query, original Mode");
    theTable.put("PEX", "Product experience");
    theTable.put("PGL", "Patient goal");
    theTable.put("PGR", "Patient goal response");
    theTable.put("PGQ", "Patient goal query");
    theTable.put("PIN", "Patient Insurance Information");
    theTable.put("PPG", "Patient pathway (goal-oriented)");
    theTable.put("PPP", "Patient pathway (problem-oriented)");
    theTable.put("PPR", "Patient problem");
    theTable.put("PPT", "Patient pathway (goal oriented)");
    theTable.put("PPV", "Patient goal response");
    theTable.put("PRQ", "Patient care problem query");
    theTable.put("PRR", "Patient problem response");
    theTable.put("PTQ", "Patient pathway (problem-oriented) query");
    theTable.put("PTR", "Patient pathway (problem-oriented) response");
    theTable.put("PTU", "Patient pathway (goal-oriented) query");
    theTable.put("PTV", "Patient pathway (goal-oriented) response");
    theTable.put("PIN", "Patient information");
    theTable.put("RAR", "Pharmacy administration information");
    theTable.put("RAS", "Pharmacy administration message");
    theTable.put("RCI", "Return clinical information");
    theTable.put("RCL", "Return clinical list");
    theTable.put("RDE", "Pharmacy encoded order message");
    theTable.put("RDR", "Pharmacy dispense information");
    theTable.put("RDS", "Pharmacy dispense message");
    theTable.put("RGV", "Pharmacy give message");
    theTable.put("RGR", "Pharmacy dose information");
    theTable.put("REF", "Patient referral");
    theTable.put("RER", "Pharmacy encoded order information");
    theTable.put("ROD", "Request patient demographics");
    theTable.put("ROR", "Pharmacy prescription order response");
    theTable.put("RPA", "Return patient authorization");
    theTable.put("RPI", "Return patient information");
    theTable.put("RPL", "Return patient display list");
    theTable.put("RPR", "Return patient list");
    theTable.put("RQA", "Request patient authorization");
    theTable.put("RQC", "Request clinical information");
    theTable.put("RQI", "Request patient information");
    theTable.put("RQP", "Request patient demographics");
    theTable.put("RRA", "Pharmacy administration acknowledgment");
    theTable.put("RRD", "Pharmacy dispense acknowledgment");
    theTable.put("RRE", "Pharmacy encoded order acknowledgment");
    theTable.put("RRG", "Pharmacy give acknowledgment");
    theTable.put("RRI", "Return patient referral");
    theTable.put("SIU", "Schedule information unsolicited");
    theTable.put("SPQ", "Stored procedure request");
    theTable.put("SQM", "Schedule query");
    theTable.put("SQR", "Schedule query response");
    theTable.put("CRM", "Clinical study registration");
    theTable.put("SRM", "Schedule request");
    theTable.put("SRR", "Scheduled request response");
    theTable.put("SUR", "Summary product experience report");
    theTable.put("TBR", "Tabular data response");
    theTable.put("UDM", "Unsolicited display message");
    theTable.put("VQQ", "Virtual table query");
    theTable.put("VXQ", "Query for vaccination record");
    theTable.put("VXX", "Vaccination query response with multiple PID matches");
    theTable.put("VXR", "Vaccination query record response");
    theTable.put("VXU", "Unsolicited vaccination record update");
  }

  protected static Table76 theInstance = new Table76();

  private Table76()
  {
  }

  public static String getText(String code)
  {
    return theTable.get(code);
  }
}

