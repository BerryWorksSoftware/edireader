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

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Provide convenience methods to simplify the construction
 * of XML attributes.
 */
public class EDIAttributes extends AttributesImpl {
    public EDIAttributes() {
    }

    public EDIAttributes(Attributes fromAttributes) {
        if (fromAttributes == null)
            return;

        for (int i = 0; i < fromAttributes.getLength(); i++) {
            addAttribute(
                    fromAttributes.getURI(i),
                    fromAttributes.getLocalName(i),
                    fromAttributes.getQName(i),
                    fromAttributes.getType(i),
                    fromAttributes.getValue(i));
        }
    }

    public void addCDATA(String name, String value) {
        addAttribute("", name, name, "CDATA", value);
    }

    public void addCDATA(String name, int value) {
        addAttribute("", name, name, "CDATA", String.valueOf(value));
    }

    public String toString() {

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < getLength(); i++) {
            if (result.length() > 0) result.append(", ");
            result.append(getLocalName(i)).append(" = ").append(getValue(i));
        }

        if (result.length() == 0)
            result.append("empty");
        return result.toString();
    }
}
