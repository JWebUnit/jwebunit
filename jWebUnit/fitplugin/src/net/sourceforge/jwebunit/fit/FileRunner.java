// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

// Modified by Wilkes Joiner and Jim Weaver

package net.sourceforge.jwebunit.fit;

import fit.Fixture;
import fit.Parse;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileRunner extends FitRunner {
    public Parse tables;
    public Fixture fixture;
    private File inFile;
    private File outFile;

    public static FileRunner parseArgs(String argv[]) {
        if (argv.length != 2) {
            System.err.println("usage: java fit.FileRunner input-file output-file");
            System.exit(-1);
        }
        return new FileRunner(new File(argv[0]), new File(argv[1]));
    }

    public static void main(String argv[]) {
        FileRunner runner = parseArgs(argv);
        runner.run();
        runner.resultWriter.write();
        System.err.println(runner.resultWriter.getCounts());
        System.exit(runner.resultWriter.getWrong() + runner.resultWriter.getExceptions());
    }

    public FileRunner(File in, File out) {
        this.inFile = in;
        this.outFile = out;
        fixture = new Fixture();
    }

    public void run() {
        try {
            tables = new Parse(read(inFile));
            fixture.doTables(tables);
        } catch (Exception e) {
            tables = new Parse("body", "Unable to parse input. Input ignored.", null, null);
            fixture.exception(tables, e);
        }
        resultWriter = new FileResultWriter(outFile, fixture.counts, tables);
    }

    protected String read(File input) throws IOException {
        FileReader in = null;
        try {
            char chars[] = new char[(int) (input.length())];
            in = new FileReader(input);
            in.read(chars);
            return new String(chars);
        } finally {
            if (in != null) in.close();
        }
    }

}

