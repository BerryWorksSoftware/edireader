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

package com.berryworks.edireader.demo;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.error.EDISyntaxExceptionHandler;
import com.berryworks.edireader.error.RecoverableSyntaxException;
import com.berryworks.edireader.tokenizer.Tokenizer;
import com.berryworks.edireader.util.CommandLine;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;

/**
 * Scans EDI input to produce a summary report.
 * <p>
 * This program illustrates the use of EDIReader with a ContentHandler other
 * than XSLT. In this case, a simple inner class accepts the call-backs from the
 * EDIReader and produces the report.
 * <p>
 * This programs also demonstrates how to parse a single input stream that
 * contains multiple EDI interchanges. Note the interchanges are not required to
 * be all of the same EDI standard; ANSI and EDIFACT interchanges can be freely
 * intermixed.
 * </p>
 * Assuming your CLASSPATH contains EDIReader.jar, you may run this program with
 * the command line <br>
 * <br>
 * <code>
 * java com.berryworks.edireader.demo.EDIScanner [input-file] [-o output-file]
 * </code><br>
 * <br>
 * If an input-file is not specified, System.in is used; if an output-file is
 * not specified, System.out is used.
 */
public class EDIScanner {
    private InputSource inputSource;
    private PrintStream scannerOutput;
    private EDIReader parser;
    private int interchangeCount;

    /**
     * Constructor for the EDIScanner object
     *
     * @param input  Description of the Parameter
     * @param output Description of the Parameter
     */
    public EDIScanner(String input, String output) {

        // Establish output stream
        if (output == null) {
            scannerOutput = System.out;

        } else {
            try {
                scannerOutput = new PrintStream(new FileOutputStream(output));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // Establish inputSource, a SAX InputSource
        if (input == null) {
            inputSource = new InputSource(new InputStreamReader(System.in));

        } else {
            try {
                inputSource = new InputSource(new InputStreamReader(new FileInputStream(input), "ISO-8859-1"));
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    /**
     * Main processing method for the EDIScanner object
     */
    public void run() {

        ContentHandler handler = new ScanningHandler();
        char[] leftOver = null;

        try {
            while (true) {
                // The following line creates an EDIReader explicitly
                // as an alternative to the JAXP-based technique.
                parser = EDIReaderFactory.createEDIReader(inputSource, leftOver);
                if (parser == null) {
                    // end of input
                    break;
                }
                parser.setContentHandler(handler);
                parser.setSyntaxExceptionHandler(new SyntaxExceptionHandler());
                parser.parse(inputSource);
                leftOver = parser.getTokenizer().getBuffered();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());

        } catch (SAXException syntaxException) {
            System.out.println();
            System.out.println("Unrecoverable syntax exception: " +
                    syntaxException.getClass().getCanonicalName() +
                    " - " + syntaxException.getMessage());

            Tokenizer ediTokenizer = parser.getTokenizer();
            System.out.println("Internal EDIReader diagnostic information: ");
            char delimiter = ediTokenizer.getDelimiter();
            System.out.println("Field delimiter: " + representationOf(delimiter));
            char terminator = ediTokenizer.getTerminator();
            System.out.println("Segment terminator: " + representationOf(terminator));

            throw new RuntimeException(syntaxException.getMessage());
        }
    }

    private String representationOf(char c) {
        String result = "|" + c + "|";
        result += " (" + (int) c + ")";
        return result;
    }

    public static void main(String args[]) {
        CommandLine commandLine = new CommandLine(args);
        String inputFileName = commandLine.getPosition(0);
        if (inputFileName == null) badArgs();
        String outputFileName = commandLine.getOption("o");

        EDIScanner scanner = new EDIScanner(inputFileName, outputFileName);
        scanner.run();
    }

    private static void badArgs() {
        System.out.println("Usage: EDIScanner inputfile [-o outputfile]");
        throw new RuntimeException("Missing or invalid command line arguments");
    }

    private class ScanningHandler extends DefaultHandler {

        @Override
        public void startElement(String namespace, String localName,
                                 String qName, Attributes atts) throws SAXException {
            String indent;
            if (localName.startsWith(parser.getXMLTags().getInterchangeTag())) {
                scannerOutput.println("+Interchange  (" + ++interchangeCount + ")");
                indent = "   ";
            } else if (localName.startsWith(parser.getXMLTags().getSenderTag())) {
                scannerOutput.println("  +Sender");
                indent = "     ";
            } else if (localName.startsWith(parser.getXMLTags().getReceiverTag())) {
                scannerOutput.println("  +Recipient");
                indent = "     ";
            } else if (localName.startsWith(parser.getXMLTags().getAddressTag())) {
                scannerOutput.println("    +Address");
                indent = "       ";
            } else if (localName.startsWith(parser.getXMLTags().getGroupTag())) {
                scannerOutput.println("  +Group");
                indent = "     ";
            } else if (localName.startsWith(parser.getXMLTags()
                    .getDocumentTag())) {
                scannerOutput.println("    +Document");
                indent = "       ";

            } else {
                // indent = " ";
                return;
            }

            for (int i = 0; i < atts.getLength(); i++)
                scannerOutput.println(indent + atts.getLocalName(i) + "="
                        + atts.getValue(i));
        }

    }

    public int interchangeCount() {
        return interchangeCount;
    }

    private class SyntaxExceptionHandler implements EDISyntaxExceptionHandler {
        public boolean process(RecoverableSyntaxException syntaxException) {
            scannerOutput.println();
            scannerOutput.println("Recoverable syntax exception: " +
                    syntaxException.getClass().getCanonicalName() +
                    " - " + syntaxException.getMessage());
            return true;
        }
    }
}
