[Inhalt](angular_toc.md)
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
## Reactive Forms
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
## Observables vs Promise
* observable kann immer wieder date liefern, ein Promise nur einmal
* Observable-`subscribe()`  entspricht Promise`then()`
* [Observables vs Promise](https://www.google.com/search?q=observables+vs+promises)
* Promises werden auch in Unittests verspätet ausgeführt

## Observable und Fehlerbehandlung
Siehe auch [hier](https://blog.angular-university.io/rxjs-error-handling/).
Let's remember that the subscribe call takes three optional arguments:
1. a success handler function, which is called each time that the stream emits a value
2. an **optional** error handler function, that gets called only if an error occurs. This handler receives the error itself
3. an **optional** completion handler function, that gets called only if the stream completes
```typescript
myObservable$.subscribe(
            data => console.log('normal data response', data),
            err => console.log('Error', err),
            () => console.log('completed.')
        );
``` 


