package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * The Options menu for Hangman.<br />
 * Created by Vincent 12.11.2015.
 * @since 0.3
 */
public class Options extends Activity implements View.OnClickListener, IApplyableSettings
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //init Buttons
        Button ownWords     = (Button) findViewById(R.id.button_ownWords);
        Button about        = (Button) findViewById(R.id.button_about);
        Button music        = (Button) findViewById(R.id.button_options_music);
        Button closeButton  = (Button) findViewById(R.id.options_close);
        Button video        = (Button) findViewById(R.id.button_video);
        Button category     = (Button) findViewById(R.id.button_options_categories);

        //add ClickListener
        closeButton.setOnClickListener(this);
        ownWords.setOnClickListener(this);
        about.setOnClickListener(this);
        music.setOnClickListener(this);
        video.setOnClickListener(this);
        category.setOnClickListener(this);

        this.changeBackground();

    }

    //TODO: implement about
    @Override
    public void onClick(View v)
    {
        Intent i;

        switch (v.getId())
        {
            case R.id.button_about:
                 i = new Intent(this,About.class);
                this.startActivity(i);
                break;
            case R.id.button_options_categories:
                i = new Intent(this,Category.class);
                this.startActivity(i);
                break;
            case R.id.button_options_music:
                i = new Intent(this,MusicMenu.class);
                this.startActivity(i);
                break;
            case R.id.button_video:
                i = new Intent(this,LayoutMenu.class);
                this.startActivity(i);
                break;
            case R.id.options_close:
                this.finish();
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
}
