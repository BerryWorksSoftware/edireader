/*
 * Copyright 2005-2019 by BerryWorks Software, LLC. All rights reserved.
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
import com.berryworks.edireader.plugin.PluginControllerFactoryInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

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
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private EDIReader theReader;
    private XMLTags xmlTags;
    private PluginControllerFactoryInterface pluginControllerFactory;

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
                    logger.debug("EDIReader.parse(InputSource) hit end of input");
                    break;
                }
                logger.info("Created an EDIReader of type {}", theReader.getClass().getName());
                theReader.setExternalXmlDocumentStart(true);
                theReader.setAcknowledgment(getAckStream());
                theReader.setAlternateAcknowledgment(getAlternateAckStream());
                theReader.setContentHandler(getContentHandler());
                theReader.setSyntaxExceptionHandler(getSyntaxExceptionHandler());
                theReader.setNamespaceEnabled(isNamespaceEnabled());
                theReader.setIncludeSyntaxCharacters(isIncludeSyntaxCharacters());
                theReader.setKeepSpacesOnlyElements(isKeepSpacesOnlyElements());
            }
            theReader.setXMLTags(xmlTags);
            if (pluginControllerFactory != null) {
                theReader.setPluginControllerFactory(pluginControllerFactory);
            }
            theReader.parse(source);
            setDelimiter(theReader.getDelimiter());
            setSubDelimiter(theReader.getSubDelimiter());
            setSubSubDelimiter(theReader.getSubSubDelimiter());
            setRepetitionSeparator(theReader.getRepetitionSeparator());
            setRelease(theReader.getRelease());
            setDecimalMark(theReader.getDecimalMark());
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

    public void setPluginControllerFactory(PluginControllerFactoryInterface pluginControllerFactory) {
        this.pluginControllerFactory = pluginControllerFactory;
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
