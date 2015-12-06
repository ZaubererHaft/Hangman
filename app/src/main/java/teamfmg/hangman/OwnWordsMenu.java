package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

public class OwnWordsMenu extends Activity implements View.OnClickListener, IApplyableSettings
{

    private TextView selectedText;
    private final int maxOwnWords = 127;
    private boolean addAWord = false;

    ArrayList<Integer> addAWordIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_words_menu);
        this.changeBackground();

        this.findViewById(R.id.ownwords_close).setOnClickListener(this);

        this.setUpLayout();
        this.readCustomWords();


    }

    private void setUpLayout()
    {

        this.addAWordIDs.add(R.id.ownWords_word);
        this.addAWordIDs.add(R.id.ownWords_category);
        this.addAWordIDs.add(R.id.ownWords_description);
        this.addAWordIDs.add(R.id.addWord_close);

        //add click listener to add a word elements
        for (Integer id:this.addAWordIDs)
        {
            this.findViewById(id).setOnClickListener(this);
        }

        this.findViewById(R.id.newWord_Done).setOnClickListener(this);
        this.findViewById(R.id.addWord_delete).setOnClickListener(this);

        ScrollView v = (ScrollView)findViewById(R.id.ownWords_Scroll);

        //adds click listener to words
        for (int i = 0; i < v.getChildCount(); i++)
        {
            v.getChildAt(i).setOnClickListener(this);
        }

        this.closeAddAWord();
    }

    private void readCustomWords()
    {

    }

    private void setAddAWord()
    {
        this.addAWord = true;

        for (Integer id:this.addAWordIDs)
        {
            this.findViewById(id).setVisibility(View.VISIBLE);
        }

        //change text of the add button
        ((Button)this.findViewById(R.id.newWord_Done)).setText(R.string.newWord_Done);

        this.findViewById(R.id.relLayout_ownWords).invalidate();
    }

    private void closeAddAWord()
    {
        this.addAWord = false;

        for (Integer id:this.addAWordIDs)
        {
            this.findViewById(id).setVisibility(View.INVISIBLE);
        }

        //change text of the add button
        ((Button)this.findViewById(R.id.newWord_Done)).setText(R.string.newWord_new);

        this.findViewById(R.id.relLayout_ownWords).invalidate();
        this.findViewById(R.id.relLayout_ownWords).refreshDrawableState();
        this.findViewById(R.id.relLayout_ownWords_sub).invalidate();
        this.findViewById(R.id.relLayout_ownWords_sub).refreshDrawableState();
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
        if(v.getId() == R.id.newWord_Done)
        {
            if(this.addAWord)
            {
                this.closeAddAWord();
            }
            else
            {
                this.setAddAWord();
            }
        }
        if(v.getId() == R.id.addWord_delete)
        {

        }
        if(v.getId() == R.id.addWord_close)
        {
            this.closeAddAWord();
        }
        if(v instanceof TextView && v.getId() != R.id.text_header_ownwords)
        {

            //deselect the old
            if(this.selectedText != null)
            {
                this.selectedText.setBackgroundColor(
                        this.getResources().getColor(R.color.color_none));
            }

            this.selectedText = (TextView) v;;
            this.selectedText.setBackgroundColor(
                    this.getResources().getColor(R.color.color_selected));
        }
    }
}
