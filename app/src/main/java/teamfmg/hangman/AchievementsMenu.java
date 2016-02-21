package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

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

        this.addAchievements();

    }

    /**
     * Adds achievement to the layout.
     */
    public void addAchievements()
    {
        DatabaseManager db = DatabaseManager.getInstance();
        db.setActivity(this);

        ArrayList<Achievement> achs = db.getAchievements();
        ArrayList<Integer> userAchs = db.getAchievements(LoginMenu.getCurrentUser(this).getId());

        /**
         * For all possible achievements...
         */
        for (int i = 0; i < achs.size(); i++)
        {
            Achievement ach = achs.get(i);

            int imageID;

            //Change icon if user has achievement.
            if(userAchs.contains(ach.getId()))
            {
                imageID = this.getResources().getIdentifier("a_" + ach.getId(), "drawable",
                        this.getPackageName());

                this.addInclude(achs.get(i).getHeader(),achs.get(i).getDescription(),imageID);
            }
            else
            {
                imageID = R.drawable.a_00;
                this.addInclude(achs.get(i).getHeader(),"???",imageID);
            }


        }
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
                i = new Intent(this, StatisticMenu.class);
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

    /**
     * Adds an include to the scrollview.
     * @since 0.7
     */
    private void addInclude(String word, String description, int drawableID)
    {
        LinearLayout parent = (LinearLayout)this.findViewById(R.id.ach_subLayout);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child = inflater.inflate(R.layout.achievement, null, false);

        ((TextView)child.findViewById(R.id.ach_header)).setText(word);
        ((TextView)child.findViewById(R.id.ach_desc)).setText(description);
            ImageView iv = (ImageView)child.findViewById(R.id.ach_image);
            iv.setImageResource(drawableID);

        parent.addView(child);
    }



}
