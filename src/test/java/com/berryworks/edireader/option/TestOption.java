/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader.option;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestOption {

    private Options options;

    @Test
    public void testDefaults() {
        options = new MyOptions();

        assertFalse(options.isSelected(Option.ELEMENT_TRIM_SUPPRESSION));
        assertFalse(options.isSelected(Option.OPTION_B));
        assertFalse(options.isSelected(Option.OPTION_C));
        assertFalse(options.isSupported(Option.ELEMENT_TRIM_SUPPRESSION));
        assertFalse(options.isSupported(Option.OPTION_B));
        assertFalse(options.isSupported(Option.OPTION_C));
    }

    @Test
    public void testSelect() {
        options = new MyOptions();

        options.select(Option.ELEMENT_TRIM_SUPPRESSION);
        assertTrue(options.isSelected(Option.ELEMENT_TRIM_SUPPRESSION));
        assertFalse(options.isSelected(Option.OPTION_B));
        assertFalse(options.isSelected(Option.OPTION_C));
    }

    @Test
    public void testDeSelect() {
        options = new MyOptions();

        options.select(Option.ELEMENT_TRIM_SUPPRESSION);
        options.select(Option.OPTION_B);

        options.deSelect(Option.OPTION_B);
        options.deSelect(Option.OPTION_C);

        assertTrue(options.isSelected(Option.ELEMENT_TRIM_SUPPRESSION));
        assertFalse(options.isSelected(Option.OPTION_B));
        assertFalse(options.isSelected(Option.OPTION_C));
    }

    private static class MyOptions extends Options {
    }

}
