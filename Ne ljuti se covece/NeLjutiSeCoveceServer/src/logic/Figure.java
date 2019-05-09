package logic;

import org.json.JSONObject;

public class Figure
{
    private static int idCounter = 0;

    private FigureColor color;
    private int ID;
    FigureState state;

    public Figure(FigureColor color)
    {
        this.color = color;
        this.ID = ++idCounter;
        state = FigureState.NotOnTable;
    }

    FigureColor getColor()
    {
        return color;
    }

    int getID()
    {
        return ID;
    }

    JSONObject toJSONObject()
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOnce("figureID", ID);
        jsonObject.putOnce("state", state);
        jsonObject.putOnce("color", color);

        return jsonObject;
    }
}
