# Neues Projekt, neue Componenten
```bash
ng new <my-project>
ng generate <class|component|module|service> whatever
```
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

## Routing
### Routes definieren
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

### Routes verwenden
* `<router-outlet/>` in app.component.html einfügen, an die Stelle, wo dann die unterschiedlichen Components eingefügt werden sollen. Und zwar statt einer statischen Referenz zu eine Componente mit dem `selector` der `@Component` einer `whatever.component.ts`
* ab dann kann man Links mit dem `path`einer Route verwenden, das bewirkt dann eine erneutes Rendern der referenzierten Component mit ggf. den zusätzlich Route-Parametern (also `?param=3` an den Link angehängt) 
### Links zu Routes
* `<a routerLink="/path" >link</a>` oder  mit parameter: `<a routerLink="/path/3" >link</a>` oder
* optinal: CSS Style für `routerLinkActive` definieren (mit  bootstrap z.B.:  `<a routerLink="/path/3" routerLinkActive="nav-link active" >link</a>`)
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


# Typescript Specials
## Getter , Setter:
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

# Browser Tricks
## Cache problem bei statischen Inhalten vermeiden
füge ein `?v=2` hinzu z.B. `<link rel="icon" type="image/x-icon" href="favicon.ico?v=2">` oder dann `3`, `4`etc. 
