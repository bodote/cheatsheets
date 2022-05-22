# Docu fix for jekyll + minimal-mistakes theme 
https://mmistakes.github.io/minimal-mistakes/docs/quick-start-guide/
## 1st Trail (not working)
### Start with:
Its unclear where to start.
I choose to start with : 
```
  jekyll new mm-remote-theme-method 
  cd mm-remote-theme-method
```

### Using "Remote theme methodPermalink":
watch the "note" from above in [quick-start-guide](https://mmistakes.github.io/minimal-mistakes/docs/quick-start-guide/), it is also needed for remote theme! 
(which was unclear to me at first)
>  jekyll-include-cache plugin which will need to be installed in your Gemfile **AND** added to the plugins array of _config.yml.

Therefore replace the contens of the Gemfile with: 
```
source "https://rubygems.org"

gem "github-pages", group: :jekyll_plugins
gem "minimal-mistakes-jekyll"
gem "jekyll-include-cache"
```

add this to the `_config.yml`:
```
remote_theme: "mmistakes/minimal-mistakes@4.20.2" 
plugins:
  - jekyll-include-cache
```

and delete the `theme: minima` line in `_config.yml` as the [quick-start-guide](https://mmistakes.github.io/minimal-mistakes/docs/quick-start-guide/) already states

then run `bundle`and then `bundle exec jekyll serve` but that doesn't work yet: I got a `Liquid Exception: No repo name found....` - error.

So obviousely you need to make the working directory (which is still mm-remote-theme-method) to be a git controled repository. 
My way was to create a empty projekt on my github account (i called it "bt-blog" , make a `git clone git@github.com:bodote/bt-blog.git`in there.
Then 
```
cd ..
cp -r mm-remote-theme-method/* bt-blog/
cd bt-blog
bundle exec jekyll serve
```

and voila: Server is finaly running with only minor warnings . The site is generated on http://localhost:4000 but still there are some parts missing.
you need to replase in `about.markdown`the line `layout: page` by `layout: single` and replase in `_posts/2020-09-17-welcome-to-jekyll.markdown`the line `layout: post` by `layout: posts` 

ok now the warnings disapears. but finally, when wir look the the result on http://localhost:4000 the links to the targes in the navigation (\_data/navigation.yml) are broken, eg.: http://localhost:4000/posts/ leads to a "not Found" error for what ever reason. maybe becausethere

## 2nd trail:
we make a indentical copy from https://github.com/mmistakes/mm-github-pages-starter/ , create a new projekt with the same name on my own github account.
Then we change Gemfile and remove these 2 lines:
``` 
gem "tzinfo-data"
gem "wdm", "~> 0.1.0" if Gem.win_platform?
```
because tzinfo-data is not really necessary and leads to a [security problem](https://github.com/mmistakes/minimal-mistakes/issues/2693) at least on my MacBook.
and the 2nd one is obviousely not needed for MacOS or github-pages-hosting.

And hey, it finally works on http://localhost:4000 ! 




