#! /usr/bin/ruby
## Copyright 1994-2000, Cunningham & Cunningham, Inc.
## in collaboration with Dave W. Smith
## Open Source for personal use only.
## ... and then only
## with the understanding that the owner(s) cannot be
## responsible for any behavior of the program or
## any damages that it may cause. See LICENSE.TXT

## straight up port from Ward's perl quicki wiki to ruby
## plus support for tables

MARK = "\263"
LINK = "[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+"

PROTOCOL = "(http|ftp|mailto|file|gopher|telnet|news)"


def formatBody(lines)
  codeArr = []
  body = ''
  lines.gsub!(/&/, '&amp;')
  lines.gsub!(/</, '&lt;')
  lines.gsub!(/>/, '&gt;')
  #puts "<!-- #{lines} -->"

  hnum = 0  # initialized for AsHiddenLink

  lines.split('\n').each do |s|
    urls   = []
    urlNum = 0

    while (s.sub!(/\b(javascript):\S.*/, "#{MARK}#{urlNum}#{MARK}"))
      urls[urlNum] = $&
      urlNum+=1
    end

    while (s.sub!(/\b#{PROTOCOL}:[^\s\<\>\[\]"'\(\)]*[^\s\<\>\[\]"'\(\)\,\.\?]/,
           "#{MARK}#{urlNum}#{MARK}"))
      urls[urlNum] = $&
      urlNum+=1
    end

    # -v- emitcode block-tag section
    code = ''
    s.sub!(/^\t+/, '')  # legacy pages with tab indented lists
    code = '...'                                    if s.sub!(/^\s*$/, '<p>')
    body += emitCode(codeArr, code='pre', 1)        if s =~ /^\s/
    body += emitCode(codeArr, code='ul', $1.length) if s.sub!(/^(\*+)/, '<li>')
    body += emitCode(codeArr, code='ol', $1.length) if s.sub!(/^(\#+)/, '<li>')
    body += emitCode(codeArr, code='dl', $1.length) if s.sub!(/^(:+)(.+?):( +)/, '<dt>\2<dd>')
    body += emitCode(codeArr, code='ol', 1)         if s.sub!(/^(\d+)\./, '<li>')
    body += emitCode(codeArr, code='h4', 1)         if s.sub!(/^!!!!/, '')
    body += emitCode(codeArr, code='h3', 1)         if s.sub!(/^!!!/, '')
    body += emitCode(codeArr, code='h2', 1)         if s.sub!(/^!!/, '')
    body += emitCode(codeArr, code='blockquote', 1) if s.sub!(/^\"\"/, '')
    if s =~ /^\|/
        s.sub!(/^\|.*\|$/) {asRow($&)}
        body += emitCode(codeArr, code='table', 1)
    end
    body += emitCode(codeArr, '', 0)                if code == ''

    ## -v- inline tag section
    s.sub! (/^-----*/,       '<hr>')
    s.gsub!(/'{3}(.*?)'{3}/, '<strong>\1</strong>')
    s.gsub!(/'{2}(.*?)'{2}/, '<em>\1</em>')

    s.gsub!(/\[Search\]/, '<form action="search.rb">' +
                          '<input type="text" name="search" size="40"><input type="submit" value="Search">'+
                          '</form>')

    s.gsub!(/\b#{LINK}\b/)         { asAnchor($&)              }
    s.gsub!(/#{MARK}(\d+)#{MARK}/) { inPlaceUrl(urls[$1.to_i]) }
    body += "#{s}\n"
  end

  body += emitCode(codeArr, '', 0);
  return body
end


def emitCode(codeArr, code, depth)
  tags = ''
  startTag = if code =~ /table/
               "table border=\"1\" cellspacing=\"0\" cellpadding=\"3\""
             else
               code.dup
             end
  while(codeArr.length > depth)
    tags += "</" + codeArr.pop + ">\n<p>"
  end
  while(codeArr.length < depth)
    codeArr.push code
    tags += "<#{startTag}>"
  end
  if !codeArr.empty? && codeArr.last != code
    tags += "</#{codeArr.last}>\n<#{startTag}>";  # split with \n
    codeArr[-1] = code
  end
  return tags
end

def asRow(s)
    puts "<!-- asRow => #{s} -->"
    cells = s.split('|')[1..-1]
    puts "<!-- cells => #{cells.inspect} -->"
    cellspans = []
    cells.each do |e|
        if e.strip! != ''
            cellspans << [e, 1]
        else
            cellspans.last[1] += 1 unless cellspans.empty?
        end
    end
    puts "<!-- cellspans => #{cellspans.inspect} -->"
    row = '<tr>'
    cellspans.each do |cell|
        content = cell[0]
        span = cell[1]
        td = (span > 1) ? "<td colspan=\"#{span}\">" : "<td>"
        td += content + "</td>"
        row += td
    end
    row += '</tr>'
end


def asAnchor(title)
  if File.exists?("pages/#{title}")
    "<a href=wiki.rb?#{title}>#{title}<\/a>"
  else
    "<a href=edit.rb?#{title}>?<\/a>#{title}"
  end
end

def inPlaceUrl(origRef)
  ref = origRef.dup
  ref.sub!(/^(javascript.{30}).*/, "#{$1} ...")
  regx = Regexp.new(/\.(gif|jpeg|jpg|png)$/, Regexp::IGNORECASE)
  regx.match(ref) ? "<img src=\"#{ref}\">" :"<a href=\"#{origRef}\">#{ref}<\/a>";
end

###############################################################################
# Main                                                                        #
###############################################################################
require 'cgi'

print "Content-type: text/html\n\n"

page = ENV['QUERY_STRING'] =~ /^(#{LINK})$/ ? $1 : "WelcomeVisitors"  # $& is the last match

par = {}
par['page' ] = page
par['title'] = page.gsub(/(.)([A-Z])/, '\1 \2')

date = nil
if File.exist?("pages/#{page}")
  body = IO.readlines("pages/#{page}").join
  #if ( /$mark/ ) {
  #  my %bla = split /$mark/, $_ ;
  #  $_ = $bla{text};  # convert hidden-field page to plain
  #}  # if-part same as in edit
  date = File.mtime("pages/#{page}").strftime("%B %d, %Y")
else
  body = <<-BODY
  "#{page}" does not yet exist. <BR>
    Use the <strong>Edit</strong> button to create it.
    BODY
end

par['summary'] = " -- Last edited #{date}" if date
par['body'] = formatBody(body)
par['action'] = <<-BLAH
<form method=post action="edit.rb?#{page}">
<input type=submit value=" Edit ">
</form>
    BLAH

template = IO.readlines("template.html").join
template.gsub!(/\$(\w+)/) { par.has_key?($1) ? par[$1] : '' }
print template