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

import org.xml.sax.Parser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;

/**
 * Wraps an EDIReader.
 * <p/>
 * EDIParser is necessary to satisfy the JAXP conventions
 * for dynamic selection and loading of a specific XML parser into
 * an XML application. By following this convention,
 * it is possible to integrate EDIReader (and therefore EDI data)
 * into various XML applications without any changes to those applications.
 */
public class EDIParser extends SAXParser
{

  protected EDIReader ediReader;


  /**
   * Construct an EDIParser object.
   * <p/>
   * This EDIParser provides for delayed format recognition,
   * where the actual subclass of EDIReader is not created until
   * the parse method is first called. This mechanism is in support
   * of the JAXP interfaces.
   */
  public EDIParser()
  {
  }


  /**
   * Construct an EDIParser object.
   *
   * @param ediReader wrapped within this EDIParser
   */
  public EDIParser(EDIReader ediReader)
  {
    this.ediReader = ediReader;
  }


  /**
   * Get the XMLReader attribute of the EDIParser.
   *
   * @return XMLReader the EDIReader wrapped within this EDIParser
   */
  @Override
  public XMLReader getXMLReader()
  {
    if (ediReader == null)
    {
      ediReader = new EDIReader();
    }
    return ediReader;
  }


  /**
   * Get the parser attribute of the EDIParser.
   *
   * @return null this deprecated method is only for SAX1 compatibility
   */
  @Override
  public Parser getParser()
  {
    return null;
  }


  /**
   * Get the validating attribute of the EDIParser.
   *
   * @return The validating value
   */
  @Override
  public boolean isValidating()
  {
    return false;
  }


  /**
   * Get the namespaceAware attribute of the EDIParser object
   *
   * @return The namespaceAware value
   */
  @Override
  public boolean isNamespaceAware()
  {
    return true;
  }


  /**
   * Sets the property attribute of the EDIReader.
   *
   * @param name  The new property value
   * @param value The new property value
   * @throws SAXNotRecognizedException for SAX compatibility
   * @throws SAXNotSupportedException  for SAX compatibility
   */
  @Override
  public void setProperty(String name, Object value)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    throw new SAXNotSupportedException("Not implemented");
  }


  /**
   * Get the property attribute of the EDIReader.
   *
   * @param name Description of the Parameter
   * @return The property value
   * @throws SAXNotRecognizedException for SAX compatibility
   * @throws SAXNotSupportedException  for SAX compatibility
   */
  @Override
  public Object getProperty(String name)
    throws SAXNotRecognizedException, SAXNotSupportedException
  {
    throw new SAXNotSupportedException("Not implemented");
  }

}

