package teamfmg.hangman;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
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
        if(obj == null)
        {
            Log.e("ERROR", "Null Error Message!");
        }
        else
        {
            Log.e("ERROR", obj.toString());
        }

    }
    public static void logOnlyWarning(Object obj)
    {
        Log.w("WARNING", obj.toString());
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

    public static void popupDialogGameResult(String word, String description, String category, String title, Activity frame)
    {
        AlertDialog.Builder d = new AlertDialog.Builder(frame);
        d.setTitle(title);

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

    public static void messageDialog (String title, String message, Activity frame)
    {
        AlertDialog.Builder d = new AlertDialog.Builder(frame);
        d.setTitle(title);
        d.setMessage(message);
        d.setPositiveButton("Ok", null);
        d.create().show();
    }

    //Currently no funktion
    public static boolean yesNoDialog (String message, Activity activity){

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };



        return true;
    }

    /**
     * Showas a notifcation.
     * @param a From wich activity to show.
     * @param target Here you are landing when clicking on notification.
     * @param title Title of the notification
     * @param description Description of the notification
     * @param iconID ID of the drawable
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotification(Activity a, Intent target, String title, String description, int iconID)
    {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(a)
                                .setSmallIcon(iconID)
                                .setContentTitle(title)
                                .setContentText(description);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(a);


                stackBuilder.addParentStack(a.getClass());
                stackBuilder.addNextIntent(target);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) a.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
                mNotificationManager.notify(1, mBuilder.build());
    }
}
