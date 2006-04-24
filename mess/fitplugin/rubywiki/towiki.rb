root = ARGV[0] || '.'

Dir[root+'/*.fit.in.html'].each do |inf|
    File.open(inf.sub(/\.fit\.in\.html/, ''), 'w') do |out|
        contents = File.readlines(inf).join
        #contents =~ /<body>(.*)<\/body>/m
        #contents = $1
        contents.gsub!(/<\/t[hd]><t[hd][\w\s"=\/]*>/, '|')
        contents.gsub!(/<\/t[hd]><t[hd][\w\s"=\/]*>/, '|')
        contents.gsub!(/<t[hd][\w\s"=\/]*>|<\/t[hd]>/, '|')
        contents.gsub!(/<[\w\s"=\/]*>/, '')
        contents.gsub!(/^\s*\|/, '|')
        out.write contents
    end
end