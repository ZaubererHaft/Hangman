package teamfmg.hangman;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by consult on 17.02.2016.
 */
public class MultiplayerLocal extends Singleplayer {

    private List<Word> wordList = new ArrayList<>();
    private HashMap<String, Integer> userList;
    private int currentPlayer = 0;
    private int currentWordPosition = 0;
    public static String[] usernames;

    public MultiplayerLocal(){
        super();

        //sets the mode to Hardcore
        userList = new HashMap<>();
        for (int i = 0; i < usernames.length; i++){
            userList.put(usernames[i], 0);
        }
    }

    @Override
    protected void resetGame() {

        if (wordList.size() > currentWordPosition){
            currentWordObject = wordList.get(currentWordPosition);
        }
        else
        {
            this.currentWordObject = this.db.getRandomWord(gameMode);
            wordList.add(currentWordObject);
        }

        this.currentWord = currentWordObject.getWord();
        this.currentWord = this.currentWord.toUpperCase();
        this.resetButtons();
        this.newWord(this.currentWord);
        this.loadNextImg();
    }

    private void nextPlayer(){
        currentPlayer++;

        if (currentPlayer == userList.size())
        {
            Logger.messageDialog("Endergebnis!", usernames[0] + " Score: " + userList.get(usernames[0]) + "\n"
                    + usernames[1] + " Score: " + userList.get(usernames[1]), this);
            Logger.logOnly("Multiplayer Beendet!");
            this.currentPlayer = 0;
            this.wordList.clear();
        }

        this.setCurrentScoreOnLable();
    }

    @Override
    protected void setCurrentScoreOnLable() {
        scoreLabel.setText("Current User: " + usernames[currentPlayer]);
    }

    @Override
    protected void finishGame(boolean won) {
        String titleOfDialog;

        if (this.currentBuildOfHangman == 0)
            titleOfDialog = getResources().getString(R.string.string_perfect);
        else if (won)
            titleOfDialog = getResources().getString(R.string.string_win);
        else
            titleOfDialog = getResources().getString(R.string.string_lose);

        Logger.popupDialogGameResult(this.currentWordObject.getWord(),
                this.currentWordObject.getDescription(),
                this.currentWordObject.getCategory(),
                titleOfDialog, this);


        if (!won)
        {
            this.userList.put(usernames[currentPlayer], score);
            this.score = 0;
            this.setCurrentScoreOnLable();
            this.setHangman(0);
            this.nextPlayer();
            currentWordPosition = 0;
        }
        else
        {
            this.score = this.score + 10;
            this.setCurrentScoreOnLable();
            if (currentBuildOfHangman > resetingInHardcore)
            {
                this.currentBuildOfHangman = this.currentBuildOfHangman - this.resetingInHardcore;
            }
            else
            {
                currentBuildOfHangman = 0;
            }
            this.setHangman(currentBuildOfHangman);
            this.loadNextImg();
            currentWordPosition++;
        }

        this.resetGame();
    }
}
