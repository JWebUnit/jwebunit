set projectDir=D:\working\jwebunit\jWebunit
set cpath=%projectDir%\classes;%projectDir%\lib\httpunit.jar;%projectDir%\lib\js.jar;%projectDir%\lib\junit.jar;%projectDir%\lib\servlet.jar;%projectDir%\lib\Tidy.jar;%projectDir%\lib\xercesImpl.jar;%projectDir%\lib\xml-apis.jar
java -cp %cpath% net.sourceforge.jwebunit.fit.DirectoryRunner %1 %2