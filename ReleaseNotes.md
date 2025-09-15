# Release Notes

### To DO
* Handle UNOB, UNOC, etc. in EDIFACT UNB segment.

### 5.9.0 - ???

* Feature: When parsing EDIFACT, choose the character set encoding based on the value of UNB-2.
  For example, UNB+UNOE indicates that ISO-8859-5 (Cyrillic) is to be used.
* Maintenance: In support of the new feature where the character set may be selected by inspecting
  an initial portion of the EDI input, refactoring was done in how an EDI stream is previewed.
  All of the changes are expected to be fully backward compatible.

### 5.8.5 - June 16, 2025

* Feature: Expand the `TransactionCallback` interface to provide a means for the parser to announce the size of each
  transaction, along with the transaction type, control number, and version. It also returns the same information for
  each functional group. Setting a TransactionCallback implementation on an EDIReader has been available since the
  earliest versions, but is little used because much richer information is available in other ways. The exact size
  in characters, however, is something best done with a simple (non-breaking) expansion to the TransactionCallback interface.

### 5.8.4 - June 4, 2025

* Maintenance: When an EDIReader instance is used as an XMLReader, getProperty(name) now returns null
  instead of throwing an exception. This allows use with in some javax.xml.transform.Transformer
  situations that would fail otherwise.

### 5.8.3 - December 5, 2024

* Maintenance: Use Java 17 as the default.
  If Java 21 is preferred, the `<source>` and `<target>` can be adjusted and the project rebuilt without issue.
  If Java 11 or 14 is needed, a branch can be easily created with the few adjustments need to avoid multi-line strings.

* Maintenance: Update Maven plugin versions.
* Maintenance: Various inspection-driven local refactoring cleanup/simplifications.

### 5.8.2 - April 14, 2024

* Maintenance: Adjust handling of an obscure special case involving repetition separator and ISA11.
  Up through and including X12 version 004010, ISA11 contained a standards id, typically U. Starting with 004020,
  ISA11 contains a repetition separator to separate values for an element that is repeated.
  In the situation where an X12 interchange of version 004010 or earlier appears to contain a
  repetition separator, such as ^, instead of U or other letter, the repetition separator in ISA11 is ignored;
  if that character appears within a segment, it is treated as a part of the data and not as an indicator of
  element repetition. Before this change, a character in ISA that appeared to be a valid repetition separator was used
  as such, regardless of the X12 version.

### 5.8.1 - January 30, 2024

* Feature: Detect the appearance of two adjacent segment terminators without an intervening segment,
  and throw a (recoverable) RepeatedSegmentTerminatorException.
* Maintenance: Minor adjustment in an internal class, EDIReaderSAXAdapter,
  to allow its subclasses to respond to the first segment in a loop
  differently than to a subsequent segment in the loop.

### 5.8.0 - November 11, 2023

* Feature: Use Java Service Provider (SPI) to find a PluginControllerFactory
  for managing the dynamic selection of transaction and version specific plugins
  during EDI parsing.
  If one is not available from a Service Provider on the classpath,
  then the legacy behavior applies which loads plugins from Java classes;
  therefore, backward compatibility is preserved.
  This new feature allows the EDIReader Framework
  (a non-open source project built on top of this core project)
  to cleanly and securely provide a PluginControllerFactory that uses its own ESD syntax to externalize plugins
  from Java source code.
* Convenience: Add methods to accept Reader instead of InputSource,
  simplifying the Java API, while preserving backward compatibility.
* Convenience: When parse() is called on an EDIReader instance without a configured ContentHandler,
  use a DefaultHandler that does nothing with the parsed EDI content.
* Maintenance: Add a static instance of a PluginControllerFactory allowing the most recently created one to be
  accessed as a singleton.

### 5.7 - skipped

* In order to synchronize versioning with the separate EDIReader Framework project,
  version 5.7 is skipped.

### 5.6.11 - November 2, 2023

