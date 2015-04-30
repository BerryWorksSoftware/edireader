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

import java.nio.CharBuffer;

/**
 * This class is part of the internal implementation
 * of the base-64 encoding/decoding.
 */
public abstract class EncoderBackEnd extends AbstractEncoderDecoder
{

  private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
  private static final byte[] base64Alphabet = charset.encode(CharBuffer.wrap(ALPHABET)).array();
  private static final byte pad = base64Alphabet[64];

  private byte previous;

  @Override
  public void consume(byte b)
  {
    switch (state)
    {
      case STATE62:
        emit(base64Alphabet[(int) b]);
        state = STATE24;
        break;
      case STATE24:
        previous = b;
        state = STATE44;
        break;
      case STATE44:
        emit(base64Alphabet[(previous << 4 | b)]);
        state = STATE42;
        break;
      case STATE42:
        previous = b;
        state = STATE26;
        break;
      case STATE26:
        emit(base64Alphabet[(previous << 2 | b)]);
        state = STATE66;
        break;
      case STATE66:
        emit(base64Alphabet[(int) b]);
        state = STATE62;
        break;
    }
  }

  @Override
  public void endOfData()
  {
    switch (state)
    {
      case STATE62:
      case STATE24:
      case STATE42:
      case STATE66:
        break;
      case STATE44:
        emit(base64Alphabet[previous << 4]);
        emit(pad);
        emit(pad);
        break;
      case STATE26:
        emit(base64Alphabet[previous << 2]);
        emit(pad);
        break;
    }
  }

}