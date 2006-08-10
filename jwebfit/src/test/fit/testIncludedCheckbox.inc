|net.sourceforge.jwebunit.fit.WebFixture ||
| baseUrl |http://localhost:8082 |

Test manipulation of checkbox input fields.  Note that jWebUnit 
supports only single checkboxes with given name.  Support for
multiple checkboxes with same name and different values will be
added later.

| net.sourceforge.jwebunit.fit.WebFixture |||
| begin    | personalInfoForm ||
| enter    | fullName         | Jim                                      |  
| select   | checkbox         | citizenCheckbox                          |  
| press    | submit           ||                                         
| check    | title            | Personal Info Results                    |
| check    | text present     | Name given is Jim                        |  
| check    | text present     | You indicated that you are a citizen     |  
| begin    | personalInfoForm ||                                         
| enter    | fullName         | Wilkes                                   |  
| select   | checkbox         | citizenCheckbox                          |  
| deselect | checkbox         | citizenCheckbox                          |  
| press    | submit           ||                                         
| check    | title            | Personal Info Results                    |  
| check    | text present     | Name given is Wilkes                     |  
| check    | text present     | You indicated that you are not a citizen |  