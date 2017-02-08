# elm-plugin
Elm language plugin for IntelliJ IDEA.

## Features

This plugin supports Elm 0.18.0. If you need to have different version supported, see [this link](http://durkiewicz.github.io/elm-plugin/#releases).

- Parsing the syntax
- Syntax highlighting and color settings page
- Going to declaration 
- Highlighting unresolved references
- Code completion
- Brace matching
- Spellchecking
- Rename refactoring

Formatting is currently not a feature of the plugin, but `elm-format` can be used instead. If you don't know how to configure IntelliJ to use `elm-format` you can find some information [here](https://github.com/durkiewicz/elm-plugin/issues/9)

## Building from sources

0. Setup your development environment according to [this instruction](http://www.jetbrains.org/intellij/sdk/docs/basics/getting_started/setting_up_environment.html).
0. Clone this repository.
0. Open it as a Plugin Project in IntelliJ IDEA (either Community or Ultimate version).
0. Make sure you have `Grammar-Kit` and `PsiViewer` plugins installed.
0. Delete the content of `gen` directory if you have previously generated parser code from another version of the BNF file.
0. Open `src/main/java/org/elmlang/intellijplugin/Elm.bnf` file and generate the parser code - twice, if needed (*)
0. Open `src/main/java/org/elmlang/intellijplugin/Elm.flex` file and generate lexer code (*)
0. The plugin should be ready to run now.

(*) either from a context menu or by keyboard shortcut ⇧⌘G

## Contributing

If you would like to develop a new feature or fix a bug:
- Please add an issue to Github so that everyone can share his/her opinions and ideas
- Please create a pull request when you finish development
- If you would like to do any change to the parser or a non-trivial change to resolving references,
  please contact me before.

## License: MIT