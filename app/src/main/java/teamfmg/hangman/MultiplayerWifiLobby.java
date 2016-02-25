package teamfmg.hangman;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class MultiplayerWifiLobby extends Activity implements IApplyableSettings, View.OnClickListener {

    private boolean isCreator;
    private Button startButton;
    private DatabaseManager db = DatabaseManager.getInstance();
    public static MultiplayerGame multiplayerGame = null;
    public static OnlineGamePlayer onlineGamePlayer = null;
    private LinearLayout parent;
    private int count = 0;
    private UpdaterPlayerList updaterPlayerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_wifi_lobby);

        Bundle extra = this.getIntent().getExtras();

        startButton = (Button) this.findViewById(R.id.mpWifiLobby_button_startGame);


        if (extra != null) {
            isCreator = extra.getBoolean("createLobby");
        } else {
            isCreator = false;
        }

        if (isCreator && multiplayerGame.getGameState() == MultiplayerGame.GameState.CREATING) {
            startButton.setText("Search for Players");
        } else if (isCreator && multiplayerGame.getGameState() == MultiplayerGame.GameState.SEARCH4PLAYERS) {
            startButton.setText("Start");
        } else {
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
        RelativeLayout rl = (RelativeLayout) this.findViewById(R.id.relLayout_mpWifiLobby);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.mpWifiLobby_exit:
                this.finish();
                break;
            case R.id.mpWifiLobby_button_startGame:
                if (multiplayerGame.getGameState() == MultiplayerGame.GameState.CREATING) {
                    startButton.setText("Start");
                    multiplayerGame.setGameState(MultiplayerGame.GameState.SEARCH4PLAYERS);
                    db.updateOnlineGame(multiplayerGame);
                } else if (multiplayerGame.getGameState() == MultiplayerGame.GameState.SEARCH4PLAYERS) {
                    multiplayerGame.setGameState(MultiplayerGame.GameState.INGAME);
                    db.updateOnlineGame(multiplayerGame);
                    //TODO start Game
                } else {
                    //TODO Bereit setzen, Button Deaktivieren, status dauerhaft überprüfen
                }

                break;

            case R.id.mpWifiLobby_button_settings:
                //TODO Einstellungmenü
                break;
        }
    }

    protected void updatePlayers() {
        parent = (LinearLayout) this.findViewById(R.id.mpWifiLobby_scrollView);
        parent.removeAllViews();
        count = 0;

        List<OnlineGamePlayer> onlineGamePlayers = db.getAllMultiplayergamePlayers(multiplayerGame.getId());

        for (int i = 0; i < onlineGamePlayers.size(); i++) {
            addInclude(onlineGamePlayers.get(i));
        }
    }

    private void addInclude(OnlineGamePlayer onlineGamePlayer) {
        count++;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child = inflater.inflate(R.layout.new_scoreboard_element, null, false);

        TextView viewPosition = (TextView) child.findViewById(R.id.scoreboardElement_Rank);
        TextView viewName = (TextView) child.findViewById(R.id.scoreboardElement_Username);
        TextView viewState = (TextView) child.findViewById(R.id.scoreboardElement_Score);

        viewPosition.setText(String.valueOf(count));
        viewName.setText(db.getUser(onlineGamePlayer.getUserID()).getName());
        viewState.setText(onlineGamePlayer.getPlayerState().name());


        if (onlineGamePlayer.getPlayerState() == OnlineGamePlayer.PlayerState.LEADER) {
            viewPosition.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Rank)).getTypeface(), Typeface.BOLD);
            viewName.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Username)).getTypeface(), Typeface.BOLD);
            viewState.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Score)).getTypeface(), Typeface.BOLD);
        }
        if (onlineGamePlayer.getPlayerState() == OnlineGamePlayer.PlayerState.READY) {
            viewPosition.setTextColor(this.getResources().getColor(R.color.color_Green));
            viewName.setTextColor(this.getResources().getColor(R.color.color_Green));
            viewState.setTextColor(this.getResources().getColor(R.color.color_Green));

        }

        child.setOnClickListener(this);

        parent.addView(child);
    }

    @Override
    protected void onPause() {
        super.onPause();

        multiplayerGame.setGameState(MultiplayerGame.GameState.FINISHED);
        db.updateOnlineGame(multiplayerGame);
        updaterPlayerList.interrupt();
    }

    @Override
    protected void onResume() {
        super.onResume();

            multiplayerGame.setGameState(MultiplayerGame.GameState.CREATING);
            db.updateOnlineGame(multiplayerGame);
            startButton.setText("Search for Players");


        if (updaterPlayerList == null)
        {
            updaterPlayerList = new UpdaterPlayerList();
        }

        if (updaterPlayerList.isInterrupted())
        {
            updaterPlayerList.run();
        }
    }

    private class UpdaterPlayerList extends Thread {

        MultiplayerWifiLobby lobby;

        public UpdaterPlayerList()
        {
            lobby = new MultiplayerWifiLobby();
        }

        @Override
        public void run() {

            while (!isInterrupted())
            {
                try
                {
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                lobby.updatePlayers();
            }
        }
    }
}