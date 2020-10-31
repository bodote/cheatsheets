# spec - file 
* Unittest für `mytypescript.ts`: `mytypescript.spec.ts` , ebenso `mycomp.compentent.ts` -> `mycomp.compentent.spec.ts`
* wird automatisch ausgeführt wenn `ng test` aufgerufen wird.
* Beispiel:
```typescript
describe('TodoFormComponent', () => {
  var component: TodoFormComponent; 
  beforeEach(() => {
    component= new TodoFormComponent(new FormBuilder)
  });
  it('should contain a name and email field', () => {
    expect(component.form.contains('name')).toBeTruthy()
  });
...
});
```
ebenso `beforeAll()` `afterEach()` und `afterAll()`

# Testbed:
```typescript
import { async, TestBed } from '@angular/core/testing';

beforeEach(async(() => {
  TestBed.configureTestingModule({
    declarations: [ SelfComponent ],
    providers: [],
    imports: []
  })
  .compileComponents();
}));

it('...', () => {
  let fixture = TestBed.createComponent(AppComponent);
});
```
* in `configureTestingModule` -> `declarations` und `imports` nur das aus `@NgModule({...` abschreiben, was auch wirklich gebraucht wird, evtl ist aber **mindestens** die zu Testenden Component, sonst werden im Template die `*ngFor` etc. nicht aufgelöst
