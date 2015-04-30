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

package com.berryworks.edireader.util.base64;

/**
 * This class is part of the internal implementation
 * of the base-64 encoding/decoding.
 */
public abstract class DecoderFrontEnd extends AbstractEncoderDecoder
{

  private static final int capitalA = 65;
  private static final int capitalZ = 90;
  private static final int lowerCaseA = 97;
  private static final int lowerCaseZ = 122;
  private static final int digit0 = 48;
  private static final int digit9 = 57;
  private static final int plus = 43;
  private static final int slash = 47;

  @Override
  public void consume(byte b)
  {

    /**
     * First map the A-Z, a-z. 0-9, +/ ASCII chars into
     * the corresponding 6-bit binary values.
     * If we encounter something other than one of these, just return.
     * Note that the "=" used as padding is also deliberately ignored.
     */
    int unsignedInt = b & 127;
    if (unsignedInt >= capitalA)
    {
      if (unsignedInt <= capitalZ) unsignedInt -= capitalA;
      else if (unsignedInt >= lowerCaseA)
      {
        if (unsignedInt <= lowerCaseZ) unsignedInt -= lowerCaseA - 26;
        else return;
      }
      else return;
    }
    else if (unsignedInt >= digit0)
      if (unsignedInt <= digit9) unsignedInt -= digit0 - 52;
      else return;
    else if (unsignedInt == plus) unsignedInt = 62;
    else if (unsignedInt == slash) unsignedInt = 63;
    else return;

    /**
     * Now emit this result as a single byte or as two bytes
     * depending upon what state we are in.
     */
    switch (state)
    {
      default:
      case STATE62:
        emit((byte) unsignedInt);
        state = STATE24;
        break;
      case STATE24:
        emit((byte) (unsignedInt >>> 4));
        emit((byte) (unsignedInt & 15));
        state = STATE42;
        break;
      case STATE42:
        emit((byte) (unsignedInt >>> 2));
        emit((byte) (unsignedInt & 3));
        state = STATE66;
        break;
      case STATE66:
        emit((byte) unsignedInt);
        state = STATE62;
        break;
    }
  }
}