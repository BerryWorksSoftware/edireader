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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ReplyGenerator
{
  protected StandardReader standardReader;

  protected String controlDateAndTimeOverride;

  protected DateFormat yymmdd;

  protected DateFormat yyyymmdd;

  protected DateFormat hhmm;

  public abstract void generateAcknowledgementWrapup() throws IOException;

  public abstract void generateTransactionAcknowledgment(String documentType,
                                                         String control) throws IOException;

  public abstract void generateAcknowledgmentHeader(String firstSegment,
                                                    String groupSender, String groupReceiver, int i,
                                                    String groupVersion, String groupFunctionCode,
                                                    String groupControlNumber) throws IOException;

  public abstract void generateAcknowledgmentHeader(String syntaxIdentifier,
                                                    String syntaxVersionNumber, String fromId, String fromQual,
                                                    String toId, String toQual, String interchangeControlNumber) throws IOException;

  public abstract void generateGroupAcknowledgmentTrailer(int docCount)
    throws IOException;

  public abstract void generateNegativeACK() throws IOException;

  /**
   * Set an override value to be used whenever generating a control date and
   * time. This method is used for automated testing.
   *
   * @param overrideValue to be used in lieu of current date and time
   */
  public void setControlDateAndTime(String overrideValue)
  {
    controlDateAndTimeOverride = overrideValue;
  }

  public String controlDateAndTime(int dateLength)
  {
    return controlDateAndTime(dateLength, standardReader.getDelimiter());
  }

  public String controlDateAndTime(int dateLength, char delimiter)
  {
    if (controlDateAndTimeOverride != null)
      return controlDateAndTimeOverride;

    // Do lazy initializations if needed
    if (yymmdd == null)
      yymmdd = new SimpleDateFormat("yyMMdd");
    if (yyyymmdd == null)
      yyyymmdd = new SimpleDateFormat("yyyyMMdd");
    if (hhmm == null)
      hhmm = new SimpleDateFormat("HHmm");

    Date now = new Date();
    DateFormat sixOrEight = (dateLength == 6) ? yymmdd : yyyymmdd;
    return sixOrEight.format(now) + delimiter + hhmm.format(now);
  }

}