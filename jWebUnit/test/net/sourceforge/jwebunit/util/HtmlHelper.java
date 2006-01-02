package net.sourceforge.jwebunit.util;

public abstract class HtmlHelper {

    
    private static final String VAR_TITLE = "title not set";
    
    public static final String START =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
        "<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\"\n" +
        "    pageEncoding=\"UTF-8\"%>\n" +
        "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
        "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
        "<head>\n" +
        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
        "<title>" + VAR_TITLE + "</title>\n" +
        "</head>\n" +
        "<body>\n";
    
    public static final String END = 
        "</body>\n" +
        "</html>\n";
    
    public static String getStart(String title) {
        return START.replaceFirst(VAR_TITLE, title);
    }
    
    public static String getEnd() {
        return END;
    }
    
    public static String getLinkParagraph(String id, String url) {
        return "<p><a id=\"" + id + "\" href=\"" + url + "\">return</a></p>\n";
    }
}
