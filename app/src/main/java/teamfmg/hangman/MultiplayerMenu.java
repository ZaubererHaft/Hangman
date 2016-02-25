package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class MultiplayerMenu extends Activity implements IApplyableSettings, View.OnClickListener {

    private Bluetooth bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_menu);
        changeBackground();

        //ClickListener
        this.findViewById(R.id.multiplayer_close).setOnClickListener(this);
        this.findViewById(R.id.multiplayerMenu_button_wifi).setOnClickListener(this);
        this.findViewById(R.id.multiplayerMenu_button_bluetooth).setOnClickListener(this);
        this.findViewById(R.id.multiplayerMenu_button_local).setOnClickListener(this);

        //Updating the lastOnline Time
        DatabaseManager db = DatabaseManager.getInstance();
        db.setActivity(this);
        db.updateLastOnline();
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_multiplayerMenu);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v)
    {
        Intent i;

        switch (v.getId()){
            case R.id.multiplayer_close:
                this.finish();
                break;

            /*
            case R.id.button_bluetooth:
                i = new Intent(this, BluetoothMenu.class);
                this.startActivity(i);
                break;
            */

            case R.id.multiplayerMenu_button_wifi:
                i = new Intent(this, MultiplayerWifiMenu.class);
                this.startActivity(i);
                break;

            case R.id.multiplayerMenu_button_local:
                i = new Intent(this, multiplayer_local_lobby.class);
                this.startActivity(i);
                break;

            default:
                Logger.write("Currently no function", this);
        }

    }

    /*
    public void Bluetooth()
    {
        Set<BluetoothDevice> pairedDevices;

        pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList list = new ArrayList(pairedDevices);

        String text = "";

        for(int x = 0; x < pairedDevices.size(); x++)
        {
            BluetoothDevice device = (BluetoothDevice)list.get(x);

            if (device.getName().contains("Yoga"))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    device.createBond();
                    Logger.logOnly("Beginn Paring");
                }
            }

            text = text + device.getName();
        }


        Logger.write(text, this);

    }
    */
}
