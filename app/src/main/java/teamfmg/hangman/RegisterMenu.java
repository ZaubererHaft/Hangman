package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Hangman register menu.<br />
 * Created by Ludwig 09.11.2015.
 * @since 0.1
 */
public class RegisterMenu extends Activity implements View.OnClickListener, IApplyableSettings
{
    /**
     * Buttons
     */
    private Button back, register;
    /**
     * Text fields
     */
    private EditText password, username, mail, repeatedPassword;
    /**
     * This handles the database connection.
    */
    private DatabaseManager db;
    /**
     * y Offset for toast
     */
    private final int offset = 125;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_menu);

        this.back       = (Button)this.findViewById(R.id.register_close);
        this.register   = (Button)this.findViewById(R.id.button_register);

        this.mail       = (EditText)this.findViewById(R.id.textField_register_mail);
        this.username   = (EditText)this.findViewById(R.id.textField_register_username);
        this.password   = (EditText)this.findViewById(R.id.textField_register_password);

        this.repeatedPassword =
                (EditText)this.findViewById(R.id.textField_register_repeat_password);

        this.back.setOnClickListener(this);
        this.register.setOnClickListener(this);

        db = new DatabaseManager(this);

        this.changeBackground();

    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        //Back button
        if(id == this.back.getId())
        {
            //Intent i = new Intent(this, LoginMenu.class);
            this.finish();
            //this.startActivity(i);
        }
        //register button
        else if(id == this.register.getId())
        {
            //try to register and get error code
            String errorCode = this.register();

            //if there was no error
            if(errorCode == null)
            {
                //return to Login
                //Intent i = new Intent(this,LoginMenu.class);
                this.finish();
                //this.startActivity(i);
            }
            else
            {
                //show message
                Logger.write(errorCode,this,offset);
            }
        }
    }
    /**
     * Checks the entered data.
     * @return Returns an error code if there is an error with the entered data or null if
     * everything was correct
     * @since 0.1
     */
    private String register()
    {
        //check username
        String enteredUsername = this.username.getText().toString().toLowerCase();
        if(enteredUsername.length() == 0)
        {
            return this.getResources().getString(R.string.error_register_emptyUsername);
        }

        User u = this.db.getUser(enteredUsername);

        //check if there is an existing user
        if(u != null)
        {
            return this.getResources().getString(R.string.error_register_userAlreadyExists);
        }

        //check e-mail address
        String enteredMail = this.mail.getText().toString();
        if(!enteredMail.contains("@") || !enteredMail.contains(".") || enteredMail.length() == 0)
        {
            return this.getResources().getString(R.string.error_register_invalidEmail);
        }

        String enteredPassword = this.password.getText().toString();
        String repeatedPassword = this.repeatedPassword.getText().toString();

        //Password too short or bad format
        if(enteredPassword.length() < 8)
        {
            return this.getResources().getString(R.string.error_register_passwordShort);
        }
        //compare the second entered password to the first version.
        if(!enteredPassword.equals(repeatedPassword))
        {
            return this.getResources().getString(R.string.error_register_wrongRepeatedPassword);
        }

        this.db.addUser
        (
            new User
            (
                enteredUsername,
                //encryption
                Caeser.encrypt(enteredPassword,Settings.encryptOffset),
                enteredMail
            )
        );
        Logger.write(this.getResources().getString(R.string.info_register_succeed), this, offset);
        return null;
    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_register);
        Settings.setColor(rl);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.gc();
    }
}
