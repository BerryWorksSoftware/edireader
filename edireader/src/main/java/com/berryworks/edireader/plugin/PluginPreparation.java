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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A runtime data structure that optimizes the LoopDescriptors of a plugin
 * for access by an EDI parser.
 *
 * @see com.berryworks.edireader.Plugin
 */
public class PluginPreparation
{

  protected final Map<String, List<LoopDescriptor>> segmentMap = new HashMap<String, List<LoopDescriptor>>();

  /**
   * Constructs an instance given an array of LoopDescriptors.
   * <p/>
   * The LoopDescriptors typically are taken directly from the EDIPlugin for a given type of document.
   *
   * @param loops
   */
  public PluginPreparation(LoopDescriptor[] loops)
  {
    if (loops == null)
      return;
    for (LoopDescriptor loop : loops)
    {
      String segmentName = loop.getFirstSegment();
      List<LoopDescriptor> descriptorList = segmentMap.get(segmentName);
      if (descriptorList == null)
      {
        descriptorList = new ArrayList<LoopDescriptor>();
        segmentMap.put(segmentName, descriptorList);
      }
      descriptorList.add(loop);
    }
  }

  /**
   * Returns an ordered list of LoopDescriptors corresponding to loops that start with a
   * given segment name.
   * <p/>
   * The LoopDescriptors appear in the same order as they were mentioned in the plugin.
   *
   * @param segment
   * @return
   */
  public List<LoopDescriptor> getList(String segment)
  {
    return segmentMap.get(segment);
  }

}
