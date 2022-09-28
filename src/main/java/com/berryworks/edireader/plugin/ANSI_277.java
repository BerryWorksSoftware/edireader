/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
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

public class ANSI_277 extends Plugin {
    public ANSI_277() {
        super("277", "Health Care Claim Status Notification");
        loops = new LoopDescriptor[]{
                new LoopDescriptor(CURRENT, "BHT", 0, ANY_CONTEXT),
                new LoopDescriptor(CURRENT, "DMG", 1, "/2000"),
                new LoopDescriptor(CURRENT, "DTP", 3, "/2000/2200/2220"),
                new LoopDescriptor(CURRENT, "DTP", 2, "/2000/2200"),
                new LoopDescriptor("2000", "HL", 1, ANY_CONTEXT),
                new LoopDescriptor("2100", "NM1", 2, "/2000/2100"),
                new LoopDescriptor("2100", "NM1", 2, "/2000"),
                new LoopDescriptor(CURRENT, "PER", 2, "/2000/2100"),
                new LoopDescriptor(CURRENT, "REF", 3, "/2000/2200/2220"),
                new LoopDescriptor(CURRENT, "REF", 2, "/2000/2200"),
                new LoopDescriptor(CURRENT, "STC", 3, "/2000/2200/2220"),
                new LoopDescriptor(CURRENT, "STC", 2, "/2000/2200"),
                new LoopDescriptor("2220", "SVC", 3, "/2000/2200/2220"),
                new LoopDescriptor("2220", "SVC", 3, "/2000/2200"),
                new LoopDescriptor("2200", "TRN", 2, "/2000/2200"),
                new LoopDescriptor("2200", "TRN", 2, "/2000"),
        };
    }
}
