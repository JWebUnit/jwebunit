package net.sourceforge.jwebunit.fit;

import fit.ActionFixture;
import net.sourceforge.jwebunit.WebTester;

public class WebFixture extends ActionFixture {
    WebTester tester = new WebTester();

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
        System.out.println("submit()");
        tester.submit();
    }

}
