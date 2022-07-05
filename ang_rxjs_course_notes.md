# Course Notes ngjs
## Subjects
### Implment a Store Service
* src/app/common/store.service.ts make the service injectable
* should contain a courses$ Observable from a Subject
* init store via app.component.ts -> onInit()
* use Store from home.compontent
* test it
* add new select...() methods in the store , and refactor home.comp to use it
### implment save operation
* course dialoge: save button 
* save it to in-memory , emit new value to all subscribers,  then asynchronouse save to backend 
* make the Store.save() method also a Observable (fromPromise(fetch())), so the subscription in course-dialoge can close the dialogue or show an error in the console
* copy the in-memory courses and replace the new data, 
* call backend
* refactor course.compoent to use the store

