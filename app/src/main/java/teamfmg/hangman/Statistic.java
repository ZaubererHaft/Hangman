package teamfmg.hangman;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Statistic extends Activity implements IApplyableSettings, View.OnClickListener
{

    DatabaseManager db = DatabaseManager.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        db.setActivity(this);
        String username = LoginMenu.getCurrentUser().getName();

        if (getIntent().hasExtra("othersStatistic")){
            Bundle extra = getIntent().getExtras();
            TextView header = (TextView)findViewById(R.id.label_statistic_header);
            header.setText(header.getText() + " by " + extra.getString("othersStatistic"));
            username = extra.getString("othersStatistic");
        }

        int[] val = db.getAllStatistics(username);

        Integer wins                    = val[0];
        Integer perfects                = val[1];
        Integer losses                  = val[2];
        Integer correctLetters          = val[3];
        Integer wrongLetters            = val[4];
        Integer scoreStandardMode       = val[7];
        Integer highscoreHardcore       = val[5];
        Integer highscoreSpeedmode      = val[6];




        String[] texts=
        {
            this.getResources().getString(R.string.label_statistic_wins),
            this.getResources().getString(R.string.label_statistic_lose),
            this.getResources().getString(R.string.label_statistic_perfect)
        };

        //String title = this.getResources().getString(R.string.label_statistic_hadder);

        double[] values =
        {
                wins - perfects,
                losses,
                perfects
        };

        int[] colors =
        {
            Color.BLUE,
            Color.RED,
            Color.GREEN
        };

        //TODO: Remove
        TextView winsAmount = (TextView)this.findViewById(R.id.label_statistic_wins_amount);
        winsAmount.setText(" " +  wins + " (" + texts[2] +" " + perfects+")");

        TextView losesAmount = (TextView)this.findViewById(R.id.label_statistic_loses_amount);
        losesAmount.setText(losses.toString());

        TextView correctLettersAmount = (TextView)this.findViewById(R.id.label_statistic_correctLetters_amount);
        correctLettersAmount.setText(correctLetters.toString());

        TextView wrongLettersAmount = (TextView)this.findViewById(R.id.label_statistic_wrongLetters_amount);
        wrongLettersAmount.setText(wrongLetters.toString());

        TextView scoreStandardModeAmount = (TextView)this.findViewById(R.id.label_statistic_scoreStandardMode_amount);
        scoreStandardModeAmount.setText(scoreStandardMode.toString());

        TextView highscoreHardcoreModeAmount = (TextView)this.findViewById(R.id.label_statistic_scoreHardcoreMode_amount);
        highscoreHardcoreModeAmount.setText(highscoreHardcore.toString());

        TextView highscoreSpeedModeAmount = (TextView)this.findViewById(R.id.label_statistic_scoreSpeedMode_amount);
        highscoreSpeedModeAmount.setText(highscoreSpeedmode.toString());

        this.changeBackground();

        //canceling drawing diagram if no statistic was set
        if(wins <= 0 && perfects <= 0 && losses <= 0)
        {
            return;
        }

        RelativeLayout rl = (RelativeLayout)this.findViewById(R.id.statistic_diagram_layout1);
        rl.addView(Charts.generatePieChart(this,texts,values,colors,"",true,false));
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_statistic);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.statistic_close:
                this.finish();
                break;
        }
    }
}
