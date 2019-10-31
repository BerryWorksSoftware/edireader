package com.berryworks.edireader.util;

import java.io.*;


public class HtmlExtractCodes implements Runnable {

    public static final String H1 = "<h1>";
    public static final String TR_TD = "<tr><td align=right valign=top><b>";
    public static final String TD = "<td>";
    private final BufferedReader reader;
    private final PrintStream output;

    public HtmlExtractCodes(File file, PrintStream output) {
        if (!file.exists())
            throw new RuntimeException("Cannot find " + file.getName());
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Cannot read " + file.getName());
        }

        this.output = output;
    }

    @Override
    public void run() {

        String codename = "unknown";
        String line;

        try {
            while ((line = reader.readLine()) != null) {

                if (line.startsWith(H1)) {
                    codename = line.substring(H1.length());
                    codename = codename.substring(0, codename.indexOf('-')).trim();
                    continue;
                }

                if (!line.startsWith(TR_TD)) continue;

                final int startIndex = TR_TD.length();
                final int endIndex = line.indexOf('<', startIndex);
                final String particularCodeValue = line.substring(startIndex, endIndex);

                String line2 = reader.readLine();
                int i = line2.indexOf(TD);
                int startOfDescription = i + TD.length();
                int endOfDescription = line2.indexOf('<', startOfDescription);
                while (endOfDescription == -1) {
                    String continuedDescription = reader.readLine();
                    line2 += continuedDescription;
                    endOfDescription = line2.indexOf('<', startOfDescription);
                }

                final String description = line2.substring(startOfDescription, endOfDescription).trim();

                final String text = codename + "|" + particularCodeValue + "|" + description + "|";
                output.println(text);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final String directoryName = "/Users/mayberry/Documents/X12_February_2009/X12_February_2009/de/";
        File directory = new File(directoryName);
        if (!(directory.exists() && directory.isDirectory())) {
            throw new RuntimeException("Cannot find directoryName " + directoryName);
        }
        File[] htmlFiles = directory.listFiles((directory1, name) -> name.endsWith(".HTM"));

        final PrintStream printStream;
        try {
            printStream = new PrintStream(new FileOutputStream(new File("codes.tmp")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to write to output file");
        }

        for (File htmlFile : htmlFiles) {
            new HtmlExtractCodes(htmlFile, printStream).run();
        }


    }
}
