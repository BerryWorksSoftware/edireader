/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
 */

package com.berryworks.edireader;

import com.berryworks.edireader.plugin.CompositeAwarePlugin;
import com.berryworks.edireader.plugin.LoopDescriptor;
import com.berryworks.edireader.plugin.PluginControllerImpl;
import com.berryworks.edireader.tokenizer.EDITokenizer;
import com.berryworks.edireader.tokenizer.Tokenizer;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.Set;

import static com.berryworks.edireader.Plugin.ANY_CONTEXT;
import static org.junit.Assert.*;

public class PluginTest {

    private static final LoopDescriptor[] LOOP_DESCRIPTORS = new LoopDescriptor[]{
            new LoopDescriptor("A", "seg1", 1, ANY_CONTEXT),
            new LoopDescriptor("*", "N1", 1, "N1"),
            new LoopDescriptor("LX", "LX", 1, ANY_CONTEXT),
            new LoopDescriptor("N1", "N1", 2, "LX"),
            new LoopDescriptor("*", "P1", 1, ANY_CONTEXT),
            new LoopDescriptor("L5", "L5", 2, ANY_CONTEXT),
            new LoopDescriptor("L1", "L1", 3, ANY_CONTEXT),
            new LoopDescriptor("*", "L3", 0, ANY_CONTEXT)
    };
    private Plugin plugin;
    private Plugin.PluginDiff diff;
    private LoopDescriptor response;
    private Set<String> resultFlags;

    @Before
    public void setUp() {
        plugin = new MockPlugin();
    }

    @Test
    public void testConstruction() {
        assertEquals("dt", plugin.getDocumentType());
        assertEquals("dn", plugin.getDocumentName());
        assertFalse(plugin.isValidating());
        assertEquals(
                "Plugin com.berryworks.edireader.PluginTest$MockPlugin\n  dn (dt)",
                plugin.toString());
        int countOfPluginsConstructed = Plugin.getCount();
        assertTrue(countOfPluginsConstructed > 0);

        plugin = new Plugin("T", "N", true) {
        };
        assertEquals("T", plugin.getDocumentType());
        assertEquals("N", plugin.getDocumentName());
        assertTrue(plugin.isValidating());
        plugin.loops = LOOP_DESCRIPTORS;
        assertEquals(
                "Plugin com.berryworks.edireader.PluginTest$1\n" +
                        "  N (T)\n" +
                        "loop A at nesting level 1: encountering segment seg1 anytime\n" +
                        "loop * at nesting level 1: encountering segment N1 while currently in loop N1\n" +
                        "loop LX at nesting level 1: encountering segment LX anytime\n" +
                        "loop N1 at nesting level 2: encountering segment N1 while currently in loop LX\n" +
                        "loop * at nesting level 1: encountering segment P1 anytime\n" +
                        "loop L5 at nesting level 2: encountering segment L5 anytime\n" +
                        "loop L1 at nesting level 3: encountering segment L1 anytime\n" +
                        "loop * at nesting level 0: encountering segment L3 anytime",
                plugin.toString());
        assertEquals(countOfPluginsConstructed + 1, Plugin.getCount());
    }

    @Test
    public void canSetLoopDescriptors() {
        assertNull(plugin.getLoopDescriptors());

        plugin.loops = LOOP_DESCRIPTORS;
        assertEquals(8, plugin.getLoopDescriptors().length);
    }

    @Test
    public void canConcatenateLoopDescriptors() {
        plugin.loops = LOOP_DESCRIPTORS;
        assertEquals(8, plugin.getLoopDescriptors().length);

        LoopDescriptor[] more = new LoopDescriptor[]{
                new LoopDescriptor("X", "x"),
                new LoopDescriptor("Y", "y")
        };
        LoopDescriptor[] concatenated = plugin.concatenate(LOOP_DESCRIPTORS, more);
        assertEquals(10, concatenated.length);
    }

