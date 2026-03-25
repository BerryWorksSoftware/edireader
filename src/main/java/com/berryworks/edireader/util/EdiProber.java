package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIStandard;
import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.tokenizer.Tokenizer;
import com.berryworks.edireader.util.sax.EDIReaderSAXAdapter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.*;

import static com.berryworks.edireader.EDIStandard.*;
import static com.berryworks.edireader.util.FixedLength.isPresent;

/**
 * Utility for probing EDI input to extract high-level metadata without fully parsing the document.
 * <p>
 * This class reads only the initial segments of an EDI stream and determines:
 * <ul>
 *     <li>EDI standard (X12, EDIFACT, HL7, TRADACOMS, etc.)</li>
 *     <li>Document type (e.g., 837, 850, ORDERS)</li>
 *     <li>Version / release</li>
 *     <li>Sender and receiver identifiers</li>
 *     <li>Control numbers (interchange, group, document)</li>
 *     <li>Delimiter characters</li>
 * </ul>
 * <p>
 * Parsing is intentionally terminated early once sufficient information is gathered,
 * making this suitable for lightweight inspection or routing decisions.
 */
public class EdiProber {
    private EDIStandard standard;
    private String documentType;
    private String version;
    private Tokenizer tokenizer;
    private String terminatorSuffix;
    private String interchangeControl, functionalGroupControl, documentControl;
    private String senderId, senderQualifier, receiverId, receiverQualifier;

    /**
     * Probe an EDI document provided as a String.
     *
     * @param edi the EDI content
     * @throws IOException  if a read error occurs
     * @throws SAXException if a parsing error occurs
     */
    public void probe(String edi) throws IOException, SAXException {
        probe(new StringReader(edi));
    }

    /**
     * Probe an EDI document from a file.
     *
     * @param ediFile the file containing EDI data
     * @throws IOException  if the file cannot be read
     * @throws SAXException if a parsing error occurs
     */
    public void probe(File ediFile) throws IOException, SAXException {
        try (Reader reader = new FileReader(ediFile)) {
            probe(reader);
        }
    }

    /**
     * Probe an EDI document from a Reader.
     * <p>
     * This method performs a partial parse and stops early once key metadata is extracted.
     * After invocation, getter methods can be used to retrieve the discovered values.
     *
     * @param reader the source of EDI data
     * @throws IOException  if a read error occurs
     * @throws SAXException if a parsing error occurs
     */
    public void probe(Reader reader) throws IOException, SAXException {
        EDIReader ediReader = new EDIReader();
        ProbeHandler handler = new ProbeHandler();
        ediReader.setContentHandler(handler);
        ediReader.setSyntaxExceptionHandler(e -> true);

        try {
            ediReader.parse(reader);
        } catch (RuntimeException e) {
            if (e.getMessage().equals(ProbeHandler.STOP_PARSING)) {
                standard = handler.getStandard();
                senderId = handler.senderId;
                senderQualifier = handler.senderQualifier;
                receiverId = handler.receiverId;
                receiverQualifier = handler.receiverQualifier;
                version = handler.getVersion();
                documentType = handler.getDocumentType();
                interchangeControl = handler.getInterchangeControl();
                functionalGroupControl = handler.getFunctionalGroupControl();
                documentControl = handler.getDocumentControl();
                tokenizer = ediReader.getTokenizer();
                terminatorSuffix = ediReader.getTerminatorSuffix();
                return;
            }
            throw e;
        } catch (EDISyntaxException e) {
            String message = e.getMessage();
            if (isPresent(message) && message.startsWith("No supported EDI standard")) {
                if (message.endsWith("STX")) {
                    standard = TRADACOMS;
                    return;
                } else if (message.endsWith("MSH")) {
                    standard = HL7;
                    return;
                }
            }
            throw e;
        }
    }

    /**
     * @return the detected EDI standard, or {@code null} if not determined
     */
    public EDIStandard getStandard() {
        return standard;
    }

    /**
     * @return the document type (e.g., 837, 850), or {@code null} if not determined
     */
    public String getDocumentType() {
        return documentType;
    }

    /**
     * @return the version or release identifier, or {@code null} if not determined
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the element delimiter character as a String, or {@code null} if unavailable
     */
    public String getDelimiter() {
        return tokenizer == null ? null : String.valueOf(tokenizer.getDelimiter());
    }

