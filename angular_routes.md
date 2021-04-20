# Routing
## Routes definieren
* im `app.module.ts` dort in `@NgModule` das `RouterModule` importieren  dabei auch gleich `.forRoot()` mit dem Array der möglichen `Routes` aufrufen.
* `Routes` werden **außerhalb** `export class AppModul` aber innerhalb von `app.module.ts`  als Array definiert mit `path` plus `component`, oder mit `path` , `redirect` und `pathMatch`, oder per `path` + `loadChildren:` für **Lazy-Loading**
### Beispiel Routes 
* ..die direct in `app.modules.ts` (oder einem anderen Modul) bei den  `Imports` und **außerhalb** der `AppModule` class definiert werden:
```typescript
export const routes: routes : Routes = [
  { path: 'category/:id', component:  ProductListComponent},
  { path: 'category', component:  ProductListComponent},
  { path: '', pathMatch: 'full', redirectTo: '/products'},  
  { path: '**', pathMatch: 'full', redirectTo: '/products'}  
]
....
@NgModule({
...
imports: [
  ...
  RouterModule.forRoot(routes,{ enableTracing: false }),
  ...
]
})
```
`export const routes: routes : Routes` ist nur nötig, wenn man die routes dann in einem unittest test will .


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

## Eager vs lazy Loading for Routes
* Default: Eager Loading
* To lazy load Angular modules, use loadChildren (instead of component) in your AppRoutingModule routes configuration as follows:
```typescript
const routes: Routes = [
  {
    path: 'items',
    loadChildren: () => import('./items/items.module').then(m => m.ItemsModule)
  }
];
``` 
Also be sure to remove the `ItemsModule` from the `AppModule`. For step-by-step instructions on lazy loading modules, continue with the following sections of this page.
* And do **NOT** include this Module in `app.modules.ts->@NgModule->imports:`