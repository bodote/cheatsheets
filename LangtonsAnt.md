# Developing LangtonsAnt TDD style
This is an exercice in Test Driven Development

## Implementation Rules:
- Implement a `LangtonsAnt` class, according to 
  -  these rules [here](https://en.wikipedia.org/wiki/Langton%27s_ant)
- Start with `LangtonsAntTest` unit test
- break down all the functionality you will need into small chunks/methods **not** longer then 9-11 lines for each method, so that they can be easily unit-tested.
- write **all** the tests **before** its implementation. 
- but write an **empty** class of `LangtonsAnt` with  the **empty** methods you plan **even before** the tests
- the board should be a simple `char[][]` array, prefilled with your test data, which is given in the constructor.
- also provide the ant's `x` and `y` position as an `int` and it's direction ('north','east','south','west') as an `string` or `enum`
- `LangtonsAnt` class should have a method (`getNextBoardState()`) , which returns the new state of the board after the Ant's rules are applied once. 
- `LangtonsAnt` class should also keep the state, so that each subsequent call to `getNextBoardState()` should return the following state after after the Ant's rules are applied again once.

###   Langton's Ant Class :
.. should look like: 
```java
public class LangtonsAnt  {
  public static char BLACK_CELL = '■'; 
  public static char WHITE_CELL = ' ';
  LangtonsAnt(char[][] array, int antX, int antY, String antDirection ) {
  } 
  
  public char[][] getNextBoardState(){//...
  }
} 
```

## Example arrays/steps for "Langton's Ant":

Find the ONE error in  the following example steps! and add another step at the end!

### example start 6x6 array:  the ant on x=2,y=3, direction=west,  "." is a white field, "■" is a black field
```
......
..■...
...■..
..*...
......
......
```
with the ant on x=2,y=3, direction=west,

### next step (1)  according to the rules: 
- At a white square, turn 90° clockwise, flip the color of the square, move forward one unit
- At a black square, turn 90° counter-clockwise, flip the color of the square, move forward one unit
- **NEW**: when ant is facing to the border of the board, it does NOT move forward, but still does flip the color of the square and roteate according to rule 1 and 2
  would be:
```
......
..■...
..*■..
..■...
......
......
```
while ants position is now x=2,y=2 , direction north (because ant was on a white field): 

### next step  (2) according to the rules

```
......
..■...
..■✪..
..■...
......
......
```
ant position x=3, y=2, direction=east (because ant was on a white field), 

### next step  (3) according to the rules
```
......
..■.*.
..■...
..■...
......
......
```
ant position : x=3, y=1 , direktion north (because last field was a black field)

