/*
 * Copyright 2005-2025 by BerryWorks Software, LLC. All rights reserved.
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
import com.berryworks.edireader.tokenizer.EDITokenizer;
import com.berryworks.edireader.tokenizer.Tokenizer;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * Creates a subclass of EDIReader appropriate for parsing a particular EDI
 * interchange. This class has just enough knowledge of the supported standards
 * make a decision based on observation of the first several characters of data.
 * This decision does not imply that data in well-formed with regard to the
 * chosen standard, but merely that we know which actual parser to use.
 */
public abstract class EDIReaderFactory {

    private static final int PEEK_LENGTH = 3;

    /**
     * Equivalent to createEDIReader(source, debugging=false)
     *
     * @param source EDI input
     * @return EDIReader
     * @throws IOException        if problem reading EDI input
     * @throws EDISyntaxException if invalid EDI is detected
     */
    public static EDIReader createEDIReader(InputSource source)
            throws EDISyntaxException, IOException {
        return createEDIReader(source, null, false);
    }

    /**
     * Equivalent to createEDIReader(new InputSource(edi))
     *
     * @param edi EDI input
     * @return EDIReader
     * @throws IOException        if problem reading EDI input
     * @throws EDISyntaxException if invalid EDI is detected
     */
    public static EDIReader createEDIReader(Reader edi)
            throws EDISyntaxException, IOException {
        return createEDIReader(new InputSource(edi), null, false);
    }

    public static EDIReader createEDIReader(InputStream edi)
            throws EDISyntaxException, IOException {
        return createEDIReader(new InputSource(edi), null, false);
    }

    /**
     * Factory method to create an instance of a subclass of EDIReader based on
     * examination of the first few characters of data.
     *
     * @param source EDI source
     * @param debug  true to turn debug on, false to turn it off
     * @return created EDIReader instance
     * @throws IOException        for problem reading EDI data
     * @throws EDISyntaxException if invalid EDI is detected
     */
    public static EDIReader createEDIReader(InputSource source, boolean debug)
            throws EDISyntaxException, IOException {
        return createEDIReader(source, null, debug);
    }

    /**
     * Factory method to create an instance of a subclass of EDIReader based on
     * examination of the first few characters of data. The second argument
     * allows for an array of chars to be treated as data that appears before
     * the next char of input from source. This can be useful when the source
     * was used earlier and some buffered and unused chars were left over.
     * In other words, the second argument provides for a kind of "pushback"
     * feature for the data source.
     *
     * @param source  EDI source
     * @param preRead chars of EDI input data to be used before reading from the source
     * @return created EDIReader instance
     * @throws IOException        for problem reading EDI data
     * @throws EDISyntaxException if invalid EDI is detected
     */
    public static EDIReader createEDIReader(InputSource source, char[] preRead)
            throws EDISyntaxException, IOException {
        return createEDIReader(source, preRead, false);
    }

    /**
     * Factory method to create an instance of a subclass of EDIReader based on
     * examination of the first few characters of data.
     *
     * @param source  EDI source
     * @param preRead chars of EDI input data to be used before reading from the source
     * @param debug   true to turn debug on, false to turn it off
     * @return created EDIReader instance
     * @throws IOException        for problem reading EDI data
     * @throws EDISyntaxException if invalid EDI is detected
     */
    public static EDIReader createEDIReader(InputSource source, char[] preRead, boolean debug)
            throws EDISyntaxException, IOException {
        Reader inputReader = EDIAbstractReader.createReader(source);
        Tokenizer tokenizer =
                (preRead == null || preRead.length == 0) ?
                        new EDITokenizer(inputReader) :
                        new EDITokenizer(inputReader, preRead);

        // Skip past any leading whitespace
        tokenizer.scanTerminatorSuffix();

        if (tokenizer.isEndOfData())
            return null;

        // Grab the first few characters
        char[] buf = tokenizer.lookahead(PEEK_LENGTH);
        if (buf == null || buf.length < PEEK_LENGTH)
            throw new RuntimeException("tokenizer.lookahead() returned null");

        // Get an appropriate parser, based on the first few characters
        String asString = new String(buf);
        EDIReader parser = ParserRegistry.get(asString);
        if (parser == null) throw new EDISyntaxException(asString.startsWith("<?xml ") ?
                ErrorMessages.XML_INSTEAD_OF_EDI :
                ErrorMessages.NO_STANDARD_BEGINS_WITH + asString.replaceAll("\\?+$", ""));

        source.setCharacterStream(inputReader);
        parser.setInputSource(source);
        parser.setTokenizer(tokenizer);
        parser.preview();

        parser.setInputReader(inputReader);
        return parser;
    }

}
