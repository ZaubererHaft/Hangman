package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class MultiplayerWifiMenu extends Activity implements IApplyableSettings, View.OnClickListener{



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

        updateGames();
    }

    /**
     * Adds an include.
     * @since 1.3
     */
    private void updateGames()
    {
        multiplayerGameList = db.getAllMultiplayergames(MultiplayerGame.GameState.SEARCH4PLAYERS);
        for (int i = 0; i < multiplayerGameList.size(); i++){
            addInclude(multiplayerGameList.get(i).getGameName(), multiplayerGameList.get(i).getLeaderName(),
                    multiplayerGameList.get(i).getRoomPlayers(), multiplayerGameList.get(i).getId());
        }
    }

    private void addInclude(String gameName, String leaderName, String playerAmount, long id)
    {
        LinearLayout parent = (LinearLayout)this.findViewById(R.id.mpWifiMenu_scrollView);

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

        /*
        if (isLeader)
        {
            viewRank.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Rank)).getTypeface(), Typeface.BOLD);
            viewName.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Username)).getTypeface(), Typeface.BOLD);
            viewScore.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Score)).getTypeface(), Typeface.BOLD);
        }
        if (ifReady)
        {
            viewRank.setTextColor(this.getResources().getColor(R.color.color_Green));
            viewName.setTextColor(this.getResources().getColor(R.color.color_Green));
            viewScore.setTextColor(this.getResources().getColor(R.color.color_Green));

        }
        */


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

            default:
                //creating an new OnlineGamePlayer
                newPlayer = new OnlineGamePlayer(Long.parseLong(((TextView)v.findViewById(R.id.mpGameElement_id)).getText().toString()), LoginMenu.getCurrentUser(this).getId(),
                        0, OnlineGamePlayer.PlayerState.JOINED);

                MultiplayerWifiLobby.onlineGamePlayer = newPlayer;

                db.createOnlineGamePlayer(newPlayer);
        }
    }
}
