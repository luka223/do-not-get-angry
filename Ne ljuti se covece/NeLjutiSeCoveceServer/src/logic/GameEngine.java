package logic;

import org.json.JSONObject;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameEngine
{
    private static final int PORT = 10000;

    private int numberOfPlayers;
    private List<Player> players;
    private Board board;
    private int turn; // ID igraca koji je na potezu
    boolean finished; // indikator da li je igra zavrsena

    public GameEngine(int numberOfPlayers)
    {
        this.numberOfPlayers = numberOfPlayers;
        players = new ArrayList<>(numberOfPlayers);
        board = new Board(this);
        turn = 0;
        finished = false;
    }

    public void startGame()
    {
        if (openConnection())
            play();
    }

    // metoda koja otvara konekciju i ceka da se odgovarajuci broj igraca poveze
    private boolean openConnection()
    {
        int connected = 0; // broj povezanih igraca
        System.out.println("Povezivanje igraca...");

        try
        {
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (connected < numberOfPlayers)
            {
                try
                {
                    Socket clientSocket = serverSocket.accept();

                    Player player = new Player(clientSocket, this, connected);
                    connected++;
                    System.out.println("Igrac (" + connected + "/" + numberOfPlayers + ") je uspesno povezan!");

                    players.add(player);
                }
                catch (Exception e)
                {
                    System.err.println("GRESKA: " + e.getMessage());
                }
            }
        }
        catch (Exception e)
        {
            System.err.println("GRESKA: Problem pri otvaranju konekcije...");
            return false;
        }

        return true;
    }

    // metoda koja se poziva nakon uspesnog povezivanja odredjenog broja igraca i zapocinje igru
    private void play()
    {
        for (Player player : players)
        {
            // slanje podataka svakom igracu kako bi se signalizirao pocetak igre
            JSONObject jsonObject = new JSONObject();
            jsonObject.putOnce("playerID", player.getID());
            jsonObject.putOnce("turn", turn);
            jsonObject.putOnce("figures", player.getFigures());

            player.sendMessage(jsonObject);
        }

        // pokretanje niti
        for (Player player : players)
            player.start();

        System.out.println("Igra je pocela!");

        try
        {
            // pozivanje join funkcije kod niti, da se metoda NE zavrsi pre zavrsetka niti
            for (Player player : players)
                player.join();
        }
        catch (Exception e)
        {
            System.err.println("GRESKA: Problem sa cekanjem niti");
        }
    }

    Figure getFigure(int figureID)
    {
        for (Player p : players)
        {
            Figure f = p.getFigure(figureID);

            if (f != null)
                return f;
        }

        return null;
    }

    void moveFigure(int figureID, int moves)
    {
        board.moveFigure(figureID, moves);
    }

    void changeTurn()
    {
        turn = (turn < numberOfPlayers - 1) ? turn + 1 : 0;
    }

    int getCurrentTurn()
    {
        return turn;
    }

    void sendBoardToPlayers()
    {
        JSONObject jsonObject = board.toJSONObject(); // trenutno stanje table i kucica
        jsonObject.putOnce("turn", turn); // ko je na potezu
        jsonObject.putOnce("finished", finished); // da li je igra zavrsena

        for (Player p : players)
        {
            if (jsonObject.has("figures"))
                jsonObject.remove("figures");

            jsonObject.putOnce("figures", p.getFigures()); // igraceve figurice u kucici
            p.sendMessage(jsonObject);
        }
    }

    Figure[] getBoard()
    {
        return board.table;
    }

    Figure[] getHoueses()
    {
        return board.houses;
    }
}
