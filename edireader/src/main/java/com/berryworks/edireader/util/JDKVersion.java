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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Utility program that reads a given Java .class file
 * and determines which JDK version was used in its compilation.
 */
public class JDKVersion
{
  public static void main(String[] args) throws Exception
  {
    if (args.length != 1)
    {
      System.err.println("Usage: java version <.class file>");
      throw new RuntimeException("Missing or invalid command line arguments");
    }

    if (!new File(args[0]).exists())
    {
      System.err.println(args[0] + " does not exist!");
      throw new RuntimeException();
    }

    DataInputStream dis = new DataInputStream(
      new FileInputStream(args[0]));
    int magic = dis.readInt();
    if (magic != 0xcafebabe)
    {
      System.err.println(args[0] + " is not a .class file");
      throw new RuntimeException();
    }

    int minor = dis.readShort();
    int major = dis.readShort();
    System.out.println("class file version is " + major + "." + minor);

    String version;

    if (major < 48)
    {
      version = "1.3.1";
    }
    else if (major == 48)
    {
      version = "1.4.2";
    }
    else if (major == 49)
    {
      version = "1.5";
    }
    else if (major == 50)
    {
      version = "6";
    }
    else
    {
      version = "7";
    }
    System.out.println("You need to use JDK " + version + " or above");
  }
}
