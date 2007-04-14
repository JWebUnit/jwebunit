/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.javascript;

public class JavascriptConfirm {

    private String message;

    private boolean action;

    public JavascriptConfirm(String message, boolean action) {
        this.message = message;
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public boolean getAction() {
        return action;
    }

}
