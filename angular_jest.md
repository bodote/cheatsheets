# Angular and jest (instead of karma/jasmine)
## Links
* https://www.amadousall.com/how-to-set-up-angular-unit-testing-with-jest/  however : 
  * You *FIRST* need to `ng new my-app` and then `npm install jest jest-preset-angular @types/jest --save-dev` If I do it as described in the blog then I end up with NO jest or jest-preset-agnular in `package.json`
  * and SECOND : you need to delete the first line of `tsconfig.json` because thats a comment that jest does not like in this json und would give you a Unexpected token / in JSON at position 0 error
  * If you would do exactly what is describe in the blog , npx jest would fail
* https://github.com/ahasall/angular-jest-demo.git
* https://github.com/thymikee/jest-preset-angular.git
## Debug jest errors: 
* Run jest with `--detectOpenHandles`. This will show you what is actually wrong with your test spec. For me, there were missing Angular Material imports and service mocks. You may be prompted to add the BrowserAnimationsModule, as Nambi eluded to in his answer

## Differences to karma/jasemine
* `TestBed.configureTestingModule()` needs to have everything the app.module.ts has , that is more then jasemine needs
* run test with `npx jest` or even `npx jest --detectOpenHandles`

