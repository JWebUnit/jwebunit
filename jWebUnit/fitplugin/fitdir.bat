set projectDir=D:\projects\jwebunit\
set cpath=%projectDir%\bin;%projectDir%\lib\fit.jar;%projectDir%\lib\httpunit.jar;%projectDir%\lib\js.jar;%projectDir%\lib\junit.jar;%projectDir%\lib\servlet.jar;%projectDir%\lib\Tidy.jar;%projectDir%\lib\xercesImpl.jar;%projectDir%\lib\xml-apis.jar
java -cp %cpath% net.sourceforge.jwebunit.fit.DirectoryRunner %1