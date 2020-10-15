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
definiere ein "get<whatever>" Funktion, die ein Observable vom Typ "<whatever>" zurückgibt.
      `return this.httpClient.get<GetResponse>(this.baseUrl).pipe(map(response => ... ))`
wo bei noch "Observable" von "rxjs" und "map" von "rxjs/operators" zu importieren ist, und ein interface "GetResponse" zu definieren ist, welchers das Json-Format der Response definiert (so , wie sie das Backend eben vorgibt)
Dadurch funktioniert dann das mapping mit "map(response => ... )" von der kompletten Response-Json auf den Teil, der davon interessiert.


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
