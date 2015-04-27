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

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * This class implements a playback facility so that
 * a stream of SAXObjects can be used to make calls to
 * an arbitrary SAX ContentHandler.
 */
public class SAXObjectReader implements XMLReader {

    private ContentHandler contentHandler;
    private ObjectInputStream inputStream;

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler() {
        return contentHandler;
    }

    public void parse(InputSource input) throws IOException, SAXException {
        /**
         * Parse may be called multiple times, so establish the inputStream
         * only on the first call. Thereafter, just read further into the inputStream
         * used on the previous call.
         */
        if (inputStream == null) {
            System.err.println(getClass().getName() + " parsing first time on input stream");
            inputStream = new ObjectInputStream(input.getByteStream());
        } else
            System.err.println(getClass().getName() + " parsing again on the same input stream");

        try {
            while (true) {
                Object o = inputStream.readObject();
                if (o == null) {
                    System.err.println("SAXObjectReader got null from readObject(), parse throwing EOFException");
                    throw new EOFException();
                } else {
//                    System.err.println("object of type " + o.getClass().getName());
                }
                SAXObject saxObject = (SAXObject) o;
                saxObject.saxCall(contentHandler);
                if (saxObject instanceof SAXEndDocument) {
                    System.err.println("SAXObjectReader noted the end document, parse returning");
                    break;
                } else if (saxObject instanceof SAXEndOfStreamMarker)
                    throw new EOFException();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return false;
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return null;
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
    }

    public void setEntityResolver(EntityResolver resolver) {
    }

    public EntityResolver getEntityResolver() {
        return null;
    }

    public void setDTDHandler(DTDHandler handler) {
    }

    public DTDHandler getDTDHandler() {
        return null;
    }

    public void setErrorHandler(ErrorHandler handler) {
    }

    public ErrorHandler getErrorHandler() {
        return null;
    }

    public void parse(String systemId) throws IOException, SAXException {
    }

    public static void main(String arg[]) {
        XMLReader reader = new SAXObjectReader();
        reader.setContentHandler(new DefaultHandler());
        try {
            reader.parse(new InputSource(new FileInputStream("queue.ser")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
