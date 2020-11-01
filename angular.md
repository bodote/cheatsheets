<!--ts-->
   * [Neues Projekt, neue Componenten](#neues-projekt-neue-componenten)
      * [Neue Child-Component in Parent - Component:](#neue-child-component-in-parent---component)
      * [neuen Service Client - Side](#neuen-service-client---side)
      * [ClientService "GET"](#clientservice-get)
      * [A new Component using a Service](#a-new-component-using-a-service)
   * [Routing](#routing)
      * [Routes definieren](#routes-definieren)
         * [Beispiel Routes:](#beispiel-routes)
      * [Routes verwenden](#routes-verwenden)
         * [Links zu Routes](#links-zu-routes)
         * [lesen der Route/Parameter  Component](#lesen-der-routeparameter--component)
      * [Routes aus Typescript Methoden heraus aufrufen](#routes-aus-typescript-methoden-heraus-aufrufen)
      * [Debug Routing](#debug-routing)
   * [Dependency Injection](#dependency-injection)
   * [add Bootstrap support](#add-bootstrap-support)
   * [einfache app.component.html für Bootstrap](#einfache-appcomponenthtml-für-bootstrap)
   * [localen server starten](#localen-server-starten)
   * [html - template](#html---template)
      * [table mit bootstrap und fallunterscheidungen](#table-mit-bootstrap-und-fallunterscheidungen)
   * [EventBinding](#eventbinding)
   * [Pagination](#pagination)
      * [Installation](#installation)
      * [Verwendung](#verwendung)
      * [Anpassen der Pagesize vom User](#anpassen-der-pagesize-vom-user)
   * [RXJS-Spezials](#rxjs-spezials)
      * [Observable &gt;&gt;Subjects&lt;&lt;](#observable-subjects)
   * [Reactive Forms](#reactive-forms)
   * [Typescript Specials](#typescript-specials)
      * [Getter, Setter:](#getter-setter)
      * [String to Number conversion:](#string-to-number-conversion)
      * [String interpolation with Varables:](#string-interpolation-with-varables)
      * [Array find](#array-find)
   * [JSON ist gleichzeitig gültiges TypeScript- Objekt](#json-ist-gleichzeitig-gültiges-typescript--objekt)
   * [Browser Tricks](#browser-tricks)
      * [Cache problem bei statischen Inhalten vermeiden](#cache-problem-bei-statischen-inhalten-vermeiden)

<!-- Added by: bodo, at: So  1 Nov 2020 17:40:54 CET -->

<!--te-->
# Neues Projekt, neue Componenten
```bash
ng new <my-project>
ng generate <class|component|module|service> whatever
```
z.B.:
`ng generate component components/search` legt im Unterverzeichniss "components" die search.component.ts , plus spec,html,css an 
## Neue Child-Component in Parent - Component: 
component mit namen generieren, dann in Parent-component den `selector` name der Child-component in die \*.html der Parent - Component einfügen

## neuen Service Client - Side
mit CLI wie oben erzeugen.
Dann in `app.module.ts` den `HttpClientModule` importieren von `@angular/commons/http` UND auch in `@NgModules({})` in das  `import`- Array reinschreiben.
Dann noch den neuen Service in `@NgModules({})`-`providers`-Array aufnehmen

Im ClientService selbst, dann baseUrl festlegen, und im `constructor` den `HttpClient`injekten.

## ClientService "GET"
definiere ein "get\<whatever\>" Funktion, die ein Observable vom Typ "\<whatever\>" zurückgibt.
      `return this.httpClient.get<GetResponse>(this.baseUrl).pipe(map(response => ... ))`
wo bei noch "Observable" von "rxjs" und "map" von "rxjs/operators" zu importieren ist, und ein interface "GetResponse" zu definieren ist, welchers das Json-Format der Response definiert (so , wie sie das Backend eben vorgibt)
Dadurch funktioniert dann das mapping mit "map(response => ... )" von der kompletten Response-Json auf den Teil, der davon interessiert.

## A new Component using a Service
* Component soll `OnInit` implementieren, macht das `ng generate...` auch schon automatisch.
* Injecte den  Service im den `constructor()` der Component.
* definiere eine `property` zum speichern des Ergebnisses des Services
* in der ngOnInit() dann das `Observable<>` des Serivces geben lassen und darauf `subscriben` 
* die `subscribe`-Lambda weißt dann die definierte `property` zu.
* verwende die `property`in der  `\*.component.hlml` der Componen

# Routing
## Routes definieren
* im `app.module.ts` dort in `@NgModule` das `RouterModule` importieren  dabei auch gleich `.forRoot()` mit dem Array der möglichen `Routes` aufrufen.
* `Routes` werden als Array definiert mit `path` plus `component`, oder mit `path` , `redirect` und `pathMatch`
### Beispiel Routes:
```typescript
let routes : Routes = [
  { path: 'category/:id', component:  ProductListComponent},
  { path: 'category', component:  ProductListComponent},
  { path: '', pathMatch: 'full', redirectTo: '/products'},  
  { path: '**', pathMatch: 'full', redirectTo: '/products'}  
]
```

## Routes verwenden
* `<router-outlet/>` in app.component.html einfügen, an die Stelle, wo dann die unterschiedlichen Components eingefügt werden sollen. Und zwar statt einer statischen Referenz zu eine Componente mit dem `selector` der `@Component` einer `whatever.component.ts`
* ab dann kann man Links mit dem `path`einer Route verwenden, das bewirkt dann eine erneutes Rendern der referenzierten Component mit ggf. den zusätzlich Route-Parametern (also `?param=3` an den Link angehängt) 
### Links zu Routes
* `<a routerLink="/path" >link</a>` oder  mit parameter: `<a routerLink="/path/3" >link</a>` oder
* optinal: CSS Style für `routerLinkActive` definieren (mit  bootstrap z.B.:  `<a routerLink="/path/3" routerLinkActive="nav-link active" >link</a>`)
* besser KEIN `href` verwenden, weil das dann ein kompletten Reload der ganzen Anwendung auslöst, alle Caches löscht etc. 

### lesen der Route/Parameter  Component
* übergebe dem constructor die `ActivatedRoute` , speichere das in `route`
* in `ngOnInit()` subscribe auf `this.route.paramMap.subscribe( () => {} )`
* In der Component kann man dann **innerhalb** des subscribe-lambda statements prüfen ob der parameter überhaupt da ist mit `this.router.snapshot.paramMap.has('<param-name>')`
* mit `this.router.snapshot.paramMap.get(<param-name>)` kann man den Parameter **innerhalb** des subscribe-lambda lesen:
```typescript
this.route.paramMap.subscribe( () => {
   if (this.router.snapshot.paramMap.has('<param-name>'))
       param = this.router.snapshot.paramMap.get(<param-name>)
   else param = 1
   //call Service mit zusätzlichem Argument `param`
   this.myservice.getWhatever(param).subscribe(...)
} )
```
## Routes aus Typescript Methoden heraus aufrufen
wenn man nicht direkt einen Link auf die Route hat, dann kann man auch einen Event mit einer Methode der Componente der Klasse verbinden und dann dort  programmatisch die Route aufrufen
* den constructor initialisieren mit : `constructor(private router:Router) { }``
* die Route direkt aufrufen: `this.router.navigateByUrl(urlString)` wobei z.B. `urlString` = `"/search/"+searchstring` wenn das Endergebniss so ausschaun soll: `http://localhost:4200/search/searchstring` 

## Debug Routing
in `app.module.ts` einfügen: ` RouterModule.forRoot(routes, { enableTracing: true }) ],`

# Dependency Injection
```TypeScript
@Injectable({})
```
# add Bootstrap support
copy from [here](https://getbootstrap.com/docs/4.5/getting-started/introduction/):
```html
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<!-- Bootstrap CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" 
      integrity="sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2" crossorigin="anonymous">
``` 
nach `<root>/index.html` und vergesse nicht die andere `viewport` - Zeile zu löschen

# einfache `app.component.html` für Bootstrap
```html
<div class="container"><h1 class="mb-3 mt-3">header</h1></div>
```
dazu muss `index.html` den Tag  `<app-root/>` verwenden und `app.component.ts` muss in `@Component` einen `selector` mit gleichem Namen `app-root` haben und die `templateUrl` muss auf `app.component.html` verweisen 

# localen server starten
`ng serve --open`

# html - template 
## table mit bootstrap und fallunterscheidungen
```html
<table class="table table-hover table-sm">
  <thead class="thead-dark">
    <tr>
      <th>First name</th>
    </tr>
  </thead>
  <tbody>
    <tr *ngFor="let salesPerson of salesPersonList"
      [ngClass]=" salesPerson.volume < 6000 ? 'bg-warning': 'table-success' ">
          <td> {{ salesPerson.firstName }}</td>
          <td> {{ salesPerson.volume | currency:'EUR' }}</td>
          <td>
            <div *ngIf="salesPerson.volume >= 6000 ; else myElseBlock">yes</div>
            <ng-template #myElseBlock>no</ng-template>
          </td>
    </tr>
  </tbody>
</table>
```
# EventBinding
* Browser/Mouse/Tastatur-Event ruft Methode in zugehöriger Componente: `<sometag (eventname)="typescript-Method-call"></..>`:
* reading Input aus einem Feld (z.B. INput) `<sometag #refMarker ..... (eventname)="method(refMarker.value) " >`
* reading Input aus einem **ANDEREN** Feld funktioniert genau so:
```html
<sometag #refMarker ..... (eventname)="method(refMarker.value) " >
<someothertag ..... (othereventname)="method(refMarker.value) " >
```
* wichtige [events](https://developer.mozilla.org/en-US/docs/Web/Events) `keyUp.enter` oder `keyDown.enter` , `click`, `focus` und sein Gegenteil: `blur` , letzeres wir ausgelöst , wenn der User eine anderes Element hinklickt also gerade noch der Focus war. Weiterhin `dblclick`

# Pagination
## Installation
* `ng add @angular/localize` und `npm install @ng-bootstrap/ng-bootstrap`
* Import in `app.module.ts` : `@NgImport .... NgbModule `
* Siehe [Spring Data REST](https://www.baeldung.com/rest-api-pagination-in-spring#spring-data-rest-pagination), oder [hier](https://bezkoder.com/spring-boot-pagination-filter-jpa-pageable/)
## Verwendung
* im Angular Service muss das `interface GetResponseWhatever{ _enbedded: {..} }` um das `page:{ size: number, ...}` erweitert werden, oder was immer spring hier mit `..&page=1&size=10` zurückgibt 
* `pageNumber, pageSize, totalElements` als Properties der Angular-Component hinzufügen damit diese dann beim Angular-Service aufruf übergeben werden können.
* umgekehrt muss in der gleichen Componente aus dem Result vom Service-Aufruf dann die 3 parameter wieder gelesen werden. d.h. der `GetResponseWhatever` vom  `Observeable<GetResponseWhatever>` muss jetzt vom Service auch zurückgegeben werden, statt nur die eigentliche Entity-Klasse  
* **Achtung** Spring ist zero-based und Angular ist "1"-based
* in dem HTML-Template verwende im Template dann `<ngb-pagination>` gemäß [ng-bootstrap](https://ng-bootstrap.github.io) [Beispiel](https://ng-bootstrap.github.io/#/components/pagination/overview):
```html
<ngb-pagination
  [(page)]="page"
  [pageSize]="pageSize"
  [maxSize]="5" <!-- zeigt max 5 pages im page-selector  an --> 
  [boundaryLinks]="true"
  [collectionSize]="items.length"></ngb-pagination>
```
* **2-Way** Data-Binding für die parameter verwenden mit `[(page)]="pageNumber"` wenn `pageNumber` die Property in der Componente ist.
* definiere Binding-call von `pageChange` zu einer Methode in der Componente die dann den Service aufruft.
## Anpassen der Pagesize vom User
* Variante 1: Verwende [ng-bootstraps Dropdown](https://ng-bootstrap.github.io/#/components/dropdown/examples) `<div ngbDropdown placement="top-right" class="d-inline-block">...` und bei jedme Button definieren den `(click)`- Event: `(click)="updatePageSize(5)` etc.
* Variante 2: verwende html - `<select>...<option>` und dazu den `(change)` - Event mit `(change)="updatePageSize($event.target.value)"` 

# RXJS-Spezials
## Observable >>Subjects<< 
damit kann man Properies "wrappen" damit andere Objekte sich auf deren Änderung "subscriben" können: 
```typescript 

mynumber = new Subject<number>()// just the wrapper
actualNumbervalue = 0 ; // that actual number
....
actualNumbervalue= 5 // neue Wert 
this.mynumber.next(actualNumbervalue) // an alle Subscriber versendet
```
so bekommen alle subscriber von "actualNumbervalue" den neuen Wert mitgeteilt
# Reactive Forms
* Installation, Configuration: in `apps.module.ts` hinzufügen: `imports: [.., FormsModule, ReacitveFormsModule, ..]`
* `FormGroup` in Component als non-private Field, im constructor einen `FormBuilder` injecten und im `ngOnInit()` mit  `FormBuilder.group()` eine oder mehrere `FormGroup`s hierarchisch als "anonyme" Json-Structuren definieren, auf unterster Ebene dann die eigentlichen `FormControl`s :
```typescript
myFormGroup: FormGroup
constructor (private formBuilder: FormBuilder )
ngOnInit(){
  this.myFormGroup = this.formBuilder.group({
    myTopLevelElement:  this.formBuilder.group({
      myControl1: ['defaultValue'],
      myControl2: [''] //empty default value
    })
  })
}
```
* `onSubmit()` methode definieren
```typescript
this.myFormGroup.get('myTopLevelElement').value // alle Felder der Gruppe
this.myFormGroup.get('myTopLevelElement').value.myControl1 //nur ein bestimmtes Feld
```

* HTML-Template bildet die hierarchische Groupierung 1:1 ab :
```html
<form [formGroup]="myFormGroup" (ngSubmit)="onSubmit()">
    <div formGroupName="myTopLevelElement" class ="form-area" >
      <div class="row">
        <label>a Label</label>
        <input formControlName="myControl1" type="text">
      </div>
    </div>
  <div class=".." >
    <button type="submit" class="btn btn-info">Buttonname</button>
  </div>  
</form>
```


# Typescript Specials
## Getter, Setter:
```typescript
class ...
private _fullName: string = "";
  get fullName(): string { return this._fullName; }
  set fullName(newName: string) {this._fullName = newName;}
...
//Verwendung:
employee.fullName = "Bob Smith";
```
## String to Number conversion: 
`let number = +"my number as string"`, also mit einem `+`Zeichen
## String interpolation with Varables:
```typescript
console.log(`The size is ${ value }`);
```
## Array find
```typescript
foundItem = myArray.find(item => { item.property === whatever })
``` 
`fundItem`is the first match  or `unfefined` if nothing found

# JSON ist gleichzeitig gültiges TypeScript- Objekt
Beispiel:
```typescript
this.checkoutForm = this.formBuilder.group({
      customer: this.formBuilder.group({
        firstname: [''],
        lastname: [''],
        email: ['']
      })
```


# Browser Tricks
## Cache problem bei statischen Inhalten vermeiden
füge ein `?v=2` hinzu z.B. `<link rel="icon" type="image/x-icon" href="favicon.ico?v=2">` oder dann `3`, `4`etc. 
