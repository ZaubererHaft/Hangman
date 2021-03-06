package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class multiplayerSettings extends Activity implements IApplyableSettings, View.OnClickListener {

    private DatabaseManager db = DatabaseManager.getInstance();
    private EditText editPassword, editMaxPlayers, editName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_settings);

        db.setActivity(this);

        this.findViewById(R.id.mpSettings_button_apply).setOnClickListener(this);

        this.editName = (EditText)this.findViewById(R.id.mpSettings_editText_gameName);
        this.editMaxPlayers = (EditText)this.findViewById(R.id.mpSettings_editText_gameMaxPlayers);
        this.editPassword = (EditText)this.findViewById(R.id.mpSettings_editText_gamePW);

        this.editName.setText(MultiplayerWifiLobby.multiplayerGame.getGameName());
        this.editMaxPlayers.setText(MultiplayerWifiLobby.multiplayerGame.getMaxPlayers() + "");

        this.changeBackground();
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl = (RelativeLayout) this.findViewById(R.id.relLayout_multiplayerSettings);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mpSettings_button_apply:

                if (editName.getText() != null && editName.length() != 0)
                {
                    MultiplayerWifiLobby.multiplayerGame.setGameName(this.editName.getText().toString());
                }
                if (editPassword.getText() != null && editPassword.length() != 0)
                {
                    MultiplayerWifiLobby.multiplayerGame.setPassword(this.editPassword.getText().toString());
                }
                if (editMaxPlayers.getText() != null && editMaxPlayers.length() != 0)
                {
                    try
                    {
                        if (Integer.parseInt(this.editMaxPlayers.getText().toString()) < 2)
                        {
                            Logger.write(this.getResources().getText(R.string.mpSettings_error_maxPlayersAmount), this);
                        }
                        else
                        {
                            MultiplayerWifiLobby.multiplayerGame.setMaxPlayers(Integer.parseInt(this.editMaxPlayers.getText().toString()));
                        }
                    }
                    catch (NumberFormatException ex)
                    {
                        Logger.write(this.getResources().getText(R.string.mpSettings_error_maxPlayersNoValidNumber), this);
                    }
                }

                this.db.updateOnlineGame(MultiplayerWifiLobby.multiplayerGame);

                this.finish();
                break;

            case R.id.mpSettings_close:
                this.finish();
                break;
        }
    }
}
