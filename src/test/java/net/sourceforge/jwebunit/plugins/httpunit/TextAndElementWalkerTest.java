/**
 * User: djoiner
 * Date: Nov 21, 2002
 * Time: 10:34:16 AM
 */
package net.sourceforge.jwebunit.plugins.httpunit;

import junit.framework.TestCase;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TextAndElementWalkerTest extends TestCase {

    public void testFindLabel() throws ParserConfigurationException {
        Element form = buildForm();
        TextAndElementWalker walker =
                new TextAndElementWalker(form,
                    new String[] { "input", "select", "textarea" });
        assertNotNull(walker.getElementAfterText("First"));
    }


//  <form method=GET action="TargetPage">
//      First : <input type="text" name="param1">
//      Second : <textarea name="param2">
//  </form>
    private Element buildForm() throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element form = doc.createElement("form");
        form.setAttribute("method", "GET");
        form.setAttribute("action", "TargetPage");
        form.appendChild(doc.createTextNode("First : "));
        Element param1 = doc.createElement("input");
        param1.setAttribute("type", "text");
        param1.setAttribute("name", "param1");
        form.appendChild(param1);
        form.appendChild(doc.createTextNode("Second : "));
        Element param2 = doc.createElement("input");
        param2.setAttribute("type", "text");
        param2.setAttribute("name", "param2");
        form.appendChild(param2);
        return form;
    }

}
