package com.berryworks.edireader;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SyntaxDescriptorTest {

    private SyntaxDescriptor syntaxDescriptor;

    @Test
    public void basics() {
        syntaxDescriptor = new SyntaxDescriptor();
        syntaxDescriptor.setDecimalMark('.');
        syntaxDescriptor.setDelimiter('*');
        syntaxDescriptor.setRelease(123);
        syntaxDescriptor.setRepetitionSeparator('+');
        syntaxDescriptor.setSubDelimiter(':');
        syntaxDescriptor.setTerminator('$');
        syntaxDescriptor.setTerminatorSuffix("\r\n");
        syntaxDescriptor.setSubSubDelimiter('&');
        assertEquals('.', syntaxDescriptor.getDecimalMark());
        assertEquals('*', syntaxDescriptor.getDelimiter());
        assertEquals(123, syntaxDescriptor.getRelease());
        assertEquals('+', syntaxDescriptor.getRepetitionSeparator());
        assertEquals(':', syntaxDescriptor.getSubDelimiter());
        assertEquals('$', syntaxDescriptor.getTerminator());
        assertEquals("\r\n", syntaxDescriptor.getTerminatorSuffix());
        assertEquals('&', syntaxDescriptor.getSubSubDelimiter());
    }
}
