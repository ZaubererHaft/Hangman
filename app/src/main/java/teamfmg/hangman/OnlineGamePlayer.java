package teamfmg.hangman;

/**
 * Created by Vincent on 23.02.2016.
 */
public class OnlineGamePlayer {

    public enum PlayerState{
        UNKNOWN, JOINED, READY, PLAYING, FINISHED, LEFTED
    }

    private PlayerState playerState;
    private long onlineGameID;
    private int userID, score;

    public OnlineGamePlayer(long onlineGameID, int userID, int score, PlayerState playerState)
    {
        this.onlineGameID = onlineGameID;
        this.userID = userID;
        this.score = score;
        this.playerState = playerState;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public long getOnlineGameID() {
        return onlineGameID;
    }

    public void setOnlineGameID(long onlineGameID) {
        this.onlineGameID = onlineGameID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
