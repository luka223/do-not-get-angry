package graphic;

import logic.GameController;
import logic.ServerConnection;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ConnectScreen extends JPanel
{
    private MainWindow mainWindow;
    private JTextField ipAdressTextField;

    public ConnectScreen(MainWindow mainWindow)
    {
        this.mainWindow = mainWindow;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        createTextField();
        createButton();
    }

    private void createTextField()
    {
        JPanel panel = new JPanel();

        JLabel label = new JLabel("Unesite IP adresu za konkeciju sa serverom (default localhost): ");
        ipAdressTextField = new JTextField(20);

        Border line = BorderFactory.createLineBorder(Color.DARK_GRAY);
        Border empty = new EmptyBorder(5, 5, 5, 5);
        CompoundBorder border = new CompoundBorder(line, empty);
        ipAdressTextField.setBorder(border);

        panel.add(label);
        panel.add(ipAdressTextField);

        add(panel, BorderLayout.NORTH);
    }

    private void createButton()
    {
        JButton button = new JButton("Konektuj se");

        button.addActionListener(e ->
        {
            try
            {
                String ipAddress = ipAdressTextField.getText();
                if (ipAddress.equals(""))
                    ipAddress = "localhost";

                ServerConnection serverConnection = new ServerConnection(ipAddress);
                mainWindow.initializeGameScreen(serverConnection);
            }
            catch (Exception ex)
            {
                // ukoliko dodje do greske pri konekciji (ServerConnection konstruktor baca exception) bice ispsiana poruka o tome
                JOptionPane.showMessageDialog(null, "GRESKA: Konekcija nije uspesna!");
            }
        });

        add(button, BorderLayout.CENTER);
    }

}
