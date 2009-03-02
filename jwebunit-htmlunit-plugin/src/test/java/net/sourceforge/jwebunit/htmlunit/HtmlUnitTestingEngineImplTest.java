package net.sourceforge.jwebunit.htmlunit;

import junit.framework.TestCase;
import com.gargoylesoftware.htmlunit.WebClient;
import net.sourceforge.jwebunit.api.ITestingEngine;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class HtmlUnitTestingEngineImplTest extends TestCase {
    public void testSetThrowExceptionOnScriptErrorShouldSetSameOnUnderlyingWebClient() throws Exception {
        WebClient client = mock(WebClient.class);
        ITestingEngine engine = new HtmlUnitTestingEngineImpl(client);

        engine.setThrowExceptionOnScriptError(true);

        verify(client).setThrowExceptionOnScriptError(true);
    }

}
