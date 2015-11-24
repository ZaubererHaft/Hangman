package teamfmg.hangman;

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
public class Singleplayer extends Activity implements View.OnClickListener, IApplyableSettings{

    /**
     * the current shown picture of Hangman
     */
    private int currentBuildOfHangman;
    /**
     * This handles the database connection.
     */
    private DatabaseManager db;
    private List <String> categorys = new ArrayList<>();
    private String currentWord;
    private TextView label;
    private String[] wordPieces;
    private ArrayList <String> wordList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singleplayer);
        this.changeBackground();
        this.resetHangman();
        initButtons();

        db = new DatabaseManager(this);
        label = (TextView) findViewById(R.id.text_askedWord);
        categorys.add(Word.CAPS);

        wordList = db.getWords(categorys);

        resetGame();
    }

    /**
     * Check the word for the pressed letter
     * @param letter which is clicked
     */
    private void checkLetter(String letter) {
        String s;
        boolean isFalse = true;
        for (int i = 0; i < currentWord.length(); i++){
            if (currentWord.charAt(i) == letter.charAt(0)){
                wordPieces[i] = letter;
                isFalse = false;
            }
        }
        if (isFalse){
            buildHangman();
        }
        updateLabel();
    }

    /**
     * loads a new word
     * @param word asked word
     */
    private void newWord(String word){
        wordPieces = new String[word.length()];

        //add underlines for every letter in the asked word
        for (int i = 0; i < wordPieces.length; i++){
            if (word.charAt(i) == ' '){
                wordPieces[i] = " ";
            }
            else if (word.charAt(i) == '-'){
                wordPieces[i] = "-";
            }
            else if (word.charAt(i) == '.'){
                wordPieces[i] = ".";
            }
            else {
                wordPieces[i] = "_ ";
            }
        }

        resetHangman();
        resetButtons();
        updateLabel();
    }

    /**
     * Update the label under the Hangman
     */
    private void updateLabel()
    {
        String s = "";
        for (String wordPiece : wordPieces)
        {
            s = s + wordPiece;
        }
        label.setText(s);
        if (s.equals(currentWord)){
            Logger.write("Gewonnen! Das Wort war: " + currentWord, this, -200);
            resetGame();
        }
    }

    /**
     * Resets all buttons to Enabled(true)
     */
    private void resetButtons(){
        //Reset Buttons in row one
        LinearLayout layoutRowOne = (LinearLayout) this.findViewById(R.id.linLayout_rowOne);
        for (int i = 0; i < layoutRowOne.getChildCount(); i++){
            View v = layoutRowOne.getChildAt(i);
            if (v instanceof Button) {
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
        for (int i = 0; i < layoutRowThree.getChildCount(); i++){
            View v = layoutRowThree.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setEnabled(true);
            }
        }
    }

    /**
     * Inits all Buttons in the Keyboard
     */
    private void initButtons()
    {
        //Add OnClickListener for Buttons in row one
        LinearLayout layoutRowOne = (LinearLayout) this.findViewById(R.id.linLayout_rowOne);
        for (int i = 0; i < layoutRowOne.getChildCount(); i++){
            View v = layoutRowOne.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setOnClickListener(this);
            }
        }

        //Add OnClickListener for Buttons in row two
        LinearLayout layoutRowTwo = (LinearLayout) this.findViewById(R.id.linLayout_rowTwo);
        for (int i = 0; i < layoutRowTwo.getChildCount(); i++){
            View v = layoutRowTwo.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setOnClickListener(this);
            }
        }

        //Add OnClickListener for Buttons in row three
        LinearLayout layoutRowThree = (LinearLayout) this.findViewById(R.id.linLayout_rowThree);
        for (int i = 0; i < layoutRowThree.getChildCount(); i++){
            View v = layoutRowThree.getChildAt(i);
            if (v instanceof Button) {
                Button b = (Button) v;
                b.setOnClickListener(this);
            }
        }
}

    /**
     * Builds the next Part of the Hangman
     */
    private void buildHangman()
    {
        currentBuildOfHangman++;
        //Makes that arms and legs appears together
        if (currentBuildOfHangman == 9 || currentBuildOfHangman == 11){
            currentBuildOfHangman++;
        }
        if(currentBuildOfHangman <= 12){
            ImageView iv = (ImageView) findViewById(R.id.image_hangman);
            int id = this.getResources().getIdentifier("hm_"+currentBuildOfHangman, "drawable", this.getPackageName());
            iv.setImageResource(id);
        }
        if (currentBuildOfHangman == 12){
            //TODO: Offset bearbeiten bzw. Gewinnanzeige ändern
            Logger.write("Verloren! Das Wort war: " + currentWord, this, -200);
            resetGame();
        }
    }

    /**
     * Resets the Round
     */
    private void resetGame()
    {
        int random = (int)(Math.random() * wordList.size());
        currentWord = wordList.get(random);
        currentWord = currentWord.toUpperCase();
        newWord(currentWord);
    }

    /**
     * resets the Hangman. No Part is build
     */
    private void resetHangman()
    {
        ImageView iv = (ImageView) findViewById(R.id.image_hangman);
        iv.setImageResource(R.drawable.hm_0);
        currentBuildOfHangman = 0;
    }

    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_singleplayer);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        b.setEnabled(false);
        checkLetter(b.getText().toString());
    }
}
