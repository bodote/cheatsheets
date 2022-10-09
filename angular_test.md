[Inhalt](angular_toc.md)

# Angular Unit Tesing

## spec - file

- Unittest für `mytypescript.ts`: `mytypescript.spec.ts` , ebenso `mycomp.compentent.ts` -> `mycomp.compentent.spec.ts`
- wird automatisch ausgeführt wenn `ng test` aufgerufen wird.
- Beispiel:

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

## nur einige Tests ausführen

- wenn `describe` oder `it` umbenannt wird in `xdescribe` oder `xit` werden diese **NICHT** ausgeführt
- umgekehrt wenn `describe` oder `it` umbenannt wird in `fdescribe` oder `fit` werden **NUR** dieseausgeführt
- `ng test --include='**/search*.ts'` **or**
- `ng test --include relative_or_absolute_path_here`




## Testbed:

```typescript
import { async, TestBed } from "@angular/core/testing";

beforeEach(async(() => {
  TestBed.configureTestingModule({
    declarations: [SelfComponent],
    providers: [],
    imports: [],
  }).compileComponents();
}));
beforeEach(() => {
  let fixture = TestBed.createComponent(AppComponent);
});
```

- eigentlich ist das `async()` und der Aufruf zu `.compileComponents();` überflüssig , weil wir `webpack` benutzen. Wirklich essentiell ist nur
  ein beforeEach() block statt 2:

```typescript
import { async, TestBed } from "@angular/core/testing";
beforeEach(() => {
  TestBed.configureTestingModule({
    declarations: [SelfComponent],
    providers: [],
    imports: [],
  });
  let fixture = TestBed.createComponent(AppComponent);
  let component = fixture.componentInstance;
});
```

- `fixture` ist vom Typ `ComponentFixture<MyComponent>` kann man entwerder von der Root - `AppComponent` per `createComponent` erzeugen , oder auch von `MyComponent` die ich gerade testen will.
- die eigentliche Test-Component bekommt man mit `component = fixture.componentInstance`
- und `fixture.nativeElement` ist das HTML-Root-Element dieser Component.
- oder `fixture.debugElement`, welches ein Wrapper um `nativeElement` ist
- in `configureTestingModule` -> `declarations` und `imports` nur das aus `@NgModule({...` abschreiben, was auch wirklich gebraucht wird, evtl ist aber **mindestens** die zu Testenden Component, sonst werden im Template die `*ngFor` etc. nicht aufgelöst
- `TestBed.get()` was deprecated as of Angular version 9. To help minimize breaking changes, Angular introduces a new function called `TestBed.inject()`, which you should use instead. For information on the removal of TestBed.get(), see its entry in the Deprecations index.

## Router testing

### Router in Testbed durch Stub ersetzten:

- um Router und andere dependencies bereitzustellen, kann man in `TestBed.configureTestingModule({..` statt eines normalen `provider: [MyService]` auch ein oder mehrere Provider - Objekte verwenden:

```typescript
TestBed.configureTestingModule({
  providers: [{provide: Router, useClass:MyRouterStub }],
  ...
```

- Dazu muss dann im Unittest selbst 2 Stub-Classen mit den Methoden `navigate` (für `Router`) und `params` (für ActivatedRouter ) erzeugen.
- spyOn() für `navigate` erzeugen und die Methode aufrufen, die das Routing verursachen soll, dann prüfen ob der spy wirklich aufgerufen wurde:

```typescript
let router = TestBed.get(Router);
let spy = spyOn(router, "navigate");
component.myMethod(); // which should call the Routers navigate()
```

wenn man die routes selbst testen will (z.B. in `app.routes.spec.ts`), dann
in `app.module.ts` wird dann noch dies hier nötig:
`export const routes: routes : Routes...` ist nur nötig, wenn man die routes dann in einem unittest test will .
statt nur `let routes...`

### ActivatedRoute durch Stub ersetzen um ActivatedRoute.subscribe() in ngOnInit() testen:

- die zu testenden Componente macht das hier:

```typescript
export class UserDetailsComponent implements OnInit {
  constructor(private router: Router, private route: ActivatedRoute) { }
  userId : string;
  ngOnInit() {
    this.route.params.subscribe(p => {
      if (p['id'] === 0)  // das hier soll getestet werden
        this.router.navigate(['not-found']);
    });
  }
```

