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

