package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIAttributes;
import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.XMLTags;
import com.berryworks.edireader.tokenizer.Token;
import org.xml.sax.SAXException;

import static com.berryworks.edireader.XMLTags.SUB_SUB_ELEMENT;

public class ElementCoordinates {
    private final EDIReader ediReader;
    private int index, repetition, subIndex, subSubIndex;
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
        if (newElement(token)) {
            // Focussing on a new element
            index = token.getIndex();
            elementStarted = elementEnded = false;

            repetition = token.getElementRepetition();
            subIndex = token.getSubIndex();
            subElementStarted = subElementEnded = false;

            subSubIndex = token.getSubSubIndex();
            subSubElementStarted = subSubElementEnded = false;

        } else if (newRepetition(token)) {
            // Focussing on a new repetition of the same element
            endElementIfNeeded();
            elementStarted = elementEnded = false;
            subElementStarted = subElementEnded = false;
            subSubElementStarted = subSubElementEnded = false;

        } else if (token.getSubIndex() != subIndex) {
            // Same element, but a different sub-element
            // TODO: This might really be a repetition of the element, not just another sub-element of the same element!
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
            endElementIfNeeded();
            elementStarted = elementEnded = false;
            subElementStarted = subElementEnded = false;
            subSubElementStarted = subSubElementEnded = false;
        }
    }

    private boolean newElement(Token token) {
        return token.getIndex() != index;
    }

    private boolean newRepetition(Token token) {
        // is this a new repetition of the same element ?
        System.out.println("... newRepetition ?");
        return false;
//        return token.getElementRepetition() != repetition;
    }

    private void endElementIfNeeded() throws SAXException {
        endSubElementIfNeeded();
        if (isElementStarted() && !isElementEnded()) {
            ediReader.endElement(XMLTags.ELEMENT);
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

    public void startElement(EDIAttributes attributes) throws SAXException {
        if (elementStarted) {
            if (elementEnded) {
                // If the element is both started and ended, we will consider this to be a repetition of the element.
                elementEnded = false;
            } else {
                throw new IllegalStateException("Element started twice");
            }
        }
        ediReader.startElement(XMLTags.ELEMENT, attributes);
        elementStarted = true;
    }

    public void endElement() throws SAXException {
        if (!elementStarted) throw new IllegalStateException("Element not started");
        if (subElementStarted && !subElementEnded) throw new IllegalStateException("Sub-element started but not ended");
        ediReader.endElement(XMLTags.ELEMENT);
        elementEnded = true;
        subElementStarted = subElementEnded = false;
        subSubElementStarted = subSubElementEnded = false;
    }

    public void startSubElement(EDIAttributes attributes) throws SAXException {
        if (subElementStarted) throw new IllegalStateException("Sub-element started twice");
        ediReader.startElement(XMLTags.SUB_ELEMENT, attributes);
        subElementStarted = true;
    }

    public void endSubElement() throws SAXException {
        if (!subElementStarted) throw new IllegalStateException("Sub-element not started");
        ediReader.endElement(XMLTags.SUB_ELEMENT);
        subElementEnded = true;
    }

    public void startSubSubElement(EDIAttributes attributes) throws SAXException {
        if (subSubElementStarted) throw new IllegalStateException("Sub-sub-element started twice");
        ediReader.startElement(SUB_SUB_ELEMENT, attributes);
        subSubElementStarted = true;
    }

    public void endSubSubElement() throws SAXException {
        if (!subSubElementStarted) throw new IllegalStateException("Sub-sub-element not started");
        ediReader.endElement(SUB_SUB_ELEMENT);
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
