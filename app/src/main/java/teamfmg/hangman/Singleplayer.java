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
     * The object of the currend word to guess
     */
    private Word currentWordObject;
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
            this.finishGame(true);
        }
    }

    /**
     * Finish the game.
     * Show Game Result and resets the Game
     * @since 0.5
     */
    private void finishGame(boolean won)
    {
        //TODO: Vorschlag: Win und Loose zusammenfügen, da nur ein Bool unterschiedlich ist
        //TODO: No hardcoded string

        Logger.popupDialog(this.currentWordObject.getWord(),
                    this.currentWordObject.getDescription(),
                    this.currentWordObject.getCategory(),
                    won, this);

        DatabaseManager db = new DatabaseManager(this);
        if (won){
            db.raiseScore(DatabaseManager.Attribut.WINS, 1);
        }
        else {
            db.raiseScore(DatabaseManager.Attribut.LOSES, 1);
        }

        this.resetGame();
    }

    /**
     * Resets all buttons to Enabled(true).
     * @since 0.5
     */
    private void resetButtons()
    {
        //Reset Buttons in row one
        LinearLayout layoutRowOne = (LinearLayout) this.findViewById(R.id.linLayout_rowOne);
        this.resetButtonRow(layoutRowOne);

        //Reset Buttons in row two
        LinearLayout layoutRowTwo = (LinearLayout) this.findViewById(R.id.linLayout_rowTwo);
        this.resetButtonRow(layoutRowTwo);

        //Reset Buttons in row three
        LinearLayout layoutRowThree = (LinearLayout) this.findViewById(R.id.linLayout_rowThree);
        this.resetButtonRow(layoutRowThree);

    }

    /**
     * Resets all buttons in a layout. (Sets them enabled)
     * @param ll layout.
     * @since 0.7
     */
    private void resetButtonRow (LinearLayout ll)
    {
        for (int i = 0; i < ll.getChildCount(); i++)
        {
            View v = ll.getChildAt(i);
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
        this.initButtonInRow(layoutRowOne);

        //Add OnClickListener for Buttons in row two
        LinearLayout layoutRowTwo = (LinearLayout) this.findViewById(R.id.linLayout_rowTwo);
        this.initButtonInRow(layoutRowTwo);

        //Add OnClickListener for Buttons in row three
        LinearLayout layoutRowThree = (LinearLayout) this.findViewById(R.id.linLayout_rowThree);
        this.initButtonInRow(layoutRowThree);
    }

    /**
     * Inits all buttons in a layout.
     * @param ll Layout.
     * @since 0.7
     */
    private void initButtonInRow(LinearLayout ll)
    {
        for (int i = 0; i < ll.getChildCount(); i++){
            View v = ll.getChildAt(i);
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
            this.finishGame(false);
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
     * Resets the Round
     * @since 0.5
     */
    private void resetGame()
    {
        this.resetHangman();
        currentWordObject = db.getRandomWord();
        this.currentWord = currentWordObject.getWord().toUpperCase();
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
