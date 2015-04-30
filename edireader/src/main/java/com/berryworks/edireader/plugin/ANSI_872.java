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
 * Plugin for the ANSI 872 Residential Mortgage Insurance Application
 */
public class ANSI_872 extends Plugin
{

  public ANSI_872()
  {
    super("827", "Residential Mortgage Insurance Application");
    loops = new LoopDescriptor[]{new LoopDescriptor("LX", "LX", 1),
      new LoopDescriptor("LRQ", "LRQ", 2, "LX"),
      new LoopDescriptor("LRQ.SCM", "SCM", 3, "LRQ"),
      new LoopDescriptor("LRQ.SCM", "SCM", 3, "LRQ.SCM"),
      new LoopDescriptor("LRQ.NX1", "NX1", 3, "LRQ"),
      new LoopDescriptor("LRQ.NX1", "NX1", 3, "LRQ.SCM"),
      new LoopDescriptor("LRQ.NX1", "NX1", 3, "LRQ.NX1"),
      new LoopDescriptor("LRQ.NX1", "NX1", 3, "PAS"),
      new LoopDescriptor("PAS", "PAS", 4),
      new LoopDescriptor("IN1", "IN1", 3),
      new LoopDescriptor("IN1.SCM", "SCM", 4, "IN1"),
      new LoopDescriptor("IN1.SCM", "SCM", 4, "IN1.SCM"),
      new LoopDescriptor("IN1.NX1", "NX1", 4, "IN1"),
      new LoopDescriptor("IN1.NX1", "NX1", 4, "IN1.SCM"),
      new LoopDescriptor("IN1.NX1", "NX1", 4, "IN1.NX1"),
      new LoopDescriptor("REA", "REA", 3),
      new LoopDescriptor("MCD", "MCD", 3),
      new LoopDescriptor("BUY", "BUY", 3),
      new LoopDescriptor("PRJ", "PRJ", 3)};
  }
}
