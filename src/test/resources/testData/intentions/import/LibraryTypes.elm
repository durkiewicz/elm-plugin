module LibraryTypes
    ( Action(..)
    , Behavior(..)
    , MyModel
    , Nonsense
    , makeNonsense
    , nonsenseToString
    )
    where

type alias MyModel =
    { title : String
    , subTitle : String
    }

type Action
    = Abscond
    | Believe
    | Cherish
    | Delve


type Behavior
    = Modal
    | NonModal
    | Overlay


-- an opaque type
type Nonsense
    = Foo
    | Bar

makeNonsense : Int -> Nonsense
makeNonsense k =
    if k == 0 then Foo else Bar

nonsenseToString : Nonsense -> String
nonsenseToString nonsense =
    case nonsense of
        Foo -> "Foo"
        Bar -> "Bar"