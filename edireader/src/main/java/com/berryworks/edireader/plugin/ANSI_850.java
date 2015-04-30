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

public class ANSI_850 extends Plugin
{
  public ANSI_850()
  {
    super("850", "Purchase Order");
    loops = new LoopDescriptor[]{
      new LoopDescriptor(null, "ADV", 2, "/PO1/SLN"),
      new LoopDescriptor("ADV", "ADV", 1, "*"),

      new LoopDescriptor("AMT", "AMT", 2, "/PO1"),
      new LoopDescriptor(null, "AMT", 1, "/CTT"),
      new LoopDescriptor("AMT", "AMT", 1, "*"),

      new LoopDescriptor(".", "BEG", 0, "*"),

      new LoopDescriptor("CB1", "CB1", 2, "/SPI"),

      new LoopDescriptor(null, "CN1", 1, "/PO1"),

      new LoopDescriptor(null, "CSH", 1, "/PO1"),
      new LoopDescriptor(".", "CSH", 0, "*"),

      new LoopDescriptor(null, "CTB", 1, "/PO1"),
      new LoopDescriptor(".", "CTB", 0, "*"),

      new LoopDescriptor(null, "CTP", 3, "/PO1/SLN/SAC"),
      new LoopDescriptor(null, "CTP", 2, "/PO1/SLN"),
      new LoopDescriptor(null, "CTP", 2, "/PO1/SAC"),
      new LoopDescriptor("CTP", "CTP", 2, "/PO1"),
      new LoopDescriptor(".", "CTP", 0, "*"),

      new LoopDescriptor("CTT", "CTT", 1, "*"),

      new LoopDescriptor(null, "CUR", 3, "/PO1/SLN/SAC"),
      new LoopDescriptor(null, "CUR", 2, "/PO1/CTP"),
      new LoopDescriptor(null, "CUR", 1, "/PO1"),
      new LoopDescriptor(null, "CUR", 1, "/SAC"),
      new LoopDescriptor(".", "CUR", 0, "*"),

      new LoopDescriptor(null, "DIS", 1, "/PO1"),
      new LoopDescriptor(".", "DIS", 0, "*"),

      new LoopDescriptor(null, "DTM", 3, "/PO1/SLN/N9"),
      new LoopDescriptor(null, "DTM", 2, "/SPI/CB1"),
      new LoopDescriptor(null, "DTM", 1, "/PO1"),
      new LoopDescriptor(null, "DTM", 1, "/AMT"),
      new LoopDescriptor(null, "DTM", 1, "/ADV"),
      new LoopDescriptor(null, "DTM", 1, "/SPI"),
      new LoopDescriptor(null, "DTM", 1, "/N9"),
      new LoopDescriptor(".", "DTM", 0, "*"),

      new LoopDescriptor("FA1", "FA1", 2, "/AMT"),

      new LoopDescriptor(null, "FA2", 2, "/AMT/FA1"),

      new LoopDescriptor(null, "FOB", 2, "/PO1/N1"),
      new LoopDescriptor(null, "FOB", 1, "/PO1"),
      new LoopDescriptor(null, "FOB", 1, "/N1"),
      new LoopDescriptor(".", "FOB", 0, "*"),

      new LoopDescriptor(null, "G61", 2, "/SPI/N1"),

      new LoopDescriptor(null, "INC", 1, "/PO1"),
      new LoopDescriptor(".", "INC", 0, "*"),

      new LoopDescriptor(null, "IT3", 1, "/PO1"),
      new LoopDescriptor(".", "IT8", 1, "/PO1"),

      new LoopDescriptor(null, "ITD", 1, "/PO1"),
      new LoopDescriptor(".", "ITD", 0, "*"),

      new LoopDescriptor("LDT", "LDT", 3, "/PO1/N1"),
      new LoopDescriptor(null, "LDT", 2, "/SPI/CB1"),
      new LoopDescriptor("LDT", "LDT", 2, "/PO1"),
      new LoopDescriptor(".", "LDT", 0, "*"),

      new LoopDescriptor(".", "LE", 1, "/PO1"),

      new LoopDescriptor(null, "LIN", 1, "/PO1"),
      new LoopDescriptor(".", "LIN", 0, "*"),

      new LoopDescriptor("LM", "LM", 3, "/PO1/LDT"),
      new LoopDescriptor("LM", "LM", 2, "/PO1"),
      new LoopDescriptor("LM", "LM", 1, "*"),

      new LoopDescriptor(null, "LQ", 3, "/PO1/LDT/LM"),
      new LoopDescriptor(null, "LQ", 2, "/PO1/LM"),
      new LoopDescriptor(null, "LQ", 1, "/LM"),

      new LoopDescriptor(".", "LS", 1, "/PO1"),

      new LoopDescriptor(null, "MAN", 3, "/PO1/N1/LDT"),
      new LoopDescriptor(null, "MAN", 1, "/PO1"),
      new LoopDescriptor(".", "MAN", 0, "*"),

      new LoopDescriptor(null, "MEA", 2, "/PO1/PID"),
      new LoopDescriptor(null, "MEA", 1, "/PO1"),
      new LoopDescriptor(".", "MEA", 0, "*"),

      new LoopDescriptor(null, "MSG", 3, "/PO1/N1/LDT"),
      new LoopDescriptor(null, "MSG", 2, "/SPI/N1"),
      new LoopDescriptor(null, "MSG", 1, "/N9"),

      new LoopDescriptor(null, "MTX", 1, "/ADV"),

      new LoopDescriptor("N1", "N1", 3, "/PO1/SLN"),
      new LoopDescriptor("N1", "N1", 2, "/PO1"),
      new LoopDescriptor("N1", "N1", 2, "/SPI"),
      new LoopDescriptor("N1", "N1", 1, "*"),

      new LoopDescriptor(null, "N2", 3, "/PO1/SLN/N1"),
      new LoopDescriptor(null, "N2", 2, "/SPI/N1"),
      new LoopDescriptor(null, "N2", 1, "/N1"),

      new LoopDescriptor(null, "N3", 3, "/PO1/SLN/N1"),
      new LoopDescriptor(null, "N3", 2, "/SPI/N1"),
      new LoopDescriptor(null, "N3", 1, "/N1"),

      new LoopDescriptor(null, "N4", 3, "/PO1/SLN/N1"),
      new LoopDescriptor(null, "N4", 2, "/SPI/N1"),
      new LoopDescriptor(null, "N4", 1, "/N1"),

      new LoopDescriptor("N9", "N9", 3, "/PO1/SLN"),
      new LoopDescriptor("N9", "N9", 2, "/PO1"),
      new LoopDescriptor("N9", "N9", 1, "*"),

      new LoopDescriptor(null, "NX2", 3, "/PO1/SLN/N1"),
      new LoopDescriptor(null, "NX2", 2, "/PO1/N1"),
      new LoopDescriptor(null, "NX2", 1, "/N1"),

      new LoopDescriptor(null, "PAM", 2, "/PO1/SLN"),
      new LoopDescriptor(".", "PAM", 1, "/PO1"),
      new LoopDescriptor(".", "PAM", 0, "*"),

      new LoopDescriptor(null, "PCT", 2, "/PO1/AMT"),
      new LoopDescriptor(null, "PCT", 1, "/PO1"),
      new LoopDescriptor(null, "PCT", 1, "/AMT"),
      new LoopDescriptor(".", "PCT", 0, "*"),

      new LoopDescriptor(null, "PER", 3, "/PO1/SLN/N1"),
      new LoopDescriptor(null, "PER", 2, "/PO1/N1"),
      new LoopDescriptor(null, "PER", 1, "/PO1"),
      new LoopDescriptor(null, "PER", 1, "/N1"),
      new LoopDescriptor(".", "PER", 0, "*"),

      new LoopDescriptor(null, "PID", 2, "/PO1/SLN"),
      new LoopDescriptor("PID", "PID", 2, "/PO1"),
      new LoopDescriptor(".", "PID", 0, "*"),

      new LoopDescriptor(null, "PKG", 3, "/PO1/N1"),
      new LoopDescriptor("PKG", "PKG", 2, "/PO1"),
      new LoopDescriptor(null, "PKG", 1, "/N1"),
      new LoopDescriptor(".", "PKG", 0, "*"),

      new LoopDescriptor("PO1", "PO1", 1, "*"),

      new LoopDescriptor(null, "PO3", 2, "/PO1/SLN"),
      new LoopDescriptor(null, "PO3", 1, "/PO1"),

      new LoopDescriptor(null, "PO4", 2, "/PO1/SLN"),
      new LoopDescriptor(null, "PO4", 1, "/PO1"),

      new LoopDescriptor(".", "PWK", 1, "/PO1"),
      new LoopDescriptor(".", "PWK", 0, "*"),

      new LoopDescriptor(null, "QTY", 3, "/PO1/N1/LDT"),
      new LoopDescriptor("QTY", "QTY", 3, "/PO1/SLN"),
      new LoopDescriptor(null, "QTY", 3, "/PO1/LDT"),
      new LoopDescriptor(null, "QTY", 3, "/PO1/N1"),
      new LoopDescriptor("QTY", "QTY", 2, "/PO1"),

      new LoopDescriptor(null, "REF", 3, "/PO1/N1/LDT"),
      new LoopDescriptor(null, "REF", 2, "/SPI/N1"),
      new LoopDescriptor(null, "REF", 1, "/SPI"),
      new LoopDescriptor(null, "REF", 1, "/PO1"),
      new LoopDescriptor(null, "REF", 1, "/AMT"),
      new LoopDescriptor(null, "REF", 1, "/N1"),
      new LoopDescriptor(".", "REF", 0, "*"),

      new LoopDescriptor("SAC", "SAC", 3, "/PO1/SLN"),
      new LoopDescriptor("SAC", "SAC", 2, "/PO1"),
      new LoopDescriptor("SAC", "SAC", 1, "*"),

      new LoopDescriptor(null, "SCH", 3, "/PO1/N1"),
      new LoopDescriptor("SCH", "SCH", 2, "/PO1"),

      new LoopDescriptor(null, "SDQ", 1, "/PO1"),

      new LoopDescriptor(".", "SE", 0, "*"),

      new LoopDescriptor(null, "SI", 3, "/PO1/SLN/QTY"),
      new LoopDescriptor(null, "SI", 2, "/PO1/QTY"),
      new LoopDescriptor(null, "SI", 2, "/PO1/SLN"),
      new LoopDescriptor(null, "SI", 1, "/N1"),
      new LoopDescriptor(".", "SI", 0, "*"),

      new LoopDescriptor("SLN", "SLN", 2, "/PO1"),

      new LoopDescriptor(null, "SPI", 1, "/PO1"),
      new LoopDescriptor("SPI", "SPI", 1, "*"),

      new LoopDescriptor(".", "ST", 0, "*"),

      new LoopDescriptor(null, "TAX", 2, "/PO1/SLN"),
      new LoopDescriptor(null, "TAX", 1, "/PO1"),
      new LoopDescriptor(".", "TAX", 0, "*"),

      new LoopDescriptor(null, "TC2", 2, "/PO1/SLN"),
      new LoopDescriptor(null, "TC2", 1, "/PO1"),
      new LoopDescriptor(".", "TC2", 0, "*"),

      new LoopDescriptor(null, "TD1", 2, "/PO1/SCH"),
      new LoopDescriptor(null, "TD1", 1, "/PO1"),
      new LoopDescriptor(null, "TD1", 1, "/N1"),
      new LoopDescriptor(".", "TD1", 0, "*"),

      new LoopDescriptor(null, "TD3", 2, "/PO1/SCH"),
      new LoopDescriptor(null, "TD3", 1, "/PO1"),
      new LoopDescriptor(null, "TD3", 1, "/N1"),
      new LoopDescriptor(".", "TD3", 0, "*"),

      new LoopDescriptor(null, "TD4", 2, "/PO1/SCH"),
      new LoopDescriptor(null, "TD4", 1, "/PO1"),
      new LoopDescriptor(null, "TD4", 1, "/N1"),
      new LoopDescriptor(".", "TD4", 0, "*"),

      new LoopDescriptor(null, "TD5", 2, "/PO1/SCH"),
      new LoopDescriptor(null, "TD5", 1, "/PO1"),
      new LoopDescriptor(null, "TD5", 1, "/N1"),
      new LoopDescriptor(".", "TD5", 0, "*"),

      new LoopDescriptor(null, "TXI", 1, "/PO1"),
      new LoopDescriptor(".", "TXI", 0, "*"),
    };
  }
}
