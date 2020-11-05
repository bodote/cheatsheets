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
# Observables vs Promise
* observable kann immer wieder date liefern, ein Promise nur einmal
* Observable-`subscribe()`  entspricht Promise`then()`
* [Observables vs Promise](https://www.google.com/search?q=observables+vs+promises)
* Promises werden auch in Unittests verspätet ausgeführt

