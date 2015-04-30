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

package com.berryworks.edireader.util.dom;

import com.berryworks.edireader.EDIAttributes;
import com.berryworks.edireader.util.sax.ContextAwareSaxAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.berryworks.edireader.util.FixedLength.isPresent;

public class DomBuildingSaxHandler extends ContextAwareSaxAdapter {

    private Document document;
    private Element currentElement;

    public DomBuildingSaxHandler() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.newDocument();

    }

    public Document getDocument() {
        return document;
    }

    @Override
    public void start(String uri, String name, String data, EDIAttributes attributes) throws SAXException {
        Element newElement = document.createElement(name);

        if (currentElement == null) {
            document.appendChild(newElement);
        } else {
            currentElement.appendChild(newElement);
        }

        currentElement = newElement;
        if (isPresent(data)) {
            Text text = document.createTextNode(data);
            currentElement.appendChild(text);
        }

        if (attributes != null && attributes.getLength() > 0) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String n = attributes.getLocalName(i);
                String v = attributes.getValue(i);
                currentElement.setAttribute(n, v);
            }

        }
    }

    @Override
    public void end(String uri, String name) throws SAXException {
        Node parentNode = currentElement.getParentNode();
        if (parentNode instanceof Element) {
            currentElement = (Element) parentNode;
        }
    }

}
