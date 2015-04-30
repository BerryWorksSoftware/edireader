/*
 * Copyright 2005-2011 by BerryWorks Software, LLC. All rights reserved.
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

package com.berryworks.edireader.plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * An object that represents a segment loop within an EDI document.
 * <p/>
 * EDIReader uses a LoopStack, which is a stack of LoopContext objects, to track the
 * current nested segment loops while parsing an EDI document.
 * <p/>
 * In the UN/EDIFACT EDI standards, a segment loop is typically called
 * a segment group.
 * <p/>
 * The primary attribute of a LoopContext instance is simply the name of the segment loop.
 * However, when a ValidatingPlugin is used, the LoopContext is also used to track
 * how many times a given segment has been repeated, which mandatory segment types have been seen,
 * and the current position within an expected ordering of segment types.
 * <p/>
 *
 * @see LoopStack
 */
public class LoopContext {
    private final String loopName;
    private int segmentPosition;
    private int segmentRepetitions;
    private Set<String> mandatorySegments;

    /**
     * Construct a LoopContext with a specified loop name.
     *
     * @param loopName name of the loop
     */
    public LoopContext(String loopName) {
        this.loopName = loopName;
        segmentPosition = 0;
        segmentRepetitions = 0;
        mandatorySegments = new HashSet<>();
    }

    /**
     * Records the fact that a given segment has appeared.
     * <p/>
     * If that segment type is mandatory, then this appearance satisfies that requirement.
     *
     * @param segmentName segment type of the segment
     */
    public void noteSegmentPresence(String segmentName) {
        mandatorySegments.remove(segmentName);
    }

    /**
     * Returns true if and only if all of the mandatory segment requirements have been met.
     *
     * @return boolean true if the requirements are satisfied
     */
    public boolean isMandatorySegmentValidationSatisfied() {
        return mandatorySegments.isEmpty();
    }

    /**
     * Returns a String representation of the LoopContext
     * for testing and debugging purposes.
     *
     * @return String representation
     */
    @Override
    public String toString() {
        return "LoopContext loopName:" + loopName +
                " position:" + segmentPosition +
                " repetitions:" + segmentRepetitions +
                " mandatory:" + getMandatorySegmentsRemaining();
    }

    /**
     * Returns the name of the loop.
     *
     * @return String loop name
     */
    public String getLoopName() {
        return loopName;
    }


    /**
     * Returns the current value of the segment position attribute.
     *
     * @return segment position
     */
    public int getSegmentPosition() {
        return segmentPosition;
    }

    /**
     * Sets the segment position attribute.
     *
     * @param segmentPosition position
     */
    public void setSegmentPosition(int segmentPosition) {
        this.segmentPosition = segmentPosition;
    }

    /**
     * Returns the segment repetitions attribute
     *
     * @return segment repetitions
     */
    public int getSegmentRepetitions() {
        return segmentRepetitions;
    }

    /**
     * Sets the segment repetitions attribute.
     *
     * @param segmentRepetitions - maximum number of repetitions allowed
     */
    public void setSegmentRepetitions(int segmentRepetitions) {
        this.segmentRepetitions = segmentRepetitions;
    }

    /**
     * Returns a comma-separated list of mandatory segment names that have not been noted.
     *
     * @return String - comma-separated list
     */
    public String getMandatorySegmentsRemaining() {
        StringBuilder result = new StringBuilder();
        for (Object mandatorySegment : mandatorySegments) {
            if (result.length() > 0)
                result.append(", ");
            result.append(mandatorySegment);
        }
        return result.toString();
    }

    /**
     * Establishes the set of segment types that are mandatory for a valid instance of the segment loop.
     *
     * @param segmentSet - designates which segments are mandatory
     */
    public void setMandatorySegments(Set<String> segmentSet) {
        mandatorySegments = segmentSet;
    }
}
