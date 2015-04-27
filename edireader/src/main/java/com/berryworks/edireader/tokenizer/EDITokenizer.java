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

package com.berryworks.edireader.tokenizer;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDISyntaxException;

import java.io.IOException;
import java.io.Reader;

/**
 * Interprets EDI input as a sequence of primitive syntactic tokens.
 * <p/>
 * As an EDI interchange is parsed, the parser uses an EDITokenizer to advance through the
 * input EDI stream one token at a time. A call to <code>nextToken()</code> causes the tokenizer to advance
 * past the next token and return a <code>Token</code> instance describing that token.
 * <p/>
 */
public class EDITokenizer extends AbstractTokenizer
{

  public static final int BUFFER_SIZE = 1000;
  private final char[] buffer = new char[BUFFER_SIZE];
  private int bufferUsed;
  private int bufferIndex;

  public EDITokenizer(Reader source)
  {
    super(source);
    if (EDIReader.debug)
      trace("Constructed a new EDITokenizer");
  }

  public EDITokenizer(Reader source, char[] preRead)
  {
    this(source);
    if (preRead == null || preRead.length == 0)
      return;
    if (preRead.length > buffer.length)
      throw new RuntimeException("Attempt to create EDITokenizer with " + preRead.length +
        " pre-read chars, which is greater than the internal buffer size of " + buffer.length);
    System.arraycopy(preRead, 0, buffer, 0, preRead.length);
    bufferUsed = preRead.length;
  }

  /**
   * Returns a String representation of the current state of the tokenizer
   * for testing and debugging purposes.
   *
   * @return String representation
   */
  @Override
  public String toString()
  {
    String result = "tokenizer state:";
    result += " segmentCount=" + segmentCount;
    result += " charCount=" + charCount;
    result += " segTokenCount=" + segTokenCount;
    result += " segCharCount=" + segCharCount;
    result += " currentToken=" + currentToken;
    result += " bufferUsed=" + bufferUsed;
    result += " bufferIndex=" + bufferIndex;
    return result;
  }

  public char[] rawBuffer()
  {
    return buffer;
  }

  /**
   * Gets the next character of input. <pr>Sets cChar, cClass
   *
   * @throws java.io.IOException for problem reading EDI data
   */
  public void getChar() throws IOException
  {
    if (unGot)
    {
      // The current character has been "put back" with ungetChar()
      // after having been seen with getChar(). Therefore, this call
      // to getChar() can simply reget the current character.
      unGot = false;
      charCount++;
      segCharCount++;
      return;
    }

    // Read a fresh character from the input source.
    // But first copy the current one to an outputWriter
    // or the recorder if necessary.
    if (outputWriter != null)
    {
      // We do have an outputWriter wanting data, but do we have
      // a current character to write? And make sure writing is
      // not suspended.
      if ((!endOfFile) && (!writingSuspended))
        outputWriter.write(cChar);
    }
    if (recorderOn)
      recording.append(cChar);

    if (bufferIndex >= bufferUsed)
    {
      // It's time to refill the buffer
      while ((bufferUsed = inputReader.read(buffer)) == 0)
      {
        if (EDIReader.debug)
          trace("read returned zero");
      }
      if (EDIReader.debug)
        trace("read " + bufferUsed + " chars of input into buffer");
      bufferIndex = 0;
      if (bufferUsed < 0)
        endOfFile = true;
    }

    if (endOfFile)
    {
      cClass = CharacterClass.EOF;
      if (EDIReader.debug)
        trace("end-of-file encountered");
    }
    else
    {
      cChar = buffer[bufferIndex++];
      if (cChar == delimiter)
        cClass = CharacterClass.DELIMITER;
      else if (cChar == subDelimiter)
        cClass = CharacterClass.SUB_DELIMITER;
      else if (cChar == release)
        cClass = CharacterClass.RELEASE;
      else if (cChar == terminator)
        cClass = CharacterClass.TERMINATOR;
      else if (cChar == repetitionSeparator)
        cClass = CharacterClass.REPEAT_DELIMITER;
      else
        cClass = CharacterClass.DATA;
    }
    charCount++;
    segCharCount++;
  }

  public char[] getBuffered()
  {
    int bufferIndexCopy = (unGot && bufferIndex > 0) ? bufferIndex - 1 : bufferIndex;

    if (bufferIndexCopy >= bufferUsed)
      return new char[0];

    int n = bufferUsed - bufferIndexCopy;
    char[] result = new char[n];
    System.arraycopy(buffer, bufferIndexCopy, result, 0, n);
    return result;
  }

  /**
   * Look ahead into the source of input chars and return the next n chars to
   * be seen, without disturbing the normal operation of getChar().
   *
   * @param n number of chars to return
   * @return char[] containing upcoming input chars
   * @throws java.io.IOException for problem reading EDI data
   * @throws com.berryworks.edireader.EDISyntaxException
   *
   */
  public char[] lookahead(int n) throws IOException, EDISyntaxException
  {
    if (EDIReader.debug)
      trace("EDITokenizer.lookahead(" + n + ")");

    char[] rval = new char[n];
    // The 1st char is grabbed using the tokenizer's built-in
    // getChar() / ungetChar() mechanism. This allows things to work
    // properly whether or not the next char has already been gotten.
    getChar();
    rval[0] = cChar;
    ungetChar();

    // The minus 1 is because we have already filled the first char of the return value, so we only need n-1 more
    if (bufferUsed < bufferIndex + n - 1)
    {
//            throw new EDISyntaxException("Internal Error: Too few buffered characters available for lookahead",
//                    String.valueOf(n) + " or more", String.valueOf(bufferUsed - bufferIndex), this);
      if (EDIReader.debug)
        trace("buffering more data to satisfy lookahead(" + n + ")");
      shiftBuffer();
      readUntilBufferProvidesAtLeast(n - 1);
    }

    // Move chars from the buffer into the return value,
    // up to the length of the buffer
    int j = 1;
    for (int i = bufferIndex; i < bufferIndex + n - 1; i++)
      rval[j++] = buffer[i];

    // If more lookahead chars were requested, fill
    // them with '?'.
    for (; j < n;) rval[j++] = '?';

    return rval;
  }

  private void shiftBuffer()
  {
    if (bufferIndex >= bufferUsed)
    {
      if (EDIReader.debug)
        trace("buffer does not need shifting");
      return;
    }
    else
    {
      if (EDIReader.debug)
        trace("shifting " + bufferUsed + " chars in buffer " + bufferIndex + " chars to the left");
    }

    for (int i = 0; i < bufferUsed - bufferIndex; i++)
    {
      buffer[i] = buffer[i + bufferIndex];
    }

    bufferUsed -= bufferIndex;
    if (EDIReader.debug) trace("after shifting, buffer contains " + bufferUsed + " chars of data");
    bufferIndex = 0;
  }

  private void readUntilBufferProvidesAtLeast(int n) throws IOException
  {

    while (n > bufferUsed - bufferIndex)
    {
      int newChars;
      while ((newChars = inputReader.read(buffer, bufferUsed, BUFFER_SIZE - bufferUsed)) == 0)
      {
        if (EDIReader.debug) trace("read returned zero in readUntil...");
      }
      if (EDIReader.debug) trace("readUntil... got " + newChars + " chars of input into buffer");
      if (newChars < 0)
      {
        if (EDIReader.debug) trace("hit end of file in readUntil...");
        endOfFile = true;
        return;
      }
      bufferUsed += newChars;
    }
    if (EDIReader.debug)
      trace("returning from readUntil... with buffer containing " + bufferUsed + " chars");
  }


}