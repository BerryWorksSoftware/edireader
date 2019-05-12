/*
 * Copyright 2005-2017 by BerryWorks Software, LLC. All rights reserved.
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

package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.error.EDISyntaxExceptionHandler;
import com.berryworks.edireader.error.RecoverableSyntaxException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import static com.berryworks.edireader.demo.EDItoXML.NEW_LINE;
import static com.berryworks.edireader.demo.EDItoXML.establishInput;

public class Splitter {
    private static int count;
    private final InputSource inputSource;
    private EDIReader parser;
    private final FileSequenceNameGenerator handlerFactory;


    public Splitter(Reader inputReader, String outputFileNamePattern) {
        inputSource = new InputSource(inputReader);
        handlerFactory = new FileSequenceNameGenerator(outputFileNamePattern);
    }

    public void run() throws IOException, SAXException {
        split(inputSource);
    }

    public void split(InputSource inputSource) throws IOException, SAXException {
        char[] leftOver = null;
        while ((parser = EDIReaderFactory.createEDIReader(inputSource, leftOver)) != null) {
            String outputFilename = handlerFactory.generateName();
            System.out.println(NEW_LINE + "EDI interchange written to: " + outputFilename);
            parser.setContentHandler(new ScanningHandler());
            try (Writer writer = new FileWriter(outputFilename)) {
                parser.setCopyWriter(writer);
                parser.parse(inputSource);
            }
            leftOver = parser.getTokenizer().getBuffered();
        }
    }

    public static void main(String[] args) {
        CommandLine commandLine = new CommandLine(args);
        String inputFileName = commandLine.getPosition(0);
        String outputFileNamePattern = commandLine.getOption("o");

        if (outputFileNamePattern == null) badArgs();

        Reader inputReader = establishInput(inputFileName);
        Splitter ediSplitter = new Splitter(inputReader, outputFileNamePattern);
        try {
            ediSplitter.run();
        } catch (SAXException | IOException e) {
            System.out.print(e);
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void badArgs() {
        System.err.println("Usage: Splitter [inputFile] [-o outputFilenamePattern]");
        throw new RuntimeException("Missing or invalid command line arguments");
    }

    public static int getCount() {
        return count;
    }

    private class ScanningHandler extends DefaultHandler {

        @Override
        public void startElement(String namespace, String localName,
                                 String qName, Attributes atts) throws SAXException {
            String indent;
            if (localName.startsWith(parser.getXMLTags().getInterchangeTag())) {
                indent = "   ";
            } else if (localName.startsWith(parser.getXMLTags().getSenderTag())) {
                System.out.println("  +Sender");
                indent = "     ";
            } else if (localName.startsWith(parser.getXMLTags().getReceiverTag())) {
                System.out.println("  +Recipient");
                indent = "     ";
            } else if (localName.startsWith(parser.getXMLTags().getAddressTag())) {
                System.out.println("    +Address");
                indent = "       ";
            } else if (localName.startsWith(parser.getXMLTags().getGroupTag())) {
                System.out.println("  +Group");
                indent = "     ";
            } else if (localName.startsWith(parser.getXMLTags()
                    .getDocumentTag())) {
                System.out.println("    +Document");
                indent = "       ";
            } else {
                // indent = " ";
                return;
            }

            for (int i = 0; i < atts.getLength(); i++)
                System.out.println(indent + atts.getLocalName(i) + "="
                        + atts.getValue(i));
        }

    }

    private static class SyntaxExceptionHandler implements EDISyntaxExceptionHandler {
        public boolean process(RecoverableSyntaxException syntaxException) {
            System.out.println();
            System.out.println("Recoverable syntax exception: " +
                    syntaxException.getClass().getCanonicalName() +
                    " - " + syntaxException.getMessage());
            return true;
        }
    }

}
