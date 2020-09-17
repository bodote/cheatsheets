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
````
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
