package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Class to set the music settings.
 * Created by Ludwig on 27.11.2015.
 * @since 0.6
 */
public class MusicMenu extends Activity implements IApplyableSettings, View.OnClickListener
{

    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_menu);

        this.closeButton = (Button) findViewById(R.id.music_close);
        this.closeButton.setOnClickListener(this);

        this.changeBackground();
    }


    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_musicMenu);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v)
    {
        //close Button
        if(v.getId() == closeButton.getId())
        {
            this.finish();
        }
    }
}
