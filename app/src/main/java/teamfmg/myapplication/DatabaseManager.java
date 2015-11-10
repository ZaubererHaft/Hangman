package teamfmg.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles the Database connection with SQLite.<br />
 * Created by Ludwig 09.11.2015.
 * @since 0.1
 */
public class DatabaseManager extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION       = 5;
    private static final String DATABASE_NAME       = "database.db";
    private static final String TABLE_USERS_NAME    = "users";

    /**
     * Creates a new instance of the database handler.
     * @param context Context class.
     * @since 0.1
     */
    public DatabaseManager(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //Create user table
        String createTableStatement =
                "CREATE TABLE " + TABLE_USERS_NAME + " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username VARCHAR(20) NOT NULL, " +
                        "password     VARCHAR(30) NOT NULL, " +
                        "mail VARCHAR(20) NOT NULL);";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_USERS_NAME);
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
