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
    private String terminatorSuffix;
    private String interchangeControl, functionalGroupControl, documentControl;

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
                // That is the signal that we have gathered all the information needed;
                // so there is no need to keep parsing.
                standard = handler.getStandard();
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
                // This means there was no parser available. However, we want to be able to recognize
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

    public String getDocumentType() {
        return documentType;
    }

    public String getVersion() {
        return version;
    }

    public String getDelimiter() {
        return tokenizer == null ? null : String.valueOf(tokenizer.getDelimiter());
    }

    public String getSubDelimiter() {
        return tokenizer == null ? null : String.valueOf(tokenizer.getSubDelimiter());
    }

    public String getRepetitionDelimiter() {
        if (tokenizer == null) return null;
        int repetitionSeparator = tokenizer.getRepetitionSeparator();
        if (repetitionSeparator < 0) return null;
        return String.valueOf((Character) (char) repetitionSeparator);
    }

    public String getReleaseCharacter() {
        if (tokenizer == null) return null;
        int release = tokenizer.getRelease();
        if (release < 0) return null;
        return String.valueOf((Character) (char) release);
    }

    public String getSegmentTerminator() {
        return tokenizer == null ? null : String.valueOf(tokenizer.getTerminator());
    }

    public String getSegmentTerminatorSuffix() {
        return terminatorSuffix;
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

    private static class ProbeHandler extends EDIReaderSAXAdapter {
        public static final String STOP_PARSING = "StopParsing";
        private EDIStandard standard;
        private String version, documentType;
        private String interchangeControl, functionalGroupControl, documentControl;


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
