package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
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

    private boolean isLeader;
    private Button startButton;
    private DatabaseManager db = DatabaseManager.getInstance();
    public static MultiplayerGame multiplayerGame = null;
    public static OnlineGamePlayer onlineGamePlayer = null;
    private LinearLayout parent;
    /**
     * Count for childs
     */
    private int count = 0;
    /**
     * boolean to stop the thread
     */
    private static boolean doUpdate;
    /**
     * boolean if is onCreate
     * needed because android lifecycle -> onResume always used
     */
    private boolean isOnCreate;
    private boolean gameIsStarting = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_wifi_lobby);

        //changing Backround
        changeBackground();

        //set Activity for db
        db.setActivity(this);

        //say that activity isOnCreate
        isOnCreate = true;

        //gets the extras
        Bundle extra = this.getIntent().getExtras();

        //inits the customButton
        startButton = (Button) this.findViewById(R.id.mpWifiLobby_button_startGame);


        if (extra != null)
        {
            //sets the activity for leaders
            isLeader = extra.getBoolean("createLobby");
        }
        else
        {
            isLeader = false;
        }
        
        if (isLeader && multiplayerGame.getGameState() == MultiplayerGame.GameState.CREATING)
        {
            //updating buttons
            startButton.setText("Search for Players");
        }
        else if (isLeader && multiplayerGame.getGameState() == MultiplayerGame.GameState.SEARCH4PLAYERS)
        {
            //updating buttons
            startButton.setText("Start");
        }
        else
        {
            //updating Buttons
            startButton.setText("Ready");
        }

        //set onClicklistener
        findViewById(R.id.mpWifiLobby_button_settings).setOnClickListener(this);
        findViewById(R.id.mpWifiLobby_exit).setOnClickListener(this);
        startButton.setOnClickListener(this);

        //updating the Playerlist
        updaterPlayerList();
    }

    private void updateLayout()
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                //init parent View
                parent = (LinearLayout) findViewById(R.id.mpWifiLobby_scrollView);

                //clear parent View
                parent.removeAllViews();
                count = 0;

                //updates the game for non Leaders
                if (!isLeader)
                {
                    multiplayerGame = db.getMultiplayergame(MultiplayerWifiLobby.multiplayerGame.getId());
                }


                //List with all Players
                List<OnlineGamePlayer> onlineGamePlayers = db.getAllMultiplayergamePlayers(multiplayerGame.getId());

                boolean allReady = true;

                //Add Player to the View and
                //Checking if every player is ready or leader
                for (int i = 0; i < onlineGamePlayers.size(); i++)
                {
                    if (onlineGamePlayers.get(i).getPlayerState() != OnlineGamePlayer.PlayerState.READY &&
                            onlineGamePlayers.get(i).getPlayerState() != OnlineGamePlayer.PlayerState.LEADER )
                    {
                        allReady = false;
                    }

                    addInclude(onlineGamePlayers.get(i));
                }

                //updating the customButton for the Leader
                if (startButton.getText().toString().equals("Start") && !allReady)
                {
                    startButton.setEnabled(false);
                }
                else if (isLeader)
                {
                    startButton.setEnabled(true);
                }

                //disable the settings button
                if (multiplayerGame.getGameState() == MultiplayerGame.GameState.SEARCH4PLAYERS)
                {
                    findViewById(R.id.mpWifiLobby_button_settings).setEnabled(false);
                }

                //starts the multiplayergame if gamestate = INGAME
                if (multiplayerGame.getGameState() == MultiplayerGame.GameState.INGAME)
                {
                    startMultiplayerGame();
                }

                //close Lobby if finished
                if (multiplayerGame.getGameState() == MultiplayerGame.GameState.FINISHED)
                {
                    finish();
                }
            }
        });
    }

    /**
     * starting the "real" Game
     */
    private void startMultiplayerGame()
    {
        //Starting singleplayer class
        //with some extras
        Intent i = new Intent(this, Singleplayer.class);
        Singleplayer.gameMode = Singleplayer.GameMode.HARDCORE;
        i.putExtra("multiplayerGameID", multiplayerGame.getId());
        i.putExtra("multiplayerGameName", multiplayerGame.getGameName());
        this.startActivity(i);
        gameIsStarting = true;
        this.finish();
    }

    /**
     * adding child views to the scrollview
     * @param onlineGamePlayer
     */
    private void addInclude(OnlineGamePlayer onlineGamePlayer) {
        count++;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child = inflater.inflate(R.layout.new_scoreboard_element, null, false);

        //inits child view elements
        TextView viewPosition = (TextView) child.findViewById(R.id.scoreboardElement_Rank);
        TextView viewName = (TextView) child.findViewById(R.id.scoreboardElement_Username);
        TextView viewState = (TextView) child.findViewById(R.id.scoreboardElement_Score);

        //sets the text of the child view elements
        viewPosition.setText(String.valueOf(count));
        viewName.setText(db.getUser(onlineGamePlayer.getUserID()).getName());
        viewState.setText(onlineGamePlayer.getPlayerState().name());

        //shows the leader in Bold
        if (onlineGamePlayer.getPlayerState() == OnlineGamePlayer.PlayerState.LEADER)
        {
            viewPosition.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Rank)).getTypeface(), Typeface.BOLD);
            viewName.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Username)).getTypeface(), Typeface.BOLD);
            viewState.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Score)).getTypeface(), Typeface.BOLD);
        }

        //Shows the ready players in green
        if (onlineGamePlayer.getPlayerState() == OnlineGamePlayer.PlayerState.READY) {
            viewPosition.setTextColor(this.getResources().getColor(R.color.color_Green));
            viewName.setTextColor(this.getResources().getColor(R.color.color_Green));
            viewState.setTextColor(this.getResources().getColor(R.color.color_Green));

        }

        //set Clicklistener
        child.setOnClickListener(this);

        //add child to parent
        parent.addView(child);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //stops the autoUpdate
        doUpdate = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!gameIsStarting)
        {
            db.deleteOnlineGamePlayer(onlineGamePlayer);
        }

        if (isLeader && !gameIsStarting){
            multiplayerGame.setGameState(MultiplayerGame.GameState.FINISHED);
            db.updateOnlineGame(multiplayerGame);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (isOnCreate)
        {
            isOnCreate = false;
            return;
        }
        updaterPlayerList();

        multiplayerGame.setGameState(MultiplayerGame.GameState.CREATING);
        db.updateOnlineGame(multiplayerGame);
        startButton.setText("Search for Players");

        /*
        if (db.onlineGameIsFree(multiplayerGame.getId()))
        {
            db.createOnlineGamePlayer(onlineGamePlayer);
        }
        else
        {
            this.finish();
        }
        */
    }


    private void updaterPlayerList() {

        doUpdate = true;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                while (doUpdate)
                {
                    updateLayout();

                    try
                    {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        t.start();
    }


    @Override
    public void changeBackground()
    {
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
                if (multiplayerGame.getGameState() == MultiplayerGame.GameState.CREATING && isLeader) {
                    startButton.setText("Start");
                    multiplayerGame.setGameState(MultiplayerGame.GameState.SEARCH4PLAYERS);
                    db.updateOnlineGame(multiplayerGame);
                    startButton.setEnabled(false);
                } else if (multiplayerGame.getGameState() == MultiplayerGame.GameState.SEARCH4PLAYERS && isLeader) {
                    multiplayerGame.setGameState(MultiplayerGame.GameState.INGAME);
                    db.updateOnlineGame(multiplayerGame);
                } else {
                    startButton.setEnabled(false);
                    onlineGamePlayer.setPlayerState(OnlineGamePlayer.PlayerState.READY);
                    db.updateOnlineGamePlayer(onlineGamePlayer);
                }

                break;

            case R.id.mpWifiLobby_button_settings:
                Intent i = new Intent(this, multiplayerSettings.class);
                this.startActivity(i);
                break;
        }
    }
}