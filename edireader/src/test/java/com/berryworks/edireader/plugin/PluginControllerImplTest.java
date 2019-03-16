package com.berryworks.edireader.plugin;

import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.Plugin;
import com.berryworks.edireader.demo.EDItoXML;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.io.StringWriter;

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
    public void canLoadPluginFor110() throws EDISyntaxException {
        plugin = new ANSI_110();
        plugin.prepare();
        assertEquals(8, plugin.getLoopDescriptors().length);
        controller.setPlugin(plugin);
        assertSame(plugin, controller.getPlugin());
        controller.setEnabled(true);
        assertTrue(controller.isEnabled());
        assertEquals("Air Freight Details and Invoice", controller.getDocumentName());
        assertNull(controller.getDocumentType());
        controller.setDocumentType("Document Type");
        assertEquals("Document Type", controller.getDocumentType());
    }

    @Test
    public void canLoadPluginFor210() throws EDISyntaxException {
        plugin = new ANSI_210();
        plugin.prepare();
        assertEquals(38, plugin.getLoopDescriptors().length);
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
    public void canLoadPluginFor277() throws EDISyntaxException {
        plugin = new ANSI_277();
        plugin.prepare();
        assertEquals(16, plugin.getLoopDescriptors().length);
        controller.setPlugin(plugin);
        assertSame(plugin, controller.getPlugin());
        controller.setEnabled(true);
        assertTrue(controller.isEnabled());
        assertEquals("Health Care Claim Status Notification", controller.getDocumentName());
        assertNull(controller.getDocumentType());
        controller.setDocumentType("Document Type");
        assertEquals("Document Type", controller.getDocumentType());
    }

    @Test
    public void canLoadPluginFor810() throws EDISyntaxException {
        plugin = new ANSI_810();
        plugin.prepare();
        assertEquals(85, plugin.getLoopDescriptors().length);
        controller.setPlugin(plugin);
        assertSame(plugin, controller.getPlugin());
        controller.setEnabled(true);
        assertTrue(controller.isEnabled());
        assertEquals("Invoice", controller.getDocumentName());
        assertNull(controller.getDocumentType());
        controller.setDocumentType("Document Type");
        assertEquals("Document Type", controller.getDocumentType());
    }

    @Test
    public void canLoadPluginFor856() throws EDISyntaxException {
        plugin = new ANSI_856();
        plugin.prepare();
        assertEquals(3, plugin.getLoopDescriptors().length);
        controller.setPlugin(plugin);
        assertSame(plugin, controller.getPlugin());
        controller.setEnabled(true);
        assertTrue(controller.isEnabled());
        assertEquals("Advance Ship Notice", controller.getDocumentName());
        assertNull(controller.getDocumentType());
        controller.setDocumentType("Document Type");
        assertEquals("Document Type", controller.getDocumentType());
    }

    @Test
    public void canLoadPluginFor870() throws EDISyntaxException {
        plugin = new ANSI_870();
        plugin.prepare();
        assertEquals(53, plugin.getLoopDescriptors().length);
        controller.setPlugin(plugin);
        assertSame(plugin, controller.getPlugin());
        controller.setEnabled(true);
        assertTrue(controller.isEnabled());
        assertEquals("Order Status Report", controller.getDocumentName());
        assertNull(controller.getDocumentType());
        controller.setDocumentType("Document Type");
        assertEquals("Document Type", controller.getDocumentType());
    }

    @Test
    public void canLoadPluginFor872() throws EDISyntaxException {
        plugin = new ANSI_872();
        plugin.prepare();
        assertEquals(19, plugin.getLoopDescriptors().length);
        controller.setPlugin(plugin);
        assertSame(plugin, controller.getPlugin());
        controller.setEnabled(true);
        assertTrue(controller.isEnabled());
        assertEquals("Residential Mortgage Insurance Application", controller.getDocumentName());
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
        plugin = new ANSI_850_X_003999();
        plugin.prepare();
        controller.setPlugin(plugin);
        controller.setEnabled(true);

        assertEquals("Purchase Order (for test purposes only)", controller.getDocumentName());

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
        assertTransition("AMT", 2, "AMT-0790", "/PO1-0700/AMT-0790", 1);
        assertTransition("AMT", 2, "AMT-0790", "/PO1-0700/AMT-0790", 1);
        assertFalse(controller.transition("MSG"));
        assertTransition("CTT", 0, "/", "/", 2, true);
        assertFalse(controller.transition("AMT"));
    }

//    @Ignore
    @Test
    public void canProduceCorrectXmlFor850WithDifficultAMT() {
        String TINY_850 =
                "ISA*00*          *00*          *ZZ*04000          *ZZ*58401          *040714*1003*U*00204*000038449*0*P*<$" +
                        "GS*PO*04000*58401*040714*1003*38327*X*003999$" +
                        "ST*850*000042460$" +
                        "BEG*00*RL*SPE1C115D1099*1594*160701****FR*SP$" +
                        "REF*DS*DOC9$" +
                        "AMT*KC*121.2$" +
                        "AT**97 0X0X49305CBX*****S33189**001      2620$" +
                        "REF*AX*BX$" +
                        "N1*BY*DLA TROOP SUPPORT*10*SPE1C1$" +
                        "N2*C AND T SUPPLY CHAIN$" +
                        "N3*800 SNOWBALL AVENUE$" +
                        "N4*PHILADELPHIA*PA*191115096*US$" +
                        "N1*SE*ANOTHER, INC. DBA*33*1CAY9$" +
                        "N2*ADS$" +
                        "N3*921 HAVEN HWY STE 100$" +
                        "N4*VIRGINIA BEACH*VA*234527448*US$" +
                        "PO1*0001*1*BX*121.20000**FS*8465015151158*UA*718020072050$" +
                        "PID*F****STRAP, INVOLUNTARY, RESTRAINT$" +
                        "CTT*1$" +
                        "AMT*TT*121.2$" +
                        "SE*19*000042460$" +
                        "GE*1*38327$" +
                        "IEA*1*000038449$";

        EDItoXML ediToXml = new EDItoXML();
        StringWriter writer = new StringWriter();
        StringReader reader = new StringReader(TINY_850);
        ediToXml.setInputReader(reader);
        ediToXml.setXmlOutputWriter(writer);
        ediToXml.run();
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<ediroot>" +
                        "<interchange Standard=\"ANSI X.12\" AuthorizationQual=\"00\" Authorization=\"          \" SecurityQual=\"00\" Security=\"          \" Date=\"040714\" Time=\"1003\" StandardsId=\"U\" Version=\"00204\" Control=\"000038449\" AckRequest=\"0\" TestIndicator=\"P\">" +
                        "<sender><address Id=\"04000          \" Qual=\"ZZ\"/></sender>" +
                        "<receiver><address Id=\"58401          \" Qual=\"ZZ\"/></receiver>" +
                        "<group GroupType=\"PO\" ApplSender=\"04000\" ApplReceiver=\"58401\" Date=\"040714\" Time=\"1003\" Control=\"38327\" StandardCode=\"X\" StandardVersion=\"003999\">" +
                        "<transaction DocType=\"850\" Name=\"Purchase Order (for test purposes only)\" Control=\"000042460\">" +
                        "<segment Id=\"BEG\"><element Id=\"BEG01\">00</element><element Id=\"BEG02\">RL</element><element Id=\"BEG03\">SPE1C115D1099</element><element Id=\"BEG04\">1594</element><element Id=\"BEG05\">160701</element><element Id=\"BEG09\">FR</element><element Id=\"BEG10\">SP</element></segment>" +
                        "<segment Id=\"REF\"><element Id=\"REF01\">DS</element><element Id=\"REF02\">DOC9</element></segment>" +
                        "<loop Id=\"AMT-0200\">" +
                        "<segment Id=\"AMT\"><element Id=\"AMT01\">KC</element><element Id=\"AMT02\">121.2</element></segment>" +
                        "<segment Id=\"AT\"><element Id=\"AT02\">97 0X0X49305CBX</element><element Id=\"AT07\">S33189</element><element Id=\"AT09\">001      2620</element></segment>" +
                        "<segment Id=\"REF\"><element Id=\"REF01\">AX</element><element Id=\"REF02\">BX</element></segment>" +
                        "</loop>" +
                        "<loop Id=\"N1\">" +
                        "<segment Id=\"N1\"><element Id=\"N101\">BY</element><element Id=\"N102\">DLA TROOP SUPPORT</element><element Id=\"N103\">10</element><element Id=\"N104\">SPE1C1</element></segment>" +
                        "<segment Id=\"N2\"><element Id=\"N201\">C AND T SUPPLY CHAIN</element></segment><segment Id=\"N3\"><element Id=\"N301\">800 SNOWBALL AVENUE</element></segment>" +
                        "<segment Id=\"N4\"><element Id=\"N401\">PHILADELPHIA</element><element Id=\"N402\">PA</element><element Id=\"N403\">191115096</element><element Id=\"N404\">US</element></segment>" +
                        "</loop>" +
                        "<loop Id=\"N1\">" +
                        "<segment Id=\"N1\"><element Id=\"N101\">SE</element><element Id=\"N102\">ANOTHER, INC. DBA</element><element Id=\"N103\">33</element><element Id=\"N104\">1CAY9</element></segment>" +
                        "<segment Id=\"N2\"><element Id=\"N201\">ADS</element></segment><segment Id=\"N3\"><element Id=\"N301\">921 HAVEN HWY STE 100</element></segment>" +
                        "<segment Id=\"N4\"><element Id=\"N401\">VIRGINIA BEACH</element><element Id=\"N402\">VA</element><element Id=\"N403\">234527448</element><element Id=\"N404\">US</element></segment>" +
                        "</loop>" +
                        "<loop Id=\"PO1-0700\">" +
                        "<segment Id=\"PO1\"><element Id=\"PO101\">0001</element><element Id=\"PO102\">1</element><element Id=\"PO103\">BX</element><element Id=\"PO104\">121.20000</element><element Id=\"PO106\">FS</element><element Id=\"PO107\">8465015151158</element><element Id=\"PO108\">UA</element><element Id=\"PO109\">718020072050</element></segment>" +
                        "<loop Id=\"PID\">" +
                        "<segment Id=\"PID\"><element Id=\"PID01\">F</element><element Id=\"PID05\">STRAP, INVOLUNTARY, RESTRAINT</element></segment>" +
                        "</loop>" +
                        "</loop>" +
                        "<segment Id=\"CTT\"><element Id=\"CTT01\">1</element></segment>" +
                        "<segment Id=\"AMT\"><element Id=\"AMT01\">TT</element><element Id=\"AMT02\">121.2</element></segment>" +
                        "</transaction>" +
                        "</group>" +
                        "</interchange>" +
                        "</ediroot>",
                writer.toString());
    }

    private void assertTransition(String segment, int nestingLevel, String loopEntered, String loopStack, int closedCount, boolean resumed) throws EDISyntaxException {
        assertTrue(controller.transition(segment));
        assertEquals(loopEntered, controller.getLoopEntered());
        assertEquals(nestingLevel, controller.getNestingLevel());
        assertEquals(loopStack, controller.getLoopStack().toString());
        assertEquals(closedCount, controller.closedCount());
        assertEquals(resumed, controller.isResumed());
    }

    private void assertTransition(String segment, int nestingLevel, String loopEntered, String loopStack, int closedCount) throws EDISyntaxException {
        assertTransition(segment, nestingLevel, loopEntered, loopStack, closedCount, false);
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
