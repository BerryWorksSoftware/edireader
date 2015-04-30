/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
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

/**
 * Plugin for the EDIFACT MEDRPT Medical Service Report Message
 */

public class EDIFACT_MEDRPT extends Plugin
{

  public EDIFACT_MEDRPT()
  {
    super("MEDRPT", "Medical Service Report Message");
    loops = new LoopDescriptor[]{new LoopDescriptor("SG02", "DIA", 2),
      new LoopDescriptor("SG03", "PRO", 2),
      new LoopDescriptor("SG04", "FCA", 1),
      new LoopDescriptor("SG09", "SPE", 2),
      new LoopDescriptor("SG15", "AUT", 1),
      new LoopDescriptor("SG01", "NAD"),
      new LoopDescriptor("SG01", "NAD", 1, "SG01"),
      new LoopDescriptor("SG01", "NAD", 1, "SG02"),
      new LoopDescriptor("SG01", "NAD", 1, "SG03"),
      new LoopDescriptor("SG05", "NAD", 2, "SG04"),
      new LoopDescriptor("SG05", "NAD", 2, "SG05"),
      new LoopDescriptor("SG06", "OBR", 2, "SG04"),
      new LoopDescriptor("SG06", "OBR", 2, "SG05"),
      new LoopDescriptor("SG06", "OBR", 2, "SG06"),
      new LoopDescriptor("SG06", "OBR", 2, "SG07"),
      new LoopDescriptor("SG06", "OBR", 2, "SG08"),
      new LoopDescriptor("SG07", "NAD", 3, "SG06"),
      new LoopDescriptor("SG07", "NAD", 3, "SG07"),
      new LoopDescriptor("SG07", "NAD", 3, "SG08"),
      new LoopDescriptor("SG08", "CTA", 4, "SG07"),
      new LoopDescriptor("SG08", "CTA", 4, "SG08"),
      new LoopDescriptor("SG10", "NAD", 3, "SG09"),
      new LoopDescriptor("SG11", "CTA", 4, "SG10"),
      new LoopDescriptor("SG11", "CTA", 4, "SG11"),
      new LoopDescriptor("SG12", "OBR", 3, "SG09"),
      new LoopDescriptor("SG12", "OBR", 3, "SG10"),
      new LoopDescriptor("SG12", "OBR", 3, "SG11"),
      new LoopDescriptor("SG12", "OBR", 3, "SG12"),
      new LoopDescriptor("SG12", "OBR", 3, "SG13"),
      new LoopDescriptor("SG12", "OBR", 3, "SG14"),
      new LoopDescriptor("SG13", "NAD", 4, "SG12"),
      new LoopDescriptor("SG14", "CTA", 5, "SG13"),
      new LoopDescriptor("SG14", "CTA", 5, "SG14"),
      new LoopDescriptor("SG15", "AUT", 1, "SG01")};
  }
}
