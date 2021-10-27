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
console.log(`The size is ${value}`);
```

## Array find

```typescript
const foundItem = myArray.find((item) => {
  return item.property === whatever;
});
//or
const foundItem = myArray.find((item) => item.property === whatever);
```

`fundItem`is the first match or `unfefined` if nothing found

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

- `Array.prototype.filter()`

```javascript
const words = ["spray", "limit", "elite", "exuberant", "destruction", "present"];
const result = words.filter((word) => word.length > 6);
console.log(result);
// expected output: Array ["exuberant", "destruction", "present"]
```

## Javascript spread operator / 3 dots operator

```typescript
const bill = {
  ...adrian,
  fullName: "Bill Gates",
  website: "https://microsoft.com",
};
```

- nimmt das `adrian` object, welches die gleich Struktur haben sollte wie das `bill` object , kopiert zuerst `adrian` nach `bill` und ersetzt dann noch `fullName` und `website`mit den angegebenen werten.
- oder auch :

```typescript
const courses[id2] = {
        ...courses[id1],
        ...changes
    };
```

wenn z.B. id1=1 wäre nimmt es das `courses[1]`- objekt und copiert es , dannach kopiert es dann noch die einzelnen werte von `changes`

## Event von HTML-Elementen type-sicher verarbeiten:

`(event.target as HTMLInputElement).value` statt `event.target.value`

## funktional programming

### reduce for arrays

```typescript
const courses = [1, 2];
const initialValue = 0;
const result = courses.reduce((val, init) => val + init, initialValue);
```

## getting a boolean from any type by double negating it

```typescript
const myarray = "somevalue";
if (!!myarray) console.log("its true");
const myother: string;
if (!!myother) console.log("its false");
```

## Brower localstorage

- only strings, so to save objects : `JSON.stringify(myObject)`
- `localstorage.setItem('myKey',JSON.stringify(myObject))`

## constructor vs inline declaration of member variables

```typescript
export class myClass {
  myVar: string;
  constructor() {
    this.myVar = "hello";
  }
}
```

seems just identical to :

```typescript
export class myClass {
  myVar = "hello";
  constructor() {}
}
```

except the latter is just shorter

## import as:

`import * as myThing from 'whatever'` -> imports funktions defined in "whatever" and makes them accessable by using `myThing.function1()`

## interface vs type

see : https://www.typescriptlang.org/docs/handbook/2/everyday-types.html#interfaces

similar but subtile differences:

```typescript
interface Animal {
  name: string;
}

interface Bear extends Animal {
  honey: boolean;
}

const bear = getBear();
bear.name;
bear.honey;
```

versus:

```typescript
type Animal = {
  name: string;
};

type Bear = Animal & {
  honey: boolean;
};

const bear = getBear();
bear.name;
bear.honey;
```

## Index Signatures

see also https://dmitripavlutin.com/typescript-index-signatures/

```typescript
interface Options {
  // all possible elements, the key is always a string
  [key: string]: string | number | boolean;
  // one specific element given (must be of the same type
  // as 'string | number | boolean' , given above)
  timeout: number;
}

const options: Options = {
  timeout: 1000, // mandatory
  timeoutMessage: "The request timed out!", // optional
  isFileUpload: false, // optional
};
```

Index Signatures are also very similar to Record types :

```typescript
type SpecificSalary = Record<"yearlySalary" | "yearlyBonus", number>;
const salary1: SpecificSalary = {
  yearlySalary: 120_000,
  yearlyBonus: 10_000,
}; // OK
```

..but can be more specific about the index - types. Whereas Index Signatures can have only `string | number ' and Symbols as an index
