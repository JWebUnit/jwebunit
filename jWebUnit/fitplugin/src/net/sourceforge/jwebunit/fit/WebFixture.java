package net.sourceforge.jwebunit.fit;

import fit.ActionFixture;
import fit.Parse;
import net.sourceforge.jwebunit.WebTester;
import net.sourceforge.jwebunit.util.reflect.MethodInvoker;
import junit.framework.AssertionFailedError;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.reflect.InvocationTargetException;

public class WebFixture extends ActionFixture {
    static WebTester tester = new WebTester();

    public WebFixture() {
        actor = this;
    }

    public void start() throws Exception {
        // do nothing
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

    public void begin() {
        tester.beginAt(cells.more.text());
    }

    public String title() {
        return tester.getDialog().getResponsePageTitle();
    }

    public void enter() throws Exception {
        tester.setFormElement(this.cells.more.text(), this.cells.more.more.text());
    }

    public void submit() {
        if(cells.more.more != null)
            tester.submit(cells.more.more.text());
        else
            tester.submit();
    }

    public void link() {
        tester.clickLinkWithText(cells.more.more.text());
    }

    public void check() throws Exception {
        String methName = camel( "assert " + cells.more.text());
        String args[] = getArgs(cells.more.more);
        Parse arg = cells.more.more;
        MethodInvoker invoker = new MethodInvoker(tester, methName, args);
        try {
            invoker.invoke();
            right(cells.last());
        } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if(t instanceof AssertionFailedError ) {
                Parse field = cells.last();
                wrong(field);
                field.addToBody(label("expected") + "<hr>" + escape(t.getMessage()));
            } else {
                exception(arg, t);
            }
        } catch(NoSuchMethodException noMeth) {
            exception(cells.more, noMeth);
        } catch (Exception e) {
            exception(arg, e);
        }
    }

    private String[] getArgs(Parse cell) {
        List args = new ArrayList();
        while(cell != null) {
            args.add(cell.text());
            cell = cell.more;
        }
        return (String[])args.toArray(new String[0]);
    }


}
