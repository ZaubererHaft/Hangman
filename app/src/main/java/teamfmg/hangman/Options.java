package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * The Options menu for Hangman.<br />
 * Created by Vincent 12.11.2015.
 * @since 0.3
 */
public class Options extends Activity implements View.OnClickListener, IApplyableSettings
{

    DatabaseManager db = DatabaseManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //init Buttons
        this.findViewById(R.id.button_ownWords).setOnClickListener(this);
        this.findViewById(R.id.button_about).setOnClickListener(this);
        this.findViewById(R.id.button_options_music).setOnClickListener(this);
        this.findViewById(R.id.options_close).setOnClickListener(this);
        this.findViewById(R.id.button_video).setOnClickListener(this);
        this.findViewById(R.id.button_options_categories).setOnClickListener(this);

        this.changeBackground();


        db.setActivity(this);

        if(!db.isOnline())
        {
            this.findViewById(R.id.button_ownWords).setEnabled(false);
        }
    }

    //TODO: implement about
    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.button_about:
                this.startActivity(new Intent(this,About.class));
                break;
            case R.id.button_options_categories:
                this.startActivity(new Intent(this,Category.class));
                break;
            case R.id.button_options_music:
                this.startActivity(new Intent(this,MusicMenu.class));
                break;
            case R.id.button_video:
                this.startActivity(new Intent(this,LayoutMenu.class));
                break;
            case R.id.options_close:
                this.finish();
                break;
            case R.id.button_ownWords:
                if(db.isOnline())
                {
                    this.startActivity(new Intent(this, OwnWordsMenu.class));
                }
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
        RelativeLayout rl   = (RelativeLayout)this.findViewById(R.id.relLayout_options);
        Settings.setColor(rl);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }
}
