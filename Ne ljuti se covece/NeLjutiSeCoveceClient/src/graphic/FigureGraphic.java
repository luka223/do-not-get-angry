package graphic;

import logic.Figure;
import logic.GameController;

import javax.swing.*;
import java.awt.*;

public class FigureGraphic extends JButton
{
    private Figure figure;

    FigureGraphic(Figure figure, GameController gameController, GameScreen gameScreen)
    {
        this.figure = figure;

        addActionListener(e ->
        {
            boolean b = gameController.moveFigure(figure.getID());
            if (b == false)
                JOptionPane.showMessageDialog(null, "Nije moguce odigrati ovaj potez");
            else
                gameScreen.playMove();
        });
    }

//    public Figure getFigure()
//    {
//        return figure;
//    }
}