- ich will testen, ob bei "id"=0 auch wirlich die Route "not-found" navigiert wird. Dazu muss ich in this.route.params das `Observable` gegen ein `Subject` austauschen, damit ich hier einen test-Wert einspeisen kann, daher erzeuge ich Stubs für `Route` und `ActivatedRoute`
- der Test sieht dann so aus:

```typescript
class RouterStub {
  navigate(params) {
    // muss nur existieren, macht aber nix
  }
}
class ActivatedRouteStub {
  private subject = new Subject();

  push(value) {
    // helper-methode um den test-Wert einzuspeisen
    this.subject.next(value);
  }
  get params() {
    // wird in der ngOnInit() der zu testenden Componente verwendet,
    // hier wird jetzt ein "Subject()"" welches ja auch ein Observable ist, "untergeschoben"
    return this.subject.asObservable();
  }
}
describe("MyComponent", () => {
  let component: MyComponent;
  let fixture: ComponentFixture<MyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [MyComponent],
      providers: [
        { provide: Router, useClass: RouterStub },
        { provide: ActivatedRoute, useClass: ActivatedRouteStub },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyComponent);
    component = fixture.componentInstance;
  });
  it("should redirekt the user to the not-found page when an invalid ID has passed", () => {
    let router = TestBed.inject(Router);
    let spy = spyOn(router, "navigate");
    let route: ActivatedRouteStub = <ActivatedRouteStub>(
      (<any>TestBed.inject(ActivatedRoute))
    );

    route.push({ id: 0 }); // the "wrong" id , we want to test
    fixture.detectChanges();

    expect(spy).toHaveBeenCalledWith(["not-found"]);
  });
});
```

**_ACHTUNG_** `fixture.detectChanges()` nicht zu früh aufrufen, sonst wird auch `ngOnInit()`gleich mit aufgerufen und der später hinzugefügte "spy" funktioniert nicht mehr richtig!

### RouterOutlet oder andere Directives prüfen

- RouterTestingModule im TestBed importieren:

```typescript
TestBed.configureTestingModule({
      imports: [
        RouterTestingModule
      ],
```

- Element mit Directive suchen: `debEle = fixture.debugElement.queryAll(By.directive(RouterOutlet))` findet **alle** elemente mit dem `router-outlet` **WENN** `RouterTestingModule` auch importiert wird
- einfache prüfung ob existiert:`expect(debEle).not.toBeNull() // korrespondiert mit <router-outlet>, import from '@angular/router';`

### RouterLink finden

```typescript
let debEleArray = fixture.debugElement.queryAll(
  By.directive(RouterLinkWithHref)
); // alle routerLink elemente
let index = debEleArray.findIndex(
  (de) => de.attributes["routerLink"] === "/my-link"
);
```

- dann nur noch den Index auf `> -1` prüfen

## EventEmitters

