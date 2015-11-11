package teamfmg.hangman;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Options extends Activity implements View.OnClickListener{

    Spinner colorSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        String[] colorList = new String[]{
                getString(R.string.color_blue),
                getString(R.string.color_green),
                getString(R.string.color_orange),
                getString(R.string.color_purple)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, colorList);
        colorSpinner.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_back:
                this.finish();
                break;
            case R.id.button_apply:
                colorSpinner.getSelectedItem();
                Logger.write(colorSpinner.getSelectedItemId(),this);
                break;
            default:
                Logger.write("Currently no function", this);
        }
    }
}
