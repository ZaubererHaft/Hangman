package teamfmg.hangman;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 *
 * Created by Ludwig on 21.02.2016.
 */
public class BluetoothServer extends Thread
{

    private final BluetoothServerSocket mmServerSocket;
    private final UUID uuid = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private final String appName = "HANGMAN";

    private BluetoothSocket client;

    public BluetoothServer(BluetoothAdapter adapter)
    {

        // Use a temporary object that is later assigned to mmServerSocket,
        // because mmServerSocket is final
        BluetoothServerSocket tmp = null;
        try
        {
            // MY_UUID is the app's UUID string, also used by the client code
            tmp = adapter.listenUsingRfcommWithServiceRecord(this.appName, this.uuid);
        }
        catch (IOException e)
        {
        }
        mmServerSocket = tmp;

        this.start();

    }


    public void run()
    {
        Logger.logOnly("Server started!");

        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true)
        {
            try
            {
                Logger.logOnly("Waiting for clients...");
                socket = mmServerSocket.accept();
                client = socket;

                OutputStream os = client.getOutputStream();

                Logger.logOnly("Cient added: " + client.toString());

                String s = "Willkommen!";
                os.write(s.getBytes());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                break;
            }

            // If a connection was accepted
            if (socket != null)
            {
                try
                {
                    //manageConnectedSocket(socket);
                    mmServerSocket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /** Will cancel the listening socket, and cause the thread to finish */
    public void cancel()
    {
        try
        {
            mmServerSocket.close();
        }
        catch (IOException e)
        {
        }
    }
}
