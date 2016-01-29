package teamfmg.hangman;

import android.app.Activity;
import android.database.sqlite.SQLiteException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
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
public class DatabaseManager
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
     * Context representing the activity.
     */
    private Activity activity;
    /**
     * Attribute to choose statistics
     */
    public enum Attribute {SCORE, WINS, LOSES, PERFECTS, CORRECTLETTER, WRONGLETTER}

    private Connection connection = null;
    private Statement statement = null;
    private ResultSet res = null;

    private static DatabaseManager manager;


    public static DatabaseManager getInstance()
    {
        if(manager == null)
        {
            manager = new DatabaseManager();
        }
        return manager;
    }

    public void setActivity(Activity a)
    {
        this.activity = a;
    }

    private void connect()
    {
        if(connection == null)
        {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection
                        ("jdbc:mysql://www.db4free.net:3306/" + DATABASE_NAME + "?useSSL=false&user=zauberhaft&password=asdfg-01");

            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.write(ex, this.activity);
            }
        }
    }

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


            this.statement = this.connection.createStatement();

            if(manipulative)
            {
                this.statement.executeUpdate(command);
            }
            else
            {
                this.res =  this.statement.executeQuery(command);
            }

        }
        catch (SQLException e)
        {
            Logger.write(e,this.activity);
            e.printStackTrace();
        }
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
    public void addWord (Word w)
    {
        try
        {
            String cmd = "INSERT INTO "+ TABLE_WORDS + " VALUES ("+
                    w.getWord() + ","+
                    w.getCategory() + ",";


            if (w.getDescription().length() != 0)
            {
                cmd += "," + w.getDescription();
            }

            cmd += ");";

            this.useCommand(cmd, true);
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
    }


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
    public boolean exists(Word word)
    {
        boolean b = false;

        //Bsp: SELECT * FROM words WHERE word LIKE "test" AND category LIKE "testCategory";
        String query = "SELECT * FROM " + TABLE_WORDS + " WHERE word LIKE \"" + word.getWord() +
                "\" AND category LIKE \"" + word.getCategory() + "\";";
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
                    User u = new User(this.res.getString(1),this.res.getString(2),this.res.getString(3));
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
        String query = "SELECT * FROM " + TABLE_WORDS;

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

        String query = "SELECT DISTINCT category FROM " + TABLE_WORDS + ";";

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
                return new User(
                        this.res.getString(2), this.res.getString(3), this.res.getString(4));
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

}