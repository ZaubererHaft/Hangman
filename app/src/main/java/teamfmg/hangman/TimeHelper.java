package teamfmg.hangman;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Vincent on 21.02.2016.
 */
public final class TimeHelper
{
    private final static DatabaseManager db = DatabaseManager.getInstance();


    private TimeHelper(){}

    /**
     * Calculate the difference between 2 Times
     * @param earliearTime the earlier Time
     * @param laterTime the later Time
     * @return Amound of minutes the time differences
     */
    private static int timeDiffMins(Date earliearTime, Date laterTime)
    {
        int mins;

        long diff = laterTime.getTime() - earliearTime.getTime();

        mins = (int)(diff / 60000);

        return mins;
    }

    /**
     * Last online State as String
     * @param username the user
     * @return an formated String, perfect for StatisticMenu
     */
    public static String lastOnlineByUsername(String username)
    {
        int minutesAgo = timeDiffMins(db.getLastOnline(username), new Date());

        String text = setLastOnlineText(minutesAgo);

        return text;
    }

    /**
     * Last online State as String
     * @param date the date
     * @return an formated String, perfect for StatisticMenu
     */
    public static String lastOnlineByDate(String date)
    {
        int minutesAgo = timeDiffMins(stringToDate(date), new Date());

        String text = setLastOnlineText(minutesAgo);

        return text;
    }

    /**
     * Convert a String in an Date
     * @param dateString sting in an "yyyy-MM-dd HH:mm:ss" Format
     * @return A Date
     */
    public static Date stringToDate(String dateString)
    {
        //converting in an DateFormat
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date lastLogin = null;

        try
        {
            lastLogin = format.parse(dateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        }

        return lastLogin;
    }

    /**
     * Last online State as String
     * @param minutesAgo the minutes the lastOnline were
     * @return an formated String, perfect for StatisticMenu
     * TODO: Entfernen der Hardcoded Strings
     */
    private static String setLastOnlineText(int minutesAgo){

        String text;

        if (minutesAgo <= 1) //in the last minute
        {
            text = "Jetzt";
        }
        else if (minutesAgo < 60) //in the last 60 minutes
        {
            text = "Vor " + minutesAgo + " Minuten";
        }
        else if (minutesAgo < 120) //in the last 2 hours
        {
            text = "Vor einer Stunde";
        }
        else if (minutesAgo < 1440) //in the last 24 hours
        {
            text = "Vor " + (int)(minutesAgo/60) + " Stunden";
        }
        else if (minutesAgo < 2880) //in the last 2 days
        {
            text = "Vor einem Tag";
        }
        else //Longer then 2 days
        {
            text = "Vor " + (int)(minutesAgo/1440) + " Tage";
        }

        return text;
    }
}
