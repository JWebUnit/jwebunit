package net.sourceforge.jwebunit;

/********************************************************************************
 * jWebUnit, simplified web testing API for HttpUnit
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * 651 W Washington Ave. Suite 500
 * Chicago, IL 60661 USA
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     + Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     + Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     + Neither the name of ThoughtWorks, Inc., jWebUnit, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.SubmitButton;
import com.meterware.httpunit.TableCell;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;
import com.meterware.httpunit.WebClientListener;
import com.meterware.httpunit.WebClient;
import net.sourceforge.jwebunit.util.ExceptionUtility;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper for HttpUnit access.  A dialog is initialized with a
 * given URL, and maintains conversational state as the dialog progresses through link navigation,
 * form submission, etc.  Public access is provided for wrappered HttpUnit objects.
 *
 * @author Jim Weaver
 * @author Wilkes Joiner
 */
public class HttpUnitDialog {
    private WebConversation wc;
    private WebResponse resp;
    private TestContext context;
    private WebForm form;

    public HttpUnitDialog(String initialURL, TestContext context) {
        this.context = context;
        initWebConversation();
        try {
            resp = wc.getResponse(new GetMethodWebRequest(initialURL));
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }


    private void initWebConversation() {
        wc = new WebConversation();

        if (context != null) {
            if (context.hasAuthorization())
                wc.setAuthorization(context.getUser(), context.getPassword());
            if (context.hasCookies()) {
                List cookies = context.getCookies();
                for (Iterator iter = cookies.iterator(); iter.hasNext();) {
                    Cookie c = (Cookie) iter.next();
                    wc.addCookie(c.getName(), c.getValue());
                }
            }
        }

        wc.addClientListener(new WebClientListener() {
            public void requestSent(WebClient webClient, WebRequest webRequest) {
            }

            public void responseReceived(WebClient webClient, WebResponse webResponse) {
                resp = webResponse;
                form = null;
            }
        });
    }

    public WebResponse getResponse() {
        return resp;
    }

    public String getResponseText() {
        try {
            return context.toEncodedString(resp.getText());
        } catch (IOException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    public String getResponsePageTitle() {
        try {
            return context.toEncodedString(resp.getTitle());
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }


    public WebForm getForm() {
        if (form == null && hasForm())
            setWorkingForm(getForm(0));
        return form;
    }

    private WebForm getForm(int formIndex) {
        try {
            return resp.getForms()[formIndex];
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    private WebForm getForm(String formName) {
        try {
            WebForm f = resp.getFormWithID(formName);
            return (f != null) ? f : resp.getFormWithName(formName);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    private WebForm getFormWithButton(String buttonName) {
        if (hasForm()) {
            for (int i = 0; i < getForms().length; i++) {
                WebForm webForm = getForms()[i];
                if (webForm.getSubmitButton(buttonName) != null)
                    return webForm;
            }
        }
        return null;
    }

    private WebForm getFormWithParameter(String paramName) {
        if (hasForm()) {
            for (int i = 0; i < getForms().length; i++) {
                WebForm webForm = getForms()[i];
                String[] names = webForm.getParameterNames();
                for (int j = 0; j < names.length; j++) {
                    if (names[j].equals(paramName)) return webForm;
                }

            }
        }
        return null;
    }


    private WebForm[] getForms() {
        try {
            return resp.getForms();
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    private void checkFormStateWithParameter(String paramName) {
        if (form == null) {
            setWorkingForm(getFormWithParameter(paramName));
        }
    }

    private void checkFormStateWithButton(String buttonName) {
        if (form == null) {
            setWorkingForm(getFormWithButton(buttonName));
        }
    }

    public void setWorkingForm(String nameOrId) {
        setWorkingForm(getForm(nameOrId));
    }

    private void setWorkingForm(WebForm newForm) {
        if (newForm == null)
            throw new UnableToSetFormException("Attempted to set form to null.");
        form = newForm;
    }

    public boolean hasForm() {
        try {
            return resp.getForms().length > 0;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean hasForm(String formName) {
        return getForm(formName) != null;
    }

    /**
     * Returns true if any form has this element
     * @param paramName
     * @return
     */
    public boolean hasFormParameterNamed(String paramName) {
        checkFormStateWithParameter(paramName);
        return getForm().hasParameterNamed(paramName);
    }

    public void setFormParameter(String paramName, String paramValue) {
        checkFormStateWithParameter(paramName);
        getForm().setParameter(paramName, paramValue);
    }

    public String getFormParameterValue(String paramName) {
        checkFormStateWithParameter(paramName);
        return getForm().getParameterValue(paramName);
    }

    public void removeFormParameter(String paramName) {
        checkFormStateWithParameter(paramName);
        getForm().removeParameter(paramName);
    }

    public SubmitButton getSubmitButton(String buttonName) {
        checkFormStateWithButton(buttonName);
        return getForm().getSubmitButton(buttonName);
    }

    public boolean isTextInResponse(String text) {
        try {
            return (context.toEncodedString(resp.getText()).indexOf(text) >= 0);
        } catch (IOException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    public boolean isTextInTable(String tableSummary, String text) {
        WebTable table = getWebTableBySummary(tableSummary);
        if (table == null) {
            throw new RuntimeException(
                    "No table with summary [" + tableSummary + "] found in response.");
        }
        for (int row = 0; row < table.getRowCount(); row++) {
            for (int col = 0; col < table.getColumnCount(); col++) {
                TableCell cell = table.getTableCell(row, col);
                if (cell != null) {
                    String cellHtml = getNodeHtml(cell.getDOM());
                    if (cellHtml.indexOf(text) != -1)
                        return true;
                }
            }
        }
        return false;
    }

    public WebTable getWebTableBySummary(String tableSummary) {
        try {
            return resp.getTableWithSummary(tableSummary);
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getNodeHtml(Node node) {
        String nodeHtml = "";
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                nodeHtml += "<" + child.getNodeName() + ">";
            }
            if (child.hasChildNodes()) {
                nodeHtml += getNodeHtml(child);
            } else {
                nodeHtml += child.getNodeValue();
            }
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                nodeHtml += "</" + child.getNodeName() + ">";
            }
        }
        return context.toEncodedString(nodeHtml);
    }

    public void submit() {
        WebRequest formRequest = getForm().getRequest((SubmitButton) null);
        submitRequest(formRequest);
    }

    public void submit(String buttonName) {
        submitRequest(getForm().getRequest(buttonName));
    }

    private void submitRequest(WebRequest aWebRequest) {
        try {
            resp = wc.getResponse(aWebRequest);
        } catch (Exception e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    public boolean isLinkInResponse(String linkText) {
        try {
            return (resp.getLinkWith(linkText) != null);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    public void clickLink(String linkText) {
        WebLink link = null;
        try {
            link = resp.getLinkWith(linkText);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        if (link == null)
            throw new RuntimeException("No Link found for \"" + linkText + "\"");
        submitRequest(link.getRequest());
    }

    public void clickLinkByID(String anID) {
        WebLink link = null;
        try {
            link = resp.getLinkWithID(anID);
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        if (link == null)
            throw new RuntimeException("No Link found with ID \"" + anID + "\"");
        submitRequest(link.getRequest());

    }


    public boolean hasRadioOption(String radioGroup, String radioOption) {
        WebForm[] forms = getForms();
        for (int i = 0; i < forms.length; i++) {
            WebForm form = forms[i];
            String[] opts = form.getOptionValues(radioGroup);
            for (int j = 0; j < opts.length; j++) {
                String opt = opts[j];
                if (radioOption.equals(opt)) return true;
            }
        }
        return false;
    }

    public boolean isLinkPresentById(String anId) {
        try {
            return resp.getLinkWithID(anId) != null;
        } catch (SAXException e) {
            throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
    }

    public WebConversation getWebConversation() {
        return wc;
    }

    public String[] getOptionsFor(String selectName) {
        return getForm().getOptions(selectName);
    }
}