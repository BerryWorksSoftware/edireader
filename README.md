## EDIReader Community Edition

#### Introduction

EDIReader is a flexible and lightweight EDI parser, written in pure Java using the SAX API
allowing for many integration options. Released as open source (GPL3) in 2004 and enhanced steadily since then,
it has handled millions of transactions in a wide variety of products, services, industries, platforms,
and custom integrations.

#### Features Summary

The EDI parser supports:
* Automatic detection of EDI standard and syntax characters (terminators, delimiters, separators)
* X12 and EDIFACT
* Segment loops:
    - detects segment loops/groups within a transaction/message
    - using EDIReader plugins
    - reflected in XML as nested <loop> elements
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

EDIReader may be easily integrated using:
* Command line interface tools accepting filename arguments
* Java API for embedding in your own Java system


#### Technical Notes

* Pure Java, with no dependence on third-party libraries
    - except for SLF4J as described below
    - avoids dependency version issues
    - avoids licensing issues
    - compatible with a wide variety of Java platforms, including Android
* Uses Simple Logging Facade for Java (SLF4J)
    - an ultra-thin logging API
    - allows deployment-time binding with log4j, java.util.logging, and other logging frameworks
* Thread safe, used in multi-threading applications
* Compatible with Java 8 syntax
* Runnable with Java 8 and later


#### License and Ownership

* Intellectual property of BerryWorks Software
* Published as open source software under the GPL
* Can be licensed without GPL constraints
    - along with the EDIReader Framework containing value-added extensions
    - including support/maintenance agreement
    - for either End Users or Service Providers

#### The EDIReader Framework

The EDIReader Framework is a set of Java modules built on top of the Community Edition.
Unlike the Community Edition, it is not released as open source but may be licensed from BerryWorks Software.
It adds many additional EDI features such as:

* EDIWriter, producing EDI output from XML of the style produced by EDIReader
* EDI Annotations, augmenting the XML from EDIReader with
    - transaction/message descriptions
    - segment descriptions
    - element and sub-element descriptions
    - code value descriptions
* Enhanced support for HIPAA transactions
    - 270, 271, 276, 277, 278, 834, 835, 837
    - Loop qualifiers included in XML (for example: 1000A, 2010AB)
    - HL hierarchies reflected via nested XML elements
* Segment loop awareness via an extensive library of version-specific plugins
* Splitting EDI input containing many transactions into many single-transaction EDI output files
* EDI validation and compliance checking, using XSDs purchased from X12
* Support for additional EDI and EDI-like formats:
    - HL7
    - NCPDP
    - TRADACOMS
* JSON support
    - EDI to JSON, analogous to EDI to XML
    - JSON to EDI, analogous to EDIWriter
    - see the edi-json project also at GitHub
* YAML support
    - EDI to YAML, annotated for human readability
* Includes EDI samples for many transactions/versions
* Includes suite of JUnit test cases
* Available with full Java source code as Maven project

