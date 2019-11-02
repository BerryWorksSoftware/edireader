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

import com.berryworks.edireader.tokenizer.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class UNHReader extends EdifactReader {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());

    @Override
    public void preview() throws EDISyntaxException, IOException {
        setDelimiter('+');
        setSubDelimiter(':');
        setTerminator('\'');
        setRelease('?');
        setRepetitionSeparator('\000');
        setDecimalMark('.');
        setPreviewed(true);
    }

    @Override
    protected Token parseInterchange(Token token) throws SAXException, IOException {
        logger.debug("parsing interchange of the UNH variety (UNB/UNZ are omitted) of EDIFACT");
        getInterchangeAttributes().clear();
        getInterchangeAttributes().addCDATA(getXMLTags().getStandard(), "EDIFACT-UNH");
        setGroupCount(0);

        startInterchange(getInterchangeAttributes());

        while (true) {
            ungExplicit = true;
            if (token.getType() == Token.TokenType.END_OF_DATA) {
                break;
            } else if (token.getType() != Token.TokenType.SEGMENT_START) {
                throw new EDISyntaxException(
                        "Invalid beginning of UNH segment", getTokenizer());
            }
            String sType = token.getValue();
            if (sType.equals("UNH")) {
                impliedFunctionalGroup(token);
            } else {
                break;
            }
        }

        endInterchange();

        return getTokenizer().skipSegment();
    }


    @Override
    protected Token recognizeBeginning() throws IOException, SAXException {
        Token t = getTokenizer().nextToken();
        if (t.getType() != Token.TokenType.SEGMENT_START || !t.valueEquals("UNH")) {
            throw new EDISyntaxException("The first segment of this type of interchange must be a UNH");
        }
        return t;
    }

    @Override
    protected Token impliedFunctionalGroup(Token token) throws SAXException, IOException {
        getGroupAttributes().clear();
        startElement(getXMLTags().getGroupTag(), getGroupAttributes());
        while (true) {
            if (token.getType() == Token.TokenType.END_OF_DATA) {
                break;
            } else if (token.getType() != Token.TokenType.SEGMENT_START) {
                throw new EDISyntaxException(
                        "Invalid beginning of UNH segment",
                        getTokenizer().getSegmentCount());
            }

            String sType = token.getValue();
            if (sType.equals("UNH")) {
                setGroupCount(1 + getGroupCount());
                parseDocument(token);
                token = getTokenizer().nextToken();
            } else {
                throw new EDISyntaxException(UNEXPECTED_SEGMENT_IN_CONTEXT,
                        "UNH", sType, getTokenizer());
            }
        }

        endElement(getXMLTags().getGroupTag());
        return (token);
    }
}
