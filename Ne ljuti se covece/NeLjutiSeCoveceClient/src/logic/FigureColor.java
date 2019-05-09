package logic;

import java.awt.*;

public enum FigureColor
{
    Red(0), Blue(1), Green(2), Yellow(3);

    private final int value;

    FigureColor(int value)
    {
        this.value = value;
    }

    public static FigureColor getColor(String colorString)
    {
        switch (colorString)
        {
            case "Red":
                return FigureColor.Red;

            case "Blue":
                return FigureColor.Blue;

            case "Green":
                return FigureColor.Green;

            default:
                return FigureColor.Yellow;
        }
    }

    public int getValue()
    {
        return value;
    }

    public Color getRealColor()
    {
        switch (value)
        {
            case 0:
                return Color.RED;

            case 1:
                return Color.BLUE;

            case 2:
                return Color.GREEN;

            case 3:
                return Color.YELLOW;

            default:
                return Color.GRAY;
        }
    }
}
