package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Shows a menu to add and remove custom hangman words.<br />
 * Created by Vincent 09.12.2015.
 * @since 0.7
 */
public class OwnWordsMenu extends Activity implements View.OnClickListener, IApplyableSettings
{

    /**
     * Displayed words.
     */
    private ArrayList<Word> words;
    /**
     * Text fields.
     */
    private TextView wordText, descriptionText;
    /**
     * This handle the database connection.
     */
    private DatabaseManager db;
    /**
     * Category of added words.
     */
    private final String categoryName = "OwnWord";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_words_menu);

        this.changeBackground();
        this.db = new DatabaseManager(this);

        this.setUpLayout();
    }

    /**
     * Generates the layout of the own words menu.
     * @since 0.7
     */
    private void setUpLayout()
    {

        this.findViewById(R.id.ownwords_close).setOnClickListener(this);
        this.findViewById(R.id.newWord_Done).setOnClickListener(this);

        this.wordText = (TextView)findViewById(R.id.ownWords_word);
        this.descriptionText = (TextView)findViewById(R.id.ownWords_description);

        this.words = db.getWordsOfCategory(this.categoryName);

        //Add custom layout for each word
        for (int i = 0; i < this.words.size(); i++)
        {
            this.addInclude
            (
                words.get(i).getWord(),
                words.get(i).getDescription()
            );
        }

        this.wordText.requestFocus();
    }

    /**
     * Adds an include to the scrollview.
     * @since 0.7
     */
    private void addInclude(String word, String description)
    {
        LinearLayout parent = (LinearLayout)this.findViewById(R.id.ownWords_linLayout);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child = inflater.inflate(R.layout.new_word_element, null, false);

        ((TextView)child.findViewById(R.id.element_word)).setText(word);
        ((TextView)child.findViewById(R.id.element_description)).setText(description);

        child.findViewById(R.id.element_delete).setOnClickListener(this);

        parent.addView(child);
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
        final int minWordLength = 3;
        final int logOffset = -70;

        //close Button
        if(v.getId() == R.id.ownwords_close)
        {
            this.finish();
        }
        //Add a word
        else if (v.getId() == R.id.newWord_Done)
        {
            Word w = new Word
            (
                this.wordText.getText().toString(),
                this.categoryName,
                this.descriptionText.getText().toString()
            );

            if(w.getWord().length() < minWordLength)
            {
                Logger.write(this.getResources().getString(R.string.ownWord_hint_tooShort),this);
                return;
            }

            //if the word doesn't exists...
            if(!this.db.exists(w))
            {
                //add it to db
                this.db.addWord(w);
                this.addInclude(w.getWord(),w.getDescription());
                Logger.write
                (
                    this.getResources().getString(R.string.ownWord_hint_added), this, logOffset
                );
            }
            else
            {
                Logger.write
                (
                    this.getResources().getString(R.string.ownWord_hint_word_already_exists),
                    this, logOffset
                );
            }

            //clear the text fields
            this.wordText.setText("");
            this.descriptionText.setText("");
        }
        else if(v.getId() == R.id.element_delete)
        {

            //get the parent of the delete button
            View parent = (RelativeLayout)v.getParent();

            //access the data
            Word w = new Word
            (
                ((TextView)parent.findViewById(R.id.element_word)).getText().toString(),
                this.categoryName,
                ((TextView)parent.findViewById(R.id.element_description)).getText().toString()
            );

            //remove word from database, list and view group
            this.db.remove(w);
            this.words.remove(w);
            ViewGroup vg = (ViewGroup)this.findViewById(R.id.ownWords_linLayout);
            vg.removeView(parent);

            Logger.write
            (
                this.getResources().getString(R.string.ownWord_hint_removed),
                this, logOffset
            );
        }
    }
}
