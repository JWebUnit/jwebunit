package net.sourceforge.jwebunit.sample;

import net.sourceforge.jwebunit.WebTestCase;
import net.sourceforge.jwebunit.WebTester;
import net.sourceforge.jwebunit.plugin.jacobie.JacobieDialog;

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
    
    public WebTester initializeWebTester() {
        return new WebTester(new JacobieDialog());
    }

    public void testSearch() {
        beginAt("/");
        setFormElement("q", "jwebunit");
        submit("btnG");
        clickLinkWithText("jWebUnit");
        assertTitleEquals("jWebUnit - jWebUnit");

        clickLinkWithText("Quick start");
        assertTitleEquals("jWebUnit - Quick Start");
    }
}