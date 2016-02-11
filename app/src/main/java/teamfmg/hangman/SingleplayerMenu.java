package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * The Singleplayer menu<br />
 * Created by Vincent 09.02.2016.
 * @since 1.2
 */
public class SingleplayerMenu extends Activity implements IApplyableSettings, View.OnClickListener
{

    /**
     * Instance to database.
     */
    DatabaseManager db = DatabaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer_menu);
        changeBackground();
        DatabaseManager db = DatabaseManager.getInstance();
        //ClickListener
        findViewById(R.id.button_singleplayerMenu_StandardMode).setOnClickListener(this);
        findViewById(R.id.button_singleplayerMenu_CustomMode).setOnClickListener(this);
        findViewById(R.id.button_singleplayerMenu_HardcoreMode).setOnClickListener(this);
        findViewById(R.id.button_singleplayerMenu_SpeedMode).setOnClickListener(this);
        findViewById(R.id.button_info_standardMode).setOnClickListener(this);
        findViewById(R.id.button_info_customMode).setOnClickListener(this);
        findViewById(R.id.button_info_HardcoreMode).setOnClickListener(this);
        findViewById(R.id.button_info_speedMode).setOnClickListener(this);
        findViewById(R.id.button_exit_singleplayerMenu).setOnClickListener(this);

        if (!db.isOnline())
        {
            findViewById(R.id.button_singleplayerMenu_StandardMode).setEnabled(false);
            findViewById(R.id.button_singleplayerMenu_HardcoreMode).setEnabled(false);
            findViewById(R.id.button_singleplayerMenu_SpeedMode).setEnabled(false);
        }

        this.db.setActivity(this);
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_singleplayerMenu);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v)
    {
        Intent i;

        switch (v.getId())
        {
            case R.id.button_exit_singleplayerMenu:
                this.finish();
                break;

            //CustomMode
            case R.id.button_singleplayerMenu_CustomMode:
                i = new Intent(this, Singleplayer.class);
                Singleplayer.gameMode = Singleplayer.GameMode.CUSTOM;
                this.startActivity(i);
                break;

            case R.id.button_info_customMode:
                Logger.messageDialog(this.getResources().getString(R.string.button_singleplayerMenu_CustomMode),
                        this.getResources().getString(R.string.message_singleplayerMenu_info_CustomMode), this);
                break;

            //StandardMode
            case R.id.button_singleplayerMenu_StandardMode:
                if(db.isOnline())
                {
                    i = new Intent(this, Singleplayer.class);
                    Singleplayer.gameMode = Singleplayer.GameMode.STANDARD;
                    this.startActivity(i);
                }
                break;

            case R.id.button_info_standardMode:
                Logger.messageDialog(this.getResources().getString(R.string.button_singleplayerMenu_StandardMode),
                        this.getResources().getString(R.string.message_singleplayerMenu_info_StandardMode), this);
                break;

            //HardcoreMode
            case R.id.button_singleplayerMenu_HardcoreMode:
                if(db.isOnline())
                {
                    i = new Intent(this, Singleplayer.class);
                    Singleplayer.gameMode = Singleplayer.GameMode.HARDCORE;
                    this.startActivity(i);
                }
                break;

            case R.id.button_info_HardcoreMode:
                Logger.messageDialog(this.getResources().getString(R.string.button_singleplayerMenu_HardcoreMode),
                        this.getResources().getString(R.string.message_singleplayerMenu_info_HardcoreMode), this);
                break;

            //Speedmode
            case R.id.button_info_speedMode:
                Logger.messageDialog(this.getResources().getString(R.string.button_singleplayerMenu_SpeedMode),
                        this.getResources().getString(R.string.message_singleplayerMenu_info_SpeedMode), this);
                break;


            default:
                Logger.write("Currently no function!", this);
        }
    }
}
