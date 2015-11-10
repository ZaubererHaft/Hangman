package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * The Login menu for Hangman.<br />
 * Created by Ludwig 09.11.2015.
 * @since 0.1
 */
public class LoginMenu extends Activity implements View.OnClickListener
{

    //DONE: (1) Fix max Length of the text fields
    //DONE: (2) Only allow letters and numbers in username
    //DONE: (3) Continue Button Needs to work
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
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
            this.finish();
            this.startActivity(i);
        }

        //login Button
        else if(id == this.login.getId())
        {
            //gets the entered data
            String enteredName = this.username.getText().toString();
            String enteredPassword = this.password.getText().toString();

            try
            {
                DatabaseManager db = new DatabaseManager(this);
                //get user an compare passwords...
                User user = db.getUser(enteredName);

                if(user.getPassword().equals(enteredPassword))
                {
                    //TODO: Add actitvity
                    Logger.write(this.getResources().getString(R.string.info_login_succeed)
                            ,this, offset);
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
}