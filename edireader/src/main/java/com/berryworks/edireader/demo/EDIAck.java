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
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;

public class EDIAck {
    InputSource inputSource;
    OutputStream ackOutput;
    ContentHandler handler;
    EDIReader parser;
    final String inputFileName;
    final String outputFileName;

    public EDIAck(String input, String output) {
        inputFileName = input;
        outputFileName = output;

        // Establish output file
        if (outputFileName == null) {
            ackOutput = System.out;
        } else {
            try {
                ackOutput = new BufferedOutputStream(new FileOutputStream(outputFileName));
            } catch (IOException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }

        // Establish inputSource, a SAX InputSource
        try {
            inputSource = new InputSource(new FileReader(inputFileName));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }


    /**
     * Main processing method for the EDIAck object
     */
    public void run() {

        handler = new DefaultHandler();
        char[] leftOver = null;
        Writer ackWriter = new PrintWriter(ackOutput);

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
                parser.setAcknowledgment(ackWriter);
                parser.parse(inputSource);
                leftOver = parser.getTokenizer().getBuffered();
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        } catch (SAXException e) {
            System.err.println("\nEDI input not well-formed:\n" + e.toString());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        String outputFileName = null;
        String inputFileName = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    if ((++i) >= args.length)
                        badArgs();
                    outputFileName = args[i];
                    break;
                default:
                    inputFileName = args[i];
                    break;
            }
        }

        if (inputFileName == null) badArgs();

        EDIAck demo = new EDIAck(inputFileName, outputFileName);
        demo.run();
    }


    /**
     * Print a summary of the proper command line usage.
     */
    private static void badArgs() {
        System.out.println("Usage: EDIAck inputFile [-o outputFile] [-d]");
        System.out.println("where:  inputFile      file containing EDI input");
        System.out.println("        -o outputFile  file for acknowledgment output, defaults to stdout");
        System.out.println("        -d             debug mode");
        throw new RuntimeException("Missing or invalid command line arguments");
    }

}

