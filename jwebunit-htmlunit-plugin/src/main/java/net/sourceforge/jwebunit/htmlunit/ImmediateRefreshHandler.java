/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.htmlunit;

import java.io.IOException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.RefreshHandler;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebWindow;

/**
 * Custom Handler until HtmlUnit fix. Cf Bug 1628076
 */
public class ImmediateRefreshHandler implements RefreshHandler {

    /**
     * Immediately refreshes the specified page using the specified URL.
     * 
     * @param page The page that is going to be refreshed.
     * @param url The URL where the new page will be loaded.
     * @param seconds The number of seconds to wait before reloading the page (ignored!).
     * @throws IOException if the refresh fails
     */
    public void handleRefresh(final Page page, final URL url, final int seconds)
            throws IOException {
        final WebWindow window = page.getEnclosingWindow();
        if (window == null) {
            return;
        }
        final WebClient client = window.getWebClient();
        if (page.getWebResponse().getUrl().toExternalForm().equals(
                url.toExternalForm())
                && SubmitMethod.GET.equals(page.getWebResponse()
                        .getRequestMethod())) {
            if (seconds > 0) {
                // Do not refresh
                return;
            } else {
                final String msg = "Refresh Aborted by HtmlUnit: "
                        + "Attempted to refresh a page using an ImmediateRefreshHandler "
                        + "which could have caused an OutOfMemoryError "
                        + "Please use WaitingRefreshHandler or ThreadedRefreshHandler instead.";
                throw new RuntimeException(msg);
            }
        }
        client.getPage(window, new WebRequestSettings(url));
    }

}
