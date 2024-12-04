/*
 * Copyright 2005-2022 by BerryWorks Software, LLC. All rights reserved.
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

import com.berryworks.edireader.Plugin;

import java.util.Set;
import java.util.TreeSet;

import static com.berryworks.edireader.util.FixedLength.isPresent;

/**
 * Static metadata about a segment loop (also known as a segment group) within an EDI document
 * (also known as transaction set or message). A sequence of LoopDescriptors
 * comprise the essence of a transaction set Plugin which allows a subclass of
 * EDIReader to consider the nested segment loops that are often important to
 * the semantics of a document.
 * <p>
 * <b>Overview.</b>
 * An instance of a LoopDescriptor in a plugin is an expression of a rule.
 * Generally speaking, when an EDIReader parser is parsing an EDI document
 * for which a plugin is available, it consults these LoopDescriptor rules
 * for each segment in the document to determine if the appearance of the
 * segment marks the beginning of a new segment loop.
 * <p>
 * For each segment, the parser evaluates each rule in order
 * and performs the action described
 * by the first rule that applies in the current context.
 * Any remaining rules are ignored, and if no rule is found that applies
 * to the segment in current context, then no action is taken;
 * in other words, the segment is treated simply as another segment in the
 * current segment loop.
 * <p>
 * <b>Basic attributes of a LoopDescriptor.</b>
 * There are four attributes of a LoopDescriptor which are specified in its constructor.
 * <p>
 * The <i>name</i> is simply the name of a segment loop described by this rule.
 * This name will appear as the value of the "Id" attribute in the loop element of the
 * generated XML.
 * There are a few special cases for this attribute that are described below.
 * <p>
 * The <i>nestingLevel</i> indicates how deeply nested is the loop. The segments of an
 * EDI document that are not within a segment loop are considered to be
 * in an implicit outer loop at nesting level 0.
 * <p>
 * The <i>firstSegment</i> attribute is the type of the segment that
 * marks the beginning of a new instance of the described segment loop.
 * For example a "PID" value might be used in a plugin for an ANSI 810 Invoice
 * document to define the segment loop that begins with the PID segment.
 * Similarly, a "LIN" value might be used in a plugin for
 * an EDIFACT INVOIC message to define a loop that begins with an LIN segment.
 * <p>
 * The <i>loopContext</i> attribute places a condition on when the
 * LoopDescriptor applies as described below.
 * <p><b>When does a LoopDescriptor rule apply?</b>
 * When the parser encounters a new segment while parsing an
 * EDI document, it consults LoopDescriptor rules to discover one that might apply.
 * When determining if a particular rule applies, only the firstSegment
 * and loopContext attributes are considered;
 * the other two attributes govern the action take once a rule is selected
 * and have no bearing on the decision of whether the rule applies.
 * <p>
 * For a rule to apply, the firstSegment attribute must match exactly the
 * segment type of the segment in the document.
 * Once the segment type is matched to the firstSegment attribute,
 * the loopContext is considered.
 * The value "*",
 * which can be specified by ANY_CONTEXT symbolic constant,
 * indicates that this LoopDescriptor rule applies any time that the
 * segment in the EDI document matches the firstSegment attribute.
 * <p>
 * The loopContext attribute may also begin with a "/" and specify
 * a nested loop path of slash-delimited loop names. For example, a loopContext
 * of "/ABC/DEF/GHI" would indicate a GHI loop nested within a DEF loop
 * nested within an ABC loop which is nested within the implicit outer loop
 * of the document.
 * A segment appearing in an EDI document is considered to match such a
 * LoopDescriptor rule if the segment type matches the firstSegment attribute
 * and the nested segment loop context of that segment in the parsed document
 * is included in the context described by the path.
 * <p>
 * <b>Actions described by a LoopDescriptor.</b>
 * The most obvious action is for the parser to start a new instance of a segment loop,
 * and this action is designated by simply using the desired loop name as the loopName
 * attribute.
 * It is common in some EDI scenarios for loops to be named after the first segment in the loop
 * but this is not necessary.
 * If the new loop instance is at a lower nesting level than the current loop, then the parser
 * generates the proper XML to terminate the current loops as needed.
 * In this way, the LoopDescriptor rules need only describe the conditions for entering a
 * new segment loop, and the parser and supporting framework can determine where the end of
 * each segment loop occurs.
 * <p>
 * A loopName of null indicates an explicit non-action. When a rule of this kind is applied,
 * then no loop transition occurs in the EDI document, and since a matching rule was encountered
 * no further are considered. By placing a null action rule in the sequence of rules for a given
 * segment type, you can in effect eliminate certain conditions and therefore simplify the
 * rules that follow.
 * <p>
 * The value "/" as a loopName indicates re-entry of the current loop at the designated nesting level.
 * The appropriate number of nested loops are properly terminated.
 * <p>
 * <b>Ordering of LoopDescriptors in a Plugin.</b>
 * Remember that the parser considers LoopDescriptors in the order that they appear
 * in the plugin and accepts the first one that matches, ignoring the rest.
 * Therefore, if there are multiple LoopDescriptors for a given segment type,
 * the relative order in which those LoopDescriptors appear is very important
 * to the semantics of the plugin. Whether all the LoopDescriptors for a given
 * segment type are grouped together or scattered throughout the LoopDescriptor array is
 * not important, only the relative order of LoopDescriptors with respect to others
 * for the same segment type. (For readability, it is suggested that you group LoopDescriptors
 * for a given segment type together, and order those groups alphabetically by segment type.)
 * <p>
 * Within a series of LoopDescriptors for a particular segment type,
 * it is usually a good idea to place those descriptors with the deepest nesting level
 * and longest context path first. In this way, the parser will consider the most
 * specific rules first and, if none of these apply, fall through to the more general rules
 * that cover the "else" conditions.
 * This can simplify the context argument for LoopDescriptors. In fact, it is common for the last
 * LoopDescriptor for a given segment type to use ANY_CONTEXT for its context argument, since the
 * prior LoopDescriptors would have covered all possibilities but one.
 * <p>
 * <b>Plugin Optimization.</b>
 * The description above suggests that the entire array of LoopDescriptors in
 * a plugin is examined serially for each segment within an EDI document.
 * This is logically but not literally true.
 * For performance reasons, an optimized form of a plugin is created when a
 * plugin class is loaded so that the LoopDescriptors associated with a
 * particular segment type can be accessed efficiently. The PluginPreparation
 * class is for this purpose.
 *
 * @see com.berryworks.edireader.Plugin
 * @see com.berryworks.edireader.plugin.PluginPreparation
 */
