package graphic;

import logic.GameController;
import logic.ServerConnection;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame
{
    // Klasa JFrame predstavlja ceo prozor

    GameController gameController;
    private JLabel titleLabel;
    private ConnectScreen connectScreen;

    public MainWindow()
    {
        super("Ne ljuti se covece");

        setLayout(new BorderLayout()); // layout prozora
        setNorth();
        setCenter();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // da se pri zatvaranju prozora ugasi cela aplikacija (inace bez ovoga ostane da radi u pozadini)
        pack(); // da prepakuje gui (nekad kada se ne pozove ova metoda, gui ne izgleda kako teba)
        setLocationRelativeTo(null); // da pokrene na sredini ekrana
        setResizable(false); // da prozor ne moze da se resizuje
        setVisible(true); // da prozor bude vidljiv
    }

    private void setNorth()
    {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titleLabel = new JLabel("Ne ljuti se covece");

        panel.add(titleLabel);
        getContentPane().add(panel, BorderLayout.NORTH);
    }

    private void setCenter()
    {
        connectScreen = new ConnectScreen(this);
        getContentPane().add(connectScreen, BorderLayout.CENTER);
    }

    void initializeGameScreen(ServerConnection serverConnection)
    {
        // inicijalizacija prozora se vrsi iz klase ConnectScreen gde se vrsi konektovanje sa serverom
        getContentPane().remove(connectScreen);
        connectScreen = null;

        gameController = new GameController(serverConnection);

        GameScreen gameScreen = new GameScreen(gameController, titleLabel);

        // Klasa SwingWorker se mora koristiti, jer Javi treba neko vreme
        // da iscrta gui, koji radi metoda doInBackground(), a nakon njenog zavrsetka
        // se poziva metoda done()

        SwingWorker swingWorker = new SwingWorker()
        {

            @Override
            protected Object doInBackground() throws Exception
            {
                setVisible(false);
                getContentPane().add(gameScreen, BorderLayout.CENTER);
                pack();
                setVisible(true);

                return null;
            }

            @Override
            protected void done()
            {
                gameScreen.playGame();
            }
        };

        swingWorker.execute();
    }

}
