# Release Notes

### 5.4.17 - April 2, 2019

* Feature: Provide a plugin for 837 version 5010 in the Community Edition
    
### 5.4.16 - March 15, 2019

* Feature: Allow specific ISA elements to be variable length instead of the X12-specific fixed length. 
    - ISA02 - authorization information
    - ISA04 - security information
    - ISA06 - sender ID
    - ISA08 - receiver ID

### 5.4.15 - March 7, 2019

* Feature: Added an option for X12 syntax characters to appear as attributes
on the \<interchange\>:
    - ElementDelimiter
    - SubElementDelimiter
    - SegmentTerminator
    - RepetitionSeparator (for later X12 versions)

### 5.4.14 - January 27, 2019

* Fix: The automatically-detected syntax characters (terminators, delimiters, separators, ...) were not all
accessible when parsing via the EDIReader class instead of a standard-specific class directly. This fixes
that problem.

### 5.4.12 - December 21, 2017

* Feature: Support the group-level sender and receiver qualifiers in the (rarely-used) EDIFACT UNG segment.

### 5.4.11 - December 24, 2017

* Feature: When generating a FA/997 as a byproduct of parsing X12 input, avoid hard-coded group control number; 
use the ISA control number instead.

### 5.4.10 - September 2, 2017

* Feature: In splitting operations where a group-level version is not available,
use information from the interchange instead.

### 5.4.9 - June 16, 2017

* Fix: When generating X12 FA/997s, be sure to use 0 as the ack request indicator in the ISA.

### 5.4.8 - June 8, 2017

* Feature: Refactor EDItoXML to ease subclassing.

### 5.4.7 - May 8, 2017

* Feature: Refactor EDItoXML and add command line option to generate 997 acknowledgment in addition to XML output.

### 5.4.5 - January 20, 2017

* Feature: Add callbacks to EDIReaderSAXAdapter to be notified at the beginning and end of a BIN (binary) X12 segment.
* Feature: Pass the GS sender and receiver to the splitter for use in naming split output files.
* Fix: An obscure bug with repeated elements involving two adjacent repetition separators.

