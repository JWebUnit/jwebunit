/******************************************************************************
 * JWebUnit project (http://jwebunit.sourceforge.net)                         *
 * Distributed open-source, see full license under LICENCE.txt                *
 ******************************************************************************/
package net.sourceforge.jwebunit.tests;

/**
 * Tests for <select> elements.
 */
public class SelectOptionsTest extends JWebUnitAPITestCase 
{

    public void setUp() throws Exception 
    {
        super.setUp();
        setBaseUrl(HOST_PATH + "/SelectOptionsTest");
    }

    public void testAssertSelectOptionsOneForm() 
    {
        beginAt("/pageWithOneForm.html");
		assertSelectOptionsForm1();
        //set the working form an repeat.
        setWorkingForm("form1");
    	assertSelectOptionsForm1();
        beginAt("/pageWithOneFormMulti.html");
		assertSelectOptionsForm1();
        //set the working form an repeat.
        setWorkingForm("form1");
    	assertSelectOptionsForm1();
    }

    public void testAssertSelectOptionsTwoForms() 
    {
        beginAt("/pageWithTwoForms.html");
        setWorkingForm("form1");
        assertSelectOptionsForm1();
        setWorkingForm("form2");
        assertSelectOptionsForm2();
        beginAt("/pageWithTwoFormsMulti.html");
        setWorkingForm("form1");
        assertSelectOptionsForm1();
        setWorkingForm("form2");
        assertSelectOptionsForm2();
    }

