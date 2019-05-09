package logic;

public enum FigureColor
{
    Red(0), Blue(1), Green(2), Yellow(3);

    private final int value;

    FigureColor(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
