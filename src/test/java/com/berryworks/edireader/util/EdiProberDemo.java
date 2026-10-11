package com.berryworks.edireader.util;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class EdiProberDemo {

    public static void main(String[] args) {
        EdiProber ediProber = new EdiProber();
        File ediFile = new File(args[0]);
        try {
            ediProber.probe(ediFile);
            System.out.println("Standard: " + ediProber.getStandard());
            System.out.println("Version: " + ediProber.getVersion());
            System.out.println("Sender: " + ediProber.getSenderId());
            System.out.println("Sender Qualifier: " + ediProber.getSenderQualifier());
            System.out.println("Receiver: " + ediProber.getReceiverId());
            System.out.println("Receiver Qualifier: " + ediProber.getReceiverQualifier());
            System.out.println("Document Type: " + ediProber.getDocumentType());
            System.out.println("Interchange Control: " + ediProber.getInterchangeControl());
            System.out.println("Functional Group Control: " + ediProber.getFunctionalGroupControl());
            System.out.println("Document Control: " + ediProber.getDocumentControl());
            System.out.println("Delimiter: " + ediProber.getDelimiter());
            System.out.println("SubDelimiter: " + ediProber.getSubDelimiter());
            System.out.println("Segment Terminator: " + ediProber.getSegmentTerminator());
            System.out.println("Segment Terminator Suffix: " + ediProber.getSegmentTerminatorSuffix());
            System.out.println("Repetition Delimiter: " + ediProber.getRepetitionDelimiter());
            System.out.println("Release Character: " + ediProber.getReleaseCharacter());
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}