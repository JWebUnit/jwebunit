package net.sourceforge.jwebunit;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Walks over a DOM subtree in reverse order associate labels with preceding element.
 */
public class TextAndElementWalkerReverse extends TextAndElementWalker {

    public TextAndElementWalkerReverse(Element root, String[] tags) {
        super(root, tags);
    }

    protected void traverse(NodeList children) {
        for (int i = children.getLength() - 1; i >= 0; i--) {
            walk(children.item(i));
        }
    }

}
