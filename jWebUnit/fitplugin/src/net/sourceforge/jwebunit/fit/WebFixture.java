package net.sourceforge.jwebunit.fit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.meterware.httpunit.cookies.CookieListener;
import com.meterware.httpunit.cookies.CookieProperties;

import junit.framework.AssertionFailedError;
import net.sourceforge.jwebunit.WebTester;
import net.sourceforge.jwebunit.util.reflect.MethodInvoker;
import fit.ActionFixture;
import fit.Parse;

public class WebFixture extends ActionFixture {
    protected static WebTester tester = new WebTester();

    public WebFixture() {
        actor = this;
    }

    public void doCells(Parse cells) {
        this.cells = cells;
        try {
            Method action = getClass().getMethod(camel(cells.text()), empty);
            action.invoke(this, empty);
        } catch (Exception e) {
            exception(cells, e);
        }
    }

    public void ignore() {
        ignore(cells);
        Parse cell = cells.more;
        while (cell != null) {
            cell.addToTag(" bgcolor=\"#efefef\"");
            cell = cell.more;
        }
    }

    public void ignore(Parse cell) {
        cell.addToTag(" bgcolor=\"#efefef\"");
        counts.ignores++;
    }


    public String getBaseUrl() {
        return tester.getTestContext().getBaseUrl();
    }

    public void setBaseUrl(String url) {
        tester.getTestContext().setBaseUrl(url);
    }

    public void setExceptionsThrownOnScriptError(boolean flag) {
        com.meterware.httpunit.HttpUnitOptions.setExceptionsThrownOnScriptError(flag);
    }

    public void setLoggingHttpHeaders(boolean flag) {
        com.meterware.httpunit.HttpUnitOptions.setLoggingHttpHeaders(flag);
        if (flag) {
        	CookieProperties.addCookieListener(new CookieListener() {
				public void cookieRejected(String arg0, int arg1, String arg2) {
					System.out.println("cookieRejected: " + arg0 + " " + arg1 + " " + arg2);
				}
        	});
        }
    }

    public void setCookieMatchingStrict(boolean flag) {
    	CookieProperties.setDomainMatchingStrict(false);
    	CookieProperties.setPathMatchingStrict(false);
    }

    // Actions
    public void exceptionsThrownOnScriptError() {
        setExceptionsThrownOnScriptError("true".equalsIgnoreCase(cells.more.text()));
    }

    public void loggingHttpHeaders() {
    	setLoggingHttpHeaders("true".equalsIgnoreCase(cells.more.text()));
    }

    public void cookieMatchingStrict() {
    	setCookieMatchingStrict("true".equalsIgnoreCase(cells.more.text()));
    }

    public void baseUrl() {
        setBaseUrl(cells.more.text());
    }

    public void bundle() {
        tester.getTestContext().setResourceBundleName(cells.more.text());
    }

    public void begin() {
        tester.beginAt(cells.more.text());
    }

    public void form() {
        tester.setWorkingForm(cells.more.text());
    }

    public void frame() {
        tester.gotoFrame(cells.more.text());
    }

    public void window() {
        String windowIdOrTitle = cells.more.text();
        if (tester.getDialog().getWindow(windowIdOrTitle) != null) {
            tester.gotoWindow(cells.more.text());            
        } else {
            tester.gotoWindowByTitle(cells.more.text());            
        }
    }

    public void baseWindow() {
        tester.gotoRootWindow();
    }
    
    public void page() {
        tester.gotoPage(cells.more.text());
    }

    public void enter() throws Exception {
        tester.setFormElement(cells.more.text(), cells.more.more.text());
    }

    public void select() {
        if (cells.more.text().equals("checkbox")) {
            Parse fieldNameCell = cells.more.more;
            if(fieldNameCell.more == null)
                tester.checkCheckbox(fieldNameCell.text());
            else
                tester.checkCheckbox(fieldNameCell.text(), fieldNameCell.more.text());
        } else if (cells.more.text().equals("option")) {
            tester.selectOption(
                    cells.more.more.text(),
                    cells.more.more.more.text());
        } else {
            exception(
                    cells.more,
                    new RuntimeException("Unsupported select target"));
        }
    }

