package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class About extends Activity implements IApplyableSettings, View.OnClickListener
{

    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_about);

        this.closeButton = (Button) findViewById(R.id.about_close);
        this.closeButton.setOnClickListener(this);

        this.changeBackground();
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_about);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == closeButton.getId())
        {
            this.finish();
        }
    }
}
