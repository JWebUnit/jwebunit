#!ruby
# Copyright 1994-2000, Cunningham & Cunningham, Inc.
# in collaboration with Dave W. Smith
# Open Source for personal use only.
# ... and then only
# with the understanding that the owner(s) cannot be
# responsible for any behavior of the program or
# any damages that it may cause. See LICENSE.TXT
require './riki'

class RefCountPage < Riki::RikiPage
    def initialize()
        super("http:pagerefs.rb")
        @title = "Page References"
    end

    def run
        @files = Dir.entries('pages').select {|e| e =~ /#{Riki::LINK}/}.sort!
        refs = {}
        @files.each do |file|
            contents = readFile("pages/#{file}")
            targets = {}
            contents.scan(/(#{Riki::LINK})/).each {|link| targets[link[0]] = file }
            inspectStr targets
            targets.each_key do |target|
                refs[target] = [] unless refs.has_key?(target)
                refs[target].push(file)
            end
        end
        @refcnt = {}
        @files.each { |file| @refcnt[file] = refs.has_key?(file) ? refs[file].length : 0 }
        @files.sort! {|a,b| @refcnt[b] <=> @refcnt[a] || a <=> b }
    end

    def body
        s = "<table border=0 cellspacing=0 cellpadding=0>\n"
        @files.each do |file|
            s += <<-ROW
            <tr>
            <td valign="right">#{@refcnt[file]}</td>
            <td>&nbsp;&nbsp;<a href="wiki.rb?page=#{file}">#{file}</a></td>
            </tr>
            ROW
        end
        s += "</table>\n"
    end
end


Riki::RefCountPage.new.display