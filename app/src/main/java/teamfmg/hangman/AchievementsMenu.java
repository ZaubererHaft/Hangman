package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * AchievementsMenu menu.<br />
 * Created by Ludwig 19.01.2016.
 * @since 1.0
 */
public class AchievementsMenu extends Activity implements IApplyableSettings, View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);

        this.findViewById(R.id.achievements_close).setOnClickListener(this);

        this.changeBackground();
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
        Intent i;

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



}
