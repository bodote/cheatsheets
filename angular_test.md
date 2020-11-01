# Angular Unit Tesing
## spec - file 
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

## Testbed:
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

## EventEmitters
* `EventEmitters` sind `Observable`s , daher kann man auf sie `subscribe`n 
* Componente initialisieren, dann auf die `@Output()` - Property (die ja vom Typ `EventEmitter` ist) subscriben.
* **innerhalb** der `subscribe` funktion dann testen ob das von subscribe übergebene argument (= der eigentliche Wert des Events , z.B. direkt eine Zahl oder ein String) der Erwartung entspricht 
