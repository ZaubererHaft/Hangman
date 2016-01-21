package teamfmg.hangman;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Statistic extends Activity implements IApplyableSettings, View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        DatabaseManager db = new DatabaseManager(this);

        Integer wins            = db.getCurrentStatistic(DatabaseManager.Attribut.WINS);
        Integer perfects        = db.getCurrentStatistic(DatabaseManager.Attribut.PERFECTS);
        Integer losses          = db.getCurrentStatistic(DatabaseManager.Attribut.LOSES);
        Integer correctLetters  = db.getCurrentStatistic(DatabaseManager.Attribut.CORRECTLETTER);
        Integer wrongLetters    = db.getCurrentStatistic(DatabaseManager.Attribut.WRONGLETTER);

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

        //init
        Button close                    = (Button)      this.findViewById(R.id.statistic_close);

        TextView winsAmount = (TextView)this.findViewById(R.id.label_statistic_wins_amount);
        winsAmount.setText(" " +  wins + " (" + texts[2] +" " + perfects+")");

        TextView losesAmount = (TextView)this.findViewById(R.id.label_statistic_loses_amount);
        losesAmount.setText(losses.toString());

        TextView correctLettersAmount = (TextView)this.findViewById(R.id.label_statistic_correctLetters_amount);
        correctLettersAmount.setText(" " + correctLetters);

        TextView wrongLettersAmount = (TextView)this.findViewById(R.id.label_statistic_wrongLetters_amount);
        wrongLettersAmount.setText(" " + wrongLetters);

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
