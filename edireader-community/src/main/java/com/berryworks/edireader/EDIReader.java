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

import com.berryworks.edireader.error.ErrorMessages;
import com.berryworks.edireader.plugin.PluginControllerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;

/**
 * Reads and parses an EDI interchange in any of the supported EDI standards.
 * Once a specific EDI standard is identified, EDIReader delegates the actual
 * parsing to a subclass of EDIReader that it creates. This delegation technique
 * allows an application to use EDIReader just as it would an XMLReader and
 * without having to configure or otherwise signal for a particular standard to
 * be used. Another advantage of this approach is that it provides a framework
 * for additional EDIReader subclasses to be developed and integrated with
 * little impact.
 */
public class EDIReader extends EDIAbstractReader implements ErrorMessages {

    /**
     * If debug is set to true, then a parser may emit diagnostic information to
     * System.err
     */
    public static boolean debug;

    private EDIReader theReader;

    private XMLTags xmlTags;

    private PluginControllerFactory pluginControllerFactory;

    public EDIReader() {
        if (Boolean.getBoolean("edireader.debug"))
            setDebug(true);
    }

    /**
     * Read enough of the EDI interchange to establish which characters are used
     * for segment terminators, element delimiters, etc. Each subclass of
     * EDIReader overrides this method with logic specific to a particular EDI
     * standard. Upon return, the input stream has been re-positioned so that
     * the interchange will be parsed from the beginning by <code>parse()</code>.
     */
    @Override
    public void preview() throws EDISyntaxException, IOException {
        throw new EDISyntaxException("EDIReader.preview() called unexpectedly");
    }

    /**
     * Parse an EDI interchange from the input source.
     */
    public void parse(InputSource source) throws SAXException, IOException {

        startXMLDocument();

        char[] leftOver = null;
        while (true) {
            if (theReader == null) {
                theReader = EDIReaderFactory.createEDIReader(source, leftOver);
                if (theReader == null) {
                    if (debug)
                        trace("EDIReader.parse(InputSource) hit end of input");
                    break;
                }
                if (debug)
                    trace("EDIReader.parse(InputSource) created an EDIReader of type "
                            + theReader.getClass().getName());
                theReader.setExternalXmlDocumentStart(true);
                theReader.setAcknowledgment(getAckStream());
                theReader.setAlternateAcknowledgment(getAlternateAckStream());
                theReader.setContentHandler(getContentHandler());
                theReader.setSyntaxExceptionHandler(getSyntaxExceptionHandler());
                theReader.setNamespaceEnabled(isNamespaceEnabled());
            }
            theReader.setXMLTags(xmlTags);
            if (pluginControllerFactory != null) {
                theReader.setPluginControllerFactory(pluginControllerFactory);
            }
            theReader.parse(source);
            setDelimiter(theReader.getDelimiter());
            setSubDelimiter(theReader.getSubDelimiter());
            setTerminator(theReader.getTerminator());
            setTerminatorSuffix(theReader.getTerminatorSuffix());

            leftOver = theReader.getTokenizer().getBuffered();
            theReader = null;
        }

        endXMLDocument();

    }

    public void setXMLTags(XMLTags tags) {
        xmlTags = tags;
    }

    public XMLTags getXMLTags() {
        if (xmlTags == null)
            xmlTags = DefaultXMLTags.getInstance();

        return xmlTags;
    }

    public void setPluginControllerFactory(PluginControllerFactory pluginControllerFactory) {
        this.pluginControllerFactory = pluginControllerFactory;
    }

    /**
     * Sets debug on or off.
     *
     * @param d true to turn debug on, false to turn it off
     */
    public static void setDebug(boolean d) {
        if (debug && d) {
            trace("Debug already on");
        } else if (!debug && d) {
            trace("Debug turned on");
        } else if (debug && !d) {
            trace("Debug turned off");
        }
        debug = d;
    }

    protected void startXMLDocument() throws SAXException {
        AttributesImpl attrList = new AttributesImpl();
        attrList.clear();
        final ContentHandler contentHandler = getContentHandler();
        if (contentHandler == null) {
            throw new SAXException("No ContentHandler configured for EDIReader");
        }
        contentHandler.startDocument();
        String rootTag = getXMLTags().getRootTag();
        if (isNamespaceEnabled()) {
            contentHandler.startElement(BERRYWORKS_NAMESPACE, rootTag, rootTag, attrList);
        } else {
            startElement(rootTag, attrList);
        }
    }

    protected void endXMLDocument() throws SAXException {
        endElement(getXMLTags().getRootTag());
        getContentHandler().endDocument();
    }

    protected void startElement(String tag, Attributes attributes)
            throws SAXException {
        getContentHandler().startElement("", tag, tag, attributes);
    }

    protected void endElement(String tag) throws SAXException {
        getContentHandler().endElement("", tag, tag);
    }
}
