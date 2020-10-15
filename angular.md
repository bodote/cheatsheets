# Neues Projekt, neue Componenten
```bash
ng new <my-project>
ng generate <class|component|module> whatever
```
# Service client-side
Import in app module `HttpClientModule`

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

# localen server starten
`ng serve --open`
