package logic;

import org.json.JSONObject;

public class Figure
{
    private FigureColor color;
    private int ID;
    FigureState state;

    public Figure(int ID, FigureColor color, FigureState state)
    {
        this.ID = ID;
        this.color = color;
        this.state = state;
    }

    public Figure(JSONObject jsonObject)
    {
        this(jsonObject.getInt("figureID"), FigureColor.getColor(jsonObject.getString("color")), FigureState.getFigureState(jsonObject.getString("state")));
    }

    public FigureColor getColor()
    {
        return color;
    }

    public int getID()
    {
        return ID;
    }
}
