package fit;

// Copyright (c) 2002 Cunningham & Cunningham, Inc.
// Released under the terms of the GNU General Public License version 2 or later.

import java.io.*;
import java.util.*;

public class FileRunner {

    String input;
    Parse tables;
    Fixture fixture = new Fixture();
    PrintWriter output;

    public static void main (String argv[]) {
        new FileRunner().run(argv);
    }

    public void run (String argv[]) {
        process(argv);
        exit();
    }

    public void process(String argv[]) {
        args(argv);
        try {
            tables = new Parse(input);
            fixture.doTables(tables);
        } catch (Exception e) {
            exception(e);
        }
        tables.print(output);
        output.close();
    }

    void args(String[] argv) {
        if (argv.length != 2) {
            System.err.println("usage: java fit.FileRunner input-file output-file");
            System.exit(-1);
        }
        File in = new File(argv[0]);
        File out = new File(argv[1]);
        Fixture.summary.put("input file", in.getAbsolutePath());
        Fixture.summary.put("input update", new Date(in.lastModified()));
        Fixture.summary.put("output file", out.getAbsolutePath());
        try {
            input = read(in);
            output = new PrintWriter(new BufferedWriter(new FileWriter(out)));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    String read(File input) throws IOException {
        char chars[] = new char[(int)(input.length())];
        FileReader in = new FileReader(input);
        in.read(chars);
        in.close();
        return new String(chars);
    }

    void exception(Exception e) {
        tables = new Parse("body","Unable to parse input. Input ignored.", null, null);
        Fixture.exception(tables, e);
    }

    void exit() {
        System.err.println(Fixture.counts());
        System.exit(Fixture.wrong + Fixture.exceptions);
    }

}
