# Angular and jest (instead of karma/jasmine)
## Links
* https://www.amadousall.com/how-to-set-up-angular-unit-testing-with-jest/  however : 
  * You *FIRST* need to `ng new my-app` and then `npm install jest jest-preset-angular @types/jest --save-dev` If I do it as described in the blog then I end up with NO jest or jest-preset-agnular in `package.json`
  * and SECOND : you need to delete the first line of `tsconfig.json` because thats a comment that jest does not like in this json und would give you a Unexpected token / in JSON at position 0 error
  * If you would do exactly what is describe in the blog , npx jest would fail
* https://github.com/ahasall/angular-jest-demo.git
* https://github.com/thymikee/jest-preset-angular.git
* https://github.com/briebug/jest-schematic

## new Angular Project with jest:
```bash
ng new angular-jest-tutorial
cd angular-jest-tutorial
ng add @briebug/jest-schematic
```
**IMPORTANT:** add in `./tsconfig.json`: `"types":[ "jest"],` , only then , the *vscode* will use `jest.expect()` instead of `jasmine.expect()`
### remarks:
[@briebug/jest-schematic](https://github.com/briebug/jest-schematic)  includes [@angular-builders/jest](https://github.com/just-jeb/angular-builders/tree/master/packages/jest) which in turn uses `jest-preset-angular`
*  @angular-builders/jest makes "ng test" work with *jest*
* change in `./setup-jest.ts` `import 'jest-preset-angular/setup-jest';`

## Debug jests own errors: 
* Run jest with `--detectOpenHandles`. This will show you what is actually wrong with your test spec. For me, there were missing Angular Material imports and service mocks. You may be prompted to add the BrowserAnimationsModule, as Nambi eluded to in his answer

## Differences to karma/jasemine
* for `jest` `TestBed.configureTestingModule()` needs to have everything the `app.module.ts` has , that is more then jasemine needs
* run test with `npx jest` or even `npx jest --detectOpenHandles`


## jest CLI:
* `jest  --collect-coverage` (see `./coverage/my-app/index.html` after running), `jest --watch`
* add `export PATH=$PATH:./node_modules/.bin` to `~/.bash_profile`  or `~/.bashrc`  (or wherever your `PATH` is defined) 
* --testNamePattern=

 
## Jest config
* which jest config to use (especially in which path or subpath from the root of your project) is not really clear from the official jest docu.
* I found out that in monorepos / multiproject repos it can work as follows:
  * in `<proj-root>/angular.json` you typicaly have all your subproject defined in 
```typescript
  { projects: {
    myProj1:{
      architect:{
        test:{
          "builder": "@angular-builders/jest:run", // to make  "ng test"  work with jest in addition to just "jest"
          "options": {
            "configPath": "./src/jest.config.js",
            "tsConfig": "./src/tsconfig.spec.json"
          }
        }
      }
    }
    myProj2:{
      architect:{
        test:{
          "builder": "@angular-builders/jest:run",// to make  "ng test"  work with jest in addition to just "jest"
          "options": {
            "configPath": "./someotherpath/jest.config.js",
            "tsConfig": "./someotherpath/tsconfig.spec.json"
          }
        }
      }
    }
  }} 
```
 


## VisualCode and jest-plugin:
* see https://github.com/jest-community/vscode-jest
* in Order to make VSCode recognice `expect()` as jest.Expect instead of jasemin.Expect and to make `toMatchSnapshot` does not exist on type 'Matchers'.` - error go away:
  * add in `./tsconfig.json`: `"types":[ "jest"],` , only then , the *vscode* will use `jest.expect()` instead of `jasmine.expect()` as stated above
* in vscode global settings:  `jest.disabledWorkspaceFolders": ["bodote.github.io","cheatsheets","docs","bodo.data"]`
* All settings are prefixed with jest and saved in standard .vscode/settings.json:
```json
{ "jest.autoRun": {
        "watch": true, 
        "onStartup": ["all-tests"] } }
```
* debug vscode-jest plugin: add   `"jest.debugMode": true` to `.vscode/settings.json`
* .vscode/launch.json: 
```json
 {
      "type": "node",
      "name": "vscode-jest-tests",
      "request": "launch",
      "console": "integratedTerminal",
      "internalConsoleOptions": "neverOpen",
      "disableOptimisticBPs": true,
      "program": "${workspaceFolder}/node_modules/.bin/ng",
      "cwd": "${workspaceFolder}",
      "args": [
        "test",
        "--",
        "--testPathPattern=${fileBasenameNoExtension}",
        "--runInBand",
        "--watchAll=false"
      ]
    }
```
* add in framework/projects an additional setup-jest.ts to avoid vscode-jest - plugin errors:
```
import '../setup-jest';
```

### only if that does not work:
* place in `src/global.d.ts`: `import { describe, expect } from '@jest/globals';` 
* or, if `src/global.d.ts` does not work eather, add `import { describe, expect } from '@jest/globals';` in your `.spec.ts` file directly
* tsconfig.json: remove alles mit `cypress*/**` , does not help
* single line fix : `const exp1 = ((expect as any) as typeof import('@jest/globals').expect); exp1('').toMatchSnapshot();`
* or for each `*.spec.ts` - file:  from  https://jestjs.io/docs/api and https://github.com/jest-community/vscode-jest/issues/440#issuecomment-828294712 `import { describe, expect } from '@jest/globals';`
* or maybe `injectGlobals` in package.json ->"jest": {  injectGlobals: true }
* **not** needed: .vscode/settings.json: `typescript{ "jest.jestCommandLine": "ng test --runInBand --"}`
* does **NOT** help eather: `src/tsconfig.spec.ts` : `{ ..  , "typeAcquisition": { "include": [ "jest" ] }, .. ,"exclude": [ "jasmine" , "cypress"] }` 


## Snapshot Testing
* https://izifortune.com/snapshot-testing-angular-applications/
* `expect(myObjectUnderTest).toMatchSnapshot()` 
    * *myObjectUnderTest* can be an entire Angular Component
    * *myObjectUnderTest* can also be a normal JSON 
    * in both cases , the snapshot in *./\_\_snapshots\_\_/my.component.spec.ts.snap* contains a text representation of the object under test
    * will be created if not yet there
    * will be compared if already there, and the test will fail, if different from the initial run
    *  directrys ./*\_\_snapshots\_\_*/ need to be commited to  git
* matchig any Date or Number  with arg to `toMatchSnapshot()` using `expect.any(Date|Number|..)`: 
```typescript
   expect(user).toMatchSnapshot({
    createdAt: expect.any(Date),
    name: 'Bond... James Bond',
  });
```
* `expect.any(constructor)`
## Manual Mocks
* https://dev.to/codedivoire/how-to-mock-an-imported-typescript-class-with-jest-2g7j
* either do it in the `*.spec.ts` or in a subdir called *\_\_mocks\_\_*:
* in `*.spec.ts` : 
```typescript
jest.mock('./../services/my.service', () => {
  return {
    MyService:jest.fn().mockImplementation(() => {
      return {
        getFavoriteMovies: () => {
          const mockObject = ["2001: A Space Odysey", "Star Wars", "Star Trek"]
          return from([mockObject]).pipe(delay(100)); // the mock return
        },
      };
    })
  };
```
* or just `jest.mock('./../services/my.service')` in the `*.spec.ts` and the actual mock in subdir called *\_\_mocks\_\_* just like the actual object, but returning whats needed for the test
* to get the mock-object, call `const mockedmyService = mocked(my.service, true)` , by using `import { mocked } from 'ts-jest/utils'`)
## Spying on methods
```typescript
const mockGetMyObjects = jest.fn().mockImplementation(()=>{
    const mockObject = ["2001: A Space Odysey", "Star Wars", "Star Trek"]
    return from([mockObject]); 
  }
)
jest.mock('../services/my.service', () => {
  return {
    MyService : jest.fn().mockImplementation(() => {
       return {getMyObjects: mockGetMyObjects};
    })
  }
});
...
  expect(mockGetMyObjects).toHaveBeenCalledTimes(1)
```
## run only a view tests:
--testPathPattern:
 `ng test -- --testPathPattern=bookmarks-container`

## end-to-end mit jest/cucumber