/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit.jacobie;

import java.io.PrintStream;
import java.util.ListIterator;

import net.sf.jacobie.exception.JacobieException;
import net.sf.jacobie.ie.A;
import net.sf.jacobie.ie.DefaultHTMLElement;
import net.sf.jacobie.ie.Document;
import net.sf.jacobie.ie.Form;
import net.sf.jacobie.ie.IE;
import net.sf.jacobie.ie.Input;
import net.sf.jacobie.ie.Select;
import net.sourceforge.jwebunit.IJWebUnitDialog;
import net.sourceforge.jwebunit.TestContext;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
import net.sourceforge.jwebunit.html.Table;
import net.sourceforge.jwebunit.util.ExceptionUtility;

/**
 * Acts as the wrapper for Jacobie API access.
 * 
 * A dialog is initialized with a given URL, and maintains conversational state
 * as the dialog progresses through link navigation, form submission, etc.
 * Public access is provided for wrappered HttpUnit objects.
 * 
 * @author Nick Neuberger
 */
public class JacobieDialog implements IJWebUnitDialog {

	public IE ie = null;

	/**
	 * This allows a developer to ignore the wait after clicking on the link.
	 * The developer will be responsible to call the aIE.waitWhileBusy();
	 */
	private boolean clickWaitOverride = false;

	/**
	 * This allows a developer to enable focus and blur to be enabled when call
	 * setFormParameter. Some controls on web pages have javascript in the focus /
	 * blur methods. Such as date controls.
	 * 
	 * This will simulate the control recieving focus and calling the blur
	 * command after the setValue.
	 */
	private boolean htmlElementFocusBlurEnabled = false;

	/**
	 *  
	 */
	public JacobieDialog() {
		super();
	}

	public void beginAt(String url, TestContext context) throws TestingEngineResponseException {
		//no notion yet of a test context.
		initWebClient();
		try {
			naviateToPage(url);
		} catch (Exception aException) {			
			//cant find requested page.  most browsers will return a page with 404 in the body or title.
            throw new TestingEngineResponseException(ExceptionUtility.stackTraceToString(aException));
		}

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
	 * 
	 * @param ie
	 */
	public void setIe(IE ie) {
		this.ie = ie;
	}

	/**
	 * Gets the IE instance for the test case.
	 * 
	 * @return
	 */
	public IE getIe() {
		if (ie == null) {
			IE theIE = new IE();
			theIE.setVisible(true);
			setIe(theIE);
		}
		return ie;
	}

	/**
	 * Resets all private variables contained by this class.
	 */
	public void resetDialog() throws TestingEngineResponseException {
		resetIE();
	}

	/**
	 * Reset the current form. See {@link #getForm}for an explanation of how
	 * the current form is established.
	 */
	public void reset() {
		throw new UnsupportedOperationException("reset");
	}

	/**
	 * Resets the IE object. Must kill the object (quit) before calling a new
	 * one.
	 */
	public void resetIE() throws TestingEngineResponseException {
		IE theIE = getIe();
		try {
			theIE.Quit();
		} catch (InterruptedException aInterruptedException) {
			throw new TestingEngineResponseException(ExceptionUtility
					.stackTraceToString(aInterruptedException));
		} finally {
			setIe(null);
		}

	}

	public String getPageTitle() {
		String theString = null;
		try {
			theString = getIe().getDocument().getTitle().trim();
		} catch (Exception e) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
		}
		return theString;
	}

	/**
	 * Gets the first form object from the forms collection.
	 * 
	 * @return
	 */
	public Form getForm() {
		Form theForm = null;
		if (hasForm()) {
			theForm = (Form) getIe().getDocument().getForms().firstElement();
		}
		return theForm;
	}

	public boolean hasForm() {
		boolean bReturn = false;
		if (getIe().getDocument().hasForms()) {
			bReturn = true;
		}
		return bReturn;
	}

