/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.javascript;

public class JavascriptPrompt {

    private String message;

    private String input;

    public JavascriptPrompt(String message, String input) {
        this.message = message;
        this.input = input;
    }

    public String getMessage() {
        return message;
    }

    public String getInput() {
        return input;
    }

}
