package teamfmg.hangman;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * <u><h1>Class to handle database connections.</h1></u><br />
 * Using <b>JDBC</b>, this class has implement all SQL statements directly in the code as well as data to
 * connect.<br />
 * As using {@link ResultSet}, this class is designed as a Singleton using getInstance().
 * <br />
 * Be aware to set setInstance() to set the right activity context.<br /><br />
 * Created by Ludwig on 1/27/16.
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
    private static final long CONNECTING_TIME = 1000;
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
    public enum Attribute {SCORE, WINS, LOSES, PERFECTS, CORRECT_LETTER, WRONG_LETTER}
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

        if (od != null)
        {
            //TODO: Nur am anfang
            this.od.synchDatabases();
        }

        while (!isInterrupted())
        {
            if (od == null)
            {
                this.od = new OfflineDatabase();
            }

            if(!this.isOnline())
            {
                Logger.logOnlyError("Connection lost - try reconnection!");

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
        if(!this.isAlive())
        {
            this.start();
        }
        this.activity = a;
    }

    /**
     * This connects to the online database.
     * @since 1.1
     */
    private void connect()
    {
        if(connection == null)
        {
            try
            {
                if(this.isOnline())
                {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection
                            ("jdbc:mysql://" + CONNECTING_URL + "/" + DATABASE_NAME
                                    + "?useSSL=false&user=external&password=asdfg-01");
                    Logger.logOnly("Connected!");
                }
                else
                {
                    Logger.logOnly("No internet connection!");
                }

            }
            catch (SQLException ex)
            {
                Logger.logOnlyError("Failed to communicate with server - "+ex.getMessage());
            }
            catch (ClassNotFoundException ex){
                Logger.logOnlyError("Error loading class - " + ex.getMessage());
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
     * @param wordname word to description.
     * @since 1.1
     */
    public void deleteCustomWord(String wordname)
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
     * Executes a command to the database and closes it automatically
     * @param command {@link String}
     * @since 1.1
     */
    public void useCommand (String command, boolean manipulative)
    {
        try
        {
            this.connect();

            if(this.isOnline())
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
            Logger.write(e,this.activity);
            e.printStackTrace();
        }
    }

    /**
     * Changes the password.
     * @param newPW The new password.
     * @since 1.1
     */
    public void changePassword(String newPW)
    {
        this.useCommand("UPDATE users SET password = " +
                Caeser.encrypt(newPW, Settings.encryptOffset) + " WHERE username = '" +
                LoginMenu.getCurrentUser().getName() + "';", true);
    }

    /**
     * Adds an value to the current value in the Database
     * @param attribut The type of Statistic
     * @param amount Value which will be added
     * @since 1.1
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
     * @since 1.1
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
     * Gets a random word from the database.
     * @return String
     * @since 0.7
     */
    public Word getRandomWord()
    {

        if(!this.isOnline())
        {
            return this.od.getRandomWord();
        }

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
            Logger.write(e, this.activity);
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
        if(!this.isOnline())
        {
            return this.od.getCategories();
        }

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
            Logger.write(e, this.activity);
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
            Logger.write(ex,this.activity);
            ex.printStackTrace();
            return null;
        }
        finally
        {
            this.closeConnection();
        }
    }

    /**
     * This inner class handles the connection to the offline database <br />
     * It is using SQLite.
     */
    class OfflineDatabase extends SQLiteOpenHelper
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

        public void synchDatabases()
        {

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

            this.copyDatabaseStructureFromSQL();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
            this.onCreate(db);
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

                    Logger.logOnly("Start copying dbs...");

                    /**
                     * Load all words.
                     */
                    String query = "SELECT * FROM words;";
                    DatabaseManager.this.useCommand(query, false);

                    try
                    {
                        if (DatabaseManager.this.res != null)
                        {
                            while (DatabaseManager.this.res.next())
                            {
                                OfflineDatabase.this.addWord
                                (
                                    new Word
                                    (
                                        DatabaseManager.this.res.getString(1),
                                        DatabaseManager.this.res.getString(2),
                                        DatabaseManager.this.res.getString(3)
                                    )
                                );
                            }
                        }
                        Logger.logOnly("Table Words DONE!");
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                        Logger.logOnlyError(e.getMessage());
                    }
                    finally
                    {
                        DatabaseManager.this.closeConnection();
                    }

                    if(LoginMenu.getCurrentUser() == null)
                    {
                        return;
                    }

                    /**
                     * Load user.
                     */
                    query = "SELECT * FROM users WHERE username LIKE '"+
                            LoginMenu.getCurrentUser().getName()+"';";
                    DatabaseManager.this.useCommand(query, false);

                    try
                    {
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
                    catch (SQLException e)
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
            catch(SQLiteException ex)
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