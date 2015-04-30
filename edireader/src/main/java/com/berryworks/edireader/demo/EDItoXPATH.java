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

package com.berryworks.edireader.demo;

import com.berryworks.edireader.EDIReaderFactory;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class EDItoXPATH
{
  private final InputSource inputSource;

  public EDItoXPATH(Reader inputReader)
  {
    inputSource = new InputSource(inputReader);
  }

  public void run()
  {

    try
    {
      // Establish the EDIReader, acting as an XMLReader
      XMLReader ediReader = EDIReaderFactory.createEDIReader(inputSource);

      // Establish the SAXSource
      SAXSource source = new SAXSource(ediReader, inputSource);

      // Establish a DOMResult to capture the generated XML
      DOMResult domResult = new DOMResult();

      // Call an XSL Transformer with no stylesheet to transform
      // the SAX output from EDIReader into a W3C DOM
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.transform(source, domResult);

      // Get the W3C DOM from the transformed domResult
      Node document = domResult.getNode();

      // Create a default XPath which is expecting a W3C DOM
      XPath xPath = XPathFactory.newInstance().newXPath();

      // Evaluate a particular XPath expression against the DOM
      String query = "/ediroot/interchange/group/transaction/@DocType";
      String xPathResult = xPath.evaluate(query, document);

      System.out.print("\nXPath query " + query + " produces result " + xPathResult + "\n");

    } catch (SAXException e)
    {
      System.err.println("\nUnable to create EDIReader: " + e);
    } catch (TransformerConfigurationException e)
    {
      System.err.println("\nUnable to create Transformer: " + e);
    } catch (TransformerException e)
    {
      System.err.println("\nUnable to transform EDI into DOM: " + e);
    } catch (IOException e)
    {
      System.err.println("\nUnable to read EDI input: " + e);
    } catch (XPathExpressionException e)
    {
      System.err.println("\nInvalid XPath expression: " + e);
    }
  }

  public static void main(String args[])
  {
    String inputFileName = null;

    if (args.length == 1)
      inputFileName = args[0];
    else
      badArgs();

    // Establish input
    Reader inputReader;
    if (inputFileName == null)
    {
      inputReader = new InputStreamReader(System.in);
    }
    else
    {
      try
      {
        inputReader = new InputStreamReader(
          new FileInputStream(inputFileName), "ISO-8859-1");
      } catch (IOException e)
      {
        System.out.println(e.getMessage());
        throw new RuntimeException(e.getMessage());
      }
    }

    EDItoXPATH theObject = new EDItoXPATH(inputReader);
    theObject.run();
  }

  /**
   * Print summary of command line arguments expected.
   */
  private static void badArgs()
  {
    System.err.println("Usage: EDItoXPATH inputfile");
    throw new RuntimeException("Missing or invalid command line arguments");
  }

}
