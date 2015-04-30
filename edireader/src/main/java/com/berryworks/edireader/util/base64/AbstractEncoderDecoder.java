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

import java.nio.charset.Charset;

/**
 * This class establishes a base for common inheritance among the classes
 * that cooperate to provide base 64 encoding and decoding.
 */
public abstract class AbstractEncoderDecoder
{
  protected static final Charset charset = Charset.forName("8859_1");

  static final byte STATE62 = 0;
  static final byte STATE44 = 1;
  static final byte STATE26 = 2;
  static final byte STATE24 = 3;
  static final byte STATE42 = 4;
  static final byte STATE66 = 5;

  private static final byte INITIAL_STATE = STATE62;
  int state = INITIAL_STATE;

  public abstract void consume(byte b);

  protected abstract void emit(byte b);

  protected abstract void endOfData();

  void reset()
  {
    state = INITIAL_STATE;
  }
}
