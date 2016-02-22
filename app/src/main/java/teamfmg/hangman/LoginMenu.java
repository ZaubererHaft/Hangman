package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * The Login menu for Hangman.<br />
 * Created by Ludwig 09.11.2015.
 * @since 0.1
 */
public class LoginMenu extends Activity implements View.OnClickListener, IApplyableSettings
{
    /**
     * Text fields.
     */
    private EditText username, password;

    private static User currentUser;
    private DatabaseManager db = DatabaseManager.getInstance();
    private boolean compareOffline;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        this.db.setActivity(this);

        (this.findViewById(R.id.button_login)).setOnClickListener(this);
        (this.findViewById(R.id.button_register)).setOnClickListener(this);
        (this.findViewById(R.id.button_exit)).setOnClickListener(this);

        if(!this.db.isOnline())
        {
            (this.findViewById(R.id.button_register)).setEnabled(false);
        }


        this.username   = (EditText)this.findViewById(R.id.textField_welcome_username);
        this.password   = (EditText)this.findViewById(R.id.textField_welcome_password);

        this.username.setOnClickListener(this);
        this.password.setOnClickListener(this);

        this.changeBackground();
        this.writeUserData();

        Updater u = new Updater(this);

        if(this.db.isOnline() && u.updatePossible())
        {
            Logger.messageDialog
            (
                this.getString(R.string.update_info_newVersion),
                this.getString(R.string.update_info_newVersion_nowTo),
                this
            );
        }
        this.password.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                password.setText("");
                compareOffline = false;
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(!this.db.isOnline())
        {
            (this.findViewById(R.id.button_register)).setEnabled(false);
        }

    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        final int offset = 50;

        //exit button
        if(id == R.id.button_exit)
        {
            System.exit(0);
        }
        //register button
        else if(id == R.id.button_register)
        {
            if(this.db.isOnline())
            {
                //start register menu and closes this app
                this.startActivity(new Intent(this, RegisterMenu.class));
            }
        }

        //login Button
        else if(id == R.id.button_login)
        {
            //gets the entered data
            String enteredName = this.username.getText().toString();

            //encrypts the entered password to compare it with the password in the db
            String enteredPassword = Caeser.encrypt
            (
                this.password.getText().toString()
            );

            try
            {
                //get user
                User user = db.getUser(enteredName);

                //no user found...
                if(user == null)
                {
                    //online mode
                    if(db.isOnline())
                    {

                        Logger.write(this.getResources().getString(R.string.error_login_wrongUsername)
                                , this, offset);

                        //do not execute the following statements
                        return;
                    }
                    //offline mode
                    else
                    {
                        Logger.write(this.getResources().getString(R.string.error_login_offlineNoUser)
                                , this, offset);
                        //do not execute the following statements
                        return;
                    }

                }

                if (this.compareOffline)
                {
                    enteredPassword = Settings.getLastPassword();
                }

                //compare passwords if there was a user...
                if(user.getPassword().equals(enteredPassword))
                {
                    CheckBox c = (CheckBox)this.findViewById(R.id.login_saveLoginData);
                    Settings.setCurrentUser(user);

                    //save login data
                    if(c.isChecked())
                    {
                        Settings.save(this);
                    }

                    //adds a user to the offline db
                    this.db.addOfflineUser(user);

                    //sets the current user
                    setCurrentUser(user);
                    Settings.saveCurrentUser(this, user);

                    //Updating the lastOnline Time
                    this.db.updateLastOnline();

                    //load categories
                    ArrayList<String>  list = db.getCategories();
                    Settings.setCategories(list);

                    db.synchDatabases();

                    //Open Main Menu
                    Logger.write(this.getResources().getString(R.string.info_login_succeed)
                            , this, offset);

                     //closing menu
                    this.finish();
                    this.startActivity(new Intent(this, MainMenu.class));
                }
                //Password wrong
                else
                {
                    Logger.write(this.getResources().getString(R.string.error_login_wrongPassword)
                            ,this,offset);
                }
            }
            //Error with the Database
            catch (SQLiteException ex)
            {
                Logger.write(this.getResources().getString(R.string.error_general_sqlite),
                        this, offset);

            }
        }
    }

    public static User getCurrentUser(Activity a)
    {
        //try reloading the user from disc...
        if(LoginMenu.currentUser == null)
        {
            currentUser = Settings.loadCurrentUser(a);

            DatabaseManager db = DatabaseManager.getInstance();
            db.setActivity(a);

            currentUser = db.getUser(currentUser.getName());
        }

        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        LoginMenu.currentUser = currentUser;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }

    private void writeUserData()
    {
        try
        {
            this.username.setText(Settings.getLastUsername());
            String text = Settings.getLastPassword();

            if (text != null && text.length() > 0)
            {
                this.compareOffline = true;
                this.password.setText("DUMMYBOY");
            }
        }
        catch (NullPointerException ex)
        {
            Logger.logOnly("No userdata entered yet!");
        }

    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        Settings.setColor((RelativeLayout) this.findViewById(R.id.relLayout_login));
    }
}