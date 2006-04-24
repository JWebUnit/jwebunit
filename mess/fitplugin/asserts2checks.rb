out = File.new('checks.txt', 'w')
File.open('asserts.txt') { |f|
    f.each_line { |l|
        l.gsub!(/assert|\java\.lang\.|\)|;|net\.sourceforge\.jwebunit\./, '')
        l.gsub!(/[A-Z]/) {|s| " #{s.downcase}"}
        l.gsub!(/\(|,/, ' |')
        out.puts('| check | ' + l.strip + ' |')
    }
}
out.close