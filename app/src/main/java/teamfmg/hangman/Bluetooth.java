package teamfmg.hangman;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Vincent on 27.01.2016.
 */
public class Bluetooth extends Thread{

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket bluetoothServerSocket;
    private UUID uuid;
    private Activity context;
    private ArrayAdapter<String> BTArrayAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothSocket socket;
    ObjectOutputStream out;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    List<BluetoothDevice> devicelist = new ArrayList<BluetoothDevice>();

    public Bluetooth(Context context)
    {
        this.context = (Activity)context;
    }

    /**
     */
    public void send(Object object)
    {
        if(out != null)
        {
            try
            {

                out.writeObject("Hello from the other Side!");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createConnection(String address) throws IOException
    {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        BluetoothSocket outSocket = device.createRfcommSocketToServiceRecord(uuid);
        outSocket.connect();
        out = new ObjectOutputStream(outSocket.getOutputStream());
        out.writeObject(bluetoothAdapter);
    }



    @Override
    public void run() {
        while (!isInterrupted())
        {
            try
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1 && bluetoothServerSocket == null)
                {
                    bluetoothServerSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("Input", uuid);
                }

                socket = bluetoothServerSocket.accept();
                socket.connect();

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                Object massage = in.readObject();

                Logger.logOnly(massage);


            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }


    public void closeConnection(){
        try
        {
            out.close();
            this.interrupt();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}