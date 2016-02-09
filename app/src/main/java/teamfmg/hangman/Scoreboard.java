package teamfmg.hangman;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;


@SuppressWarnings("deprecation")
public class Scoreboard extends TabActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);

        TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabSpec tab2 = tabHost.newTabSpec("Second Tab");
        TabSpec tab3 = tabHost.newTabSpec("Third Tab");

        Intent i1 = new Intent(this, ScoreboardTab.class);
        Intent i2 = new Intent(this, ScoreboardTab.class);
        Intent i3 = new Intent(this, ScoreboardTab.class);

        tab1.setIndicator(this.getResources().getString(R.string.button_singleplayerMenu_StandardMode));
        i1.putExtra("shownScoreboard", 0);
        tab1.setContent(i1);
        tabHost.addTab(tab1);


        tab2.setIndicator(this.getResources().getString(R.string.button_singleplayerMenu_HardcoreMode));
        i2.putExtra("shownScoreboard", 1);
        tab2.setContent(i2);
        tabHost.addTab(tab2);


        tab3.setIndicator(this.getResources().getString(R.string.button_singleplayerMenu_SpeedMode));
        i3.putExtra("shownScoreboard", 2);
        tab3.setContent(i3);
        tabHost.addTab(tab3);


        /** Add the tabs  to the TabHost to display. */

    }

    @Override
    public void onClick(View v) {

    }
}
