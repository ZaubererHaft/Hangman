package teamfmg.hangman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by consult on 2/17/16.
 */
public class Updater extends Thread
{


    @Override
    public void run()
    {
        try
        {
            //version[0].toString()
            //URL file = new URL("h2530840.stratoserver.net/version.txt");
            //URL url = new URL("http://myexample.com/android/");
            URL url = new URL("http://h2530840.stratoserver.net/version.txt");


            URLConnection uconn = url.openConnection();
            uconn.setReadTimeout(3000);
            uconn.setConnectTimeout(3000);

            InputStream is = uconn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            Logger.logOnly("Current version is: " +reader.readLine());
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

    }
}
