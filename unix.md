# ruby , gem
* uninstall all `gem uninstall -aIx`
* `gem query -l -n jekyll-theme | xargs gem uninstall` uninstall all gems that match "jekyll-theme"
* `gem list | cut -d" " -f1 | xargs gem uninstall -aIx`

# find
* `find . -type f  -mtime +30 -exec echo  {} \;` find all files older then 30 days
* `find . -type d -depth 1 -mtime -30 -exec echo  {} \;` find all direcotry newer then 30 days
