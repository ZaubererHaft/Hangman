package teamfmg.myapplication;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Hangman logging class.<br />
 * Created by Ludwig 09.11.2015.
 * @since 0.1
 */
public class Logger
{
    /**
     * Logs a info text to the console and opens a toast at the device.
     * @param text The text to show.
     * @param frame The activity to show the toast.
     * @param offset The offset of the text from the bottom.
     * @since 0.1
     */
    public static void write(Object text, Activity frame, int offset)
    {
        Log.i("MESSAGE:", text.toString());

        if(frame != null)
        {
            Toast toast = Toast.makeText(frame.getApplicationContext(),
                    text.toString(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, offset);
            toast.show();
        }
    }

    /**
     * Logs a info text to the console and opens a toast at the device.
     * @param text The text to show.
     * @param frame The activity to show the toast.
     * @since 0.1
    */
    public static void write(Object text, Activity frame)
    {
        final int defaultOffset = 100;
        Log.i("MESSAGE:", text.toString());

        if(frame != null)
        {
            Toast toast = Toast.makeText(frame.getApplicationContext(),
                    text.toString(), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, defaultOffset);
            toast.show();
        }
    }
}
