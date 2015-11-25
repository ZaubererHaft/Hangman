package teamfmg.hangman;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Switch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
public class DatabaseManager extends SQLiteOpenHelper
{

    /**
     * Version of the database.
     */
    private static final int DATABASE_VERSION       = 16;
    /**
     * Name of the database
     */
    private static final String DATABASE_NAME       = "database.db";
    /**
     * Name of the table users.
     */
    private static final String TABLE_USERS_NAME    = "users";
    /**
     * Name of the table words
     */
    private static final String TABLE_WORDS    = "words";

    private Context c;
    /**
     * Creates a new instance of the database handler.
     * @param context Context class.
     * @since 0.1
     */
    public DatabaseManager(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        c = context;

        if(this.getUser("Admin") == null)
        {
            this.addUser(new User("Admin",Caeser.encrypt("a",Settings.encryptOffset),
                    "admin@hangman.com"));
        }
    }

    //TODO: comment and redesign
    public void loadWords(SQLiteDatabase db)
    {

        Field[] fields=R.raw.class.getFields();
        Logger.logOnlyError(fields[0].getName());

        for (Field field : fields) {

            int id = c.getResources().getIdentifier(field.getName(), "raw", c.getPackageName());
            InputStream in = c.getResources().openRawResource(id);

            BufferedReader buffer = new BufferedReader(new InputStreamReader(in));

            while (true) {

                try {

                    String line = buffer.readLine();
                    String[] list = line.split(";");

                    String createTableStatement =
                            "INSERT INTO " + TABLE_WORDS + " (word,category,description) " +
                                    " VALUES(\"" + list[0] + "\", \"" + list[1] + "\",\"" + list[2] + "\");";
                    db.execSQL(createTableStatement);
                } catch (Exception e) {
                    Logger.logOnly("Database loaded!");
                    break;
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
                        "username     VARCHAR(20) NOT NULL, " +
                        "password     VARCHAR(30) NOT NULL, " +
                        "mail         VARCHAR(20) NOT NULL);";
        db.execSQL(createTableStatement);

        try
        {
            //Create words table
            createTableStatement =
                    "CREATE TABLE " + TABLE_WORDS + " (" +
                            "word         VARCHAR PRIMARY KEY, " +
                            "category     VARCHAR NOT NULL," +
                            "description  VARCHAR); ";
            db.execSQL(createTableStatement);
            this.loadWords(db);
        }
        catch (SQLiteException ex)
        {
            Logger.logOnlyError(ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_WORDS);
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
        val.put("mail",u.getMail());

        db.insert(TABLE_USERS_NAME, null, val);
        db.close();
    }

    /**
     * Add a word to the database
     * @param w Word to add
     */
    public void addWord (Word w){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put("word", w.getWord());
        val.put("category", w.getCategory());

        db.insert(TABLE_WORDS,null,val);
        db.close();
    }

    /**
     * Gets all registered users
     * @return List of users.
     * @since 0.1
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
                User u = new User(cursor.getString(1),cursor.getString(2),cursor.getString(3));
                // Add user
                users.add(u);
            }
            while (cursor.moveToNext());

            cursor.close();
        }
        return users;
    }

    /**
     * Return a List of Words
     * @param categorys Categorys witch get selected
     * @return List of Words
     */
    public ArrayList <String> getWords(List <String> categorys)
    {
        ArrayList <String> words = new ArrayList<>();

        String query = "SELECT  * FROM " + TABLE_WORDS;

        for (int i = 0; i < categorys.size(); i++){
            if (i == 0) {
                query = query + " WHERE category LIKE \"" + categorys.get(i) + "\"";
                continue;
            }
            query = query + " OR category LIKE \"" + categorys.get(i) + "\"";
        }

        query = query + ";";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst())
        {
            do
            {
                String w = cursor.getString(0);
                // Add word
                words.add(w);
            }
            while (cursor.moveToNext());

            cursor.close();
        }
        return words;
    }

    /**
     * Return a List of Categorys (no duplicates)
     * @return List of Categorys
     */
    public ArrayList <String> getCategorys()
    {
        ArrayList <String> categorys = new ArrayList<>();

        String query = "SELECT  DISTINCT category FROM " + TABLE_WORDS + ";";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst())
        {
            do
            {
                String c = cursor.getString(0);
                // Add category
                categorys.add(c);
            }
            while (cursor.moveToNext());

            cursor.close();
        }
        return categorys;
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
        String query = "SELECT  * FROM " + TABLE_USERS_NAME +
                " WHERE username LIKE '" + name + "';";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor != null)
        {
            try
            {
                cursor.moveToFirst();
                return new User(cursor.getString(1), cursor.getString(2), cursor.getString(3));
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
