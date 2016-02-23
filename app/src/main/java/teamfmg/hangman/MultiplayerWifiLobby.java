package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MultiplayerWifiLobby extends Activity implements IApplyableSettings, View.OnClickListener{

    private boolean isCreator;
    private Button startButton;
    private DatabaseManager db = DatabaseManager.getInstance();
    public static MultiplayerGame multiplayerGame = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_wifi_lobby);

        Bundle extra = this.getIntent().getExtras();

        startButton = (Button)this.findViewById(R.id.mpWifiLobby_button_startGame);


        if (extra != null)
        {
            isCreator = extra.getBoolean("createLobby");
        }
        else
        {
            isCreator = false;
        }

        if (isCreator)
        {
            startButton.setText("Search for Players");
        }
        else
        {
            startButton.setText("Ready");
        }

        //set onClicklistener
        findViewById(R.id.mpWifiLobby_button_settings).setOnClickListener(this);
        findViewById(R.id.mpWifiLobby_exit).setOnClickListener(this);
        startButton.setOnClickListener(this);
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_mpWifiLobby);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.mpWifiLobby_exit:
                this.finish();
                break;
            case R.id.mpWifiLobby_button_startGame:
                if (startButton.getText().equals("Search for Players"))
                {
                    startButton.setText("Start");
                    multiplayerGame.setGameState(MultiplayerGame.GameState.SEARCH4PLAYERS);
                    db.updateOnlineGame(multiplayerGame);
                }
                else if (startButton.getText().equals("Start"))
                {
                    multiplayerGame.setGameState(MultiplayerGame.GameState.INGAME);
                    db.updateOnlineGame(multiplayerGame);
                    //TODO start Game
                }
                else
                {
                    //TODO Bereit setzen, Button Deaktivieren, status dauerhaft überprüfen
                }

                break;
            case R.id.mpWifiLobby_button_settings:
                //TODO Einstellungmenü
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        multiplayerGame.setGameState(MultiplayerGame.GameState.FINISHED);
        db.updateOnlineGame(multiplayerGame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        multiplayerGame.setGameState(MultiplayerGame.GameState.SEARCH4PLAYERS);
        db.updateOnlineGame(multiplayerGame);
    }
}
