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

package com.berryworks.edireader.util;

import com.berryworks.edireader.util.base64.AbstractEncoder;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * Encode char data using base-64 conventions
 * before presenting that data to a SAX ContentHandler.
 */
public class ContentHandlerBase64Encoder extends AbstractEncoder
{

  private ContentHandler contentHandler;
  private ByteBuffer base64Bytes;

  public void encode(char[] dataObject, ContentHandler contentHandler)
  {
    this.contentHandler = contentHandler;
    CharBuffer charBuffer = CharBuffer.wrap(dataObject);

    // Allocate a modest sized non-direct ByteBuffer to receive the
    // bytes as they are encoded from the chars
    ByteBuffer byteBuffer = ByteBuffer.allocate(100);

    // Allocate a similar ByteBuffer to receive the bytes as they are emitted
    // by the Base 64 encoder.
    base64Bytes = ByteBuffer.allocate(100);

    // Use an encoder repeatedly until all of the chars have been encoded as bytes
    // and presented as input for base 64 encoding.
    CharsetEncoder encoder = charset.newEncoder();
    while (true)
    {
      CoderResult coderResult = encoder.encode(charBuffer, byteBuffer, true);
      if (coderResult.isError())
        throw new RuntimeException("Unrecoverable failure in Base64 encoding");
      byteBuffer.flip();
      while (byteBuffer.hasRemaining())
        consume(byteBuffer.get());
      if (coderResult.isUnderflow()) break;
      byteBuffer.clear();
    }
    endOfData();
    feedContentHandler();
  }

  @Override
  protected void emit(byte b)
  {

    if (!base64Bytes.hasRemaining())
    {
      // Whenever the ByteBuffer gets full, use the CharSetEncoder
      // to turn the bytes into chars in a CharBuffer.
      // Whenever the CharBuffer gets full, give those chars
      // to the SAX ContentHandler.
      feedContentHandler();
      base64Bytes.clear();
    }
    // Put the byte into a byteBuffer.
    base64Bytes.put(b);
  }

  private void feedContentHandler()
  {
    base64Bytes.flip();
    CharBuffer charBuffer = charset.decode(base64Bytes);
    try
    {
      contentHandler.characters(charBuffer.array(), 0, charBuffer.length());
    } catch (SAXException e)
    {
      throw new RuntimeException("Unrecoverable failure in Base64 encoding");
    }
  }

}