* Fix: When producing an X12 acknowledgment as a parsing by-product,
  use a proper length for each GS element, truncating or padding as necessary to comply with min:max rules.
  For some GS elements, the min:max length varies between X12 versions.
  For example, the GS04 date element has minimum and maximum length of 8 beginning with 004010 and 6 before.

### 5.6.10 - October 3, 2023

* Maintenance: Change from INFO-level to DEBUG-level
  the logging of every document type as it is parsed.

### 5.6.9 - August 27, 2023

* Feature: Use the plugin, if available, to recognize a composite element with only its first sub-element,
  which looks identical to a simple element since no sub-element delimiter is visible. In this situation, the parsed
  EDI content reflects a composite element instead of a simple element.
* Maintenance: Reduce debug logging that is no longer needed.
* Maintenance: Use latest versions of Maven plugins and dependencies.

### 5.6.8 - July 18, 2023

* Maintenance: Revise ParserRegistry class in order to eliminate any potential
  for malicious creation of a custom parser class that gets control in response to carefully
  crafted input data.
* Fix: With the XmlFormatter, format XML correctly when it includes an attribute value that contains a / (slash).
  Such a slash must not be confused with a slash that closes an XML element.

### 5.6.7 - May 24, 2023

* Maintenance: Use latest versions of Maven plugins.
* Maintenance: Reduce dependencies on third-party libraries (even great ones!)
  whenever possible to simplify build process and minimize footprint.
  With this release, there is no longer a dependence on xmlunit or JQuantify.
* Maintenance: Remove the internal benchmarking tool, moving it to the EDIReader Framework
  project.
* Feature: Create a compare method in DocumentUtil to compare two
  org.w3c.dom.Document objects.

### 5.6.6 - December 16, 2022

* Fix: Pass the correct receiver address to the process() method when parsing an X12 envelope. The process() method
  allows a subclass of AnsiReader to easily grab EDI addresses from the ISA and GS envelope. This feature is not used
  within the project, so the fixed bug is visible only with a custom AnsiReader subclass. With this fix, the process()
  hook is extended to also provide access to ISA, GS, and ST control numbers and the version indicated in the GS.

### 5.6.5 - September 29, 2022

* Fix: Simplify to a single-module Maven project, removing the unnecessary complexity of parent/child modules when
  there is only one child.

### 5.6.4 - September 26, 2022

* Feature: Provide a MaskingTool as a utility class allowing two text items
  to be compared where certain fields (for example, date and time fields) are
  disregarded. This is primarily for testing purposes.
* Fix: Improve support for CONTRL messages produced as an optional by-product
  of EDIFACT parsing.

### 5.6.3 - June 24, 2022

* Feature: When splitting, let the syntax details of the original EDI input be easily accessible
  which may be useful to the system handling the individual transactions.

### 5.6.2 - May 1, 2022

* Fix: Avoid potential for a null pointer exception when using a plugin with condition flags. For a few
  transaction/message types, the same segment type can mark the beginning of two different loop/group types;
  condition flags are used to disambiguate such situations.

### 5.6.1 - April 1, 2022

* Feature: Add min and max occurrence attributes to a LoopDescriptor. Among other things, this allows an easy
  way to treat repeatable and non-repeatable segments in a particular loop context differently. For example,
  when rendering EDI as JSON, you might want to use a JSON key:object pair for a non-repeatable segment, but
  a JSON array for a repeatable segment.

### 5.6.0 - January 25, 2022

* Feature: An EDI element that contains only spaces is, by default, treated as an omitted element within the segment.
  Add an option to treat these spaces as data instead. This option is NOT recommended for normal use.
* Refactor: Revise handling of the fixed length ISA elements, preserving full backward compatibility.

### 5.5.3 - May 15, 2020

* Feature: In the EDI splitter tool, let the test indicator, "P" or "T", be available as a component when
  naming the split files.

### 5.5.2 - March 23, 2020

* Fix: Use CharBuffer with compatibility for Java 11 a well as previous versions.
  Avoid NoSuchMethodError when using limit() or clear().

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

