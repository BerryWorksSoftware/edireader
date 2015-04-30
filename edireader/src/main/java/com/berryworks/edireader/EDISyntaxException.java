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

import com.berryworks.edireader.tokenizer.Tokenizer;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * An exception thrown during EDI parsing when invalid EDI syntax, structure,
 * or content is encountered.
 */
public class EDISyntaxException extends SAXException
{

  private int errorSegmentNumber;

  private int errorElementNumber;

  public EDISyntaxException(String desc)
  {
    super(desc);
  }

  public EDISyntaxException(String desc, int seg)
  {
    super(desc + " at segment " + seg);
    errorSegmentNumber = seg;
  }

  public EDISyntaxException(String desc, Tokenizer tokenizer)
  {
    super(desc + " at segment " + tokenizer.getSegmentCount() + ", field "
      + tokenizer.getElementInSegmentCount());
    errorSegmentNumber = tokenizer.getSegmentCount();
    errorElementNumber = tokenizer.getElementInSegmentCount();
  }

  public EDISyntaxException(String desc, String expected, String actual,
                            Tokenizer tokenizer)
  {
    this(desc + ". Expected " + expected + " instead of " + actual,
      tokenizer);
  }

  public EDISyntaxException(String desc, int expected, int actual, Tokenizer tokenizer)
  {
    this(desc + ". Expected " + expected + " instead of " + actual,
      tokenizer);
  }

  public EDISyntaxException(String desc, IOException e)
  {
    this(desc);
  }

  public int getErrorElementNumber()
  {
    return errorElementNumber;
  }

  public int getErrorSegmentNumber()
  {
    return errorSegmentNumber;
  }

}