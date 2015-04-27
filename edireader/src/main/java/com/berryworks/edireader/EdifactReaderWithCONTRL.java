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

package com.berryworks.edireader;

import org.xml.sax.SAXException;

import java.io.IOException;

public class EdifactReaderWithCONTRL extends EdifactReader
{

  @Override
  protected void startInterchange(EDIAttributes attributes)
    throws SAXException
  {
    startElement(getXMLTags().getInterchangeTag(), attributes);
    getEdifactCONTRLGenerator().generateAcknowledgmentHeader(attributes);
  }

  @Override
  protected void endInterchange() throws SAXException
  {
    endElement(getXMLTags().getInterchangeTag());
    try
    {
      getAckGenerator().generateAcknowledgementWrapup();
    } catch (IOException e)
    {
      throw new SAXException(e);
    }
  }

  @Override
  protected void startSenderAddress(EDIAttributes attributes)
    throws SAXException
  {
    startElement(getXMLTags().getAddressTag(), attributes);
    getEdifactCONTRLGenerator().setSender(attributes);
  }

  @Override
  protected void startReceiverAddress(EDIAttributes attributes)
    throws SAXException
  {
    startElement(getXMLTags().getAddressTag(), attributes);
    getEdifactCONTRLGenerator().setReceiver(attributes);
  }

  @Override
  protected void startMessage(EDIAttributes attributes) throws SAXException
  {
    startElement(getXMLTags().getDocumentTag(), attributes);
    try
    {
      getEdifactCONTRLGenerator().generateTransactionAcknowledgment(
        attributes);
    } catch (IOException e)
    {
      throw new SAXException(e);
    }
  }

  protected EdifactCONTRLGenerator getEdifactCONTRLGenerator()
  {
    return (EdifactCONTRLGenerator) getAckGenerator();
  }

  @Override
  public ReplyGenerator getAckGenerator()
  {
    if (super.getAckGenerator() == null)
      setAckGenerator(new EdifactCONTRLGenerator(this, getAckStream()));

    return super.getAckGenerator();
  }

}
