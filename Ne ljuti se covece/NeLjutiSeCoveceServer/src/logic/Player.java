package logic;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;

public class Player extends Thread
{
    private BufferedReader input;
    private PrintStream output;

    private int ID;
    private Figure[] figures;
    private GameEngine gameEngine;

    public Player(Socket socket, GameEngine gameEngine, int ID) throws Exception
    {
        try
        {
            this.gameEngine = gameEngine;

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintStream(socket.getOutputStream());

            this.ID = ID;
            initializeFigures(ID);
        }
        catch (Exception e)
        {
            throw new Exception("Greska pri komunikaciji");
        }
    }

    private void initializeFigures(int ID)
    {
        figures = new Figure[4];
        FigureColor color = FigureColor.values()[ID];

        for (int i = 0; i < 4; i++)
            figures[i] = new Figure(color);
    }

    void sendMessage(JSONObject jsonObject)
    {
        try
        {
            output.println(jsonObject.toString());
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    }

    private JSONObject recieveMessage()
    {
        try
        {
            return new JSONObject(input.readLine()); // exception ukoliko ucitani podaci nisu u odgovarajucem formatu
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private boolean isFinished()
    {
        // ako su sve figure u kucici, igra je zavrsena
        for (Figure figure : figures)
            if (figure.state != FigureState.InHouse)
                return false; // bar jedna figura nije u kucici, odmah se vraca false jer sigurno nije kraj

        // nijedna figura nije van kucice, sigurno je kraj
        return true;
    }

    JSONArray getFigures()
    {
        JSONArray jsonArray = new JSONArray();

        for (Figure f : figures)
            jsonArray.put(f.toJSONObject());

        return jsonArray;
    }

    int getID()
    {
        return ID;
    }

    Figure getFigure(int figureID)
    {
        for (Figure f : figures)
            if (f.getID() == figureID)
                return f;

        return null;
    }

    @Override
    public void run()
    {
        while (gameEngine.finished == false)
        {
            JSONObject jsonObject = recieveMessage();

            if (jsonObject != null)
            {
                int figureID, moves;

                try
                {
                    System.out.println("Recieved: " + jsonObject.toString());
                    moves = jsonObject.getInt("moves");

                    if (moves > -1)
                    {
                        // ako je moves == -1, znaci da igrac nema nijedan potez na raspolaganju i preskace se dalje

                        figureID = jsonObject.getInt("figureID");

                        gameEngine.moveFigure(figureID, moves);
                        gameEngine.finished = isFinished(); // provera da li je igra zavrsena
                    }

                    // ako nije bacio 6, igra sledeci igrac
                    // nula signalizira dodavanje nove figurice (sto znaci da je isto bacio 6)
                    if (moves != 0 && moves < 6)
                        gameEngine.changeTurn();

                    gameEngine.sendBoardToPlayers();
                }
                catch (NoSuchElementException e)
                {
                    // u JSON-u se ne nalaze ispravni podaci
                    System.err.println("Primljeni su neispravni podaci");
                }
            }

        }

    }
}
