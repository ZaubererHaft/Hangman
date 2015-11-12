package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * The Main menu for Hangman.<br />
 * Created by Vincent 12.11.2015.
 * @since 0.3
 */
public class MainMenu extends Activity implements View.OnClickListener
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

        //add ClickListener
        exit.setOnClickListener(this);
        options.setOnClickListener(this);
        play.setOnClickListener(this);
        multiplayer.setOnClickListener(this);

        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_mainMenu);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //Stop the App
            case R.id.button_exit:
                System.exit(0);
                break;
            //Open Options Activity
            case R.id.button_options:
                Intent i = new Intent(this, Options.class);
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
        RelativeLayout rl = (RelativeLayout)this.findViewById(R.id.relLayout_mainMenu);
        Settings.setColor(rl);
    }
}
