# VSCode support für Angular

## Extension: Angular Language Service

[hier](https://marketplace.visualstudio.com/items?itemName=Angular.ng-template)

## Extension: Auto Import

## Extension: VisualStudio IntelliCode

## VSCode für Angular npm-scrips-tab anpassen

- OSX : change Settings: `"terminal.integrated.inheritEnv": true, "terminal.integrated.shell.osx": "/bin/bash"`

## Karma Debug direkt in VSCode:

https://medium.com/nextfaze/debug-angular-10-karma-tests-in-vscode-9685b0565e8

## Angular debug direkt in VSCode:

see: https://youtu.be/tC91t9OvVHA

- no debugger in chrome extension necessary
- start the app via npm script "start" (or `ng serve`)
- open vscode debug panel, wenn app.component.ts im editor offen ist
- klick auf "Ausführen und Debuggen"
- editiere den port auf "4200" in der `launch.json` wenn nötig.
- klicke oben auf den grünen Pfeil zum starten
