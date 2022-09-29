package com.berryworks.edireader.util.sax;

import com.berryworks.edireader.EDIAttributes;
import org.junit.Assert;
import org.junit.Test;

public class SerializableAttributesTest {

    private SerializableAttributes attributes;

    @Test
    public void basics() {
        EDIAttributes fromAttributes = new EDIAttributes();
        fromAttributes.addCDATA("A", "a");
        attributes = new SerializableAttributes(fromAttributes);
        Assert.assertEquals("SerializableAttribute(1): A=a", attributes.toString());
    }
}
