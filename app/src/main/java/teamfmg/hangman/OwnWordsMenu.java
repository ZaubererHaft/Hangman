package teamfmg.hangman;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class OwnWordsMenu extends Activity implements View.OnClickListener, IApplyableSettings
{

    private ArrayList<String> words;
    private TextView wordText, descriptionText;
    private DatabaseManager db;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_words_menu);
        this.changeBackground();

        this.findViewById(R.id.ownwords_close).setOnClickListener(this);

        db = new DatabaseManager(this);
        this.words = db.getWordsOfCategory("custom");

        this.setUpLayout();
        wordText = (TextView)findViewById(R.id.ownWords_word);
        descriptionText = (TextView)findViewById(R.id.ownWords_description);

    }

    private void setUpLayout()
    {
        int viewsCount = 0;
        this.findViewById(R.id.newWord_Done).setOnClickListener(this);


        LinearLayout ll = (LinearLayout)findViewById(R.id.ownWords_linLayout);

        //Add one Checkbox for each Button (+ GUI Settings)
        //TODO: Implement
        for (int i = 0; i < this.words.size(); i++)
        {
            View c = new View(this);

            c.setId(viewsCount);
            viewsCount++;
            ll.addView(c);
        }


        //adds click listener to words
        for (int i = 0; i < ll.getChildCount(); i++)
        {
            ll.getChildAt(i).setOnClickListener(this);
        }
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
        if (v.getId() == R.id.newWord_Done){
            Word w = new Word(wordText.getText().toString(), "ownWord", descriptionText.getText().toString());

            try {
                db.addWord(w);
                }
            catch (Exception e){
                Logger.write("Word already excists", this);
            }

            wordText.setText("");
            descriptionText.setText("");
        }
    }
}
