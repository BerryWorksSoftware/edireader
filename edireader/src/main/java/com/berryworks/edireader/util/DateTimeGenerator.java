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

package com.berryworks.edireader.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represent the current date and time in the format used by
 * ANSI X12 and EDIFACT EDI standards.
 */
public class DateTimeGenerator
{

  private static final ThreadLocal<DateFormat> yymmdd = new ThreadLocal<DateFormat>()
  {
    @Override
    protected DateFormat initialValue()
    {
      return new SimpleDateFormat("yyMMdd");
    }
  };
  private static final ThreadLocal<DateFormat> hhmm = new ThreadLocal<DateFormat>()
  {
    @Override
    protected DateFormat initialValue()
    {
      return new SimpleDateFormat("HHmm");
    }
  };

  private static String dateTestValue = null;
  private static String timeTestValue = null;

  /**
   * Generate the date and time in an yyMMdd<delimiter>HHmm format.
   *
   * @param delimiter
   * @return String representation of date and time
   */
  public static String generate(char delimiter)
  {
    if (dateTestValue == null || timeTestValue == null)
    {
      Date now = new Date();
      return yymmdd.get().format(now) + delimiter + hhmm.get().format(now);
    }
    else
    {
      return dateTestValue + delimiter + timeTestValue;
    }
  }

  /**
   * Inject particular date and time values to use instead of
   * the current date and time.
   * <p/>
   * This can be very useful in testing as a mean of allowing expected
   * results that are full repeatable.
   *
   * @param date
   * @param time
   */
  public static void setTestValues(String date, String time)
  {
    dateTestValue = date;
    timeTestValue = time;
  }
}
