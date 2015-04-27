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

public class XmlFormatter
{
  private final static String SEPARATOR = System.getProperty("line.separator");

  private final static String INDENT = "    ";
  private final static int INDENT_LENGTH = 4;

  public String format(String xmlText)
  {

    String indent = "";

    int startLookingAtIndex = 0;

    while (true)
    {

      int i = xmlText.indexOf("><", startLookingAtIndex);
      if (i < 0)
      {
        break;
      }

      char peekAhead = xmlText.charAt(i + 2);

      int delta = changeIndent(xmlText, i, peekAhead);
      if (delta < 0)
      {
        switch (indent.length())
        {
          case 0:
            break;
          case INDENT_LENGTH:
            indent = "";
            break;
          default:
            indent = indent.substring(INDENT.length());
        }
      }
      else if (delta > 0)
      {
        indent += INDENT;
      }

      String insertedWhitespace = SEPARATOR + indent;
      xmlText = xmlText.substring(0, i + 1) + insertedWhitespace + xmlText.substring(i + 1);

      startLookingAtIndex = i + insertedWhitespace.length() + 1;

    }

    return xmlText;
  }

  private int changeIndent(String xmlText, int startingIndex, char peekAhead)
  {

    int index = startingIndex;

    while (--index > 0)
    {
      char c = xmlText.charAt(index);

      switch (c)
      {
        case '<':
          return 1;
        case '?':
          return 0;
        case '/':
          if (index + 1 == startingIndex)
          {
            return -1;
          }
          else
          {
            return peekAhead == '/' ? -1 : 0;
          }
      }

    }

    return 0;
  }

  private char priorNonWhitespace(String xmlText, int index)
  {

    while (true)
    {

      if (--index <= 0)
        return xmlText.charAt(0);

      char c = xmlText.charAt(index);
      if (!Character.isWhitespace(c))
        return c;
    }
  }

  private boolean isClosing(String xmlText, int index)
  {

    while (--index > 0)
    {
      char c = xmlText.charAt(index);

      switch (c)
      {
        case '<':
          return false;
        case '?':
        case '/':
          return true;
      }

    }

    return false;
  }
}