public class LoopDescriptor {

    public static final int UNSPECIFIED = -1;

    /**
     * Name of the loop entered as the result of applying this descriptor.
     * <p>
     * The name is typically a simple text name that appears in the
     * specifications of the EDI document but not in the actual data. The value
     * "/" in this field designates the implicit outer loop. The variant form
     * /segmentname designates the outer loop following the appearance of the
     * named segment.
     */
    protected String name;

    /**
     * Segment type of the first segment in this loop.
     */
    protected final String firstSegment;

    /**
     * Level of nesting for this loop.
     * <p>
     * Typically, a document will begin with one or more segments that are
     * considered to be outside of any segment loops. The segments are
     * implicitly at nesting level 0. The first level of explicit nesting is
     * level 1, and increases with each new level.
     */
    protected final int nestingLevel;

    /**
     * Specifies a minimum and maximum number of sequential occurrences of the loop.
     * In the case of a LoopDescriptor used to explicitly specify that a segment does NOT constitute
     * instance of a new loop but simply continuation of the current loop, the minimum and maximum
     * can describe the minimum and maximum occurences of that segment.
     */
    protected final int minimumOccurrences;
    protected final int maximumOccurrences;

    /**
     * Context which determines whether a particular appearance of a segment type that matches the firstSegment
     * is in fact an instance of this loop.
     */
    protected String loopContext;

    private final int levelContext;

    /**
     * One or flags which may be set as the result of encountering the loop.
     * These flags may be used as conditions within the context for the loop.
     * resultFlags are the flags that are set when the rule (loop descriptor) is fired
     * conditionFlags are the flags that must be set for the descriptor's condition to be satisfied
     */
    private final Set<String> resultFlags = new TreeSet<>();
    private final Set<String> conditionFlags = new TreeSet<>();


    /**
     * Construct a descriptor for recognizing the beginning of a nested loop.
     *
     * @param loopName     Name of the loop, suitable for use as an XML attribute value
     * @param firstSegment Segment type that (at least sometimes) indicates entry into
     *                     this loop.
     * @param nestingLevel How deeply is this loop nested within other loops.
     * @param currentLoop  Name of a loop; indicates a valid prior state
     */
    public LoopDescriptor(String loopName, String firstSegment, int nestingLevel, String currentLoop) {
        this(loopName, firstSegment, nestingLevel, currentLoop, UNSPECIFIED, UNSPECIFIED);
    }

    /**
     * Construct a descriptor for recognizing the beginning of a nested loop.
     *
     * @param loopName     Name of the loop, suitable for use as an XML attribute value
     * @param firstSegment Segment type that (at least sometimes) indicates entry into
     *                     this loop.
     * @param nestingLevel How deeply is this loop nested within other loops.
     * @param currentLoop  Name of a loop; indicates a valid prior state
     * @param minimumOccurrences Minimum number of sequential occurrences, or -1 for unspecified
     * @param maximumOccurrences Maximum number of sequential occurrences, or -1 for unspecified
     */
    public LoopDescriptor(String loopName, String firstSegment, int nestingLevel, String currentLoop,
                          int minimumOccurrences, int maximumOccurrences) {
        this.name = loopName;
        this.firstSegment = firstSegment;
        this.nestingLevel = nestingLevel;
        this.loopContext = currentLoop;
        this.levelContext = -1;
        this.minimumOccurrences = minimumOccurrences;
        this.maximumOccurrences = maximumOccurrences;
        lookForFlags();
    }

