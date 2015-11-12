package teamfmg.hangman;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class Options extends Activity implements View.OnClickListener{

    private String[] colorList;

    private Spinner colorSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        //init Buttons
        Button ownWords = (Button) findViewById(R.id.button_ownWords);
        Button back = (Button) findViewById(R.id.button_back);
        Button about = (Button) findViewById(R.id.button_about);
        Button apply = (Button) findViewById(R.id.button_apply);
        //add ClickListener
        ownWords.setOnClickListener(this);
        back.setOnClickListener(this);
        apply.setOnClickListener(this);
        about.setOnClickListener(this);
        //init Spinner
        //TODO: Design vom Dropdown ändern
        colorSpinner = (Spinner) findViewById(R.id.spinner_chooseColor);
        //add values to Spinner

        colorList = new String[]{
                this.getString(R.string.color_blue),
                getString(R.string.color_green),
                getString(R.string.color_orange),
                getString(R.string.color_purple)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, colorList);
        colorSpinner.setAdapter(adapter);

        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_options);
        Settings.setColor(rl);
    }

    //TODO: implement about, apply
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_back:
                this.finish();
                break;
            case R.id.button_apply:
                colorSpinner.getSelectedItem();

                String id = colorSpinner.getSelectedItem().toString();

                if(id.equals(colorList[0].toString()))
                {
                    Settings.theme = Settings.Theme.BLUE;
                }
                if(id.equals(colorList[1].toString()))
                {
                    Settings.theme = Settings.Theme.GREEN;
                }
                if(id.equals(colorList[2].toString()))
                {
                    Settings.theme = Settings.Theme.ORANGE;
                }
                if(id.equals(colorList[3].toString()))
                {
                    Settings.theme = Settings.Theme.PURPLE;
                }

                RelativeLayout rl   = (RelativeLayout)this.findViewById(R.id.relLayout_options);
                Settings.setColor(rl);


                //Logger.write(this.getResources().getString(R.string.options_hint_restart),this);

                break;
            default:
                Logger.write("Currently no function", this);
        }
    }
}
