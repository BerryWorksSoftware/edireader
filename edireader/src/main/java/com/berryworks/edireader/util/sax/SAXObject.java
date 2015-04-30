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

package com.berryworks.edireader.util.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.Serializable;

/**
 * This class encapsulates the primary attributes of a SAX token
 * into a serializable object.
 */
public abstract class SAXObject implements Serializable {
  protected final String uri;
  protected final String localName;
  protected final String qName;

  public SAXObject() {
    this.localName = null;
    this.qName = null;
    this.uri = null;
  }

  public SAXObject(String uri, String localName, String qName) {
    this.localName = localName;
    this.qName = qName;
    this.uri = uri;
  }

  public String getqName() {
    return qName;
  }

  public String getUri() {
    return uri;
  }

  public String getLocalName() {
    return localName;
  }

  public abstract void saxCall(ContentHandler contentHandler) throws SAXException;
}
