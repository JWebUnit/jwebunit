/*
 * User: djoiner
 * Date: Oct 5, 2002
 * Time: 1:19:50 PM
 */
package fit;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DirectoryResult {
    private List results;
    private File directory;

    public DirectoryResult(File directory) {
        this.directory = directory;
        results = new ArrayList();
    }

    public void addResult(FileResult result) {
        results.add(result);
    }

    public void writeIndexFile() {
        File indexFile = new File(directory, "index.html");
        FileWriter writer = null;
        try {
            writer = new FileWriter(indexFile);
            writer.write("<html><head><title>Fit Results Summary</title></head><body>");
            writer.write("<h1>Results</h1>");
            writer.write("<table border=\"1\" cellspacing=\"5\" cellpadding=\"5\">");
            writeResults(writer);
            writer.write("</table>");
            writer.write("<br>");
            writer.write(new Date().toString());
            writer.write("</body></html>");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeResults(FileWriter writer) throws IOException {
        for (Iterator iterator = results.iterator(); iterator.hasNext();) {
            FileResult result = (FileResult) iterator.next();
            String color = (result.didFail()) ? "#ffcfcf" : "#cfffcf";
            writeRow(writer, result, color);
        }
    }

    private void writeRow(FileWriter writer, FileResult result, String color) throws IOException {
        writer.write("<tr bgcolor=\"" + color + "\"><td>");
        writer.write("<a href=\"" + result.getOutname() + "\">" + result.getTestName() + "</a>");
        writer.write("<td>" + result.counts() + "</td>");
        writer.write("</td></tr>");

    }

}
