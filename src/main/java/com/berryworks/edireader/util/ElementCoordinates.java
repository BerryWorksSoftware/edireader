package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.XMLTags;
import com.berryworks.edireader.tokenizer.Token;
import org.xml.sax.SAXException;

public class ElementCoordinates {
    private EDIReader ediReader;
    private int index, subIndex, subSubIndex;
    private boolean elementStarted, elementEnded;
    private boolean subElementStarted, subElementEnded;
    private boolean subSubElementStarted, subSubElementEnded;
    private String segmentType;

    public ElementCoordinates(EDIReader ediReader) {
        this.ediReader = ediReader;
    }

    public void focus(Token token) throws SAXException {
        if (token == null) throw new IllegalArgumentException("token is null");

        segmentType = token.getSegmentType();
        if (token.getIndex() != index) {
            // Focussing on a new element
            index = token.getIndex();
            elementStarted = elementEnded = false;

            subIndex = token.getSubIndex();
            subElementStarted = subElementEnded = false;

            subSubIndex = token.getSubSubIndex();
            subSubElementStarted = subSubElementEnded = false;

        } else if (token.getSubIndex() != subIndex) {
            // Same element, but a different sub-element
            endSubElementIfNeeded();
            subIndex = token.getSubIndex();
            subElementStarted = subElementEnded = false;

            subSubIndex = token.getSubSubIndex();
            subSubElementStarted = subSubElementEnded = false;

        } else if (token.getSubSubIndex() != subSubIndex) {
            // Same element and sub-element, but a different sub-sub-element
            subSubIndex = token.getSubSubIndex();
            subSubElementStarted = subSubElementEnded = false;
        } else {
            // This appears to be a repetition of an element.
            elementStarted = elementEnded = false;
            subElementStarted = subElementEnded = false;
            subSubElementStarted = subSubElementEnded = false;
        }
    }

    private void endSubElementIfNeeded() throws SAXException {
        if (isSubElementStarted() && !isSubElementEnded()) {
            ediReader.endElement(XMLTags.SUB_ELEMENT);
        }
    }

    public int getIndex() {
        return index;
    }

    public int getSubIndex() {
        return subIndex;
    }

    public int getSubSubIndex() {
        return subSubIndex;
    }

    public void startElement() {
        if (elementStarted) {
            if (elementEnded) {
                // If the element is both started and ended, we will consider this to be a repetition of the element.
                elementEnded = false;
            } else {
                throw new IllegalStateException("Element started twice");
            }
        }
        elementStarted = true;
    }

    public void endElement() {
        if (!elementStarted) throw new IllegalStateException("Element not started");
        if (subElementStarted && !subElementEnded) throw new IllegalStateException("Sub-element started but not ended");
        elementEnded = true;
        subElementStarted = subElementEnded = false;
        subSubElementStarted = subSubElementEnded = false;
    }

    public void startSubElement() {
        if (subElementStarted) throw new IllegalStateException("Sub-element started twice");
        subElementStarted = true;
    }

    public void endSubElement() {
        if (!subElementStarted) throw new IllegalStateException("Sub-element not started");
        subElementEnded = true;
    }

    public void startSubSubElement() {
        if (subSubElementStarted) throw new IllegalStateException("Sub-sub-element started twice");
        subSubElementStarted = true;
    }

    public void endSubSubElement() {
        if (!subSubElementStarted) throw new IllegalStateException("Sub-sub-element not started");
        subSubElementEnded = true;
    }

    public boolean isElementStarted() {
        return elementStarted;
    }

    public boolean isElementEnded() {
        return elementEnded;
    }

    public boolean isSubElementStarted() {
        return subElementStarted;
    }

    public boolean isSubElementEnded() {
        return subElementEnded;
    }

    public boolean isSubSubElementStarted() {
        return subSubElementStarted;
    }

    public boolean isSubSubElementEnded() {
        return subSubElementEnded;
    }

    @Override
    public String toString() {
        return segmentType + " " + index + "." + subIndex + "." + subSubIndex +
                ", " + elementStarted + "," + elementEnded +
                ", " + subElementStarted + "," + subElementEnded +
                ", " + subSubElementStarted + "," + subSubElementEnded;
    }
}
