package net.sourceforge.jwebunit.fit;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

public class DirectoryRunner extends FitRunner {
    private File inputDir;
    private File outputDir;

    public static DirectoryRunner parseArgs(String[] argv) {
        if (argv.length > 2) {
            System.err.println(
                    "usage: java fit.DirectoryRunner [input directory] [output directory]");
            System.exit(-1);
        }
        File in = (argv.length >= 1) ? new File(argv[0]) : new File(".");
        File out = (argv.length == 2) ? new File(argv[1]) : in;
        System.out.println(in +  " " + out);
        return new DirectoryRunner(in, out);
    }

    public static void main(String argv[]) throws Throwable {
			DirectoryRunner runner = parseArgs(argv);
			runner.run();
			runner.getResultWriter().write();
			System.out.println("Cumulative Results: " + runner.getResultWriter().getCounts());
    }

    public DirectoryRunner(File inputDirectory, File outputDirectory) {
        inputDir = inputDirectory;
        outputDir = outputDirectory;
        resultWriter = new DirectoryResultWriter(outputDir);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
    }

    public void run() {
        if (!inputDir.exists()) {
            throw new RuntimeException("The input directory '" + inputDir.getAbsolutePath() + "' does not exist.");
        }
        File[] files = inputDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return hasRightExtension(name) || (new File(dir, name).isDirectory() && !name.equals("CVS"));
            }

            private boolean hasRightExtension(String name) {
                return RunnerUtility.useWikiParser() ? name.endsWith(".fit") : name.endsWith(".fit.in.html");
            }
        });
        for (int i = 0; i < files.length; i++) {
            runFile(files[i]);
        }
    }

    private void runFile(File inFile) {
        FitRunner runner = getRunner(inFile);
        runner.run();
        FitResultWriter eachResult = runner.getResultWriter();
        ((DirectoryResultWriter) getResultWriter()).addResult(eachResult);
        System.out.println(eachResult.getDisplayName() + ":\n\t" + eachResult.getCounts() + "\n");
    }

    private FitRunner getRunner(File inFile) {
        if (inFile.isDirectory())
            return new DirectoryRunner(inFile, new File(outputDir, inFile.getName()));
        return new FileRunner(inFile, getOutputFile(inFile.getName()));
    }

    private File getOutputFile(String name) {
        return RunnerUtility.useWikiParser() ? getWikiOutputFile(name) : getHtmlOutputFile(name);
    }

    private File getWikiOutputFile(String name) {
        return new File(outputDir, name + ".html");
    }

    private File getHtmlOutputFile(String name) {
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

}