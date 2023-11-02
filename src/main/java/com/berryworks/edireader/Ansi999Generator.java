/*
 * Copyright 2005-2019 by BerryWorks Software, LLC. All rights reserved.
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

package com.berryworks.edireader;

import com.berryworks.edireader.util.BranchingWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * A delegate for generating an interchange containing some number of 999
 * transactions acknowledging the functional groups parsed by AnsiReader.
 */
public class Ansi999Generator extends AnsiFAGenerator {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private static final String CONTROL_NUMBER_999 = "0001";

    public Ansi999Generator(final StandardReader ansiReader, final BranchingWriter ackStream) {
        super(ansiReader, ackStream);
    }

    @Override
    public void generateAcknowledgmentHeader(String firstSegment,
                                             String groupSender, String groupReceiver, int groupDateLength,
                                             String groupVersion, String groupFunctionCode,
                                             String groupControlNumber) throws IOException {
        if (ackStream == null)
            return;

        // Do not generate an FA to acknowledge an FA
        if ("FA".equals(groupFunctionCode)) {
            skipFA = true;
            return;
        }
        skipFA = false;

        logger.debug("generating FA envelope");
        generateAcknowledgementPreamble(firstSegment, groupSender, groupReceiver, groupVersion);

        // Generate the ST 999
        logger.debug("generating first part of 999");
        thisDocumentCount++;
        ackStream.write("ST" + delimiter + "999" + delimiter + CONTROL_NUMBER_999 + delimiter + groupVersion);
        ackStream.write(terminatorWithSuffix);

        // Generate the AK1 segment to identify the group being acknowledged
        ackStream.write("AK1" + delimiter + groupFunctionCode + delimiter
                + groupControlNumber);
        ackStream.write(terminatorWithSuffix);
        headerGenerated = true;
    }

    @Override
    protected void generateTransactionAcknowledgmentUsing(String transactionCode, String controlNumber) {
        logger.debug("generating AK2/IK5");
        // Generate the AK2 segment to identify the transaction set
        ackStream.writeTrunk("AK2" + delimiter + transactionCode + delimiter
                + controlNumber);
        ackStream.writeTrunk(terminatorWithSuffix);

        // Generate the IK5 segment acknowledging the transaction set
        ackStream.writeTrunk("IK5" + delimiter + "A");
        ackStream.writeTrunk(terminatorWithSuffix);
    }


}
