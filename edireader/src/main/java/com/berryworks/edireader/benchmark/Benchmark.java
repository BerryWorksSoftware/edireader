/*
 * Copyright 2005-2015 by BerryWorks Software, LLC. All rights reserved.
 *
 * This file is part of EDIReader. You may obtain a license for its use directly from
 * BerryWorks Software, and you may also choose to use this software under the terms of the
 * GPL version 3. Other products in the EDIReader software suite are available only by licensing
 * with BerryWorks. Only those files bearing the GPL statement below are available under the GPL.
 *
 * EDIReader is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * EDIReader is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with EDIReader.  If not,
 * see <http://www.gnu.org/licenses/>.
 */

package com.berryworks.edireader.benchmark;

import com.berryworks.jquantify.EventCounter;
import com.berryworks.jquantify.SessionCounter;

public class Benchmark implements Runnable {

    private int numberOfThreads;
    private int iterations;
    private int interchangeKCs;
    private SessionCounter iterationCounter;
    private EventCounter charCounter;
    private boolean verbose;

    public void run() {
        System.out.println("EDIReader benchmark");

        validateParameters();

        System.out.println("  Parsing " + iterations + " interchanges");
        System.out.println("  Each interchange approximately " + interchangeKCs
                + " KCs in size");
        System.out.println("  Using " + numberOfThreads
                + ((numberOfThreads == 1) ? " thread" : " threads"));

        iterate();

        if (verbose)
            System.out.println(iterationCounter);
        if (verbose)
            System.out.println(charCounter);
        long totalChars = charCounter.getCumulativeEvents();
        long milliSeconds = charCounter.getAgeMillis();
        long charThroughput = (int) charCounter.getCumulativeFreq();
        long interchangeThroughput = iterations * 1000 / milliSeconds;
        System.out.println("Results");
        System.out.println("  Actual number of characters parsed: "
                + totalChars);
        System.out.println("  Number of milliseconds: " + milliSeconds);
        System.out.println("  Parsing throughput:");
        System.out.println("    " + charThroughput + " chars/second");
        System.out.println("    " + interchangeThroughput
                + " interchanges/second");
        System.out.println("EDIReader benchmark complete");

    }

    protected void iterate() {
        iterationCounter = new SessionCounter("Iterations");
        charCounter = new EventCounter("Parsed Characters");
        while (iterationCounter.getCumulativeEvents() < iterations) {
            if (iterationCounter.getConcurrency() < numberOfThreads) {
                BenchmarkUnitOfWork unitOfWork = createUnitOfWork();
                unitOfWork.setSessionCounter(iterationCounter);
                unitOfWork.setCharCounter(charCounter);
                unitOfWork.setInterchangeKCs(interchangeKCs);
                iterationCounter.start();
                new Thread(unitOfWork).start();
                if (verbose)
                    System.out.println("... "
                            + iterationCounter.getCumulativeEvents()
                            + " iterations have now been started");
            } else {
                if (verbose)
                    System.out.println("... maximum number of threads active");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        }

        while (iterationCounter.getConcurrency() > 0) {
            if (verbose)
                System.out.println("... " + iterationCounter.getConcurrency()
                        + " threads still active");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //ignore
            }
        }

    }

    protected void validateParameters() {
        if (numberOfThreads < 1)
            abort("Number of threads must be a positive number");

        if (interchangeKCs < 1)
            abort("Interchange size in KCs must be a positive number");

        if (iterations < 1)
            abort("Number of iterations must be a positive number");
    }

    protected void abort(String string) {
        System.err.println(string);
        throw new RuntimeException(string);
    }

    protected BenchmarkUnitOfWork createUnitOfWork() {
        return new AnsiUnitOfWork();
    }

    private void setVerbose(boolean b) {
        verbose = b;
    }

    public int getInterchangeKCs() {
        return interchangeKCs;
    }

    public void setInterchangeKCs(int interchangeKCs) {
        this.interchangeKCs = interchangeKCs;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public static void main(String[] args) {
        Benchmark controller = new Benchmark();
        setDefaults(controller);
        processArgs(args, controller);
        controller.run();
    }

    private static void setDefaults(Benchmark controller) {
        controller.setInterchangeKCs(100);
        controller.setIterations(10);
        controller.setNumberOfThreads(10);
        controller.setVerbose(false);
    }

    private static void processArgs(String[] args, Benchmark controller) {
        int n;
        for (String arg : args) {
            char c = arg.charAt(0);
            switch (c) {
                case 'v':
                    controller.setVerbose(true);
                    break;
                case 't':
                    n = parseInteger(arg.substring(1));
                    controller.setNumberOfThreads(n);
                    break;
                case 'k':
                    n = parseInteger(arg.substring(1));
                    controller.setInterchangeKCs(n);
                    break;
                case 'i':
                    n = parseInteger(arg.substring(1));
                    controller.setIterations(n);
                    break;
                default:
                    badArgs();
            }
        }
    }

    private static int parseInteger(String string) {
        int n = 0;
        try {
            n = Integer.parseInt(string);
        } catch (Exception e) {
            badArgs();
        }
        return n;
    }

    private static void badArgs() {
        System.err.println("Command line arguments:");
        System.err.println("  t<threads>       where <threads> is the number of concurrent threads");
        System.err.println("  k<kcs>           where <kcs> is the interchange size in 1024-char units");
        System.err.println("  i<interchanges>  where <interchanges> is the number of interchanges to parse");
        System.err.println("  v                verbose output");
        System.err.println();
        System.err.println("Notes:");
        System.err.println("  All arguments are optional. Default values:");
        System.err.println("    t  10");
        System.err.println("    k  100");
        System.err.println("    i  10");
        System.err.println("    v  false");
        throw new RuntimeException("Missing or invalid command line arguments");
    }

}