    /**
     * Equivalent to LoopDescriptor(loopName, firstSegment, nestingLevel, ANY_CONTEXT)
     *
     * @param loopName     Name of the loop, suitable for use as an XML attribute value
     * @param firstSegment Segment type that (at least sometimes) indicates entry into
     *                     this loop.
     * @param nestingLevel How deeply is this loop nested within other loops.
     */
    public LoopDescriptor(String loopName, String firstSegment, int nestingLevel) {
        this(loopName, firstSegment, nestingLevel, Plugin.ANY_CONTEXT);
    }

    public LoopDescriptor(String loopName, String firstSegment) {
        this(loopName, firstSegment, 1, Plugin.INITIAL_CONTEXT);
    }

    public LoopDescriptor(String loopName, String firstSegment, int nestingLevel, int currentLevel) {
        this.name = loopName;
        this.firstSegment = firstSegment;
        this.nestingLevel = nestingLevel;
        this.loopContext = Plugin.ANY_CONTEXT;
        this.levelContext = currentLevel;
        minimumOccurrences = maximumOccurrences = UNSPECIFIED;
        lookForFlags();
    }

    private void lookForFlags() {
        // Look for name+flag+flag pattern in name
        if (isPresent(name)) {
            int indexOfFirstPlus = name.indexOf('+');
            if (indexOfFirstPlus > 0) {
                boolean first = true;
                for (String part : name.split("\\+")) {
                    if (first) {
                        name = part;
                        first = false;
                    } else {
                        resultFlags.add(part);
                    }
                }
            }
        }

        // Look for text?flag?flag pattern in the loop context
        if (isPresent(loopContext)) {
            int indexOfFirstQuestion = loopContext.indexOf('?');
            if (indexOfFirstQuestion > 0) {
                boolean first = true;
                for (String part : loopContext.split("\\?")) {
                    if (first) {
                        loopContext = part;
                        first = false;
                    } else {
                        conditionFlags.add(part);
                    }
                }
            }
        }
    }

    /**
     * Get the name of the loop.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Get the nested loop depth for this loop within the document.
     *
     * @return int nestingLevel value
     */
    public int getNestingLevel() {
        return nestingLevel;
    }

    public String getLoopContext() {
        return loopContext;
    }

    public int getLevelContext() {
        return levelContext;
    }

    public int getMinimumOccurrences() {
        return minimumOccurrences;
    }

    public int getMaximumOccurrences() {
        return maximumOccurrences;
    }

    /**
     * Get the segment type of the first segment in the loop which, in context,
     * defines an occurrence of the loop.
     *
     * @return The firstSegment value
     */
    public String getFirstSegment() {
        return firstSegment;
    }

    /**
     * Returns a String representation of this LoopDescriptor
     * for testing and debugging purposes.
     *
     * @return String representation
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("loop " + getName() + " at nesting level " + getNestingLevel()
                + ": encountering segment " + getFirstSegment());
        String context = getLoopContext();
        switch (context) {
            case "*":
                result.append(" anytime");
                break;
            case "/":
                result.append(" while outside any loop");
                break;
            default:
                result.append(" while currently in loop ").append(context);
                break;
        }
        if (levelContext > -1)
            result.append(" while current at nesting level ").append(levelContext);
        if (!resultFlags.isEmpty()) {
            result.append(", setting");
            for (String flagName : resultFlags) {
                result.append(" ").append(flagName);
            }
        }
        if (!conditionFlags.isEmpty()) {
            result.append(", conditional based on");
            for (String flagName : conditionFlags) {
                result.append(" ").append(flagName);
            }
        }

        return result.toString();
    }

    /**
     * Overrides equals
     *
     * @param target - the reference object with which to compare.
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object target) {
        if (!(target instanceof LoopDescriptor))
            return false;

        LoopDescriptor sld = (LoopDescriptor) target;
        return equalsOrBothNull(getName(), sld.getName())
                && equalsOrBothNull(getFirstSegment(), sld.getFirstSegment())
                && getNestingLevel() == sld.getNestingLevel()
                && getLoopContext().equals(sld.getLoopContext())
                && getLevelContext() == sld.getLevelContext()
                && getMinimumOccurrences() == sld.getMinimumOccurrences()
                && getMaximumOccurrences() == sld.getMaximumOccurrences()
                && getResultFlags().equals(sld.getResultFlags())
                && getConditionFlags().equals(sld.getConditionFlags());
    }

    public int hashCode() {
        return getName().hashCode() + getFirstSegment().hashCode();
    }

    private boolean equalsOrBothNull(String value1, String value2) {
        return value1 == null && value2 == null || value1 != null && value1.equals(value2);
    }

    public boolean isAnyContext() {
        return Plugin.ANY_CONTEXT.equals(loopContext);
    }

    public boolean isResultFlag(String flagName) {
        return resultFlags.contains(flagName);
    }

    public Set<String> getResultFlags() {
        return resultFlags;
    }

    public boolean isConditionFlag(String flagName) {
        return conditionFlags.contains(flagName);
    }

    public Set<String> getConditionFlags() {
        return conditionFlags;
    }
}

