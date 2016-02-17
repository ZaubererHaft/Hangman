package teamfmg.hangman;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by consult on 17.02.2016.
 */
public class MultiplayerLocal extends Singleplayer {

    private List<Word> wordList;
    private HashMap<String, Integer> userList;
    private int currentPlayer = 0;
    private String[] usernames;

    public MultiplayerLocal(){
        super();
        Bundle extra = this.getIntent().getExtras();
        String[] usernames = extra.getStringArray("players");

        for (int i = 0; i < usernames.length; i++){
            userList.put(usernames[i], 0);
        }
    }

    private void nextPlayer(){
        currentPlayer++;
        ((TextView)this.findViewById(R.id.label_singleplayer_score)).setText("Current Player: " + usernames[currentPlayer]);

        if (currentPlayer == userList.size()){
            //TODO: Zeige ergebniss
            Logger.logOnly("Multiplayer Beendet!");
            this.currentPlayer = 0;
        }
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

        //Special Hardcore Mode
        if (!won)
        {
            this.userList.put(usernames[currentPlayer], score);
            this.score = 0;
            this.setCurrentScoreOnLable();
            this.setHangman(0);
            this.nextPlayer();
        }

        if (won)
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
        }

        this.resetGame();
    }
}
