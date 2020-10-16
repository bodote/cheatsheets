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
* verwende die `property`in der  `\*.component.hlml` der Component

# Dependency Injection
```TypeScript
@Injectable({})
```
# add Bootstrap support
copy from (https://getbootstrap.com/docs/4.5/getting-started/introduction/):
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

