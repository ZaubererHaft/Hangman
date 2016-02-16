package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class multiplayer_local_lobby extends Activity implements IApplyableSettings, View.OnClickListener{

    private int idInclude = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_local_lobby);
        changeBackground();

        //ClickListener
        findViewById(R.id.multiplayer_local_lobby_addPlayer).setOnClickListener(this);
        findViewById(R.id.multiplayer_local_lobby_removePlayer).setOnClickListener(this);
        findViewById(R.id.button_multiplayer_local_lobby_exit).setOnClickListener(this);
        findViewById(R.id.multiplayer_local_lobby_button_play).setOnClickListener(this);
    }

    private void addInclude()
    {
        LinearLayout parent = (LinearLayout)this.findViewById(R.id.multiplayer_local_lobby_scrollView_playerList);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child = inflater.inflate(R.layout.new_player_element, null, false);

        ((TextView)child.findViewById(R.id.new_player_element_playerXY)).setText("Player " + idInclude + 1);

        idInclude++;

        parent.addView(child);
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_multiplayer_local_lobby);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.button_multiplayer_local_lobby_exit:
                this.finish();
                break;

            case R.id.multiplayer_local_lobby_addPlayer:
                this.addInclude();
                break;

            case R.id.multiplayer_local_lobby_removePlayer:
                break;

            default:
                Logger.write("Currently no function", this);
        }
    }
}
