# after authentication is implemented: 

## actions
* add actions to the courses module Actions for loadAllCourses und allCoursesLoaded
  * loadAllCourses triggered from the routerResolver, so before the router show the homescreen, no parameter
  * action name : "[source] what it does", 
  * allCoursesLoaded: parameter: the list of courses . Source : the Courses Load - Effect
* implement the action-types (importing the actions and exporting it )
## router/Resolver<any> for loading courses
* implement a service that implements Router<any>
* implement the resolve() method
* plug it in to the courses.module router config by adding a `resolve:{courses:CourseResolver}` to it
  * AND as a "provider" , since it is a injectable Service
* if courses are not yet in the store, then dispach the loadCourses Action
* otherwise ignore it
* the resolver returns an observable
* avoid multiple triggers (from multipel threads)of the resolvers , make a semaphore into the resolver service 
## implement Effect for the action loadAllCourse
* make `@Injectable() CoursesEffects`,  and plug it into `courses.module.ts`:
  * add `EffectsModule.forFeature([CoursesEffects])` into the `imports`
* because this should trigger a side effect, we use an Effect instead of an Reducer to react to the Action.
* but we don't use tap() but `mergeMap()`, since the `action` should be mapped to the `courses[]` 
* Then we <strike>emit</strike> **return** another action. this is acomplished by the following `map()` to map the old action to the new one. Only THAT Action should be processed by a reducer to produce a new state
## implement the reducer
* ...that saves the courses from the loalAllCourse - Effect to the store
* using `EntityAdapter<MyClass>`
* add the Reducer to mymodule.module.ts to `imports`:  `StoreModule.forFeature('myClassName', myReducer)`
## use of ngrx EntityState<MyClass>
to save the courses from the httpservice in the Store
## replace reload in home component by selectors


