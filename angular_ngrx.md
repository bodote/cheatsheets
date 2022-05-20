# Angular ngrx

## Zusammenfassung: 
* Actions
* Reducer : action  -> new state
* Selectors: get state info
* Effects : **SIDE** - Effects von actions , die nicht den state beeinflussen
* EntityState<T>: handles collections  of entities that are saved in the store as state.
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
### filter for ngrx actions 
given `myAction = createAction(..)` then you can  filter in pipe(): `ofType(myAction)` for exactly this action
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
  }),
  //more reducers based on actions can be defined here

);
```
* this `authReducer` then must be added to the *.module.ts - file for this module, in the `@NgModules` -> `imports:` -> `StoreModule.forFeature('auth', authReducer)`
* a reducer **should not** return a modified previous state, it should instead return a new constructed/copied state

### Metareducers
* are executed BEFORE the normal reducers.
* are executed in exactly the order given in `reducers/index.ts` in the Array : `export const metaReducers: MetaReducer<AppState>[] = !environment.production ? [] : [];`
* a metaReducer must 
  * implement: `function logger(reducer: ActionReducer<any>):ActionReducer<any>`
  * this function must return another 2nd anonymous function which in turn takes (state,action)
  * this 2nd function must call the reducer on the `return` statement using the  parameters  (state,action): `return reducer(state,action)`
* Developers can think of meta-reducers as hooks into the action->reducer pipeline. Meta-reducers allow developers to pre-process actions before normal reducers are invoked.
* e.g.: [Using a meta-reducer to log all actions](https://ngrx.io/guide/store/metareducers)

## Use state for deciding on what to show in the UI
* inject the ~~state~~ **store** (Type `private store : Store<AppState>`) in the component via constructor, the type parameter either the root of the state or some child state. the root State is usually exportet in `./reducers/index.ts` as a plain interface
* use the `*ngIf="myobs$ | async"`to decide , whether the UI Element should be there
* take the state (which is already an observable ), `state.pipe(map(state => ...))` and map it to a boolean value and set this to your `myobs$`

## ngrx - specific custom operators for observables
see [angular_rxjs#ngRx Custom Operators for Observables](./angular_rxjs.md#custom-operators-from-ngrx)

## mapping functions with memory for optimizing
* to avoid recalculating a mapping from a state/Store, if the relevant part of the state has not change
* use `createSelector()` from `@ngrx/store` , define this in own seperate source file called `xx.selector.ts`
* `createSelector(selector , projector )` arguments are explained [here](https://ngrx.io/guide/store/selectors#selecting-feature-states) 
* the `selector` is **memoized** for optimal performance, createSelector(selector , projector ) creates a mapping function, that has a memory and only makes the calculation if the arguments are not yet previousely seen. Otherwise it just returns the result from its cache.
* use this in combination : `observable$.pipe(select(myselector))`
### reusing a selector function:
if e.g. the inverse output from selectorFunctionA is needed you can write:
```typescript
export const selectorFunctionB = createSelector(
  selectorFunctionA, 
  resultA => !resultA
)
```
### type safe feature selectors
* to select a specific part of the global state/store in a typesafe way:
`export const authSelector = createFeatureSelector<AuthState>("auth")`  if "AuthState" is the type of the part of the global state/store we want to have selected and "auth" is the name of it. eg.:
```typescript
{
  auth: {
    user: {
      id: "1"
      email: "what@ev.er"
    }
  }
}
```
* result can be used as the first argument to `createSelector(..,..)`

## Effects Module
* an Effect is a Service that gets all Actions, just like Reducers,  and makes a side - effect from some of the Actions (so Actions are normaly filtered first).
* add the `EffectsModule.forRoot([])` to app.module.ts - > imports:
* add the `EffectsModule.forFeature([])` to a submodule-imports
* a Effect is a Service class, so you need `@Injectable()`
* the important part here is the `constructor(private actions$:Actions)` 
  * here you filter for the relevant actions [see Action Filter above](#filter-for-ngrx-actions)
  * you _could_ `subscribe()` and perform your code in the subscribe, but better use `tap()` in the `pipe()` after the `ofType()`-filter, **BECAUSE** the you get type-information for the action (from the `ofType()`-filter before the `tap()`)
### using createEffect() instead of constructor
* using `createEffect()` from `@ngrx/effects` instead of constructor is even better, e.g. because of error handling.
* instead for deriving a new observable from the action$ and subscribe to it, `createEffect()` takes the observable and subscribe to it automaticaly
* an effect that makes an side-effect from actions but dispaches no other actions
```typescript
@Injectable()
export class AuthEffects {
  login$ = createEffect(()=>this.actions$.pipe(
    ofType(AuthAction.login),
    tap(action => localStorage.setItem('myKey', JSON.stringify(action.user)))
  ),
  {dispatch:false})// don't forget this
  constructor(private actions$: Actions) {}
}
```
### mapping to another Action
maps one action to another action , while possibly doing some side effect
```
effectName$ = createEffect(
 () => this.actions$.pipe(
    ofType(FeatureActions.actionOne),
    map((myObject) => FeatureActions.actionTwo(myObject))
  )
);
```
so in this case the last map operation needs to **return** (**not** `dispatch()` ) the new action 

## Reducers vs. Effects
* Reducer gets an Action and changes the State
* Effects gets an Action and does a Sideeffect and dispaches another action or no action
* Effect-Class and Reducer-Class, if you have both Effect-class and Reducer-class react to the same action type, Reducer-class will react first, and then Effect-class
* definition of a Reducer always needs an initial state defined. 
* Effects don't do anything with the State/Store

## Debugging ngrx
* zusammen mit @ngrx/store-devtools (siehe oben)
* debugging the **router states**: add to `app.module.ts` -> `@NgModules` -> `imports` `StoreRouterConnectionModule.forRoot({ stateKey:'router',routerState:RouterState.Minimal})`
  * and add to `app/reducers/index.ts` : `export const reducers: ActionReducerMap<AppState> = {router:routerReducer};` 
* runtime check for **NOT mutating of the store - state**: add in `app.module.ts` -> `@NgModules` -> `imports` : 
```typescript
StoreModule.forRoot(reducers, { metaReducers, 
  runtimeChecks:{
        strictStateImmutability: true,
        strictActionImmutability: true,
        strictActionSerializability: true,
        strictStateSerializability: true}
})
```
## rgrx Entities
### native implementation:
* `entities: {[key:number]:MyClass}`
* for easy search using the key
* additional indices possible using `myIndex:number[]`
### better using ngrx EntitieState<MyClass>
* define a MyClassStore interface that extends `EntitieState<MyClass>` in *myclass.reducers.ts*
* optional add additional fields, eg.: a "loaded"-Flag if needed
* use  `EntityAdapter<MyClass>` by `adapter = createEntityAdapter<MyClass>()`
* `export` the adapter as a `const` (no Class required)
* use adapters methods, e.g. : `adapter.getInitialState()` and `adapter.addAll()` to get or add data.
* add the new reducer to xxx.module.ts to `imports`:  `StoreModule.forFeature('myClassName', myReducer)`
### optional arguments for createEntityAdapter<MyClass>()
* [Docu](https://ngrx.io/guide/entity/adapter)
* `{sortComparer:mySortFunction,...}`comparer for sorting (only the primary index table "ids", **not** the "entities" gets sortet)
* `{selectId:myId,...}` replacement for the "id:" - default property , if we want to use "courseId:" instead for just "id:"
### Update<MyEntity>
 use `Update<T>`  from '@ngrx/entity' as an argument to the action that changes the store.



## rgrx Selectors
* own file courses.selector.ts
* create a `selectMyClassState` for  the "MyClass" feature by `createFeatureSelector<EntityState<MyClass>>` as a **BASE** for alle other selectors
* create the actual selectors (using `selectMyClassState`) by using `createSelector()`
  * **either** use the `EntityAdapter<MyClass>`-select-methods-(pointer!) , we can get from `adapter.getSelectors()` in `createSelector()` 
  *  **OR** : first arg of `createSelector()` is another  Selector for Pre-Selection, 2nd arg is the actual selector, which is a lambda or another method-pointer (which can be derived from `EntityAdapter<MyClass>.getSelectors())`.
* selectors can be stacked on each other : a new selector uses existing selectors as pre-selectors
### getting observable from the store using selectors
* inject the `Store<MyState>` into a class constructor (the Store is already a Injectable instantiatet automaticaly by StoreModule)
* `obs$ = this.store.pipe(select(ngrx-selector-defined-previousely))`  wheras `pipe(select())` is equal to `pipe(map(),distinctUnitilChanged())` 

## NgRx Data
* add `EntityDataModule.forRoot({})` to `app.module.ts` -> `imports:` Config `{}` empty, if no entities are directly associated with app.module but with a child-module
* im child-module: define "const" - configurationVariable of Type `EntityMetadataMap` , with at least one element "MyEntity" (singular)
* inject  the `EntityDefinitionService` to  constructor of the ChildModule class and `register()` this `EntityMetadataMap`- object defined before as a config to the `EntityDefinitionService`
* all entities need to be configured in the `EntityMetadataMap`
* create an MyClassEntityService that extends `EntityCollectionServiceBase` as a *.service.ts file and make it `@Injectable()` 
* its constructor needs a `EntityCollectionServiceElementsFactory` and pass this to `super()`
as 2nd arg, together with the name of the entity `MyClass` as a string as its 1st arg.
* this service needs to be defined as a `provider` and  injected  in `ChildModule`s `constructor()`  
### using MyClassEntityService in the Router Resolver
* the loading from backend and storing in the local store still needs to be plugged in somewhere. Good place is the route - resolver, just like with see [Router Resolver](angular_routes.md#router-resolver)
* inject the `MyClassEntityService` in to the MyResolverService which should implement `Resolve<T>`
* use the "loaded" and the "loading" - flags via `MyClassEntityService.loaded$` Observable  to decide whether the data are already in the store. only if they are not , get the data from the backend via `MyClassEntityService.getAll()` 
* the overwritten `resolve()` - method should return the `MyClassEntityService.loaded$`. In additon , we pipe the Observable and `tap(loaded => ...)` it. In the `tap()` , we get the data from the backend, only if loaded was false
* after the `tap()` , we need to filter for `loaded==true` and then we only interested in the  `first()` element
* first() is mandatory, since the Router otherwise would wait indefinitely , until the Observable is completed
### customize ngrx Data
* to adapt rgrx Data to non-standard-convention data-structure from the backend 
* for this ngrx Data uses another Service: `myclass-data.service.ts` as `@Injectable()`
* create a new **MyDataSerivce** class that extends the `DefaultDataService<MyClass>` 
* inject the `HttpClient` via its `constructor()`
* inject also a HttpUrlGenerator 
* pass them to the `super()` constuctor in addition to to the 'MyClass' -String 
* plug **MyDataSerivce** in to the xxx.module.ts as a provider AND inject it in the ModuleClass constructor AND inject also the generic `EntityDataService` (import from ngrx data) AND inject the **MyDataSerivce** AND register **MyDataSerivce** to `EntityDataService` , 1st arg: 'MyEntityName' (eg. 'Courses') end arg instance of **MyDataSerivce**
* the actual customization is done in the **MyDataSerivce** by overwriting some methods from its base class.
* overwriting the `getAll()`method , we can change the URL and the data-mapping from the default behaviour.
### defining a sort criterium in EntityMetadataMap
* add a `sortComparar` to `EntityMetadataMap`
### CRUD operations
* works via `MyClassEntityService extends EntityCollectionServiceBase<MyClass>` 
* default behaviour for `update()` is "pessimisic" , can be changes in `EntityMetadataMap` config
* new Entities via `add()`; add should be kept pessimistic because IDs might be generated on the backend only. -> therefore the `dialogue.close()` should be performed **after** the data are saved
* `delete()`: is optimistic by default 
### loading entitys with query Params
* use `EntityCollectionServiceBase<MyClass>.entity$.getWithQueriy()`
* `getWithQuery()` args are a json depending on the backend: 
```typescript
getWithQuery({
  'myarg1':whatever.toString(),
  'myarg2':whatever2.toString()
})
```
##  changeDetection: ChangeDetectionStrategy.OnPush
```typescript
@Component({
    selector: 'home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css'],
    changeDetection: ChangeDetectionStrategy.OnPush // should work out of the box
    // with ngrx data , as long as in the html template only async pipes are used for the data
})
export class HomeComponent 
``` 