    @Test
    public void canCreateController() {
        Tokenizer tokenizer = new EDITokenizer(new StringReader(""), null);
        PluginControllerImpl controller = plugin.createController("SomeStandard", tokenizer);
        assertNotNull(controller);
    }

    @Test
    public void canGetDocumentVersionFromPluginName() {
        assertNull(plugin.getDocumentVersion());

        assertEquals("Four", new Plugin_Two_Three_Four().getDocumentVersion());
    }

    @Test
    public void testQueries() {
        plugin.loops = LOOP_DESCRIPTORS;
        plugin.prepare();

        response = plugin.query("seg1", null, -1, resultFlags);
        assertNotNull(response);
        assertEquals("A", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("seg1", "A", 1, resultFlags);
        assertNotNull(response);
        assertEquals("A", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("LX", null, 0, resultFlags);
        assertNotNull(response);
        assertEquals("LX", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("N1", "LX", 1, resultFlags);
        assertNotNull(response);
        assertEquals("N1", response.getName());
        assertEquals(2, response.getNestingLevel());

        response = plugin.query("L1", "N1", 2, resultFlags);
        assertNotNull(response);
        assertEquals("L1", response.getName());
        assertEquals(3, response.getNestingLevel());

        response = plugin.query("L1", "L1", 3, resultFlags);
        assertNotNull(response);
        assertEquals("L1", response.getName());
        assertEquals(3, response.getNestingLevel());
    }

    @Test
    public void testLevelSensitive() {
        plugin.loops = new LoopDescriptor[]{
                new LoopDescriptor("A", "seg1", 1, ANY_CONTEXT),
                new LoopDescriptor("B", "seg2", 2, "A"),
                new LoopDescriptor("C", "seg3", 3, 2),
        };
        plugin.prepare();

        response = plugin.query("seg1", null, 0, resultFlags);
        assertNotNull(response);
        assertEquals("A", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("seg2", "A", 1, resultFlags);
        assertNotNull(response);
        assertEquals("B", response.getName());
        assertEquals(2, response.getNestingLevel());

        response = plugin.query("seg3", "B", 2, resultFlags);
        assertNotNull(response);
        assertEquals("C", response.getName());
        assertEquals(3, response.getNestingLevel());

        response = plugin.query("seg3", "C", 3, resultFlags);
        assertNull(response);
    }

    @Test
    public void testStackContextQuery() {
        plugin.loops = new LoopDescriptor[]{
                new LoopDescriptor("A", "seg1", 1),
                new LoopDescriptor("B", "seg2", 2, "/A"),
                new LoopDescriptor("C", "seg3", 3, "/A/B"),
        };
        plugin.prepare();

        response = plugin.query("seg1", null, 0, resultFlags);
        assertNotNull(response);
        assertEquals("A", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("seg2", "/A", 1, resultFlags);
        assertNotNull(response);
        assertEquals("B", response.getName());
        assertEquals(2, response.getNestingLevel());

        response = plugin.query("seg3", "/A/B", 2, resultFlags);
        assertNotNull(response);
        assertEquals("C", response.getName());
        assertEquals(3, response.getNestingLevel());

        response = plugin.query("seg3", "C", 3, resultFlags);
        assertNull(response);
    }

    @Test
    public void testNullLoopNameActsAsANotRule() {
        // Baseline
        plugin.loops = new LoopDescriptor[]{
                new LoopDescriptor("*", "N1", 1, "N1"),
                new LoopDescriptor("LX", "LX", 1, ANY_CONTEXT),
                new LoopDescriptor("N1", "N1", 2, "LX")
        };
        plugin.prepare();

        response = plugin.query("LX", null, 0, resultFlags);
        assertNotNull(response);
        assertEquals("LX", response.getName());
        assertEquals(1, response.getNestingLevel());

        response = plugin.query("N1", "LX", 1, resultFlags);
        assertNotNull(response);
        assertEquals("N1", response.getName());
        assertEquals(2, response.getNestingLevel());

        // Same as baseline, but with null loop name
        plugin.loops = new LoopDescriptor[]{
                new LoopDescriptor("*", "N1", 1, "N1"),
                new LoopDescriptor("LX", "LX", 1, ANY_CONTEXT),
                new LoopDescriptor(null, "N1", 2, "LX")
        };
        plugin.prepare();

        response = plugin.query("LX", null, 0, resultFlags);
        assertNotNull(response);
        assertEquals("LX", response.getName());
        assertEquals(1, response.getNestingLevel());

        assertNull(plugin.query("N1", "LX", 1, resultFlags));
    }

    @Test
    public void queryWithNullLoopsReturnsNull() {
        plugin.prepare();
        assertNull(plugin.query("seg1", null, -1, resultFlags));
    }


    @Test(expected = RuntimeException.class)
    public void mustPrepareOptimizedForm() {
        plugin.loops = LOOP_DESCRIPTORS;
        plugin.query("seg1", null, -1, resultFlags);
    }


    @Test
    public void comparePluginToItself() {
        final CompositeAwarePlugin pluginA =
                new CompositeAwarePlugin("210", "Motor Carrier Freight Details and Invoice") {
                };
        diff = pluginA.compare(pluginA);
        assertTrue(diff.isMatch());
        assertEquals("matches", diff.getReason());
    }

    @Test
    public void comparePluginToNull() {
        final CompositeAwarePlugin pluginA =
                new CompositeAwarePlugin("210", "Motor Carrier Freight Details and Invoice") {
                };
        diff = pluginA.compare(null);
        assertFalse(diff.isMatch());
        assertEquals("second plugin is null", diff.getReason());
    }

    @Test
    public void comparedTypesMustMatch() {
        final CompositeAwarePlugin pluginA =
                new CompositeAwarePlugin("210", "Motor Carrier Freight Details and Invoice") {
                };
        final CompositeAwarePlugin pluginB =
                new CompositeAwarePlugin("211", "Motor Carrier Freight Details and Invoice") {
                };
        diff = pluginA.compare(pluginB);
        assertFalse(diff.isMatch());
        assertEquals("types differ", diff.getReason());
    }

    @Test
    public void comparedNamesMustMatch() {
        final CompositeAwarePlugin pluginA =
                new CompositeAwarePlugin("210", "Motor Carrier Freight Details and Invoice") {
                };
        final CompositeAwarePlugin pluginB =
                new CompositeAwarePlugin("210", "Motor Carrier Freight Details & Invoice") {
                };
        diff = pluginA.compare(pluginB);
        assertFalse(diff.isMatch());
        assertEquals("names differ", diff.getReason());
    }

    @Test
    public void comparedDescriptorCountMustMatch() {
        diff = new PluginA1().compare(new PluginA2());
        assertFalse(diff.isMatch());
        assertEquals("second plugin has a different number of loops than this plugin", diff.getReason());
    }

    private static class MockPlugin extends Plugin {
        public MockPlugin() {
            super("dt", "dn");
        }
    }

    private static class Plugin_Two_Three_Four extends Plugin {

        public Plugin_Two_Three_Four() {
            super("dt", "dn");
        }

        public void setDescriptors(LoopDescriptor[] loops) {
            this.loops = loops;
        }

    }

    static class PluginA1 extends CompositeAwarePlugin {
        public PluginA1() {
            super("type", "name");
            loops = new LoopDescriptor[]{
                    new LoopDescriptor(CURRENT, "B3", 0, ANY_CONTEXT),
            };
        }
    }

    static class PluginA2 extends CompositeAwarePlugin {
        public PluginA2() {
            super("type", "name");
            loops = new LoopDescriptor[]{
                    new LoopDescriptor(CURRENT, "B3", 0, ANY_CONTEXT),
                    new LoopDescriptor(CURRENT, "C2", 0, ANY_CONTEXT),
            };
        }
    }

}
