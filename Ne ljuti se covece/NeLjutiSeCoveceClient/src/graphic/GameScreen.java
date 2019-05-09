package graphic;

import logic.Figure;
import logic.FigureColor;
import logic.GameController;

import javax.swing.*;
import java.awt.*;

public class GameScreen extends JPanel
{
    private GameController gameController;
    private JPanel[][] panels;
    private JPanel[] notOnTablePanels;

    private JLabel titleLabel;
    private ImageIcon[] diceImages;
    private JLabel dice;

    public GameScreen(GameController gameController, JLabel titleLabel)
    {
        this.gameController = gameController;
        this.titleLabel = titleLabel;

        // BorderLayout - deli komponentu na 5 delova (north, east, center, west i south)
        setLayout(new BorderLayout());

        // funkcije setuju odgovarajuce delove u BorderLayout-u
        setWest();
        setCenter();
        setEast();
    }

    private void setWest()
    {
        JPanel panel = new JPanel();
        Dimension d = new Dimension();
        d.width = 200;
        panel.setPreferredSize(d);

        panel.add(new JLabel("Nisu u igri"));

        notOnTablePanels = new JPanel[4];
        JPanel panel1 = new JPanel(new GridLayout(2, 2));
        for (int i = 0; i < 4; i++)
        {
            notOnTablePanels[i] = new JPanel();
            notOnTablePanels[i].setPreferredSize(new Dimension(40, 40));
            notOnTablePanels[i].setBorder(BorderFactory.createLineBorder(Color.GRAY, 10));

            panel1.add(notOnTablePanels[i]);
        }
        paintFiguresNotOnTable();

        panel.add(panel1);

        JButton button = new JButton("Dodaj novog igraca");
        button.addActionListener(e ->
        {
            if (gameController.addFigure() == true)
                playMove();
        });

        panel.add(button);

        add(panel, BorderLayout.WEST);
    }

