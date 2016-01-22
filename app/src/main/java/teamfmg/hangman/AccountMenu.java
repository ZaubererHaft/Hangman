package teamfmg.hangman;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Account menu.<br />
 * Created by Ludwig 19.01.2016.
 * @since 1.0
 */
public class AccountMenu extends Activity implements IApplyableSettings, View.OnClickListener
{


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_menu);

        this.changeBackground();

        //adding click listener
        this.findViewById(R.id.account_statistic).setOnClickListener(this);
        this.findViewById(R.id.account_achievments).setOnClickListener(this);
        this.findViewById(R.id.account_change_pw).setOnClickListener(this);
        this.findViewById(R.id.account_close).setOnClickListener(this);
        this.findViewById(R.id.account_logout).setOnClickListener(this);

        ((TextView)this.findViewById(R.id.label_current_User)).setText(LoginMenu.getCurrentUser().getName());
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_Account);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v)
    {
        Intent i = null;

        switch (v.getId())
        {
            case R.id.account_close:
                this.finish();
            break;

            case R.id.account_statistic:
                i = new Intent(this, Statistic.class);
                this.startActivity(i);
            break;

            case R.id.account_achievments:
                i = new Intent(this, Achievements.class);
                this.startActivity(i);
            break;

            case R.id.account_logout:
                LoginMenu.setCurrentUser(null);

                i = new Intent(this, LoginMenu.class);
                ComponentName cn = i.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                this.startActivity(mainIntent);
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
