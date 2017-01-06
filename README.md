# tictactoe-machine

## Description

This class provides static methods to satisfy the requirements of the following exercise:
~~~
"Please write a program that lets a human player to play “Tic Tac Toe” game with a computer.
The computer initially plays by randomly choosing an  empty cell in a grid. However, it keeps track of all
previous losing combinations and never loses by following the same sequence of moves. (you can also
consider that one loosing combination can be converted into 4 if you rotate the grid by 90, 180, and 270 degrees)
For simplicity, make it a console game and use text file  for storing loosing combinations. To simplify debugging,
please write each loosing combination on a separate line like this: (2,2)-(2,3)-(1,1)-(3,3)-(3,1)-(3,2) where
the last move need to be changed the next time machine plays."
~~~

## Running this program

The ant build file (build.xml) can be used to build and run the TicTacToe excercise, using the following ant targets: 
```
    > ant compile
    > ant jar
    > ant run
```
The "full" ant target will do it all, and is also the default, so: 
```
    > ant full 
```
or simply: 
```
    > ant
```
A data file containing bad moves is saved in the directory where the program is run from, as tttVerbose.log.
