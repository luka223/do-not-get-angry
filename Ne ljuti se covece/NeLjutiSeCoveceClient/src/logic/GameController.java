package logic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Random;

public class GameController
{
    private static final int FIELDS_NUMBER = 32;

    private ServerConnection serverConnection;

    private boolean finished;
    private int ID;
    private Figure[] table;
    private Figure[] houses;
    private Figure[] figures; // sve figure igraca
    private int turn;
    private int dice;

    public GameController(ServerConnection serverConnection)
    {
        this.serverConnection = serverConnection;
        finished = false;
        dice = -1;

        table = new Figure[FIELDS_NUMBER]; // samo polja, bez kucica (null - prazno polje)
        houses = new Figure[16];
        figures = new Figure[4];
    }

    public void configure()
    {
        JSONObject jsonObject = serverConnection.recieveMesage();

        ID = jsonObject.getInt("playerID");
        turn = jsonObject.getInt("turn");
        JSONArray jsonArrayFigures = jsonObject.getJSONArray("figures");

        for (int i = 0; i < 4; i++)
        {
            JSONObject jsonObject1 = jsonArrayFigures.getJSONObject(i);
            figures[i] = new Figure(jsonObject1);
        }
    }

    public void recieveMessage()
    {
        // prima poruku od servera i setuje sve parametre
        JSONObject jsonObject = serverConnection.recieveMesage();
        System.out.println(jsonObject.toString());

        try
        {
            JSONArray jsonArrayTable = jsonObject.getJSONArray("table");
            for (int i = 0; i < jsonArrayTable.length(); i++)
            {
                JSONObject jsonObject1 = jsonArrayTable.getJSONObject(i);
                if (jsonObject1.has("figureID"))
                    table[i] = new Figure(jsonObject1);
                else
                    table[i] = null;
            }

            JSONArray jsonArrayHouses = jsonObject.getJSONArray("houses");
            for (int i = 0; i < jsonArrayHouses.length(); i++)
            {
                JSONObject jsonObject1 = jsonArrayHouses.getJSONObject(i);
                if (jsonObject1.has("figureID"))
                    houses[i] = new Figure(jsonObject1);
                else
                    houses[i] = null;
            }

            JSONArray jsonArrayFigures = jsonObject.getJSONArray("figures");
            for (int i = 0; i < jsonArrayFigures.length(); i++)
            {
                JSONObject jsonObject1 = jsonArrayFigures.getJSONObject(i);

                if (jsonObject1.has("figureID"))
                    figures[i] = new Figure(jsonObject1);
                else
                    figures[i] = null;
            }

            turn = jsonObject.getInt("turn");
            finished = jsonObject.getBoolean("finished");
        }
        catch (Exception e)
        {
            System.err.println("GRESKA: " + e.getMessage());
        }
    }

    public boolean isAnyMovePossible()
    {
        for (Figure f : figures)
            if (canFigureOnTableMove(f.getID(), dice) == true)
                return true;

        if (dice < 6)
        {
            // ako je bacio manje od 6, onda ne moze novu figuru da doda
            // a petlja iznad je proverila da nijednu figuru na tabli ne moze da pomeri

            JSONObject jsonObject = new JSONObject();
            jsonObject.putOnce("moves", -1);
            serverConnection.sendMessage(jsonObject);

            dice = -1;
            return false;
        }

        return true;
    }

    public boolean addFigure()
    {
        // novog igraca dodaje samo ako je bacio 6
        if (dice == 6)
        {
            // ako vec ima vec svoju figuru na mestu, onda ne moze da igra
            if (table[ID * 8] != null && table[ID * 8].getColor().getValue() == ID)
                return false;

            for (Figure f : figures)
                if (f.state == FigureState.NotOnTable)
                {
                    dice = -1;

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.putOnce("figureID", f.getID());
                    jsonObject.putOnce("moves", 0); // 0 - oznaka da se dodaje novi igrac

                    serverConnection.sendMessage(jsonObject);
                    return true;
                }
        }

        return false;
    }

    public boolean moveFigure(int figureID)
    {
        // ako je bacio kocicu i ako je igrac na potezu
        // 0 - dodaje se nova figura
        // 1-6 - pomera se figura
        if (dice > -1 && ID == turn && canFigureOnTableMove(figureID, dice))
        {
            JSONObject jsonObject = new JSONObject();
            jsonObject.putOnce("figureID", figureID);
            jsonObject.putOnce("moves", dice);

            serverConnection.sendMessage(jsonObject);

            dice = -1;

            return true;
        }
        else
            return false;
    }

    private boolean canFigureOnTableMove(int figureID, int moves)
    {
        // ako igrac nema figuru sa zadatim ID-em, ne moze da je pomeri jer nije njegova
        if (Arrays.stream(figures).anyMatch(x -> x.getID() == figureID) == false)
            return false;

        // nalazenje pozicije figurice
        int currentPos = -1;
        for (int i = 0; i < table.length; i++)
            if (table[i] != null && table[i].getID() == figureID)
            {
                currentPos = i;
                break;
            }

        // figurica nije na tabli
        if (currentPos == -1)
        {
            // proveri da nije figurica u kucici
            int n = ID * 4;
            for (int i = n; i < n + 4; i++)
                if (houses[i] != null && houses[i].getID() == figureID)
                {
                    int posInHouse = i - n;
                    System.out.println(posInHouse);

                    // moze [0-3]
                    // npr. igrac sa ID-em 1 moze [4][5][6][7] da uzme mesta
                    if (posInHouse + moves > 3)
                        return false;

                    // pokusava da preskoci figuricu u kucici
                    for (int j = posInHouse + 1; j <= posInHouse + moves; j++)
                        if (houses[n + j] != null)
                            return false;

                    return true;
                }

            return false;
        }
        else
        {
            // figurica je na tabli, proveri ako ulazi u kucicu
            if ((ID == 0 && (currentPos + moves) > FIELDS_NUMBER - 1) || currentPos < ID * 8 && (currentPos + moves) >= ID * 8)
            {
                int posInHouse = (currentPos + moves) % FIELDS_NUMBER - ID * 8;

                // kucica ima samo 4 polja
                if (posInHouse > 3)
                    return false;

                // ulazi u kucicu, proveri da ne preskace vec nekog u kucici
                for (int i = ID * 4; i <= ID * 4 + posInHouse; i++)
                    if (houses[i] != null)
                        return false;

                return true;
            }

            // uslov da ne moze da pojede svoju figuricu
            if (table[(currentPos + moves) % FIELDS_NUMBER] != null && table[(currentPos + moves) % FIELDS_NUMBER].getColor() == table[currentPos].getColor())
                return false;
            else
                return true;
        }
    }

    public int rollDice()
    {
        // ako kockica nema vrednost -1, vec je bacena
        if (dice == -1 || (dice == 6 && isAnyMovePossible() == false))
        {
            Random r = new Random();
            dice = r.nextInt(6) + 1; // r.nextInt(6) vraca ceo broj u intervalu [0, 5], pa zato +1
        }

        return dice;
    }

    public Figure[] getTable()
    {
        return table;
    }

    public Figure[] getHouses()
    {
        return houses;
    }

    public int numberOfFiguresNotOnTable()
    {
        int sum = 0;

        for (Figure f : figures)
            if (f != null && f.state == FigureState.NotOnTable)
                sum++;

        return sum;
    }

    public int getID()
    {
        return ID;
    }

    public int getTurn()
    {
        return turn;
    }

    public boolean isFinished()
    {
        return finished;
    }
}
