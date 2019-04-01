package com.berryworks.edireader.plugin;

import static com.berryworks.edireader.Plugin.ANY_CONTEXT;
import static com.berryworks.edireader.Plugin.CURRENT;

class LoopRule {
    private final String segmentName;
    private final String loopContext;

    LoopRule(String segmentName, String loopContext) {
        this.segmentName = segmentName;
        this.loopContext = loopContext;
    }

    LoopDescriptor then(String loopName, int nestingLevel) {
        return new LoopDescriptor(loopName, segmentName, nestingLevel, loopContext);
    }

    public LoopDescriptor then(int nestingLevel) {
        return new LoopDescriptor(CURRENT, segmentName, nestingLevel, loopContext);
    }

    static LoopRule when(String segmentName) {
        return new LoopRule(segmentName, ANY_CONTEXT);
    }

    static LoopRule when(String segmentName, String loopContext) {
        return new LoopRule(segmentName, loopContext);
    }
}
