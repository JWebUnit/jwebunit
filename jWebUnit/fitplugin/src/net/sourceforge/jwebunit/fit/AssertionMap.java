package net.sourceforge.jwebunit.fit;

import java.util.HashMap;

/**
 * Map camelized second cell of WebFixture checks to assertion methods on the
 * jWebunit WebTester.  The initial purpose here is to remove the need
 * to use trailing "equal" and "equals" in the fit tables.
 *
 * i.e.
 *
 * Table entry is: check | form element | value
 *
 * We pass into the map "form element" and return "form element equals".
 *
 * @author Jim Weaver
 */
public class AssertionMap {

    private static HashMap map = new HashMap();

    static {
        map.put("title", "title equals");
        map.put("form element", "form element equals");
        map.put("title key", "title equals key");
        map.put("option", "option equals");
    }

    public static String getAssertionName(String fitName) {
        String translation = (String)map.get(fitName);
        if (translation != null)
            return translation;
        else
            return fitName;
    }
}
