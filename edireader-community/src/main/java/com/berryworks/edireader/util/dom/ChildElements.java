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

import java.util.ListIterator;

public class ChildElements extends AbstractElementList {

    private final Node parentNode;
    private final String tag;

    public ChildElements(Node node) {
        this(node, null);
    }

    public ChildElements(Node node, String tag) {
        parentNode = node;
        this.tag = tag;
    }

    @Override
    public ListIterator<Element> listIterator(int index) {

        if (index != 0) {
            throw new RuntimeException("index was " + index + " instead of 0 as expected");
        }
        return new ElementListIterator(parentNode);
    }

    private class ElementListIterator extends AbstractElementListIterator {
        private Element next;

        public ElementListIterator(Node parentNode) {
            Node nextNode = parentNode.getFirstChild();
            while (nextNode != null) {
                if (nextNode instanceof Element) {
                    if (tag == null) {
                        // If no tag to match, then this one qualifies
                        break;
                    } else if (tag.equals(nextNode.getNodeName())) {
                        // This one qualifies since the tag matches
                        break;
                    }
                }
                nextNode = nextNode.getNextSibling();
            }
            next = (Element) nextNode;
        }

        public Element next() {
            Element result = next;

            Node nextNode = next.getNextSibling();
            while (nextNode != null) {
                if (nextNode instanceof Element) {
                    if (tag == null) {
                        // If no tag to match, then this one qualifies
                        break;
                    } else if (tag.equals(nextNode.getNodeName())) {
                        // This one qualifies since the tag matches
                        break;
                    }
                }
                nextNode = nextNode.getNextSibling();
            }
            next = (Element) nextNode;
            return result;
        }

        public boolean hasNext() {
            return next != null;
        }
    }
}