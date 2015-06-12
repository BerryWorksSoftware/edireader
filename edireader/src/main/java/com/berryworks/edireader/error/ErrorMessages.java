/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
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

package com.berryworks.edireader.error;

public interface ErrorMessages {
    String ELEMENT_TOO_LONG = "Too many characters in an element (delimiter problem?)";
    String EXPECTED_SIMPLE_TOKEN = "Expected a simple token";
    String MALFORMED_EDI_SEGMENT = "Malformed EDI segment";
    String INTERNAL_ERROR_MULTIPLE_PREVIEWS = "Internal error: interchange previewed more than once";
    String INTERNAL_ERROR_MULTIPLE_EOFS = "End-of-file hit multiple times (internal error)";
    String DIGITS_ONLY = "Element must contain only digits";
    String INVALID_COMPOSITE = "Invalid composite element";
    String INVALID_SEGMENT_TERMINATOR = "Invalid segment terminator";
    String UNEXPECTED_EOF = "Unexpected end of data";
    String INVALID_BEGINNING_OF_SEGMENT = "Invalid beginning of segment";
    String UNEXPECTED_SEGMENT_IN_CONTEXT = "Unexpected segment type in this context";
    String X12_MISSING_ISA = "ANSI X.12 interchange must begin with ISA";
    String TOO_MANY_ISA_FIELDS = "Too many fields for an ISA (Segment terminator problem?)";
    String ISA_FIELD_WIDTH = "Incorrect length of fixed-length ISA field";
    String ISA_SEGMENT_HAS_TOO_FEW_FIELDS = "ISA segment requires 16 instances of the field delimiter";
    String INCOMPLETE_X12 = "Incomplete ANSI X.12 interchange";
    String CONTROL_NUMBER_IEA = "Control number error in IEA segment";
    String CONTROL_NUMBER_GE = "Control number error in GE segment";
    String CONTROL_NUMBER_SE = "Control number error in SE segment";
    String COUNT_IEA = "Functional group count error in IEA segment";
    String COUNT_GE = "Transaction count error in GE segment";
    String COUNT_SE = "Segment count error in SE segment";
    String INVALID_UNA = "Improperly formed UNA segment";
    String CONTROL_NUMBER_UNZ = "Control number error in UNZ segment";
    String CONTROL_NUMBER_UNT = "Control number error in UNT segment";
    String COUNT_UNZ = "Functional group count error in UNZ segment";
    String COUNT_UNT = "Segment count error in UNT segment";
    String FIRST_SEGMENT_MUST_BE_UNA_OR_UNB = "First segment must be UNA or UNB";
    String NO_HL7_PARSER = "Data begins with MSH indicating HL7 data, but no HL7 parser is available";
    String INCOMPLETE_HL7_MESSAGE = "Incomplete HL7 message";
    String INCOMPLETE_ACH_MESSAGE = "Incomplete ACH file";
    String NO_STANDARD_BEGINS_WITH = "No supported EDI standard interchange begins with ";
    String XML_INSTEAD_OF_EDI = "Encountered XML when expecting EDI";
    String SE_MISSING = "Transaction must be terminated with an SE segment";
    String MANDATORY_ELEMENT_MISSING = "Mandatory element missing";
    String MISSING_UNP = "UNP segment not properly positioned after UNO segment and data object sequence";
    String MISMATCHED_UNP_LENGTH = "UNP segment contains length field that does not match length field in UNO";
    String MISMATCHED_PACKAGE_REF = "UNP segment contains package reference that does not match corresponding reference in UNO";
    String MISSING_UNO_LENGTH = "UNO segment missing mandatory length field";
    String TRADACOMS_MISSING_STX = "TRADACOMS interchange must begin with STX";
    String SEQUENCE_MHD = "Message sequence error in MHD segment";
    String COUNT_MTR = "Segment count error in MTR segment";
    String COUNT_END = "Message count error in END segment";
    String CONTROL_NUMBER_RSG = "Sender's transmission reference error in RSG reconciliation segment";
    String RECIPIENT_RSG = "Recipient error in RSG reconciliation segment";
}
