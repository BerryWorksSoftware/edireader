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
 * Plugin for the EDIFACT Purchase Order
 */

public class EDIFACT_ORDERS extends Plugin
{

  public EDIFACT_ORDERS()
  {
    super("ORDERS", "Purchase Order Message");
    loops = new LoopDescriptor[]{
      new LoopDescriptor("SG43", "ALC", 2, "/SG28"),
      new LoopDescriptor("SG60", "ALC", 1, "/UNS"),
      new LoopDescriptor("SG60", "ALC", 1, "/SG60"),
      new LoopDescriptor("SG19", "ALC", 1),

      new LoopDescriptor(null, "APR", 2, "/SG28"),
      new LoopDescriptor("SG18", "APR", 1),

      new LoopDescriptor("SG29", "CCI", 2),

      new LoopDescriptor("SG05", "CTA", 2, "/SG02"),
      new LoopDescriptor("SG27", "CTA", 2, "/SG26"),
      new LoopDescriptor("SG42", "CTA", 3, "/SG28/SG39"),
      new LoopDescriptor("SG59", "CTA", 3, "/SG28/SG58"),

      new LoopDescriptor(null, "CUX", 2, "/SG28/SG32"),
      new LoopDescriptor("SG07", "CUX", 1),

      new LoopDescriptor("SG58", "DGS", 2, "/SG28"),
      new LoopDescriptor("SG26", "DGS", 1),

      new LoopDescriptor("SG41", "DOC", 3, "/SG28/SG39"),
      new LoopDescriptor("SG04", "DOC", 2, "/SG02"),

      new LoopDescriptor("SG52", "EQD", 2, "/SG28"),
      new LoopDescriptor("SG15", "EQD", 1),

      new LoopDescriptor("SG28", "LIN", 1),

      new LoopDescriptor("SG11", "LOC", 2, "/SG10"),
      new LoopDescriptor(null, "LOC", 2, "/SG28/SG38"),
      new LoopDescriptor(null, "LOC", 2, "/SG28/SG39"),
      new LoopDescriptor(null, "LOC", 2, "/SG28/SG51"),
      new LoopDescriptor("SG50", "LOC", 3, "/SG28/SG49"),
      new LoopDescriptor("SG37", "LOC", 2, "/SG28"),

      new LoopDescriptor("SG09", "MOA", 2, "/SG08"),
      new LoopDescriptor(null, "MOA", 3, "/SG19/SG24"),
      new LoopDescriptor("SG22", "MOA", 2, "/SG19"),
      new LoopDescriptor("SG31", "MOA", 3, "/SG28/SG30"),
      new LoopDescriptor(null, "MOA", 3, "/SG28/SG43/SG48"),
      new LoopDescriptor("SG46", "MOA", 3, "/SG28/SG43"),

      new LoopDescriptor("SG39", "NAD", 2, "/SG28"),
      new LoopDescriptor("SG02", "NAD", 1),

      new LoopDescriptor("SG34", "PAC", 2, "/SG28"),
      new LoopDescriptor("SG13", "PAC", 1),

      new LoopDescriptor("SG08", "PAT"),
      new LoopDescriptor("SG08", "PAT", 1, "SG01"),
      new LoopDescriptor("SG08", "PAT", 1, "/SG02"),
      new LoopDescriptor("SG08", "PAT", 1, "SG06"),
      new LoopDescriptor("SG08", "PAT", 1, "SG07"),
      new LoopDescriptor("SG08", "PAT", 1, "/SG08"),
      new LoopDescriptor("SG30", "PAT", 2),
      new LoopDescriptor("SG08", "PYT"),
      new LoopDescriptor("SG08", "PYT", 1, "SG01"),
      new LoopDescriptor("SG08", "PYT", 1, "/SG02"),
      new LoopDescriptor("SG08", "PYT", 1, "SG06"),
      new LoopDescriptor("SG08", "PYT", 1, "SG07"),
      new LoopDescriptor("SG08", "PYT", 1, "/SG08"),
      new LoopDescriptor("SG30", "PYT", 2),

      new LoopDescriptor("SG21", "PCD", 2, "/SG19"),
      new LoopDescriptor("SG45", "PCD", 3, "/SG28/SG43"),

      new LoopDescriptor("SG14", "PCI", 2, "/SG13"),
      new LoopDescriptor("SG36", "PCI", 3, "/SG28/SG34"),

      new LoopDescriptor("SG32", "PRI", 2),

      new LoopDescriptor("SG17", "QTY", 2, "/SG16"),
      new LoopDescriptor("SG20", "QTY", 2, "/SG19"),
      new LoopDescriptor("SG44", "QTY", 3, "/SG28/SG43"),
      new LoopDescriptor("SG54", "QTY", 3, "/SG28/SG53"),
      new LoopDescriptor("SG57", "QTY", 3, "/SG28/SG56"),

      new LoopDescriptor("SG55", "RCS", 2, "/SG28"),
      new LoopDescriptor("SG25", "RCS", 1),

      new LoopDescriptor("SG01", "RFF"),
      new LoopDescriptor("SG01", "RFF", 1, "SG01"),
      new LoopDescriptor("SG03", "RFF", 2, "/SG02"),
      new LoopDescriptor(null, "RFF", 3, "/SG28/SG34/SG36"),
      new LoopDescriptor(null, "RFF", 2, "/SG28/SG53"),
      new LoopDescriptor(null, "RFF", 2, "/SG28/SG55"),
      new LoopDescriptor("SG35", "RFF", 3, "/SG28/SG34"),
      new LoopDescriptor("SG40", "RFF", 3, "/SG28/SG39"),
      new LoopDescriptor("SG33", "RFF", 2, "/SG28"),

      new LoopDescriptor("SG23", "RTE", 2, "/SG19"),
      new LoopDescriptor("SG47", "RTE", 3, "/SG28/SG43"),

      new LoopDescriptor("SG53", "SCC", 2, "/SG28"),
      new LoopDescriptor("SG16", "SCC", 1),

      new LoopDescriptor("SG56", "STG", 2),

      new LoopDescriptor("SG24", "TAX", 2, "/SG19"),
      new LoopDescriptor("SG48", "TAX", 3, "/SG28/SG43"),
      new LoopDescriptor("SG38", "TAX", 2, "/SG28"),
      new LoopDescriptor("SG06", "TAX", 1),

      new LoopDescriptor("SG49", "TDT", 2, "/SG28"),
      new LoopDescriptor("SG10", "TDT", 1),

      new LoopDescriptor("SG51", "TOD", 2, "/SG28"),
      new LoopDescriptor("SG12", "TOD", 1),

      new LoopDescriptor("/UNS", "UNS", 0)

    };
  }
}
