package net.sourceforge.jwebunit.fit;

import fit.FileRunner;
import fit.Fixture;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

public class DirectoryRunner {
    private File inputDir;
    private File outputDir;
    private DirectoryResult results;
    private int prevRight;
    private int prevWrong;
    private int prevIgnores;
    private int prevExceptions;


    public static DirectoryRunner parseArgs(String[] argv) {
        if (argv.length > 2) {
            System.err.println("usage: java fit.DirectoryRunner [target directory] [output directory]");
            System.exit(-1);
        }
        File in = (argv.length >= 1) ?  new File(argv[0]) : new File(".");
        File out = (argv.length == 2) ? new File(argv[1]) : in;
        return new DirectoryRunner(in, out);
    }

    public static void main(String argv[]) {
        DirectoryRunner runner = parseArgs(argv);
        runner.run();
    }

    public DirectoryRunner(File inputDirectory, File outputDirectory) {
        inputDir = inputDirectory;
        outputDir = outputDirectory;
        results = new DirectoryResult(outputDir);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
    }

    public void run() {
        process();
        exit();
    }

    public void process() {
        File[] files = inputDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.indexOf("fit.in.html") != -1 || new File(dir, name).isDirectory();
            }
        });
        for (int i = 0; i < files.length; i++) {
            runFile(files[i]);
        }
        results.writeIndexFile();
    }

    private void runFile(File file) {
        if (file.isDirectory()) {
            DirectoryRunner runner = new DirectoryRunner(file, new File(outputDir, file.getName()));
            runner.run();
            results.addResult(runner.results);
        } else {
            setPreviousCounts();
            final File outFile = getOutputFile(file.getName());
            String[] args = new String[]{file.getAbsolutePath(), outFile.getAbsolutePath()};
            FileRunner runner = new FileRunner() {
                protected void exit() {
                    captureSummary(outFile);
                    output.close();
                }
            };
            runner.run(args);
        }
    }

    protected void captureSummary(File outFile) {
        FileResult result = new FileResult(outFile,
                Fixture.right - prevRight,
                Fixture.wrong - prevWrong,
                Fixture.ignores - prevIgnores,
                Fixture.exceptions - prevExceptions);
        result.dumpCounts();
        results.addResult(result);
    }

    private void setPreviousCounts() {
        prevRight = Fixture.right;
        prevWrong = Fixture.wrong;
        prevIgnores = Fixture.ignores;
        prevExceptions = Fixture.exceptions;
    }

    private File getOutputFile(String name) {
        StringTokenizer tokenizer = new StringTokenizer(name, ".", true);
        StringBuffer outputFileName = new StringBuffer();
        while (tokenizer.hasMoreTokens()) {
            String part = tokenizer.nextToken();
            if (part.equals("in")) {
                outputFileName.append("out");
            } else {
                outputFileName.append(part);
            }
        }
        return new File(outputDir, outputFileName.toString());
    }

    void exit() {
        System.out.println(Fixture.counts());
    }
}