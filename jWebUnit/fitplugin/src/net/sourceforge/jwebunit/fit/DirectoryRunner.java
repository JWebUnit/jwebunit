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
		System.out.println(argv[0] + " " + argv[1]);
		File in = (argv.length >= 1) ? new File(argv[0]) : new File(".");
		File out = (argv.length == 2) ? new File(argv[1]) : in;
		return new DirectoryRunner(in, out);
	}

	public static void main(String argv[]) {
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
		File[] files = inputDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
					//TODO: make a propertye
		//return name.indexOf("fit.in.html") != -1
				return name.indexOf("fit") != -1 || (new File(dir, name).isDirectory() && !name.equals("CVS"));
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
		//return getHtmlOutputFile(name);
		return getWikiOutputFile(name);
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