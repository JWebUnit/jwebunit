package fit;

import java.io.*;
import java.util.StringTokenizer;

public class DirectoryRunner {

    private File targetDirectory;

    public static void main (String argv[]) {
        new DirectoryRunner().run(argv);
    }

    public void run(String argv[]) {
        process(argv);
        exit();
    }

    public void process(String argv[]) {
        args(argv);
        File [] files = targetDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.indexOf("fit.in.html") != -1;
            } }
            );
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            FileRunner runner = new FileRunner();
            runner.process(new String[] {file.getAbsolutePath(), getOutputFileName(file.getName())});
        }
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
        return targetDirectory.getPath() + "/" + outputFileName.toString();
    }

    void args(String[] argv) {
        if (argv.length > 1) {
            System.err.println("usage: java fit.DirectoryRunner [targetDirectory]");
            System.exit(-1);
        }
        if(argv.length == 1) {
            targetDirectory = new File(argv[0]);
        } else {
            targetDirectory = new File(".");
        }
    }


   void exit() {
        System.err.println(Fixture.counts());
        System.exit(Fixture.wrong + Fixture.exceptions);
    }
}
