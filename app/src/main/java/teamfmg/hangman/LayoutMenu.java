package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

/**
 * Menu to change the video settings.
 * Created by Ludwig 27.11.2015.
 * @since 0.6
 */
public class LayoutMenu extends Activity implements View.OnClickListener,IApplyableSettings,
        AdapterView.OnItemSelectedListener
{
    /**
     * GUI objects.
     */
    private Spinner colorSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_menu);

        Button closeButton  = (Button) this.findViewById(R.id.layout_close);

        closeButton.setOnClickListener(this);

        this.createSpinner();
        this.chooseActiveElementFromSettings();
        this.changeBackground();

    }

    /**
     * Creates the color spinner to choose the layout in game.
     * @since 0.6
     */
    private void createSpinner()
    {
        String[] colorList;

        //TODO: change spinner design
        this.colorSpinner = (Spinner) findViewById(R.id.options_menu_spinner);
        //TODO Deutsche Version funktuioniert nicht.
        colorList = new String[]
        {
            this.getString(R.string.color_blue),
            this.getString(R.string.color_green),
            this.getString(R.string.color_orange),
            this.getString(R.string.color_purple)
        };

        //add values to Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, colorList);
        this.colorSpinner.setAdapter(adapter);

        this.colorSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Chooses the active spinner element from the settings class.
     * @since 0.6
     */
    private void chooseActiveElementFromSettings()
    {
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

    @Override
    public void onClick(View v)
    {
        //close activity
        if (v.getId() == R.id.layout_close)
        {
            this.finish();
        }
    }

    @Override
    public void changeBackground()
    {
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_layout);
        Settings.setColor(rl);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        //chooses index and set color
        Settings.indexToColor(colorSpinner.getSelectedItemPosition());

        //apply color on layout
        changeBackground();

        //save settings
        Settings.save(this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
