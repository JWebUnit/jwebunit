package net.sourceforge.jwebunit.plugin.jacobie;

import java.util.ListIterator;

import net.sf.jacobie.exception.JacobieException;
import net.sf.jacobie.ie.A;
import net.sf.jacobie.ie.DefaultHTMLElement;
import net.sf.jacobie.ie.Document;
import net.sf.jacobie.ie.Form;
import net.sf.jacobie.ie.IE;
import net.sf.jacobie.ie.Select;
import net.sourceforge.jwebunit.CompositeJWebUnitDialog;
import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.TestContext;
import net.sourceforge.jwebunit.util.ExceptionUtility;

/**
 * Acts as the wrapper for Jacobie API access. 
 * 
 * A dialog is initialized with a given URL, and maintains conversational
 * state as the dialog progresses through link navigation, form submission, etc. Public access is provided for
 * wrappered HttpUnit objects.
 * 
 * @author Nick Neuberger
 */
public class JacobieDialog extends CompositeJWebUnitDialog {
    public IE ie = null;
    
    /**
     * This allows a developer to ignore the wait after clicking on the link.
     * The developer will be responsible to call the aIE.waitWhileBusy();
     */
    private boolean clickWaitOverride = false;
    
    /**
     * This allows a developer to enable focus and blur to be enabled when call setFormParameter.
     * Some controls on web pages have javascript in the focus / blur methods.  Such as date controls.
     * 
     * This will simulate the control recieving focus and calling the blur command after the setValue.
     */
    private boolean htmlElementFocusBlurEnabled = false;

    /**
     * 
     */
    public JacobieDialog() {
        super();
    }
    
