package teamfmg.hangman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the Database connection with SQLite.<br />
 * Created by Ludwig 09.11.2015.
 * @since 0.1
 */
public class OfflineMode extends SQLiteOpenHelper
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
    private Context context;
    /**
     * Attribute to choose statistics
     */
    public enum Attribute {SCORE, WINS, LOSES, PERFECTS, CORRECTLETTER, WRONGLETTER}



    /**
     * Creates a new instance of the database handler.
     * @param context Context class.
     * @since 0.1
     */
    public OfflineMode(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;

        //Test user TODO: Remove
        if(this.getUser("Admin") == null)
        {
            this.addUser(new User("Admin",Caeser.encrypt("a",Settings.encryptOffset),
                    "admin@hangman.com",-1));
        }
    }

    /**
     * Loads all words from the *.csv to the data base.
     * @param db Database to load.
     * @since 0.5
     */
    public void loadWords(SQLiteDatabase db)
    {

        Logger.logOnly(R.string.hint_loading);

        //Get all files in raw directory
        Field[] fields = R.raw.class.getFields();

        for (Field field : fields)
        {
            int id = this.context.getResources().getIdentifier(
                    field.getName(), "raw", this.context.getPackageName());

            InputStream in = this.context.getResources().openRawResource(id);

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

                    db.execSQL(createTableStatement);
                }

                catch (IOException ex)
                {
                    Logger.logOnly(ex.getMessage());
                }
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Create user table
        String createTableStatement =
                "CREATE TABLE " + TABLE_USERS_NAME + " ( " +
                        "_id          INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username     VARCHAR(20), " +
                        "password     VARCHAR(30) NOT NULL, " +
                        "mail         VARCHAR(20) NOT NULL, " +
                        "score        INTEGER DEFAULT '0'," +
                        "wins         INTEGER DEFAULT '0'," +
                        "loses        INTEGER DEFAULT '0'," +
                        "perfects     INTEGER DEFAULT '0'," +
                        "correctLetters INTEGER DEFAULT '0'," +
                        "wrongLetters INTEGER DEFAULT '0')";
        db.execSQL(createTableStatement);

        try
        {
            //Create words table
            createTableStatement =
                    "CREATE TABLE " + TABLE_WORDS +
                            " (" +
                            "word         VARCHAR NOT NULL, " +
                            "category     VARCHAR NOT NULL," +
                            "description  VARCHAR," +
                            "PRIMARY KEY (word, category)"+
                            "); ";
            db.execSQL(createTableStatement);
            this.loadWords(db);
        }
        catch (SQLiteException ex)
        {
            Logger.logOnlyError(ex.getMessage());
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        this.onCreate(db);

    }

    @Deprecated
    /**
     * Deletes the table user
     * @since 0.1
     */
    public void deleteAll()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String statement = "DROP TABLE IF EXISTS" + TABLE_USERS_NAME+";";
        db.execSQL(statement);
        db.close();
    }

    /**
     * Adds a user to the database.
     * @param u User to add
     * @throws SQLiteException
     * @since 0.1
     */
    public void addUser (User u) throws SQLiteException
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put("username",u.getName());
        val.put("password", u.getPassword());
        val.put("mail", u.getMail());

        db.insert(TABLE_USERS_NAME, null, val);
        db.close();
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
     * Adds an value to the current value in the Database
     * @param attribut The type of Statistic
     * @param amount Value which will be added
     */
    public void raiseScore (Attribute attribut, int amount){

        String attributName = getAttributName(attribut);

        useCommand("UPDATE " + TABLE_USERS_NAME + " SET " + attributName + " = " + attributName + " + "
                + amount + " WHERE username LIKE '" + LoginMenu.getCurrentUser().getName() + "';");

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

        //Curser
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null)
        {
            try
            {
                cursor.moveToFirst();
                return cursor.getInt(0);
            }
            catch (CursorIndexOutOfBoundsException ex)
            {
                return 0;
            }
            finally
            {
                cursor.close();
                db.close();
            }
        }
        else
        {
            throw new NullPointerException();
        }
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
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues val = new ContentValues();
            val.put("word", w.getWord());
            val.put("category", w.getCategory());

            if (w.getDescription().length() != 0)
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


    public void remove (Word word)
    {
        String query = "DELETE FROM " + TABLE_WORDS + " WHERE word LIKE \"" + word.getWord() +
                "\" AND category LIKE \"" + word.getCategory() + "\";";
        try
        {
            this.useCommand(query);
        }
        catch (SQLiteException ex)
        {
            Logger.logOnlyError(ex.getMessage());
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
        boolean b;

        //Bsp: SELECT * FROM words WHERE word LIKE "test" AND category LIKE "testCategory";
        String query = "SELECT * FROM " + TABLE_WORDS + " WHERE word LIKE \"" + word.getWord() +
                "\" AND category LIKE \"" + word.getCategory() + "\";";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        //a word exists if we found something
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            b = true;
        }
        else
        {
            b = false;
        }

        try
        {
            cursor.close();
            db.close();
        }
        catch (NullPointerException ex)
        {
            Logger.logOnly(ex.getMessage());
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

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst())
        {
            do
            {
                User u = new User(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4));
                // Add user
                users.add(u);
            }
            while (cursor.moveToNext());
            db.close();
        }
        try
        {
            cursor.close();
            db.close();
        }
        catch (NullPointerException ex)
        {
            Logger.logOnly(ex.getMessage());
        }
        return users;
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

        //execute queries.
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        //add all words to the list
        if (cursor != null && cursor.moveToFirst())
        {
            do
            {
                Word w = new Word
                        (
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2)
                        );
                // Add word
                words.add(w);
            }
            while (cursor.moveToNext());

            db.close();
        }
        try
        {
            cursor.close();
            db.close();
        }
        catch (NullPointerException ex)
        {
            Logger.logOnly(ex.getMessage());
        }
        return words;

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
            db.close();
        }
        try
        {
            cursor.close();
            db.close();
        }
        catch (NullPointerException ex)
        {
            Logger.logOnly(ex.getMessage());
        }
        return result;
    }

    /**
     * Gets all words from the database. <br/>
     * If no category was saved, all word are used.
     * @return List of Words depending on the categories in the options menu.
     * @deprecated
     * @since 0.5
     */
    public ArrayList <String> getWords()
    {
        List <String> categories = Settings.getCategories();
        ArrayList <String> words = new ArrayList<>();

        //Take all (default)
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

        //add all words to the list
        if (cursor != null && cursor.moveToFirst())
        {
            do
            {
                String w = cursor.getString(0);
                // Add word
                words.add(w);
            }
            while (cursor.moveToNext());

            db.close();
        }

        try
        {
            cursor.close();
            db.close();
        }
        catch (NullPointerException ex)
        {
            Logger.logOnly(ex.getMessage());
        }

        return words;
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

            db.close();
        }
        try
        {
            cursor.close();
            db.close();
        }
        catch (NullPointerException ex)
        {
            Logger.logOnly(ex.getMessage());
        }
        return categories;
    }

    /**
     * Gets a user by its name.
     * @param name Name of the user.
     * @return A User Object.
     * @throws NullPointerException Exception, if no user was found.
     * @since 0.1
     */
    public User getUser(String name) throws NullPointerException
    {
        String query = "SELECT * FROM " + TABLE_USERS_NAME +
                " WHERE username LIKE '" + name + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null)
        {
            try
            {
                cursor.moveToFirst();
                return new User(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getInt(4));
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
            throw new NullPointerException();
        }
    }

    @Override
    public void close()
    {
        //this.getWritableDatabase().close();
    }
}