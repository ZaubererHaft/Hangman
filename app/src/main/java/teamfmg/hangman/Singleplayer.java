package teamfmg.hangman;

import android.content.DialogInterface;
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

public class Singleplayer extends Activity implements View.OnClickListener, IApplyableSettings{

    private Button  button_a, button_b, button_c, button_d, button_e,
                    button_f, button_g, button_h, button_i, button_j,
                    button_k, button_l, button_m, button_n, button_o,
                    button_p, button_q, button_r, button_s, button_t,
                    button_u, button_v, button_w, button_x, button_y,
                    button_z;

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
        categorys.add(db.CAPS);

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
    private void updateLabel(){
        String s = "";
        for (int i = 0; i < wordPieces.length; i++){
            s = s + wordPieces[i];
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
    private void initButtons(){
        // inits all Buttons
        Button button_a         =       (Button) findViewById(R.id.button_A);
        Button button_b         =       (Button) findViewById(R.id.button_B);
        Button button_c         =       (Button) findViewById(R.id.button_C);
        Button button_d         =       (Button) findViewById(R.id.button_D);
        Button button_e         =       (Button) findViewById(R.id.button_E);
        Button button_f         =       (Button) findViewById(R.id.button_F);
        Button button_g         =       (Button) findViewById(R.id.button_G);
        Button button_h         =       (Button) findViewById(R.id.button_H);
        Button button_i         =       (Button) findViewById(R.id.button_I);
        Button button_j         =       (Button) findViewById(R.id.button_J);
        Button button_k         =       (Button) findViewById(R.id.button_K);
        Button button_l         =       (Button) findViewById(R.id.button_L);
        Button button_m         =       (Button) findViewById(R.id.button_M);
        Button button_n         =       (Button) findViewById(R.id.button_N);
        Button button_o         =       (Button) findViewById(R.id.button_O);
        Button button_p         =       (Button) findViewById(R.id.button_P);
        Button button_q         =       (Button) findViewById(R.id.button_Q);
        Button button_r         =       (Button) findViewById(R.id.button_R);
        Button button_s         =       (Button) findViewById(R.id.button_S);
        Button button_t         =       (Button) findViewById(R.id.button_T);
        Button button_u         =       (Button) findViewById(R.id.button_U);
        Button button_v         =       (Button) findViewById(R.id.button_V);
        Button button_w         =       (Button) findViewById(R.id.button_W);
        Button button_x         =       (Button) findViewById(R.id.button_X);
        Button button_y         =       (Button) findViewById(R.id.button_Y);
        Button button_z         =       (Button) findViewById(R.id.button_Z);

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
    private void buildHangman(){
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
    private void resetGame(){
        int random = (int)(Math.random() * wordList.size());
        currentWord = wordList.get(random);
        currentWord = currentWord.toUpperCase();
        newWord(currentWord);
    }

    /**
     * resets the Hangman. No Part is build
     */
    private void resetHangman(){
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
