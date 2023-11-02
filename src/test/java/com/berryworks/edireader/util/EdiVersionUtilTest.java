package com.berryworks.edireader.util;

import org.junit.Test;

import static com.berryworks.edireader.util.EdiVersionUtil.isX12VersionBefore;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EdiVersionUtilTest {

    @Test
    public void canCompareGS07Versions() {
        // Some true cases
        assertTrue(isX12VersionBefore("004010", 4030));
        assertTrue(isX12VersionBefore("004010ABC", 4030));
        assertTrue(isX12VersionBefore("004010", 4020));
        assertTrue(isX12VersionBefore("004010", 5000));
        assertTrue(isX12VersionBefore("004010", 4011));
        assertTrue(isX12VersionBefore("003070", 4000));

        // Some false cases
        assertFalse(isX12VersionBefore("004010", 4010));
        assertFalse(isX12VersionBefore("005010", 5010));
        assertFalse(isX12VersionBefore("003020", 3010));
        assertFalse(isX12VersionBefore("004000", 3070));

        // Some edge cases
        assertFalse(isX12VersionBefore("00401", 4010));
        assertFalse(isX12VersionBefore("00401x", 4010));
        assertFalse(isX12VersionBefore("", 4010));
        assertFalse(isX12VersionBefore(null, 4010));

    }
}
