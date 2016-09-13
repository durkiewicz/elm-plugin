module ExposedUnionMember exposing (..)

import LibraryTypes exposing (Behavior(Modal, NonModal, Overlay))

main =
    text "Stuff: "
        ++ (behaviorToString LibraryTypes.Modal)


behaviorToString : Behavior -> String
behaviorToString behavior =
    case behavior of
        Overlay -> "Overlay"
        Modal -> "Modal"
        NonModal -> "NonModal"