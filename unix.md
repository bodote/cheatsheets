# Unix based OS
##  ruby , gem
* uninstall all `gem uninstall -aIx`
* `gem query -l -n jekyll-theme | xargs gem uninstall` uninstall all gems that match "jekyll-theme"
* `gem list | cut -d" " -f1 | xargs gem uninstall -aIx`

##  find
* `find . -type f  -mtime +30 -exec echo  {} \;` find all files older then 30 days
* `find . -type d -depth 1 -mtime -30 -exec echo  {} \;` find all direcotry newer then 30 days
* `find . -type d -depth 1 -mtime -30 -print | sed -r 's/\.\/(.*)(-[[:digit:]].*)/\1/'`
* `find . -type d -depth 1 -mtime -10 -print | sed -r 's/\.\/(.*)(-[[:digit:]].*)/\1/' | xargs gem uninstall `

0 drwxr-xr-x   3 root  wheel   96  1 Jan  2020 bundler-1.17.2eci
0 drwxr-xr-x  15 root  wheel  480  1 Jan  2020 did_you_mean-1.3.0
0 drwxr-xr-x   3 root  wheel   96  1 Jan  2020 irb-1.0.0
0 drwxr-xr-x  10 root  wheel  320  1 Jan  2020 net-telnet-0.2.0
0 drwxr-xr-x   3 root  wheel   96  1 Jan  2020 rdoc-6.1.0
0 drwxr-xr-x  10 root  wheel  320  1 Jan  2020 xmlrpc-0.3.0