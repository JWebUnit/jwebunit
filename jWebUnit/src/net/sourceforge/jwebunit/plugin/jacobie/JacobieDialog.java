/********************************************************************************
 * Copyright (c) 2001, ThoughtWorks, Inc.
 * Distributed open-source, see full license under licenses/jwebunit_license.txt
 **********************************/
package net.sourceforge.jwebunit.plugin.jacobie;

import java.util.ListIterator;

import net.sf.jacobie.exception.JacobieException;
import net.sf.jacobie.ie.A;
import net.sf.jacobie.ie.DefaultHTMLElement;
import net.sf.jacobie.ie.Document;
import net.sf.jacobie.ie.Form;
import net.sf.jacobie.ie.IE;
import net.sf.jacobie.ie.Input;
import net.sf.jacobie.ie.Select;
import net.sourceforge.jwebunit.CompositeJWebUnitDialog;
import net.sourceforge.jwebunit.TestContext;
import net.sourceforge.jwebunit.exception.TestingEngineResponseException;
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
public class JacobieDialog extends CompositeJWebUnitDialog {

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

	public void beginAt(String url, TestContext context) {
		//no notion yet of a test context.
		initWebClient();
		try {
			naviateToPage(url);
		} catch (Exception anException) {
			throw new RuntimeException(ExceptionUtility
					.stackTraceToString(anException));
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
	public void reset() {
		resetIE();
	}

	/**
	 * Reset the current form. See {@link #getForm}for an explanation of how
	 * the current form is established.
	 */
	public void resetForm() {
		throw new UnsupportedOperationException("resetForm");
	}

	/**
	 * Resets the IE object. Must kill the object (quit) before calling a new
	 * one.
	 */
	public void resetIE() {
		IE theIE = getIe();
		theIE.Quit();
		//ComThread.Release(); //this may have been causing problems between
		// each "test" method ran.
		setIe(null);
	}

	public String getResponsePageTitle() {
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
					"Element Not Found witht the following name: [" + paramName
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
		if (theDocument != null && theDocument.findLinkByID(anId) != null) {
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
		if (theDocument != null && theDocument.hasLinkByInnerHTML(imageFileName)) {
			bReturn = true;
		}
		return bReturn;
	}

	public boolean isLinkPresentWithText(String linkText) {
		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		if (theDocument != null && theDocument.hasLink(linkText)) {
			bReturn = true;
		}
		return bReturn;
	}

	public boolean isLinkPresentWithText(String linkText, int index) {
		int iZeroToOneIndex = index + 1; //converting to jacobie 1 based index.

		boolean bReturn = false;
		Document theDocument = getIe().getDocument();
		if (theDocument != null && theDocument.hasLinks()) {
			A theA = theDocument.findLink(linkText, iZeroToOneIndex);
			if (theA != null) {
				bReturn = true;
			}
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
     * Navigate by submitting a request based on a link with a given image file name. A RuntimeException is thrown if
     * no such link can be found.
     * 
     * @param imageFileName
     *            A suffix of the image's filename; for example, to match <tt>"images/my_icon.png"<tt>, you could just pass in
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

	
	
	public void clickLinkWithText(String linkText) {
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

	public void clickLinkWithText(String linkText, int index) {
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

}