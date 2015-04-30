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

package com.berryworks.edireader.formatter;

import java.io.*;

/**
 * A utility program that produces a line-oriented listing of the segments of an
 * EDI interchange. The listing is indented to reflect the individual documents
 * nested within functional groups, which in turn are nested with the
 * interchange. If a plugin is available for a document, the segment groups
 * nested within the document are shown via indenting as well.
 */
public class Formatter {

    protected Reader input;
    protected PrintWriter output;
    private String ediFilename;

    public Formatter(String inputFilename, String outputFilename) {
        if (inputFilename != null) {
            try {
                input = new FileReader(inputFilename);
            } catch (IOException e) {
                System.err
                        .println("Unable to open input file " + inputFilename);
                throw new RuntimeException(e.getMessage());
            }
        } else {
            input = new InputStreamReader(System.in);
        }

        if (outputFilename != null) {
            try {
                output = new PrintWriter(new FileWriter(outputFilename));
            } catch (IOException e) {
                System.err.println("Unable to open output file "
                        + outputFilename);
                throw new RuntimeException(e.getMessage());
            }
        } else {
            output = new PrintWriter(System.out, true);
        }

    }

    public Formatter(Reader input, PrintWriter output) {
        this.input = input;
        this.output = output;
    }

    public int format() throws IOException {
        // Pipe A provides a way to read the data directly
        PipedReader pipedReaderA = new PipedReader();
        PipedWriter pipedWriterA = new PipedWriter(pipedReaderA);
        // Pipe B feeds an EDIReader to parse the data as EDI
        PipedReader pipedReaderB = new PipedReader();
        PipedWriter pipedWriterB = new PipedWriter(pipedReaderB);
        FormatterParser parser = createFormatterParser(pipedReaderA,
                pipedReaderB, output);
        Thread thread = new Thread(parser);
        thread.start();

        int count = tee(input, pipedWriterA, pipedWriterB);

        try {
            thread.join();
        } catch (InterruptedException e) {
            // ignore
        }

        return count;

    }

    protected int tee(Reader source, Writer destinationA, Writer destinationB)
            throws IOException {
        char buffer[] = new char[1000];
        int count = 0;
        for (; ; ) {
            int n = source.read(buffer);
            if (n > 0) {
                try {
                    destinationB.write(buffer, 0, n);
                    destinationB.flush();
                    Thread.yield();
                    destinationA.write(buffer, 0, n);
                    destinationA.flush();
                } catch (IOException e) {
                    // the thread that is reading from the pipes
                    // probably closed one or both of them due to a
                    // parsing error in the EDI data.
                    System.out.println(getClass().getName() + ": caught "
                            + e.getMessage());
                    break;
                }
            } else if (n < 0) {
                destinationB.close();
                destinationA.close();
                break;
            }
            Thread.yield();
            count += n;
        }
        return count;
    }

    protected FormatterParser createFormatterParser(Reader pipedReaderA,
                                                    Reader pipedReaderB, PrintWriter out) {
        return new FormatterParser(pipedReaderA, pipedReaderB, out);
    }

    protected String getFilename() {
        return ediFilename;
    }

    public void setFilename(String filename) {
        ediFilename = filename;
    }

    public static void main(String args[]) {
        String outputFileName = null;
        String inputFileName = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-o")) {
                if ((++i) >= args.length)
                    badArgs();
                outputFileName = args[i];
            } else
                inputFileName = args[i];
        }

        Formatter formatter = new Formatter(inputFileName, outputFileName);
        try {
            formatter.format();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static void badArgs() {
        System.out.println("Usage: Formatter [-o outputfile] [inputfile]");
        throw new RuntimeException("Missing or invalid command line arguments");
    }

}