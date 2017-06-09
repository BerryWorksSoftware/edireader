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

package com.berryworks.edireader.demo;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.error.EDISyntaxExceptionHandler;
import com.berryworks.edireader.error.RecoverableSyntaxException;
import com.berryworks.edireader.util.CommandLine;
import com.berryworks.edireader.util.XmlFormatter;
import org.xml.sax.InputSource;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Converts EDI input to XML output using the default XSLT transformer.
 * <p>
 * Assuming your CLASSPATH contains edireader-n.n.n.jar, you may run this program with
 * the command line <br>
 * <br><code>
 * java com.berryworks.edireader.demo.EDItoXML [input-file] [-o output-file]
 * </code><br><br>
 * If an input-file is not specified, System.in is used; if an output-file is
 * not specified, System.out is used.
 */
public class EDItoXML {
    public final static String NEW_LINE = System.getProperty("line.separator");

    private Writer generatedOutput;
    private Writer acknowledgmentWriter;
    private Reader inputReader;
    private boolean namespaceEnabled;
    private boolean recover;

    public static void main(String args[]) {
        EDItoXML theObject = new EDItoXML();
        if (!configure(args, theObject)) return;
        theObject.run();
    }

    /**
     * Main processing method for the EDItoXML object
     */
    public void run() {

        try {
            EDIReader ediReader = createEDIReader();

            // Tell the ediReader if an xmlns="http://..." is desired
            if (namespaceEnabled) {
                ediReader.setNamespaceEnabled(true);
            }

            // Tell the ediReader to handle EDI syntax errors instead of aborting
            if (recover) {
                ediReader.setSyntaxExceptionHandler(new IgnoreSyntaxExceptions());
            }

            // Give the ediReader a Writer to use for acknowledgment output if needed
            if (acknowledgmentWriter != null) {
                ediReader.setAcknowledgment(acknowledgmentWriter);
            }

            // Establish the SAXSource
            SAXSource source = new SAXSource(ediReader, new InputSource(inputReader));

            // Use a StreamResult to capture the generated XML output
            StreamResult result = new StreamResult(generatedOutput);

            // Use a Transformer to generate XML output from the parsed input
            TransformerFactory.newInstance().newTransformer().transform(source, result);
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

    protected EDIReader createEDIReader() {
        return new EDIReader();
    }

    static boolean configure(final String[] args, EDItoXML theObject) {
        CommandLine commandLine = new CommandLine(args) {
            @Override
            public String usage() {
                String text = NEW_LINE + "EDItoXML [ediInputFile] [-o xmlOutputFile] [-a acknowledgmentFile]" +
                        " [-n true|false] [-r true|false] [-i true|false]";
                text += NEW_LINE + "options:";
                text += NEW_LINE + "   -n   XML includes namespace declaration. Defaults to false.";
                text += NEW_LINE + "   -r   Recover and continue parsing after an error is detected in EDI input. Defaults to false.";
                text += NEW_LINE + "   -i   Indent XML output for readability. Defaults to false.";
                return text;
            }
        };

        if (!commandLine.isValid()) {
            return false;
        }

        String inputFileName = commandLine.getPosition(0);
        String outputFileName = commandLine.getOption("o");
        String acknowledgmentFileName = commandLine.getOption("a");
        boolean namespaceEnabled = "true".equals(commandLine.getOption("n"));
        boolean recover = "true".equals(commandLine.getOption("r"));
        boolean indent = "true".equals(commandLine.getOption("i"));

        Reader inputReader = establishInput(inputFileName);
        Writer generatedOutput = establishOutput(outputFileName);

        theObject.setInputReader(inputReader);
        theObject.setXmlOutputWriter(generatedOutput);
        theObject.setNamespaceEnabled(namespaceEnabled);
        theObject.setRecover(recover);
        theObject.setIndent(indent);
        if (acknowledgmentFileName != null) {
            theObject.setAcknowledgmentWriter(establishOutput(acknowledgmentFileName));
        }
        return true;
    }

    static Writer establishOutput(String outputFileName) {
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
        return generatedOutput;
    }

    public static Reader establishInput(String inputFileName) {
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
        return inputReader;
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

    public void setInputReader(Reader inputReader) {
        this.inputReader = inputReader;
    }

    public void setXmlOutputWriter(Writer xmlOutputWriter) {
        generatedOutput = xmlOutputWriter;
    }

    public void setAcknowledgmentWriter(Writer acknowledgmentWriter) {
        this.acknowledgmentWriter = acknowledgmentWriter;
    }

    public static class IgnoreSyntaxExceptions implements EDISyntaxExceptionHandler {

        public boolean process(RecoverableSyntaxException syntaxException) {
            System.out.println("Syntax Exception. class: " + syntaxException.getClass().getName() + "  message:" + syntaxException.getMessage());
            // Return true to indicate that you want parsing to continue.
            return true;
        }
    }

}
