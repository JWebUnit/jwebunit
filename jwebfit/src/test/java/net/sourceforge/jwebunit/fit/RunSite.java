package net.sourceforge.jwebunit.fit;

/**
 * Application that runs the sample site, useful when developing tests.
 */
public class RunSite {

    static WebFixtureTest test = null;
    
    /**
     * Run the sample web application that the test cases use.
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) {
        test = new WebFixtureTest("site");
        try {
            test.setUp();
            test.testFixtureStart();
            print("Site started at: " + test.getApplicationRootUrl());
            waitForClose(); 
        } catch (Exception e) {
            e.printStackTrace();
            print("Could not run the site. Error: " + e.getMessage());
        } finally {
            try {
                test.tearDown();
                test = null;
            } catch (Exception e) {
                print("Could not stop the site. Error: " + e.getMessage());
            }
        }
    }

    private static void print(String msg) {
        System.out.println(msg);
    }

    private static void waitForClose() {
        print("Press any key or close window to stop the sample site.");
        try {
            System.in.read();
            print("Key pressed. Shutting down jetty.");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    protected void finalize() throws Throwable {
        if (test != null) {
            test.tearDown();
        }
        super.finalize();
    }

}
