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

package com.berryworks.edireader.util.dom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.ListIterator;


public class XPathElements extends AbstractElementList {

    protected final XPath xPath = XPathFactory.newInstance().newXPath();
    private NodeList nodeList;

    public XPathElements(Node node, String path) throws XPathExpressionException {
        nodeList = (NodeList) xPath.evaluate(path, node, XPathConstants.NODESET);
    }

    @Override
    public boolean isEmpty() {
        return nodeList == null || nodeList.getLength() == 0;
    }

    @Override
    public ListIterator<Element> listIterator(int index) {
        if (index != 0) {
            throw new RuntimeException("index was " + index + " instead of 0 as expected");
        }
        return new ElementListIterator(nodeList);
    }

    private static class ElementListIterator extends AbstractElementListIterator {

        private final NodeList nodeList;
        private Element next;
        private int indexOfNext;

        public ElementListIterator(NodeList nodeList) {
            this.nodeList = nodeList;
            indexOfNext = -1;
            next();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Element next() {
            Element result = next;

            next = null;

            if (nodeList != null) {
                for (int i = indexOfNext + 1; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node instanceof Element) {
                        next = (Element) node;
                        indexOfNext = i;
                        break;
                    }
                }
            }

            if (next == null) {
                indexOfNext = -1;
            }

            return result;
        }
    }

}
