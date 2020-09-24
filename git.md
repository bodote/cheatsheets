## neues project welches local schon ein git ist, nach github oder git.office hochladen
* in WebGui neues Projekt 
```
it remote add origin git@github.com:bodote/neuerProjektname.git
git push -u origin master
```
* wenn das Projekt schon mal anderswo eingecheckt war , dann muss statt dessen die remote url geändert werden:
`git remote set-url origin https://hostname/USERNAME/REPOSITORY.git`