    private void setCenter()
    {
        JPanel boardPanel = new JPanel(new GridLayout(9, 9));
        panels = new JPanel[9][9];

        for (int i = 0; i < panels.length; i++)
            for (int j = 0; j < panels[i].length; j++)
            {
                panels[i][j] = new JPanel(new GridBagLayout());
                panels[i][j].setPreferredSize(new Dimension(50, 50));
                panels[i][j].setBackground(Color.WHITE);

                boardPanel.add(panels[i][j]);
            }


        for (int i = 0; i < panels.length - 1; i++)
        {
            // gornja
            panels[0][i + 1].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            panels[0][i + 1].setBackground(Color.GRAY);

            // desna
            panels[i + 1][8].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            panels[i + 1][8].setBackground(Color.GRAY);

            // donja
            panels[8][i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            panels[8][i].setBackground(Color.GRAY);

            // leva
            panels[i][0].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            panels[i][0].setBackground(Color.GRAY);
        }

        for (int i = 1; i < 5; i++)
        {
            // crvene kucice [1][1], [1][2], [1][3], [1][4]
            panels[1][i].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            panels[1][i].setBackground(new Color(255, 80, 80));

            // plave kucice [1][7], [2][7], [3][7], [4][7]
            panels[i][7].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            panels[i][7].setBackground(new Color(153, 204, 255));

            // zelene kucice [7][4], [7][5], [7][6], [7][7]
            panels[7][i + 3].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            panels[7][i + 3].setBackground(new Color(153, 255, 153));

            // zute kucice [4][1], [5][1], [6][1], [7][1]
            panels[i + 3][1].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            panels[i + 3][1].setBackground(new Color(255, 255, 153));
        }

        add(boardPanel, BorderLayout.CENTER);
    }

    private void setEast()
    {
        // slikeKockica[0] sadrzi sliku broja 1, itd.
        dice = new JLabel();
        dice.setBackground(Color.WHITE);
        dice.setPreferredSize(new Dimension(100, 100));
        diceImages = new ImageIcon[6];
        for (int i = 0; i < diceImages.length; i++)
        {
            ImageIcon imageIcon = new ImageIcon(MainWindow.class.getResource("/images/" + (i + 1) + ".png"));
            diceImages[i] = resizeImageIcon(imageIcon, 100, 100);
        }

        JButton button = new JButton("Baci kockicu");
        button.addActionListener(e ->
        {
            if (gameController.getID() == gameController.getTurn())
            {
                int number = gameController.rollDice();

                dice.setIcon(diceImages[number - 1]);

                if (gameController.isAnyMovePossible() == false)
                    playMove(); // ukoliko je nemoguce odigrati bilo koji potez, onda poslati poruku serveru

            }
        });

        // panel sirine 200px
        JPanel panel = new JPanel();
        Dimension d = new Dimension();
        d.width = 200;
        panel.setPreferredSize(d);

        panel.add(button);
        panel.add(dice);
        add(panel, BorderLayout.EAST);
    }

    private ImageIcon resizeImageIcon(ImageIcon image, int width, int height)
    {
        Image img = image.getImage();
        Image newimg = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    void playMove()
    {
        SwingWorker swingWorker = new SwingWorker()
        {
            @Override
            protected Object doInBackground() throws Exception
            {
                gameController.recieveMessage();
                return null;
            }

            @Override
            protected void done()
            {
                drawGUI();

                if (gameController.isFinished())
                    JOptionPane.showMessageDialog(null, "Kraj igre");

                else if (gameController.getID() != gameController.getTurn())
                    playMove();

            }
        };

        swingWorker.execute();
    }

    void playGame()
    {
        SwingWorker swingWorker = new SwingWorker()
        {
            @Override
            protected Object doInBackground() throws Exception
            {
                titleLabel.setText("Cekaju se ostali igraci...");
                return null;
            }

            @Override
            protected void done()
            {
                gameController.configure(); // konfiguracija pocetnih vrednosti (ID, ko je prvi na potezu)
            }
        };

        SwingWorker swingWorker1 = new SwingWorker()
        {
            @Override
            protected Object doInBackground() throws Exception
            {
                swingWorker.execute();
                Thread.sleep(1000); // zato sto se done() izvrsi pre ove metode, a ne treba tako da radi
                return null;
            }

            @Override
            protected void done()
            {
                drawGUI();

                if (gameController.getTurn() != gameController.getID())
                    playMove();
            }
        };

        swingWorker1.execute();
    }

    private void paintFiguresNotOnTable()
    {
        for (int i = 0; i < 4; i++)
        {
            if (i < gameController.numberOfFiguresNotOnTable())
            {
                FigureColor figureColor = FigureColor.values()[gameController.getID()];
                notOnTablePanels[i].setBackground(figureColor.getRealColor());
            }
            else
                notOnTablePanels[i].setBackground(Color.GRAY);

        }

    }

    private void placeFigureOnPanel(Figure f, int i, int j)
    {
        panels[i][j].removeAll();

        if (f != null)
        {
            FigureGraphic fg = new FigureGraphic(f, gameController, this);

            fg.setBackground(f.getColor().getRealColor());
            fg.setPreferredSize(new Dimension(30, 30));
            fg.setSize(new Dimension(30, 30));

            panels[i][j].add(fg);
        }
    }

    private void drawTable()
    {
        Figure[] table = gameController.getTable();
        int count = 0;

        for (int j = 0; j < 8; j++)
        {
            placeFigureOnPanel(table[count], 0, j);
            count++;
        }

        for (int i = 0; i < 8; i++)
        {
            placeFigureOnPanel(table[count], i, 8);
            count++;
        }

        for (int j = 8; j > 0; j--)
        {
            placeFigureOnPanel(table[count], 8, j);
            count++;
        }

        for (int i = 8; i > 0; i--)
        {
            placeFigureOnPanel(table[count], i, 0);
            count++;
        }
    }

    private void drawHouses()
    {
        Figure[] houses = gameController.getHouses();

        for (int i = 0; i < 4; i++)
        {
            panels[1][i + 1].removeAll();
            if (houses[i] != null)
                placeFigureOnPanel(houses[i], 1, i + 1);

            panels[i + 1][7].removeAll();
            if (houses[4 + i] != null)
                placeFigureOnPanel(houses[4 + i], i + 1, 7);

            panels[7][7 - i].removeAll();
            if (houses[8 + i] != null)
                placeFigureOnPanel(houses[8 + i], 7, 7 - i);

            panels[7 - i][1].removeAll();
            if (houses[12 + i] != null)
                placeFigureOnPanel(houses[12 + i], 7 - i, 1);

        }
    }

    private void drawGUI()
    {
        if (gameController.getTurn() == gameController.getID())
        {
            // igrac je na potezu
            titleLabel.setText("Vi ste na potezu");

        }
        else
        {
            // neko drugi je na potezu
            FigureColor color = FigureColor.values()[gameController.getTurn()];
            titleLabel.setText("Na potezu je: " + color);
        }

        drawTable();
        drawHouses();
        paintFiguresNotOnTable();

        repaint();
    }
}
