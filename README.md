# elm-plugin
Elm language plugin for IntelliJ IDEA. This repository is no longer maintained due to lack of resources. If you need an Elm language support in IntelliJ - please consider using [this plugin](https://github.com/klazuka/intellij-elm) instead.

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
0. Open `src/main/java/org/elmlang/intellijplugin/Elm.flex` file and generate lexer code (*)
0. Open `src/main/java/org/elmlang/intellijplugin/Elm.bnf` file and generate the parser code - twice, if needed (*)
0. Open `File -> Project Structure` under `Project` set the `Projekt SDK` to the (in Step 1) configured `IntelliJ Platform Plugin SDK`, the `Project language level` at least to `8` and the `Project compiler output` to `out`
0. Create a `Plugin`-Run Configuration

(*) either from a context menu or by keyboard shortcut ⇧⌘G

## Contributing

Please do not contribute to this repository directly - fork it and contribute to your fork instead.

## License: MIT