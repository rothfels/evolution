# Quantum Tic Tac Toe

For rules on the game itself, see the
[Wikipedia article](http://en.wikipedia.org/wiki/Quantum_tic-tac-toe)
or the original
[paper](http://qttt.googlecode.com/files/QT3-AJP%2010-20-06.pdf)
published in the American Journal of Physics.

This implementation is written in ClojureScript and ReactJS, and has
three front-ends written using different UI libraries:

- [Om](https://github.com/omcljs/om)
- [Reagent](http://reagent-project.github.io)
- [Quiescent](https://github.com/levand/quiescent)

## Running the Program

Note: QTTT depends on a library, `com.cognitect/contextual` that may not yet
be in public repositories. If not, you can install the dependency locally by cloning
`https://github.com/levand/contextual.git` and running `lein install` in that repository.

First, compile the ClojureScript:

`lein cljsbuild`

Then, visit the HTML page at `resources/public/dev.html`.

You will need to select which UI library to view by passing a `lib`
URL parameter. For example:

- `dev.html?lib=om`
- `dev.html?lib=reagent`
- `dev.html?lib=quiescent`

Play proceeds in turns. There is no win condition detection, yet, but
all the other rules of the game are in place.

## Future Ideas

Ideas for future expansion include:

- Win condition detection
- Network multiplayer
- AI

# React Native Port

1. From the top level run `lein cljsbuild once ios`.
2. `cd iOS/QTTT`
3. `npm install`
4. `pod install`
5. `open QTTT.xcworkspace`
6. Run on a simulator (iPhone 6 works great)

## REPL

If you'd like to connect to the iOS app using a REPL, run `script/ambly-repl`.

## Demo


[Watch a quick demo](https://youtu.be/7HtOTzllwTY) of it running on iOS.