package net.sourceforge.jwebunit.sample;

import net.sourceforge.jwebunit.WebTestCase;
import net.sourceforge.jwebunit.WebTester;
import net.sourceforge.jwebunit.plugin.jacobie.JacobieDialog;

/**
 * Intended Uses:
 * --Example for end developers of jwebunit to show how jacobie will run within jwebunit. 
 * 
 * @author Nick Neuberger
 */
public class JWebUnitJacobieExampleWebTestCase extends WebTestCase {

    public static final String GOOGLE_SITE = "http://www.google.com";
    
    public static final String SF_NET_SECURE = "https://sourceforge.net";
    public static final String SF_NET_NOT_SECURE = "http://sourceforge.net";
    public static final String JWEBUNIT_BEGIN_AT = "/projects/jwebunit/";
    
    public static final String JACOBIE_BEGIN_AT = "/projects/jacobie/";
    
    public static final String COMPANY_STAGE_LOCALHOST = "http://localhost:8080";
    public static final String COMPANY_STAGE_CRUISECONTROL = "http://cruise1server";
    public static final String COMPANY_STAGE_QA = "http://qa1server";
    public static final String COMPANY_STAGE_PREPROD = "http://preprod1server";
    public static final String COMPANY_LOGIN_BEGIN_AT = "/ims/servlet/Login?COLLABORATION=login&OPERATION=display&TARGET=home";

    /**
     *  
     */
    public JWebUnitJacobieExampleWebTestCase() {
        super();
    }

    public void setUp() throws Exception {
        super.setUp();
        getTestContext().setBaseUrl(SF_NET_NOT_SECURE);

        //authenticateApplication();
    }

    /**
     * Gets the UserId used in the test cases. Also used for clicking users on
     * ACL screens.
     */
    public String getUserId() {
        return "USERIDGOESHERE";
    }

    public String getUserPass() {
        return "PASSWORDGOESHERE";
    }
    
    public String getUserFullName() {
        return "Nick Neuberger";
    }
    
    public JacobieDialog getJacobieDialog() {
        return (JacobieDialog) getDialog();
    }

    /**
     * Call this to login or authenticate with the IMS application.
     */
    public void authenticateApplication() {
        beginAt(COMPANY_LOGIN_BEGIN_AT);

        //assert your title of your login page.
        assertTitleEquals("IMS Login");

        setFormElement("userId", getUserId());
        setFormElement("password", getUserPass());

        submit();

        assertTitleEquals("Home Page Title after login");

    }

    public WebTester initializeWebTester() {
        return new WebTester(new JacobieDialog());
    }

    /**
     * Sets up the http unit options.
     */
    public void setUpHttpUnitOptions() {
        //HttpUnitOptions.setExceptionsThrownOnScriptError(false);
    }

    public void setUpJacobieOptions() {

    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testJWebUnitProjNotSecure() {
        beginAt(JWEBUNIT_BEGIN_AT);
        assertTitleEquals("SourceForge.net: Project Info - jWebUnit");
        assertTextPresent("Project: jWebUnit: Summary");
        
        clickLinkWithText("Home Page");
        assertTitleEquals("jWebUnit - jWebUnit");
        
        clickLinkWithText("Project page");
        assertTitleEquals("SourceForge.net: Project Info - jWebUnit");

        //problem with this link.  not waiting until the page is loaded.
//      clickLinkWithText("statistics");
//      assertTitleEquals("SourceForge.net: Project Info - jWebUnit");
        
        clickLinkWithText("Forums");
        assertTitleEquals("SourceForge.net: Forums for jWebUnit");
        
        clickLinkWithText("Tracker");
        assertTitleEquals("SourceForge.net");
        
    }
    

}