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
## Rules for Subjects
* do not share Subjects outsite the file , where it is created.
* instead share the `Subject.asObservable()` 
* but Subjects have not unsubscribe logic.

## other Subject Types
* `BehaviorSubject(initVal)` emits always its initial value. And emits also the last value to late subscribers.
* `AsyncSubject` does **not** emit anything until it calles `complete()` but late subscribers also get the last value **even after** `complete()`
* `ReplaySubject` does **replay** all values **even after** `complete()`


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
* falls es ein benanntes top-Level FormGroupName gibt: 
```typescript
this.myFormGroup.get('myTopLevelElement').value // alle Felder der Gruppe
this.myFormGroup.get('myTopLevelElement').value.myControl1 //nur ein bestimmtes Feld
```
* falld nicht , dann einfach: 
```typescript
this.myFormGroup.value // alle Felder der Gruppe
this.myFormGroup.value.myControl1 //nur ein bestimmtes Feld
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
* Observable startet erst, wenn wirlich eine subscription vorliegt <-> Promise startet gleich.
## Promise (oder normal call back ) zum Observable machen
```typescript
new Observable(observer => {
    fetch(url) // this is the Promise
      .then(response => {
        return response.json(); // get something from the promise
      })
      .then(body => {
        observer.next(body) // push it to the observer 
        observer.complete() // call this, whenever no new data will be pushed to the observer
      })
      .catch(err => {
        observer.error("fetch:catch in promise " + err)
      })
  });
