package net.sourceforge.jwebunit.sample;

import net.sourceforge.jwebunit.TestingEngineRegistry;
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

    public void setUp() throws Exception {
    	super.setUp();
        getTestContext().setBaseUrl("http://www.google.com");
        
        //New implementation on choosing a testing engine (dialog).
        setTestingEngineKey(TestingEngineRegistry.TESTING_ENGINE_JACOBIE);
    }
    
    public void testSearch() throws Exception {
        beginAt("/");
        setFormElement("q", "jwebunit");
        submit("btnG");
        clickLinkWithText("jWebUnit - jWebUnit");
        assertTitleEquals("jWebUnit - jWebUnit");

        clickLinkWithText("Quick start");
        assertTitleEquals("jWebUnit - Quick Start");
    }
}