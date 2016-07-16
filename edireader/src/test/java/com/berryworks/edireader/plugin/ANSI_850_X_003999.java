/*
 *  Generated from 834-4010.SEF
 *  using tools provided by BerryWorks Software, LLC
 */
package com.berryworks.edireader.plugin;

public class ANSI_850_X_003999 extends CompositeAwarePlugin {

    public ANSI_850_X_003999() {
        super("850", "Purchase Order (for test purposes only)");

        loops = new LoopDescriptor[]{
                new LoopDescriptor(null, "ADV", 2, "/PO1-0700/SLN"),
                new LoopDescriptor("ADV", "ADV", 1, ANY_CONTEXT),

                new LoopDescriptor("AMT-0790", "AMT", 2, "/PO1-0700"),
//                new LoopDescriptor(null, "AMT", 1, "/CTT"),
                new LoopDescriptor("AMT-0200", "AMT", 1, ANY_CONTEXT),

                new LoopDescriptor(".", "BEG", 0, ANY_CONTEXT),

                new LoopDescriptor("CB1", "CB1", 2, "/SPI"),

                new LoopDescriptor(null, "CN1", 1, "/PO1-0700"),

                new LoopDescriptor(null, "CSH", 1, "/PO1-0700"),
                new LoopDescriptor(".", "CSH", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "CTB", 1, "/PO1-0700"),
                new LoopDescriptor(".", "CTB", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "CTP", 3, "/PO1-0700/SLN/SAC"),
                new LoopDescriptor(null, "CTP", 2, "/PO1-0700/SLN"),
                new LoopDescriptor(null, "CTP", 2, "/PO1-0700/SAC"),
                new LoopDescriptor("CTP", "CTP", 2, "/PO1-0700"),
                new LoopDescriptor(".", "CTP", 0, ANY_CONTEXT),

                new LoopDescriptor(".", "CTT", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "CUR", 3, "/PO1-0700/SLN/SAC"),
                new LoopDescriptor(null, "CUR", 2, "/PO1-0700/CTP"),
                new LoopDescriptor(null, "CUR", 1, "/PO1-0700"),
                new LoopDescriptor(null, "CUR", 1, "/SAC"),
                new LoopDescriptor(".", "CUR", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "DIS", 1, "/PO1-0700"),
                new LoopDescriptor(".", "DIS", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "DTM", 3, "/PO1-0700/SLN/N9"),
                new LoopDescriptor(null, "DTM", 2, "/SPI/CB1"),
                new LoopDescriptor(null, "DTM", 1, "/PO1-0700"),
                new LoopDescriptor(null, "DTM", 1, "/AMT-0200"),
                new LoopDescriptor(null, "DTM", 1, "/ADV"),
                new LoopDescriptor(null, "DTM", 1, "/SPI"),
                new LoopDescriptor(null, "DTM", 1, "/N9"),
                new LoopDescriptor(".", "DTM", 0, ANY_CONTEXT),

                new LoopDescriptor("FA1", "FA1", 2, "/AMT-0200"),

                new LoopDescriptor(null, "FA2", 2, "/AMT-0200/FA1"),

                new LoopDescriptor(null, "FOB", 2, "/PO1-0700/N1"),
                new LoopDescriptor(null, "FOB", 1, "/PO1-0700"),
                new LoopDescriptor(null, "FOB", 1, "/N1"),
                new LoopDescriptor(".", "FOB", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "G61", 2, "/SPI/N1"),

                new LoopDescriptor(null, "INC", 1, "/PO1-0700"),
                new LoopDescriptor(".", "INC", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "IT3", 1, "/PO1-0700"),
                new LoopDescriptor(".", "IT8", 1, "/PO1-0700"),

                new LoopDescriptor(null, "ITD", 1, "/PO1-0700"),
                new LoopDescriptor(".", "ITD", 0, ANY_CONTEXT),

                new LoopDescriptor("LDT", "LDT", 3, "/PO1-0700/N1"),
                new LoopDescriptor(null, "LDT", 2, "/SPI/CB1"),
                new LoopDescriptor("LDT", "LDT", 2, "/PO1-0700"),
                new LoopDescriptor(".", "LDT", 0, ANY_CONTEXT),

                new LoopDescriptor(".", "LE", 1, "/PO1-0700"),

                new LoopDescriptor(null, "LIN", 1, "/PO1-0700"),
                new LoopDescriptor(".", "LIN", 0, ANY_CONTEXT),

                new LoopDescriptor("LM", "LM", 3, "/PO1-0700/LDT"),
                new LoopDescriptor("LM", "LM", 2, "/PO1-0700"),
                new LoopDescriptor("LM", "LM", 1, ANY_CONTEXT),

                new LoopDescriptor(null, "LQ", 3, "/PO1-0700/LDT/LM"),
                new LoopDescriptor(null, "LQ", 2, "/PO1-0700/LM"),
                new LoopDescriptor(null, "LQ", 1, "/LM"),

                new LoopDescriptor(".", "LS", 1, "/PO1-0700"),

                new LoopDescriptor(null, "MAN", 3, "/PO1-0700/N1/LDT"),
                new LoopDescriptor(null, "MAN", 1, "/PO1-0700"),
                new LoopDescriptor(".", "MAN", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "MEA", 2, "/PO1-0700/PID"),
                new LoopDescriptor(null, "MEA", 1, "/PO1-0700"),
                new LoopDescriptor(".", "MEA", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "MSG", 3, "/PO1-0700/N1/LDT"),
                new LoopDescriptor(null, "MSG", 2, "/SPI/N1"),
                new LoopDescriptor(null, "MSG", 1, "/N9"),

                new LoopDescriptor(null, "MTX", 1, "/ADV"),

                new LoopDescriptor("N1", "N1", 3, "/PO1-0700/SLN"),
                new LoopDescriptor("N1", "N1", 2, "/PO1-0700"),
                new LoopDescriptor("N1", "N1", 2, "/SPI"),
                new LoopDescriptor("N1", "N1", 1, ANY_CONTEXT),

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
                new LoopDescriptor("N9", "N9", 1, ANY_CONTEXT),

                new LoopDescriptor(null, "NX2", 3, "/PO1-0700/SLN/N1"),
                new LoopDescriptor(null, "NX2", 2, "/PO1-0700/N1"),
                new LoopDescriptor(null, "NX2", 1, "/N1"),

                new LoopDescriptor(null, "PAM", 2, "/PO1-0700/SLN"),
                new LoopDescriptor(".", "PAM", 1, "/PO1-0700"),
                new LoopDescriptor(".", "PAM", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "PCT", 2, "/PO1-0700/AMT"),
                new LoopDescriptor(null, "PCT", 1, "/PO1-0700"),
                new LoopDescriptor(null, "PCT", 1, "/AMT-0200"),
                new LoopDescriptor(".", "PCT", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "PER", 3, "/PO1-0700/SLN/N1"),
                new LoopDescriptor(null, "PER", 2, "/PO1-0700/N1"),
                new LoopDescriptor(null, "PER", 1, "/PO1-0700"),
                new LoopDescriptor(null, "PER", 1, "/N1"),
                new LoopDescriptor(".", "PER", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "PID", 2, "/PO1-0700/SLN"),
                new LoopDescriptor("PID", "PID", 2, "/PO1-0700"),
                new LoopDescriptor(".", "PID", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "PKG", 3, "/PO1-0700/N1"),
                new LoopDescriptor("PKG", "PKG", 2, "/PO1-0700"),
                new LoopDescriptor(null, "PKG", 1, "/N1"),
                new LoopDescriptor(".", "PKG", 0, ANY_CONTEXT),

                new LoopDescriptor("PO1-0700", "PO1", 1, ANY_CONTEXT),

                new LoopDescriptor(null, "PO3", 2, "/PO1-0700/SLN"),
                new LoopDescriptor(null, "PO3", 1, "/PO1-0700"),

                new LoopDescriptor(null, "PO4", 2, "/PO1-0700/SLN"),
                new LoopDescriptor(null, "PO4", 1, "/PO1-0700"),

                new LoopDescriptor(".", "PWK", 1, "/PO1-0700"),
                new LoopDescriptor(".", "PWK", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "QTY", 3, "/PO1-0700/N1/LDT"),
                new LoopDescriptor("QTY", "QTY", 3, "/PO1-0700/SLN"),
                new LoopDescriptor(null, "QTY", 3, "/PO1-0700/LDT"),
                new LoopDescriptor(null, "QTY", 3, "/PO1-0700/N1"),
                new LoopDescriptor("QTY", "QTY", 2, "/PO1-0700"),

                new LoopDescriptor(null, "REF", 3, "/PO1-0700/N1/LDT"),
                new LoopDescriptor(null, "REF", 2, "/SPI/N1"),
                new LoopDescriptor(null, "REF", 1, "/SPI"),
                new LoopDescriptor(null, "REF", 1, "/PO1-0700"),
                new LoopDescriptor(null, "REF", 1, "/AMT-0200"),
                new LoopDescriptor(null, "REF", 1, "/N1"),
                new LoopDescriptor(".", "REF", 0, ANY_CONTEXT),

                new LoopDescriptor("SAC", "SAC", 3, "/PO1-0700/SLN"),
                new LoopDescriptor("SAC", "SAC", 2, "/PO1-0700"),
                new LoopDescriptor("SAC", "SAC", 1, ANY_CONTEXT),

                new LoopDescriptor(null, "SCH", 3, "/PO1-0700/N1"),
                new LoopDescriptor("SCH", "SCH", 2, "/PO1-0700"),

                new LoopDescriptor(null, "SDQ", 1, "/PO1-0700"),

                new LoopDescriptor(".", "SE", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "SI", 3, "/PO1-0700/SLN/QTY"),
                new LoopDescriptor(null, "SI", 2, "/PO1-0700/QTY"),
                new LoopDescriptor(null, "SI", 2, "/PO1-0700/SLN"),
                new LoopDescriptor(null, "SI", 1, "/N1"),
                new LoopDescriptor(".", "SI", 0, ANY_CONTEXT),

                new LoopDescriptor("SLN", "SLN", 2, "/PO1-0700"),

                new LoopDescriptor(null, "SPI", 1, "/PO1-0700"),
                new LoopDescriptor("SPI", "SPI", 1, ANY_CONTEXT),

                new LoopDescriptor(".", "ST", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "TAX", 2, "/PO1-0700/SLN"),
                new LoopDescriptor(null, "TAX", 1, "/PO1-0700"),
                new LoopDescriptor(".", "TAX", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "TC2", 2, "/PO1-0700/SLN"),
                new LoopDescriptor(null, "TC2", 1, "/PO1-0700"),
                new LoopDescriptor(".", "TC2", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "TD1", 2, "/PO1-0700/SCH"),
                new LoopDescriptor(null, "TD1", 1, "/PO1-0700"),
                new LoopDescriptor(null, "TD1", 1, "/N1"),
                new LoopDescriptor(".", "TD1", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "TD3", 2, "/PO1-0700/SCH"),
                new LoopDescriptor(null, "TD3", 1, "/PO1-0700"),
                new LoopDescriptor(null, "TD3", 1, "/N1"),
                new LoopDescriptor(".", "TD3", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "TD4", 2, "/PO1-0700/SCH"),
                new LoopDescriptor(null, "TD4", 1, "/PO1-0700"),
                new LoopDescriptor(null, "TD4", 1, "/N1"),
                new LoopDescriptor(".", "TD4", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "TD5", 2, "/PO1-0700/SCH"),
                new LoopDescriptor(null, "TD5", 1, "/PO1-0700"),
                new LoopDescriptor(null, "TD5", 1, "/N1"),
                new LoopDescriptor(".", "TD5", 0, ANY_CONTEXT),

                new LoopDescriptor(null, "TXI", 1, "/PO1-0700"),
                new LoopDescriptor(".", "TXI", 0, ANY_CONTEXT),
        };

        composites.put("REF-4", "");

    }
}  
