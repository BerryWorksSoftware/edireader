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

/**
 * Represents the full collection of syntax characters that apply to EDI interchanges.
 * <p/>
 * An instance of a SyntaxDescriptor is typically used to describe the particular syntax
 * characters used in a given EDI interchange.
 * The <bold>delimiter</bold> is the character that separates two consecutive fields in an EDI segment.
 * The <bold>subdelimiter</bold> is the character that separates two consecutive subfields.
 * The <bold>subSubDelimiter</bold> the the character that separates two consecutive sub-subfields.
 * (ANSI X12 and EDIFACT standards to no use sub-subfields. This is included for other standards
 * such as HL7.)
 * The <bold>decimalMark</bold> is the character used in numeric EDI data to represent the radix point.
 * The decimal mark is the often the period but can be a comma or other value.
 * The <bold>repetitionSeparator</bold> is the character used to separate consecutive instances of
 * field that is allowed to repeat.
 * The <bold>terminator</bold> is the character used to mark the end of a segment.
 * The <bold>terminatorSuffix</bold> is a short String of characters that optionally follow the
 * terminator. Such suffixes are typically a LF or CR,LF sequence marking the end of a record.
 * The <bold>release</bold> character is the used to mark the immediately following character as
 * data and not to be interpreted as a syntax characters.
 */
public class SyntaxDescriptor
{
  private char delimiter;
  private char subDelimiter;
  private char subSubDelimiter;
  private char decimalMark;
  private char repetitionSeparator;
  private char terminator;
  private int release;
  private String terminatorSuffix;

  public SyntaxDescriptor()
  {
  }

  public char getDelimiter()
  {
    return delimiter;
  }

  public void setDelimiter(char delimiter)
  {
    this.delimiter = delimiter;
  }

  public char getSubDelimiter()
  {
    return subDelimiter;
  }

  public void setSubDelimiter(char subDelimiter)
  {
    this.subDelimiter = subDelimiter;
  }

  public char getSubSubDelimiter()
  {
    return subSubDelimiter;
  }

  public void setSubSubDelimiter(char subSubDelimiter)
  {
    this.subSubDelimiter = subSubDelimiter;
  }

  public char getDecimalMark()
  {
    return decimalMark;
  }

  public void setDecimalMark(char decimalMark)
  {
    this.decimalMark = decimalMark;
  }

  public char getRepetitionSeparator()
  {
    return repetitionSeparator;
  }

  public void setRepetitionSeparator(char repetitionSeparator)
  {
    this.repetitionSeparator = repetitionSeparator;
  }

  public char getTerminator()
  {
    return terminator;
  }

  public void setTerminator(char terminator)
  {
    this.terminator = terminator;
  }

  public int getRelease()
  {
    return release;
  }

  public void setRelease(int release)
  {
    this.release = release;
  }

  public String getTerminatorSuffix()
  {
    return terminatorSuffix;
  }

  public void setTerminatorSuffix(String terminatorSuffix)
  {
    this.terminatorSuffix = terminatorSuffix;
  }
}
