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

/**
 * Provides utility methods for handling fixed length text and numeric fields.
 */
public abstract class FixedLength
{

  private static final String DEFAULT_SPACES = "                                                  ";
  private static final String DEFAULT_ZEROES = "00000000000000000000000000000000000000000000000000";

  /**
   * Express a positive int value as a fixed length sequence of digits.
   *
   * @param value
   * @param size
   * @return String value
   */
  public static String valueOf(int value, int size)
  {
    if (size > 12)
      throw new RuntimeException("FixedLength.valueOf support not available for sizes > 12 (" + size + " requested)");
    long n = 1000000000000L + value;
    String digits = String.valueOf(n);
    return digits.substring(digits.length() - size);
  }

  /**
   * Force a given String value to a fixed length
   * by truncating right-most characters or padding on the right with spaces.
   * <p/>
   * A null or empty String argument results in a String of spaces with the
   * fixed length.
   *
   * @param value
   * @param size
   * @return
   */
  public static String valueOf(String value, int size)
  {
    if (value == null)
      return spaces(size);
    int n = value.length();
    if (n == size)
      return value;
    else if (n > size)
      return value.substring(0, size);
    else
      return value + spaces(size - n);
  }

  /**
   * Return a String of a fixed length by adding zeros at the beginning
   * of a given String.
   * <p/>
   * If the original String is greater than the
   * fixed length, return a right-truncated copy of the String.
   *
   * @param value
   * @param size
   * @return String value
   */
  public static String leadingZeros(String value, int size)
  {
    if (value == null)
      return zeroes(size);
    int n = value.length();
    if (n == size)
      return value;
    else if (n > size)
      return value.substring(0, size);
    else
      return zeroes(size - n) + value;
  }

  /**
   * Return a String composed of a designated number of spaces.
   *
   * @param n - number of spaces
   * @return String value
   */
  public static String spaces(int n)
  {
    if (n > DEFAULT_SPACES.length())
      throw new RuntimeException("Unexpectedly large number of spaces to generate: " + n);
    return DEFAULT_SPACES.substring(0, n);
  }

  /**
   * Return a String composed of a designated number of zeros.
   *
   * @param n - number of zeros
   * @return String value
   */
  public static String zeroes(int n)
  {
    if (n > DEFAULT_ZEROES.length())
      throw new RuntimeException("Unexpectedly large number of zeroes to generate: " + n);
    return DEFAULT_ZEROES.substring(0, n);
  }

}
