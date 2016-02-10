package teamfmg.hangman;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Public settings class.<br />
 * Created by Ludwig on 10.11.2015.
 * @since 0.2
 */
public final class Settings
{
    /**
     * Enum to define color theme.
     */
    public enum Theme {BLUE, GREEN, ORANGE, PURPLE}
    /**
     * Color theme of the game.
     */
    private static Theme theme = Theme.GREEN;
    /**
     * The key for encryption.
     */
    public static final int encryptOffset = 6;
    /**
     * The user the player has logged in with.
     */
    private static User currentUser = new User("","","",-1);
    /**
     * The last entered username(use this to fill up the text field in login)
     */
    private static String lastUsername;
    /**
     * Gets the latest password
     */
    private static String lastPassword;
    /**
     * Word categories.
     */
    private static List<String> categories = new ArrayList<>();

    private Settings()
    {

    }

    /**
     * Gets the available categories.
     * @return List
     * @since 0.7
     */
    public static List<String> getCategories()
    {
        Collections.sort(categories);
        return  categories;
    }

    /**
     * Sets the categories.
     * @param categories The categories.
     * @since 0.7
     */
    public static void setCategories(List<String> categories)
    {
        Settings.categories = categories;
    }

    /**
     * Detects whether program is running on a large screen.
     * @param context Activity context.
     * @return boolean
     */
    public static boolean isTablet(Context context)
    {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Gets the color theme of the game.
     * @return Theme (enum).
     * @since 0.3
     */
    public static Theme getTheme()
    {
        return theme;
    }

    /**
     * Gets the current user.
     * @return user
     * @since 0.3
     */
    public static User getCurrentUser()
    {
        return currentUser;
    }

    /**
     * Gets the latest password
     * @return user
     * @since 0.3
     */
    public static String getLastPassword()
    {
        return lastPassword;
    }

    /**
     * Sets the current user.
     * @param currentUser user
     * @since 0.3
     */
    public static void setCurrentUser(User currentUser)
    {
        Settings.currentUser = currentUser;
    }

    /**
     * Returns the screenSize of an activity.
     * @param a the activity.
     * @deprecated
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
     * @param a Context to ave the file.
     * @since 0.3
     */
    public static void save(Activity a)
    {
        File file = new File(Settings.getPath(a) + "/settings.ini");
        PrintWriter pWriter = null;
        try
        {
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter(file),8192));
            pWriter.println(theme.ordinal());
            pWriter.println(getCurrentUser().getName());
            pWriter.println(getCurrentUser().getPassword());

            for (int i = 0; i < categories.size(); i++)
            {
                pWriter.println(categories.get(i));
            }

            Logger.logOnly("Settings saved!");
        }
        catch (IOException ex)
        {
            Logger.logOnly(ex.getMessage());
        }
        finally
        {
            if (pWriter != null)
            {
                pWriter.flush();
                pWriter.close();
            }
        }
    }

    /**
     * Gets the name of the latest logged in user. <br />
     * Use this to fill in the username in the {@link LoginMenu}.
     * @return string.
     * @since 0.3
     */
    public static String getLastUsername()
    {
        return lastUsername;
    }

    /**
     * Loads the settings if they were already saved.
     * @param a Context to load the file.
     * @since 0.3
     */
    public static void load(Activity a)
    {
        try
        {
            File file = new File(Settings.getPath(a) + "/settings.ini");
            categories = new ArrayList<>();

            //if the file doesn't exits, just create one.
            if(!file.exists())
            {
                Settings.save(a);
                return;
            }

            FileReader fr;
            fr =  new FileReader(Settings.getPath(a)+"/settings.ini");
            BufferedReader br = new BufferedReader(fr,8192);

            //Gets the color theme
            String color = br.readLine();
            Settings.indexToColor(Integer.valueOf(color));

            //Gets the last logged in user
            lastUsername = br.readLine();
            lastPassword =  Caeser.encrypt(br.readLine(), 128 - Settings.encryptOffset);

            String cat;

            while(true)
            {
                cat = br.readLine();

                if(cat == null)
                {
                    break;
                }
                categories.add(cat);
            }

            Logger.logOnly("Settings loaded!");
            fr.close();
            br.close();

            //sort list
            Collections.sort(categories);

        }
        catch (IOException ex)
        {
            Logger.logOnly(ex.getMessage());
        }
        catch (NumberFormatException ex)
        {
            Logger.logOnlyError(ex.getMessage());
        }
    }

    /**
     * Sets the color of the settings by a index.
     * @param i String.
     * @since 0.8
     */
    public static void indexToColor(int i)
    {

        switch (i)
        {
            case 0:
                theme = Theme.BLUE;
                break;
            case 1:
                theme = Theme.GREEN;
                break;
            case 2:
                theme = Theme.ORANGE;
                break;
            case 3:
                theme = Theme.PURPLE;
                break;
            default:
                Logger.logOnly("Couldn't index to color!");
        }
    }
    /**
     * Sets the color of the settings by a string.
     * @param s String.
     * @since 0.3
     * @deprecated
     */
    private static void stringToColor(String s)
    {

        switch (s)
        {
            case "BLUE":
                theme = Theme.BLUE;
                break;
            case "GREEN":
                theme = Theme.GREEN;
                break;
            case "ORANGE":
                theme = Theme.ORANGE;
                break;
            case "PURPLE":
                theme = Theme.PURPLE;
                break;
            default:
                Logger.logOnly("Couldn't string to color!");
        }
    }
    /**
     * Gets the path of an activity.
     * @param a The activity.
     * @return The path as string.
     * @since 0.3
     */
    public static String getPath(Activity a)
    {
        if(a != null)
        {
            return a.getFilesDir().getPath();
        }
        else
        {
            return null;
        }
    }
}
