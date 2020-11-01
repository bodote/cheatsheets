<!--ts-->


<!--te-->
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
foundItem = myArray.find(item => { item.property === whatever })
``` 
`fundItem`is the first match  or `unfefined` if nothing found

# JSON ist gleichzeitig gültiges TypeScript- Objekt
Beispiel:
```typescript
this.checkoutForm = this.formBuilder.group({
      customer: this.formBuilder.group({
        firstname: [''],
        lastname: [''],
        email: ['']
      })
```
