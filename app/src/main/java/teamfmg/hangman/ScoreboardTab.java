package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardTab extends Activity implements View.OnClickListener, IApplyableSettings{

    int shownScoreboard;
    List<String[]> scorelist;
    private DatabaseManager db = DatabaseManager.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard_tab);
        changeBackground();
        Bundle extra = getIntent().getExtras();
        shownScoreboard = extra.getInt("shownScoreboard");
        TextView header = (TextView) findViewById(R.id.label_scoreboard_header);

        switch (shownScoreboard){
            case 0:
                header.setText("Rangliste Standard");
                scorelist = db.getStandardScoreboard();
                break;
            case 1:
                header.setText("Rangliste Hardcore");
                scorelist = db.getHardcoreScoreboard();
                break;
            case 2:
                header.setText("Rangliste Speed");
                scorelist = db.getSpeedModeScoreboard();
                break;
        }

        initScorelist();
    }

    private void initScorelist()
    {
        for (int i = 0; i < scorelist.size(); i++){
            if (scorelist.get(i)[0].equals(LoginMenu.getCurrentUser().getName()))
            {
                addInclude(i+1, scorelist.get(i)[0], scorelist.get(i)[1], true);
            }
            else
            {
                addInclude(i+1, scorelist.get(i)[0], scorelist.get(i)[1], false);
            }
        }
    }

    /**
     * Adds an include to the scrollview.
     * @since 0.7
     */
    private void addInclude(int rank, String name, String score, Boolean bold)
    {
        LinearLayout parent = (LinearLayout)this.findViewById(R.id.linearLayout_scoreboard_tab);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child;


        if (bold)
        {
            child = inflater.inflate(R.layout.new_scoreboard_element_bold, null, false);
            ((TextView)child.findViewById(R.id.scoreboardElement_Rank_bold)).setText(String.valueOf(rank));
            ((TextView)child.findViewById(R.id.scoreboardElement_Username_bold)).setText(name);
            ((TextView)child.findViewById(R.id.scoreboardElement_Score_bold)).setText(score);
        }
        else
        {
            child = inflater.inflate(R.layout.new_scoreboard_element, null, false);
            ((TextView)child.findViewById(R.id.scoreboardElement_Rank)).setText(String.valueOf(rank));
            ((TextView)child.findViewById(R.id.scoreboardElement_Username)).setText(name);
            ((TextView)child.findViewById(R.id.scoreboardElement_Score)).setText(score);
        }


        child.setOnClickListener(this);

        parent.addView(child);
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_scoreboardTab);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button_exit_Scoreboard:
                this.finish();
            break;
            
            default:
                Logger.write("Currently no funktion!", this);
            break;
        }
    }
}
