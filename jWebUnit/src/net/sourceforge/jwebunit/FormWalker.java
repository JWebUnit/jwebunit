package net.sourceforge.jwebunit;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.CharacterData;

import java.util.ArrayList;

/**
 * Walks over a form's DOM to associate labels with field names.
 * <p>
 * @see WebTester#assertFormElementPresentWithLabel(String)
 * @see WebTester#assertFormElementNotPresentWithLabel(String)
 * @see WebTester#setFormElementWithLabel(String,String)
 */
class FormWalker {
    private StringBuffer currentText;
    private ArrayList texts;
    private ArrayList nodes;

    public FormWalker(Element formElement) {
        currentText = new StringBuffer();
        texts = new ArrayList();
        nodes = new ArrayList();
        walk(formElement);
    }

    private void walk(Node node) {
        if (node instanceof CharacterData) {
            currentText.append(((CharacterData) node).getData());
        } else if (node instanceof Element) {
            String tag = node.getNodeName();
            if (tag.equals("input") || tag.equals("select") ||
                tag.equals("textarea"))
            {
                addFormElement((Element) node);
            } else {
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    walk(children.item(i));
                }
            }
        }
    }

    private void addFormElement(Element node) {
        texts.add(currentText.toString());
        nodes.add(node);
        currentText = new StringBuffer();
    }

    public Element getFormElementWithLabel(String label) {
        for (int i = 0; i < nodes.size(); i++) {
            if (((String) texts.get(i)).indexOf(label) != -1) {
                return (Element) nodes.get(i);
            }
        }
        return null;
    }

}
