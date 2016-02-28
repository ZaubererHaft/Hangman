package teamfmg.hangman;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
    protected int currentBuildOfHangman;
    /**
     * Current word to guess.
     */
    protected String currentWord;
    /**
     * The object of the currend word to guess
     */
    protected Word currentWordObject;
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
    protected DatabaseManager db = DatabaseManager.getInstance();
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
    /**
     * Temp value for database connect;
     */
    private int wrongLetters, correctLetters;
    /**
     * Enum for the available GameModes
     */
    public enum GameMode {STANDARD, CUSTOM, HARDCORE, SPEED, LOCALMULTIPLAYER}
    /**
     * The choosen Gamemode
     */
    public static GameMode gameMode;
    /**
     * the current score in Speed and Hardcore mode
     */
    protected int score;
    /**
     * the name of the Package
     */
    private String pack;
    /**
     * Resources
     */
    private Resources res;
    /**
     * Amount of reseting Hangmanparts in Harcoremode.
     */
    final int resetingInHardcore = 3;

    protected TextView scoreLabel;

    private long multiplayerGameID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);

        db.setActivity(this);

        this.findViewById(R.id.singleplayer_back).setOnClickListener(this);
        this.initButtons();
        pack = this.getPackageName();
        res = this.getResources();

        this.label = (TextView) findViewById(R.id.text_askedWord);
        TextView header = (TextView)findViewById(R.id.label_singleplayer_header);
        scoreLabel = (TextView) findViewById(R.id.label_singleplayer_score);

        String headerText = null;
        switch (gameMode)
        {
            case STANDARD:
                headerText = this.getResources().getString(R.string.button_singleplayerMenu_StandardMode);
                break;
            case CUSTOM:
                headerText = this.getResources().getString(R.string.button_singleplayerMenu_CustomMode);
                break;
            case HARDCORE:
                headerText = this.getResources().getString(R.string.button_singleplayerMenu_HardcoreMode);
                break;
            case SPEED:
                headerText = this.getResources().getString(R.string.button_singleplayerMenu_SpeedMode);
                break;
            case LOCALMULTIPLAYER:
                headerText = this.getResources().getString(R.string.text_header_multiplayer_local);
                //In Local singleplayer the gamemode will be an Hardcore Mode.
                gameMode = GameMode.HARDCORE;
                break;
        }

        //Gets extras (Currently only in MultiplayerWifi mode)
        Bundle extra = this.getIntent().getExtras();
        if (extra != null)
        {
            //Set multiplayerGameID
            multiplayerGameID = extra.getLong("multiplayerGameID");
            //Set headerText
            headerText = extra.getString("multiplayerGameName");
        }

        header.setText(headerText);

        this.resetGame();
        setHangman(0);
        score = 0;
        setCurrentScoreOnLable();
        this.changeBackground();

        //Updating the lastOnline Time
        db.updateLastOnline();
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
            wrongLetters++;
        }
        else
        {
            correctLetters++;
            score++;
            setCurrentScoreOnLable();
        }
        this.updateLabel();
    }

    /**
     * loads a new word
     * @param word asked word
     * @since 0.5
     */
    protected void newWord(String word)
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
    protected void finishGame(boolean won)
    {
        String titleOfDialog;


        if (this.currentBuildOfHangman == 0)
        {
            titleOfDialog = getResources().getString(R.string.string_perfect);
        }
        else if (won)
        {
            titleOfDialog = getResources().getString(R.string.string_win);
        }
        else {
            titleOfDialog = getResources().getString(R.string.string_lose);
        }

        Logger.popupDialogGameResult(this.currentWordObject.getWord(),
                this.currentWordObject.getDescription(),
                this.currentWordObject.getCategory(),
                titleOfDialog, this);

        //Special Hardcore Mode
        if (!won && gameMode == GameMode.HARDCORE)
        {
            Statistics s = new Statistics();

            if (score > db.getCurrentStatistic(DatabaseManager.Attribute.HIGHSCORE_HARDCORE,
                    LoginMenu.getCurrentUser(this).getName()))
            {
                Logger.messageDialog(this.getResources().getString(R.string.string_newHighscore),
                        this.getResources().getString(R.string.string_yourNewHighscore) + score + "\n"
                                + this.getResources().getString(R.string.string_yourOldHighscore)
                        + db.getCurrentStatistic(DatabaseManager.Attribute.HIGHSCORE_HARDCORE, LoginMenu.getCurrentUser(this).getName()), this);

                s.scoreHardcore = score;

            }
            else
            {
                Logger.messageDialog(this.getResources().getString(R.string.string_lose),
                        this.getResources().getString(R.string.string_yourCurrentScore) + score + "\n"
                                + this.getResources().getString(R.string.string_yourHighscore)
                        + db.getCurrentStatistic(DatabaseManager.Attribute.HIGHSCORE_HARDCORE, LoginMenu.getCurrentUser(this).getName()), this);
            }
            score = 0;
            setCurrentScoreOnLable();
            setHangman(0);

            this.db.raiseStatistic(s, gameMode, null);
        }

        //Special Hardcore Mode
        if (won && gameMode == GameMode.HARDCORE)
        {
            score = score + 10;
            setCurrentScoreOnLable();
            if (currentBuildOfHangman > resetingInHardcore)
            {
                currentBuildOfHangman = currentBuildOfHangman - resetingInHardcore;
            }
            else
            {
                currentBuildOfHangman = 0;
            }
            setHangman(currentBuildOfHangman);
            loadNextImg();
        }

        //Special Standard Mode
        if (gameMode == GameMode.STANDARD)
        {
            Statistics s = new Statistics();

            if (won)
            {
                //db.raiseStatistic(DatabaseManager.Attribute.WINS, 1);
                s.wins = 1;
            }
            else
            {
                //db.raiseStatistic(DatabaseManager.Attribute.LOSES, 1);
                s.losses = 1;
            }
            if (this.currentBuildOfHangman == 0)
            {
                //db.raiseStatistic(DatabaseManager.Attribute.PERFECTS, 1);
                s.perfects = 1;
            }

            //db.raiseStatistic(DatabaseManager.Attribute.WRONG_LETTER, wrongLetters);
            s.wrongLetters = this.wrongLetters;
            //db.raiseStatistic(DatabaseManager.Attribute.CORRECT_LETTER, correctLetters);
            s.correctLetters = this.correctLetters;

            Integer lucker = null;

            if (won && this.currentBuildOfHangman == this.fullHangman -1)
            {
                lucker = 14;
            }

            this.db.raiseStatistic(s, gameMode, lucker);
        }

        //Updating the lastOnline Time
        db.updateLastOnline();

        this.resetGame();
    }

    /**
     * Resets all buttons to Enabled(true).
     * @since 0.5
     */
    protected void resetButtons()
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
        if (gameMode != GameMode.HARDCORE && (this.currentBuildOfHangman == arms || this.currentBuildOfHangman == legs))
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
    protected void loadNextImg()
    {
        if(!this.isLoading)
        {
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
                        Logger.logOnly("Done! Loaded hm_" + i);
                    }
                    catch (OutOfMemoryError ex)
                    {
                        Logger.logOnlyError(ex.getMessage());
                    }
                    catch(Exception ex)
                    {
                        Logger.logOnlyError(ex.getMessage());
                    }

                    isLoading = false;
                }
            });
            t.start();
        }
    }

    protected void setCurrentScoreOnLable()
    {
        if (gameMode == GameMode.CUSTOM || gameMode == GameMode.STANDARD){
            scoreLabel.setText("");
            return;
        }
        scoreLabel.setText(this.getResources().getText(R.string.string_score) + ": " + this.score);
    }

    /**
     * Resets the Round
     * @since 0.5
     */
    protected void resetGame()
    {
        if (gameMode != GameMode.HARDCORE)
        this.setHangman(0);

        this.currentWordObject = this.db.getRandomWord(gameMode);
        this.wrongLetters = 0;
        this.correctLetters = 0;

        if(this.currentWordObject == null)
        {
            Logger.write(this.getResources().getText(R.string.error_reload_categories) ,this);
            this.finish();
            return;
        }

        this.currentWord = currentWordObject.getWord();
        this.currentWord = this.currentWord.toUpperCase();
        this.resetButtons();
        this.newWord(this.currentWord);
        this.loadNextImg();
    }

    /**
     * resets the Hangman. No Part is build
     * @since 0.5
     */
    protected void setHangman(int buildOfHangman)
    {
        try
        {
            ImageView iv = (ImageView) findViewById(R.id.image_hangman);
            int id = res.getIdentifier("hm_" + buildOfHangman, "drawable", pack);
            iv.setImageResource(id);
            this.currentBuildOfHangman = buildOfHangman;
        }
        catch(OutOfMemoryError ex)
        {
            Logger.logOnlyError(ex);
        }
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

        if(!this.isLoading)
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
