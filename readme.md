# Conway's Game of Life

For rules on the game itself, see the
[Wikipedia article](http://en.wikipedia.org/wiki/Conway%27s_Game_of_Life).

This implementation is written in ClojureScript and ReactJS (native) with the help of [Om](https://github.com/omcljs/om)

## Overview

The logic for the game of life is in `src/qttt/life.cljs`. This file also contains the intial application (board) state.

UI components live in `src/qttt/ui/om_ios.cljs`.

Currently, there's a toggle to stop/start time and a slider to control the speed of time's progression.

If you click on any cell, you will switch the value of that cell.

## Running the Program

1. From the top level run `lein cljsbuild once ios`.
2. `cd iOS/QTTT`
3. `npm install`
4. `pod install`
5. `open QTTT.xcworkspace`
6. Run on a simulator (iPhone 6 works great)

## REPL

If you'd like to connect to the iOS app (running in the emulator) using a REPL, run `script/ambly-repl`. Have fun!

