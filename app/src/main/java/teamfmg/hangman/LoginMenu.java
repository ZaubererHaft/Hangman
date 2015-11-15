package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

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
     * Buttons.
     */
    private Button login, register, exit;
    /**
     * Text fields.
     */
    private EditText username, password;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);

        //load user settings
        Settings.load(this);

        this.login      = (Button)this.findViewById(R.id.button_login);
        this.register   = (Button)this.findViewById(R.id.button_register);
        this.exit       = (Button)this.findViewById(R.id.button_exit);

        this.username   = (EditText)this.findViewById(R.id.textField_welcome_username);
        this.password   = (EditText)this.findViewById(R.id.textField_welcome_password);

        this.login.setOnClickListener(this);
        this.register.setOnClickListener(this);
        this.exit.setOnClickListener(this);

        this.username.setOnClickListener(this);
        this.password.setOnClickListener(this);

        this.changeBackground();

    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        final int offset = 50;

        //exit button
        if(id == this.exit.getId())
        {
            System.exit(0);
        }
        //register button
        else if(id == this.register.getId())
        {
            //start register menu and closes this app
            Intent i = new Intent(this,RegisterMenu.class);
            this.startActivity(i);
        }

        //login Button
        else if(id == this.login.getId())
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
                    //Open Main Menu
                    Logger.write(this.getResources().getString(R.string.info_login_succeed)
                            ,this, offset);
                    Intent i = new Intent(this,MainMenu.class);
                    this.finish();
                    this.startActivity(i);
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
                        ,this, offset);
            }
            //Error with the Database
            catch (SQLiteException ex)
            {
                Logger.write(this.getResources().getString(R.string.error_general_sqlite),
                        this, offset);
            }
        }
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_login);
        Settings.setColor(rl);
    }
}