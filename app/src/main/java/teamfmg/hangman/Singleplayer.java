package teamfmg.hangman;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;

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
    /**
     * How many parts there are of the hangman.
     */
    private static final int fullHangman = 12;
    /**
     * The ids of the hangman parts.
     */
    private static ArrayList<Integer> hangmanParts = new ArrayList<>(fullHangman);
    /**
     * The hangman pictures.
     */
    private static ArrayList<Drawable> pics = new ArrayList<>(fullHangman);
    /**
     * The main imageview.
     */
    private ImageView hangmanDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_singleplayer);

        Button back = (Button)this.findViewById(R.id.singleplayer_back);
        back.setOnClickListener(this);

        this.hangmanDrawable  = (ImageView) findViewById(R.id.image_hangman);
        this.label            = (TextView) findViewById(R.id.text_askedWord);

        //This handles the database connection.
        DatabaseManager db = new DatabaseManager(this);


        //gets the categories of the settings.
        this.wordList = db.getWords();

        this.changeBackground();
        this.initButtons();
        this.loadHangmanData(Settings.getQuality());
        this.resetGame();
    }

    /**
     * Loads all hangman images as bitmap.
     * @param quality The quality to decode the samples.<br/>
     *                <b>A value from 2 to 4 is recommended!</b>
     * @since 0.7
     */
    private void loadHangmanData(int quality)
    {
        if(Singleplayer.hangmanParts.size() == 0)
        {
            Logger.logOnly("Loading images...");

            Resources res = this.getResources();

            for (int i = 0; i < Singleplayer.fullHangman; i++)
            {
                //adds the IDs of the images
                Singleplayer.hangmanParts.add(this.getResources().getIdentifier
                        ("hm_" + i, "drawable", this.getPackageName()));

                //decodes drawables as bitmap
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = quality;

                Bitmap b =
                        BitmapFactory.decodeResource(res, Singleplayer.hangmanParts.get(i), options);
                Drawable d = new BitmapDrawable(res, b);

                //add them to a saved list
                Singleplayer.pics.add(d);
            }

            Logger.logOnly("Images successfully loaded!");
        }

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
        final int arms = 8;
        final int legs = 10;


        this.currentBuildOfHangman++;
        //Makes that arms and legs appears together
        if (this.currentBuildOfHangman == arms || this.currentBuildOfHangman == legs)
        {
            this.currentBuildOfHangman++;
        }
        if(this.currentBuildOfHangman < fullHangman)
        {
            //this.hangmanDrawable = pics.get(this.currentBuildOfHangman);
            Drawable d = pics.get(currentBuildOfHangman);
            this.hangmanDrawable.setImageDrawable(d);
        }
        else
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
        this.hangmanDrawable.setImageDrawable(pics.get(0));
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
    }
}
