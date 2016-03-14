package teamfmg.hangman;

import android.app.Activity;
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
            pWriter.println(Settings.theme.ordinal());
            pWriter.println(Settings.getCurrentUser().getName());
            pWriter.println(Settings.getCurrentUser().getPassword());

            for (int i = 0; i < Settings.categories.size(); i++)
            {
                pWriter.println(Settings.categories.get(i));
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
     * Delets the file with the last word.
     * @param a Context.
     */
    public static void deleteLastWord(Activity a)
    {
        try
        {
            File file = new File(Settings.getPath(a) + "/lastWord.txt");

            if(file.exists())
            {
                file.delete();
            }
        }
        catch (Exception e)
        {
            Logger.logOnlyError(e.getMessage());
        }
    }


    /**
     * Loads a word an its guessed letters.<br />
     * Position 1: Word<br />
     * Position 2: Category<br />
     * Position 3: Description <br />
     * Position 4 - end: Letters already guessed.<br />
     * @param a Context.
     * @return ArrayList
     */
    public static ArrayList<String> loadLastWord(Activity a)
    {
        try
        {
            File file = new File(Settings.getPath(a) + "/lastWord.txt");

            //if the file doesn't exits, abort.
            if(!file.exists())
            {
                Logger.logOnlyWarning("Tried loading last word but there is none!");
                return null;
            }

            FileReader fr;
            fr =  new FileReader(Settings.getPath(a)+"/lastWord.txt");
            BufferedReader br = new BufferedReader(fr,8192);


            ArrayList<String> ret = new ArrayList<>();

            ret.add(br.readLine());
            ret.add(br.readLine());
            ret.add(br.readLine());

            while(true)
            {
                String cat  = br.readLine();;

                if(cat == null)
                {
                    break;
                }
                else
                {
                    ret.add(cat);
                }
            }

            Logger.logOnly("Last word loaded! " + ret.get(0));

            fr.close();
            br.close();

            return ret;
        }
        catch (IOException ex)
        {
            Logger.logOnly(ex.getMessage());
        }
        catch (NumberFormatException ex)
        {
            Logger.logOnlyError(ex.getMessage());
        }

        return null;
    }

    /**
     * Saves a word to a txt.
     * @param word Word to save.
     * @param letters Letters to save.
     * @param a Context.
     */
    public static void saveWord(Word word, String[] letters, Activity a)
    {
        File file = new File(Settings.getPath(a) + "/lastWord.txt");
        PrintWriter pWriter = null;

        try
        {
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter(file),8192));
            pWriter.println(word.getWord());
            pWriter.println(word.getCategory());
            pWriter.println(word.getDescription());

            for (int i = 0; i < letters.length; i++)
            {
                pWriter.println(letters[i]);
            }

            Logger.logOnly("Word saved!: " + word.getWord());

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
     * Saves a user local.
     * @param a Context.
     * @param u User.
     */
    public static void saveCurrentUser(Activity a, User u)
    {
        File file = new File(Settings.getPath(a) + "/curUser.ini");
        PrintWriter pWriter = null;

        try
        {
            pWriter = new PrintWriter(new BufferedWriter(new FileWriter(file),8192));
            pWriter.print(u.getName());
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
     * Loads a user local.
     * @return user.
     */
    public static User loadCurrentUser(Activity a)
    {
        try
        {
            File file = new File(Settings.getPath(a) + "/curUser.ini");

            //if the file doesn't exits, abort.
            if(!file.exists())
            {
                return null;
            }

            FileReader fr;
            fr =  new FileReader(Settings.getPath(a)+"/curUser.ini");
            BufferedReader br = new BufferedReader(fr,8192);

            User u = new User
            (
                br.readLine(),
                "",
                "",
                -1
            );


            Logger.logOnly("User loaded!");
            fr.close();
            br.close();

            return u;
        }
        catch (IOException ex)
        {
            Logger.logOnly(ex.getMessage());
        }
        catch (NumberFormatException ex)
        {
            Logger.logOnlyError(ex.getMessage());
        }

        return null;
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
            Settings.categories = new ArrayList<>();

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
            Settings.lastUsername = br.readLine();
            Settings.lastPassword =  br.readLine();

            String cat;

            while(true)
            {
                cat = br.readLine();

                if(cat == null)
                {
                    break;
                }
                Settings.categories.add(cat);
            }

            Logger.logOnly("Settings loaded!");
            fr.close();
            br.close();

            //sort list
            Collections.sort(Settings.categories);

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
                Settings.theme = Theme.BLUE;
                break;
            case 1:
                Settings.theme = Theme.GREEN;
                break;
            case 2:
                Settings.theme = Theme.ORANGE;
                break;
            case 3:
                Settings.theme = Theme.PURPLE;
                break;
            default:
                Logger.logOnly("Couldn't index to color!");
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
