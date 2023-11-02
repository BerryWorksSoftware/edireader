package com.berryworks.edireader.util;

import static com.berryworks.edireader.util.FixedLength.isPresent;

public class EdiVersionUtil {
    public static boolean isX12VersionBefore(String gs07, int versionAsInt) {
        if (!isPresent(gs07) || gs07.length() < 6) return false;
        try {
            return Integer.parseInt(gs07.substring(0, 6)) < versionAsInt;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
