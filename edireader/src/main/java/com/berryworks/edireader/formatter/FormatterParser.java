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

package com.berryworks.edireader.formatter;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import com.berryworks.edireader.EDISyntaxException;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;

/**
 * Controller used by the Formatter utility to run in a separate thread
 * and conduct the work of an EDIReader and a FormatterHandler.
 */
public class FormatterParser implements Runnable {

    protected final Reader inputA;
    protected final Reader inputB;
    protected final PrintWriter output;

    protected String filename;

    public FormatterParser(Reader inputA, Reader inputB, PrintWriter output, String filename) {
        this(inputA, inputB, output);
        this.filename = filename;
    }

    public FormatterParser(Reader inputA, Reader inputB, PrintWriter output) {
        this.inputA = inputA;
        this.inputB = inputB;
        this.output = output;
    }

    public void run() {
        // Feed inputB through an EDIReader, and use its call-back
        // results to direct the reading of inputA, which is expected
        // to be an exact copy of inputB.
        InputSource inputSource = new InputSource(inputB);
        EDIReader ediReader;
        FormatterHandler handler;

        try {
            ediReader = createEDIReader(inputSource);
            if (ediReader == null) {
                throw new RuntimeException("createEDIReader returned null");
            }
            log(ediReader);
            handler = createFormatterHandler(ediReader, inputA, output);
            try {
                handler.preface();
                ediReader.setContentHandler(handler);
                ediReader.parse(inputSource);
                handler.addendum();
            } catch (Exception e) {
                handler.recover(e);
            }
        } catch (Exception e) {
            errorReport(e);
        }

        try {
            if (inputA != null) inputA.close();
            inputB.close();
        } catch (IOException e1) {
            // ignore
        }
    }

    protected EDIReader createEDIReader(InputSource inputSource) throws EDISyntaxException, IOException {
        return EDIReaderFactory.createEDIReader(inputSource);
    }

    protected void log(EDIReader parser) {
    }

    protected void errorReport(Exception e) {
        output.println(e.getMessage());
    }

    protected FormatterHandler createFormatterHandler(EDIReader parser,
                                                      Reader input, PrintWriter out) {
        return new FormatterHandler(parser, input, out);
    }

}