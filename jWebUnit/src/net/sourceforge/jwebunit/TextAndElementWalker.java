package net.sourceforge.jwebunit;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.CharacterData;

import java.util.ArrayList;

/**
 * Walks over a DOM subtree to associate labels with some kind of elements.
 */
class TextAndElementWalker {
    private StringBuffer currentText;
    private ArrayList texts;
    private ArrayList nodes;
    private String[] tags;

    public TextAndElementWalker(Element root, String[] tags) {
        currentText = new StringBuffer();
        texts = new ArrayList();
        nodes = new ArrayList();
        this.tags = tags;
        walk(root);
    }

    private void walk(Node node) {
        if (node instanceof CharacterData) {
            currentText.append(((CharacterData) node).getData());
        } else if (node instanceof Element) {
            String tag = node.getNodeName();
            if (matches(tag)) {
                rememberElement((Element) node);
            } else {
                NodeList children = node.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    walk(children.item(i));
                }
            }
        }
    }

    private boolean matches(String tag) {
        for (int i = 0; i < tags.length; i++) {
            if (tag.equalsIgnoreCase(tags[i])) {
                return true;
            }
        }
        return false;
    }

    private void rememberElement(Element node) {
        texts.add(currentText.toString());
        nodes.add(node);
        currentText = new StringBuffer();
    }

    public Element getElementAfterText(String label) {
        int index = indexOfElementAfterText(label);
        return index == -1 ? null : (Element) nodes.get(index);
    }

    private int indexOfElementAfterText(String label) {
        for (int i = 0; i < nodes.size(); i++) {
            if (((String) texts.get(i)).indexOf(label) != -1) {
                return i;
            }
        }
        return -1;
    }

    public Element getElementWithTextAfterText(String linkText, String labelText) {
        int index = indexOfElementAfterText(labelText);
        if (index != -1) {
            for (int i = index; i < nodes.size(); i++) {
                Element node = (Element) nodes.get(i);
                if (HttpUnitDialog.nodeContainsText(node, linkText)) {
                    return node;
                }
            }
        }
        return null;
    }

}
