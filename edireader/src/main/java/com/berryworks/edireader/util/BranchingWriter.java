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

package com.berryworks.edireader.util;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * An implementation of Writer that supports two alternate versions
 * of the written output.
 * <p/>
 * As a matter of convention, the two version are termed the "trunk" and the "branch".
 * In addition to the normal write methods which write data to both the trunk and the branch,
 * methods are provided to write different data to the trunk and/or the branch.
 * <p/>
 * If this Writer is closed using the normal close method, then the trunk version is used
 * and the branch is discarded.
 * A closeUsingBranch() method is provided which causes the branch version to be used instead
 * of the trunk version.
 * <p/>
 * Once the two versions diverge, the alternate versions are buffered in memory.
 * Therefore, this implementation is not appropriate for arbitrarily large volumes of data.
 */
public class BranchingWriter extends FilterWriter
{

  private boolean branchActive;
  private StringWriter branch;
  private StringWriter trunk;

  public BranchingWriter(Writer out)
  {
    super(out);
  }

  @Override
  public void write(int c) throws IOException
  {
    if (branchActive)
    {
      trunk.write(c);
      branch.write(c);
    }
    else
      super.write(c);
  }

  @Override
  public void write(char cbuf[], int off, int len) throws IOException
  {
    if (branchActive)
    {
      trunk.write(cbuf, off, len);
      branch.write(cbuf, off, len);
    }
    else
      super.write(cbuf, off, len);
  }

  @Override
  public void write(String str, int off, int len) throws IOException
  {
    if (branchActive)
    {
      trunk.write(str, off, len);
      branch.write(str, off, len);
    }
    else
      super.write(str, off, len);
  }

  @Override
  public void flush() throws IOException
  {
    if (branchActive)
    {
      trunk.flush();
      branch.flush();
    }
    else
      super.flush();
  }

  @Override
  public void close() throws IOException
  {
    if (branchActive)
    {
      out.write(trunk.toString());
      out.close();
    }
    else
      super.close();
  }

  public void writeTrunk(String s)
  {
    if (!branchActive)
    {
      branchActive = true;
      trunk = new StringWriter();
      branch = new StringWriter();
    }
    trunk.write(s);
  }

  public void writeBranch(String s)
  {
    if (!branchActive)
    {
      branchActive = true;
      trunk = new StringWriter();
      branch = new StringWriter();
    }
    branch.write(s);
  }

  public void closeUsingBranch() throws IOException
  {
    if (branchActive)
    {
      out.write(branch.toString());
      out.close();
    }
    else
      super.close();
  }
}
