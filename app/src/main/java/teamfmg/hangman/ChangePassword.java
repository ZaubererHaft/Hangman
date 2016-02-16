package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.sql.SQLException;

public class ChangePassword extends Activity implements View.OnClickListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        this.findViewById(R.id.changePassword_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {

        DatabaseManager db =  DatabaseManager.getInstance();
        db.setActivity(this);

        if (v.getId() == R.id.changePassword_button)
        {
                String oldPW = ((EditText)this.findViewById(R.id.changePasswort_oldPW)).getText().toString();

                EditText newPw = (EditText)this.findViewById(R.id.changePasswort_newPW);
                String newPW = newPw.getText().toString();

                EditText confirmPw = (EditText)this.findViewById(R.id.changePasswort_confirmPW);
                String confirmPW = confirmPw.getText().toString();

                String oldUserPW = db.getUser(LoginMenu.getCurrentUser().getName()).getPassword();
                oldPW = Caeser.encrypt(oldPW, Settings.encryptOffset);

                if (oldPW.equals(oldUserPW))
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
                            db.changePassword(Caeser.encrypt(newPW,Settings.encryptOffset));
                            Logger.write(this.getString(R.string.changePW_success), this);
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
                            getString(R.string.error_changePW_oldPasswordWrong),this);
                }

        }

    }
}
