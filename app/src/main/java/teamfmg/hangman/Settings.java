package teamfmg.hangman;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.widget.RelativeLayout;

/**
 * Public settings class.
 * Created by Ludwig on 10.11.2015.
 * @since 0.2
 */
public final class Settings
{

    //TODO: Save settings (Color)

    public enum Theme {BLUE, GREEN, ORANGE, PURPLE}
    public static Theme theme = Theme.GREEN;

    public static final Point minScreenRes = new Point(600,800);

    /**
     * Returns the screenSize of an activity.
     * @param a the activity.
     * @return Point.
     * @since 0.2
     */
    public static Point getScreenSize(Activity a)
    {
        Display display = a.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();

        return new Point(width,height);
    }

    /**
     * Sets the color depending on the settings.
     * @param rl Layout to set.
     * @since 0.3
     */
    public static void setColor(RelativeLayout rl)
    {
        switch (Settings.theme)
        {
            case BLUE:
                rl.setBackgroundResource(R.drawable.blue);
                break;
            case GREEN:
                rl.setBackgroundResource(R.drawable.green);
                break;
            case ORANGE:
                rl.setBackgroundResource(R.drawable.orange);
                break;
            case PURPLE:
                rl.setBackgroundResource(R.drawable.purple);
                break;
        }
    }

    /**
     * Saves the settings to a file.
     * @since 0.3
     */
    public static void save()
    {
        //TODO: implement
    }

    /**
     * Loads the settings if they were already saved.
     * @since 0.3
     */
    public static void load()
    {
        //TODO: implement
    }

}
