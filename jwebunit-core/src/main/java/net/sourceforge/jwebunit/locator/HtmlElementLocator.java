/******************************************************************************
 * jWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.locator;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the interface for all HtmlElementLocators.
 * A Html element locator is a way to locate one element
 * in an HTML page.
 * My inspiration come from Selenium.
 * 
 * @author Julien Henry
 */
public class HtmlElementLocator implements Locator {
    
    private String tag;
    
    /**
     * When more than one element can match the current locator, users can specify
     * this 0-based index.
     */
    private int index = 0;
    
    private List<HtmlElementAttribut> attributs = new LinkedList<HtmlElementAttribut>();
    
    /**
     * Text representation of the childs of this node.
     */
    private String text;
    
    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return Returns the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index The index to set.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public HtmlElementLocator(String tag) {
        this.tag=tag;
    }
    
    public HtmlElementLocator(String tag, String id) {
        this.tag=tag;
        addAttribut("id", id);
    }

    /**
     * Get html tag of the element (i.e. button, textarea, select, ...)
     * @return The html tag of the element.
     */
    public String getTag() {
        return tag;
    }
    
    /**
     * Change or add an attribut.
     * @param name
     * @param value
     */
    public void addAttribut(String name, String value) {
        removeAttribut(name);
        attributs.add(new HtmlElementAttribut(name, value));
    }
    
    public void removeAttribut(String name) {
        for (HtmlElementAttribut a : attributs) {
            if (a.getName().equals(name)) {
                attributs.remove(a);
                return;
            }
        }
    }
    
    public List<HtmlElementAttribut> getAttributs() {
        return attributs;
    }
    
    public class HtmlElementAttribut {
        private String name;
        private String value;
        
        public HtmlElementAttribut(String name, String value) {
            this.name=name;
            this.value=value;
        }
        
        /**
         * @return Returns the name.
         */
        public String getName() {
            return name;
        }
        /**
         * @return Returns the value.
         */
        public String getValue() {
            return value;
        }
    }
    
    public String getXPath() {
        StringBuffer result = new StringBuffer();
        result.append("//");
        result.append(getTag());
        if (getAttributs().size()>0) {
            result.append("[");
        }
        boolean first = true;
        for (HtmlElementAttribut a:getAttributs()) {
            if (!first) {
                result.append(" and ");
            }
            else {
                first = false;
            }
            result.append("@").append(a.name).append("=\"").append(a.value).append("\"");
        }
        if (getAttributs().size()>0) {
            result.append("]");
        }
        if (getIndex()>0) {
            result.append("[").append(getIndex()).append("]");
        }
        
        return result.toString();
    }
    
    public String toString() {
        StringBuffer result = new StringBuffer("<");
        result.append(getTag());
        for (HtmlElementAttribut a : getAttributs()) {
            result.append(" ").append(a.getName()).append(" ").append("\"").append(a.getValue()).append("\"");
        }
        result.append(">...</").append(getTag()).append(">");
        return result.toString();
    }

}
