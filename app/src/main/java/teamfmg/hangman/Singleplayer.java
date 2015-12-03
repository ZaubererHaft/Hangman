package teamfmg.hangman;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


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
     * Handles database connection.
     */
    private DatabaseManager db;
    /**
     * Full size of hangman.
     */
    public final int fullHangman = 11;
    /**
     * The next drawable to show.
     */
    private Drawable nextDrawable;
    /**
     * Detects whether a thread is loading images.
     */
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        this.findViewById(R.id.singleplayer_back).setOnClickListener(this);
        this.initButtons();

        this.db = new DatabaseManager(this);
        this.label = (TextView) findViewById(R.id.text_askedWord);

        this.resetGame();
        this.changeBackground();
    }

    /**
     * Check the word for the pressed letter
     * @param letter which is clicked
     */
    private void checkLetter(String letter)
    {
        boolean isFalseWord = true;

        for (int i = 0; i < this.currentWord.length(); i++)
        {
            char sign = this.currentWord.charAt(i);

            if(sign == 'Ü' && letter.equals("U"))
            {
                this.wordPieces[i] = "Ü";
                isFalseWord = false;
            }
            else if(sign == 'Ä' && letter.equals("A"))
            {
                this.wordPieces[i] = "Ä";
                isFalseWord = false;
            }
            else if(sign == 'Ö' && letter.equals("O"))
            {
                this.wordPieces[i] = "Ö";
                isFalseWord = false;
            }
            if (sign == letter.charAt(0))
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
     * Updates the label(text) under the Hangman.
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
     * @since 0.5
     */
    private void win ()
    {
        //TODO: No hardcoded string
        Logger.write("Gewonnen! Das Wort war: " +  this.currentWord, this, -200);
        this.resetGame();
    }

    /**
     * Resets all buttons to Enabled(true)
     * TODO: Verbessern :)
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
     * TODO: Verbesssern :)
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

        final int arms = 7;
        final int legs = 9;

        this.currentBuildOfHangman++;

        //Makes that arms and legs appears together
        if (this.currentBuildOfHangman == arms || this.currentBuildOfHangman == legs)
        {
            this.currentBuildOfHangman++;
        }
        if(this.currentBuildOfHangman < this.fullHangman)
        {
            ((ImageView) findViewById(R.id.image_hangman)).setImageDrawable(this.nextDrawable);
            this.loadNextImg();
        }
        else if (this.currentBuildOfHangman == this.fullHangman)
        {
            this.loose();
        }
    }

    /**
     * Loads the next hangman picture.
     * @since 0.7
     */
    private void loadNextImg()
    {
        if(!this.isLoading)
        {
            final String pack = this.getPackageName();
            final Resources res = this.getResources();

            Thread t = new Thread(new Runnable()
            {
                int id = 0;

                @Override
                public void run()
                {
                    isLoading = true;

                    try
                    {
                        System.gc();
                        Logger.logOnly("Loading next image...");
                        int i = currentBuildOfHangman + 1;
                        id = res.getIdentifier
                                ("hm_" + i, "drawable", pack);
                        nextDrawable = res.getDrawable(id);
                        Logger.logOnly("Done!");
                    }
                    catch (OutOfMemoryError ex)
                    {
                        Logger.logOnlyError(ex.getMessage());
                    }

                    isLoading = false;
                }
            });
            t.start();
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
        this.currentWord = db.getRandomWord().toUpperCase();
        this.resetButtons();
        this.newWord(this.currentWord);
        this.loadNextImg();
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

        if(!isLoading)
        {
            Button b = (Button) v;
            b.setEnabled(false);
            checkLetter(b.getText().toString());
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }
}
