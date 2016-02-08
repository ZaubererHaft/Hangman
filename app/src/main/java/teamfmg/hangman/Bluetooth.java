package teamfmg.hangman;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;

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
        activateBluetooth();
    }

    /**
     * Start the BluetoothAdapter
     */
    private void activateBluetooth()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.enable();

        while (!bluetoothAdapter.isEnabled()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        uuid = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
        this.start();
    }

    /**
     */
    public void send(Object object) throws IOException
    {
        if(out != null)
        {
            out.writeObject("Hello from the Ather Side!");
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


    public List<BluetoothDevice> listBoundedDevices(){

        List<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
        // get paired devices
        pairedDevices = bluetoothAdapter.getBondedDevices();


        // put it's one to the list
        for(BluetoothDevice device : pairedDevices)
        {
            list.add(device);
        }
        return list;
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
