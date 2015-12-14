package teamfmg.hangman;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    private int viewsCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_words_menu);
        this.changeBackground();

        this.findViewById(R.id.ownwords_close).setOnClickListener(this);

        db = new DatabaseManager(this);

        this.setUpLayout();
        wordText = (TextView)findViewById(R.id.ownWords_word);
        descriptionText = (TextView)findViewById(R.id.ownWords_description);

    }

    private void addInclude(int id)
    {
        LinearLayout parent = (LinearLayout)this.findViewById(R.id.ownWords_linLayout);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View child = inflater.inflate(R.layout.new_word_element, null, false);

        parent.addView(child);
    }

    private void setUpLayout()
    {

        this.findViewById(R.id.newWord_Done).setOnClickListener(this);
        LinearLayout ll = (LinearLayout)findViewById(R.id.ownWords_linLayout);
        words = db.getWordsOfCategory("ownWord");

        //Add one Checkbox for each Button (+ GUI Settings)
        //TODO: Implement
        for (int i = 0; i < this.words.size(); i++)
        {
            viewsCount++;
            this.addInclude(10+viewsCount);
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
        if (v.getId() == R.id.newWord_Done)
        {
            Word w = new Word(wordText.getText().toString(), "ownWord", descriptionText.getText().toString());

            //TODO hier exists
            db.addWord(w);
            ++viewsCount;
            this.addInclude(viewsCount);


            wordText.setText("");
            descriptionText.setText("");
        }
    }
}
