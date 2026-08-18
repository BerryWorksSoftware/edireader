/*
 * Copyright 2005-2026 by BerryWorks Software, LLC. All rights reserved.
 */
package com.berryworks.edireader.hl7;

import com.berryworks.edireader.ReplyGenerator;
import com.berryworks.edireader.StandardReader;

import java.io.IOException;
import java.io.Writer;

/**
 * A delegate for generating an interchange containing some number of 997
 * transactions acknowledging the functional groups parsed by AnsiReader.
 */
public class HL7AckGenerator extends ReplyGenerator
{

  private final Writer ackStream;
  private boolean preambleGenerated, skipFA;

  private char delimiter;
  private String segmentTerminator;
  private String controlNumber;

  public HL7AckGenerator(final StandardReader reader, final Writer ackStream)
  {
    this.standardReader = reader;
    this.ackStream = ackStream;
  }

  @Override
  public void generateAcknowledgmentHeader(String syntaxIdentifier,
                                           String syntaxVersionNumber, String fromId, String fromQual,
                                           String toId, String toQual, String interchangeControlNumber) throws IOException
  {
    if ((ackStream == null) || skipFA)
    {
      return;
    }

    delimiter = standardReader.getDelimiter();
    char subDelimiter = standardReader.getSubDelimiter();
    char subSubDelimiter = standardReader.getSubSubDelimiter();
    char repetitionSeparator = standardReader.getRepetitionSeparator();
    char release = standardReader.getReleaseCharacter();
    segmentTerminator = standardReader.getTerminator() + standardReader.getTerminatorSuffix();
    controlNumber = interchangeControlNumber;
    ackStream.write("MSH" + delimiter + subDelimiter + repetitionSeparator + release
      + subSubDelimiter + delimiter + interchangeControlNumber + delimiter);
    ackStream.write(segmentTerminator);
  }

  @Override
  public void generateTransactionAcknowledgment(String transactionCode,
                                                String ignoredControlNumber) throws IOException
  {
//    if (EDIReader.debug) System.err.println("...generateTransactionAcknowledgment");
    if ((ackStream == null) || skipFA)
    {
      return;
    }

    ackStream.write("MSA" + delimiter + "AA" + delimiter + controlNumber + delimiter);
    ackStream.write(segmentTerminator);

  }

  @Override
  public void generateGroupAcknowledgmentTrailer(int docCount)
    throws IOException
  {
//    if (EDIReader.debug) System.err.println("...generateGroupAckTrailer");
    if ((ackStream == null) || skipFA)
    {
      return;
    }

    // Generate the AK9 segment to designate acceptance of the entire
    // functional group
    ackStream.write("AK9" + delimiter + "A" + delimiter + docCount
      + delimiter + docCount + delimiter + docCount);
    ackStream.write(segmentTerminator);

    // Generate the SE to match the ST
    ackStream
      .write("SE" + delimiter + (4 + 2 * docCount) + delimiter + "1");
    ackStream.write(segmentTerminator);

  }

  @Override
  public void generateNegativeACK()
  {
  }

  @Override
  public void generateAcknowledgementWrapup() throws IOException
  {
//    if (EDIReader.debug) System.err.println("...generateAcknowledgementWrapup");
    if (ackStream == null)
    {
      return;
    }
    ackStream.flush();

  }

  private void generateAcknowledgementPreamble(String firstSegment,
                                               String groupSender, String groupReceiver, int groupDateLength,
                                               String groupVersion)
  {
//    if (EDIReader.debug) System.err.println("...generateAcknowledgementPreamble");
    if (ackStream == null)
    {
      return;
    }
    if (preambleGenerated)
    {
      return;
    }

    // Note that the initialization of the following items cannot occur
    // in the constructor because ansiReader may not have all of the
    // necessary information at that point.
    delimiter = standardReader.getDelimiter();
    if (delimiter != '|') throw new RuntimeException("wrong delimiter");
    char terminator = standardReader.getTerminator();
    String terminatorSuffix = standardReader.getTerminatorSuffix();
    segmentTerminator = terminator + terminatorSuffix;

    preambleGenerated = true;
  }

  @Override
  public void generateAcknowledgmentHeader(String firstSegment,
                                           String groupSender, String groupReceiver, int groupDateLength,
                                           String groupVersion, String groupFunctionCode,
                                           String groupControlNumber) throws IOException
  {
    throw new RuntimeException("not implemented for HL7");
  }


}
