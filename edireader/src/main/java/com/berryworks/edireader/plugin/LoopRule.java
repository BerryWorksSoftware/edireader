package com.berryworks.edireader.plugin;

import static com.berryworks.edireader.Plugin.ANY_CONTEXT;
import static com.berryworks.edireader.Plugin.CURRENT;

/**
 * A rule within a plugin used to specify the action to take in response to the appearance
 * of a given segment type within a transaction.
 * The actions may be (1) enter a new instance of a segment loop, (2) remain in the
 * current loop, or (3) close the current loop at the current nesting level and resume
 * the current loop at a higher nesting level.
 * <p/>
 * Nesting levels start at 0 for the implicit outer loop of a transaction, and increase
 * by 1 for each level of nested looping. Therefore, a "deeper" nesting level has a
 * larger numeric nesting level, while a "higher" level has a smaller number.
 */
class LoopRule {
    private final String segmentName;
    private final String loopContext;

    private LoopRule(String segmentName, String loopContext) {
        this.segmentName = segmentName;
        this.loopContext = loopContext;
    }

    /**
     * Enter a new instance of a loop.
     *
     * @param loopName     the name of the loop (for example: "LX-2400")
     * @param nestingLevel the nesting level for the new loop
     * @return LoopDescriptor for the new loop
     */
    LoopDescriptor then(String loopName, int nestingLevel) {
        return new LoopDescriptor(loopName, segmentName, nestingLevel, loopContext);
    }

    /**
     * Resume the current loop at a specified nesting level.
     * @param nestingLevel the nesting level for the resumed loop
     * @return LoopDescriptor for the loop
     */
    public LoopDescriptor then(int nestingLevel) {
        return new LoopDescriptor(CURRENT, segmentName, nestingLevel, loopContext);
    }

    /**
     * Create a rule to react to the appearance of a given segment, regardless of current looping context.
     * @param segmentName name of the segment (for example: "REF")
     * @return LoopRule the new rule
     */
    static LoopRule when(String segmentName) {
        return new LoopRule(segmentName, ANY_CONTEXT);
    }

    /**
     * Create a rule to react to the appearance of a given segment in a particular looping context.
     * @param segmentName name of the segment (for example: "REF")
     * @param loopContext the context in which the rule applies (for example: "/HL-2000/NM1-2010")
     * @return LoopRule the new rule
     */
    static LoopRule when(String segmentName, String loopContext) {
        return new LoopRule(segmentName, loopContext);
    }
}
