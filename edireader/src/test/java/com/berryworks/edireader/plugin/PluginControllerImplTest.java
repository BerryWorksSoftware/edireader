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

        assertEquals("Motor Carrier Freight Details and Invoice", controller.getDocumentName());

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

    @Test
    public void canTransitionThrough850WithDifficultAMT() throws EDISyntaxException {
        plugin = new My_850();
        plugin.prepare();
        controller.setPlugin(plugin);
        controller.setEnabled(true);

        assertEquals("Purchase Order", controller.getDocumentName());

        assertFalse(controller.transition("BEG"));
        assertFalse(controller.transition("CUR"));
        assertFalse(controller.transition("FOB"));
        assertFalse(controller.transition("ITD"));
        assertFalse(controller.transition("DTM"));
        assertFalse(controller.transition("DTM"));
        assertFalse(controller.transition("TD5"));
        assertTransition("AMT", 1, "AMT-0200", "/AMT-0200", 0);
        assertTransition("AMT", 1, "AMT-0200", "/AMT-0200", 1);
        assertTransition("N9", 1, "N9", "/N9", 1);
        assertTransition("N9", 1, "N9", "/N9", 1);
        assertTransition("N9", 1, "N9", "/N9", 1);
        assertTransition("N9", 1, "N9", "/N9", 1);
        assertTransition("N1", 1, "N1", "/N1", 1);
        assertTransition("N1", 1, "N1", "/N1", 1);
        assertFalse(controller.transition("N2"));
        assertFalse(controller.transition("N3"));
        assertFalse(controller.transition("N4"));
        assertTransition("PO1", 1, "PO1-0700", "/PO1-0700", 1);
        assertTransition("PID", 2, "PID", "/PO1-0700/PID", 0);
        assertFalse(controller.transition("N9*"));
        assertFalse(controller.transition("MSG"));
        assertTransition("PO1", 1, "PO1-0700", "/PO1-0700", 2);
        assertFalse(controller.transition("PI"));
        assertTransition("N9", 2, "N9", "/PO1-0700/N9", 0);
        assertFalse(controller.transition("MSG"));
        assertTransition("CTT", 1, "CTT", "/CTT", 2);
//        assertTransition("AMT", 1, "?", "?", 1);
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

    class My_850 extends ANSI_850 {
        public My_850() {

            loops = new LoopDescriptor[]{
                    new LoopDescriptor(null, "ADV", 2, "/PO1-0700/SLN"),
                    new LoopDescriptor("ADV", "ADV", 1, "*"),

                    new LoopDescriptor("AMT", "AMT", 2, "/PO1-0700"),
                    new LoopDescriptor(null, "AMT", 1, "/CTT"),
                    new LoopDescriptor("AMT-0200", "AMT", 1, "*"),

                    new LoopDescriptor(".", "BEG", 0, "*"),

                    new LoopDescriptor("CB1", "CB1", 2, "/SPI"),

                    new LoopDescriptor(null, "CN1", 1, "/PO1-0700"),

                    new LoopDescriptor(null, "CSH", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "CSH", 0, "*"),

                    new LoopDescriptor(null, "CTB", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "CTB", 0, "*"),

                    new LoopDescriptor(null, "CTP", 3, "/PO1-0700/SLN/SAC"),
                    new LoopDescriptor(null, "CTP", 2, "/PO1-0700/SLN"),
                    new LoopDescriptor(null, "CTP", 2, "/PO1-0700/SAC"),
                    new LoopDescriptor("CTP", "CTP", 2, "/PO1-0700"),
                    new LoopDescriptor(".", "CTP", 0, "*"),

                    new LoopDescriptor("CTT", "CTT", 1, "*"),

                    new LoopDescriptor(null, "CUR", 3, "/PO1-0700/SLN/SAC"),
                    new LoopDescriptor(null, "CUR", 2, "/PO1-0700/CTP"),
                    new LoopDescriptor(null, "CUR", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "CUR", 1, "/SAC"),
                    new LoopDescriptor(".", "CUR", 0, "*"),

                    new LoopDescriptor(null, "DIS", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "DIS", 0, "*"),

                    new LoopDescriptor(null, "DTM", 3, "/PO1-0700/SLN/N9"),
                    new LoopDescriptor(null, "DTM", 2, "/SPI/CB1"),
                    new LoopDescriptor(null, "DTM", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "DTM", 1, "/AMT"),
                    new LoopDescriptor(null, "DTM", 1, "/ADV"),
                    new LoopDescriptor(null, "DTM", 1, "/SPI"),
                    new LoopDescriptor(null, "DTM", 1, "/N9"),
                    new LoopDescriptor(".", "DTM", 0, "*"),

                    new LoopDescriptor("FA1", "FA1", 2, "/AMT"),

                    new LoopDescriptor(null, "FA2", 2, "/AMT/FA1"),

                    new LoopDescriptor(null, "FOB", 2, "/PO1-0700/N1"),
                    new LoopDescriptor(null, "FOB", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "FOB", 1, "/N1"),
                    new LoopDescriptor(".", "FOB", 0, "*"),

                    new LoopDescriptor(null, "G61", 2, "/SPI/N1"),

                    new LoopDescriptor(null, "INC", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "INC", 0, "*"),

                    new LoopDescriptor(null, "IT3", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "IT8", 1, "/PO1-0700"),

                    new LoopDescriptor(null, "ITD", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "ITD", 0, "*"),

                    new LoopDescriptor("LDT", "LDT", 3, "/PO1-0700/N1"),
                    new LoopDescriptor(null, "LDT", 2, "/SPI/CB1"),
                    new LoopDescriptor("LDT", "LDT", 2, "/PO1-0700"),
                    new LoopDescriptor(".", "LDT", 0, "*"),

                    new LoopDescriptor(".", "LE", 1, "/PO1-0700"),

                    new LoopDescriptor(null, "LIN", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "LIN", 0, "*"),

                    new LoopDescriptor("LM", "LM", 3, "/PO1-0700/LDT"),
                    new LoopDescriptor("LM", "LM", 2, "/PO1-0700"),
                    new LoopDescriptor("LM", "LM", 1, "*"),

                    new LoopDescriptor(null, "LQ", 3, "/PO1-0700/LDT/LM"),
                    new LoopDescriptor(null, "LQ", 2, "/PO1-0700/LM"),
                    new LoopDescriptor(null, "LQ", 1, "/LM"),

                    new LoopDescriptor(".", "LS", 1, "/PO1-0700"),

                    new LoopDescriptor(null, "MAN", 3, "/PO1-0700/N1/LDT"),
                    new LoopDescriptor(null, "MAN", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "MAN", 0, "*"),

                    new LoopDescriptor(null, "MEA", 2, "/PO1-0700/PID"),
                    new LoopDescriptor(null, "MEA", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "MEA", 0, "*"),

                    new LoopDescriptor(null, "MSG", 3, "/PO1-0700/N1/LDT"),
                    new LoopDescriptor(null, "MSG", 2, "/SPI/N1"),
                    new LoopDescriptor(null, "MSG", 1, "/N9"),

                    new LoopDescriptor(null, "MTX", 1, "/ADV"),

                    new LoopDescriptor("N1", "N1", 3, "/PO1-0700/SLN"),
                    new LoopDescriptor("N1", "N1", 2, "/PO1-0700"),
                    new LoopDescriptor("N1", "N1", 2, "/SPI"),
                    new LoopDescriptor("N1", "N1", 1, "*"),

                    new LoopDescriptor(null, "N2", 3, "/PO1-0700/SLN/N1"),
                    new LoopDescriptor(null, "N2", 2, "/SPI/N1"),
                    new LoopDescriptor(null, "N2", 1, "/N1"),

                    new LoopDescriptor(null, "N3", 3, "/PO1-0700/SLN/N1"),
                    new LoopDescriptor(null, "N3", 2, "/SPI/N1"),
                    new LoopDescriptor(null, "N3", 1, "/N1"),

                    new LoopDescriptor(null, "N4", 3, "/PO1-0700/SLN/N1"),
                    new LoopDescriptor(null, "N4", 2, "/SPI/N1"),
                    new LoopDescriptor(null, "N4", 1, "/N1"),

                    new LoopDescriptor("N9", "N9", 3, "/PO1-0700/SLN"),
                    new LoopDescriptor("N9", "N9", 2, "/PO1-0700"),
                    new LoopDescriptor("N9", "N9", 1, "*"),

                    new LoopDescriptor(null, "NX2", 3, "/PO1-0700/SLN/N1"),
                    new LoopDescriptor(null, "NX2", 2, "/PO1-0700/N1"),
                    new LoopDescriptor(null, "NX2", 1, "/N1"),

                    new LoopDescriptor(null, "PAM", 2, "/PO1-0700/SLN"),
                    new LoopDescriptor(".", "PAM", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "PAM", 0, "*"),

                    new LoopDescriptor(null, "PCT", 2, "/PO1-0700/AMT"),
                    new LoopDescriptor(null, "PCT", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "PCT", 1, "/AMT"),
                    new LoopDescriptor(".", "PCT", 0, "*"),

                    new LoopDescriptor(null, "PER", 3, "/PO1-0700/SLN/N1"),
                    new LoopDescriptor(null, "PER", 2, "/PO1-0700/N1"),
                    new LoopDescriptor(null, "PER", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "PER", 1, "/N1"),
                    new LoopDescriptor(".", "PER", 0, "*"),

                    new LoopDescriptor(null, "PID", 2, "/PO1-0700/SLN"),
                    new LoopDescriptor("PID", "PID", 2, "/PO1-0700"),
                    new LoopDescriptor(".", "PID", 0, "*"),

                    new LoopDescriptor(null, "PKG", 3, "/PO1-0700/N1"),
                    new LoopDescriptor("PKG", "PKG", 2, "/PO1-0700"),
                    new LoopDescriptor(null, "PKG", 1, "/N1"),
                    new LoopDescriptor(".", "PKG", 0, "*"),

                    new LoopDescriptor("PO1-0700", "PO1", 1, "*"),

                    new LoopDescriptor(null, "PO3", 2, "/PO1-0700/SLN"),
                    new LoopDescriptor(null, "PO3", 1, "/PO1-0700"),

                    new LoopDescriptor(null, "PO4", 2, "/PO1-0700/SLN"),
                    new LoopDescriptor(null, "PO4", 1, "/PO1-0700"),

                    new LoopDescriptor(".", "PWK", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "PWK", 0, "*"),

                    new LoopDescriptor(null, "QTY", 3, "/PO1-0700/N1/LDT"),
                    new LoopDescriptor("QTY", "QTY", 3, "/PO1-0700/SLN"),
                    new LoopDescriptor(null, "QTY", 3, "/PO1-0700/LDT"),
                    new LoopDescriptor(null, "QTY", 3, "/PO1-0700/N1"),
                    new LoopDescriptor("QTY", "QTY", 2, "/PO1-0700"),

                    new LoopDescriptor(null, "REF", 3, "/PO1-0700/N1/LDT"),
                    new LoopDescriptor(null, "REF", 2, "/SPI/N1"),
                    new LoopDescriptor(null, "REF", 1, "/SPI"),
                    new LoopDescriptor(null, "REF", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "REF", 1, "/AMT"),
                    new LoopDescriptor(null, "REF", 1, "/N1"),
                    new LoopDescriptor(".", "REF", 0, "*"),

                    new LoopDescriptor("SAC", "SAC", 3, "/PO1-0700/SLN"),
                    new LoopDescriptor("SAC", "SAC", 2, "/PO1-0700"),
                    new LoopDescriptor("SAC", "SAC", 1, "*"),

                    new LoopDescriptor(null, "SCH", 3, "/PO1-0700/N1"),
                    new LoopDescriptor("SCH", "SCH", 2, "/PO1-0700"),

                    new LoopDescriptor(null, "SDQ", 1, "/PO1-0700"),

                    new LoopDescriptor(".", "SE", 0, "*"),

                    new LoopDescriptor(null, "SI", 3, "/PO1-0700/SLN/QTY"),
                    new LoopDescriptor(null, "SI", 2, "/PO1-0700/QTY"),
                    new LoopDescriptor(null, "SI", 2, "/PO1-0700/SLN"),
                    new LoopDescriptor(null, "SI", 1, "/N1"),
                    new LoopDescriptor(".", "SI", 0, "*"),

                    new LoopDescriptor("SLN", "SLN", 2, "/PO1-0700"),

                    new LoopDescriptor(null, "SPI", 1, "/PO1-0700"),
                    new LoopDescriptor("SPI", "SPI", 1, "*"),

                    new LoopDescriptor(".", "ST", 0, "*"),

                    new LoopDescriptor(null, "TAX", 2, "/PO1-0700/SLN"),
                    new LoopDescriptor(null, "TAX", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "TAX", 0, "*"),

                    new LoopDescriptor(null, "TC2", 2, "/PO1-0700/SLN"),
                    new LoopDescriptor(null, "TC2", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "TC2", 0, "*"),

                    new LoopDescriptor(null, "TD1", 2, "/PO1-0700/SCH"),
                    new LoopDescriptor(null, "TD1", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "TD1", 1, "/N1"),
                    new LoopDescriptor(".", "TD1", 0, "*"),

                    new LoopDescriptor(null, "TD3", 2, "/PO1-0700/SCH"),
                    new LoopDescriptor(null, "TD3", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "TD3", 1, "/N1"),
                    new LoopDescriptor(".", "TD3", 0, "*"),

                    new LoopDescriptor(null, "TD4", 2, "/PO1-0700/SCH"),
                    new LoopDescriptor(null, "TD4", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "TD4", 1, "/N1"),
                    new LoopDescriptor(".", "TD4", 0, "*"),

                    new LoopDescriptor(null, "TD5", 2, "/PO1-0700/SCH"),
                    new LoopDescriptor(null, "TD5", 1, "/PO1-0700"),
                    new LoopDescriptor(null, "TD5", 1, "/N1"),
                    new LoopDescriptor(".", "TD5", 0, "*"),

                    new LoopDescriptor(null, "TXI", 1, "/PO1-0700"),
                    new LoopDescriptor(".", "TXI", 0, "*"),
            };
        }
    }
}
