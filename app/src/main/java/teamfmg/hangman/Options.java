package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
/**
 * The Options menu for Hangman.<br />
 * Created by Vincent 12.11.2015.
 * @since 0.3
 */
public class Options extends Activity implements View.OnClickListener, IApplyableSettings
{

    /**
     * GUI objects.
     */
    private Spinner colorSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        String[] colorList;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //init Buttons
        Button ownWords     = (Button) findViewById(R.id.button_ownWords);
        Button back         = (Button) findViewById(R.id.button_back);
        Button about        = (Button) findViewById(R.id.button_about);
        Button apply        = (Button) findViewById(R.id.button_apply);

        //add ClickListener
        ownWords.setOnClickListener(this);
        back.setOnClickListener(this);
        apply.setOnClickListener(this);
        about.setOnClickListener(this);


        //TODO: Design vom Dropdown ändern
        colorSpinner = (Spinner) findViewById(R.id.spinner_chooseColor);

        colorList = new String[]
        {
            this.getString(R.string.color_blue),
            getString(R.string.color_green),
            getString(R.string.color_orange),
            getString(R.string.color_purple)
        };

        //add values to Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, colorList);
        colorSpinner.setAdapter(adapter);

        this.changeBackground();

        //Select the active element from the spinner
        switch (Settings.getTheme())
        {
            case BLUE:
                this.colorSpinner.setSelection(0);
                break;
            case GREEN:
                this.colorSpinner.setSelection(1);
                break;
            case ORANGE:
                this.colorSpinner.setSelection(2);
                break;
            case PURPLE:
                this.colorSpinner.setSelection(3);
                break;
        }
    }

    //TODO: implement about
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_back:
                this.finish();
                break;
            case R.id.button_apply:
                String s = this.colorSpinner.getSelectedItem().toString();

               //chooses color from string
                Settings.stringToColor(s.toUpperCase());

                //apply color on layout
                this.changeBackground();

                //save settings
                Settings.save(this);

                break;
            default:
                Logger.write("Currently no function", this);
        }
    }

    @Override
    public void changeBackground()
    {
        RelativeLayout rl   = (RelativeLayout)this.findViewById(R.id.relLayout_options);
        Settings.setColor(rl);
    }
}
