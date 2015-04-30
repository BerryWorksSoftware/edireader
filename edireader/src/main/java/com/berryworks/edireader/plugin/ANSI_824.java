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
 * Plugin for the ANSI 824 Application Advice
 */
public class ANSI_824 extends Plugin
{

  public ANSI_824()
  {
    super("824", "Application Advice");
    loops = new LoopDescriptor[]{new LoopDescriptor("N1", "N1", 1),
      new LoopDescriptor("OTI", "OTI", 1),
      new LoopDescriptor("TED", "TED", 2),
      new LoopDescriptor("LM", "LM", 2),
      new LoopDescriptor("LQ", "LQ", 3),};
  }
}