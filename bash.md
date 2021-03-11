# bash allgemein

### bash farben
setzte weißen hintergrund / schwarze schrift
` bash> echo -e "Default \e[107mWhite \e[30mBlack"`

### kill mehrere Prozesse mit grep gefunden
` ps aux | grep 3313-re | awk '{print $2}' | xargs kill -9`

## debug
```bash
#!/bin/bash

set -x
..code to debug...
set +x
``` 

