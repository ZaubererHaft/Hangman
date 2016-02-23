package teamfmg.hangman;

/**
 * Created by Vincent on 23.02.2016.
 */
public class MultiplayerGame
{
    public enum GameState{
        UNKNOWN, CREATING, SEARCH4PLAYERS, FULL, INGAME, FINISHED
    }

    private GameState gameState;
    private int maxPlayers, currPlayers;
    private long id;
    private String gameName, password, leaderName;

    public MultiplayerGame(long id, String gameName, String password, int maxPlayers, String leaderName, GameState gameState)
    {
        this.id = id;
        this.gameName = gameName;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.currPlayers = 0;
        this.leaderName = leaderName;
        this.gameState = gameState;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getCurrPlayers() {
        return currPlayers;
    }

    public void setCurrPlayers(int currPlayers) {
        this.currPlayers = currPlayers;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(int leaderID) {
        this.leaderName = leaderName;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public String getRoomPlayers(){
        return currPlayers + "/" + maxPlayers;
    }
}
