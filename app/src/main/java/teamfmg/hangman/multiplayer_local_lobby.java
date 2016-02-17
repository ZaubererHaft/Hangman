package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class multiplayer_local_lobby extends Activity implements IApplyableSettings, View.OnClickListener{

    private int idInclude = 0;
    private List<View> playerViewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_local_lobby);
        this.changeBackground();

        this.playerViewsList = new ArrayList<>();
        this.addInclude();
        this.addInclude();
        this.findViewById(R.id.multiplayer_local_lobby_removePlayer).setEnabled(false);

        //ClickListener
        this.findViewById(R.id.multiplayer_local_lobby_addPlayer).setOnClickListener(this);
        this.findViewById(R.id.multiplayer_local_lobby_removePlayer).setOnClickListener(this);
        this.findViewById(R.id.button_multiplayer_local_lobby_exit).setOnClickListener(this);
        this.findViewById(R.id.multiplayer_local_lobby_button_play).setOnClickListener(this);
    }

    /**
     * Adds an new Player to the ScrollView
     */
    private void addInclude()
    {
        //Enables the oputunity to remove an player
        this.findViewById(R.id.multiplayer_local_lobby_removePlayer).setEnabled(true);

        idInclude++;


        //adds the Child to the ScrollView and add it to the Arraylist
        LinearLayout parent = (LinearLayout)this.findViewById(R.id.multiplayer_local_lobby_scrollView_playerList);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

        View child = inflater.inflate(R.layout.new_player_element, null, false);

        ((TextView)child.findViewById(R.id.new_player_element_playerXY)).setText("Player " + idInclude);

        playerViewsList.add(child);

        parent.addView(child);

        ((EditText)child.findViewById(R.id.new_player_element_textField_playerName)).setHint("Player " + idInclude);

        if (idInclude == 1)
        {
            ((EditText)child.findViewById(R.id.new_player_element_textField_playerName)).setText(LoginMenu.getCurrentUser().getName());
            child.findViewById(R.id.new_player_element_textField_playerName).setEnabled(false);
        }

        //disable the add Button if 8 Players are shown
        if (idInclude == 8)
        {
            this.findViewById(R.id.multiplayer_local_lobby_addPlayer).setEnabled(false);
        }
    }

    /**
     * Removes an Player from the ScrollView
     */
    private void removeInclude()
    {
        //Enables the oputunity to add an player
        this.findViewById(R.id.multiplayer_local_lobby_addPlayer).setEnabled(true);

        idInclude--;

        //remove the Child from the ScrollView and remove it from the Arraylist
        ViewGroup vg = (ViewGroup)this.findViewById(R.id.multiplayer_local_lobby_scrollView_playerList);
        vg.removeView(playerViewsList.get(idInclude));

        playerViewsList.remove(idInclude);

        //disable the removeButton if only 2 Players are shown
        if (idInclude == 2)
        {
            this.findViewById(R.id.multiplayer_local_lobby_removePlayer).setEnabled(false);
        }
    }

    private String[] getUsers()
    {
        String[] array = new String[playerViewsList.size()];
        for (int i = 0; playerViewsList.size() > i; i++)
        {
            EditText editText = (EditText)playerViewsList.get(i).findViewById(R.id.new_player_element_textField_playerName);
            if (editText.getText().toString().length() > 1)
            {
                array[i] = editText.getText().toString();
            }
            else
            {
                array[i] = editText.getHint().toString();
            }
        }

        return array;
    }


    @Override
    public void changeBackground() {
        Settings.load(this);
        RelativeLayout rl         = (RelativeLayout)this.findViewById(R.id.relLayout_multiplayer_local_lobby);
        Settings.setColor(rl);
    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch(v.getId())
        {
            case R.id.button_multiplayer_local_lobby_exit:
                this.finish();
                break;

            case R.id.multiplayer_local_lobby_addPlayer:
                this.addInclude();
                break;

            case R.id.multiplayer_local_lobby_removePlayer:
                this.removeInclude();
                break;

            case R.id.multiplayer_local_lobby_button_play:
                i = new Intent(this, MultiplayerLocal.class);
                MultiplayerLocal.usernames = getUsers();
                Singleplayer.gameMode = Singleplayer.GameMode.LOCALMULTIPLAYER;
                this.startActivity(i);
                break;

            default:
                Logger.write("Currently no function", this);
        }
    }
}
