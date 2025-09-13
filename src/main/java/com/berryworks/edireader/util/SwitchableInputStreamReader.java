package com.berryworks.edireader.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * Drop-in replacement for InputStreamReader that allows switching
 * Charset between reads. Properly preserves partial multi-byte sequences
 * and returns correct read counts at EOF.
 */
public class SwitchableInputStreamReader extends Reader {
    private final InputStream in;
    private CharsetDecoder decoder;
    private final ByteBuffer byteBuffer;
    private final CharBuffer charBuffer;
    private boolean endOfStream = false;

    private static final int DEFAULT_BYTE_BUFFER_SIZE  = 8192;
    private static final int DEFAULT_CHAR_BUFFER_SIZE  = 8192;

    public SwitchableInputStreamReader(InputStream in) {
        this(in, Charset.defaultCharset());
    }

    public SwitchableInputStreamReader(InputStream in, String charsetName) {
        this(in, Charset.forName(charsetName));
    }

    public SwitchableInputStreamReader(InputStream in, Charset cs) {
        if (in == null) throw new NullPointerException("InputStream is null");
        if (cs == null) throw new NullPointerException("Charset is null");
        this.in = in;
        this.decoder = cs.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        this.byteBuffer = ByteBuffer.allocate(DEFAULT_BYTE_BUFFER_SIZE);
        // start with an "empty" buffer for reading: position=0, limit=0
        this.byteBuffer.limit(0);

        this.charBuffer = CharBuffer.allocate(DEFAULT_CHAR_BUFFER_SIZE);
        this.charBuffer.limit(0);
    }

    /**
     * Switch decoding charset for subsequent reads. This discards any
     * partially buffered bytes/characters and resets decoder state.
     */
    public void switchCharset(Charset newCharset) {
        if (newCharset == null) throw new NullPointerException("Charset is null");
        this.decoder = newCharset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
        // discard any buffered bytes/chars (safe, but means partial sequences are lost)
        byteBuffer.clear();
        byteBuffer.limit(0);
        charBuffer.clear();
        charBuffer.limit(0);
        endOfStream = false;
    }

    public void switchCharset(String charsetName) {
        switchCharset(Charset.forName(charsetName));
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (cbuf == null) throw new NullPointerException();
        if (off < 0 || len < 0 || off + len > cbuf.length) throw new IndexOutOfBoundsException();
        if (len == 0) return 0;

        int count = 0;
        while (count < len) {
            if (!charBuffer.hasRemaining()) {
                // refill charBuffer; if it returns false there are no more chars available now
                if (!refill()) {
                    break;
                }
            }
            while (count < len && charBuffer.hasRemaining()) {
                cbuf[off + count++] = charBuffer.get();
            }
        }

        // If we read nothing and the stream is at EOF, return -1 per Reader contract
        if (count == 0 && endOfStream && !charBuffer.hasRemaining()) {
            return -1;
        }
        return count;
    }

    @Override
    public int read() throws IOException {
        char[] one = new char[1];
        int n = read(one, 0, 1);
        return (n == -1) ? -1 : one[0];
    }

    /**
     * Refill charBuffer by decoding bytes from byteBuffer / InputStream.
     * Returns true if charBuffer has characters to read.
     */
    private boolean refill() throws IOException {
        charBuffer.clear();

        for (;;) {
            // prepare byteBuffer for reading by decoder
            byteBuffer.flip(); // limit = position, position = 0

            // decode available bytes; if endOfStream is true, tell the decoder it's the last input
            CoderResult cr = decoder.decode(byteBuffer, charBuffer, endOfStream);

            // move remaining bytes to start (to preserve partial sequences) and prepare for further reads
            byteBuffer.compact(); // position = remaining, limit = capacity

            // if we produced any chars, break to let caller read them
            if (charBuffer.position() > 0) {
                break;
            }

            if (cr.isOverflow()) {
                // charBuffer full (shouldn't hit here since we check charBuffer.position())
                break;
            } else if (cr.isUnderflow()) {
                if (endOfStream) {
                    // no more bytes will arrive; flush the decoder state (may produce chars)
                    cr = decoder.flush(charBuffer);
                    // ignore overflow/underflow here; flush either produces characters or not
                    break;
                }

                // Need more input bytes. Read into the free region of byteBuffer.
                int writePos = byteBuffer.position(); // where compact left us
                int toRead = byteBuffer.capacity() - writePos;
                int read = in.read(byteBuffer.array(), writePos, toRead);
                if (read == -1) {
                    endOfStream = true;
                } else if (read > 0) {
                    byteBuffer.position(writePos + read);
                } else {
                    // read == 0 (rare), try again
                }
                // loop and attempt decode again
            } else if (cr.isError()) {
                cr.throwException();
            }
        }

        charBuffer.flip(); // prepare for reading by caller
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