package net.sourceforge.jwebunit.fit;

import fit.FileRunner;
import fit.Fixture;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

public class DirectoryRunner {
    private File targetDirectory;
    private int prevRight;
    private int prevWrong;
    private int prevIgnores;
    private int prevExceptions;
    private DirectoryResult dirResult;

    public static void main(String argv[]) {
        new DirectoryRunner().run(argv);
    }

    public void run(String argv[]) {
        process(argv);
        exit();
    }

    private void args(String[] argv) {
        if (argv.length > 1) {
            System.err.println("usage: java fit.DirectoryRunner [targetDirectory]");
            System.exit(-1);
        }
        if (argv.length == 1) {
            targetDirectory = new File(argv[0]);
        } else {
            targetDirectory = new File(".");
        }
        dirResult = new DirectoryResult(targetDirectory);
    }

    public void process(String argv[]) {
        args(argv);
        File[] files = targetDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.indexOf("fit.in.html") != -1;
            }
        });
        for (int i = 0; i < files.length; i++) {
            runFile(files[i]);
        }
        dirResult.writeIndexFile();
    }

    private void runFile(File file) {
        setPreviousCounts();
        final String filename = file.getName();
        final String outname = getOutputFileName(file.getName());
        String[] args = new String[]{file.getAbsolutePath(), targetDirectory.getPath() + "/" + outname};
        FileRunner runner = new FileRunner() {
            protected void exit() {
                captureSummary(filename, outname);
                output.close();
            }
        };
        runner.run(args);
    }

    protected void captureSummary(String filename, String outname) {
        FileResult result = new FileResult(filename, outname,
                                           Fixture.right - prevRight,
                                           Fixture.wrong - prevWrong,
                                           Fixture.ignores - prevIgnores,
                                           Fixture.exceptions - prevExceptions);
        result.dumpCounts();
        dirResult.addResult(result);
    }

    private void setPreviousCounts() {
        prevRight = Fixture.right;
        prevWrong = Fixture.wrong;
        prevIgnores = Fixture.ignores;
        prevExceptions = Fixture.exceptions;
    }

    private String getOutputFileName(String name) {
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
        return outputFileName.toString();
    }

    void exit() {
        System.out.println(Fixture.counts());
        System.exit(Fixture.wrong + Fixture.exceptions);
    }
}
