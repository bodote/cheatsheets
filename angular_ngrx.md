# Angular ngrx
## Installation:
```bash
ng add @ngrx/store
ng add @ngrx/store-devtools
```
* https://chrome.google.com/webstore/detail/redux-devtools installieren
## Scaffolding (Gerüst) für store config für ein Modul:
* `ng generate store auth/Auth --module auth.module.ts`  wenn `auth` der pfad zum auth.module.ts ist. (evtl. muss hier noch korrigiert werden: `import { environment } from '../../../environments/environment';` )
* ` StoreModule.forFeature('auth', fromAuth.reducers)` hier ist `auth` der name für den Bereich , zu sehen z.B. im NgRx Store DevTools -> State -> Raw  
## Store Service API
* store injekten: im Constructor füge hinzu: `private store : Store<State>`, wobei 
  * `import { Store } from "@ngrx/store";` , und 
  * `State` der Typ ist der im `app.module.ts` in ..`imports...` bei ` StoreModule.forRoot(reducers, { metaReducers }),`als erster parameter von `forRoot()` definiert ist. 
  * hier also `reducers`, welches seinerseits ein `interface` ist das von app.modules.ts aus gesehen in `app/reducers/index.ts` exportiert wurde: `export interface State {` (**NICHT** das , das in `app/auth/reducers/index.ts` definiert wurde! )
  * am besten in `app/reducers/index.ts` : `State` -> `AppState` umbenennen weil das ist der "global" - State !
* der `Store<State>` ist auch ein `Observable`
* der injectete `store` kann nicht direkt geändert werden, sondern Änderungen werden über dessen `dispatch(Action)` publiziert.
## ngRx Actions 
* plain JavaScript Object für `dispatch()` mit einem `type` und (meistens) einem `payload` child.
  * can be a command, or
  * öfter noch ein event , der eine Änderung auf level der Componente mitteilt.
* der Store selbst entscheidet, was mit einer Action konkret zu tun ist. defaultmäßig passiert erstmal gar nicht damit, auch der State wird nicht geändert.
* Vorteil: lose coupling 
* Empfehlung: statt plain JavaScript Object besser ein explizit factory methode  definieren und dann typesave verwenden. Die factory methode  verwendet ihrerseits `createAction()` aus `@ngrx/store`:
```typescript
export const login = createAction(
  "[Login Page] " //source, i.e.: "type" of the action
  ,props<{user:User}>()// props() from "@ngrx/store"
)
//and in the compoentent itself , which will deliver the user profile that needs to be dispached to the store:
...
 someObservable.pipe().(tap(user => dispatch(login({user:user})))).subscribe(user => ...)
```
## action-types.ts
for some reason, Actions are imported and re-exportet in `app/auth/action-types.ts`. Seems to be merily a typescript - trick to get command-line - completion . 

## Reducer
* defines a function, what the store should do in response to Actions the are dispached to it.
* this function can be created from  a factory-function called `createReducer()` from `@ngrx/store`, possibly using the `on()` function :
```typescript
export const authReducer = createReducer(
  initialState,
  on(AuthAction.login,(state,action) => {
    return {
      user: action.user
    }
  })
);
```
* this `authReducer` then must be added to the *.module.ts - file for this module, in the `@NgModules` -> `imports:` -> `StoreModule.forFeature('auth', authReducer)`
## Use state for deciding on what to show in the UI
* inject the ~~state~~ **store** (Type `private store : Store<AppState>`) in the component via constructor, the type parameter either the root of the state or some child state. the root State is usually exportet in `./reducers/index.ts` as a plain interface





