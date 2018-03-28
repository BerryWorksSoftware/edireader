package com.berryworks.edireader.plugin;

import com.berryworks.edireader.Plugin;

public class ANSI_837 extends Plugin {

    public ANSI_837() {
        super("837", "Health Care Claim");
        loops = new LoopDescriptor[]{
            new LoopDescriptor("2300", "CLM", 2, "/2000/2300"),
            new LoopDescriptor("2300", "CLM", 2, "/2000"),
            new LoopDescriptor("2305", "CR7", 3, "/2000/2300/2305"),
            new LoopDescriptor("2305", "CR7", 3, "/2000/2300"),
            new LoopDescriptor("2000", "HL", 1, ANY_CONTEXT),
            new LoopDescriptor("2440", "LQ", 4, "/2000/2300/2400/2440"),
            new LoopDescriptor("2440", "LQ", 4, "/2000/2300/2400"),
            new LoopDescriptor("2400", "LX", 3, "/2000/2300/2400"),
            new LoopDescriptor("2400", "LX", 3, "/2000/2300"),
            new LoopDescriptor("2330", "NM1", 4, "/2000/2300/2320/2330"),
            new LoopDescriptor("2420", "NM1", 4, "/2000/2300/2400/2420"),
            new LoopDescriptor("2310", "NM1", 3, "/2000/2300/2310"),
            new LoopDescriptor("2330", "NM1", 4, "/2000/2300/2320"),
            new LoopDescriptor("2420", "NM1", 4, "/2000/2300/2400"),
            new LoopDescriptor("2010", "NM1", 2, "/2000/2010"),
            new LoopDescriptor("2310", "NM1", 3, "/2000/2300"),
            new LoopDescriptor("2010", "NM1", 2, "/2000"),
            new LoopDescriptor("1000", "NM1", 1, ANY_CONTEXT),
            new LoopDescriptor("2320", "SBR", 3, "/2000/2300/2320"),
            new LoopDescriptor("2320", "SBR", 3, "/2000/2300"),
            new LoopDescriptor(CURRENT, "SBR", 1, "/2000"),
            new LoopDescriptor("2430", "SVD", 4, "/2000/2300/2400/2430"),
            new LoopDescriptor("2430", "SVD", 4, "/2000/2300/2400")};
    }
}
