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

package com.berryworks.edireader.benchmark;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.jquantify.EventCounter;
import com.berryworks.jquantify.SessionCounter;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;

public class AnsiUnitOfWork implements BenchmarkUnitOfWork
{

  private SessionCounter sessionCounter;

  private EventCounter charCounter;

  private int interchangeKCs;

  private int charCount;

  public void run()
  {
    if (sessionCounter == null)
      throw new RuntimeException("SessionCounter net set");

    PipedReader pipedReader;
    PipedWriter pipedWriter = new PipedWriter();
    try
    {
      pipedReader = new PipedReader(pipedWriter);
      generateContent(pipedWriter);
      InputSource inputSource = new InputSource(pipedReader);
      EDIReader reader = EDIReaderFactory.createEDIReader(inputSource);
      reader.setContentHandler(new DefaultHandler());
      reader.parse(inputSource);
      charCount = reader.getTokenizer().getCharCount();
      charCounter.add(charCount);
    } catch (Exception e)
    {
      throw new RuntimeException("caught exception", e);
    }
    sessionCounter.stop();
  }

  private void generateContent(Writer writer)
  {
    EDITestData generator = new EDITestData();
    generator.setWriter(writer);
    generator.setInterchangeKCs(interchangeKCs);
    new Thread(generator).start();
  }

  public int getCharCount()
  {
    return charCount;
  }

  public SessionCounter getSessionCounter()
  {
    return sessionCounter;
  }

  public void setSessionCounter(SessionCounter sessionCounter)
  {
    this.sessionCounter = sessionCounter;
  }

  public EventCounter getEventCounter()
  {
    return charCounter;
  }

  public void setCharCounter(EventCounter charCounter)
  {
    this.charCounter = charCounter;
  }

  public void setInterchangeKCs(int kcs)
  {
    interchangeKCs = kcs;
  }


}
