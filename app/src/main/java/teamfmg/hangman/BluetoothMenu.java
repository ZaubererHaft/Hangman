package teamfmg.hangman;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BluetoothMenu extends Activity implements IApplyableSettings, View.OnClickListener {

    private Bluetooth bt;
    private List<BluetoothDevice> boundedDevices;
    private LinearLayout linearLayout;
    private List<Button> deviceButtons;
    /**
     * Amount of created Checkboxes
     */
    private int viewsCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_menu);
        changeBackground();
        bt = new Bluetooth(this);

        //init
        linearLayout = (LinearLayout) findViewById(R.id.bluetoothLinearLayoutForBoundedBluetoothDevices);
        deviceButtons = new ArrayList<Button>();

        //Updating the lastOnline Time
        DatabaseManager db = DatabaseManager.getInstance();
        db.updateLastOnline();

        //ClickListener
        findViewById(R.id.bluetooth_close).setOnClickListener(this);

        createDevicesButtons();

    }

    private void createDevicesButtons(){
        boundedDevices = bt.listBoundedDevices();

        for (int i = 0; i < boundedDevices.size(); i++){
            Button b = new Button(this);
            b.setId(this.viewsCount);
            this.viewsCount++;
            linearLayout.addView(b);
            b.setText(boundedDevices.get(i).getName());
            b.setOnClickListener(this);
            this.deviceButtons.add(b);
        }
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_bluetoothMenu);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bluetooth_close:
                this.finish();
                break;
            default:
                int i;
                for (i = 0; i < deviceButtons.size(); i++){
                    if (deviceButtons.get(i).getId() == v.getId()){
                        break;
                    }
                }
                try
                {
                    bt.createConnection(boundedDevices.get(i).getAddress());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        bt.closeConnection();
    }
}