    /**
     * @return the sub-element delimiter character, or {@code null} if unavailable
     */
    public String getSubDelimiter() {
        return tokenizer == null ? null : String.valueOf(tokenizer.getSubDelimiter());
    }

    /**
     * @return the repetition separator character, or {@code null} if not defined
     */
    public String getRepetitionDelimiter() {
        if (tokenizer == null) return null;
        int repetitionSeparator = tokenizer.getRepetitionSeparator();
        if (repetitionSeparator < 0) return null;
        return String.valueOf((char) repetitionSeparator);
    }

    /**
     * @return the release (escape) character, or {@code null} if not defined
     */
    public String getReleaseCharacter() {
        if (tokenizer == null) return null;
        int release = tokenizer.getRelease();
        if (release < 0) return null;
        return String.valueOf((char) release);
    }

    /**
     * @return the segment terminator character, or {@code null} if unavailable
     */
    public String getSegmentTerminator() {
        return tokenizer == null ? null : String.valueOf(tokenizer.getTerminator());
    }

    /**
     * @return any suffix following the segment terminator (e.g., CR/LF), or {@code null}
     */
    public String getSegmentTerminatorSuffix() {
        return terminatorSuffix;
    }

    /**
     * @return the interchange control number, or {@code null} if not available
     */
    public String getInterchangeControl() {
        return interchangeControl;
    }

    /**
     * @return the functional group control number, or {@code null} if not available
     */
    public String getFunctionalGroupControl() {
        return functionalGroupControl;
    }

    /**
     * @return the document (transaction set/message) control number, or {@code null}
     */
    public String getDocumentControl() {
        return documentControl;
    }

    /**
     * @return the sender identifier, or {@code null} if not available
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * @return the sender qualifier, or {@code null} if not available
     */
    public String getSenderQualifier() {
        return senderQualifier;
    }

    /**
     * @return the receiver identifier, or {@code null} if not available
     */
    public String getReceiverId() {
        return receiverId;
    }

    /**
     * @return the receiver qualifier, or {@code null} if not available
     */
    public String getReceiverQualifier() {
        return receiverQualifier;
    }

    private static class ProbeHandler extends EDIReaderSAXAdapter {
        public static final String STOP_PARSING = "StopParsing";
        private EDIStandard standard;
        private String version, documentType;
        private String interchangeControl, functionalGroupControl, documentControl;
        private String senderId, senderQualifier, receiverId, receiverQualifier;


        @Override
        protected void beginInterchange(int charCount, int segmentCharCount, Attributes attributes) {
            String standardAttribute = attributes.getValue("Standard");
            standard = select(standardAttribute);
            interchangeControl = attributes.getValue("Control");
            if (standard == HL7) {
                // In HL7 we get the version and document type from the MSH header
                version = attributes.getValue("SyntaxVersion");
                documentType = attributes.getValue("Type");
                throw new RuntimeException(STOP_PARSING);
            } else if (standard == TRADACOMS) {
                version = attributes.getValue("SyntaxVersion");
            }
        }

        @Override
        protected void senderAddress(String qualifier, String address, String extra) {
            senderQualifier = qualifier;
            senderId = address;
        }

        @Override
        protected void receiverAddress(String qualifier, String address, String extra) {
            receiverQualifier = qualifier;
            receiverId = address;
        }

        @Override
        protected void beginExplicitGroup(int charCount, int segmentCharCount, Attributes attributes) {
            version = attributes.getValue("StandardVersion");
            functionalGroupControl = attributes.getValue("Control");
        }

        @Override
        protected void beginDocument(int charCount, int segmentCharCount, Attributes attributes) {
            documentType = attributes.getValue("DocType");
            documentControl = attributes.getValue("Control");
            if (version == null && standard == EDIFACT) {
                // In EDIFACT, an explicit functional group is optional and typically omitted.
                // The version comes from the document-level UNT envelope.
                version = attributes.getValue("Release");
            }
            throw new RuntimeException(STOP_PARSING);
        }

        @Override
        protected void beginAnotherSegment(Attributes atts) {
            throw new RuntimeException(STOP_PARSING);
        }

        private EDIStandard getStandard() {
            return standard;
        }

        private String getVersion() {
            return version;
        }

        private String getDocumentType() {
            return documentType;
        }

        public String getInterchangeControl() {
            return interchangeControl;
        }

        public String getFunctionalGroupControl() {
            return functionalGroupControl;
        }

        public String getDocumentControl() {
            return documentControl;
        }
    }
}