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

import com.berryworks.edireader.EDIParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

/**
 * Transform EDI input using an XSLT stylesheet.
 * <p>
 * <br>
 * <br>
 * Assuming your CLASSPATH contains EDIReader.jar, you may run this program with
 * the command line <br>
 * <br>
 * <code>
 * java com.berryworks.edireader.demo.EDITransform [input-file] -x stylesheet-file [-o output-file]
 * </code><br>
 * <br>
 * If an input-file is not specified, System.in is used; if an output-file is
 * not specified, then System.out is used.
 */
public class EDITransform {
    private final InputSource inputSource;
    private final StreamSource stylesheetSource;
    private final OutputStream generatedOutput;

    /**
     * Constructor for the EDITransform object
     *
     * @param input      file containing EDI-structured data
     * @param stylesheet file containing the stylesheet to be used
     * @param output     file containing result of the transformation
     */
    public EDITransform(String input, String stylesheet, String output) {

        // Establish inputSource, a SAX InputSource
        if (input != null) {
            try {
                inputSource = new InputSource(new FileReader(input));
                System.out.println("Input file " + input + " opened");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        } else {
            inputSource = new InputSource(new InputStreamReader(System.in));
        }

        // Establish output file
        if (output != null) {
            try {
                generatedOutput = new BufferedOutputStream(
                        new FileOutputStream(output));
                System.out.println("Output file " + output + " opened");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        } else {
            generatedOutput = System.out;
        }

        // Establish stylesheet
        stylesheetSource = new StreamSource(new File(stylesheet));
        System.out.println("Stylesheet file " + stylesheet + " opened");

    }

    /**
     * Main processing method for the EDITransform object
     */
    public void run() {

        try {
            XMLReader ediReader;

            // Construct the SAXParser, to read the EDI input.
            // The normal JAXP sequence using SAXParserFactory.newInstance()
            // also works, but the XSL transformer below needs a SAXParser
            // that actually parses XML so changing JAXP system properties
            // at this point can interfere with proper transformation.
            // Therefore, the simplest thing is to use EDIParserFactory in
            // the following line instead of SAXParserFactory.
            SAXParserFactory sFactory = EDIParserFactory.newInstance();
            SAXParser sParser = sFactory.newSAXParser();
            ediReader = sParser.getXMLReader();

            // Construct a SAXTransformerFactory
            TransformerFactory tFactory = TransformerFactory.newInstance();
            if (!tFactory.getFeature(SAXTransformerFactory.FEATURE)) {
                String msg = "TransformerFactory "
                        + tFactory.getClass().getName()
                        + " does not support SAXTransformerFactory";
                System.err.println(msg);
                throw new RuntimeException(msg);
            }
            SAXTransformerFactory stFactory = (SAXTransformerFactory) tFactory;

            // Use the SAXTransformerFactory to create a TransformerHandler
            TransformerHandler transformerHandler;
            transformerHandler = stFactory
                    .newTransformerHandler(stylesheetSource);

            // Tell the TransformerHandler to send output to the result stream
            transformerHandler.setResult(new StreamResult(generatedOutput));

            // Tell the EDIReader to use the TransformerHandler as its
            // ContentHandler
            ediReader.setContentHandler(transformerHandler);

            // Finally, tell the EDIReader to start parsing.
            ediReader.parse(inputSource);
        } catch (SAXException e) {
            System.out.println("\nUnable to create SAXParser: " + e);
        } catch (IOException e) {
            System.out.println("\nUnable to create EDIReader: " + e);
        } catch (ParserConfigurationException e) {
            System.out.println("\nUnable to create Parser: " + e);
        } catch (TransformerConfigurationException e) {
            System.out.println("\nUnable to create Transformer: " + e);
        }
    }

    /**
     * Main for EDITransform.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        String outputFileName = null;
        String stylesheetFileName = null;
        String inputFileName = null;
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    if ((++i) >= args.length)
                        badArgs();
                    outputFileName = args[i];
                    break;
                case "-x":
                    if ((++i) >= args.length)
                        badArgs();
                    stylesheetFileName = args[i];
                    break;
                default:
                    inputFileName = args[i];
                    break;
            }
        }
        if (stylesheetFileName == null)
            badArgs();

        EDITransform theObject = new EDITransform(inputFileName,
                stylesheetFileName, outputFileName);
        theObject.run();
        String s = System.getProperty("line.separator");
        System.out.print(s + "Transformation complete" + s);
    }

    /**
     * Print summary of command line arguments expected.
     */
    private static void badArgs() {
        System.out.println(
                "Usage: EDITransform  [inputfile] [-x stylesheet] [-o outputfile] ");
        throw new RuntimeException("Missing or invalid command line arguments");
    }

}
