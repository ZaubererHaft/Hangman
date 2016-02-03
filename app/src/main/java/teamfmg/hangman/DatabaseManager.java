package teamfmg.hangman;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Created by Ludwig on 1/27/16.
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
    private static final String CONNCECTING_URL = "h2530840.stratoserver.net";
    /**
     * Time to connect to the server.
     */
    private static final long CONNECTTING_TIME = 1000;
    /**
     * This handles the database connection.
     */
    private static DatabaseManager manager;
    /**
     * Context representing the activity.
     */
    private Activity activity;
    /**
     * Attribute to choose statistics
     */
    public enum Attribute {SCORE, WINS, LOSES, PERFECTS, CORRECTLETTER, WRONGLETTER}
    /**
     * The connection. Represents the status to the online DB
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
     * Default constructor to avoid instances.
     */
    private DatabaseManager()
    {
        this.start();
    }

    @Override
    public void run()
    {
        Looper.prepare();

        while (!isInterrupted())
        {

            if (!this.isOnline())
            {
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
                Logger.logOnly("Connection lost... Try reconnecting...");

                this.connect();
            }
            try
            {
                Thread.sleep(CONNECTTING_TIME);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    //TODO: implement
    public String getOnlineStatus()
    {
        return null;
    }

    /**
     * Checks the online connection.
     * @return true
     */
    public boolean isOnline()
    {
        ConnectivityManager cm =
                (ConnectivityManager) this.activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

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
     */
    public void setActivity(Activity a)
    {
        this.activity = a;
    }

    /**
     * This connects to the database.
     */
    private void connect()
    {
        if(connection == null)
        {
            try
            {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection
                        ("jdbc:mysql://" +CONNCECTING_URL+ "/" + DATABASE_NAME
                                + "?useSSL=false&user=external&password=asdfg-01");
                Logger.logOnly("Connected!");

            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Logger.write(ex, this.activity);
            }
        }
    }

    /**
     * This closes the connection
     */
    private void closeConnection()
    {
        try
        {

            if (this.statement != null)
            {
                this.statement.close();
            }
            //if (this.connection != null)
            //{
             //   this.connection.close();
            //}
            if(this.res != null)
            {
                this.res.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Logger.write(e,this.activity);
        }
    }

    /**
     * Deletes a word from the user.
     * @param u user.
     * @param wordname word to description.
     */
    public void deleteCustomWord(User u, String wordname)
    {
        String query = "DELETE FROM userwords WHERE " +
            "(SELECT wordID FROM customwords WHERE word LIKE '" + wordname + "') LIKE wordID " +
            "AND " +LoginMenu.getCurrentUser().getId()+" LIKE userID;" ;


        this.useCommand(query, true);

    }

    /**
     * Gets a custom word identified by a user.
     * @param u User.
     * @return List of words.
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
            e.printStackTrace();
            Logger.write(e, this.activity);
        }
        finally
        {
            this.closeConnection();
        }

        return words;
    }

    /**
     * Gets all words of a category.
     * @param category Category to get the words.
     * @return {@link ArrayList}
     * @since 0.7
     */
    public ArrayList<Word> getWordsOfCategory(String category)
    {
        String query = "SELECT * FROM " + TABLE_WORDS + " WHERE category LIKE \""+category+"\";";
        ArrayList<Word> words = new ArrayList<>();

        this.useCommand(query, false);

        try
        {        //add all words to the list
            if (this.res != null)
            {
                while (this.res.next())
                {
                    Word w = new Word
                    (
                        this.res.getString(0),
                        this.res.getString(1),
                        this.res.getString(2)
                    );
                    // Add word
                    words.add(w);
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Logger.write(e, this.activity);
        }
        finally
        {
            this.closeConnection();
        }

        return words;

    }
    /**
     * Loads all words from the *.csv to the data base.
     * @since 0.5
     */
    public void loadWords()
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
                    Logger.write(ex, this.activity);
                }
                finally
                {
                    this.closeConnection();
                }
            }
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

        String cmd = "INSERT INTO "+ TABLE_USERS_NAME + " VALUES ( DEFAULT,'"+
                u.getName() + "','"+
                u.getPassword() + "','"+
                u.getMail()+"',0,0,0,0,0,0);";

        this.useCommand(cmd, true);
    }

    /**
     * Executes a command to the database and closes ot automatically
     * @param command {@link String}
     */
    public void useCommand (String command, boolean manipulative)
    {
        try
        {
            this.connect();

            if(this.isOnline())
            {
                this.statement = this.connection.createStatement();

                if (manipulative) {
                    this.statement.executeUpdate(command);
                } else {
                    this.res = this.statement.executeQuery(command);
                }
            }

        }
        catch (SQLException e)
        {
            Logger.write(e,this.activity);
            e.printStackTrace();
        }
    }

    /**
     * Changes the password.
     * @param newPW The new password.
     */
    public void changePassword(String newPW)
    {
        this.useCommand("UPDATE users SET password = "+
                Caeser.encrypt(newPW,Settings.encryptOffset)+" WHERE username = '"+
                LoginMenu.getCurrentUser().getName()+"';",true);
    }

    /**
     * Adds an value to the current value in the Database
     * @param attribut The type of Statistic
     * @param amount Value which will be added
     */
    public void raiseScore (Attribute attribut, int amount){

        String attributName = getAttributName(attribut);

        String cmd = "UPDATE " + TABLE_USERS_NAME + " SET " + attributName + " = " + attributName + " + "
                + amount + " WHERE username LIKE '" + LoginMenu.getCurrentUser().getName() + "';";

        useCommand(cmd, true);

    }

    /**
     * Creates an Select for the DB for the Statistics
     * @param attribut The type of Statistic
     * @return Value of the attribut for the current User
     */
    public int getCurrentStatistic (Attribute attribut)
    {
        //convert attribut for the using in the DB
        String attributName = getAttributName(attribut);

        //DB Command for get the wish-value
        String query = "SELECT " + attributName + " FROM " + TABLE_USERS_NAME + " WHERE username LIKE '"
                + LoginMenu.getCurrentUser().getName() + "';";

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
            Logger.write(ex,this.activity);
            ex.printStackTrace();
        }
        finally
        {
            this.closeConnection();
        }
        return 0;
    }

    /**
     * Convert the Enum to an String. This String is the correct name of the Attribut in the Database
     * @param attribut Attribut which will get converted
     * @return correct name of the Attribut in the Database
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
            case CORRECTLETTER:
                attributName = "correctLetters";
                break;
            case WRONGLETTER:
                attributName = "wrongLetters";
                break;
            case PERFECTS:
                attributName = "perfects";
                break;
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
                    LoginMenu.getCurrentUser().getId()+"'),'"+w.getDescription()+"');";

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
            ex.printStackTrace();
            Logger.write(ex, this.activity);
        }
        finally
        {
            this.closeConnection();
        }

        return false;
    }


    /**
     * Removes a word from the database.
     * @param word Wprd to remove
     */
    public void remove (Word word)
    {
        String query = "DELETE FROM " + TABLE_WORDS + " WHERE word LIKE \"" + word.getWord() +
                "\" AND category LIKE \"" + word.getCategory() + "\";";
        try
        {
            this.useCommand(query, true);
        }
        catch (SQLiteException ex)
        {
            ex.printStackTrace();
            Logger.write(ex, this.activity);
        }
        finally
        {
            this.closeConnection();
        }
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
                "AND "+LoginMenu.getCurrentUser().getId()+" LIKE userID;" ;

        this.useCommand(query, false);

        //a word exists if we found something
        try
        {
            b = this.res.next();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Logger.write(e, this.activity);
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
            e.printStackTrace();
            Logger.write(e, this.activity);
        }
        finally
        {
            this.closeConnection();
        }
        return b;
    }

    /**
     * Gets all registered users
     * @return List of users.
     * @since 0.1i
     * @deprecated
     */
    public List<User> getUsers()
    {
        List<User> users = new LinkedList<>();

        String query = "SELECT  * FROM " + TABLE_USERS_NAME;

        this.useCommand(query, false);

        try
        {
            if (this.res != null)
            {

                while (this.res.next())
                {
                    User u = new User(this.res.getString(1),this.res.getString(2),this.res.getString(3),this.res.getInt(4));
                    // Add user
                    users.add(u);
                }
            }
        }
        catch (SQLException e)
        {
            Logger.write(e,this.activity);
            e.printStackTrace();
        }
        finally
        {
            this.closeConnection();
        }

        return users;
    }


    /**
     * Gets a random word from the database.
     * @return String
     * @since 0.7
     */
    public Word getRandomWord()
    {

        List <String> categories = Settings.getCategories();
        String query = "SELECT word, category, description FROM " + TABLE_WORDS;

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


        query += " UNION SELECT word,'ownwords' AS category, description "+
        "FROM  userwords "+
        "INNER JOIN customwords ON customwords.wordID = userwords.wordID "+
        "WHERE '" + LoginMenu.getCurrentUser().getId()+ "' = userID " +
                "ORDER BY RAND() LIMIT 1;";

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
            Logger.write(e,this.activity);
            e.printStackTrace();
        }
        finally
        {
            this.closeConnection();
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

        String query = "SELECT DISTINCT category FROM " + TABLE_WORDS;

        query += " UNION SELECT '"+activity.getString(R.string.categoryName_ownWord)+"' " +
                "FROM userwords WHERE userID = '"+LoginMenu.getCurrentUser().getId()+"';";

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
            e.printStackTrace();
            Logger.write(e,this.activity);
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
        String query = "SELECT * FROM " + TABLE_USERS_NAME +
                " WHERE username LIKE '" + name + "';";


        this.useCommand(query, false);

        try
        {
            if (this.res != null && this.res.next())
            {
                return new User
                (
                    this.res.getString(2), this.res.getString(3),
                    this.res.getString(4), this.res.getInt(1)
                );
            }
            else
            {
                return null;
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
    }

    class OfflineDatabase extends SQLiteOpenHelper
    {
        /**
         * Version of the database.
         */
        private static final int DATABASE_VERSION       = 80;
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
            super(activity, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            String cmd =
            "CREATE TABLE users"+
            "    ("+
            "            _id          INTEGER PRIMARY KEY AUTO_INCREMENT,"+
            "            username     VARCHAR(20),"+
            "            password     VARCHAR(30) NOT NULL,"+
            "            mail         VARCHAR(20) NOT NULL,"+
            "            score        INTEGER DEFAULT '0',"+
            "    wins         INTEGER DEFAULT '0',"+
            "    loses        INTEGER DEFAULT '0',"+
            "    perfects     INTEGER DEFAULT '0',"+
            "    correctLetters INTEGER DEFAULT '0',"+
            "    wrongLetters INTEGER DEFAULT '0'"+
            ");";

            this.useCommand(cmd);

            cmd = "CREATE TABLE  words"+
            "    ("+
            "            word         VARCHAR(60) NOT NULL,"+
            "            category     VARCHAR(18) NOT NULL,"+
            "            description  VARCHAR(255),"+
            "            PRIMARY KEY (word, category)"+
            "    );";

            this.useCommand(cmd);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            this.useCommand("DROP TABLE IF EXISTS " + TABLE_USERS_NAME);
            this.useCommand("DROP TABLE IF EXISTS " + TABLE_WORDS);
            this.onCreate(db);
        }

        /**
         * Executes a vommand to the database.
         * @param command {@link String}
         */
        public void useCommand (String command)
        {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(command);
            db.close();
        }

        /**
         * This copies the data to the SQLitedatabase.
         */
        public void copyDatabaseStructureFromSQL()
        {
            Thread t = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Document doc = Jsoup.connect(CONNCECTING_URL+"/files").get();
                        Elements links = doc.getElementsByTag("a");

                        for (Element link : links)
                        {
                            if(link.toString().endsWith(".csv"))
                            {

                                URL url = new URL(CONNCECTING_URL+"/files/"+link.attr("href"));

                                String cmd = ".separator \",\"" +
                                    ".mode csv" +
                                    ".import "+url+" users";

                                useCommand(cmd);
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                    }
                }
            });

            t.start();
        }

    }

}