package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Statistic extends Activity implements IApplyableSettings {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);
        DatabaseManager db = new DatabaseManager(this);

        TextView winsAmount = (TextView)this.findViewById(R.id.label_statistic_wins_amount);
        winsAmount.setText(" " + db.getCurrentStatistic(DatabaseManager.Attribut.WINS, LoginMenu.getCurrentUser()));

        TextView losesAmount = (TextView)this.findViewById(R.id.label_statistic_loses_amount);
        losesAmount.setText(" " + db.getCurrentStatistic(DatabaseManager.Attribut.LOSES, LoginMenu.getCurrentUser()));
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_statistic);
        Settings.setColor(rl);
    }
}
