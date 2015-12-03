package teamfmg.hangman;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Plays music.
 * TODO:implement
 * Created by Ludwig on 28.11.2015.
 * @since 0.6
 */
public class MusicPlayer
{
    private static MediaPlayer mp = new MediaPlayer();

    public enum MusicType {INTRO}

    private MusicPlayer()
    {

    }

    public static void playNext(Context context, MusicType type)
    {
        //TODO: Add Musik
       // mp = MediaPlayer.create(context, R.raw.intro);
        //mp.start();
    }
}

