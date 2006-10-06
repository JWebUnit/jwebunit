The jWebUnit team is pleased to announce the jWebUnit 1.3 release!

http://jwebunit.sourceforge.net

jWebUnit is a Java framework that facilitates creation of acceptance tests for 
web applications. It evolved from a project where we were using HttpUnit and 
JUnit to create acceptance tests. As the tests were being written, they were 
continuously refactored to remove duplication and other bad smells in the test 
code. jWebUnit is the result of these refactorings. 

Changes in this version include:

  New Features:

o Added ability to navigate to windows / assert presence by window id.
o Refactoring of Table assertions to handle perfectly colspan and rowspan.
o Added XPath methods to core API.
o Added Maven 2 support. There are many reports available on the website.
o Added new method clickButtonWithText.
o Integrated patch for multiple submit buttons with different values.  API change - assertSubmitButtonValue(button, value) now assertSubmitButtonPresent(button, value).
o Assert button (not) present with text added.
o Added ability to navigate to windows / assert presence by window title.
o Added assert select option present / not present.


  Fixed bugs:
o Javascript better support thanks to HtmlUnit

  Changes:
o Remove HttpUnit testing engine.
o Updated to Jetty 6 for running tests. Need less dependencies and run faster.
o Add HtmlUnit as testing engine, that provide better Javascript support.


Have fun!
-The jWebUnit team
      