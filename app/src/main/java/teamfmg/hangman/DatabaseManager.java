package teamfmg.hangman;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * <u><h1>Class to handle database connections.</h1></u><br />
 * Using <b>JDBC</b>, this class has implemented all SQL statements directly in the code as well as data to
 * connect.<br />
 * As using {@link ResultSet}, this class is designed as a Singleton using getInstance().
 * <br />
 * Be aware to set set the right activity context with setActivity().<br /><br />
 * Created by Ludwig on 1/27/16.
 * @author Ludwig
 * @since 0.1
 */
public class DatabaseManager extends Thread
{
    /**
     * Name of the database.
     */
    private static final String DATABASE_NAME       = "proj_hangman";
    /**
     * Name of the table users.
     */
    private static final String TABLE_USERS_NAME    = "users";
    /**
     * Name of the table words.
     */
    private static final String TABLE_WORDS    = "words";
    /**
     * URL to server.
     */
    private static final String CONNECTING_URL = "h2530840.stratoserver.net";
    /**
     * Time to connect to the server.
     */
    private static final long CONNECTING_TIME = 2000;
    /**
     * This handles the database connection.
     */
    private static DatabaseManager manager;
    /**
     * Context representing the activity.
     */
    private Activity activity;
    /**
     * Attribute to choose statistics.
     */
    public enum Attribute {SCORE, HIGHSCORE_HARDCORE, HIGHSCORE_SPEEDMODE, WINS, LOSES, PERFECTS, CORRECT_LETTER, WRONG_LETTER}
    /**
     * The connection. Represents the status to the online DB.
     */
    private Connection connection = null;
    /**
     * The SQL statement.
     */
    private Statement statement = null;
    /**
     * The ResultSet of all statements.
     */
    private ResultSet res = null;
    /**
     * Reference to the offline database.
     */
    private OfflineDatabase od;

    /**
     * Default constructor to avoid instances.
     */
    private DatabaseManager()
    {

    }

