package logic;

import org.json.JSONArray;
import org.json.JSONObject;

public class Board
{
    private static final int FIELDS_NUMBER = 32;

    private GameEngine gameEngine;

    Figure[] table;
    Figure[] houses;

    public Board(GameEngine gameEngine)
    {
        this.gameEngine = gameEngine;

        table = new Figure[FIELDS_NUMBER]; // samo polja, bez kucica (null - prazno polje)
        houses = new Figure[16];
    }

    private void addFigure(int figureID)
    {
        // dodavanje nove figure na tablu
        Figure f = this.gameEngine.getFigure(figureID);
        f.state = FigureState.OnTable;
        int ID = f.getColor().getValue();

        if (table[ID * 8] != null)
            table[ID * 8].state = FigureState.NotOnTable;

        table[ID * 8] = f;
    }

    void moveFigure(int figureID, int moves)
    {
        if (moves == 0)
        {
            // dodaje novu figuru
            addFigure(figureID);
            return;
        }
        // figureID - ID figure koja se pomera
        // moves - za koliko poteza

        for (int i = 0; i < table.length; i++)
        {
            if (table[i] != null && table[i].getID() == figureID)
            {
                int playerID = table[i].getColor().getValue();
                int maxPosition = playerID * 8 - 1; // indeks gde poslednje moze da stane, posle mu je kucica

                Figure f = table[i];
                table[i] = null; // sad je prazno polje gde je bila figurica

                int newIndex = (i + moves) % FIELDS_NUMBER;

                if ((i + moves > FIELDS_NUMBER - 1 && playerID == 0) || i <= maxPosition && newIndex > maxPosition)
                {
                    // ulazi u kucicu
                    f.state = FigureState.InHouse;

                    if (playerID != 0)
                        newIndex -= (maxPosition + 1); // mesto u novoj kucici
                    houses[playerID * 4 + newIndex] = f;
                }
                else
                {
                    // ne ulazi u kucicu

                    if (table[newIndex] != null)
                        table[newIndex].state = FigureState.NotOnTable; // pojedena figurica

                    table[(i + moves) % FIELDS_NUMBER] = f; // ako je npr. polje na indeksu 30, i pomera se za 5, mora ostatak posto niz ide do 31
                }

                return;
            }
        }

        // ako nije na tabli, proveri da nije u kucici
        for (int i = 0; i < houses.length; i++)
            if (houses[i] != null && houses[i].getID() == figureID)
            {
                houses[i + moves] = houses[i];
                houses[i] = null;

                // ne menja se state, jer je vec bio u kucici

                return;
            }
    }

    JSONObject toJSONObject()
    {
        JSONObject jsonObject = new JSONObject();

        JSONArray jsonArrayTable = new JSONArray();
        for (Figure f : table)
        {
            if (f != null)
                jsonArrayTable.put(f.toJSONObject());
            else
                jsonArrayTable.put(new JSONObject());
        }

        JSONArray jsonArrayHouses = new JSONArray();
        for (Figure f : houses)
        {
            if (f != null)
                jsonArrayHouses.put(f.toJSONObject());
            else
                jsonArrayHouses.put(new JSONObject());
        }

        jsonObject.putOnce("table", jsonArrayTable);
        jsonObject.putOnce("houses", jsonArrayHouses);

        return jsonObject;
    }
}
