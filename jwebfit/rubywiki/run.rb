#!/usr/bin/ruby
#(c) 2002, Ward Cunningham

project="d:/p4work/iqe"
cpath="d:/working/jwebunit/jWebUnit/classes;#{project}/webApplication/WEB-INF/classes;#{project}/lib/jwebunit.jar;#{project}/lib/httpunit.jar;#{project}/lib/js.jar;#{project}/lib/junit.jar;#{project}/lib/servlet.jar;#{project}/lib/Tidy.jar;#{project}/lib/xercesImpl.jar;#{project}/lib/xml-apis.jar"

puts "Content-type: text/html\n\n"

referer = ENV['HTTP_REFERER'] || ARGV[0]
referer.sub!(/save\.rb/, 'wiki.rb').untaint
`java -cp #{cpath} net.sourceforge.jwebunit.fit.URLRunner #{referer}`  # why doesn't this work!?!
puts IO.readlines("out.txt").join