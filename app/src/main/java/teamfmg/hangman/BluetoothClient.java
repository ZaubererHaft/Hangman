package teamfmg.hangman;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Created by consult on 2/22/16.
 */
public class  BluetoothClient extends Thread
{
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private BluetoothAdapter adapter;
    private final UUID uuid = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    public BluetoothClient(BluetoothDevice device, BluetoothAdapter adapter)
    {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.adapter = adapter;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try
        {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(uuid);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        mmSocket = tmp;

        this.start();
    }

    public void run()
    {
        Logger.logOnly("Client started!");

        // Cancel discovery because it will slow down the connection
        adapter.cancelDiscovery();

        try
        {
            Logger.logOnly("Searching for servers...");
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            mmSocket.connect();
        }
        catch (IOException connectException)
        {
            connectException.printStackTrace();
            // Unable to connect; close the socket and get out
            try
            {
                mmSocket.close();
            }
            catch (IOException closeException)
            {
                closeException.printStackTrace();
            }
            return;
        }

        Logger.logOnly("Listening for input...");

        while (true)
        {
            try
            {

                InputStream is = mmSocket.getInputStream();

                if(is != null)
                {
                    Logger.logOnly("Incoming message: "+is.read());
                }

                Thread.sleep(1000);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        // Do work to manage the connection (in a separate thread)
        //manageConnectedSocket(mmSocket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel()
    {
        try
        {
            mmSocket.close();
        }
        catch (IOException e)
        {
        }
    }
}