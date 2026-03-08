package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIReader;
import com.berryworks.edireader.EDIStandard;
import com.berryworks.edireader.EDISyntaxException;
import com.berryworks.edireader.tokenizer.Tokenizer;
import com.berryworks.edireader.util.sax.EDIReaderSAXAdapter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static com.berryworks.edireader.EDIStandard.*;
import static com.berryworks.edireader.util.FixedLength.isPresent;

public class EdiProber {
    private EDIStandard standard;
    private String documentType;
    private String version;
    private Tokenizer tokenizer;

    public EdiProber() {
    }

    public boolean probe(String edi) {
        return probe(new StringReader(edi));
    }

    public boolean probe(Reader reader) {
        try {
            probeContent(reader);
            return true;
        } catch (IOException | SAXException e) {
            return false;
        }
    }

    private void probeContent(Reader reader) throws IOException, SAXException {
        EDIReader ediReader = new EDIReader();
        ProbeHandler handler = new ProbeHandler();
        ediReader.setContentHandler(handler);
        ediReader.setSyntaxExceptionHandler(e -> {
            return true; // Ignore
        });
        try {
            ediReader.parse(reader);
        } catch (RuntimeException e) {
            if (e.getMessage().equals(ProbeHandler.STOP_PARSING)) {
                // That is the signal that we have gathered all the information needed
                // so there is no need to keep parsing.
                version = handler.getVersion();
                documentType = handler.getDocumentType();
                standard = handler.getStandard();
                tokenizer = ediReader.getTokenizer();
                return;
            }
            throw e;
        } catch (EDISyntaxException e) {
            String message = e.getMessage();
            if (isPresent(message) && message.startsWith("No supported EDI standard")) {
                // This means there was no parser available. However, we want to be able to recognized
                // HL7, TRADACOMS, etc. even if we don't have a parser available.
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

    public EDIStandard getStandard() {
        return standard;
    }

    private void setStandard(EDIStandard standard) {
        this.standard = standard;
    }

    public String getDocumentType() {
        return documentType;
    }

    private void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getVersion() {
        return version;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    public String getDelimiter() {
        return tokenizer == null ? null : String.valueOf(tokenizer.getDelimiter());
    }

    public String getSubDelimiter() {
        return tokenizer == null ? null : String.valueOf(tokenizer.getSubDelimiter());
    }

    public String getRepetitionDelimiter() {
        String result = null;
        if (tokenizer == null) return result;
        int repetitionSeparator = tokenizer.getRepetitionSeparator();
        if (repetitionSeparator < 0) return result;
        Character c = (char) repetitionSeparator;
        result = String.valueOf(c);
        return result;
    }

    public String getSegmentTerminator() {
        return tokenizer == null ? null : String.valueOf(tokenizer.getTerminator());
    }

    public String getSegmentTerminatorSuffix() {
        String result = null;
        if (tokenizer == null) return result;
//        tokenizer.
        return result;
    }

//    public String getSegmentTerminatorSuffix() {
//        return tokenizer == null ? null : String.valueOf(tokenizer.getTerminatorSuffix());
//    }

    private static class ProbeHandler extends EDIReaderSAXAdapter {
        public static final String STOP_PARSING = "StopParsing";
        private EDIStandard standard;
        private String version;
        private String documentType;

        @Override
        protected void beginInterchange(int charCount, int segmentCharCount, Attributes attributes) {
            String standardAttribute = attributes.getValue("Standard");
            standard = select(standardAttribute);
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
        protected void beginExplicitGroup(int charCount, int segmentCharCount, Attributes attributes) {
            version = attributes.getValue("StandardVersion");
        }

        @Override
        protected void beginDocument(int charCount, int segmentCharCount, Attributes attributes) {
            documentType = attributes.getValue("DocType");
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
    }
}
