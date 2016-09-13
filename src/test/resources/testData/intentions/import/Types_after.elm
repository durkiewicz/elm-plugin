module Types where

import LibraryTypes exposing (Behavior(Modal, NonModal, Overlay), MyModel, Nonsense, makeNonsense, nonsenseToString)

main =
    text "Stuff: "
        ++ (behaviorToString LibraryTypes.Modal)
        ++ ", "
        ++ (nonsenseToString someNonsense)

behaviorToString : Behavior -> String
behaviorToString behavior =
    case behavior of
        Overlay -> "Overlay"
        Modal -> "Modal"
        NonModal -> "NonModal"


someNonsense : Nonsense
someNonsense = makeNonsense 99


title : MyModel -> String
title model =
    model.title