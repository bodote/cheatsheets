# e2e
`ng e2e`
# cypress
* `npm install --save-dev cypress` fuer das jeweilige Angular Projekt
* projekt servern mit `ng serve`
* in andererm Terminal`npx cypress open` um den cypress client zu starten. 
* cypress ersetzt angulars `protractor`  und gibts open source oder kommerziell
* unabhängig von angular, geht mit jeder browseranwendung
## [cypress tutorial mit Angular default Seite](https://medium.com/@armno/setting-up-cypress-for-an-angular-project-92dfa2e2caef)
### cypress.json
```json
{"$schema": "https://on.cypress.io/cypress.schema.json",
  "baseUrl": "http://localhost:4200"
}
``` 
### cypress tests
füge als erste Zeile dies hinzu `/// <reference types="Cypress" />` (This is to get type definition support from VSCode’s IntelliSense for auto-completion and method signatures of Cypress.)


## [cypress tutorial mit Tour of Heroes](https://medium.com/@talktokapildev/angular-e2e-with-cypress-and-cucumber-d689e123d469)