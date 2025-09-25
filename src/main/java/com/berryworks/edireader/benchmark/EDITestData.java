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

package com.berryworks.edireader.benchmark;

import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

public class EDITestData implements Runnable {

    private static final String ISA_FRAGMENT = "ISA~00~          ~00~          ~ZZ~04000          ~ZZ~58401          ~040714~1003~U~00204~000038449~1~P~<$";

    private static final String GS_FRAGMENT = "GS~AG~04000~58401~040714~1003~38327~X~002040CHRY$";

    private static final String A824_FRAGMENT = "ST~824~000042460$"
                                                + "BGN~11~07141005162~040714~1003$" + "N1~SU~~92~58401O$"
                                                + "N1~SF~~92~58401O$" + "N1~ST~~92~05304$" + "N1~MA~~92~05304$"
                                                + "OTI~IA~SI~AC32804~~~~~~~856$" + "REF~BM~AC32804$"
                                                + "REF~PK~00032804$" + "REF~PM~52069902AA$"
                                                + "DTM~009~040714~1003~ED$";

    private static final String B824_FRAGMENT = "DTM~011~040714$"
                                                + "OTI~IA~SI~AC32804~~~~~~~856$" + "REF~BM~AC32804$"
                                                + "REF~PK~00032804$" + "REF~PM~0CP00015$"
                                                + "DTM~009~040714~1003~ED$" + "DTM~011~040714$"
                                                + "OTI~IA~SI~AC32804~~~~~~~856$" + "REF~BM~AC32804$"
                                                + "REF~PK~00032804$" + "REF~PM~0CD03536$"
                                                + "DTM~009~040714~1003~ED$" + "DTM~011~040714$"
                                                + "OTI~IA~SI~AC32804~~~~~~~856$" + "REF~BM~AC32804$"
                                                + "REF~PK~00032804$" + "REF~PM~0CP00016$"
                                                + "DTM~009~040714~1003~ED$" + "DTM~011~040714$" + "SE~";

    private static final String FILLER_FRAGMENT = "DTM~009~040714~1003~ED$";

    private static final String DOCUMENT_CONTROL_NUMBER = "~000042460$";

    private static final String GROUP_CONTROL_NUMBER = "~38327$";

    private static final String INTERCHANGE_CONTROL_NUMBER = "~000038449$";

    private Writer writer;

    private int fillerSegments;

    public static InputSource getAnsiInputSource() {
        return new InputSource(new StringReader(getAnsiInterchange()));
    }

    private static String getAnsiISA() {
        return ISA_FRAGMENT;
    }

    private static String getAnsiGS() {
        return GS_FRAGMENT;
    }

    private static String getAnsiGE(int count) {
        return "GE~" + count + GROUP_CONTROL_NUMBER;
    }

    private static String getAnsiIEA(int count) {
        return "IEA~" + count + INTERCHANGE_CONTROL_NUMBER;
    }

    private static String getAnsi824PartA() {
        return A824_FRAGMENT;
    }

    private static String getAnsi824Filler() {
        return FILLER_FRAGMENT;
    }

    private static String getAnsi824PartB(int fillerSegments) {
        int segmentCount = 31 + fillerSegments;
        return B824_FRAGMENT + segmentCount + DOCUMENT_CONTROL_NUMBER;
    }

    public static String getAnsiInterchange() {
        return getAnsiInterchange(1);
    }

    public static String getAnsiInterchange(int numberOfDocuments) {
        return getAnsiInterchange(1, numberOfDocuments);
    }

    public static String getAnsiInterchange(int numberOfGroups, int numberOfDocuments) {
        StringBuilder result = new StringBuilder(getAnsiISA());
        for (int i = 0; i < numberOfGroups; i++) {
            result.append(getAnsiGS());
            for (int j = 0; j < numberOfDocuments; j++)
                result.append(getAnsi824PartA()).append(getAnsi824PartB(0));
            result.append(getAnsiGE(numberOfDocuments));
        }
        result.append(getAnsiIEA(numberOfGroups));
        return result.toString();
    }

    public static InputSource getEdifactInputSource() {
        return new InputSource(new StringReader(getEdifactInterchange()));
    }

    public static InputSource getEdifactInputSource(String type) {
        return new InputSource(new StringReader(getEdifactInterchange(type, "APR")));
    }

    public static String getEdifactInterchange() {
        return getEdifactInterchange(1);
    }

    public static String getEdifactInterchange(int numberOfMessages) {
        return getEdifactInterchange(numberOfMessages, "DCQCKI", "APR");
    }

    public static String getEdifactInterchange(String type) {
        return getEdifactInterchange(type, "APR");
    }

    public static String getEdifactInterchange(String type, String appRef) {
        return getEdifactInterchange(1, type, appRef);
    }

    public static String getEdifactInterchange(int numberOfMessage, String type, String appRef) {
        return getEdifactInterchange(numberOfMessage, type, "CONVTST2", appRef);
    }

    public static String getEdifactInterchange(String type, String recipientRef, String appRef) {
        return getEdifactInterchange(1, type, recipientRef, appRef);
    }

    public static String getEdifactInterchange(int numberOfMessages, String type, String recipientRef, String appRef) {
        StringBuilder result = new StringBuilder("UNB+IATA:1+REUAIR08DLH:PIMA+REUAGT82AGENT/LHR01:PIMA+941027:1520+841F60UNZ+" +
                                                 recipientRef + "+" + appRef
                                                 + "+L+1'");
        for (int n = 1; n <= numberOfMessages; n++) {
            result.append("UNH+").append(n).append("+").append(type).append(":90:1:IA+841F60'")
                    .append("""
                            LOR+SR:GVA'
                            FDQ+DL+573+890701+ATL+MIA++SR+120+8907011300+8907011655+ZRH+ATL'
                            PPD+MEIER+F:Y++BARBARAMRS+MILLER:JOHN'
                            PRD+Y'
                            PSD+N'
                            PBD+2:22'
                            """)
                    .append("UNT+8+").append(n).append("'");
        }
        result.append("UNZ+").append(numberOfMessages).append("+841F60UNZ'");
        return result.toString();
    }

    @Override
    public void run() {
        try {
            writer.write(getAnsiISA());
            writer.write(getAnsiGS());
            writer.write(getAnsi824PartA());
            for (int i = 0; i < fillerSegments; i++) {
                writer.write(getAnsi824Filler());
            }
            writer.write(getAnsi824PartB(fillerSegments));
            writer.write(getAnsiGE(1));
            writer.write(getAnsiIEA(1));
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception generating data: "
                                       + e);
        }
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    protected void setFillerSegments(int count) {
        fillerSegments = count;
    }


    public void setInterchangeKCs(int kcs) {
        // Determine number of chars in a generated interchange with zero filler
        // segments.
        int n = ISA_FRAGMENT.length() + GS_FRAGMENT.length()
                + A824_FRAGMENT.length();
        n += B824_FRAGMENT.length() + 6 + DOCUMENT_CONTROL_NUMBER.length();
        n += 4 + GROUP_CONTROL_NUMBER.length();
        n += 5 + INTERCHANGE_CONTROL_NUMBER.length();

        // Now estimate how many additional chars it will take to meet the
        // desired number of KCs
        int needed = kcs * 1024 - n;

        // Finally figure out how many filler segments it takes to form this
        // number of needed chars
        int f = needed / FILLER_FRAGMENT.length();
        setFillerSegments(Math.max(f, 0));
    }
}