	private Form getForm(String aNameOrID) {
		Form theForm = getIe().getDocument().findFormByNameOrID(aNameOrID);
		return theForm;
	}

	/**
	 * Return true if the current response contains a specific form.
	 * 
	 * @param nameOrID
	 *            name of id of the form to check for.
	 */
	public boolean hasForm(String nameOrID) {
		return getForm(nameOrID) != null;
	}

	public boolean hasFormParameterNamed(String paramName) {
		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		DefaultHTMLElement theDefaultHTMLElement = theDocument
				.getElementById(paramName);
		if (theDefaultHTMLElement != null) {
			bReturn = true;
		}
		return bReturn;
	}

	/**
	 * Return the current value of a form input element. Finds the first with
	 * this name.
	 * 
	 * @param paramName
	 *            name of the input element.
	 */
	public String getFormParameterValue(String paramName) {
		String theString = null;
		Form theForm = getForm();
		if (theForm != null) {
			DefaultHTMLElement theDefaultHTMLElement = theForm
					.findElementByName(paramName);
			if (theDefaultHTMLElement != null) {
				theString = theDefaultHTMLElement.getValue();
			}
		}

		if (theString == null) {
			throw new RuntimeException(
					"Element Not Found with the following name: [" + paramName
							+ "]");
		}

		return theString;
	}

	public void setFormParameter(String paramName, String paramValue) {
		Document theDocument = getIe().getDocument();
		DefaultHTMLElement theDefaultHTMLElement = theDocument
				.getElementById(paramName);

		if (isHtmlElementFocusBlurEnabled()) {
			theDefaultHTMLElement.focus();
		}
		theDefaultHTMLElement.setValue(paramValue);

		if (isHtmlElementFocusBlurEnabled()) {
			theDefaultHTMLElement.blur();
		}
	}
    
    public void setTextField(String paramName, String paramValue) {
        setFormParameter(paramName, paramValue);
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
		if (theDocument != null) {
			DefaultHTMLElement theDefaultHTMLElement = theDocument
					.findElementsByName(radioGroup, radioOption);
			if (theDefaultHTMLElement != null) {
				bReturn = true;
			}

		}
		return bReturn;
	}

	public Input getSubmitButton(String aButtonName) {
		Input theInput = getForm().findSubmitButton(aButtonName);
		return theInput;
	}

	public Input getButton(String aButtonID) {
		Input theInput = getForm().findButton(aButtonID);
		return theInput;
	}

	public Input getButtonByValue(String aButtonValue) {
		Input theInput = getForm().findButtonByValue(aButtonValue);
		return theInput;
	}


	public String getSubmitButtonValue(String aButtonName) {
		String theButtonValue = null;
		Input theInput = getForm().findSubmitButton(aButtonName);
		if (theInput != null) {
			theButtonValue = theInput.getValue();
		}
		return theButtonValue;
	}

	public boolean hasSubmitButton(String aButtonName, String aButtonValue) {
		boolean bReturn = false;
		Input theInput = getSubmitButton(aButtonName);
		if (theInput != null && theInput.getValue().equals(aButtonValue)) {
			bReturn = true;
		}
		return bReturn;
	}

	public boolean hasSubmitButton(String aButtonName) {
		boolean bReturn = getSubmitButton(aButtonName) != null ? true : false;
		return bReturn;
	}

	public boolean hasButton(String aButtonID) {
		boolean bReturn = getSubmitButton(aButtonID) != null ? true : false;
		return bReturn;
	}
	
	public boolean hasButtonByValue(String aButtonValue) {
		boolean bReturn = getButtonByValue(aButtonValue) != null ? true : false;
		return bReturn;
	}

	

	public void clickButton(String buttonId) {
	    if(hasButton(buttonId)) {
	        getSubmitButton(buttonId).click();
	    }
	}

	public void clickButtonWithText(String buttonValueText) {
	    if(hasButtonByValue(buttonValueText)) {
	        getButtonByValue(buttonValueText).click();
	    }
	}
	
