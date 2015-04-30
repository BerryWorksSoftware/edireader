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

package com.berryworks.edireader;

import java.util.HashMap;
import java.util.Map;

/**
 * Data structure that associates leading character sequences with specific parser implementations.
 * When an EDI or EDI-like stream of data is to be parsed without pre-knowledge of which particular
 * EDI standard is to be used, this parser registry is used to select a parser based on the initial
 * characters of data.
 * <p/>
 * The parsers for ANSI X.12 and UN/EDIFACT are included in the registry be default. The classes
 * that implement these parsers are provided by the core EDIReader framework.
 * <p/>
 * Parsers for other formats, including HL7, ACH, and NSF, are also listed in the registry.
 * The classes that implement these formats are optional modules not included in the core EDIReader framework.
 * If an optional parser module is present in the classpath, the registry is therefore able to
 * select and load the appropriate parser in response to the leading character sequences in the data.
 * <p/>
 * It is also possible for a developer to implement a parser for an EDI-like data format and register that
 * parser along with the leading data characters which signal the instance of an interchange of that format.
 * In this way, the EDIReader framework can be extended to parse previously unsupported data formats in the same
 * way that it supports X12 and EDIFACT.
 */
public class ParserRegistry {

    private static final Map<String, Class> builtinClass = new HashMap<>();
    private static final Map<String, String>
            registeredClassNames = new HashMap<>();

    static {
        new ParserRegistry();
    }

    private ParserRegistry() {
        builtinClass.put("ISA", AnsiReader.class);
        builtinClass.put("UNA", EdifactReaderWithCONTRL.class);
        builtinClass.put("UNB", EdifactReaderWithCONTRL.class);
        builtinClass.put("UNH", UNHReader.class);
        registeredClassNames.put("UNH",
                "com.berryworks.edireader.amadeus.AmadeusReader");
        registeredClassNames.put("MSH",
                "com.berryworks.edireader.hl7.HL7Reader");
        registeredClassNames.put("STX",
                "com.berryworks.edireader.tradacoms.TradacomsReader");
        registeredClassNames.put("1",
                "com.berryworks.edireader.ach.ACHReader");
        registeredClassNames.put("AA0",
                "com.berryworks.edireader.nsf.NSFReader");
    }

    /**
     * Returns an instance of some EDIReader subclass based on the first
     * several chars of data to be parsed.
     * <p/>
     * Parsers for ANSI X12 and UN/EDIFACT are built-in. Other parsers can be registered, including
     * custom parsers developed by users. Parsers registered via register() are considered first for
     * a match with the incoming data before the built-in parsers are considered, allowing users to
     * provide custom implementations of X12 and EDIFACT parsers if needed.
     *
     * @param firstChars of data to be parsed
     * @return subclass of EDIReader that knows how to parse the data, or null if no parser is available
     */

    public static EDIReader get(String firstChars) {
        EDIReader result = null;
        Class parserClass;

        // See if a suitable registered class name is recognized by these
        // firstChars
        String name = (String) getMatch(firstChars, registeredClassNames);
        if (name != null) {
            try {
                parserClass = Class.forName(name);
                result = (EDIReader) parserClass.newInstance();
            } catch (Exception ignore) {
            }
        }

        // If not, see if there is a builtin class that matches
        if (result == null) {
            parserClass = (Class) getMatch(firstChars, builtinClass);
            if (parserClass != null) {
                try {
                    result = (EDIReader) parserClass.newInstance();
                } catch (Exception ignore) {
                }
            }
        }

        // If still nothing, return the "catch all" parser if there is one
        if (result == null) {
            name = registeredClassNames.get("");
            if (name != null) {
                try {
                    parserClass = Class.forName(name);
                    result = (EDIReader) parserClass.newInstance();
                } catch (Exception ignore) {
                }
            }
        }

        return result;
    }

    /**
     * Registers a parser and associates it with the leading data characters that signal an instance of an interchange
     * supported by the parser.
     *
     * @param firstChars of data to be parsed
     * @param className  fully qualified classname of an EDIReader subclass
     */
    public static void register(String firstChars, String className) {
        registeredClassNames.put(firstChars, className);
    }

    private static Object getMatch(String firstChars, Map map) {
        Object result = null;
        for (int n = firstChars.length(); result == null && n > 0; firstChars = firstChars.substring(0, --n)) {
            result = map.get(firstChars);
        }

        return result;
    }

}
