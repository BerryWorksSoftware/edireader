package com.berryworks.edireader.util;

import com.berryworks.edireader.EDIReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

public class StandardRecognizer implements Runnable {

    private final File ediFile;
    private String standard;

    public static void main(String args[]) {
        if (args.length < 1) {
            throw new RuntimeException("A filename argument is required");
        }

        File file = new File(args[0]);
        if (!file.exists() || file.isDirectory() || !file.canRead()) {
            throw new RuntimeException("Unable to read EDI data from " + file.getAbsolutePath());
        }

        StandardRecognizer standardRecognizer = new StandardRecognizer(file);
        standardRecognizer.run();
        System.out.println("Standard: " + standardRecognizer.getStandard());
    }

    public StandardRecognizer(File ediFIle) {
        this.ediFile = ediFIle;
    }

    @Override
    public void run() {
        try (Reader fileReader = new FileReader(ediFile)) {
            EDIReader ediReader = new EDIReader();
            MyContentHandler handler = new MyContentHandler();
            ediReader.setContentHandler(handler);
            ediReader.parse(new InputSource(fileReader));
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse EDI input", e);
        }
    }

    public String getStandard() {
        return standard;
    }

    private class MyContentHandler extends DefaultHandler {

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("interchange".equals(localName)) {
                standard = attributes.getValue("Standard");
            }
        }
    }
}
