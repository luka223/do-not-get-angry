package logic;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

public class ServerConnection
{
    // Klasa za konekciju i razmenu poruka sa serverom
    private static final int PORT = 10000;

    private Socket socket;
    private BufferedReader input;
    private PrintStream output;

    public ServerConnection(String ipAddress) throws Exception
    {
        try
        {
            socket = new Socket(ipAddress, PORT);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintStream(socket.getOutputStream());
        }
        catch (IOException e)
        {
            throw new Exception("Problem sa povezivanjem na server");
        }

    }

    void sendMessage(JSONObject jsonObject)
    {
        try
        {
            output.println(jsonObject.toString());
        }
        catch (Exception e)
        {
            System.err.println("Greska pri slanju poruke");
        }

    }

    JSONObject recieveMesage()
    {
        try
        {
            return new JSONObject(input.readLine());
        }
        catch (IOException e)
        {
            return null;
        }
    }
}
