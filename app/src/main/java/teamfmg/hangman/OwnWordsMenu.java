package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

public class OwnWordsMenu extends Activity implements View.OnClickListener, IApplyableSettings
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_words_menu);
        this.changeBackground();

        this.findViewById(R.id.ownwords_close).setOnClickListener(this);
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_ownWords);
        Settings.setColor(rl);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.ownwords_close)
        {
            this.finish();
        }
    }
}
