package teamfmg.hangman;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Created by consult on 17.02.2016.
 */
public class MultiplayerLocal extends Singleplayer {

    private List<Word> wordList = new ArrayList<>();
    private HashMap<String, Integer> userList;
    private int currentPlayer = 0;
    private int currentWordPosition = 0;
    public static String[] usernames;
    public static List<String[]> scoreboard;

    /**
     * Local multiplayer with custom Amount of Players, wich all plays in Singleplayer Hardcore Mode.
     * Every Player gets the same Words
     */
    public MultiplayerLocal(){
        super();

        //Create and Fill the HashMap
        userList = new HashMap<>();

        for (int i = 0; i < usernames.length; i++)
        {
            userList.put(usernames[i], 0);
        }
    }

    /**
     * reset Game for Local Multiplayer
     */
    @Override
    protected void resetGame() {

        //List of words will be filled, so every Player gets the same Words
        if (this.wordList.size() > this.currentWordPosition){
            this.currentWordObject = this.wordList.get(currentWordPosition);
        }
        else
        {
            this.currentWordObject = this.db.getRandomWord(gameMode);
            this.wordList.add(this.currentWordObject);
        }

        //Standard preparation for next Round
        this.currentWord = currentWordObject.getWord();
        this.currentWord = this.currentWord.toUpperCase();
        this.resetButtons();
        this.newWord(this.currentWord);
        this.loadNextImg();
    }

    private List<String[]> hashMapToListOfArrayLists (HashMap<String, Integer> hashMap)
    {
        List<String[]> listOfArrayLists = new ArrayList<>();

        int highestScore = -1;
        String highestUser = null;


        for (int i = 0; i < hashMap.size(); i++)
        {
            for (int j = 0; j < hashMap.size(); j++)
            {
                if (hashMap.get(usernames[j]) > highestScore)
                {
                    highestScore = hashMap.get(usernames[j]);
                    highestUser = usernames[j];
                }
            }
            String[] s = new String[2];
            s[0] = highestUser;
            s[1] = "" + highestScore;
            listOfArrayLists.add(s);
            highestScore = -1;
            hashMap.put(highestUser, -1);
        }

        return listOfArrayLists;
    }

    /**
     * Prepaire next Player
     */
    private void nextPlayer(){
        this.currentPlayer++;

        //Every Player has finished
        if (this.currentPlayer == this.userList.size())
        {
            Intent i = new Intent(this, ScoreboardTab.class);
            i.putExtra("shownScoreboard", 3);
            scoreboard = new ArrayList<>();
            scoreboard = hashMapToListOfArrayLists(userList);
            this.startActivity(i);


            Logger.messageDialog("Endergebnis!", this.usernames[0] + " Score: " + this.userList.get(this.usernames[0]) + "\n"
                    + this.usernames[1] + " Score: " + this.userList.get(this.usernames[1]), this);
            Logger.logOnly("Multiplayer Beendet!");

            //Reset Game
            this.currentPlayer = 0;
            this.wordList.clear();
        }

        this.setCurrentScoreOnLable();
    }

    /**
     * Usually shows the Current Score, however in Local Multiplayer it shows the Current user.
     * Overiting this methode save overiting a lot Methodes
     */
    @Override
    protected void setCurrentScoreOnLable() {
        this.scoreLabel.setText("Current User: " + this.usernames[this.currentPlayer]);
    }

    /**
     * Finishes the Game
     * Show the result of the Word and resets the hangman
     * @param won is true if game is won
     */
    @Override
    protected void finishGame(boolean won) {
        String titleOfDialog;

        //Sets Title of the Popup Dialog
        if (this.currentBuildOfHangman == 0)
            titleOfDialog = getResources().getString(R.string.string_perfect);
        else if (won)
            titleOfDialog = getResources().getString(R.string.string_win);
        else
            titleOfDialog = getResources().getString(R.string.string_lose);

        //PopupDialog
        Logger.popupDialogGameResult(this.currentWordObject.getWord(),
                this.currentWordObject.getDescription(),
                this.currentWordObject.getCategory(),
                titleOfDialog, this);


        if (!won)
        {
            //Update the HashMap and reset Game
            this.userList.put(usernames[currentPlayer], score);
            this.score = 0;
            this.setCurrentScoreOnLable();
            this.setHangman(0);
            this.nextPlayer();
            currentWordPosition = 0;
        }
        else
        {
            //Add score and reset hangman a bit
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