	public void testSelectOptionsOneForm()
	{
        //by label.
        beginAt("/pageWithOneForm.html");
        selectOption("singleSelect", "Option 2");
        assertSelectedOptionEquals("singleSelect", "Option 2");
        selectOption("duplicateSelect", "Option 2");
        assertSelectedOptionEquals("duplicateSelect", "Option 2");
        selectOption("duplicateSelect", 0, "Option 3");
        assertSelectedOptionEquals("duplicateSelect", 0, "Option 3");
        selectOption("duplicateSelect", 1, "Option B");
        assertSelectedOptionEquals("duplicateSelect", 1, "Option B");

        beginAt("/pageWithOneFormMulti.html");
        selectOption("singleSelect", "Option 2");
        assertSelectedOptionEquals("singleSelect", "Option 2");
        selectOption("duplicateSelect", "Option 2");
        assertSelectedOptionEquals("duplicateSelect", "Option 2");
        //re-begin since unselectOption is not exposed via WebTester.
        beginAt("/pageWithOneFormMulti.html");
        selectOption("duplicateSelect", 0, "Option 3");
        assertSelectedOptionEquals("duplicateSelect", 0, "Option 3");
        selectOption("duplicateSelect", 1, "Option B");
        assertSelectedOptionEquals("duplicateSelect", 1, "Option B");

        //re-begin since unselectOption is not exposed via WebTester.
        beginAt("/pageWithOneFormMulti.html");
		selectOptions("singleSelect", new String[] { "Option 3", "Option 4"});
		assertSelectedOptionsEqual("singleSelect", new String[] { "Option 3", "Option 4"});
		selectOptions("duplicateSelect", new String[] { "Option 1", "Option 4"});
		assertSelectedOptionsEqual("duplicateSelect", new String[] { "Option 1", "Option 4"});
        //re-begin since unselectOption is not exposed via WebTester.
        beginAt("/pageWithOneFormMulti.html");
		selectOptions("duplicateSelect", 0, new String[] { "Option 2", "Option 4"});
		assertSelectedOptionsEqual("duplicateSelect", 0, new String[] { "Option 2", "Option 4"});
		selectOptions("duplicateSelect", 1, new String[] { "Option C", "Option B"});
		assertSelectedOptionsEqual("duplicateSelect", 1, new String[] { "Option B", "Option C"});

		//by value.        
		beginAt("/pageWithOneForm.html");
        selectOptionByValue("singleSelect", "option2");
        assertSelectedOptionValueEquals("singleSelect", "option2");
        selectOptionByValue("duplicateSelect", "option2");
        assertSelectedOptionValueEquals("duplicateSelect", "option2");
        selectOptionByValue("duplicateSelect", 0, "option3");
        assertSelectedOptionValueEquals("duplicateSelect", 0, "option3");
        selectOptionByValue("duplicateSelect", 1, "optionB");
        assertSelectedOptionValueEquals("duplicateSelect", 1, "optionB");

        beginAt("/pageWithOneFormMulti.html");
        selectOptionByValue("singleSelect", "option2");
        assertSelectedOptionValueEquals("singleSelect", "option2");
        assertSelectedOptionMatches("singleSelect", "option2");
        selectOptionByValue("duplicateSelect", "option2");
        assertSelectedOptionValueEquals("duplicateSelect", "option2");
        //re-begin since unselectOption is not exposed via WebTester.
        beginAt("/pageWithOneFormMulti.html");
        selectOptionByValue("duplicateSelect", 0, "option3");
        assertSelectedOptionValueEquals("duplicateSelect", 0, "option3");
        assertSelectedOptionMatches("duplicateSelect", 0, "option3");
        selectOptionByValue("duplicateSelect", 1, "optionB");
        assertSelectedOptionValueEquals("duplicateSelect", 1, "optionB");

        //re-begin since unselectOption is not exposed via WebTester.
        beginAt("/pageWithOneFormMulti.html");
		selectOptionsByValues("singleSelect", new String[] { "option3", "option4"});
		assertSelectedOptionValuesEqual("singleSelect", new String[] { "option3", "option4"});
		//Note that this matches values, not labels, even though its name doesn't
		//include Values like other methods do.
		assertSelectedOptionsMatch("singleSelect", new String[] { "option3", "option4"});
		selectOptionsByValues("duplicateSelect", new String[] { "option1", "option4"});
		assertSelectedOptionValuesEqual("duplicateSelect", new String[] { "option1", "option4"});
        //re-begin since unselectOption is not exposed via WebTester.
        beginAt("/pageWithOneFormMulti.html");
		selectOptionsByValues("duplicateSelect", 0, new String[] { "option2", "option4"});
		assertSelectedOptionValuesEqual("duplicateSelect", 0, new String[] { "option2", "option4"});
		assertSelectedOptionsMatch("duplicateSelect", 0, new String[] { "option2", "option4"});
		selectOptions("duplicateSelect", 1, new String[] { "Option C", "Option B"});
		assertSelectedOptionValuesEqual("duplicateSelect", 1, new String[] { "optionB", "optionC"});
		assertSelectedOptionsMatch("duplicateSelect", 0, new String[] { "option2", "option4"});
	}




	private void assertSelectOptions(String selectName, 
									 String[] validOptions, String invalidOption,
								     String[] validOptionValues, String invalidOptionValue)
	{
		for ( int i = 0; i < validOptions.length; ++i )
		{
			assertSelectOptionPresent(selectName, validOptions[i]);
		}
		assertSelectOptionsPresent(selectName, validOptions);
		assertSelectOptionsEqual(selectName, validOptions);
		for ( int i = 0; i < validOptionValues.length; ++i )
		{
			assertSelectOptionValuePresent(selectName, validOptionValues[i]);
		}
		assertSelectOptionValuesPresent(selectName, validOptionValues);
		assertSelectOptionValuesEqual(selectName, validOptionValues);

		assertSelectOptionNotPresent(selectName, invalidOption);
		assertSelectOptionsNotEqual(selectName, new String[] { invalidOption});
		assertSelectOptionValueNotPresent(selectName, invalidOptionValue);
		assertSelectOptionValuesNotEqual(selectName, new String[] { invalidOptionValue });
	}	

