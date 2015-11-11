package teamfmg.hangman;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

/**
 * Public settings class.
 * Created by Ludwig on 10.11.2015.
 * @since 0.2
 */
public class Settings
{

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

}
