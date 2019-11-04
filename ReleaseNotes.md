# Release Notes

### 5.5.1 - November 4, 2019

* Feature: Use SLF4J as a thin logging facade or abstraction for various logging frameworks,
allowing the end user to plug in the desired logging framework at deployment time or to run with no
logging framework at all.

### 5.5.0 - October 31, 2019

* Update: Use Java 8 instead of Java 7

### 5.4.20 - September 30, 2019

* Feature: Support reset() on a PluginController to restore the internal state to its initial condition, allowing
a controller to be reused for multiple documents of the same type. This is not used with X12 or EDIFACT, but comes
in handy with TRADACOMS available with the premium edition.

### 5.4.19 - August 8, 2019

* Fix: With this change, the EDIFACT UNB interchange header segment allows the syntax identifier to contain
any letter after the UNO instead of restricting to A-K. For example, UNB+UNOL:1+... is allowed.

### 5.4.18 - June 2, 2019

* Fix: A bug that resulted in an incorrect Id attribute on elements within a segment in a particular 
combination of repeating elements, composite elements, and empty elements. The situation was not observed
with X12 or EDIFACT documents, but in the HL7 parser available in the premium edition.

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

