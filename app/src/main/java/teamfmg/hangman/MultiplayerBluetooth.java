package teamfmg.hangman;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MultiplayerBluetooth extends Activity implements IApplyableSettings, View.OnClickListener
{

    private BluetoothAdapter adapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private BroadcastReceiver receiver = null;
    //private final ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private static final int VISIBLETIME = 300;
    private BluetoothDevice bondedDevice;

    //rivate ArrayList <BluetoothDevice> devices = new ArrayList<>();

    private HashMap<Button, BluetoothDevice> devices = new HashMap<>();

    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_bluetooth);

        this.setUpGUI();

        //Check if Bluetooth is available
        if(this.checkBluetoothCompatibility())
        {
            //activates Bluetooth
            this.enableBluetooth();
            this.addReceiver();
        }
        else
        {
            Logger.messageDialog("NO_BLUETOOTH", "NO_BLUETOOTH", this);
        }
    }

    /**
     * Checks whether a device supports Bluetooth.
     * @return {@link Boolean}
     */
    private boolean checkBluetoothCompatibility()
    {
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        return this.adapter != null;
    }

    /**
     * Cancels finding of devices.
     */
    private void startDiscovery()
    {
        Logger.logOnly("Searching for devices...");
        this.adapter.startDiscovery();
    }

    /**
     * Cancels discovering of devices.
     */
    private void cancelDiscovery()
    {
        Logger.logOnly("Cancel search...");
        this.adapter.cancelDiscovery();
    }

    /**
     * Lists all devices to the GUI
     */
    private Button addDeviceToLayout(BluetoothDevice device)
    {
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.subLayout_bluetooth);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child = inflater.inflate(R.layout.device, null, false);

        Button b = ((Button) child.findViewById(R.id.device_button));

        b.setText(device.getName());
        b.setOnClickListener(this);
        ll.addView(child);

        return b;

    }

    /**
     * Adds an event receiver to find devices.
     */
    private void addReceiver()
    {
        // Create a BroadcastReceiver for ACTION_FOUND
        this.receiver = new BroadcastReceiver()
        {
            public void onReceive(Context context, Intent intent)
            {
                Logger.logOnly("Device added!");

                String action = intent.getAction();
                // When discovery finds a device

                if (BluetoothDevice.ACTION_FOUND.equals(action))
                {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    Button b = addDeviceToLayout(device);
                    devices.put(b, device);

                }

            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(this.receiver, filter);
    }

    /**
     * Activates the Bluetooth of the device.
     */
    private void enableBluetooth ()
    {
        if (!this.adapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, MultiplayerBluetooth.REQUEST_ENABLE_BT);
        }
    }

    /**
     * Makes the own device visible
     */
    private void makeDeviceVisible()
    {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
                MultiplayerBluetooth.VISIBLETIME);
        this.startActivity(discoverableIntent);
    }

    /**
     * Bonds to a bluetooth device.
     * @param btDevice Device to bound.

     * @throws Exception
     */
    public boolean createBond(BluetoothDevice btDevice) throws Exception
    {
        Logger.logOnly("Bonding to device: "+btDevice.getAddress());
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        Logger.logOnly("Bonding was " + returnValue);
        return returnValue;
    }

    /**
     * Removes the connection to a device.
     * @param btDevice
     * @return succesfully bonded
     * @throws Exception
     */
    public boolean removeBond(BluetoothDevice btDevice) throws Exception
    {
        Logger.logOnly("Unbond from device: "+btDevice.getAddress());
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        Logger.logOnly("Remove bonding was " + returnValue);
        return returnValue;
    }

    /**
     * Rmoves all entries from the screen.
     */
    public void clearScreen()
    {
        this.devices = new HashMap<>();
        LinearLayout ll = (LinearLayout) this.findViewById(R.id.subLayout_bluetooth);
        ll.removeAllViews();
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.bluetooth_close)
        {
            this.finish();
        }
        if(v.getId() == R.id.bluetooth_host)
        {
            this.makeDeviceVisible();

            //BluetoothServer bs = new BluetoothServer(this.adapter);
            Bluetooth bs = new Bluetooth(this);
            bs.send("Hallo");

        }
        if(v.getId() == R.id.bluetooth_scan)
        {

            ToggleButton tb = (ToggleButton) this.findViewById(R.id.bluetooth_scan);

            if(tb.isChecked())
            {
                this.clearScreen();
                this.startDiscovery();
            }
            else
            {
                this.cancelDiscovery();
            }
        }
        /**
         * Connects to a device
         */
        if(v.getId() == R.id.device_button)
        {

            /**
             * Remove if there was a connection.
             */
            if(this.bondedDevice != null)
            {
                try
                {
                    boolean b = this.removeBond(bondedDevice);
                    this.bondedDevice = null;

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            try
            {
                boolean b = this.createBond(this.devices.get(v));
                this.bondedDevice = this.devices.get(v);
                //BluetoothClient bc = new BluetoothClient(this.bondedDevice, adapter);
                Bluetooth bs = new Bluetooth(this);

                if(b)
                {
                    Logger.write("Successfully bonded!", this);

                }
                else
                {
                    Logger.write("Error while bonding!", this);
                }


            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    /**
     * Do some gui set-up. (Add ClickListener etc.)
     */
    private void setUpGUI()
    {
        this.findViewById(R.id.bluetooth_close).setOnClickListener(this);
        this.findViewById(R.id.bluetooth_host).setOnClickListener(this);
        this.findViewById(R.id.bluetooth_scan).setOnClickListener(this);

        this.changeBackground();
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_Bluetooth);
        Settings.setColor(rl);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();

        if(receiver != null)
        {
            this.unregisterReceiver(receiver);
        }
    }

}