    public JacobieDialog(String url, TestContext context) {
        //no notion yet of a test context.
        initWebClient();
        try {
            naviateToPage(url);            
        }
        catch (Exception anException) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(anException));
        }
        
    }
    
    public IJWebUnitDialog constructNewDialog(String url, TestContext context) {
        return new JacobieDialog(url, context);
    }
    
    public void initWebClient() {
        getIe();
    }
    
    /**
     * Navigate to a page.
     */
    public void naviateToPage(String aURL) throws JacobieException {
        IE theIE = getIe();
        theIE.Navigate(aURL);
        theIE.waitWhileBusy();        
    }

    /**
     * Sets the IE instance for the test case.
     * @param ie
     */
    public void setIe(IE ie) {
        this.ie = ie;
    }

    /**
     * Gets the IE instance for the test case.
     * @return
     */
    public IE getIe() {
        if(ie == null) {
            IE theIE = new IE();
            theIE.setVisible(true);
            setIe(theIE);
        }
        return ie;
    }
    
    
    /**
     * Resets all private variables contained by this class.
     */
    public void reset() {
        super.reset();
    	resetIE();
    }

    /**
     * Resets the IE object.  Must kill the object (quit) before calling a new one.
     */
    public void resetIE() {
    	IE theIE = getIe();
    	theIE.Quit();
    	//ComThread.Release(); //this may have been causing problems between each "test" method ran.
    	setIe(null);		
    }
    

    public String getResponsePageTitle() {
        String theString = null;
        try {
            theString = getIe().getDocument().getTitle().trim();
        }
        catch (Exception e) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
        }
        return theString;
    }
    
    /**
     * Gets the first form object from the forms collection.
     * @return
     */
    public Form getForm() {
        Form theForm = null;
        if(hasForm()) {
            theForm = (Form) getIe().getDocument().getForms().firstElement();
        }
        return theForm;
    }
    
    public boolean hasForm() {
        boolean bReturn = false;
        if(getIe().getDocument().hasForms()) {
            bReturn = true;
        }
        return bReturn;
    }

    public boolean hasFormParameterNamed(String paramName) {
        boolean bReturn = false;
        Document theDocument = getIe().getDocument();        
        DefaultHTMLElement theDefaultHTMLElement = theDocument.getElementById(paramName);
        if(theDefaultHTMLElement != null) {
            bReturn = true;
        }
        return bReturn;
    }
    
	/**
	 * Return the current value of a form input element.  Finds the first with this name.
	 * @param paramName
	 *            name of the input element.
	 */
	public String getFormParameterValue(String paramName) {
	    String theString = null;
	    Form theForm = getForm();        
	    if(theForm != null) {
	        DefaultHTMLElement theDefaultHTMLElement = theForm.findElementByName(paramName);
	        if(theDefaultHTMLElement != null) {
	            theString = theDefaultHTMLElement.getValue();
	        }
	    }
	    
	    if(theString == null) {
	        throw new RuntimeException("Element Not Found witht the following name: [" + paramName + "]");
	    }
	    
		return theString;
	}
    

    public void setFormParameter(String paramName, String paramValue) {
        Document theDocument = getIe().getDocument();        
        DefaultHTMLElement theDefaultHTMLElement = theDocument.getElementById(paramName);
        
        if(isHtmlElementFocusBlurEnabled()) {
            theDefaultHTMLElement.focus();
        }
        theDefaultHTMLElement.setValue(paramValue);
        
        if(isHtmlElementFocusBlurEnabled()) {
            theDefaultHTMLElement.blur();
        }
    }

	/**
	 * Return true if a radio group contains the indicated option.
	 * 
	 * @param radioGroup
	 *            name of the radio group.
	 * @param radioOption
	 *            value of the option to check for.
	 */    
    public boolean hasRadioOption(String radioGroup, String radioOption) {
        boolean bReturn = false;
        
        Document theDocument = getIe().getDocument();
        if(theDocument != null) {
            DefaultHTMLElement theDefaultHTMLElement = theDocument.findElementsByName(radioGroup, radioOption);
            if (theDefaultHTMLElement != null) {
                bReturn = true;
            }
            
        }
        return bReturn;
    }
    
       
	/**
	 * Submit the current form with the default submit button. See {@link #getForm}for an explanation of how the
	 * current form is established.
	 */
	public void submit() {
		try {
			getForm().submit();
			getIe().waitWhileBusy();
		} catch (Exception e) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
		}
	}
	
    public boolean isTextInResponse(String text) {
        boolean bReturn = false;
        
        Document theDocument = getIe().getDocument();
        if(theDocument != null) {
            bReturn = theDocument.isTextInDocument(text);
        }
        return bReturn;
    }

    public boolean isLinkPresent(String anId) {
        boolean bReturn = false;
        Document theDocument = getIe().getDocument();
        if(theDocument != null && theDocument.findLinkByID(anId) != null) {
            bReturn = true;
        }
        return bReturn;
    }
    
    
    public boolean isLinkPresentWithText(String linkText) {
        boolean bReturn = false;
        Document theDocument = getIe().getDocument();
        if(theDocument != null && theDocument.hasLinks()) {
            A theA = theDocument.findLink(linkText);
            if(theA != null) {
                bReturn = true;
            }
        }

        return bReturn;
    }
    
    public boolean isLinkPresentWithText(String linkText, int index) {
        int iZeroToOneIndex = index + 1; //converting to jacobie 1 based index.
        
        boolean bReturn = false;
        Document theDocument = getIe().getDocument();
        if(theDocument != null && theDocument.hasLinks()) {
            A theA = theDocument.findLink(linkText, iZeroToOneIndex);
            if(theA != null) {
                bReturn = true;
            }
        }
        return bReturn;
    }
    
    
    public void clickLink(String anID) {
        try {
	        IE theIE = getIe();
	        Document theDocument = getIe().getDocument();
	        if(theDocument != null && theDocument.hasLinks()) {
	            A theA = theDocument.findLinkByID(anID);
                clickLink(theIE, theA);
            }
        }   
        catch (JacobieException anException) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(anException));
        }
    }
    
    
    public void clickLinkWithText(String linkText) {
        try {           
	        IE theIE = getIe();
	        Document theDocument = theIE.getDocument();
	        if(theDocument != null && theDocument.hasLinks()) {
	            A theA = theDocument.findLink(linkText);
	            clickLink(theIE, theA);
	        }
        }        
        catch (JacobieException anException) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(anException));
        }

    
    }
    
    public void clickLinkWithText(String linkText, int index) {
        int iZeroToOneIndex = index + 1; //converting to jacobie 1 based index.
        try {           
	        IE theIE = getIe();
	        Document theDocument = theIE.getDocument();
	        if(theDocument != null && theDocument.hasLinks()) {
	            A theA = theDocument.findLink(linkText, iZeroToOneIndex);
	            clickLink(theIE, theA);
	        }
        }        
        catch (JacobieException anException) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(anException));
        }

    
    }
    
    protected void clickLink(IE aIE, A aA) throws JacobieException {
        if(aA != null) {
        	aA.click();
        	if(isClickWaitOverride()) {
        	    //resetting back to false.  developer should call this to not call this so the click doesn't wait.
        	    setClickWaitOverride(false);
        	}
        	else {
        	    aIE.waitWhileBusy();
        	}
        	
        }    	
    }
    
    /**
     * Clicks a radio option.  Asserts that the radio option exists first.	 
     * 
     * * @param radioGroup
	 *			name of the radio group.
	 * @param radioOption
	 * 			value of the option to check for.
     */    
    public void clickRadioOption(String radioGroup, String radioOption) {
        
        IE theIE = getIe();
        Document theDocument = theIE.getDocument();
        if(theDocument != null) {
            DefaultHTMLElement theDefaultHTMLElement = theDocument.findElementsByName(radioGroup, radioOption);
            if (theDefaultHTMLElement != null) {
                theDefaultHTMLElement.click();
            }
        }
    }

    /**
     * Developer should call prior to a click method if the developer doesn't want the "default"
     * wait mechanism.
     * 
     * Note the internal boolean will be reset immediately after the click is invoked.  No need for developer
     * to reset after the clickLinkWith....  method.
     * 
     * Used for clicking "OK" "Cancel" dialogs when clicking on a link.  Or the "chicken / dumb test".
     * @param clickWaitOverride
     */
    public void setClickWaitOverride(boolean clickWaitOverride) {
        this.clickWaitOverride = clickWaitOverride;
    }

    public boolean isClickWaitOverride() {
        return clickWaitOverride;
    }

	/**
	 * Select an option of a select box by display label.
	 * 
	 * @param selectName
	 *            name of the select box.
	 * @param option
	 *            label of the option to select.
	 */
	public void selectOption(String selectName, String option) {
		setFormParameter(selectName, getValueForOption(selectName, option));
	}

	/**
	 * Get the value for a given option of a select box.
	 * 
	 * @param selectName
	 *            name of the select box.
	 * @param option
	 *            label of the option.
	 */
    public String getValueForOption(String selectName, String aText) {
        String theValue = null;
        Document theDocument = getIe().getDocument();
        if(theDocument != null) {
            if(theDocument.hasForms()) {
                ListIterator theFormsIterator = theDocument.getForms().listIterator();
                while (theFormsIterator.hasNext()) {
                    Form theForm = (Form) theFormsIterator.next();
                    Select theSelect = theForm.getSelect(selectName);
                    if(theSelect != null) {
                        if(theSelect.hasOptionBasedOnText(aText)) {
                            theValue = theSelect.getOptionBasedOnText(aText).getValue();
                        }
                    }
                }
            }
        }
        if(theValue == null) {
    		throw new RuntimeException("Unable to find option " + aText + " for " + selectName);            
        }
        
        return theValue;
    }

    /**
     * Call this to turn on focus / blur for when setting a value on a form. 
     * 
     * This allows a developer to enable focus and blur to be enabled when call setFormParameter.
     * Some controls on web pages have javascript in the focus / blur methods.  Such as date controls.
     * 
     * NOTE: THIS works like a windows application.  If the IE window doesn't have window focus, it seems
     * as if this doesn't work quite right.
     * 
     * This will simulate the control recieving focus and calling the blur command after the setValue.

     * @param htmlElementFocusBlurEnabled
     */
    public void setHtmlElementFocusBlurEnabled(boolean htmlElementFocusBlurEnabled) {
        this.htmlElementFocusBlurEnabled = htmlElementFocusBlurEnabled;
    }

    public boolean isHtmlElementFocusBlurEnabled() {
        return htmlElementFocusBlurEnabled;
    }
	
    
    

}
