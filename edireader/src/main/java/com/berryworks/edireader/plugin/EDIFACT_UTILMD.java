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
 * Plugin for the EDIFACT UTILMD Utilities Master Data Message
 */
public class EDIFACT_UTILMD extends Plugin
{

  public EDIFACT_UTILMD()
  {
    super("UTILMD", "Utilities Master Data Message");
    loops = new LoopDescriptor[]{
      new LoopDescriptor("SG10", "CCI", 3, "/SG4/SG8"),
      new LoopDescriptor("SG7", "CCI", 2, "/SG4"),

      new LoopDescriptor(".", "CNT", 0, ANY_CONTEXT),

      new LoopDescriptor("SG13", "CTA", 3, "/SG4/SG12"),
      new LoopDescriptor("SG3", "CTA", 2, "/SG2"),

      new LoopDescriptor("SG4", "IDE", 1, ANY_CONTEXT),

      new LoopDescriptor("SG5", "LOC", 2, "/SG4"),

      new LoopDescriptor("SG11", "MOA", 2, ANY_CONTEXT),

      new LoopDescriptor("SG12", "NAD", 2, "/SG4"),
      new LoopDescriptor("SG2", "NAD", 1, ANY_CONTEXT),

      new LoopDescriptor("SG9", "QTY", 3, ANY_CONTEXT),

      new LoopDescriptor(null, "RFF", 3, "/SG4/SG11"),
      new LoopDescriptor(null, "RFF", 3, "/SG4/SG12"),
      new LoopDescriptor("SG6", "RFF", 2, "/SG4"),
      new LoopDescriptor("SG1", "RFF", 1, ANY_CONTEXT),

      new LoopDescriptor("SG8", "SEQ", 2),


    };
  }
}
