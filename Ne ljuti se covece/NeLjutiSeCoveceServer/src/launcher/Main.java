package launcher;

import logic.GameEngine;

import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);
        int numberOfPlayers;
        System.out.println("Unesite broj igraca: ");
        do
        {
            numberOfPlayers = scanner.nextInt();
            if (numberOfPlayers < 2 || numberOfPlayers > 4)
                System.out.println("Broj igraca mora biti izmedju 2 i 4. Unesite broj ponovo: ");

        } while (numberOfPlayers < 2 || numberOfPlayers > 4);

        GameEngine gameEngine = new GameEngine(numberOfPlayers);
        gameEngine.startGame();
    }
}
