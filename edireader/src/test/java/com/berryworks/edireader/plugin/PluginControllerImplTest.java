package com.berryworks.edireader.plugin;

import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.Plugin;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PluginControllerImplTest {

    private PluginControllerImpl controller;
    private Plugin plugin;

    @Before
    public void setUp() {
        controller = new PluginControllerImpl("TestStandard", null);
//        PluginControllerImpl.setDebug(true);
    }

    @Test
    public void basics() throws EDISyntaxException {
        plugin = new ANSI_210();
        plugin.prepare();
        controller.setPlugin(plugin);
        assertSame(plugin, controller.getPlugin());
        controller.setEnabled(true);
        assertTrue(controller.isEnabled());
        assertEquals("Motor Carrier Freight Details and Invoice", controller.getDocumentName());
        assertNull(controller.getDocumentType());
        controller.setDocumentType("Document Type");
        assertEquals("Document Type", controller.getDocumentType());
    }

    @Test
    public void canTransitionThrough210() throws EDISyntaxException {
        plugin = new ANSI_210();
        plugin.prepare();
        controller.setPlugin(plugin);
        controller.setEnabled(true);
        assertTrue(controller.isEnabled());
        assertEquals("Motor Carrier Freight Details and Invoice", controller.getDocumentName());
        assertNull(controller.getDocumentType());

        assertFalse(controller.transition("B3"));
        assertTransition("N1", 1, "N1", "/N1", 0);
        assertFalse(controller.transition("N3"));
        assertFalse(controller.transition("N4"));
        assertTransition("N1", 1, "N1", "/N1", 1);
        assertFalse(controller.transition("N3"));
        assertFalse(controller.transition("N4"));
        assertTransition("N1", 1, "N1", "/N1", 1);
        assertFalse(controller.transition("N3"));
        assertFalse(controller.transition("N4"));
        assertTransition("LX", 1, "LX", "/LX", 1);
        assertFalse(controller.transition("L5"));
        assertFalse(controller.transition("L0"));
        assertFalse(controller.transition("L7"));
        assertTransition("LX", 1, "LX", "/LX", 1);
        assertFalse(controller.transition("L5"));
        assertFalse(controller.transition("L0"));
        assertFalse(controller.transition("L1"));
        assertFalse(controller.transition("L7"));
        assertTransition("LX", 1, "LX", "/LX", 1);
        assertFalse(controller.transition("L1"));
        assertTransition("LX", 1, "LX", "/LX", 1);
        assertFalse(controller.transition("L1"));
        assertTrue(controller.transition("L3"));
    }

    private void assertTransition(String segment, int nestingLevel, String loopEntered, String loopStack, int closedCount) throws EDISyntaxException {
        assertTrue(controller.transition(segment));
        assertEquals(nestingLevel, controller.getNestingLevel());
        assertEquals(loopEntered, controller.getLoopEntered());
        assertEquals(loopStack, controller.getLoopStack().toString());
        assertEquals(closedCount, controller.closedCount());
        assertFalse(controller.isResumed());
    }

    @Test
    public void pluginMustBeEnabled() throws EDISyntaxException {
        plugin = new ANSI_210();
        plugin.prepare();
        controller.setPlugin(plugin);
        controller.setEnabled(false);

        assertFalse(controller.transition("B3"));
        assertFalse(controller.transition("N1"));
    }

}
