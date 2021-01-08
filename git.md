# git tricks:
## neues project welches local schon ein git ist, nach github oder git.office hochladen
* in WebGui neues Projekt 
```
git remote add origin git@github.com:bodote/neuerProjektname.git
git push -u origin master
```
* wenn das Projekt schon mal anderswo eingecheckt war , dann muss statt dessen die remote url geändert werden:

`git remote set-url origin https://hostname/USERNAME/REPOSITORY.git`

* To create a new branch and switch to it at the same time, you can run the git checkout command with the -b switch:
```
git checkout -b newBranch
git push --set-upstream origin newBranch  # diesen dann auch remote einchecken
```
## git (remote) tags
* alle commits anzeigen `git log --pretty=oneline` 
* show tags mit dem zugehörigen commit : `git show-ref --tags`. Oder nur bestimmte Tags: `git tag --sort=committerdate -l v*`  zeigt alle die mit `v` im Namen beginnen
* shot tag details: `git show v1.0` 
* und remote tags zeigen mit  :`git ls-remote --tags origin`
* local tag löschen : `git tag -d v1.4`
* remote tag löschen: `git push --delete origin <tag-name>` oder besser noch `git push  origin :refs/tags/<tag-name>` um **Verwechslungen** mit einem Branch zu vermeiden
* tag nachträglich auf bestimmten Commit setzten: `git tag -a v1.2 -m "kommentar" 9fceb02` wobei `9fceb02` die commmit ID ist
* remote tag: `git push origin v1.5` oder direkt alle Tags veröffentlichen: `git push origin --tags`
* 
## git (remote) branches
* neue branch: `git checkout -b local_backend_setup`
* push to server: `git push origin local_backend_setup`

## git last commit message:
`git log -1 --pretty=%B`
