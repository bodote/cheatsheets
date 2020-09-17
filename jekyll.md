# Docu fix for minimal-mistakes theme 
https://mmistakes.github.io/minimal-mistakes/docs/quick-start-guide/
## Start with:
Its unclear where to start.
I choose to start with : 
```
  jekyll new mm-remote-theme-method 
  cd mm-remote-theme-method
```

## Using "Remote theme methodPermalink":
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

ok now the warnings disapear.


