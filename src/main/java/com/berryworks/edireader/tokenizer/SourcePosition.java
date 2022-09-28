package com.berryworks.edireader.tokenizer;

public interface SourcePosition {
    int getCharCount();

    int getSegmentCharCount();

    void setCharCounts(int charCount, int segmentCharCount);
}