```
* oder viel einfacher: `from(promise)`
* **Vorsicht** der `.catch` vom `fetch`-`Promise` wird nicht bei einem 400er oder 500er error gerufen, sondern nur wenn einen kompletten Verbindungsabbruch gab. die 400er oder 500er error  muss man ganz am Angang im `.then(response=>.. )` mit abfangen

## Quelle eines Observable mit mehreren subscribern teilen
...um zu verhindert, dass z.B. Daten vom Server mehrfach geholt werden (also cache - funktion)
* in der `pipe()` ein `shareReplay()` einfügen : `...pipe(map(...),shareReplay())` ; beachte das **Komma** in der `pipe()`

## .pipe() debuggen mit tap()
* `...pipe(tap(()=>console.log("something"),...,...)`
* `tap()`kann aber auch für andere Side-Effects verwendet werden, z.B. fürs Routing:
```typescript
   tap(val => {
          console.log("login tap passed:", val);
          this.router.navigateByUrl('/courses')
        })
```


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
# timing helpers
## normal JavaScript
* `setInterval(()=> { doSomething }, 1000)`  alle 1000 ms was tun
* `setTimeout(()=>{ doSomething },3000)` erst nach 3000 ms etwas tun 
## RXJS
* `interval(1000)` emitting increasing int numberst starting with 0 each 1000 ms 
* `of(1,2,3,).pipe(concatMap(item => of(item).pipe(delay(1000))))` verzögert JEDES elemet für sich um 1000ms 
* im Gegensatz zu `of(1,2,3,).pipe(delay(1000))` welches alle 3 Elemeter später, dann aber sehr schnell hintereinander ausliefert

## Operators for Observables
### observable pipe(concatMap())
```typescript
obs1$.pipe(concatMap(itemFromObs1 => {
      obs2$ = this.saveCourse(itemFromObs1)
      return obs2$ 
    }))
    .subscribe((itemFromObs2) => console.log("itemFromObs2"+itemFromObs2))
```
hängt leitet itemFromObs2 von itemFromObs1 ab und serialisiert alles, d.h. beide callbacks werden nacheinandern ausgeführt und überholen sich nicht. also alle Observables warten aufeinandern und überlappen sich nicht
###  observable pipe(mergeMap())
```typescript
obs1$.pipe(mergeMap(itemFromObs1 => {
      obs2$ = this.saveCourse(itemFromObs1)
      return obs2$ 
    }))
    .subscribe((itemFromObs2) => console.log("itemFromObs2"+itemFromObs2))
```
alle callbacks für alle Observable werden kombiniert zu einem Observable und alle so schnell wie möglich ausgeführt, können sich also überlappen , warten NICHT aufeinander.
###  observable pipe(exhaustMap())
```typescript
obs1$.pipe(exhaustMap(itemFromObs1 => {
      obs2$ = this.saveCourse(itemFromObs1)
      return obs2$ 
    }))
    .subscribe((itemFromObs2) => console.log("itemFromObs2"+itemFromObs2))
```
alle callbacks für alle Observable , die kommen befor das erste Observable beendet ist werden ignoriert, können sich also NICHT überlappen , warten auch NICHT aufeinander, sonder alles was zu schnell vorn reinkommt bevor es hinten fertig ist , wird einfach ignoriert.
###  observable pipe(switchMap())
```typescript
  this.lessons$ = searchTerms$.pipe(
      switchMap(searchTerm => this.loadLessons(searchTerm))
    )
```
wenn neue `searchTerms` in der pipe erscheinen, werden der laufende `loadLessons()`-request sofort gecancelt (also unsubscribed) und sofort ein neuer `loadLessons()`-request erzeugt. 

### concat Observables
wenn inital zuerst ein Observable verwendet werden soll, dannach aber ein anders (was z.b auf Userinput reagiert) , beide aber den gleich Output haben und ein Element der UI befüllen sollen , dann kann man die einfach zusammenhängen mit: 
`this.lessons$ = concat(initialLessons$,searchedLessons$)`
wobei in `this.lessons$` zuerst die Elemente aus `initialLessons$` erscheinen, und dannach die aus `searchedLessons$`

### combineLatest(obs1$,obs2$)
When any observable emits a value, emit the last emitted value from each
### startWith
um einen Stream (z.B. von einer Eventquelle) zu initialisieren mit einem einzelnen startwert

### withLatestFrom(obs2$)
combines `obs$1` and `obs2$` : `obs$1.pipe(withLatestFrom(obs2$),map(([ent1,ent2])=>...),..` so that the `next` operator after will deliver pairs of observable-entities 
### forkjoin(obs1$,obs2$,..)
* last value from all the observables, but not before all observable have been completed.

### throttle and throttleTime
`throttle(()=>intervall(500))`==`throttleTime(500)` limitiert die events pro zeit und lässt z.B. nur den ersten Event pro 500ms durch , alle folgenden werden  wird ignoriert bis 500ms vergangen sind.
### debounceTime
im Gegensatz zu `throttleTime(500)` , wartet `debounceTime(500)` ob 500ms lang KEIN Event mehr reinkommt, erst dann wird der letzte Event ausgegeben. Alle events vorher, die schneller als 500ms Abstand hatten werden ignoriert.
### throttleTime vs debounceTime
* `throttleTime` gives the **first** value of many that are send too fast. but sends values at the requested min-speed, even if the origial stream is still too fast. 
* `debounceTime` gives the **last** value **after** the sending of values has slowed down enough.


### delay und delayWhen()
verzögert die Eventweitergabe einfach, bei delayWhen() abhängig von einem 2. Observable. z.B. : 
`delayWhen(()=>intervall(500))`. delayWhen time shifts each emitted value from the source Observable by a time span determined by another Observable. 

### Custom Operators from ngRx
* select() =  same as map(),distinctUntilChanged()

## Observable von einer Eventquelle (clicks, tastatur)
* events from parameters of the Route:
```typescript
constructor(private route: ActivatedRoute) { }
const paramMap$ = this.route.paramMap
```
* from Forms : 
`this.form.valueChanges$``
* from mouse events: `fromEvent(this.saveButton.nativeElement,'click')`
* from Keyboard: `fromEvent(this.searchInput.nativeElement, 'keyup')`

## Error Handling
### catchError()
3 Möglichkeiten:
* catchError in the pipe(..., catchError()) and emit some sort of static predefined replacement/alternativ instead of the real thing.
* catchError in the pipe, log to console and then rethrow it
* do nothing.
### finalize()
wird in jedem Fall ausgeführt.

### retry()
innerhalb einer `.pipe(...,shareReplay())` mit z.B. einem , am Schluss ein : 
 `.pipe(...,shareReplay(),retryWhen(error => error.pipe(delayWhen(()=>timer(500)))))`
