package teamfmg.hangman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

    public static void showNotification(String eventtext, Context ctx) {

        /*
        // intent triggered, you can add other intent for other actions
        Intent intent = new Intent(ctx, LoginMenu.class);
        PendingIntent pIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        // this is it, we'll build the notification!
        // in the addAction method, if you don't want any icon, just set the first param to 0
        Notification mNotification = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
        {
            mNotification = new Notification.Builder(ctx)

                    .setContentTitle("New Post!")
                    .setContentText("Here's an awesome update for you!")
                    //.setSmallIcon(R.drawable.ninja)
                    .setContentIntent(pIntent)
                    //.setSound(soundUri)

                    //.addAction(R.drawable.ninja, "View", pIntent)
                    .addAction(0, "Remind", pIntent)

                    .build();

        }

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(ctx.NOTIFICATION_SERVICE);

        // If you want to hide the notification after it was selected, do the code below
        // myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, mNotification);
        */
    }
}
