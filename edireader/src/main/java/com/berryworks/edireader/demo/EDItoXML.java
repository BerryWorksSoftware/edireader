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

package com.berryworks.edireader.demo;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.error.EDISyntaxExceptionHandler;
import com.berryworks.edireader.error.RecoverableSyntaxException;
import com.berryworks.edireader.util.CommandLine;
import com.berryworks.edireader.util.XmlFormatter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Converts EDI input to XML output using the default XSLT transformer.
 * <p/><br><br>
 * Assuming your CLASSPATH contains edireader-n.n.n.jar, you may run this program with
 * the command line <br>
 * <br><code>
 * java com.berryworks.edireader.demo.EDItoXML [input-file] [-o output-file]
 * </code><br><br>
 * If an input-file is not specified, System.in is used; if an output-file is
 * not specified, System.out is used.
 */
public class EDItoXML {
    private final InputSource inputSource;
    private Writer generatedOutput;
    private final Reader inputReader;
    private boolean namespaceEnabled;
    private boolean recover;

    public EDItoXML(Reader inputReader, Writer outputWriter) {
        this.inputReader = inputReader;
        inputSource = new InputSource(inputReader);
        generatedOutput = outputWriter;
    }

    /**
     * Main processing method for the EDItoXML object
     */
    public void run() {

        try {
            XMLReader ediReader = new EDIReader();

            // Tell the ediReader if an xmlns="http://..." is desired
            if (namespaceEnabled) {
                ((EDIReader) ediReader).setNamespaceEnabled(namespaceEnabled);
            }

            // Tell the ediReader to handle EDI syntax errors instead of aborting
            if (recover) {
                ((EDIReader) ediReader).setSyntaxExceptionHandler(new IgnoreSyntaxExceptions());
            }

            // Establish the SAXSource
            SAXSource source = new SAXSource(ediReader, inputSource);

            // Establish a Transformer
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            // Use a StreamResult to capture the generated XML output
            StreamResult result = new StreamResult(generatedOutput);

            // Call the Transformer to generate XML output from the parsed input
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            System.err.println("\nUnable to create Transformer: " + e);
        } catch (TransformerException e) {
            System.err.println("\nFailure to transform: " + e);
            System.err.println(e.getMessage());
        }

        try {
            inputReader.close();
        } catch (IOException ignored) {
        }
        try {
            generatedOutput.close();
        } catch (IOException ignored) {
        }
    }


    public void run_alternate1() {

        try {
            // Establish an EDIReader.
            EDIReader ediReader = EDIReaderFactory.createEDIReader(inputSource);

            // Tell the ediReader if an xmlns="http://..." is desired
            if (namespaceEnabled) {
                ediReader.setNamespaceEnabled(namespaceEnabled);
            }

            // Tell the ediReader to handle EDI syntax errors instead of aborting
            if (recover) {
                ediReader.setSyntaxExceptionHandler(new IgnoreSyntaxExceptions());
            }

            // Establish the SAXSource
            SAXSource source = new SAXSource(ediReader, inputSource);

            // Establish a Transformer
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            // Use a StreamResult to capture the generated XML output
            StreamResult result = new StreamResult(generatedOutput);

            // Call the Transformer to generate XML output from the parsed input
            transformer.transform(source, result);
        } catch (EDISyntaxException e) {
            System.err.println("\nSyntax error while parsing EDI: " + e);
        } catch (IOException e) {
            System.err.println("\nException attempting to read EDI data: " + e);
        } catch (TransformerConfigurationException e) {
            System.err.println("\nUnable to create Transformer: " + e);
        } catch (TransformerException e) {
            System.err.println("\nFailure to transform: " + e);
            System.err.println(e.getMessage());
        }
    }

