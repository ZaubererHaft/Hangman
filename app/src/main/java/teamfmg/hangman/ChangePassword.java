package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.sql.SQLException;

/**
 * Menu to change password.
 * @author Lucas
 * @since 1.1
 */
public class ChangePassword extends Activity implements View.OnClickListener, IApplyableSettings
{
    private DatabaseManager db;
    private Boolean hasToChangePW;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Bundle extra = this.getIntent().getExtras();

        hasToChangePW = false;
        if (extra != null)
        {
            hasToChangePW = extra.getBoolean("hasToChangePW");
        }
        if (hasToChangePW)
        {
            this.findViewById(R.id.changePasswort_oldPW).setEnabled(false);
        }

        this.db =  DatabaseManager.getInstance();

        this.changeBackground();
        this.findViewById(R.id.changePassword_button).setOnClickListener(this);

        //Updating the lastOnline Time
        db.updateLastOnline();
    }

    @Override
    public void onClick(View v)
    {

        DatabaseManager db =  DatabaseManager.getInstance();
        db.setActivity(this);

        if (v.getId() == R.id.changePassword_button)
        {

                if(!db.isOnline())
                {
                    Logger.write(this.getString(R.string.error_offline_general),this);
                    return;
                }

                String oldPW = ((EditText)this.findViewById(R.id.changePasswort_oldPW)).getText().toString();

                EditText newPw = (EditText)this.findViewById(R.id.changePasswort_newPW);
                String newPW = newPw.getText().toString();

                EditText confirmPw = (EditText)this.findViewById(R.id.changePasswort_confirmPW);
                String confirmPW = confirmPw.getText().toString();

                oldPW = Caeser.encrypt(oldPW);

                String oldUserPW = db.getUser(LoginMenu.getCurrentUser(this).getName()).getPassword();



                if (hasToChangePW || oldPW.equals(oldUserPW))
                {
                    if(newPW.length() < 8)
                    {
                        Logger.write(this.getString(R.string.error_register_passwordShort), this);
                        return;
                    }

                    if (newPW.equals(confirmPW))
                    {
                        try
                        {
                            db.changePassword(Caeser.encrypt(newPW));
                            Logger.write(this.getString(R.string.changePW_success), this);
                            this.finish();
                        }
                        catch(SQLException ex)
                        {
                            Logger.write(this.getString(R.string.error_changePW),this);
                        }
                    }
                    else
                    {
                        Logger.write(this.getResources().
                                getString(R.string.error_register_wrongRepeatedPassword),this);
                    }
                }
                else
                {
                    Logger.write(this.getResources().
                            getString(R.string.error_changePW_oldPasswordWrong), this);
                }

        }

        if(v.getId() == R.id.changePW_close)
        {
            this.finish();
        }

    }

    @Override
    public void changeBackground()
    {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_changePW);
        Settings.setColor(rl);
    }
}
