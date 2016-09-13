module ExposedUnionMember exposing (..)

main =
    text "Stuff: "
        ++ (behaviorToString LibraryTypes.Modal)


behaviorToString : Behavior -> String
behaviorToString behavior =
    case behavior of
        Overlay -> "Overlay"
        Modal -> "Modal"
        NonModal -> "NonModal"