    @Override
    public void run()
    {
        Looper.prepare();

        while (!isInterrupted())
        {
            /*
            If we're running offline mode, try to reconnect
             */
            if(!this.isOnline())
            {
                Logger.logOnlyError("Connection lost - try reconnection!");

                /**
                 * Shut down old connection
                 */
                if (this.connection != null)
                {
                    try
                    {
                        this.connection.close();
                        this.connection = null;
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    this.connect();
                }
            }

            try
            {
                Thread.sleep(CONNECTING_TIME);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds a user to the offline DB.
     * @param user User to add.
     * @since 1.1
     */
    public void addOfflineUser(User user)
    {
        if(this.od == null)
        {
            this.od = new OfflineDatabase();
        }

        this.od.addUser(user);
    }

    /**
     * Checks the online connection.
     * @return true
     * @since 1.1
     */
    public boolean isOnline()
    {
        /*if (this.hasNetworkConnection())
        {
            try
            {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout((int)CONNECTING_TIME);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            }
            catch (IOException e)
            {
                Logger.logOnlyError(e.getMessage());
            }
        }
        else
        {
            return false;
        }*/

        return hasNetworkConnection();
    }

    /**
     * Detects whether there is network connection.
     * @return boolean.
     * @since 1.1
     */
    private boolean hasNetworkConnection()
    {

        ConnectivityManager cm =
                (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Gets the instance of the singleton.
     * @return {@link DatabaseManager}
     * @since 1.1
     */
    public static DatabaseManager getInstance()
    {
        if(manager == null)
        {
            manager = new DatabaseManager();
        }
        return manager;
    }

    /**
     * Sets the activity context.
     * @param a Activity to set.
     * @since 1.1
     */
    public void setActivity(Activity a)
    {
        if(!this.isAlive()) {
            this.start();
        }
        this.activity = a;
    }

    /**
     * Sets an achievemnt DONE for a player.
     * @param achievements AString with userIDs and achID.
     */
    public void setAchievement(String achievements)
    {
        String query = "INSERT INTO userachievements (userID, achievementID) " +
                "VALUES "+achievements+";";
        this.useCommand(query, true);
    }

    /**
     * Checks whether a mal exists in the DB,
     * @param mail mail to check {@link String}
     * @return Boolean.
     * @since 1.2
     */
    public boolean exists(String mail)
    {

        String command = "SELECT mail FROM users WHERE mail LIKE '"+mail+"';";

        this.useCommand(command, false);

        try
        {        //add all words to the list
            if (this.res != null && this.res.next())
            {
                return true;
            }
        }
        catch (SQLException e)
        {
            //e.printStackTrace();
            //Logger.write(e, this.activity);
            Logger.logOnlyError(e.getMessage());
        }
        finally
        {
            this.closeConnection();
        }

        return false;
    }

    /**
     * Gets all possible achievements.
     * @return ArrayList.
     * @since 1.2
     */
    public ArrayList<Achievement> getAchievements()
    {
        String command ="SELECT * FROM achievements";

        ArrayList<Achievement> achievements = new ArrayList<>();

        this.useCommand(command, false);

        try
        {        //add all words to the list
            if (this.res != null)
            {
                while (this.res.next())
                {
                    Achievement a = new Achievement
                    (
                        this.res.getInt(1),
                        this.res.getString(2),
                        this.res.getString(3)
                    );
                    // Add word
                    achievements.add(a);
                }
            }
        }
        catch (SQLException e)
        {
            //e.printStackTrace();
            //Logger.write(e, this.activity);
            Logger.logOnlyError(e.getMessage());
        }
        finally
        {
            this.closeConnection();
        }

        return achievements;
    }

    /**
     * Get achievements of a specific user
     * @param userID userID to check.
     * @return integer
     * @since 1.2
     */
    public ArrayList<Integer> getAchievements(int userID)
    {
        String command =
                "SELECT * FROM userachievements WHERE userID LIKE '"+userID+"';";

        ArrayList<Integer> achievements = new ArrayList<>();

        this.useCommand(command, false);

        try
        {        //add all words to the list
            if (this.res != null)
            {
                while (this.res.next())
                {
                    // Add word
                    achievements.add(this.res.getInt(2));
                }
            }
        }
        catch (SQLException e)
        {
            Logger.logOnlyError(e.getMessage());
            //Logger.write(e, this.activity);
        }
        finally
        {
            this.closeConnection();
        }

        return achievements;
    }

    /**
     * This connects to the online database.
     * @since 1.1
     */
    private void connect()
    {
        if(connection == null)
        {
            if(this.isOnline())
            {
                try
                {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection
                            ("jdbc:mysql://" + CONNECTING_URL + "/" + DATABASE_NAME
                                    + "?useSSL=false&user=external&password=asdfg-01");
                    Logger.logOnly("Connected!");
                }
                catch (SQLException ex)
                {
                    Logger.logOnlyError("Failed to communicate with server - " + ex.getMessage());
                }
                catch (ClassNotFoundException ex)
                {
                    Logger.logOnlyError("Error loading class - " + ex.getMessage());
                }
            }
        }
    }

    /**
     * This closes the connection
     * @since 1.1
     */
    private void closeConnection()
    {
        try
        {

            if (this.statement != null)
            {
                this.statement.close();
            }
            if(this.res != null)
            {
                this.res.close();
            }
        }
        catch (SQLException e)
        {
            //e.printStackTrace();
            //Logger.write(e,this.activity);
            Logger.logOnlyError(e.getMessage());
        }
    }

    /**
     * Deletes a word from the user.
     * @param wordname word to description.
     * @since 1.1
     */
    public void deleteCustomWord(String wordname)
    {
        String query = "DELETE FROM userwords WHERE " +
            "(SELECT wordID FROM customwords WHERE word LIKE '" + wordname + "') LIKE wordID " +
            "AND " +LoginMenu.getCurrentUser(activity).getId()+" LIKE userID;" ;


        this.useCommand(query, true);

    }

    /**
     * Gets a custom word identified by a user.
     * @param u User.
     * @return List of words.
     * @since 1.1
     */
    public ArrayList<Word> getCustomWords(User u)
    {
        String command ="SELECT customwords.word, userwords.description FROM customwords "+
        "INNER JOIN userwords ON userwords.wordID = customwords.wordID "+
        "INNER JOIN users ON users._id = userwords.userID "+
        "WHERE users.username LIKE '"+u.getName()+"';";

        ArrayList<Word> words = new ArrayList<>();

        this.useCommand(command, false);

        try
        {        //add all words to the list
            if (this.res != null)
            {
                while (this.res.next())
                {
                    Word w = new Word
                    (
                            this.res.getString(1),
                            "ownWord",
                            this.res.getString(2)
                    );
                    // Add word
                    words.add(w);
                }
            }
        }
        catch (SQLException e)
        {
            Logger.logOnlyError(e.getMessage());
            //Logger.write(e, this.activity);
        }
        finally
        {
            this.closeConnection();
        }

        return words;
    }


    /**
     * Adds a user to the database.
     * @param u User to add
     * @throws SQLiteException
     * @since 0.1
     */
    public void addUser (User u) throws SQLiteException
    {

        String cmd = "INSERT INTO "+ TABLE_USERS_NAME + "(username, password, mail) VALUES ('"+
                u.getName() + "','"+
                u.getPassword() + "','"+
                u.getMail()+"');";

        this.useCommand(cmd, true);
    }

    /**
     * Executes a command to the database and closes it automatically
     * @param command {@link String}
     * @since 1.1
     */
    public void useCommand (String command, boolean manipulative)
    {
        try
        {
            //check connection before running command
            this.connect();

            if(isOnline())
            {
                this.statement = this.connection.createStatement();

                if (manipulative)
                {
                    this.statement.executeUpdate(command);
                }
                else
                {
                    this.res = this.statement.executeQuery(command);
                }
            }


        }
        catch (SQLException e)
        {
            Logger.logOnlyError(e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * Loads all words from the *.csv to the data base.
     * @since 0.5
     */
    /*public void loadWords()
    {

        Logger.logOnly(R.string.hint_loading);

        //Get all files in raw directory
        Field[] fields = R.raw.class.getFields();

        for (Field field : fields)
        {
            int id = this.activity.getResources().getIdentifier(
                    field.getName(), "raw", this.activity.getPackageName());

            InputStream in = this.activity.getResources().openRawResource(id);

            BufferedReader buffer = new BufferedReader(new InputStreamReader(in),8192);

            String line;

            int amount = 0;

            while(true)
            {
                try
                {
                    line = buffer.readLine();

                    if(line == null)
                    {
                        Logger.logOnly(field.toString()+": Data loaded: " +amount);
                        break;
                    }
                    ++amount;
                    String[] list = line.split(";");

                    String createTableStatement;

                    if(list.length == 2)
                    {
                        createTableStatement =
                                "INSERT INTO " + TABLE_WORDS + " (word,category) " +
                                        " VALUES(\"" + list[0] + "\", \"" + list[1] + "\");";
                    }
                    else
                    {
                        createTableStatement =
                                "INSERT INTO " + TABLE_WORDS + " (word,category,description) " +
                                        " VALUES(\"" + list[0] + "\", \"" + list[1] + "\",\"" + list[2] + "\");";
                    }

                    this.useCommand(createTableStatement, true);
                }

                catch (IOException ex)
                {
                    ex.printStackTrace();
                    Logger.logOnly(ex.getMessage());
                }
                finally
                {
                    this.closeConnection();
                }
            }
        }
    }*/

    /**
     * Changes the password.
     * @param newPW The new password.
     * @since 1.1
     */
    public void changePassword(String newPW) throws SQLException
    {
        this.useCommand("UPDATE users SET password = '" + newPW + "' WHERE username = '" +
                LoginMenu.getCurrentUser(activity).getName() + "';", true);

        this.od.changePW(newPW);
    }

    /**
     * Adds an value to the current value in the Database
     * @param statistics The values to rise
     * @since 1.1
     */
    public void raiseStatistic(Statistics statistics, Singleplayer.GameMode mode, Integer forcedAchievemet)
    {

        Integer[] addStats = statistics.asList();

        String query =
                "UPDATE users SET " +
                "wins = wins + "+addStats[0]+","+
                "perfects = perfects + "+addStats[1]+","+
                "loses = loses + "+addStats[2]+","+
                "correctLetters = correctLetters + "+addStats[3]+","+
                "wrongLetters = wrongLetters+ "+addStats[4]+","+
                "highscoreHardcore = highscoreHardcore + "+addStats[5]+","+
                "highscoreSpeedmode = highscoreSpeedmode + "+addStats[6]+ " "+
                "WHERE username LIKE '" + LoginMenu.getCurrentUser(activity).getName() + "';";

        this.useCommand(query, true);

        this.checkForAchievements(mode, forcedAchievemet);


    }

    /**
     * This checks for new Achievements...
     * @param mode to Check
     */
    private synchronized void checkForAchievements(Singleplayer.GameMode mode, Integer forcedAchievement)
    {
        int[] updatedStats = this.getAllStatistics(LoginMenu.getCurrentUser(activity).getName());

        //how the statistics are set up
        //wins, perfects, loses, correctLetters, wrongLetters,highscoreHardcore, highscoreSpeedmode

        int userID;

        if(LoginMenu.getCurrentUser(activity) != null)
        {
            userID = LoginMenu.getCurrentUser(activity).getId();
        }
        else
        {
            return;
        }

        //all possible achievements
        ArrayList<Integer> achs = this.getAchievements(userID);
        ArrayList<Integer[]> conditions = new ArrayList<>();

        BufferedReader br = null;
        InputStream is = null;

        boolean newAchievement = false;

        try
        {
            /**
             * Default achievements.
             */
            is = this.activity.getResources().openRawResource(R.raw.ach_default);
            br = new BufferedReader(new InputStreamReader(is));

            String line;

            /**
             * Format achievements *.txt
             */
            while ((line = br.readLine()) != null)
            {
                String[] arg = line.split(";");
                Integer[] cons = new Integer[3];

                //read conditions and add them
                cons[0] = Integer.parseInt(arg[0]);
                cons[1] = Integer.parseInt(arg[1]);
                cons[2] = Integer.parseInt(arg[2]);

                conditions.add(cons);
            }

            String statsToAdd = "";

            /**
             * Format a string with all achievements to add.
             */
            for (int i = 0; i < conditions.size(); i++)
            {
                if (updatedStats[conditions.get(i)[0]] >= conditions.get(i)[1] &&
                        !achs.contains(conditions.get(i)[2]))
                {
                    //this.setAchievement(userID, conditions.get(i)[2]);
                    statsToAdd += "("+userID +", "+conditions.get(i)[2] + "),";
                    newAchievement = true;
                }
            }

            final int firstTimeHardcore = 7;

            /**
             * Mode specific stats.
             */
            switch (mode)
            {
                case HARDCORE:
                    if(!achs.contains(firstTimeHardcore))
                    {
                        statsToAdd += "("+userID+", "+firstTimeHardcore+"),";
                        newAchievement = true;
                    }
                break;
            }

            /**
             * Special achievements.
             */
            if(forcedAchievement != null)
            {
                if(!achs.contains(forcedAchievement))
                {
                    statsToAdd += "("+userID+", "+forcedAchievement+"),";
                    newAchievement = true;
                }
            }

            //remove signs
            if(statsToAdd.endsWith(","))
            {
                statsToAdd = statsToAdd.substring(0,statsToAdd.length() -1 );
            }

            if(statsToAdd.length() > 0 )
            {
                this.setAchievement(statsToAdd);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(is != null)
                {
                    is.close();
                }
                if(br != null)
                {
                    br.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        /**
         * Inform user...
         */
        if(newAchievement)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                Intent intent = new Intent(activity.getBaseContext(), AchievementsMenu.class);
                Logger.showNotification
                (
                    this.activity,
                    intent,
                    this.activity.getString(R.string.info_achievement),
                    this.activity.getString(R.string.info_achievement),
                    R.drawable.hangman_logo
                );
            }
            else
            {
                this.activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Logger.write
                        (
                            activity.getString(R.string.info_achievement),
                            activity
                        );
                    }
                });
            }
        }
        else
        {
            Logger.logOnly("No new achievement!");
        }
    }

    /**
     * Gets all statistics sorted in an array.
     * @return int[]
     */
    public int[] getAllStatistics(String name)
    {
        int[] i = new int[8];

        String cmd = "SELECT wins, perfects, loses, correctLetters, wrongLetters, " +
                "highscoreHardcore, highscoreSpeedmode FROM users WHERE username LIKE '"
                + name +"';";

        this.useCommand(cmd, false);

        try
        {
            if(this.res != null && this.res.next())
            {

                for (int a = 0; a < i.length; a++)
                {
                    if(a < i.length - 1)
                    {
                        i[a] = this.res.getInt(a + 1);
                    }
                }

                i[i.length-1] = ((i[0] + (i[1] * 4) - i[2]) * 10) + i[3] - i[4];

                return i;
            }
        }
        catch (SQLException ex)
        {
            //Logger.write(ex,this.activity);
            //ex.printStackTrace();
            Logger.logOnlyError(ex.getMessage());
        }
        finally
        {
            this.closeConnection();
        }

        return i;
    }

    /**
     * Gets the last Time an User was online
     * @param name User
     * @return Date the user was last time online
     */
    public Date getLastOnline(String name){
        String string = null;

        //Database command
        String cmd = "SELECT lastOnline FROM users WHERE username LIKE '" + name +"';";

        this.useCommand(cmd, false);

        try
        {
            if(this.res != null && this.res.next())
            {
               string = this.res.getString(1);
            }
        }
        catch (SQLException ex)
        {
            Logger.write(ex,this.activity);
            ex.printStackTrace();
            return null;
        }
        finally
        {
            this.closeConnection();

        }

        //converting in an DateFormat
        Date lastLogin = TimeHelper.stringToDate(string);

        return lastLogin;
    }


    /**
     * Creates an Select for the DB for the Statistics
     * @param attribut The type of StatisticMenu
     * @return Value of the attribut for the current User
     * @since 1.1
     */
    public int getCurrentStatistic (Attribute attribut, String username)
    {
        //convert attribut for the using in the DB
        String attributName = getAttributName(attribut);
        String query;

        if (attribut == Attribute.SCORE)
        {
            query = "SELECT ((wins + (perfects * 4) - loses)*10 + correctLetters - wrongletters)" +
                    " AS 'score' FROM  users WHERE username LIKE '" + username + "';";
        }
        else
        {
            //DB Command for get the wish-value
            query = "SELECT " + attributName + " FROM " + TABLE_USERS_NAME + " WHERE username LIKE '"
                    + username + "';";
        }

        this.useCommand(query, false);

        try
        {
            if(this.res != null && this.res.next())
            {
                return this.res.getInt(attributName);
            }
        }
        catch (SQLException ex)
        {
            //Logger.write(ex,this.activity);
            //ex.printStackTrace();
            Logger.logOnlyError(ex.getMessage());
        }
        finally
        {
            this.closeConnection();
        }
        return 0;
    }

    /**
     * Returns a list with highscores.
     * @return {@link List}
     * @since 1.1
     */
    public List<String[]> getHardcoreScoreboard ()
    {
        return getScoreboard("highscoreHardcore");
    }
    /**
     * Returns a list with highscores.
     * @return {@link List}
     * @since 1.1
     */
    public List<String[]> getSpeedModeScoreboard ()
    {
        return getScoreboard("highscoreSpeedmode");
    }
    /**
     * Returns a list with highscores.
     * @return {@link List}
     * @since 1.1
     */
    public List<String[]> getStandardScoreboard ()
    {
        return getScoreboard("highscoreStandard");
    }


    /**
     * Executes a command getting highscore values given by the name in the column.
     * @param colomnName {@link String} - e.g. highscoreSpeedmode
     * @return List with String elements.
     * @since 1.1
     */
    private List<String[]> getScoreboard(String colomnName)
    {
        String query;

        if (colomnName.equals("highscoreStandard"))
        {
            query = "SELECT username, ((wins + (perfects * 4) - loses)*10 + correctLetters - wrongletters) " +
                    "AS 'score', lastOnline FROM  users ORDER BY score DESC";
        }
        else
        {
            query = "SELECT username, " + colomnName + ", lastOnline FROM users ORDER BY " + colomnName + " DESC;";
        }

        this.useCommand(query, false);

        List<String[]> list = new ArrayList<>();

        try
        {
            if(this.res != null)
            {
                while (this.res.next())
                {
                    String[] innerList = new String[3];
                    innerList[0] = this.res.getString(1);
                    innerList[1] = ((Integer)this.res.getInt(2)).toString();
                    innerList[2] = this.res.getString(3);
                    list.add(innerList);
                }
                return list;
            }
        }
        catch (SQLException ex)
        {
            //Logger.write(ex,this.activity);
            //ex.printStackTrace();
            Logger.logOnlyError(ex.getMessage());
        }
        finally
        {
            this.closeConnection();
        }
        return null;
    }

    /**
     * Convert the Enum to an String. This String is the correct name of the Attribut in the Database
     * @param attribut Attribut which will get converted
     * @return correct name of the attribut in the Database
     * @since 1.1
     */
    private String getAttributName(Attribute attribut){

        String attributName = null;

        switch (attribut) {
            case SCORE:
                attributName = "score";
                break;
            case WINS:
                attributName = "wins";
                break;
            case LOSES:
                attributName = "loses";
                break;
            case CORRECT_LETTER:
                attributName = "correctLetters";
                break;
            case WRONG_LETTER:
                attributName = "wrongLetters";
                break;
            case PERFECTS:
                attributName = "perfects";
                break;
            case HIGHSCORE_HARDCORE:
                attributName = "highscoreHardcore";
                break;
            case HIGHSCORE_SPEEDMODE:
                attributName = "highscoreSpeedmode";
        }

        return attributName;
    }

    /**
     * Add a word to the database
     * @param w Word to add
     * @since 0.5
     */
    public boolean addWord (Word w)
    {
        try
        {
            String command = "INSERT INTO customwords VALUES (DEFAULT, '"+ w.getWord() + "');";

            String cmd2 = "INSERT INTO userwords VALUES ( ("+
                    "SELECT wordID FROM customwords WHERE word LIKE '"+w.getWord()+"'),('"+
                    LoginMenu.getCurrentUser(activity).getId()+"'),'"+w.getDescription()+"');";

            if(!exists(w))
            {
                this.useCommand(command, true);
            }
            if(existsInUserWord(w))
            {
                Logger.write
                (
                    activity.getResources().getString(R.string.ownWord_hint_word_already_exists),
                        activity, -70
                );
                return false;
            }
            else
            {
                this.useCommand(cmd2, true);

                Logger.write
                (
                        activity.getResources().getString(R.string.ownWord_hint_added),
                        activity, -70
                );
                return true;
            }
        }
        catch(SQLiteException ex)
        {
            //ex.printStackTrace();
            //Logger.write(ex, this.activity);
            Logger.logOnlyError(ex.getMessage());
        }
        finally
        {
            this.closeConnection();
        }

        return false;
    }



    /**
     * Searches for an word and return its existance.
     * @param word Word to add.
     * @return boolean
     * @since 0.7
     */
    public boolean existsInUserWord(Word word)
    {
        boolean b = false;

        //Bsp: SELECT * FROM words WHERE word LIKE "test" AND category LIKE "testCategory";
        String query = "SELECT * FROM userwords WHERE (SELECT wordID FROM customwords WHERE word " +
                "LIKE '" + word.getWord() + "') LIKE wordID " +
                "AND "+LoginMenu.getCurrentUser(activity).getId()+" LIKE userID;" ;

        this.useCommand(query, false);

        //a word exists if we found something
        try
        {
            b = this.res.next();
        }
        catch (SQLException e)
        {
            //e.printStackTrace();
            //Logger.write(e, this.activity);
            Logger.logOnlyError(e.getMessage());
        }
        finally
        {
            this.closeConnection();
        }
        return b;
    }

    /**
     * Checks whether a word exists om the database.
     * @param word word to check.
     * @return boolean.
     */
    public boolean exists(Word word)
    {
        boolean b = false;

        if(word.getWord().lastIndexOf(" ") == word.getWord().length()-1)
        {
            word.setWord(word.getWord().substring(0,word.getWord().length()-1));
        }

        //Bsp: SELECT * FROM words WHERE word LIKE "test" AND category LIKE "testCategory";
        String query = "SELECT * FROM cust" +
                "omwords WHERE word LIKE '"+word.getWord()+"' OR word LIKE '" + word.getWord() +" '; ";

        this.useCommand(query, false);

        //a word exists if we found something
        try
        {
            b = this.res.next();
        }
        catch (SQLException e)
        {
            //e.printStackTrace();
            //Logger.write(e, this.activity);
            Logger.logOnlyError(e.getMessage());
        }
        finally
        {
            this.closeConnection();
        }
        return b;
    }

    /**
     * Gets a random word from the database.
     * @return String
     * @since 0.7
     */
    public Word getRandomWord(Singleplayer.GameMode gameMode)
    {
        if(!this.isOnline())
        {
            return this.od.getRandomWord();
        }

        String query = "SELECT word, category, description FROM " + TABLE_WORDS;

        if (gameMode == Singleplayer.GameMode.CUSTOM)
        {
            List <String> categories = Settings.getCategories();

            for (int i = 0; i < categories.size(); i++)
            {
                //overwrite query if there are categories.
                if (i == 0)
                {
                    query = query + " WHERE category LIKE '" + categories.get(i) + "'";
                    continue;
                }
                query = query + " OR category LIKE '" + categories.get(i) + "'";
            }

            //Ugly if
            if (categories.contains("Eigene Wörter") || categories.contains("Own Words"))
            {
                query += " UNION SELECT word,'ownwords' AS category, description " +
                        "FROM  userwords " +
                        "INNER JOIN customwords ON customwords.wordID = userwords.wordID " +
                        "WHERE '" + LoginMenu.getCurrentUser(activity).getId() + "' = userID";
            }

        }
        query += " ORDER BY RAND() LIMIT 1;";

        //execute queries.
        this.useCommand(query, false);

        Word result = null;

        try
        {   //add all words to the list
            if (this.res != null && this.res.next())
            {
                result =
                    new Word(this.res.getString(1), this.res.getString(2), this.res.getString(3));
            }
        }
        catch (SQLException e)
        {
            //Logger.write(e, this.activity);
            //e.printStackTrace();
            Logger.logOnlyError(e.getMessage());
        }
        finally
        {
            this.closeConnection();
        }

        return result;
    }

    /**
     * Updating the Value of the lastOnline time in the database
     */
    public void updateLastOnline()
    {
        if (!this.isOnline())
        {
            return;
        }

        //get current time
        GregorianCalendar now = new GregorianCalendar();
        //Creating the time format which is used in database
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String date = df.format(now.getTime());
        //update the Database
        String query = "UPDATE users SET lastOnline = '" + date + "' WHERE username LIKE '" + LoginMenu.getCurrentUser(activity).getName() + "';";

        useCommand(query, true);
    }

    /**
     * Return a List of Categorys (no duplicates)
     * @return List of Categorys
     * @since 0.6
     */
    public ArrayList <String> getCategories()
    {
        if(!this.isOnline())
        {
            return this.od.getCategories();
        }

        ArrayList <String> categories = new ArrayList<>();

        String query = "SELECT DISTINCT category FROM " + TABLE_WORDS;

        query += " UNION SELECT '"+activity.getString(R.string.categoryName_ownWord)+"' " +
                "FROM userwords WHERE userID = '"+LoginMenu.getCurrentUser(activity).getId()+"';";

        this.useCommand(query, false);


        try
        {
            if (this.res != null)
            {
                while (this.res.next())
                {
                    String c = this.res.getString("category");
                    // Add category
                    categories.add(c);
                }
            }
        }
        catch (SQLException e)
        {
            //e.printStackTrace();
            //Logger.write(e, this.activity);
            Logger.logOnlyError(e.getMessage());
        }
        finally
        {
            this.closeConnection();
        }

        return categories;
    }

    /**
     * Gets a user by their name
     * @param name Name of the user.
     * @return A User Object.
     * @throws NullPointerException Exception, if no user was found.
     * @since 0.1
     */
    public User getUser(String name) throws NullPointerException
    {
        //Use offline mode
        if(!this.isOnline())
        {
            if(od == null)
            {
                od = new OfflineDatabase();
            }
            return this.od.getUser(name);

        }

        String query = "SELECT username, password, mail, _id FROM " + TABLE_USERS_NAME +
                " WHERE username LIKE '" + name + "';";


        this.useCommand(query, false);

        try
        {
            if (this.res != null && this.res.next())
            {
                return new User
                (
                    this.res.getString(1), this.res.getString(2),
                    this.res.getString(3), this.res.getInt(4)
                );
            }
            else
            {
                return null;
            }
        }
        catch (SQLException ex)
        {
            //Logger.write(ex, this.activity);
            //ex.printStackTrace();
            Logger.logOnlyError(ex.getMessage());
            return null;
        }
        finally
        {
            this.closeConnection();
        }
    }

    /**
     * Gets the amount of words.
     * @return Integer.
     */
    public int getWordsCount()
    {
        String query = "SELECT COUNT(*) FROM words;";
        this.useCommand(query, false);

        try
        {
            if (this.res != null && this.res.next())
            {
                return this.res.getInt(1);
            }
            else
            {
                return -1;
            }
        }
        catch (SQLException ex)
        {
            //Logger.write(ex, this.activity);
            Logger.logOnlyError(ex.getMessage());
            //ex.printStackTrace();
        }
        finally
        {
            this.closeConnection();
        }

        return -1;
    }


    /**
     * Synch the databases.
     */
    public void synchDatabases()
    {
        Logger.logOnly("Check for updates...");

        if(this.isOnline())
        {
            this.od.copyDatabaseStructureFromSQL();
        }
        else
        {
            Logger.logOnlyWarning("Database synch failed!");
        }
    }

    /**
     * This inner class handles the connection to the offline database <br />
     * It is using SQLite.
     * @since 1.1
     * @author Ludwig
     */
    private class OfflineDatabase extends SQLiteOpenHelper
    {
        /**
         * Version of the database.
         */
        private static final int DATABASE_VERSION       = 87;
        /**
         * Name of the database.
         */
        private static final String DATABASE_NAME       = "database.db";

        /**
         * Creates a new instance of the database handler.
         * @since 0.1
         */
        public OfflineDatabase ()
        {
            super(activity.getBaseContext(), DATABASE_NAME, null, DATABASE_VERSION);
            this.getWritableDatabase();
        }

        /**
         * Deletes all words from the offline Database.
         */
        public void deleteAllWords()
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM words");
            db.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String cmd =
            "CREATE TABLE users"+
            "    ("+
            "            _id          INTEGER,"+
            "            username     VARCHAR(20) PRIMARY KEY,"+
            "            password     VARCHAR(30) NOT NULL,"+
            "            mail         VARCHAR(20) NOT NULL,"+
            "            score        INTEGER DEFAULT '0',"+
            "    wins         INTEGER DEFAULT '0',"+
            "    loses        INTEGER DEFAULT '0',"+
            "    perfects     INTEGER DEFAULT '0',"+
            "    correctLetters INTEGER DEFAULT '0',"+
            "    wrongLetters INTEGER DEFAULT '0'"+
            ");";

            db.execSQL(cmd);

            cmd = "CREATE TABLE  words"+
            "    ("+
            "            word         VARCHAR(60) NOT NULL,"+
            "            category     VARCHAR(18) NOT NULL,"+
            "            description  VARCHAR(255),"+
            "            PRIMARY KEY (word, category)"+
                    "    );";

            db.execSQL(cmd);

            Logger.logOnly("Offline DB created!");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
            this.onCreate(db);
        }

        public void changePW(String newPW)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("UPDATE users SET password = '"+newPW+"';");
            db.close();
        }


        /**
         * Get the amount of words in the offline DB.
         * @return int.
         */
        public int getWordsCount()
        {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM words;", null);

            if(cursor != null)
            {
                try
                {
                    cursor.moveToFirst();
                    return cursor.getInt(0);
                }
                catch (IllegalStateException | CursorIndexOutOfBoundsException ex)
                {
                    return -2;
                }
                finally
                {
                    cursor.close();
                    db.close();
                }
            }
            else
            {
                return -2;
            }
        }


        /**
         * This copies the data to the SQLite database.
         * @since 1.1
         */
        public void copyDatabaseStructureFromSQL()
        {

            Thread t = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    if(od.getWordsCount() != DatabaseManager.this.getWordsCount())
                    {
                        try
                        {
                            Logger.logOnly("Start updating dbs...");
                            od.deleteAllWords();
                        }
                        catch (SQLiteException ex)
                        {
                            ex.printStackTrace();
                            Logger.logOnlyWarning("Update failed!");
                        }
                    }
                    else
                    {
                        Logger.logOnly("All dbs are up to date!");
                        return;
                    }

                    /**
                     * Load all words.
                     */
                    String query = "SELECT * FROM words;";
                    DatabaseManager.this.useCommand(query, false);


                    /**
                     * For threading reasons we need a second ResultSet
                     */
                    DatabaseManager.this.connect();
                    ResultSet res2 = null;

                    if(DatabaseManager.this.isOnline())
                    {
                        try
                        {
                            res2 = statement.executeQuery(query);
                        }
                        catch (SQLException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    try
                    {
                        if (res2 != null)
                        {
                            while (res2.next())
                            {
                                OfflineDatabase.this.addWord
                                (
                                    new Word
                                    (
                                        res2.getString(1),
                                        res2.getString(2),
                                        res2.getString(3)
                                    )
                                );
                            }
                        }
                        Logger.logOnly("Table Words DONE!");
                    }
                    catch (SQLException | NullPointerException e)
                    {
                        e.printStackTrace();
                        Logger.logOnlyError(e.getMessage());
                    }
                    finally
                    {
                        DatabaseManager.this.closeConnection();
                    }

                    try
                    {
                        /**
                         * Load user.
                         */
                        query = "SELECT * FROM users WHERE username LIKE '"+
                                LoginMenu.getCurrentUser(activity).getName()+"';";
                        DatabaseManager.this.useCommand(query, false);

                        if (DatabaseManager.this.res != null)
                        {
                            while (DatabaseManager.this.res.next())
                            {
                                OfflineDatabase.this.addUser
                                (
                                    new User
                                    (
                                        DatabaseManager.this.res.getString(2),
                                        DatabaseManager.this.res.getString(3),
                                        DatabaseManager.this.res.getString(4),
                                        DatabaseManager.this.res.getInt(1)
                                    )
                                );
                            }
                            Logger.logOnly("Table users DONE!");
                        }
                    }
                    catch (SQLException | NullPointerException e)
                    {
                        e.printStackTrace();
                        Logger.logOnlyError(e.getMessage());
                    }
                    finally
                    {
                        DatabaseManager.this.closeConnection();
                    }
                }
            });

            t.start();
        }


        /**
         * Adds a word to the offline db.
         * @since 0.1
         * @param w The word to add.
         */
        public void addWord (Word w)
        {
            try
            {
                SQLiteDatabase db = this.getWritableDatabase();
                ContentValues val = new ContentValues();
                val.put("word", w.getWord());
                val.put("category", w.getCategory());

                if (w.getDescription() != null && w.getDescription().length() > 0)
                {
                    val.put("description", w.getDescription());
                }

                db.insert(TABLE_WORDS, null, val);
                db.close();
            }
            catch(IllegalStateException | SQLiteException ex)
            {
                Logger.logOnlyError(ex.getMessage());
            }

        }


        /**
         * Adds a user to the database.
         * @param u User to add
         * @throws SQLiteException
         * @since 0.1
         */
        public void addUser (User u) throws SQLiteException
        {
            if(this.getUser(u.getName()) == null)
            {
                SQLiteDatabase db = this.getWritableDatabase();

                ContentValues val = new ContentValues();
                val.put("username", u.getName());
                val.put("password", u.getPassword());
                val.put("mail", u.getMail());
                val.put("_id", u.getId());

                db.insert(TABLE_USERS_NAME, null, val);
                db.close();
            }
        }


        /**
         * Gets a user by its name.
         * @param name Name of the user.
         * @return A User Object.
         * @since 0.1
         */
        public User getUser(String name)
        {
            String query = "SELECT username,password,mail,_id FROM " + TABLE_USERS_NAME +
                    " WHERE username LIKE '" + name + "';";
            SQLiteDatabase db = this.getWritableDatabase();

            if (!db.isOpen())
            {
                db = activity.getApplicationContext().openOrCreateDatabase
                        (DATABASE_NAME, SQLiteDatabase.OPEN_READWRITE, null);
            }

            Cursor cursor = db.rawQuery(query, null);

            if(cursor != null)
            {
                try
                {
                    cursor.moveToFirst();
                    return new User(cursor.getString(0),
                            cursor.getString(1), cursor.getString(2), cursor.getInt(3));
                }
                catch (CursorIndexOutOfBoundsException ex)
                {
                    return null;
                }
                finally
                {
                    cursor.close();
                    db.close();
                }
            }
            else
            {
                return null;
            }
        }

        /**
         * Gets a random word from the database.
         * @return String
         * @since 0.7
         */
        public Word getRandomWord()
        {
            List <String> categories = Settings.getCategories();
            String query = "SELECT * FROM " + TABLE_WORDS;

            for (int i = 0; i < categories.size(); i++)
            {
                //overwrite query if there are categories.
                if (i == 0)
                {
                    query = query + " WHERE category LIKE \"" + categories.get(i) + "\"";
                    continue;
                }
                query = query + " OR category LIKE \"" + categories.get(i) + "\"";
            }

            query = query + ";";

            //execute queries.
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            Word result = null;

            //add all words to the list
            if (cursor != null && cursor.moveToFirst())
            {
                int rand = (int)(Math.random() * cursor.getCount());
                cursor.move(rand);
                result = new Word(cursor.getString(0), cursor.getString(1), cursor.getString(2));

                try
                {
                    cursor.close();
                    db.close();
                }
                catch (NullPointerException ex)
                {
                    Logger.logOnly(ex.getMessage());
                }
            }

            return result;
        }

        /**
         * Return a List of Categorys (no duplicates)
         * @return List of Categorys
         * @since 0.6
         */
        public ArrayList <String> getCategories()
        {
            ArrayList <String> categories = new ArrayList<>();

            String query = "SELECT DISTINCT category FROM " + TABLE_WORDS + ";";

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst())
            {
                do
                {
                    String c = cursor.getString(0);
                    // Add category
                    categories.add(c);
                }
                while (cursor.moveToNext());

                try
                {
                    cursor.close();
                    db.close();
                }
                catch (NullPointerException ex)
                {
                    Logger.logOnly(ex.getMessage());
                }
            }
            return categories;
        }
    }
}