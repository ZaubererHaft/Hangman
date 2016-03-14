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

import java.util.ArrayList;
import java.util.List;

/**
 * The Scoreboard menu for Hangman.<br />
 * Created by Vincent 10.02.2016.
 * @author Vincent
 * @since 1.2
 */
public class ScoreboardTab extends Activity implements View.OnClickListener, IApplyableSettings
{

    /**
     * The Scorelist.
     */
    private List<String[]> scorelist;
    /**
     * Instance to the Database.
     */
    private DatabaseManager db = DatabaseManager.getInstance();
    /**
     * the shownScoreboard
     */
    private int shownScoreboard;

    private boolean doUpdate;

    private LinearLayout parent;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard_tab);


        changeBackground();
        Bundle extra = getIntent().getExtras();
        shownScoreboard = extra.getInt("shownScoreboard");

        TextView header = (TextView) findViewById(R.id.label_scoreboard_header);

        switch (shownScoreboard)
        {
            case 0:
                header.setText(R.string.button_singleplayerMenu_StandardMode);
                this.scorelist = this.db.getStandardScoreboard();
                break;
            case 1:
                header.setText(R.string.button_singleplayerMenu_HardcoreMode);
                this.scorelist = this.db.getHardcoreScoreboard();
                break;
            case 2:
                header.setText(R.string.button_singleplayerMenu_SpeedMode);
                this.scorelist = this.db.getSpeedModeScoreboard();
                break;
            case 3:
                header.setText(R.string.text_header_multiplayer_local);
                this.scorelist = MultiplayerLocal.scoreboard;
                break;
            case 4:
                header.setText(MultiplayerWifiLobby.multiplayerGame.getGameName());
                updateScoreboard();
                updaterScoreboard();
                break;
        }

        initScorelist();
    }

    /**
     * Only for wifi multiplayer game
     * gets the scor of each player and convert it for the scorelist
     */
    private void updateScoreboard()
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                List<OnlineGamePlayer> onlineGamePlayers = new ArrayList<>();
                onlineGamePlayers = db.getAllMultiplayergamePlayers(MultiplayerWifiLobby.multiplayerGame.getId());

                List<String[]> list = new ArrayList<>();

                boolean hasToUpdate = false;

                for (int i = 0; i < onlineGamePlayers.size(); i++) {
                    String[] innerList = new String[2];
                    innerList[0] = db.getUser(onlineGamePlayers.get(i).getUserID()).getName();
                    if (onlineGamePlayers.get(i).getPlayerState() == OnlineGamePlayer.PlayerState.PLAYING) {
                        innerList[1] = onlineGamePlayers.get(i).getPlayerState().name();
                    } else {
                        innerList[1] = "" + onlineGamePlayers.get(i).getScore();
                        hasToUpdate = true;
                    }
                    list.add(innerList);
                }

                doUpdate = hasToUpdate;

                if (!hasToUpdate){
                    MultiplayerWifiLobby.multiplayerGame.setGameState(MultiplayerGame.GameState.FINISHED);
                    db.updateOnlineGame(MultiplayerWifiLobby.multiplayerGame);
                }

                scorelist = list;
                initScorelist();
            }
        });
    }

    /**
     * Inits the score list.
     * @since 1.2
     */
    private void initScorelist()
    {
        parent = (LinearLayout)this.findViewById(R.id.linearLayout_scoreboard_tab);
        parent.removeAllViews();

        for (int i = 0; i < scorelist.size(); i++)
        {
            boolean isOnline = false;

            if (shownScoreboard != 3 && shownScoreboard != 4)
            {
                isOnline = TimeHelper.lastOnlineByDate(scorelist.get(i)[2]).equals("Jetzt");
            }

            if (scorelist.get(i)[0].equals(LoginMenu.getCurrentUser(this).getName()) && shownScoreboard != 3 && shownScoreboard != 4)
            {
                addInclude(i+1, scorelist.get(i)[0], scorelist.get(i)[1], true, isOnline);
            }
            else
            {
                addInclude(i+1, scorelist.get(i)[0], scorelist.get(i)[1], false, isOnline);
            }
        }
    }

    /**
     * Adds an include.
     * @since 1.2
     */
    private void addInclude(int rank, String name, String score, Boolean bold, Boolean isOnline)
    {


        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child = inflater.inflate(R.layout.new_scoreboard_element, null, false);

        TextView viewRank = (TextView)child.findViewById(R.id.scoreboardElement_Rank);
        TextView viewName = (TextView)child.findViewById(R.id.scoreboardElement_Username);
        TextView viewScore = (TextView)child.findViewById(R.id.scoreboardElement_Score);

        viewRank.setText(String.valueOf(rank));
        viewName.setText(name);
        viewScore.setText(score);


        if (bold)
        {
            viewRank.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Rank)).getTypeface(), Typeface.BOLD);
            viewName.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Username)).getTypeface(), Typeface.BOLD);
            viewScore.setTypeface(((TextView) child.findViewById(
                    R.id.scoreboardElement_Score)).getTypeface(), Typeface.BOLD);
        }
        if (isOnline)
        {
            viewRank.setTextColor(this.getResources().getColor(R.color.color_Green));
            viewName.setTextColor(this.getResources().getColor(R.color.color_Green));
            viewScore.setTextColor(this.getResources().getColor(R.color.color_Green));

        }


        if(shownScoreboard != 3)
        {
            child.setOnClickListener(this);
        }

        parent.addView(child);
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_scoreboardTab);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()){

            case R.id.button_exit_Scoreboard:
                this.finish();
            break;
            
            default:
                Intent i = new Intent(this, StatisticMenu.class);
                i.putExtra("othersStatistic",
                        ((TextView)v.findViewById(R.id.scoreboardElement_Username)).getText());
                this.startActivity(i);
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUpdate = false;
    }

    private void updaterScoreboard() {

        doUpdate = true;
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                while (doUpdate)
                {
                    updateScoreboard();

                    try
                    {
                        Thread.sleep(10000);
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
}
