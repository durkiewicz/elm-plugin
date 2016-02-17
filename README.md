# elm-plugin
Elm language plugin for IntelliJ IDEA.

## Features

- Parsing the syntax
- Coloring keywords, comments, strings and numbers

This plugin is still at development phase. More features are coming soon.

## Building from sources

0. Setup your development environment according to [this instruction](http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html).
0. Clone this repository.
0. Open it as a Plugin Project in IntelliJ IDEA (either Community or Ultimate version).
0. Make sure you have Grammar-Kit and PsiViewer plugins installed.
0. Delete the content of `gen` directory if you have previously generated parser code from another version of the BNF file.
0. Open `src/org/elmlang/intellijplugin/Elm.bnf` file and generate the parser code (*)
0. Open `src/org/elmlang/intellijplugin/Elm.flex` file and generate lexer code (*)
0. The plugin should be ready to run now.

(*) either from a context menu or by keyboard shortcut ⇧⌘G

## License: MIT