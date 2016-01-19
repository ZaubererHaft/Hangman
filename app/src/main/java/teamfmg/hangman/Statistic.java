package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Statistic extends Activity implements IApplyableSettings, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        DatabaseManager db = new DatabaseManager(this);

        //init
        Button close                    = (Button)      this.findViewById(R.id.statistic_close);

        TextView winsAmount = (TextView)this.findViewById(R.id.label_statistic_wins_amount);
        winsAmount.setText(" " + db.getCurrentStatistic(DatabaseManager.Attribut.WINS));

        TextView losesAmount = (TextView)this.findViewById(R.id.label_statistic_loses_amount);
        losesAmount.setText(" " + db.getCurrentStatistic(DatabaseManager.Attribut.LOSES));

        TextView correctLettersAmount = (TextView)this.findViewById(R.id.label_statistic_correctLetters_amount);
        correctLettersAmount.setText(" " + db.getCurrentStatistic(DatabaseManager.Attribut.CORRECTLETTER));

        TextView wrongLettersAmount = (TextView)this.findViewById(R.id.label_statistic_wrongLetters_amount);
        wrongLettersAmount.setText(" " + db.getCurrentStatistic(DatabaseManager.Attribut.WRONGLETTER));

        changeBackground();
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_statistic);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.statistic_close:
                this.finish();
                break;
        }
    }
}
