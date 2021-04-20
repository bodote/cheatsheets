[Inhalt](angular_toc.md)

# Angular basic building blocks
## Dependency Injection
```TypeScript
@Injectable({})
```
## Services as Providers
* einen `my.service.ts` muss man entweder im `app.modules.ts` der `providers` Liste hinzufügen (Singleton) oder direkt in der Componente , dort dann in `@Component({ providers: [],...` wo er gebraucht wird (Nicht-Singleton ) 

## add Bootstrap support
copy from [here](https://getbootstrap.com/docs/4.5/getting-started/introduction/):
```html
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<!-- Bootstrap CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.5.3/dist/css/bootstrap.min.css" 
      integrity="sha384-TX8t27EcRE3e/ihU7zmQxVncDAy5uIKz4rEkgIXeMed4M0jlfIDPvg6uqKI2xXr2" crossorigin="anonymous">
``` 
nach `<root>/index.html` und vergesse nicht die andere `viewport` - Zeile zu löschen

## einfache `app.component.html` für Bootstrap
```html
<div class="container"><h1 class="mb-3 mt-3">header</h1></div>
```
dazu muss `index.html` den Tag  `<app-root/>` verwenden und `app.component.ts` muss in `@Component` einen `selector` mit gleichem Namen `app-root` haben und die `templateUrl` muss auf `app.component.html` verweisen 


## html - template 
### table mit bootstrap und fallunterscheidungen
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
* Dabei wird mit `ng-template` ein Template definiert, welches aber normalerweise **NICHT** angezeigt wird. 
* Damit es sichtbar wird, muss es 
  1. erstens explizit mit einem `#someMarker` markiert werden und 
  2. über z.B. ein `*ngIf` aufgerufen werden, so wie hier oben im Beispiel
* statt aus einem `*ngIf` kann es auch über eine Observable async-Pipe `myObservable$ | async as myValues; else myElesBlock` angesprochen werden
## NG-CONTAINER
`<ng-container>` kann statt einem `<div>` verwendet werden, z.B. wenn man keine class - attribute braucht aber `*ng??` direktiven verwenden will

## Input Properties
* in der zur Wiederverwendung gedachten Child-Componente: definiere eine `@Input('my-prop-name') myProperty = anyClass_or_string_or_number`
* in der Parent-Componenten verwende : `<my-child-component [my-prop-name]="anything_from_Parent_class">`
* auf diese weise kann man von der Parent-Componente Werte an die Child-Componente übergeben.

## Events
### Binding
* Browser/Mouse/Tastatur-Event ruft Methode in zugehöriger Componente: `<sometag (eventname)="typescript-Method-call"></..>`:
* reading Input aus einem Feld (z.B. INput) `<sometag #refMarker ..... (eventname)="method(refMarker.value) " >`
* reading Input aus einem **ANDEREN** Feld funktioniert genau so:
```html
<sometag #refMarker ..... (eventname)="method(refMarker.value) " >
<someothertag ..... (othereventname)="method(refMarker.value) " >
```
* wichtige [events](https://developer.mozilla.org/en-US/docs/Web/Events) `keyUp.enter` oder `keyDown.enter` , `click`, `focus` und sein Gegenteil: `blur` , letzeres wir ausgelöst , wenn der User eine anderes Element hinklickt also gerade noch der Focus war. Weiterhin `dblclick`
### Observable von Eventquelle:
statt `<button class="mat-raised-button mat-primary" #saveButton (click)="clickHappend(saveButton)">`
```html
<button class="mat-raised-button mat-primary" #saveButton >
```
```typescript
@ViewChild('saveButton', { static: true }) saveButton: ElementRef;
....
fromEvent(this.saveButton.nativeElement,'click').subscribe(do something)
```


### Event Emitters und Output Properties
* in der zur Wiederverwendung gedachten Child-Componente: definiere eine `@Output('my-event-name') myoutput = new EventEmitter()` Property.  
* damit kann man jetzt in einer beliebigen Methode der Componente einen event erzeugen: `myoutput.emit(any_Object_or_Number)`
* in der Parent-Componente kann man jetzt im html-Template `<my-child-component (my-event-name)="callMethodInParent($event)">` wobei `$event` gleich `any_Object_or_Number` aus der `emit()`methode ist.
* **Interessant:** Event Emitters sind gleichzeitig auch Observables, auf die `subscribe`en kann


## Pagination
### Installation
* `ng add @angular/localize` und `npm install @ng-bootstrap/ng-bootstrap`
* Import in `app.module.ts` : `@NgImport .... NgbModule `
* Siehe [Spring Data REST](https://www.baeldung.com/rest-api-pagination-in-spring#spring-data-rest-pagination), oder [hier](https://bezkoder.com/spring-boot-pagination-filter-jpa-pageable/)
### Verwendung
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
### Anpassen der Pagesize vom User
* Variante 1: Verwende [ng-bootstraps Dropdown](https://ng-bootstrap.github.io/#/components/dropdown/examples) `<div ngbDropdown placement="top-right" class="d-inline-block">...` und bei jedme Button definieren den `(click)`- Event: `(click)="updatePageSize(5)` etc.
* Variante 2: verwende html - `<select>...<option>` und dazu den `(change)` - Event mit `(change)="updatePageSize($event.target.value)"` 

## Directives vs Components
* Components are a subclass of Directives
* Directives haben keine eigene View , sondern können exisitierende DOM-Elemente ändern (events abfangen und aussehen verändern) 
* es kann mehrere Directivs in einem Dom-Element geben.
* Components sind mindestens ein Dom - Element und können noch Kind-Elemente habe.
* [Google Directivs vs Components ](https://www.google.com/search?q=directives+vs+components+angular)
## Browser Tricks
### Cache problem bei statischen Inhalten vermeiden
füge ein `?v=2` hinzu z.B. `<link rel="icon" type="image/x-icon" href="favicon.ico?v=2">` oder dann `3`, `4`etc. 

## localen server starten
`ng serve --open`
## globale Variablen (z.B. für unterscheidung Prod / Test Environment)
* in dir `environment/envirnoment.prod.ts` oder eben `environment/envirnoment.ts` für test ist eine globale Struktur `environment` definiert, in die man beliebige weitere Elemente ergänzen kann

## update packages
* `rm packages-lock.json`
* `npm cache verify`
* `npm install --force protractor` um ein bestimmtes Packet neu zu installiern und den rest zu updaten 
## Perform a basic update to the current stable release of the core framework and CLI by running the following command.
*  `ng update @angular/cli @angular/core`

## Livecycle Hooks

* A lifecycle hook that is called after Angular has fully initialized a component's view. Define an `ngAfterViewInit()` method to handle any additional initialization tasks.
* A lifecycle hook that is called after Angular has initialized all data-bound properties of a directive. Define an `ngOnInit()` method to handle any additional initialization tasks.

## Query Parameter of Routes
```typescript
//Example: /app?param1=hallo&param2=123
param1: string;
param2: string;
constructor(private route: ActivatedRoute) {
    console.log('Called Constructor');
    
}
ngOnInit() {
  this.route.queryParams.subscribe(params => {// für die query params
        this.param1 = params['param1'];
        this.param2 = params['param2'];
    });
  this.route.paramMap.subscribe .. // für die route params
}
```

## @ViewChild()
* inject a child component used in the template of this class in this class in order to **call methods on it** :
```html
<color-sample>..</color-sample>
```
when `color-sample` is the component selector for component "ColorSampleComponent"
```typescript
@ViewChild(ColorSampleComponent)
primarySampleComponent: ColorSampleComponent;
```
* just using a child component in the template alone does **not require** the `@ViewChild`. (Only when the typescript part of the component should be used in some way (eg. to change a state in the child))
* to be mostly used in the "ngAfterViewInit" Lifecycle Hook
* works also with plain html element using the '#'-Tag :
```html
<h2 #title>bla</h2>
``` 
```typescript
@ViewChild('title')
titleHeader: ElementRef;
```

## Debugging:
add the `debugger` statement to the source code an then in the browser: click on the call - stack , to see the source code

## using *ngIf with an boolean Observable 
```typescript
private observable$ : Observable<boolean>
``` 
```html
<div *ngif="observable$ | async">
```
