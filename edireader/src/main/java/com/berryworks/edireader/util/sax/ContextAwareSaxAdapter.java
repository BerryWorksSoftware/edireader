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

package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIAttributes;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the SAX ContentHandler adapts to a modified API enhanced with
 * two new features: the characters of XML data content are made available when an element is
 * started, and the names of all the nested elements are available at any point.
 */
public abstract class ContextAwareSaxAdapter extends DefaultHandler {

    private boolean pending = false;
    private String pendingUri;
    private String pendingName;
    private EDIAttributes pendingAttributes;
    private String pendingData;
    private final List<String> context = new ArrayList<>();

    @Override
    public final void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (pending) {
            if (pendingData != null) pendingData = pendingData.trim();
            start(pendingUri, pendingName, pendingData, pendingAttributes);
            pending = false;
        }

        pendingUri = uri;
        pendingName = localName;
        pendingAttributes = new EDIAttributes(attributes);
        pendingData = null;
        pending = true;

        context.add(representationOf(localName, attributes));

    }

    protected String representationOf(String localName, Attributes attributes) {
        return localName;
    }

    @Override
    public final void endElement(String uri, String localName, String qName) throws SAXException {
        if (pending) {
            if (pendingData != null) pendingData = pendingData.trim();
            start(pendingUri, pendingName, pendingData, pendingAttributes);
            pending = false;
            pendingData = null;
        }
        end(uri, localName);

        if (context.size() > 0) {
            final int indexOfLast = context.size() - 1;
            final String lastOne = context.get(indexOfLast);
            if (localName.equals(lastOne)) {
                context.remove(indexOfLast);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String fragment = new String(ch, start, length);
        if (pendingData == null)
            pendingData = fragment;
        else
            pendingData += fragment;
    }

    public abstract void start(String uri, String name, String data, EDIAttributes attributes) throws SAXException;

    public abstract void end(String uri, String name) throws SAXException;

    public List<String> getContext() {
        return context;
    }
}
