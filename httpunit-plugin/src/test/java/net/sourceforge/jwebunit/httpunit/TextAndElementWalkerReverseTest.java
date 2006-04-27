package net.sourceforge.jwebunit.plugins.httpunit;

import junit.framework.TestCase;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TextAndElementWalkerReverseTest extends TestCase {

    public void testFindLabel() throws ParserConfigurationException {
        Element form = buildSimpleForm();
        TextAndElementWalker walker = new TextAndElementWalkerReverse(form,
                new String[] { "input", "select", "textarea" });
        assertNotNull("Should find the field before the text", walker
                .getElementAfterText("Check"));
        assertNotNull("Should find the field before the complete text", walker
                .getElementAfterText(" Check 1"));
    }

    // <form method="GET" action="TargetPage">
    // <input type="checkbox" name="chk" value="2" /> Check 1
    // </form>
    private Element buildSimpleForm() throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().newDocument();
        Element form = doc.createElement("form");
        form.setAttribute("method", "GET");
        form.setAttribute("action", "TargetPage");
        // checkbox
        Element param1 = doc.createElement("input");
        param1.setAttribute("type", "checkbox");
        param1.setAttribute("name", "chk");
        param1.setAttribute("value", "2");
        form.appendChild(param1);
        // name
        form.appendChild(doc.createTextNode(" Check 1"));
        return form;
    }

}
