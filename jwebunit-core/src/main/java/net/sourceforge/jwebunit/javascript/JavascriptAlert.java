/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.javascript;

public class JavascriptAlert {

    private String message;

    public JavascriptAlert(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
