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

package com.berryworks.edireader.util.base64;

/**
 * This class is part of the internal implementation
 * of the base-64 encoding/decoding.
 */
public abstract class EncoderFrontEnd extends AbstractEncoderDecoder {

    @Override
    public void consume(byte b) {
        int partA, partB;
        switch (state) {
            default:
            case STATE62:
                partA = (b & 255) >>> 2;
                partB = b & 3;
                state = STATE44;
                break;
            case STATE44:
                partA = (b & 255) >>> 4;
                partB = b & 15;
                state = STATE26;
                break;
            case STATE26:
                partA = (b & 255) >>> 6;
                partB = b & 63;
                state = STATE62;
                break;
        }
        emit((byte) partA);
        emit((byte) partB);
    }

}
