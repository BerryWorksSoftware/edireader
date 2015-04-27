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

import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This implementation of the SAX Attributes interface
 * supports Java serialization.
 */
public class SerializableAttributes extends AttributesImpl implements Serializable {

    private List<SerializableAttribute> attributes;

    public SerializableAttributes(Attributes fromAttributes) {
        attributes = new ArrayList<SerializableAttribute>();

        if (fromAttributes == null)
            return;

        for (int i = 0; i < fromAttributes.getLength(); i++) {
            SerializableAttribute attribute = new SerializableAttribute();
            attribute.setLocalName(fromAttributes.getLocalName(i));
            attribute.setQName(fromAttributes.getQName(i));
            attribute.setType(fromAttributes.getType(i));
            attribute.setURI(fromAttributes.getURI(i));
            attribute.setValue(fromAttributes.getValue(i));
            attributes.add(attribute);
        }
    }


    @Override
    public String getURI(int index) {
        return attributes.get(index).getURI();
    }

    @Override
    public String getLocalName(int index) {
        return attributes.get(index).getLocalName();
    }

    @Override
    public String getQName(int index) {
        return attributes.get(index).getQName();
    }

    @Override
    public String getType(int index) {
        return attributes.get(index).getType();
    }

    @Override
    public String getValue(int index) {
        return attributes.get(index).getValue();
    }

    @Override
    public int getLength() {
        return attributes.size();
    }

    @Override
    public void clear() {
        attributes = new ArrayList<SerializableAttribute>();
    }

    @Override
    public int getIndex(String uri, String localName) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public int getIndex(String qName) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String getType(String uri, String localName) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String getType(String qName) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String getValue(String uri, String localName) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public String getValue(String qName) {
        for (SerializableAttribute a : attributes) {
            if (qName.equals(a.getLocalName())) {
                return a.getValue();
            }
        }
        return null;
    }

    @Override
    public void setAttributes(Attributes atts) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void addAttribute(String uri, String localName, String qName, String type, String value) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setAttribute(int index, String uri, String localName, String qName, String type, String value) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void removeAttribute(int index) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setURI(int index, String uri) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setLocalName(int index, String localName) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setQName(int index, String qName) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setType(int index, String type) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void setValue(int index, String value) {
        throw new RuntimeException("not implemented");
    }

    static class SerializableAttribute implements Serializable {

        private String localName;
        private String qName;
        private String type;
        private String uRI;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getURI() {
            return uRI;
        }

        public void setURI(String uRI) {
            this.uRI = uRI;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private String value;


        public String getLocalName() {
            return localName;
        }

        public void setLocalName(String localName) {
            this.localName = localName;
        }

        public String getQName() {
            return qName;
        }

        public void setQName(String qName) {
            this.qName = qName;
        }
    }

    @Override
    public String toString() {
        String result = "";

        int n = getLength();
        for (int i = 0; i < n; i++) {
            String name = getQName(i);
            String value = getValue(i);
            if (result.length() > 0) {
                result += ", ";
            }
            result += name + "=" + value;
        }

        return "SerializableAttribute(" + n + "): " + result;
    }


}
