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

package com.berryworks.edireader.util;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.berryworks.edireader.util.FixedLength.isPresent;

/**
 * Utility class to simplify some of the tedious aspects of interpreting
 * command line arguments.
 */
public class CommandLine {

    private final Map<String, String> optionMap = new HashMap<>();
    private final List<String> positionalArgs = new ArrayList<>();
    private PrintStream errStream = System.err;
    private boolean valid = true;

    public CommandLine(String[] args) {

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                String option = arg.substring(1);
                if (++i < args.length) {
                    String value = args[i];
                    optionMap.put(option, value);
                } else
                    badArgs("missing value for option " + option);
            } else
                positionalArgs.add(args[i]);
        }

    }

    public void setErrorOutputStream(PrintStream errStream) {
        this.errStream = errStream;
    }

    public void badArgs(String msg) {
        valid = false;
        if (errStream != null) {
            errStream.println("Invalid command line argument(s): " + msg);
            errStream.println("");
            errStream.println(usage());
        }
    }

    public String usage() {
        return "";
    }

    public int size() {
        return optionMap.size() + positionalArgs.size();
    }

    public boolean isOptionPresent(String arg) {
        return optionMap.get(arg) != null;
    }

    public String getOption(String arg) {
        return optionMap.get(arg);
    }

    public String getPosition(int i) {
        return positionalArgs.size() > i ? positionalArgs.get(i) : null;
    }

    public int getPositionAsInt(int i) {
        String value = getPosition(i);
        if (value == null)
            return -1;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            String msg = "Invalid integer argument at position " + i;
            badArgs(msg);
            throw new RuntimeException(msg);
        }
    }

    public String getPosition(int i, String defaultValue) {
        if (positionalArgs.size() > i) {
            String value = positionalArgs.get(i);
            return (isPresent(value)) ? value : defaultValue;
        } else {
            if (defaultValue == null) {
                String msg = "Required argument missing at position " + i;
                badArgs(msg);
                throw new RuntimeException(msg);
            } else {
                return defaultValue;
            }
        }
    }

    public int getAsInt(String option) {
        String value = getOption(option);
        if (value == null)
            return -1;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            String msg = "Invalid integer value for option " + value;
            badArgs(msg);
            throw new RuntimeException(msg);
        }
    }

    public boolean isValid() {
        return valid;
    }

}
