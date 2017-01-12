# tictactoe-machine

## Description

This project provides a program that lets a human player play Tic Tac Toe with a computer. The computer initially plays by randomly choosing an  empty cell in a grid. However, it keeps track of all previous losing combinations and never loses by following the same sequence of moves, including sets of moves that are functionally equivalent (e.g., with the board rotated or flipped on an axis).

Beating the program at least once will result in a data file containing bad moves being saved in the directory where the program is run from.  This data file isused in subsequent games by the computer to avoid repeating such losses.


## Running this program with Gradle

The project comes with a build.gradle file and wrappers that can be used to run the program using gradle, if you have it installed:
```
    > gradle -q
```

## Running this program with Ant

The project comes with an ant build.xml file that can be used to run the program using ant, if you have ant installed.

```
    > ant
```
