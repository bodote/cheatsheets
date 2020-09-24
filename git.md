### neues project welches local schon ein git ist, nach github oder git.office hochladen
* in WebGui neues Projekt 
```
it remote add origin git@github.com:bodote/neuerProjektname.git
git push -u origin master
```
* wenn das Projekt schon mal anderswo eingecheckt war , dann muss statt dessen die remote url geändert werden:

`git remote set-url origin https://hostname/USERNAME/REPOSITORY.git`

* To create a new branch and switch to it at the same time, you can run the git checkout command with the -b switch:
```
git checkout -b newBranch
git push --set-upstream origin newBranch  # diesen dann auch remote einchecken
```
