# Typescript Specials
## Getter, Setter:
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
## String interpolation with Varables:
```typescript
console.log(`The size is ${ value }`);
```
## Array find
```typescript
const foundItem = myArray.find(item => { return item.property === whatever })
//or
const foundItem = myArray.find(item => item.property === whatever )
``` 
`fundItem`is the first match  or `unfefined` if nothing found

## JSON ist gleichzeitig gültiges TypeScript- Objekt
Beispiel:
```typescript
this.checkoutForm = this.formBuilder.group({
      customer: this.formBuilder.group({
        firstname: [''],
        lastname: [''],
        email: ['']
      })
```
## filtering arrays (plain JavaScipt)
* `Array.prototype.filter()`
```javascript
const words = ['spray', 'limit', 'elite', 'exuberant', 'destruction', 'present'];
const result = words.filter(word => word.length > 6);
console.log(result);
// expected output: Array ["exuberant", "destruction", "present"]
```

## Javascript spread operator / 3 dots operator
```typescript
const bill = {
    ...adrian,
    fullName: 'Bill Gates',
    website: 'https://microsoft.com'
};
``` 
* nimmt das `adrian` object, welches die gleich Struktur haben sollte wie das `bill` object , kopiert zuerst `adrian` nach `bill` und ersetzt dann noch `fullName` und `website`mit den angegebenen werten.
* oder auch : 
```typescript
const courses[id2] = {
        ...courses[id1],
        ...changes 
    };
```  
wenn z.B. id1=1 wäre nimmt es das `courses[1]`- objekt und copiert es , dannach kopiert es dann noch die einzelnen werte von  `changes` 
# Event von HTML-Elementen type-sicher verarbeiten:
`(event.target as HTMLInputElement).value` statt `event.target.value`

# funktional programming
## reduce for arrays
```typescript
const courses=[1,2];
const initialValue = 0;
const result = courses.reduce((val,init)=>val+init, initialValue);
```