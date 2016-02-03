package teamfmg.hangman;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

        if (v.getId() == R.id.changePassword_button)
        {
            EditText oldPw = (EditText)this.findViewById(R.id.changePasswort_oldPW);
            String oldPW = oldPw.getText().toString();
            DatabaseManager dbm = DatabaseManager.getInstance();

                EditText newPw = (EditText)this.findViewById(R.id.changePasswort_newPW);
                String newPW = newPw.getText().toString();

                EditText confirmPw = (EditText)this.findViewById(R.id.changePasswort_confirmPW);
                String confirmPW = confirmPw.getText().toString();

                if(newPW == confirmPW)
                {
                    DatabaseManager db = DatabaseManager.getInstance();
                    db.setActivity(this);

                    db.changePassword(newPW);


                }

        }

    }
}
