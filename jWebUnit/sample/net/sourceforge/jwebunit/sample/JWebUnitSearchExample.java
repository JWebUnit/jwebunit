package net.sourceforge.jwebunit.sample;

import net.sourceforge.jwebunit.WebTestCase;

/**
 * Google search test with jWebUnit.
 *
 * @author Jim Weaver
 */
public class JWebUnitSearchExample extends WebTestCase {

    public JWebUnitSearchExample(String name) {
        super(name);
    }

    public void setUp() {
        getTestContext().setBaseUrl("http://www.google.com");
    }

    public void testSearch() {
        beginAt("/");
        setFormElement("q", "httpunit");
        submit("btnG");
        clickLinkWithText("HttpUnit Home");
        assertTitleEquals("HttpUnit Home");
        assertLinkPresentWithText("User's Manual");
    }
}