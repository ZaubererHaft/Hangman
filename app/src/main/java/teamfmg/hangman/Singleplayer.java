package teamfmg.hangman;

import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

//TODO: Comments
//TODO: Sonderzeichen Bugs

/**
 * The Hangman single player.
 * @since 0.5
 * @author Vincent
 */
public class Singleplayer extends Activity implements View.OnClickListener, IApplyableSettings{

    /**
     * the current shown picture of Hangman
     */
    private int currentBuildOfHangman;

    /**
     * Current word to guess.
     */
    private String currentWord;
    /**
     * Label to show the text.
     */
    private TextView label;
    /**
     * The current word in String pieces.
     */
    private String[] wordPieces;
    /**
     * All words which can appear.
     */
    private ArrayList <String> wordList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        Button back = (Button)this.findViewById(R.id.singleplayer_back);
        back.setOnClickListener(this);

        this.initButtons();

        /*
            This handles the database connection.
        */
        DatabaseManager db = new DatabaseManager(this);

        this.label = (TextView) findViewById(R.id.text_askedWord);

        //gets the categories of the settings.
        this.wordList = db.getWords();

        this.resetGame();
        this.changeBackground();
    }

    /**
     * Check the word for the pressed letter
     * TODO: Umlaute
     * @param letter which is clicked
     */
    private void checkLetter(String letter)
    {
        boolean isFalseWord = true;
        for (int i = 0; i < currentWord.length(); i++)
        {
            if (this.currentWord.charAt(i) == letter.charAt(0))
            {
                this.wordPieces[i] = letter;
                isFalseWord = false;
            }
        }
        if (isFalseWord)
        {
            this.buildHangman();
        }
        this.updateLabel();
    }

    /**
     * loads a new word
     * @param word asked word
     * @since 0.5
     */
    private void newWord(String word)
    {
        this.wordPieces = new String[word.length()];

        //add underlines for every letter in the asked word
        for (int i = 0; i < this.wordPieces.length; i++)
        {
            if (word.charAt(i) == ' ')
            {
                this.wordPieces[i] = " ";
            }
            else if (word.charAt(i) == '-')
            {
                this.wordPieces[i] = "-";
            }
            else if (word.charAt(i) == '.')
            {
                this.wordPieces[i] = ".";
            }
            else
            {
                this.wordPieces[i] = "_ ";
            }
        }

        this.updateLabel();
    }

    /**
     * Updates the label under the Hangman.
     * @since 0.5
     */
    private void updateLabel()
    {
        String s = "";
        for (String wordPiece : this.wordPieces)
        {
            s = s + wordPiece;
        }

        this.label.setText(s);

        //win the game
        if (s.equals(this.currentWord))
        {
            this.win();
        }
    }

    /**
     * Win the game.
     */
    private void win ()
    {
        //TODO: No hardcoded string
        Logger.write("Gewonnen! Das Wort war: " +  this.currentWord, this, -200);
        this.resetGame();
    }

    /**
     * Resets all buttons to Enabled(true)
     * @since 0.5
     */
    private void resetButtons()
    {
        //Reset Buttons in row one
        LinearLayout layoutRowOne = (LinearLayout) this.findViewById(R.id.linLayout_rowOne);
        for (int i = 0; i < layoutRowOne.getChildCount(); i++)
        {
            View v = layoutRowOne.getChildAt(i);
            if (v instanceof Button) 
            {
                Button b = (Button) v;
                b.setEnabled(true);
            }
        }

        //Reset Buttons in row two
        LinearLayout layoutRowTwo = (LinearLayout) this.findViewById(R.id.linLayout_rowTwo);
        for (int i = 0; i < layoutRowTwo.getChildCount(); i++){
            View v = layoutRowTwo.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setEnabled(true);
            }
        }

        //Reset Buttons in row three
        LinearLayout layoutRowThree = (LinearLayout) this.findViewById(R.id.linLayout_rowThree);
        for (int i = 0; i < layoutRowThree.getChildCount(); i++)
        {
            View v = layoutRowThree.getChildAt(i);
            if (v instanceof Button)
            {
                Button b = (Button) v;
                b.setEnabled(true);
            }
        }
    }

    /**
     * Inits all Buttons on the Keyboard.
     * @since 0.5
     */
    private void initButtons()
    {
        //Add OnClickListener for Buttons in row one
        LinearLayout layoutRowOne = (LinearLayout) this.findViewById(R.id.linLayout_rowOne);
        for (int i = 0; i < layoutRowOne.getChildCount(); i++)
        {
            View v = layoutRowOne.getChildAt(i);
            if (v instanceof Button)
            {
                Button b = (Button) v;
                b.setOnClickListener(this);
            }
        }

        //Add OnClickListener for Buttons in row two
        LinearLayout layoutRowTwo = (LinearLayout) this.findViewById(R.id.linLayout_rowTwo);
        for (int i = 0; i < layoutRowTwo.getChildCount(); i++)
        {
            View v = layoutRowTwo.getChildAt(i);
            if (v instanceof Button)
            {
                Button b = (Button) v;
                b.setOnClickListener(this);
            }
        }

        //Add OnClickListener for Buttons in row three
        LinearLayout layoutRowThree = (LinearLayout) this.findViewById(R.id.linLayout_rowThree);
        for (int i = 0; i < layoutRowThree.getChildCount(); i++){
            View v = layoutRowThree.getChildAt(i);
            if (v instanceof Button)
            {
                Button b = (Button) v;
                b.setOnClickListener(this);
            }
        }
    }

    /**
     * Builds the next Part of the Hangman.
     * @since 0.5
     */
    private void buildHangman()
    {
        final int fullHangman = 12;
        final int arms = 9;
        final int legs = 11;


        this.currentBuildOfHangman++;
        //Makes that arms and legs appears together
        if (this.currentBuildOfHangman == arms || this.currentBuildOfHangman == legs)
        {
            this.currentBuildOfHangman++;
        }
        if(this.currentBuildOfHangman <= fullHangman)
        {
            ImageView iv = (ImageView) findViewById(R.id.image_hangman);
            int id = this.getResources().getIdentifier
                   ("hm_"+this.currentBuildOfHangman, "drawable", this.getPackageName());
            iv.setImageResource(id);
        }
        if (this.currentBuildOfHangman == fullHangman)
        {
            this.loose();
        }
    }

    /**
     * Looses the game.
     * @since 0.5
     */
    private void loose()
    {
        //TODO: Offset bearbeiten bzw. Gewinnanzeige ändern
        Logger.write("Verloren! Das Wort war: " + this.currentWord, this, -200);
        resetGame();
    }

    /**
     * Resets the Round
     * @since 0.5
     */
    private void resetGame()
    {
        this.resetHangman();
        int random = (int)(Math.random() * this.wordList.size());
        this.currentWord = this.wordList.get(random);
        this.currentWord = this.currentWord.toUpperCase();
        this.resetButtons();
        this.newWord(this.currentWord);
    }

    /**
     * resets the Hangman. No Part is build
     * @since 0.5
     */
    private void resetHangman()
    {
        ImageView iv = (ImageView) findViewById(R.id.image_hangman);
        iv.setImageResource(R.drawable.hm_0);
        this.currentBuildOfHangman = 0;
    }


    @Override
    public void changeBackground() 
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_singleplayer);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) 
    {

        if(v.getId() == this.findViewById(R.id.singleplayer_back).getId())
        {
            this.finish();
            return;
        }

        Button b = (Button) v;
        b.setEnabled(false);
        checkLetter(b.getText().toString());
        switch (b.getText().toString()){
            case "U": checkLetter("Ü");
                break;
            case "O": checkLetter("Ö");
                break;
            case "A": checkLetter("Ä");
        }
    }
}
