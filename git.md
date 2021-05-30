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
* show all remote branches : `git branch -r`
* neue branch: `git checkout -b local_backend_setup`
* push to server: `git push -u origin DK-72_web_umb_bookm_list` , **"-u"** ist wichtig damit der locale branch auch den remote branch ab sofort tracked!
* checkout existing remote  branch; `git checkout --track <name of remote>/<branch name>` 
* **THIS DOES NOT WORK** as expected: on a feature branch, but want to update the master: `git pull origin develop:develop ` auf KEINEN FALL mit `--rebase`  if `develop` is the name of you main/master-branch.
* on a feature branch: `git fetch origin` gets **NOT** the remote changes either, only get new branches or tags
* instead: to **merge AND pull** from a remote branch , just use `git merge origin/develop` to merge the latest changes from remote-"develop" in your feature branch 
* on a feature branch, but want to update the master:   `git fetch origin develop:develop ` does also apply the changes to the local copies of this particular remote branch (this has worked for me already)
* git delete local branch `git branch -d name`
* checkout remote branch `git checkout --track origin/newsletter`
* show connection local<->remote branches `git branch -vv`
### rebase vs merge:
* see : https://www.atlassian.com/de/git/tutorials/merging-vs-rebasing
* use rebase if possible but **ONLY** on **LOCAL CHANGES**, but only on local feature branch that only you and nobody else has, never on   branches that other people use!  Use `git merge origin/develop` for public feature branches,
* because "rebase" changes historic commits that other people might have checked out already
* Rebase:  Der gesamte feature--Branch wird zur Spitze des master-Branch verschoben :![rebase](./assets/rebase.svg  )
* Merge Der Änderungen seit der Abzweigung werden in den  feature--Branch als neuer zusätzlicher Commit hinzugefügt :![rebase](./assets/merge.svg  )
* fast-forward-merge: if there is already a linear history, but the master-pointer points to an older commit, then the fast-forward points the masterpointer now to the most recent commit, which will be the head of the master now.


## git merge

## git stash oder reset
* `git stash list`  `git stash  push -m "name" `
* `git reset  HEAD ; git restore *`
   * --soft: uncommit changes, changes are left staged (index).
   * --mixed (default): uncommit + unstage changes, changes are left in working tree.
   * --hard: uncommit + unstage + delete changes, nothing left.
* `git reset --hard HEAD ` plus ggf. `git rebase --abort` resettet alles auf den aktuellen HEAD
* `git reset --hard HEAD^ ` löscht den letzten (localen) commit und   resettet alles auf den aktuellen HEAD

* `git reset --hard HEAD~2` reset last 2 commits ein `git push --force` resettet dann dementsprechend auch den remote  branch


## git logs :
* last commit message `git log -1 --pretty=%B`
* changes by author `git log --stat --author="Bodo"`
* `git log --oneline --graph`

