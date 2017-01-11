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

import com.berryworks.edireader.splitter.HandlerFactory;
import com.berryworks.edireader.splitter.SplittingHandler;
import com.berryworks.edireader.util.CommandLine;
import com.berryworks.edireader.util.dom.DomBuildingSaxHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Converts EDI input into a series of XML output files
 * such that each XML file corresponds to one document
 * from the EDI input as if that document had been the only
 * document in its interchange.
 */
public class EDISplitter {
    private static int count;
    private final InputSource inputSource;
    private HandlerFactory handlerFactory;

    public EDISplitter(Reader inputReader, String outputFileNamePattern) {
        inputSource = new InputSource(inputReader);
        setHandlerFactory(new FileSequenceHandlerFactory(outputFileNamePattern));
    }

    public void run() throws IOException, SAXException {
        new SplittingHandler(handlerFactory).split(inputSource);
    }

    public void setHandlerFactory(HandlerFactory handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    public static void main(String args[]) {
        CommandLine commandLine = new CommandLine(args);
        String inputFileName = commandLine.getPosition(0);
        String outputFileNamePattern = commandLine.getOption("o");

        if (outputFileNamePattern == null) badArgs();

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

        EDISplitter ediSplitter = new EDISplitter(inputReader, outputFileNamePattern);
        try {
            ediSplitter.run();
        } catch (SAXException | IOException e) {
            System.out.print(e);
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new RuntimeException(e.getMessage());
        }
        String s = System.getProperty("line.separator");
        System.out.print(s + "EDI input parsed into " + count + " XML output files" + s);
    }

    private static void badArgs() {
        System.err.println("Usage: EDISplitter [inputFile] [-o outputFilenamePattern]");
        throw new RuntimeException("Missing or invalid command line arguments");
    }

    public static int getCount() {
        return count;
    }

    static class FileSequenceHandlerFactory implements HandlerFactory {
        private String filenameSuffix, filenamePrefix;
        private int sequenceNumberLength;
        private DomBuildingSaxHandler saxHandler;

        public FileSequenceHandlerFactory(String fileNamePattern) {
            establishPattern(fileNamePattern);
        }

        @Override
        public ContentHandler createDocument() throws Exception {
            count++;
            saxHandler = new DomBuildingSaxHandler();
            return saxHandler;
        }

        @Override
        public void closeDocument(
                String senderQualifier, String senderId,
                String receiverQualifier, String receiverId,
                String interchangeControlNumber,
                String groupSender, String groupReceiver, String groupControlNumber,
                String documentControlNumber, String documentType, String version) throws IOException {
            String xmlFilename = generateName();

//            System.out.println("Generating XML into file " + xmlFilename);

            DOMSource source = new DOMSource(saxHandler.getDocument());

            try (FileWriter writer = new FileWriter(xmlFilename)) {
                TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(writer));

            } catch (TransformerException e) {
                throw new IOException("Unable to generate XML from DOM");
            }
        }

        @Override
        public void markEndOfStream() {
        }

        private void establishPattern(String fileNamePattern) {
            String[] splitResult = fileNamePattern.split("0+", 2);
            if (splitResult.length < 2) badArgs();
            filenamePrefix = splitResult[0];
            filenameSuffix = splitResult[1];
            sequenceNumberLength = fileNamePattern.length() - filenamePrefix.length() - filenameSuffix.length();
        }

        private String generateName() {
            String sequenceDigits = "" + (100000 + count);
            sequenceDigits = sequenceDigits.substring(sequenceDigits.length() - sequenceNumberLength);
            return filenamePrefix + sequenceDigits + filenameSuffix;
        }
    }

}