    public void run_alternate2() {

        try {
            // Establish an XMLReader which is actually an EDIReader.
            System.setProperty("javax.xml.parsers.SAXParserFactory",
                    "com.berryworks.edireader.EDIParserFactory");
            SAXParserFactory sFactory = SAXParserFactory.newInstance();
            SAXParser sParser = sFactory.newSAXParser();
            XMLReader ediReader = sParser.getXMLReader();

            // Tell the ediReader if an xmlns="http://..." is desired
            if (namespaceEnabled) {
                ((EDIReader) ediReader).setNamespaceEnabled(namespaceEnabled);
            }

            // Tell the ediReader to handle EDI syntax errors instead of aborting
            if (recover) {
                ((EDIReader) ediReader).setSyntaxExceptionHandler(new IgnoreSyntaxExceptions());
            }

            // Establish the SAXSource
            SAXSource source = new SAXSource(ediReader, inputSource);

            // Establish a Transformer
            Transformer transformer = TransformerFactory.newInstance().newTransformer();

            // Use a StreamResult to capture the generated XML output
            StreamResult result = new StreamResult(generatedOutput);

            // Call the Transformer to generate XML output from the parsed input
            transformer.transform(source, result);
        } catch (SAXException | ParserConfigurationException e) {
            System.err.println("\nUnable to create EDIReader: " + e);
        } catch (TransformerConfigurationException e) {
            System.err.println("\nUnable to create Transformer: " + e);
        } catch (TransformerException e) {
            System.err.println("\nFailure to transform: " + e);
            System.err.println(e.getMessage());
        }
    }

    /**
     * Main for EDItoXML.
     *
     * @param args command line arguments
     */
    public static void main(String args[]) {
        CommandLine commandLine = new CommandLine(args) {
            @Override
            public String usage() {
                return "EDItoXML [inputfile] [-o outputfile] [-n true|false] [-r true|false] [-i true|false]";
            }
        };
        String inputFileName = commandLine.getPosition(0);
        String outputFileName = commandLine.getOption("o");
        boolean namespaceEnabled = "true".equals(commandLine.getOption("n"));
        boolean recover = "true".equals(commandLine.getOption("r"));
        boolean indent = "true".equals(commandLine.getOption("i"));

        // Establish input
        Reader inputReader;
        if (inputFileName == null) {
            inputReader = new InputStreamReader(System.in);
        } else {
            try {
                inputReader = new InputStreamReader(
                        new FileInputStream(inputFileName), "ISO-8859-1");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }

        // Establish output
        Writer generatedOutput;
        if (outputFileName == null) {
            generatedOutput = new OutputStreamWriter(System.out);
        } else {
            try {
                generatedOutput = new OutputStreamWriter(new FileOutputStream(
                        outputFileName), "ISO-8859-1");
                System.out.println("Output file " + outputFileName + " opened");
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }

        EDItoXML theObject = new EDItoXML(inputReader, generatedOutput);
        theObject.setNamespaceEnabled(namespaceEnabled);
        theObject.setRecover(recover);
        theObject.setIndent(indent);
        theObject.run();
        String s = System.getProperty("line.separator");
        System.out.print(s + "Transformation complete" + s);
    }

    public boolean isNamespaceEnabled() {
        return namespaceEnabled;
    }

    public void setNamespaceEnabled(boolean namespaceEnabled) {
        this.namespaceEnabled = namespaceEnabled;
    }

    public void setRecover(boolean recover) {
        this.recover = recover;
    }

    public void setIndent(boolean indent) {
        if (indent) {
            if (generatedOutput instanceof XmlFormatter) {
                // The output Writer is already wrapper in an indenting filter, so do not do it again
            } else {
                // Wrap the Writer for the generated output with an indenting filter
                generatedOutput = new XmlFormatter(generatedOutput);
            }
        }
    }

    static class IgnoreSyntaxExceptions implements EDISyntaxExceptionHandler {

        public boolean process(RecoverableSyntaxException syntaxException) {
            System.out.println("Syntax Exception. class: " + syntaxException.getClass().getName() + "  message:" + syntaxException.getMessage());
            // Return true to indicate that you want parsing to continue.
            return true;
        }
    }

}
