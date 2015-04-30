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
 * Plugin for the Secure Authentication and Acknowledgement Message
 */
public class EDIFACT_AUTACK extends Plugin
{

  public EDIFACT_AUTACK()
  {
    super("AUTACK", "Secure Authentication and Acknowledgement Message");
    loops = new LoopDescriptor[]{
      new LoopDescriptor(null, "USA", 0, ANY_CONTEXT),
      new LoopDescriptor(".", "USB", 0, ANY_CONTEXT),
      new LoopDescriptor("SG2", "USC", 2, "/SG1"),
      new LoopDescriptor("SG1", "USH", 1, ANY_CONTEXT),
      new LoopDescriptor(null, "USR", 0, ANY_CONTEXT),
      new LoopDescriptor("SG4", "UST", 1, ANY_CONTEXT),
      new LoopDescriptor("SG3", "USX", 1, ANY_CONTEXT),
      new LoopDescriptor(null, "USY", 0, ANY_CONTEXT),
    };
  }
}
