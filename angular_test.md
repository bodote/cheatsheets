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

