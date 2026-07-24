## EDIReader Community Edition

EDIReader is a flexible and lightweight EDI parser, written in pure Java using the SAX API
allowing for many integration options. Released as open source (GPL3) in 2004 and enhanced steadily since then,
it has handled millions of transactions in a wide variety of products, services, industries, platforms,
and custom integrations.

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-21%2B-orange.svg)](pom.xml)

[ReleaseNotes.md](ReleaseNotes.md)

Looking for an on-premise REST API engine built on EDIReader?
Check out [BerryWave EDI API](https://github.com/RBMayberry/BerryWave-EDI-API) 

### Quick Start

#### Basic Java Usage Example

```java
import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIReaderFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.FileReader;

public class ParseExample {
    public static void main(String[] args) throws Exception {
        // Create an EDIReader instance for an input stream
        InputSource inputSource = new InputSource(new FileReader("sample.edi"));
        XMLReader ediReader = EDIReaderFactory.createEDIReader(inputSource);

        // Attach a SAX ContentHandler (e.g., custom handler or standard XML transformer)
        ediReader.setContentHandler(new MySaxHandler());
        
        // Parse the EDI stream
        ediReader.parse(inputSource);
    }
}
```

#### Features Summary

The EDI parser supports:
* Automatic detection of EDI standard and syntax characters (terminators, delimiters, separators)
* X12 and EDIFACT
* Segment loops:
    - detects segment loops/groups within a transaction/message
    - using EDIReader plugins
* Handles multiple:
    - interchanges per input stream
    - functional groups per interchange
    - transactions/messages per functional group
* Command line tools:
    - EDI to XML
    - splitting EDI input into multiple XML single-transaction output files
    - scanning EDI input to produce a summary of transactions
* Java API
    - based on XML push-parser patterns
    - can be configured with custom SAX content handlers 
* Detailed error messages for EDI syntax issues
    - for example: *Segment count error in UNT segment. Expected 8 instead of 88 at segment 9, field 2*
    - option to continue parsing after recoverable errors
* High performance
    - parses arbitrarily large input streams
    - without growing in-memory data structures or file I/O
* Acknowledgments as a by-product of parsing
    - 997, 999 (X12)
    - CONTRL (EDIFACT)
* Binary sequences
    - BIN segment (X12)
    - UNO/UNP (EDIFACT)


#### Primary Interfaces

* Command line interface tools accepting filename arguments
* Java API for embedding in your own Java system


#### Technical Notes

* Pure Java: Zero third-party library dependencies (except SLF4J), avoiding dependency conflicts and licensing baggage. Compatible with standard JVMs and Android.
* Logging: Uses Simple Logging Facade for Java (SLF4J) for lightweight deployment-time binding (Logback, Log4j2, java.util.logging).
* Thread-Safe: Designed for concurrent execution in high-throughput enterprise applications.


#### License and Ownership

* Intellectual Property: Copyright BerryWorks Software.
* Open Source License: Distributed under the GNU General Public License v3.0 (GPLv3).
* Commercial Licensing: Commercial licenses (without GPL constraints) are available from BerryWorks Software, along with support and maintenance agreements for end-users and service providers.

#### The EDIReader Framework

The EDIReader Framework is a set of Java modules built on top of the Community Edition.
Unlike the Community Edition, it is not released as open source but may be licensed from BerryWorks Software.
It adds many additional EDI features such as:

* EDIWriter, producing EDI output
* EDI Annotations, augmenting the parsed content from EDIReader with
    - transaction/message descriptions
    - segment descriptions
    - element and sub-element descriptions
    - code value descriptions
* Segment loop awareness
* Splitting EDI input containing many transactions into many single-transaction EDI output files
* EDI validation and compliance checking
* Support for additional EDI and EDI-like formats:
    - HL7
    - NCPDP
    - TRADACOMS
* JSON support
    - EDI to JSON
    - JSON to EDI


