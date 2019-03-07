# Release Notes

### Release 5.4.15 - March 7, 2019

* Feature: Added an option for X12 syntax characters to appear as attributes
on the \<interchange\>
    - ElementDelimiter
    - SubElementDelimiter
    - SegmentTerminator
    - RepetitionSeparator (for later X12 versions)

### Release 5.4.14 - January 27, 2019

* Fix: Properly reflect syntax characters in EDIReader when detected dynamically by AnsiReader subclass

### Release 5.4.12 - December 21, 2017

* Feature: Support the sender and receiver qualifiers in the (rarely-used) EDIFACT UNG segment

### Release 5.4.11 - December 24, 2017

* Feature: When generating a FA/997 as a byproduct of parsing X12 input, avoid hard-coded group control number; 
use the ISA control number instead 

### Release 5.4.10 - September 2, 2017

* Feature: In splitting operations where a group-level version is not available, use information from the interchange instead

### Release 5.4.9 - June 16, 2017

* Fix: When generatng X12 FA/997s, be sure to use 0 as the ack request indicator in the ISA 

### Release 5.4.8 - June 8, 2017

* Feature: Refactor EDItoXML to ease subclassing

### Release 5.4.7 - May 8, 2017

* Feature: Refactor EDItoXML and add command line option to generate 997 acknowledgment in addition to XML output

### Release 5.4.5 - January 20, 2017

* Feature: Add callbacks to EDIReaderSAXAdapter to be notified at the beginning and end of a BIN (binary) X12 segment.
on the \<interchange\>
* Feature: Pass the GS sender and receiver to the splitter for use in naming split output files
* Fix: An obscure bug with repeated elements involing two adjacent repetition separators

