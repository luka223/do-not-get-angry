package logic;

public enum FigureState
{
    NotOnTable,
    OnTable,
    InHouse;

    public static FigureState getFigureState(String stateString)
    {
        switch (stateString)
        {
            case "NotOnTable":
                return NotOnTable;

            case "OnTable":
                return OnTable;

            default:
                return InHouse;
        }
    }
}