	private void assertSelectOptions(String selectName, int index, 
				   				     String[] validOptions, String invalidOption,
								     String[] validOptionValues, String invalidOptionValue)
	{
		for ( int i = 0; i < validOptions.length; ++i )
		{
			assertSelectOptionPresent(selectName, index, validOptions[i]);
		}
		assertSelectOptionsPresent(selectName, index, validOptions);
		assertSelectOptionsEqual(selectName, index, validOptions);
		for ( int i = 0; i < validOptionValues.length; ++i )
		{
			assertSelectOptionValuePresent(selectName, index, validOptionValues[i]);
		}
		assertSelectOptionValuesPresent(selectName, index, validOptionValues);
		assertSelectOptionValuesEqual(selectName, index, validOptionValues);

		assertSelectOptionNotPresent(selectName, index, invalidOption);
		assertSelectOptionsNotEqual(selectName, index, new String[] { invalidOption});
		assertSelectOptionValueNotPresent(selectName, index, invalidOptionValue);
		assertSelectOptionValuesNotEqual(selectName, index, new String[] { invalidOptionValue });
	}

	
	private void assertSelectOptionsForm1()
	{
        //there is just one form element named "singleSelect".
        //find its options by label.
		assertSelectOptions("singleSelect",
							new String[] { "Option 1", "Option 2", "Option 3", "Option 4"},
							"badoption",
							new String[] { "option1", "option2", "option3", "option4"},
							"badoption");
		assertSelectOptions("singleSelect", 0,
							new String[] { "Option 1", "Option 2", "Option 3", "Option 4"},
							"badoption",
							new String[] { "option1", "option2", "option3", "option4"},
							"badoption");

        //there are two form elements named "duplicateSelect"
        //first one has option1,option2.  second one has optionA,optionB
		//non-indexed version should always get the first one.
		assertSelectOptions("duplicateSelect",
							new String[] { "Option 1", "Option 2", "Option 3", "Option 4"},
							"badoption",
							new String[] { "option1", "option2", "option3", "option4"},
							"badoption");
		assertSelectOptions("duplicateSelect", 0,
							new String[] { "Option 1", "Option 2", "Option 3", "Option 4"},
							"badoption",
							new String[] { "option1", "option2", "option3", "option4"},
							"badoption");
		assertSelectOptions("duplicateSelect", 1,
							new String[] { "Option A", "Option B", "Option C", "Option D"},
							"badoption",
							new String[] { "optionA", "optionB", "optionC", "optionD"},
							"badoption");
	}
	private void assertSelectOptionsForm2()
	{
        //there is just one form element named "singleSelect".
        //find its options by label.
		assertSelectOptions("singleSelectA",
							new String[] { "Option 1a", "Option 2a", "Option 3a", "Option 4a"},
							"badoption",
							new String[] { "option1a", "option2a", "option3a", "option4a"},
							"badoption");
		assertSelectOptions("singleSelectA", 0,
							new String[] { "Option 1a", "Option 2a", "Option 3a", "Option 4a"},
							"badoption",
							new String[] { "option1a", "option2a", "option3a", "option4a"},
							"badoption");

        //there are two form elements named "duplicateSelect"
        //first one has option1,option2.  second one has optionA,optionB
		//non-indexed version should always get the first one.
		assertSelectOptions("duplicateSelectA",
							new String[] { "Option 1a", "Option 2a", "Option 3a", "Option 4a"},
							"badoption",
							new String[] { "option1a", "option2a", "option3a", "option4a"},
							"badoption");
		assertSelectOptions("duplicateSelectA", 0,
							new String[] { "Option 1a", "Option 2a", "Option 3a", "Option 4a"},
							"badoption",
							new String[] { "option1a", "option2a", "option3a", "option4a"},
							"badoption");
		assertSelectOptions("duplicateSelectA", 1,
							new String[] { "Option Aa", "Option Ba", "Option Ca", "Option Da"},
							"badoption",
							new String[] { "optionAa", "optionBa", "optionCa", "optionDa"},
							"badoption");
	}
}
