package net.sourceforge.jwebunit.fit;

import fit.ActionFixture;
import fit.Parse;
import net.sourceforge.jwebunit.WebTester;
import net.sourceforge.jwebunit.util.reflect.MethodInvoker;
import junit.framework.AssertionFailedError;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.InvocationTargetException;

public class WebFixture extends ActionFixture {
    protected static WebTester tester = new WebTester();

    public WebFixture() {
        actor = this;
    }

    public String getBaseUrl() {
        return tester.getTestContext().getBaseUrl();
    }

    public void setBaseUrl(String url) {
        tester.getTestContext().setBaseUrl(url);
    }

    // Actions
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

    public void enter() throws Exception {
        tester.setFormElement(cells.more.text(), cells.more.more.text());
    }

    public void select() {
        if (cells.more.text().equals("checkbox"))
            tester.checkCheckbox(cells.more.more.text());
        else if (cells.more.text().equals("option"))
            tester.selectOption(cells.more.more.text(), cells.more.more.more.text());
        else
            exception(cells.more, new RuntimeException("Unsupported select target"));
    }

    public void deselect() {
        if (cells.more.text().equals("checkbox"))
            tester.uncheckCheckbox(cells.more.more.text());
        else
            exception(cells.more, new RuntimeException("Unsupported deselection target"));
    }

    public void submit() {
        if(isNotEmpty(cells.more.more))
            tester.submit(cells.more.more.text());
        else
            tester.submit();
    }

    public void link() {
        try {
            tester.clickLinkWithText(cells.more.more.text());
        } catch (Throwable t) {
            if (t instanceof AssertionFailedError) {
                markLastArgumentWrong(cells.more.more, 1, t.getMessage());
            }
            else {
                exception(cells.last(), t);
            }
        }
    }

    public void linkId() {
        tester.clickLink(cells.more.more.text());
    }

    public void check() throws Exception {
        String methName = camel( "assert " + AssertionMap.getAssertionName(cells.more.text()));
        String args[] = getArgs(cells.more.more);
        try {
            getInvoker(methName, args).invoke();
            markLastArgumentRight(cells.more.more, args.length);
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if(t instanceof AssertionFailedError ) {
                markLastArgumentWrong(cells.more.more, args.length, t.getMessage());
            } else {
                exception(cells.last(), t);
            }
        } catch(NoSuchMethodException noMeth) {
            exception(cells.more, noMeth);
        } catch (Exception e) {
            exception(cells.last(), e);
        }
    }

     private MethodInvoker getInvoker(String methName, String[] args) throws NoSuchMethodException {
        MethodInvoker invoker;
        try {
            invoker = new MethodInvoker(tester, methName, args);
            invoker.getMethod();
        } catch (NoSuchMethodException e) {
            //if the method is not on the tester,
            //check this instance (could be defined in a subclass
            invoker = new MethodInvoker(this, methName, args);
            invoker.getMethod();
        }
        return invoker;
    }

    private void markLastArgumentRight(Parse more, int length) {
        right(more.at(length - 1));
    }

    private void markLastArgumentWrong(Parse more, int length, String message) {
        wrong(more.at(length - 1));
        more.at(length - 1).addToBody(label("expected") + "<hr>" + escape(message));
    }


    private String[] getArgs(Parse cell) {
        List args = new ArrayList();
        while((cell != null) && (!cell.text().equals(""))) {
            args.add(cell.text());
            cell = cell.more;
        }
        return (String[])args.toArray(new String[0]);
    }

    private boolean isNotEmpty(Parse cell) {
        return cell != null && !cell.text().equals("");
    }
    protected void dumpResponse() {
        System.err.println("***************begin page***********************");
        tester.dumpResponse(System.err);
        System.err.println("***************end page*************************");
    }

}