	/**
	 * Patch sumbitted by Alex Chaffee.
	 */
	public void gotoPage(String url) throws TestingEngineResponseException {
		try {
			naviateToPage(url);
		} catch (JacobieException aJacobieException) {
			throw new TestingEngineResponseException(ExceptionUtility
					.stackTraceToString(aJacobieException));
		}
	}

	/**
	 * Submit the current form with the default submit button. See
	 * {@link #getForm}for an explanation of how the current form is
	 * established.
	 */
	public void submit() {
		try {
			getForm().submit();
			getIe().waitWhileBusy();
		} catch (Exception e) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#submit(java.lang.String)
	 */
	public void submit(String aButtonName) {
		try {
			if (hasSubmitButton(aButtonName)) {
				Input theInput = getSubmitButton(aButtonName);
				theInput.click();
				getIe().waitWhileBusy();
			}
		} catch (Exception e) {
			throw new RuntimeException(ExceptionUtility.stackTraceToString(e));
		}
	}

	/**
	 * Submit the current form with the specifed submit button (by name and
	 * value). See {@link #getForm}for an explanation of how the current form
	 * is established.
	 * 
	 * @author Dragos Manolescu
	 * @param buttonName
	 *            name of the button to use for submission.
	 * @param buttonValue
	 *            value/label of the button to use for submission
	 */
	public void submit(String buttonName, String buttonValue) {
		throw new UnsupportedOperationException(
				"submit(buttonName, buttonValue)");
	}

	public boolean isTextInResponse(String text) {
		boolean bReturn = false;

		Document theDocument = getIe().getDocument();
		if (theDocument != null) {
			bReturn = theDocument.isTextInDocument(text);
		}
		return bReturn;
	}

	public boolean isLinkPresent(String anId) {
		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		if (theDocument != null && theDocument.hasLinkByID(anId)) {
			bReturn = true;
		}
		return bReturn;
	}

	/**
	 * 
	 * Return true if a link is present with a given image based on filename of
	 * image.
	 * 
	 * <a href="/jwebunit/NavigationTest/targetPage2.html"> <img
	 * src="graphic.jpg"/> </a>
	 * 
	 * @param imageFileName
	 *            A suffix of the image's filename; for example, to match
	 *            <tt>"images/my_icon.png"<tt>, you could just pass in
	 *                      <tt>"my_icon.png"<tt>.
	 */
	public boolean isLinkPresentWithImage(String imageFileName) {
		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		if (theDocument != null
				&& theDocument.hasLinkByInnerHTML(imageFileName)) {
			bReturn = true;
		}
		return bReturn;
	}
	
    /**
     * Return true if a link is present in the current response containing the Exact specified text.
     * Note. This will call String.trim() to trim all leading / trailing spaces.
     * 
     * RFE 996031...
     * 
     * @param linkText
     *            text to check for in links on the response.
     */
    public boolean isLinkPresentWithExactText(String linkText) {
		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		if (theDocument != null && theDocument.hasLink(linkText)) {
			bReturn = true;
		}
		return bReturn;    	
    }

    /**
     * Return true if a link is present in the current response containing the Exact specified text.
     * Note. This will call String.trim() to trim all leading / trailing spaces.
     * 
     * RFE 996031...
     * 
     * @param linkText
     *            text to check for in links on the response.
     * @param index
     *            The 0-based index, when more than one link with the same text is expected.
     */
    public boolean isLinkPresentWithExactText(String linkText, int index) {
		int iZeroToOneIndex = index + 1; //converting to jacobie 1 based index.

		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		if (theDocument != null && theDocument.hasLinks()) {
			bReturn = theDocument.hasLink(linkText, iZeroToOneIndex);
		}
		return bReturn;    	
    }
    
	

	public boolean isLinkPresentWithText(String linkText) {
		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		if (theDocument != null && theDocument.hasLinkByInnerHTML(linkText)) {
			bReturn = true;
		}
		return bReturn;
	}

	public boolean isLinkPresentWithText(String linkText, int index) {
		int iZeroToOneIndex = index + 1; //converting to jacobie 1 based index.

		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		if (theDocument != null && theDocument.hasLinks()) {
			bReturn = theDocument.hasLink(linkText, iZeroToOneIndex);
		}
		return bReturn;
	}

	public boolean isCheckboxSelected(String checkBoxName) {
		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		DefaultHTMLElement theDefaultHTMLElement = theDocument
				.getElementById(checkBoxName);
		if (theDefaultHTMLElement != null) {
			if (theDefaultHTMLElement.isChecked()) {
				bReturn = true;
			}
		}
		return bReturn;
	}

	public boolean isCheckboxNotSelected(String checkBoxName) {
		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		DefaultHTMLElement theDefaultHTMLElement = theDocument
				.getElementById(checkBoxName);
		if (theDefaultHTMLElement != null) {
			if (!theDefaultHTMLElement.isChecked()) {
				bReturn = true;
			}
		}
		return bReturn;
	}

	/**
	 * Select a specified checkbox. If the checkbox is already checked then the
	 * checkbox will stay checked.
	 * 
	 * @param checkBoxName
	 *            name of checkbox to be deselected.
	 */
	public void checkCheckbox(String checkBoxName) {
		Document theDocument = getIe().getDocument();
		DefaultHTMLElement theDefaultHTMLElement = theDocument
				.getElementById(checkBoxName);
		if (theDefaultHTMLElement != null) {
			if (!theDefaultHTMLElement.isChecked()) {

				if (isHtmlElementFocusBlurEnabled()) {
					theDefaultHTMLElement.focus();
				}

				//theDefaultHTMLElement.setChecked(true);
				theDefaultHTMLElement.click();

				if (isHtmlElementFocusBlurEnabled()) {
					theDefaultHTMLElement.blur();
				}

			}
		}
	}

	public void checkCheckbox(String checkBoxName, String value) {
		Document theDocument = getIe().getDocument();
		DefaultHTMLElement theDefaultHTMLElement = theDocument
				.getElementById(checkBoxName);
		if (theDefaultHTMLElement != null) {
			if (!theDefaultHTMLElement.isChecked()) {
				theDefaultHTMLElement.setChecked(true);

				//this may not be the best way to set the incoming value but
				// for now this will work.
				theDefaultHTMLElement.setValue(value);
			} else {
				//this may not be the best way to set the incoming value but
				// for now this will work.
				theDefaultHTMLElement.setValue(value);
			}
		}
	}

	/**
	 * Deselect a specified checkbox. If the checkbox is already unchecked then
	 * the checkbox will stay unchecked.
	 * 
	 * @param checkBoxName
	 *            name of checkbox to be deselected.
	 */
	public void uncheckCheckbox(String checkBoxName) {
		Document theDocument = getIe().getDocument();
		DefaultHTMLElement theDefaultHTMLElement = theDocument
				.getElementById(checkBoxName);
		if (theDefaultHTMLElement != null) {
			if (theDefaultHTMLElement.isChecked()) {
				//theDefaultHTMLElement.setChecked(false);
				if (isHtmlElementFocusBlurEnabled()) {
					theDefaultHTMLElement.focus();
				}

				theDefaultHTMLElement.click();

				if (isHtmlElementFocusBlurEnabled()) {
					theDefaultHTMLElement.blur();
				}

			}
		}
	}

	public void uncheckCheckbox(String checkBoxName, String value) {
		Document theDocument = getIe().getDocument();
		DefaultHTMLElement theDefaultHTMLElement = theDocument
				.getElementById(checkBoxName);
		if (theDefaultHTMLElement != null) {
			if (theDefaultHTMLElement.isChecked()) {
				theDefaultHTMLElement.setChecked(false);

				//this may not be the best way to set the incoming value but
				// for now this will work.
				theDefaultHTMLElement.setValue(value);
			} else {
				//this may not be the best way to set the incoming value but
				// for now this will work.
				theDefaultHTMLElement.setValue(value);
			}
		}
	}

	public void clickLink(String anID) {
		try {
			IE theIE = getIe();
			Document theDocument = getIe().getDocument();
			if (theDocument != null && theDocument.hasLinks()) {
				A theA = theDocument.findLinkByID(anID);
				clickLink(theIE, theA);
			}
		} catch (JacobieException anException) {
			throw new RuntimeException(ExceptionUtility
					.stackTraceToString(anException));
		}
	}

	/**
	 * Navigate by submitting a request based on a link with a given image file
	 * name. A RuntimeException is thrown if no such link can be found.
	 * 
	 * @param imageFileName
	 *            A suffix of the image's filename; for example, to match
	 *            <tt>"images/my_icon.png"<tt>, you could just pass in
	 *                      <tt>"my_icon.png"<tt>.
	 */
	public void clickLinkWithImage(String imageFileName) {
		try {
			IE theIE = getIe();
			Document theDocument = theIE.getDocument();
			if (theDocument != null && theDocument.hasLinks()) {
				A theA = theDocument.findLinkByInnerHTML(imageFileName);
				clickLink(theIE, theA);
			}
		} catch (JacobieException anException) {
			throw new RuntimeException(ExceptionUtility
					.stackTraceToString(anException));
		}

	}

	/*
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithExactText(java.lang.String)
	 */
	public void clickLinkWithExactText(String linkText) {
		try {
			IE theIE = getIe();
			Document theDocument = theIE.getDocument();
			if (theDocument != null && theDocument.hasLinks()) {
				A theA = theDocument.findLink(linkText);
				clickLink(theIE, theA);
			}
		} catch (JacobieException anException) {
			throw new RuntimeException(ExceptionUtility
					.stackTraceToString(anException));
		}

	}

	/*
	 * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithExactText(java.lang.String,
	 *      int)
	 */
	public void clickLinkWithExactText(String linkText, int index) {
		int iZeroToOneIndex = index + 1; //converting to jacobie 1 based index.
		try {
			IE theIE = getIe();
			Document theDocument = theIE.getDocument();
			if (theDocument != null && theDocument.hasLinks()) {
				A theA = theDocument.findLink(linkText, iZeroToOneIndex);
				clickLink(theIE, theA);
			}
		} catch (JacobieException anException) {
			throw new RuntimeException(ExceptionUtility
					.stackTraceToString(anException));
		}

	}

	
	
	public void clickLinkWithText(String linkText) {
		try {
			IE theIE = getIe();
			Document theDocument = theIE.getDocument();
			if (theDocument != null && theDocument.hasLinks()) {
				A theA = theDocument.findLinkByInnerText(linkText);
				clickLink(theIE, theA);
			}
		} catch (JacobieException anException) {
			throw new RuntimeException(ExceptionUtility
					.stackTraceToString(anException));
		}

	}

	public void clickLinkWithText(String linkText, int index) {
		int iZeroToOneIndex = index + 1; //converting to jacobie 1 based index.
		try {
			IE theIE = getIe();
			Document theDocument = theIE.getDocument();
			if (theDocument != null && theDocument.hasLinks()) {
				A theA = theDocument.findLinkByInnerText(linkText, iZeroToOneIndex);
				clickLink(theIE, theA);
			}
		} catch (JacobieException anException) {
			throw new RuntimeException(ExceptionUtility
					.stackTraceToString(anException));
		}

	}

	protected void clickLink(IE aIE, A aA) throws JacobieException {
		if (aA != null) {
			aA.click();
			if (isClickWaitOverride()) {
				//resetting back to false. developer should call this to not
				// call this so the click doesn't wait.
				setClickWaitOverride(false);
			} else {
				aIE.waitWhileBusy();
			}

		}
	}

	public void clickLinkWithTextAfterText(String linkText, String labelText) {
		throw new UnsupportedOperationException(
				"clickLinkWithTextAfterText not supported yet by Jacobie");
	}

	/**
	 * Clicks a radio option. Asserts that the radio option exists first. *
	 * 
	 * @param radioGroup
	 *            name of the radio group.
	 * @param radioOption
	 *            value of the option to check for.
	 */
	public void clickRadioOption(String radioGroup, String radioOption) {

		IE theIE = getIe();
		Document theDocument = theIE.getDocument();
		if (theDocument != null) {
			DefaultHTMLElement theDefaultHTMLElement = theDocument
					.findElementsByName(radioGroup, radioOption);
			if (theDefaultHTMLElement != null) {
				theDefaultHTMLElement.click();
			}
		}
	}

	/**
	 * Developer should call prior to a click method if the developer doesn't
	 * want the "default" wait mechanism.
	 * 
	 * Note the internal boolean will be reset immediately after the click is
	 * invoked. No need for developer to reset after the clickLinkWith....
	 * method.
	 * 
	 * Used for clicking "OK" "Cancel" dialogs when clicking on a link. Or the
	 * "chicken / dumb test".
	 * 
	 * @param clickWaitOverride
	 */
	public void setClickWaitOverride(boolean clickWaitOverride) {
		this.clickWaitOverride = clickWaitOverride;
	}

	public boolean isClickWaitOverride() {
		return clickWaitOverride;
	}

	/**
	 * Return true if a select box has the given option (by label).
	 * 
	 * @param selectName
	 *            name of the select box.
	 * @param optionLabel
	 *            label of the option.
	 * @return
	 */
	public boolean hasSelectOption(String selectName, String optionLabel) {
		boolean bReturn = false;

		String theString = getValueForOption(selectName, optionLabel);
		if (theString != null) {
			//if the value is return then the option with the label must exist.
			bReturn = true;
		}
		return bReturn;
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
	 * Return the label of the currently selected item in a select box.
	 * 
	 * @param selectName
	 *            name of the select box.
	 */
	public String getSelectedOption(String selectName) {
		String theString = null;
		Select theSelect = getSelect(selectName);
		if (theSelect != null && theSelect.hasSelectedOption()) {
			theString = theSelect.getSelectedOption().getText();
		}
		return theString;
	}

	/**
	 * Gets the first select from the first form that has the select.
	 * 
	 * @param selectName
	 * @return
	 */
	public Select getSelect(String selectName) {
		Select theSelect = null;
		Document theDocument = getIe().getDocument();
		if (theDocument != null) {
			if (theDocument.hasForms()) {
				ListIterator theFormsIterator = theDocument.getForms()
						.listIterator();
				while (theFormsIterator.hasNext()) {
					Form theForm = (Form) theFormsIterator.next();
					Select theNextSelect = theForm.getSelect(selectName);
					if (theNextSelect != null) {
						theSelect = theNextSelect;
						break;
					}
				}
			}
		}
		return theSelect;
	}

	/**
	 * Get the value for a given option of a select box. <option value="1">one
	 * </option> Returns the "1" value.
	 * 
	 * @param selectName
	 *            name of the select box.
	 * @param option
	 *            label of the option.
	 */
	public String getValueForOption(String selectName, String aText) {
		String theValue = null;
		Select theSelect = getSelect(selectName);
		if (theSelect != null) {
			if (theSelect.hasOptionBasedOnText(aText)) {
				theValue = theSelect.getOptionBasedOnText(aText).getValue();
			}
		}
		return theValue;
	}

	/**
	 * Call this to turn on focus / blur for when setting a value on a form.
	 * 
	 * This allows a developer to enable focus and blur to be enabled when call
	 * setFormParameter. Some controls on web pages have javascript in the focus /
	 * blur methods. Such as date controls.
	 * 
	 * NOTE: THIS works like a windows application. If the IE window doesn't
	 * have window focus, it seems as if this doesn't work quite right.
	 * 
	 * This will simulate the control recieving focus and calling the blur
	 * command after the setValue.
	 * 
	 * @param htmlElementFocusBlurEnabled
	 */
	public void setHtmlElementFocusBlurEnabled(
			boolean htmlElementFocusBlurEnabled) {
		this.htmlElementFocusBlurEnabled = htmlElementFocusBlurEnabled;
	}

	public boolean isHtmlElementFocusBlurEnabled() {
		return htmlElementFocusBlurEnabled;
	}

    // The following methods have only been implemented in HttpUnitDialog
    
    public String getFormElementNameBeforeLabel(String formElementLabel) {
        throw new UnsupportedOperationException("Method JacobieDialog#getFormElementNameBeforeLabel not implemented yet.");
    }

    public String getFormElementValueBeforeLabel(String formElementLabel) {
        throw new UnsupportedOperationException("Method JacobieDialog#getFormElementValueBeforeLabel not implemented yet.");
    }

    public String getFormElementValueForLabel(String formElementLabel) {
        throw new UnsupportedOperationException("Method JacobieDialog#getFormElementValueForLabel not implemented yet.");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpCookies(java.io.PrintStream)
     */
    public void dumpCookies(PrintStream stream) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpResponse()
     */
    public void dumpResponse() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpResponse(java.io.PrintStream)
     */
    public void dumpResponse(PrintStream stream) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpTable(java.lang.String, java.io.PrintStream)
     */
    public void dumpTable(String tableNameOrId, PrintStream stream) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpTable(java.lang.String, java.lang.String[][], java.io.PrintStream)
     */
    public void dumpTable(String tableNameOrId, String[][] table, PrintStream stream) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#dumpTable(java.lang.String, java.lang.String[][])
     */
    public void dumpTable(String tableNameOrId, String[][] table) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCookieValue(java.lang.String)
     */
    public String getCookieValue(String cookieName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getFormElementNameForLabel(java.lang.String)
     */
    public String getFormElementNameForLabel(String formElementLabel) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getOptionsFor(java.lang.String)
     */
    public String[] getOptionsFor(String selectName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getOptionValuesFor(java.lang.String)
     */
    public String[] getOptionValuesFor(String selectName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getResponseText()
     */
    public String getPageText() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSparseTableBySummaryOrId(java.lang.String)
     */
    public String[][] getSparseTableBySummaryOrId(String tableSummaryOrId) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getTableBySummaryOrId(java.lang.String)
     */
    public String[][] getTableBySummaryOrId(String tableSummaryOrId) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoFrame(java.lang.String)
     */
    public void gotoFrame(String frameName) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoRootWindow()
     */
    public void gotoRootWindow() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoWindow(java.lang.String)
     */
    public void gotoWindow(String windowName) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#gotoWindowByTitle(java.lang.String)
     */
    public void gotoWindowByTitle(String title) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasButtonWithText(java.lang.String)
     */
    public boolean hasButtonWithText(String text) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasCookie(java.lang.String)
     */
    public boolean hasCookie(String cookieName) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFormParameterLabeled(java.lang.String)
     */
    public boolean hasFormParameterLabeled(String paramLabel) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isElementPresent(java.lang.String)
     */
    public boolean isElementPresent(String anID) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isFramePresent(java.lang.String)
     */
    public boolean isFramePresent(String frameName) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isMatchInElement(java.lang.String, java.lang.String)
     */
    public boolean isMatchInElement(String elementID, String regexp) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isMatchInResponse(java.lang.String)
     */
    public boolean isMatchInResponse(String regexp) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isMatchInTable(java.lang.String, java.lang.String)
     */
    public boolean isMatchInTable(String tableSummaryOrId, String regexp) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isTextInElement(java.lang.String, java.lang.String)
     */
    public boolean isTextInElement(String elementID, String text) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isTextInTable(java.lang.String, java.lang.String)
     */
    public boolean isTextInTable(String tableSummaryOrId, String text) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isWebTableBySummaryOrIdPresent(java.lang.String)
     */
    public boolean isWebTableBySummaryOrIdPresent(String tableSummaryOrId) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isWindowByTitlePresent(java.lang.String)
     */
    public boolean isWindowByTitlePresent(String title) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#isWindowPresent(java.lang.String)
     */
    public boolean isWindowPresent(String windowName) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#removeFormParameter(java.lang.String)
     */
    public void removeFormParameter(String paramName) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#removeFormParameterWithValue(java.lang.String, java.lang.String)
     */
    public void removeFormParameterWithValue(String paramName, String value) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setWorkingForm(java.lang.String)
     */
    public void setWorkingForm(String nameOrId) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#updateFormParameter(java.lang.String, java.lang.String)
     */
    public void updateFormParameter(String paramName, String paramValue) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#setScriptingEnabled(boolean)
     */
    public void setScriptingEnabled(boolean value) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#closeBrowser()
     */
    public void closeBrowser() throws TestingEngineResponseException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#closeWindow()
     */
    public void closeWindow() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getCookies()
     */
    public String[][] getCookies() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getPageSource()
     */
    public String getPageSource() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSelectedOptions(java.lang.String)
     */
    public String[] getSelectedOptions(String selectName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getServerResponse()
     */
    public String getServerResponse() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSparseTable(java.lang.String)
     */
    public String[][] getSparseTable(String tableSummaryNameOrId) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getTable(java.lang.String)
     */
    public Table getTable(String tableSummaryNameOrId) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#goBack()
     */
    public void goBack() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasElement(java.lang.String)
     */
    public boolean hasElement(String anID) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasFrame(java.lang.String)
     */
    public boolean hasFrame(String frameName) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasLink(java.lang.String)
     */
    public boolean hasLink(String anId) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasLinkWithExactText(java.lang.String, int)
     */
    public boolean hasLinkWithExactText(String linkText, int index) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasLinkWithImage(java.lang.String, int)
     */
    public boolean hasLinkWithImage(String imageFileName, int index) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasLinkWithText(java.lang.String, int)
     */
    public boolean hasLinkWithText(String linkText, int index) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasTable(java.lang.String)
     */
    public boolean hasTable(String tableSummaryNameOrId) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasWindow(java.lang.String)
     */
    public boolean hasWindow(String windowName) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasWindowByTitle(java.lang.String)
     */
    public boolean hasWindowByTitle(String title) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#refresh()
     */
    public void refresh() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#selectOptions(java.lang.String, java.lang.String[])
     */
    public void selectOptions(String selectName, String[] options) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#unselectOptions(java.lang.String, java.lang.String[])
     */
    public void unselectOptions(String selectName, String[] options) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSelectOptionLabelForValue(java.lang.String, java.lang.String)
     */
    public String getSelectOptionLabelForValue(String selectName, String optionValue) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSelectOptionValueForLabel(java.lang.String, java.lang.String)
     */
    public String getSelectOptionValueForLabel(String selectName, String optionLabel) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#getSelectOptionValues(java.lang.String)
     */
    public String[] getSelectOptionValues(String selectName) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasSelectOptionValue(java.lang.String, java.lang.String)
     */
    public boolean hasSelectOptionValue(String selectName, String optionValue) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickElementByXPath(java.lang.String)
     */
    public void clickElementByXPath(String xpath) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#clickLinkWithImage(java.lang.String, int)
     */
    public void clickLinkWithImage(String imageFileName, int index) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.jwebunit.IJWebUnitDialog#hasElementByXPath(java.lang.String)
     */
    public boolean hasElementByXPath(String xpath) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasResetButton() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasResetButton(String nameOrID) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean hasSubmitButton() {
        // TODO Auto-generated method stub
        return false;
    }

}