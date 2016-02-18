package teamfmg.hangman;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Updater class.
 * Created by Ludwig on 2/17/16.
 */
public final class Updater extends Thread
{

    /**
     * Context of this class.
     */
    private Activity context;

    /**
     * Time to timeout connection (in ms).
     */
    private static final int TIMEOUT = 5000;

    /**
     * Creates a new updater class.
     * @param a Activity context.
     */
    public Updater(Activity a)
    {
        this.context = a;
    }


    /**
     * Checks whether a update is necessary or not.
     * @return Boolean
     */
    public boolean updatePossible()
    {
        int clientVersion = -1;
        int serverVersion = -1;

        try
        {
            serverVersion = this.getServerVersion();
            clientVersion = this.getClientVersion();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.write(this.context.getString(R.string.update_error_general), this.context);
        }

        Logger.logOnly("Client version is: " +clientVersion);
        Logger.logOnly("Server version is: " +serverVersion);

        return serverVersion != clientVersion;
    }

    /**
     * Checks fo an update of hangman and start the download.
     */
    public void checkForUpdatesAndDownload()
    {
        if(this.updatePossible())
        {
            Logger.messageDialog
            (
                this.context.getString(R.string.update_info_downloadStarted),
                "",
                this.context
            );
            this.start();
        }
        else
        {
            Logger.write(this.context.getString(R.string.update_info_alreadyLatest), this.context);
        }

    }


    /**
     * Tries to update the current version. <br />
     * Changes: Now saving internal.
     * @since 1.3
     */
    private void update()
    {
        final String fileName = "app-Android2-release.apk";

        FileOutputStream fos = null;
        URLConnection uconn;
        InputStream is = null;
        File file  = new File(context.getFilesDir().getPath());
        //File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        file.mkdirs();
        File outputFile = new File(file, fileName);

        try
        {

            //Server URL
            URL url = new URL("http://h2530840.stratoserver.net/"+fileName);

            uconn = url.openConnection();
            uconn.setReadTimeout(TIMEOUT);
            uconn.setConnectTimeout(TIMEOUT);

            if(outputFile.exists())
            {
                outputFile.delete();
            }

            fos = context.openFileOutput("/"+fileName,Activity.MODE_WORLD_READABLE);
            //new FileOutputStream(outputFile, context.MODE_WORLD_READABLE);
            is = new BufferedInputStream(uconn.getInputStream());

            int fileLength = uconn.getContentLength();
            Logger.logOnly("You are downloading: "+fileLength);

            byte data[] = new byte[8192];
            int total = 0;
            int bytesRead;
            int newProgress = 0, oldProgress;

            //writes the data to the memory
            while ((bytesRead = is.read(data)) > 0)
            {
                total += bytesRead;
                // publishing the progress....
                if (fileLength > 0)
                {
                    oldProgress = newProgress;
                    newProgress =  (total * 100 / fileLength);

                    if(oldProgress != newProgress)
                    {
                        Logger.logOnly(newProgress+"% downloaded!");
                    }
                }
                fos.write(data, 0, bytesRead);
            }
        }
        catch (Exception e)
        {
            Logger.logOnlyError(e.getMessage());
        }


        finally
        {
            try
            {
                if(fos != null)
                {
                    fos.flush();
                    fos.close();
                }
                if(is != null)
                {
                    is.close();
                }
            }
            catch (IOException | NullPointerException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
            Logger.logOnlyError(e.getMessage());
        }

        //Try open and start installer
        if(outputFile.exists())
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(new File(file.getPath() + "/" + fileName));
            intent.setDataAndType(uri, "application/vnd.android.package-archive");

            /**
             * Show notification for new versions.
             */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN)
            {
                Logger.showNotification
                (
                    this.context,
                    intent,
                    this.context.getString(R.string.update_info_success_title),
                    this.context.getString(R.string.update_info_success_description),
                    R.drawable.hangman_logo
                );
            }
            else
            {
                //else start just the installer.
                this.context.startActivity(intent);
            }
        }
        else
        {
            Logger.logOnlyError("Error updating!");

            this.context.runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    Logger.write(context.getString(R.string.update_error_general), context);
                }
            });
        }

    }


    /**
     * Gets the version of the server.
     * @return Int.
     * @throws Exception if sth. went wrong.
     */
    private int getServerVersion() throws Exception
    {

        URL url = new URL("http://h2530840.stratoserver.net/version.txt");


        URLConnection uconn = url.openConnection();
        uconn.setReadTimeout(3000);
        uconn.setConnectTimeout(3000);

        InputStream is = uconn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String vers = reader.readLine();

        return Integer.parseInt(vers);
    }


    /**
     * Gets the version of the app pf the client.
     * @return Integer.
     */
    private int getClientVersion()
    {
        int versionCode = -1;
        try
        {
            versionCode = this.context.getPackageManager().
                    getPackageInfo(this.context.getPackageName(), 0).versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            Logger.write(this.context.getString(R.string.update_error_general), this.context);
        }

        return versionCode;
    }


    @Override
    public void run()
    {
        this.update();
    }
}
