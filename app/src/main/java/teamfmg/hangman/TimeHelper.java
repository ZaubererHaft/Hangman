package teamfmg.hangman;

import java.util.Date;

/**
 * Created by Vincent on 21.02.2016.
 */
public class TimeHelper
{
    private static DatabaseManager db = DatabaseManager.getInstance();

    /**
     * Calculate the difference between 2 Times
     * @param earliearTime the earlier Time
     * @param laterTime the later Time
     * @return Amound of minutes the time differences
     */
    private int timeDiffMins(Date earliearTime, Date laterTime)
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
     * //TODO Entfernen der Hardcoded Strings
     */
    public String lastOnline(String username)
    {
        int minutesAgo = timeDiffMins(db.getLastOnline(username), new Date());

        String text;

        if (minutesAgo <= 3) //in the last 3 minutes
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
