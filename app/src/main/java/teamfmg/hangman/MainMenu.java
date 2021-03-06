package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * The Main menu for Hangman.<br />
 * Created by Vincent 12.11.2015.
 * @since 0.3
 */
public class MainMenu extends Activity implements View.OnClickListener, IApplyableSettings
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //init Buttons
        Button exit         =       (Button) findViewById(R.id.button_exit);
        Button options      =       (Button) findViewById(R.id.button_options);
        Button play         =       (Button) findViewById(R.id.button_play);
        Button multiplayer  =       (Button) findViewById(R.id.button_multiplayer);
        Button account      =       (Button) findViewById(R.id.button_mainMenu_account);

        //add ClickListener
        exit.setOnClickListener(this);
        options.setOnClickListener(this);
        play.setOnClickListener(this);
        multiplayer.setOnClickListener(this);
        account.setOnClickListener(this);

        //Updating the lastOnline Time
        DatabaseManager db = DatabaseManager.getInstance();
        db.setActivity(this);
        db.updateLastOnline();

        this.changeBackground();

        MusicPlayer.playNext(R.raw.track01,this);

    }

    @Override
    public void onClick(View v)
    {
        Intent i;

        switch (v.getId())
        {
            //Stop the App
            case R.id.button_exit:
                System.exit(0);
                break;

            //Open Options Activity
            case R.id.button_options:
                i = new Intent(this, Options.class);
                this.startActivity(i);
                break;

            //Single player
            case R.id.button_play:
                i = new Intent(this, SingleplayerMenu.class);
                this.startActivity(i);
                break;

            //Account
            case R.id.button_mainMenu_account:


                /**
                 * Fixing logout bug for versions < Honeycomb
                 */
                int curVersion = android.os.Build.VERSION.SDK_INT;

                if (curVersion < Build.VERSION_CODES.HONEYCOMB)
                {
                    this.finish();
                }

                i = new Intent(this, AccountMenu.class);
                this.startActivity(i);

                break;


            //Multiplayer
            case  R.id.button_multiplayer:
                i = new Intent(this, MultiplayerMenu.class);
                this.startActivity(i);
                break;


            default:
                Logger.write("Currently no function", this);
        }
    }

    //this is needed to see the changes in the options menu
    @Override
    protected void onResume()
    {
        super.onResume();
        this.changeBackground();
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_mainMenu);
        Settings.setColor(rl);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }
}
