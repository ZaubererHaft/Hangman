package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class MultiplayerWifiMenu extends Activity implements IApplyableSettings, View.OnClickListener{


    private LinearLayout parent;
    private DatabaseManager db = DatabaseManager.getInstance();
    private List<MultiplayerGame> multiplayerGameList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_wifi_menu);
        this.changeBackground();

        //set onClicklistener
        findViewById(R.id.mpWifiMenu_button_createLobby).setOnClickListener(this);
        findViewById(R.id.mpWifiMenu_exit).setOnClickListener(this);
        findViewById(R.id.mpWifiMenu_button_reload).setOnClickListener(this);

        updateGames();
    }

    /**
     * Adds an include.
     * @since 1.3
     */
    private void updateGames()
    {
        parent = (LinearLayout)this.findViewById(R.id.mpWifiMenu_scrollView);
        parent.removeAllViews();

        multiplayerGameList = db.getAllMultiplayergames(MultiplayerGame.GameState.SEARCH4PLAYERS);
        for (int i = 0; i < multiplayerGameList.size(); i++){
            addInclude(multiplayerGameList.get(i).getGameName(), multiplayerGameList.get(i).getLeaderName(),
                    multiplayerGameList.get(i).getRoomPlayers(multiplayerGameList.get(i).getId()),
                    multiplayerGameList.get(i).getId());
        }
    }

    private void addInclude(String gameName, String leaderName, String playerAmount, long id)
    {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child = inflater.inflate(R.layout.new_mpgame_element, null, false);

        TextView viewGameName = (TextView)child.findViewById(R.id.mpGameElement_gameName);
        TextView viewLeaderName = (TextView)child.findViewById(R.id.mpGameElement_leaderName);
        TextView viewPlayerAmount = (TextView)child.findViewById(R.id.mpGameElement_playerAmount);
        TextView viewID = (TextView)child.findViewById(R.id.mpGameElement_id);


        viewGameName.setText(gameName);
        viewLeaderName.setText(leaderName);
        viewPlayerAmount.setText(playerAmount);
        viewID.setText(id + "");

        child.setOnClickListener(this);

        parent.addView(child);
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_mpWifiMenu);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {

        Intent i;
        OnlineGamePlayer newPlayer;

        switch (v.getId()){
            case R.id.mpWifiMenu_button_createLobby:

                //creating an new OnlineGame
                long id = System.currentTimeMillis()*10000 + LoginMenu.getCurrentUser(this).getId();
                String gameName = LoginMenu.getCurrentUser(this).getName() + "s Game";

                MultiplayerGame newGame = new MultiplayerGame(id, gameName, "", 2, LoginMenu.getCurrentUser(this).getName(),
                        MultiplayerGame.GameState.CREATING);

                MultiplayerWifiLobby.multiplayerGame = newGame;

                db.createOnlineGame(newGame);

                //creating an new OnlineGamePlayer
                newPlayer = new OnlineGamePlayer(newGame.getId(), LoginMenu.getCurrentUser(this).getId(),
                        0, OnlineGamePlayer.PlayerState.LEADER);

                MultiplayerWifiLobby.onlineGamePlayer = newPlayer;

                db.createOnlineGamePlayer(newPlayer);

                //start activity
                i = new Intent(this, MultiplayerWifiLobby.class);

                i.putExtra("createLobby", true);

                this.startActivity(i);

                break;


            case R.id.mpWifiMenu_exit:
                this.finish();
                break;

            case R.id.mpWifiMenu_button_reload:
                this.updateGames();
                break;

            default:
                long idSelectedGame = Long.parseLong(((TextView)v.findViewById(R.id.mpGameElement_id)).getText().toString());
                //creating an new OnlineGamePlayer
                if (db.onlineGameIsFree(idSelectedGame))
                {
                    newPlayer = new OnlineGamePlayer(idSelectedGame, LoginMenu.getCurrentUser(this).getId(),
                            0, OnlineGamePlayer.PlayerState.JOINED);

                    MultiplayerWifiLobby.onlineGamePlayer = newPlayer;

                    db.createOnlineGamePlayer(newPlayer);

                    MultiplayerWifiLobby.multiplayerGame = db.getMultiplayergame(idSelectedGame);
                    //start activity
                    i = new Intent(this, MultiplayerWifiLobby.class);

                    this.startActivity(i);
                }
                else
                {
                    Logger.write("Game is full or not avaiable!", this);
                }

        }
    }
}
