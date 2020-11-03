[Inhalt](angular_toc.md)

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
beforeEach( () => {
  let fixture = TestBed.createComponent(AppComponent);
});
```
* eigentlich ist das `async()` und der Aufruf zu `.compileComponents();` überflüssig , weil wir `webpack` benutzen. Wirklich essentiell ist nur 
ein beforeEach() block statt 2:
```typescript
import { async, TestBed } from '@angular/core/testing';
beforeEach(() => {
  TestBed.configureTestingModule({
    declarations: [ SelfComponent ],
    providers: [],
    imports: []
  })
   let fixture = TestBed.createComponent(AppComponent);
   let component = fixture.componentInstance
});
``` 
* `fixture` ist vom Typ `ComponentFixture<MyComponent>` kann man entwerder von der Root - `AppComponent` per `createComponent` erzeugen , oder auch von `MyComponent` die ich gerade testen will.
* die eigentliche Test-Component bekommt man mit `component =  fixture.componentInstance` 
* und `fixture.nativeElement` ist das HTML-Root-Element dieser Component.
* oder `fixture.debugElement`, welches ein Wrapper um `nativeElement` ist
* in `configureTestingModule` -> `declarations` und `imports` nur das aus `@NgModule({...` abschreiben, was auch wirklich gebraucht wird, evtl ist aber **mindestens** die zu Testenden Component, sonst werden im Template die `*ngFor` etc. nicht aufgelöst
* `TestBed.get()` was deprecated as of Angular version 9. To help minimize breaking changes, Angular introduces a new function called `TestBed.inject()`, which you should use instead. For information on the removal of TestBed.get(), see its entry in the Deprecations index.

## EventEmitters
* `EventEmitters` sind `Observable`s , daher kann man auf sie `subscribe`n 
* Componente initialisieren, dann auf die `@Output()` - Property (die ja vom Typ `EventEmitter` ist) subscriben.
* **außerhalb** der `subscribe` funktion dann testen ob das von subscribe übergebene argument (= der eigentliche Wert des Events , z.B. direkt eine Zahl oder ein String) der Erwartung entspricht 

## Testing Observables
* über geben statt kein Argument das Argumente `done` , subscribe das `Observable` und teste innerhalb des `subscribe` das Ergebnis. Dann rufe `done()` auf: 
```typescript
it('a test', done =>  {
    myobservable.subscribe( data => {
      expect(data).not.toBeNull()
      expect(data).toBe(whatever)
      done() // dont forget!!
    })
})
do_something_to_trigger_next_element_in_observable()
```
* optional: setzte im `beforeEach()`einen timeout:
```typescript
beforeEach(done => {
    setTimeout(() => {
      done()
    },1000)
  });
```

## Testing Forms
* Initialisiere :  `component= new MyComponent(new FormBuilder)` 
* prüfe, ob die Felder da sind: `expect(component.form.contains('myField')).toBeTruthy()`
* check Validation: 
```typescript
let control = component.form.get('myField')
control.setValue('any ok-value')
expect(control.valid).toBeTruthy()
control.setValue('any not-ok-value')
expect(control.valid).toBeFalsy()
```

## Spy um Services zu testen
* verwende `spyOn(clazz,'methodName')` um einen spy aus einer echten Klasse `clazz` zu erzeugen.
* verwende `jasmine.createSpyObj()` um einen spy ganz ohne echte Klasse zu erzeugen 
```typescript
tape = jasmine.createSpyObj('tape', 
    {
        'controls': {
            rewind: function() {
                return true;
            },
            forward: function() { 
                return true;
            }
        }
    }
);
spyOn(tape.controls(), 'rewind');
```
## Test Observables
* use `EMPTY` `from 'rxjs'`
* Fake-Observable : entweder mit `spyOn.and.callFake(()=>{})` der noch einfacher direkt `spyOn.and.returnValue(from())` , wobei `rxjs.from()`ein Observable erzeugt.
* simuliere, dass ein Service Fehler im Observable  wirft: `spyOn(service, 'add').and.returnValue(throwError(error))`

## Finding HTML-Elements in DOM
### mit querySelector (ist aber Browserspezifisch) 
für z.B. "<span class="vote-count">some content</span>``: 
```typescript
hTMLElement = fixture.nativeElement;
hTMLElement.querySelector('span, .vote-count')?.textContent
```
würde den Text-inhalt `some content` finden
* querySelectors argument ist ein String mit eine Kommaseparierte Liste von class-attributen und/oder html-tags
* querySelector suche nach einem Element mit einer ID `<p id="demo">..` : `querySelector("#demo")`
### mit debugElement (besser für Unittests)
* verwende `fixture.debugElement.query()` und im Query  als Argument z.b: `By.css('h1')` oder `By.css('.someclass')` oder kombiniere `By.css('h1.someclass')`
* see [here](https://github.com/puddlejumper26/blogs/issues/4) oder [here](https://sodocumentation.net/de/protractor/topic/1524/css-selektoren)

## Router testing

## Template testing

## Code Coverage
`ng test --code-coverage`

## async, await and Promise 
about [async, await and Promise](https://blog.logrocket.com/async-await-in-typescript/)

