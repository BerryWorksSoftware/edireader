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

package com.berryworks.edireader.util.dom;

import org.w3c.dom.Element;

import java.util.AbstractSequentialList;
import java.util.ListIterator;

public abstract class AbstractElementList extends AbstractSequentialList<Element>
{

  @Override
  public int size()
  {
    throw new RuntimeException("size() not supported");
  }

  protected abstract class AbstractElementListIterator implements ListIterator<Element>
  {

    public boolean hasPrevious()
    {
      throw new RuntimeException("hasPrevious() not implemented");
    }

    public Element previous()
    {
      throw new RuntimeException("previous() not implemented");
    }

    public int nextIndex()
    {
      throw new RuntimeException("nextIndex() not implemented");
    }

    public int previousIndex()
    {
      throw new RuntimeException("previousIndex() not implemented");
    }

    public void remove()
    {
      throw new RuntimeException("remove() not implemented");
    }

    public void set(Element element)
    {
      throw new RuntimeException("set() not implemented");
    }

    public void add(Element element)
    {
      throw new RuntimeException("add() not implemented");
    }
  }

}