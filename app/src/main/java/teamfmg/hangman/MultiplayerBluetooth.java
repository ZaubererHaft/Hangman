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
import java.util.ArrayList;

public class MultiplayerBluetooth extends Activity implements IApplyableSettings, View.OnClickListener
{

    private BluetoothAdapter adapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private BroadcastReceiver receiver = null;
    private final ArrayList<BluetoothDevice> devices = new ArrayList<>();
    private static final int VISIBLETIME = 300;
    private BluetoothDevice bondedDevice;

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
            Logger.messageDialog("NO_BLUETOOTH","NO_BLUETOOTH",this);
            this.finish();
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
    private void listDevices()
    {
        LinearLayout ll = (LinearLayout)this.findViewById(R.id.subLayout_bluetooth);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        for (BluetoothDevice d:devices)
        {
            View child = inflater.inflate(R.layout.device, null, false);
            ((Button)child.findViewById(R.id.device_button)).setText(d.getAddress());
            child.findViewById(R.id.device_button).setOnClickListener(this);
            ll.addView(child);
        }

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
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    devices.add(device);
                }

                listDevices();
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
    public void createBond(BluetoothDevice btDevice) throws Exception
    {
        Logger.logOnly("Bonding to device: "+btDevice.getAddress());
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        Logger.logOnly("Bonding was " + returnValue);
    }

    /**
     * Removes the connection to a device.
     * @param btDevice
     * @throws Exception
     */
    public void removeBond(BluetoothDevice btDevice) throws Exception
    {
        Logger.logOnly("Unbond from device: "+btDevice.getAddress());
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        Logger.logOnly("Remove bonding was " + returnValue);
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

            BluetoothServer bs = new BluetoothServer(this.adapter);

        }
        if(v.getId() == R.id.bluetooth_scan)
        {
            ToggleButton tb = (ToggleButton) this.findViewById(R.id.bluetooth_scan);

            if(tb.isChecked())
            {
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
                    this.removeBond(bondedDevice);
                    this.bondedDevice = null;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            Button b = (Button)this.findViewById(R.id.device_button);

            /**
             * Connect to new device
             */
            for (BluetoothDevice d: devices)
            {
                if(d.getAddress().equals(b.getText().toString()))
                {
                    try
                    {
                        this.createBond(d);
                        this.bondedDevice = d;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    break;
                }
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
