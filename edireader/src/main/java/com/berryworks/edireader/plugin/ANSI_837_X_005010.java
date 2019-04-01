/*
 * Copyright 2005-2019 by BerryWorks Software, LLC. All rights reserved.
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

package com.berryworks.edireader.plugin;

import com.berryworks.edireader.Plugin;

import static com.berryworks.edireader.plugin.LoopRule.when;

public class ANSI_837_X_005010 extends Plugin {
    public ANSI_837_X_005010() {
        super("835", "Health Care Claim");
        loops = new LoopDescriptor[]{
                when("AMT", "/HL-2000/CLM-2300/LX-2400/SVD-2430").then(4),
                when("AMT", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("AMT", "/HL-2000/CLM-2300/SBR-2320").then(3),
                when("AMT", "/HL-2000/CLM-2300").then(2),
                when("BHT").then(0),
                when("CAS", "/HL-2000/CLM-2300/LX-2400/SVD-2430").then(4),
                when("CAS", "/HL-2000/CLM-2300/SBR-2320").then(3),
                when("CL1", "/HL-2000/CLM-2300").then(2),
                when("CLM", "/HL-2000/CLM-2300").then("CLM-2300", 2),
                when("CLM", "/HL-2000").then("CLM-2300", 2),
                when("CN1", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("CN1", "/HL-2000/CLM-2300").then(2),
                when("CR1", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("CR1", "/HL-2000/CLM-2300").then(2),
                when("CR2", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("CR2", "/HL-2000/CLM-2300").then(2),
                when("CR3", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("CR3", "/HL-2000/CLM-2300").then(2),
                when("CR4", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("CR4", "/HL-2000/CLM-2300").then(2),
                when("CR5", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("CR5", "/HL-2000/CLM-2300").then(2),
                when("CR6", "/HL-2000/CLM-2300").then(2),
                when("CR7", "/HL-2000/CLM-2300/CR7-2305").then("CR7-2305", 3),
                when("CR7", "/HL-2000/CLM-2300").then("CR7-2305", 3),
                when("CR8", "/HL-2000/CLM-2300").then(2),
                when("CRC", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("CRC", "/HL-2000/CLM-2300").then(2),
                when("CTP", "/HL-2000/CLM-2300/LX-2400/LIN-2410").then(4),
                when("CUR", "/HL-2000").then(1),
                when("DMG", "/HL-2000/CLM-2300/SBR-2320").then(3),
                when("DMG", "/HL-2000/NM1-2010").then(2),
                when("DN1", "/HL-2000/CLM-2300").then(2),
                when("DN2", "/HL-2000/CLM-2300").then(2),
                when("DSB", "/HL-2000/CLM-2300").then(2),
                when("DTP", "/HL-2000/CLM-2300/LX-2400/SVD-2430").then(4),
                when("DTP", "/HL-2000/CLM-2300/SBR-2320/NM1-2330").then(4),
                when("DTP", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("DTP", "/HL-2000/CLM-2300").then(2),
                when("DTP", "/HL-2000").then(1),
                when("FRM", "/HL-2000/CLM-2300/LX-2400/LQ-2440").then(4),
                when("HCP", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("HCP", "/HL-2000/CLM-2300").then(2),
                when("HI", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("HI", "/HL-2000/CLM-2300").then(2),
                when("HL").then("HL-2000", 1),
                when("HSD", "/HL-2000/CLM-2300/CR7-2305").then(3),
                when("HSD", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("IMM", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("K3", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("K3", "/HL-2000/CLM-2300").then(2),
                when("LIN", "/HL-2000/CLM-2300/LX-2400/LIN-2410").then("LIN-2410", 4),
                when("LIN", "/HL-2000/CLM-2300/LX-2400").then("LIN-2410", 4),
                when("LQ", "/HL-2000/CLM-2300/LX-2400/LQ-2440").then("LQ-2440", 4),
                when("LQ", "/HL-2000/CLM-2300/LX-2400").then("LQ-2440", 4),
                when("LX", "/HL-2000/CLM-2300/LX-2400").then("LX-2400", 3),
                when("LX", "/HL-2000/CLM-2300").then("LX-2400", 3),
                when("MEA", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("MIA", "/HL-2000/CLM-2300/SBR-2320").then(3),
                when("MOA", "/HL-2000/CLM-2300/SBR-2320").then(3),
                when("N2", "/HL-2000/CLM-2300/LX-2400/NM1-2420").then(4),
                when("N2", "/HL-2000/CLM-2300/SBR-2320/NM1-2330").then(4),
                when("N2", "/HL-2000/CLM-2300/NM1-2310").then(3),
                when("N2", "/HL-2000/NM1-2010").then(2),
                when("N2", "/NM1-1000").then(1),
                when("N3", "/HL-2000/CLM-2300/LX-2400/NM1-2420").then(4),
                when("N3", "/HL-2000/CLM-2300/SBR-2320/NM1-2330").then(4),
                when("N3", "/HL-2000/CLM-2300/NM1-2310").then(3),
                when("N3", "/HL-2000/NM1-2010").then(2),
                when("N3", "/NM1-1000").then(1),
                when("N4", "/HL-2000/CLM-2300/LX-2400/NM1-2420").then(4),
                when("N4", "/HL-2000/CLM-2300/SBR-2320/NM1-2330").then(4),
                when("N4", "/HL-2000/CLM-2300/NM1-2310").then(3),
                when("N4", "/HL-2000/NM1-2010").then(2),
                when("N4", "/NM1-1000").then(1),
                when("NM1", "/HL-2000/CLM-2300/LX-2400/NM1-2420").then("NM1-2420", 4),
                when("NM1", "/HL-2000/CLM-2300/LX-2400").then("NM1-2420", 4),
                when("NM1", "/HL-2000/CLM-2300/SBR-2320/NM1-2330").then("NM1-2330", 4),
                when("NM1", "/HL-2000/CLM-2300/SBR-2320").then("NM1-2330", 4),
                when("NM1", "/HL-2000/CLM-2300/NM1-2310").then("NM1-2310", 3),
                when("NM1", "/HL-2000/CLM-2300").then("NM1-2310", 3),
                when("NM1", "/HL-2000/NM1-2010").then("NM1-2010", 2),
                when("NM1", "/HL-2000").then("NM1-2010", 2),
                when("NM1").then("NM1-1000", 1),
                when("NTE", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("NTE", "/HL-2000/CLM-2300").then(2),
                when("OI", "/HL-2000/CLM-2300/SBR-2320").then(3),
                when("PAT", "/HL-2000").then(1),
                when("PER", "/HL-2000/CLM-2300/LX-2400/NM1-2420").then(4),
                when("PER", "/HL-2000/CLM-2300/SBR-2320/NM1-2330").then(4),
                when("PER", "/HL-2000/CLM-2300/NM1-2310").then(3),
                when("PER", "/HL-2000/NM1-2010").then(2),
                when("PER", "/NM1-1000").then(1),
                when("PRV", "/HL-2000/CLM-2300/LX-2400/NM1-2420").then(4),
                when("PRV", "/HL-2000/CLM-2300/NM1-2310").then(3),
                when("PRV", "/HL-2000").then(1),
                when("PS1", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("PWK", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("PWK", "/HL-2000/CLM-2300").then(2),
                when("QTY", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("QTY", "/HL-2000/CLM-2300").then(2),
                when("REF", "/HL-2000/CLM-2300/LX-2400/LIN-2410").then(4),
                when("REF", "/HL-2000/CLM-2300/LX-2400/NM1-2420").then(4),
                when("REF", "/HL-2000/CLM-2300/SBR-2320/NM1-2330").then(4),
                when("REF", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("REF", "/HL-2000/CLM-2300/NM1-2310").then(3),
                when("REF", "/HL-2000/CLM-2300").then(2),
                when("REF", "/HL-2000/NM1-2010").then(2),
                when("REF", "/NM1-1000").then(1),
                when("REF").then(0),
                when("SBR", "/HL-2000/CLM-2300/SBR-2320").then("SBR-2320", 3),
                when("SBR", "/HL-2000/CLM-2300").then("SBR-2320", 3),
                when("SBR", "/HL-2000").then(1),
                when("SV1", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("SV2", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("SV3", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("SV4", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("SV5", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("SV6", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("SV7", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("SVD", "/HL-2000/CLM-2300/LX-2400/SVD-2430").then("SVD-2430", 4),
                when("SVD", "/HL-2000/CLM-2300/LX-2400").then("SVD-2430", 4),
                when("TOO", "/HL-2000/CLM-2300/LX-2400").then(3),
                when("UR", "/HL-2000/CLM-2300").then(2),
//
//        composites:
//        CLM-11
//        CLM-5
//        CTP-5
//        DMG-5
//        HI-1
//        HI-10
//        HI-11
//        HI-12
//        HI-2
//        HI-3
//        HI-4
//        HI-5
//        HI-6
//        HI-7
//        HI-8
//        HI-9
//        K3-3
//        MEA-4
//        PRV-5
//        PWK-8
//        QTY-3
//        REF-4
//        SV1-1
//        SV1-7
//        SV2-2
//        SV3-1
//        SV3-11
//        SV3-4
//        SV4-2
//        SV5-1
//        SV6-1
//        SV6-5
//        SVD-3
//        TOO-3
        };
    }


}