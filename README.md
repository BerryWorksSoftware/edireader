## EDIReader

## Introduction

EDIReader is a flexible and lightweight EDI parser, written in pure Java using the SAX API
allowing for many integration options. Released as open source (GPL) originally in 2004, it has
handled millions of transactions in a wide variety of products, services, industries, platforms,
and custom integrations.

## Features Summary

The EDI parser supports:
* Automatic detection of EDI standard and syntax characters (terminators, delimiters, separators)
* Native support for both X12 and EDIFACT
* Support for segment loops
    - detects segment loops/groups within a transaction/message
    - using EDIReader plugins
    - reflected in XML as nested <loop> elements
* Handles multiple:
    - interchanges per input stream
    - functional groups per interchange
    - transactions/messages per function group
* Command line tools for
    - EDI to XML
    - Splitting EDI input into multiple XML single-transaction output files
    - Scanning EDI input producing summary of transactions
* Detailed error messages for EDI syntax issues
    - for example: *Segment count error in UNT segment. Expected 8 instead of 88 at segment 9, field 2*
    - option to terminate or continue for recoverable errors
* High performance
* Generation of acknowledgments as a by-product of parsing
    - 997 (X12)
    - 999 (X12)
    - CONTRL (EDIFACT)
* Support for X12 binary sequences via the BIN segment

## Primary Interfaces

EDIReader may be easily integrated using:
* Command line interface tools accepting filename arguments
* Java API for embedding in your own Java system

## License and Ownership

* Intellectual property of BerryWorks Software
* Published as open source software under the GPL
* Can be licensed without GPL constraints
    - along with the EDIReader Framework containing value-added extensions
    - including support/maintenance agreement
    - for either End Users or Service Providers

## EDIReader Framework

The EDIReader Framework (not released as open source) provides a number of valued-added modules
providing features such as:

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
* Extensive library of version-specific plugins
* EDI validation and compliance checking, using XSDs purchased from X12
* Support for additional EDI and EDI-like formats:
    - HL7
    - NCPDP
    - TRADACOMS
* Splitting EDI input containing many transactions into many single-transaction EDI output files
* Includes EDI samples for many transactions/versions
* Includes suite of JUnit test cases
* Available with full Java source code as Maven project 

## Technical Notes

* Pure Java. No dependency third-party libraries.
    - avoids dependency version issues
    - avoids licensing issues
    - compatible with a wide variety of Java platforms, including Android
* Thread safe, used in multi-threading applications
* Compatible with Java 7 syntax
* Runnable with Java 7 and later