- `EventEmitters` sind `Observable`s , daher kann man auf sie `subscribe`n
- Componente initialisieren, dann auf die `@Output()` - Property (die ja vom Typ `EventEmitter` ist) subscriben. (diese Output-property mit Typ EventEmitter wird in der Componente mit `myOutputEventEmmiter.emit()`
- wir können dann testen indem wir im testcode auf die Output-property / Typ EventEmitter - property `subscribe()`, innerhalb des subscript und den Wert 'merken' und ...
- **außerhalb** der `subscribe` funktion dann testen ob das von subscribe übergebene argument (= der eigentliche Wert des Events , z.B. direkt eine Zahl oder ein String) der Erwartung entspricht

## Test einfache Observables

- use `EMPTY` `from 'rxjs'`
- Fake-Observable : entweder mit `spyOn.and.callFake(()=>{})` der noch einfacher direkt `spyOn.and.returnValue(from())` , wobei `rxjs.from()`ein Observable erzeugt.
- beachte bei `from()` : muss als Argument eine Array sein, und wenn der Observable<Type> ohnehin schon auf einem Array basiert, braucht es hier ein Array von Array

```typescript
mytestArray: MyType[] = whatever
testObs = from([mytestArray])
```

- simuliere, dass ein Service Fehler im Observable wirft: `spyOn(service, 'add').and.returnValue(throwError(error))`

## Testing innerhalb des "subscribe()" eines Observables

- über geben statt kein Argument das Argumente `done` , subscribe das `Observable` und teste innerhalb des `subscribe` das Ergebnis. Dann rufe `done()` auf:

```typescript
it("a test", (done) => {
  myobservable.subscribe((data) => {
    expect(data).not.toBeNull();
    expect(data).toBe(whatever);
    done(); // dont forget!!
  });
});
do_something_to_trigger_next_element_in_observable();
```

- optional: setzte im `beforeEach()`einen timeout:

```typescript
beforeEach((done) => {
  setTimeout(() => {
    done();
  }, 1000);
});
```

## Asynchrones Testen

- siehe auch oben bei Testing innerhalb des "subscribe()" eines Observables
- ansonsten bei [Asynchrones Testen](https://codecraft.tv/courses/angular/unit-testing/asynchronous/)
  - **entweder** async and whenStable zusammen verwenden, was aber nur mit Promises , nicht mit Observable funktioniert
  - **ODER** die `done()` funktion verwenden wie oben beschrieben
  - **oder** mit `.pipe(toArray()).subscribe(... done() )` arbeiten z.B:
```typescript
      it('should be get a book as the search result on loadSearchs-action', (done) => {
      effects.searchBooks$.pipe(toArray()).subscribe((actions) => {
        expect(actions.length).toBe(1);
        expect(actions).toEqual([
          loadSearchsSuccess({ searchResults: [bookEntity] })
        ]);
        done();
      });
    });
```

## Testing Forms

- Initialisiere : `component= new MyComponent(new FormBuilder)`
- prüfe, ob die Felder da sind: `expect(component.form.contains('myField')).toBeTruthy()`
- check Validation:

```typescript
let control = component.form.get("myField");
control.setValue("any ok-value");
expect(control.valid).toBeTruthy();
control.setValue("any not-ok-value");
expect(control.valid).toBeFalsy();
```

## Spy um Services zu mocken und von Service abhängige Components zu testen

- Der Service muss gemockt werden mit `spyOn()`, **Vorsicht**: nicht die Classe als erstes Argument sondern eine Objektinstanz der ServiceKlasse! sonst kommt die Meldung: "Das Argument vom Typ "string" kann dem Parameter vom Typ "never" nicht zugewiesen werden"
- verwende `spyOn(clazz-instance,'methodName')` um einen spy aus einer Objekt-instanz einer Klasse `clazz-instance` zu erzeugen.
- verwende `jasmine.createSpyObj()` um einen spy ganz ohne echte Klasse zu erzeugen
**wichtig** "tape" ist nur ein name für das spy-objekt, hat nix mit dem klassennamen zu tun !
```typescript
tape = jasmine.createSpyObj("tape", {
  controls: {
    rewind: function () {
      return true;
    },
    forward: function () {
      return true;
    },
  },
});
spyOn(tape.controls(), "rewind");
```

## Testen von Services selbst

- nutze `import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing'`
- import `HttpClientTestingModule`im Testbed, (und prüfe ob im app.module.ts auch `HttpClientModule` importiert wird, weil das wird die Anwendung unabhängig von den unittest benötigen )
- der Testinstanz ins Testbed inizieren : `http = TestBed.inject(HttpTestingController);`
- prüfen ob eine bestimmte URL aufgerufen wurde: `req = http.expectOne(environment.favoriteUrl)` weitere Details werden so in `req` gespeichert
- prüfe weitere Details des Requests mit `req.request.method`

```typescript
const req: TestRequest = http.expectOne(environment.myUrl); // expect that the URL is called once, and get the request itself for further testing
expect(req.request.method).toEqual("GET"); // check that req is the right method

//Resolve the request by returning a body plus additional HTTP information
// (such as response headers) if provided. If the request specifies an expected body type,
// the body is converted into the requested type. Otherwise, the body is converted to JSON by default.
req.flush(testContent);

http.verify(); //Verify that no unmatched requests are outstanding.

//check if we really get what we wanted
expect(returnContentFromRestService).toEqual(
  expectedReturnContentFromRestService
);
```

- dabei kann die `flush()` methode des `TestRequest` eine große Zahl unterschiedlicher Argumentkombinationen verarbeiten, details siehe [hier](https://angular.io/api/common/http/testing/TestRequest). Beispiele:

```typescript
testRequest.flush(anyObjekt)
testRequest.flush(anyObjekt[]) // or Array of any
testRequest.flush(msg, { status: 404, statusText: 'Not Found' }); //Return error
testRequest.flush(msg, { status: 500, statusText: 'Internal Server Error' }); //Return error
testRequest.flush({}, { status: 500, statusText: 'Internal Server Error' });
```

- mehrfache hintereinander folgende Restaufrufe lassen sich simulieren, in dem man immer wieder einen **neuen** `TestRequest` erzeugt und auf diesem wieder `flush()` oder eben `error()` aufruft.
- ein einmal ge`flush`-ter `TestRequest` lässt sich **NICHT** wieder verwenden.

## echter Testserver:

[beeceptor](https://beeceptor.com/console/bodote)

## Test mit Dep-Injection und ngOnInit()

- wenn man von einer Component das `ngOnInit()`testen will darf man `fixture.detectChanges()` **nicht** zu früh aufrufen, sondern muss zuerst den `spyOn` erzeugen und die Dep-Injektion statt finden lassen.

## Finding HTML-Elements in DOM

### mit querySelector (ist aber Browserspezifisch)

für z.B. "<span class="vote-count">some content</span>``:

```typescript
hTMLElement = fixture.nativeElement;
hTMLElement.querySelector("span, .vote-count")?.textContent;
```

würde den Text-inhalt `some content` finden

- querySelectors argument ist ein String mit eine Kommaseparierte Liste von class-attributen und/oder html-tags
- querySelector suche nach einem Element mit einer ID `<p id="demo">..` : `querySelector("#demo")`

### mit debugElement (besser für Unittests) ...

#### und query(By...)

- verwende `fixture.debugElement.query()` und im Query als Argument z.b: `By.css('h1')` oder `By.css('.someclass')` oder kombiniere `By.css('h1.someclass')`
- `By.css('.someclass, .someother')` findest elemente mit `.someclass` **oder** `.someother`
- see [here](https://github.com/puddlejumper26/blogs/issues/4) oder [here](https://sodocumentation.net/de/protractor/topic/1524/css-selektoren)
- By.css('.glyphicon, .glyphicon-menu-up')
- verwende statt `.myclazz` oder `id` besser : [data-* attribute](https://developer.mozilla.org/en-US/docs/Learn/HTML/Howto/Use_data_attributes) , kann auch für integrationstests verwendet werden
- verwendet in `*ngFor` loops: 
```html
<ng-container *ngFor="let book of foundBooks; let i = index">
            <tr
              [attr.data-id]="'foundBook-' + i"
```
```typescript
bookFound0 = fixture.debugElement.query(
        By.css('[data-id="foundBook-0"]')
      );
```

## benutze "nativeElement" wenn ein debugElement gefunden wurde.

- `debugElement.nativeElement` (welches eigentlich vom Typ `HTMLElement`ist)
- verwende z.B. `debugElement.nativeElement.innerText` oder `.textContent`um den Text-Content zu bekommen,
- innerText returns the **visible** text contained in a node, while textContent returns the full text. As a result, innerText is much more performance-heavy: it requires layout information to return the result.
  innerText is defined only for HTMLElement objects, while textContent is defined for all Node objects.

### ... und query(By.directive())

- verwende `fixture.debugElement.query()` und im Query als Argument z.b: `By.directive(DirectiveComponent)`

## Directive - Objekte von debugElement abfragen

- `directive = mydebElement.injector.get(MyDirective)` aber findet nur MyDirective direkt im mydebElement, **NICHT** in dessen childs

## test trigger events

- finde ein `DebugElement` , wie oben, darauf kann man dann `triggerEventHandler('eventName',$event)` aufrufen. Wobei `$event` auch null sein darf

## Code Coverage

`ng test --code-coverage`

## async, await and Promise

about [async, await and Promise](https://blog.logrocket.com/async-await-in-typescript/)

## testing Promises

- da Promises verspätet ausgeführt werden

# Integration tests mit selenium und protractor

ist eingebaut. einfach `ng e2e` starten
