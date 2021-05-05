# Course Notes ngrx
## after authentication is implemented: 

### actions 
* add actions to the courses module Actions for loadAllCourses und allCoursesLoaded
  * loadAllCourses triggered from the routerResolver, so before the router show the homescreen, no parameter
  * action name : "[source] what it does", 
  * allCoursesLoaded: parameter: the list of courses . Source : the Courses Load - Effect
* implement the action-types (importing the actions and exporting it )
### router/Resolver<any> for loading courses
* implement a service that implements `Resolve<any>`
* implement the resolve() method
* plug it in to the courses.module router config by adding a `resolve:{courses:CourseResolver}` to it
  * AND as a "provider" , since it is a injectable Service
* if courses are not yet in the store, then dispach the loadCourses Action
* otherwise ignore it
* the resolver returns an observable
* avoid multiple triggers (from multipel threads)of the resolvers , make a semaphore into the resolver service 
### implement Effect for the action loadAllCourse
* make `@Injectable() CoursesEffects`,  and plug it into `courses.module.ts`:
  * add `EffectsModule.forFeature([CoursesEffects])` into the `imports`
* because this should trigger a side effect, we use an Effect instead of an Reducer to react to the Action.
* but we don't use tap() but `mergeMap()`, since the `action` should be mapped to the `courses[]` 
* Then we <strike>emit</strike> **return** another action. this is acomplished by the following `map()` to map the coursesArray to a new Action (allCoursesLoaded) . Only THAT Action should be processed by a reducer to produce a new state
### implement the reducer
* ...that saves the courses from the loalAllCourse - Effect to the store
* using `EntityAdapter<MyClass>`
* add the Reducer to mymodule.module.ts to `imports`:  `StoreModule.forFeature('myClassName', myReducer)`
### use of ngrx EntityState<MyClass>
to save the courses from the httpservice in the Store using `EntityAdapter<MyClass>`
### replace reload in home component by selectors
* new file *xxx.selectors.ts*, define all selectors by using the `createFeatureSelector()`  and `createSelector()` methods
* add the compareCourses method pointer to `createEntityAdapter<MyClass>()`

### avoid fetching the courses from backend multiple times
* add a flag to the `CoursesEntity`
* add condition to resolver so that it only loads if not already loaded.
  * for this createSelector for the new flag of the `CoursesEntity`
* set the loaded flag to true in the coursesReducer in `on(CoursesActions.allCoursesLoaded... `

### remove the loading indicator for the courses
Spinner only in app.component.ts and app.component.html, between router events NavigationStart and NavigationEnd . Because this is where the httpcall to the backend is performed.

### Editing Entity Data
* define an action using `Update<MyClass>` (which in turn contains a `Partial<MyClass>` field.)
* the courses edit dialogue should be refactored to dispatch an action instead of saving the data directly to the backend.
* add a `on()` to the existing reducer that immediately change the store in memeory
* change the onSave() method accordingly
### Saving the Data
* add a effect to the existing effects , that does the backend call

## after authentication is implemented, alternative: 
try the same as above, but now instead using Actions, Effects, etc. use ngrx- Data 
### using ngrx Data
* exception from the default behaviour: the actual courses data are in a json-element called "payload", instead transmitted directly with Courses[] as Json.
* therefore use ngrx Data with a Custom **MyDataSerivce** class that extends the `DefaultDataService<MyClass>` 
* but we still need a resolver for the Router
* as soon as the Router-Resolver takes care of getting the data from the backend,
refactor the "home" Component to use the new Custom **MyDataSerivce** instead of the old "CoursesHttpService" 
* fix the wrong sorting by adding a `{sortComparer: compareCourses}` to `EntityMetadataMap` - configuration
### adding CRUD operations
* rewrite `onSave()` for modified course details using `CourseEntityService` instead CourseHttpService
* change update behaviour to `optimistic` instead of pessimisic 
* mind the `mode: 'create' | 'update';` 
* rewrite `add()` and `delete()`
### implementing ngrx EntityData for course lessons
* add new Entity to `EntityMetadataMap`
* implement a LessionEntityService just like **MyDataSerivce** above and -as it is a service- plug it into courses.module.ts- `providers`
* rewrite the course component to use CourseEntityService and LessonEntityService instead of *HttpService. to get exactly one course from the courses array , use "find()" instead of "filter()"
* combine `courses$` and `lessons$` with a `pipe()`
* initially load only the first page of lessons
* load more , if the user clicks "more"
* fix change detection error in console using "delay(0)" for the load spinner





