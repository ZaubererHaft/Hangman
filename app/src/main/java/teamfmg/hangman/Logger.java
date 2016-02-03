package teamfmg.hangman;

import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Hangman logging class.<br />
 * Created by Ludwig 09.11.2015.
 * @since 0.1
 */
public final class Logger
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

    public static void logOnlyError(Object obj)
    {
        Log.e("ERROR", obj.toString());
    }

    /**
     * Only writing to logcat.
     * @param obj Object.
     * @since 0.3
     */
    public static void logOnly(Object obj)
    {
        Log.i("MESSAGE:", obj.toString());
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

    public static void popupDialog (String word, String description, String category, Boolean won, Activity frame)
    {
        AlertDialog.Builder d = new AlertDialog.Builder(frame);
        if (won)
        {
            d.setTitle(frame.getResources().getString(R.string.string_win));
        }
        else
        {
            d.setTitle(frame.getResources().getString(R.string.string_lose));
        }
        if (description == null || description.length() == 0)
        {
            d.setMessage(frame.getResources().getString(R.string.string_Word) + ": " + word + "\n" +
                    frame.getResources().getString(R.string.string_Category) + ": " + Category.convertCategoryName(category, frame.getResources()) + "\n");
        }
        else
        {
            d.setMessage(frame.getResources().getString(R.string.string_Word) + ": " + word + "\n" +
                frame.getResources().getString(R.string.string_Description) + ": " + description + "\n" +
                frame.getResources().getString(R.string.string_Category) + ": " + Category.convertCategoryName(category, frame.getResources()) + "\n");
        }

        d.setPositiveButton("Ok", null);
        d.create().show();
    }
}
