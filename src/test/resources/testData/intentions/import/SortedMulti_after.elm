module Main exposing (..)

import LibraryA
import LibraryB
import LibraryC

main =
    text "Stuff: "
        ++ LibraryB.beCool
        ++ ", "
        ++ LibraryC.calamity
        ++ ", "
        ++ LibraryA.avril14th
