package com.berryworks.edireader.util;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static com.berryworks.edireader.EdifactEncodingTest.ISO_8859_5;
import static org.junit.Assert.assertEquals;

public class SwitchableInputStreamReaderTest {

    private byte[] bytes;
    private Reader reader;

    @Before
    public void setUp() {
        bytes = EDIFACT_UNOE.getBytes(ISO_8859_5);
    }

    @Test
    public void baseline() throws IOException {
        // Take a byte stream of ISO-8859-5 chars and read it with normal InputStreamReader with default decoding.
        reader = new InputStreamReader(new ByteArrayInputStream(bytes));
        char[] buf = new char[32];
        reader.read(buf);
        assertEquals("UNB+UNOE:1+005435656:1+006415160", new String(buf));
        reader.read(buf);
        assertEquals("CFS:1+000210:1434+00000000000778", new String(buf));
        reader.read(buf);
        assertEquals("+rref+aref+p+a+cid+t'\nUNH+000000", new String(buf));
        reader.read(buf);
        assertEquals("00000117+INVOIC:D:97B:UN'\nBGM+38", new String(buf));
        reader.read(buf);
        assertEquals("0+342459+9'\nNAD+SE+005435656::16", new String(buf));
        reader.read(buf);
        assertEquals("++���� �����'\nUNT+4+000000000001", new String(buf));
        int n = reader.read(buf);
        assertEquals(26, n);
        assertEquals("17'\nUNZ+1+00000000000778'\n", new String(buf, 0, n));
    }

    @Test
    public void basics() throws IOException {
        // Read the same byte stream with SwitchableInputStreamReader with default decoding
        reader = new SwitchableInputStreamReader(new ByteArrayInputStream(bytes));
        char[] buf = new char[32];
        reader.read(buf);
        assertEquals("UNB+UNOE:1+005435656:1+006415160", new String(buf));
        reader.read(buf);
        assertEquals("CFS:1+000210:1434+00000000000778", new String(buf));
        reader.read(buf);
        assertEquals("+rref+aref+p+a+cid+t'\nUNH+000000", new String(buf));
        reader.read(buf);
        assertEquals("00000117+INVOIC:D:97B:UN'\nBGM+38", new String(buf));
        reader.read(buf);
        assertEquals("0+342459+9'\nNAD+SE+005435656::16", new String(buf));
        reader.read(buf);
        assertEquals("++���� �����'\nUNT+4+000000000001", new String(buf));
        int n = reader.read(buf);
        assertEquals(26, n);
        assertEquals("17'\nUNZ+1+00000000000778'\n", new String(buf, 0, n));
    }

    private static final String EDIFACT_UNOE = """
            UNB+UNOE:1+005435656:1+006415160CFS:1+000210:1434+00000000000778+rref+aref+p+a+cid+t'
            UNH+00000000000117+INVOIC:D:97B:UN'
            BGM+380+342459+9'
            NAD+SE+005435656::16++Рыба текст'
            UNT+4+00000000000117'
            UNZ+1+00000000000778'
            """;

}
