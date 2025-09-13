package com.berryworks.edireader.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * A drop-in replacement for InputStreamReader that allows switching
 * the decoding charset between read operations.
 */
public class SwitchableInputStreamReader extends Reader {
    private final InputStream in;
    private CharsetDecoder decoder;
    private final ByteBuffer byteBuffer;
    private CharBuffer charBuffer;

    private boolean endOfStream = false;

    // Constructors similar to InputStreamReader
    public SwitchableInputStreamReader(InputStream in) {
        this(in, Charset.defaultCharset());
    }

    public SwitchableInputStreamReader(InputStream in, String charsetName) throws UnsupportedEncodingException {
        this(in, Charset.forName(charsetName));
    }

    public SwitchableInputStreamReader(InputStream in, Charset cs) {
        if (in == null) throw new NullPointerException("InputStream is null");
        if (cs == null) throw new NullPointerException("Charset is null");

        this.in = in;
        this.decoder = cs.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
        this.byteBuffer = ByteBuffer.allocate(8192);
        this.byteBuffer.limit(0); // start empty
        this.charBuffer = CharBuffer.allocate(8192);
        this.charBuffer.limit(0);
    }

    /**
     * Switch the decoding charset for subsequent reads.
     * This discards any partially decoded bytes.
     */
    public void switchCharset(Charset newCharset) {
        if (newCharset == null) throw new NullPointerException("Charset is null");
        this.decoder = newCharset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
        this.byteBuffer.clear();
        this.byteBuffer.limit(0);
        this.charBuffer.clear();
        this.charBuffer.limit(0);
        this.endOfStream = false;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off + len > cbuf.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) return 0;

        int count = 0;
        while (count < len) {
            if (!charBuffer.hasRemaining()) {
                if (!refill()) {
                    break; // no more chars
                }
            }
            while (count < len && charBuffer.hasRemaining()) {
                cbuf[off + count++] = charBuffer.get();
            }
        }
        return (count == 0 && endOfStream) ? -1 : count;
    }

    @Override
    public int read() throws IOException {
        char[] buf = new char[1];
        int n = read(buf, 0, 1);
        return (n == -1) ? -1 : buf[0];
    }

    private boolean refill() throws IOException {
        charBuffer.clear();

        // refill byte buffer if empty
        if (!byteBuffer.hasRemaining() && !endOfStream) {
            byteBuffer.clear();
            int n = in.read(byteBuffer.array(), 0, byteBuffer.capacity());
            if (n == -1) {
                endOfStream = true;
            } else {
                byteBuffer.limit(n);
                byteBuffer.position(0);
            }
        }

        decoder.reset();
        CoderResult result = decoder.decode(byteBuffer, charBuffer, endOfStream);
        if (result.isError()) result.throwException();

        charBuffer.flip();
        return charBuffer.hasRemaining();
    }

    @Override
    public boolean ready() throws IOException {
        return charBuffer.hasRemaining() || in.available() > 0;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}