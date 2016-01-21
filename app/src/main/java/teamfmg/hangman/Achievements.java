package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Achievements menu.<br />
 * Created by Ludwig 19.01.2016.
 * @since 1.0
 */
public class Achievements extends Activity implements IApplyableSettings, View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        this.findViewById(R.id.achievements_close).setOnClickListener(this);

        this.changeBackground();
        this.openPieChart();
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_Achievements);
        Settings.setColor(rl);

    }

    @Override
    public void onClick(View v)
    {
        Intent i = null;

        switch (v.getId())
        {
            case R.id.achievements_close:
                this.finish();
                break;

            case R.id.account_statistic:
                i = new Intent(this, Statistic.class);
                this.startActivity(i);
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }

    private void openPieChart()
    {
        // Start Activity
        //RelativeLayout rl = (RelativeLayout)activity.findViewById(R.id.achievements_subLayout);
        //rl.addView(chartView, 0);

        // Pie Chart Section Names
        String[] code = new String[] {
                "Eclair & Older", "Froyo", "Gingerbread", "Honeycomb",
                "IceCream Sandwich", "Jelly Bean"
        };
        // Pie Chart Section Value
        double[] distribution = { 3.9, 12.9, 55.8, 1.9, 23.7, 1.8 } ;
        // Color of each Pie Chart Sections
        int[] colors = { Color.BLUE, Color.MAGENTA, Color.GREEN, Color.CYAN, Color.RED,Color.YELLOW };

    }


}
