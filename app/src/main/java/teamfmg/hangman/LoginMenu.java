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


    //DONE: (1) Fix max Length of the text fields
    //DONE: (2) Only allow letters and numbers in username
    //DONE: (3) Continue Button Needs to work
    //DONE: (4) implement color chosen from settings

    /**
     * Text fields.
     */
    private EditText username, password;

    private static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        (this.findViewById(R.id.button_login)).setOnClickListener(this);
        (this.findViewById(R.id.button_register)).setOnClickListener(this);
        (this.findViewById(R.id.button_exit)).setOnClickListener(this);

        this.username   = (EditText)this.findViewById(R.id.textField_welcome_username);
        this.password   = (EditText)this.findViewById(R.id.textField_welcome_password);

        this.username.setOnClickListener(this);
        this.password.setOnClickListener(this);

        this.changeBackground();

        try
        {
            this.username.setText(Settings.getLastUsername());
            this.password.setText(Settings.getLastPassword());
        }
        catch(NullPointerException ex)
        {
            Logger.logOnly("No userdata entered yet!");
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
            //start register menu and closes this app
            this.startActivity(new Intent(this,RegisterMenu.class));
        }

        //login Button
        else if(id == R.id.button_login)
        {
            //gets the entered data
            String enteredName = this.username.getText().toString();
            //encrypts the entered password to compare it with the password in the db
            String enteredPassword = Caeser.encrypt
            (
                this.password.getText().toString(),
                Settings.encryptOffset
            );

            try
            {
                DatabaseManager db = new DatabaseManager(this);
                //get user and compare passwords...
                User user = db.getUser(enteredName);

                if(user.getPassword().equals(enteredPassword))
                {
                    CheckBox c = (CheckBox)this.findViewById(R.id.login_saveLoginData);
                    Settings.setCurrentUser(user);

                    if(c.isChecked())
                    {
                        Settings.save(this);
                    }

                    setCurrentUser(user);

                    ArrayList<String>  list = db.getCategories();
                    Settings.setCategories(list);
                    //Open Main Menu
                    Logger.write(this.getResources().getString(R.string.info_login_succeed)
                            , this, offset);
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
            //User does not exists...
            catch (NullPointerException ex)
            {
                Logger.write(this.getResources().getString(R.string.error_login_wrongUsername)
                        , this, offset);
            }
            //Error with the Database
            catch (SQLiteException ex)
            {
                Logger.write(this.getResources().getString(R.string.error_general_sqlite),
                        this, offset);

            }
        }
    }

    public static User getCurrentUser() {
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

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        Settings.setColor((RelativeLayout) this.findViewById(R.id.relLayout_login));
    }
}