package net.sourceforge.jwebunit.fit;

import fit.Fixture;
import fit.Parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileRunner extends FitRunner {
    public String input;
    public Parse tables;
    public Fixture fixture = new Fixture();
    public PrintWriter output;
    private File inFile;
    private File outFile;
    private int prevRight;
    private int prevWrong;
    private int prevIgnores;
    private int prevExceptions;

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
        System.err.println(Fixture.counts());
        System.exit(Fixture.wrong + Fixture.exceptions);
    }

    public FileRunner(File in, File out) {
        this.inFile = in;
        this.outFile = out;
    }

    public void run() {
        process();
        exit();
    }

    public void process() {
        setPreviousCounts();
        try {
            input = read(inFile);
            output = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
            tables = new Parse(input);
            fixture.doTables(tables);
        } catch (Exception e) {
            exception(e);
        }
        tables.print(output);
    }

    private void setPreviousCounts() {
        prevRight = Fixture.right;
        prevWrong = Fixture.wrong;
        prevIgnores = Fixture.ignores;
        prevExceptions = Fixture.exceptions;
    }

    protected String read(File input) throws IOException {
        char chars[] = new char[(int) (input.length())];
        FileReader in = new FileReader(input);
        in.read(chars);
        in.close();
        return new String(chars);
    }

    protected void exception(Exception e) {
        tables = new Parse("body", "Unable to parse input. Input ignored.", null, null);
        Fixture.exception(tables, e);
    }

    protected void exit() {
        if (output != null) {
            output.close();
        }
        result = new FileResult(outFile,
                Fixture.right - prevRight,
                Fixture.wrong - prevWrong,
                Fixture.ignores - prevIgnores,
                Fixture.exceptions - prevExceptions);
    }

}

