package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class MultiplayerWifiLobby extends Activity implements IApplyableSettings, View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_wifi_lobby);
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_mpWifiMenu);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {

    }
}