    public void deselect() {
        if (cells.more.text().equals("checkbox"))
            tester.uncheckCheckbox(cells.more.more.text());
        else
            exception(
                    cells.more,
                    new RuntimeException("Unsupported deselection target"));
    }

    public void submit() {
        if (isNotEmpty(cells.more.more))
            tester.submit(cells.more.more.text());
        else
            tester.submit();
    }

    public void button() {
        try {
            tester.clickButton(cells.more.more.text());
        } catch (Throwable t) {
            if (t instanceof AssertionFailedError) {
                markLastArgumentWrong(cells.more.more, 1, t.getMessage());
            } else {
                exception(cells.last(), t);
            }
        }
    }

    public void link() {
        try {
            tester.clickLinkWithText(cells.more.more.text());
        } catch (Throwable t) {
            if (t instanceof AssertionFailedError) {
                markLastArgumentWrong(cells.more.more, 1, t.getMessage());
            } else {
                exception(cells.last(), t);
            }
        }
    }

    public void linkWithImage() {
        try {
            tester.clickLinkWithImage(cells.more.more.text());
        } catch (Throwable t) {
            if (t instanceof AssertionFailedError) {
                markLastArgumentWrong(cells.more.more, 1, t.getMessage());
            } else {
                exception(cells.last(), t);
            }
        }
    }

    public void linkId() {
        tester.clickLink(cells.more.more.text());
    }

    public void check() throws Exception {
        String args[] = getArgs(cells.more.more);
        try {
            getInvoker(args).invoke();
            if (isNotEmpty(cells.more.more))
                markLastArgumentRight(cells.more.more, args.length);
            else
                right(cells.more); // for no arg checks
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (t instanceof AssertionFailedError) {
                if (isNotEmpty(cells.more.more))
                    markLastArgumentWrong(
                            cells.more.more,
                            args.length,
                            t.getMessage());
                else
                    wrong(cells.more, t.getMessage()); // for no arg checks
            } else {
                exception(cells.last(), t);
            }
        } catch (NoSuchMethodException noMeth) {
            exception(cells.more, noMeth);
        } catch (Exception e) {
            exception(cells.last(), e);
        }
    }

    private MethodInvoker getInvoker(String[] args)
            throws NoSuchMethodException {
        String baseName = AssertionMap.getAssertionName(cells.more.text());
        try {
            return getInvoker(this, camel("check " + baseName), args);
        } catch (NoSuchMethodException e) {
            return getInvoker(tester, camel("assert " + baseName), args);
        }
    }

    private MethodInvoker getInvoker(
            Object target,
            String methName,
            String[] args)
            throws NoSuchMethodException {
        MethodInvoker invoker;
        invoker = new MethodInvoker(target, methName, args);
        invoker.getMethod();
        return invoker;
    }

    private void markLastArgumentRight(Parse more, int length) {
        right(more.at(length - 1));
    }

    private void markLastArgumentWrong(
            Parse more,
            int length,
            String message) {
        if (message == null)
            message = "No message!";
        wrong(more.at(length - 1));
        more.at(length - 1).addToBody(
                label("expected") + "<hr>" + escape(message));
    }

    private String[] getArgs(Parse cell) {
        List args = new ArrayList();
        while ((cell != null) && (!cell.text().equals(""))) {
            args.add(cell.text());
            cell = cell.more;
        }
        return (String[]) args.toArray(new String[0]);
    }

    protected boolean isNotEmpty(Parse cell) {
        return cell != null && !cell.text().equals("");
    }

    public void dumpResponse() {
        System.err.println("***************begin page***********************");
        tester.dumpResponse(System.err);
        System.err.println("***************end page*************************");
    }

    // Checks
    public void checkLink(String text) {
        tester.assertLinkPresentWithText(text);
    }

    public void checkLinkId(String anID) {
        tester.assertLinkPresent(anID);
    }
}
