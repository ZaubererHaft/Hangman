package teamfmg.hangman;

import android.app.Activity;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.AdapterView;

/**
 * Plays music.
 * TODO:implement
 * Created by Ludwig on 28.11.2015.
 * @since 0.6
 */
public class MusicPlayer implements AdapterView.OnItemSelectedListener
{
    private static MediaPlayer mp = new MediaPlayer();
    public enum MusicType {INTRO}

    private MusicPlayer()
    {

    }

    public static void playNext(int id, Activity context)
    {
        mp = MediaPlayer.create(context, id);
        mp.start();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }




}

