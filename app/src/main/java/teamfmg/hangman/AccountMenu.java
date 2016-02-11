package teamfmg.hangman;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
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

    DatabaseManager db = DatabaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_menu);

        db.setActivity(this);
        this.changeBackground();

        //adding click listener
        this.findViewById(R.id.account_statistic).setOnClickListener(this);
        this.findViewById(R.id.account_achievments).setOnClickListener(this);
        this.findViewById(R.id.account_change_pw).setOnClickListener(this);
        this.findViewById(R.id.account_close).setOnClickListener(this);
        this.findViewById(R.id.account_logout).setOnClickListener(this);
        this.findViewById(R.id.button_accountMenu_Scoreboard).setOnClickListener(this);

        ((TextView)this.findViewById(R.id.label_current_User)).setText(LoginMenu.getCurrentUser().getName());

        if(!db.isOnline())
        {
            this.findViewById(R.id.account_statistic).setEnabled(false);
            this.findViewById(R.id.account_achievments).setEnabled(false);
            this.findViewById(R.id.account_change_pw).setEnabled(false);
            this.findViewById(R.id.button_accountMenu_Scoreboard).setEnabled(false);
        }
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
        int curVersion = android.os.Build.VERSION.SDK_INT;
        Intent i;


        switch (v.getId())
        {
            case R.id.account_close:
                this.finish();

                /**
                 * Fixing logout bug < gingerbread
                 */
                if (curVersion < Build.VERSION_CODES.HONEYCOMB)
                {
                    this.startActivity(new Intent(this,MainMenu.class));
                }

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

                /**
                 * Fixing logout bug < gingerbread
                 */
                i = new Intent(this, LoginMenu.class);

                if (curVersion >= Build.VERSION_CODES.HONEYCOMB)
                {
                    ComponentName cn = i.getComponent();
                    Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                    this.startActivity(mainIntent);
                }
                else
                {
                    this.finish();
                    this.startActivity(i);
                }

            break;

            case R.id.button_accountMenu_Scoreboard:
                i = new Intent(this, Scoreboard.class);
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

    @Override
    public void onBackPressed()
    {
        this.finish();

        /**
         * Fixing logout bug < gingerbread
         */
        int curVersion = android.os.Build.VERSION.SDK_INT;

        if (curVersion < Build.VERSION_CODES.HONEYCOMB)
        {

            this.startActivity(new Intent(this,MainMenu.class));
        }
    }
}
