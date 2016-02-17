package teamfmg.hangman;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

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
     * Creates a new updater class.
     * @param a Activity context.
     */
    public Updater(Activity a)
    {
        this.context = a;
    }

    /**
     * Checks fo an update of hangman.
     */
    public void checkForUpdates()
    {
        int clientVersion = -2;
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
            return;
        }

        Logger.logOnly("Client version is: " +clientVersion);
        Logger.logOnly("Server version is: " +serverVersion);

        /*
         * Compare versions and start download.
         */
        if(serverVersion != clientVersion)
        {
            AlertDialog.Builder d = new AlertDialog.Builder(context);
            d.setTitle(this.context.getString(R.string.update_info_downloadStarted));
            d.setMessage(this.context.getString(R.string.update_info_downloadNotice));
            d.setPositiveButton("Ok", null);
            d.create().show();
            this.start();
        }
        else
        {
            Logger.write(this.context.getString(R.string.update_info_alreadyLatest), this.context);
        }

    }


    /**
     * Tries to update the current version.
     */
    private void update()
    {
        FileOutputStream fos = null;
        URLConnection uconn = null;
        InputStream is = null;
        //String PATH = a.getFilesDir().getPath();
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        file.mkdirs();
        File outputFile = new File(file, "app-Android2-release.apk");

        try
        {
            //Server URL
            URL url = new URL("http://h2530840.stratoserver.net/app-Android2-release.apk");

            uconn = url.openConnection();
            uconn.setReadTimeout(20000);
            uconn.setConnectTimeout(20000);

            if(outputFile.exists())
            {
                outputFile.delete();
            }

            fos = new FileOutputStream(outputFile);
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
            e.printStackTrace();
            Logger.logOnlyError(e.getMessage());
            Logger.write(this.context.getString(R.string.update_error_general), this.context);
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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Try open and start installer
        if(outputFile.exists())
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(new File(file.getPath() + "/app-Android2-debug.apk"));
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            this.context.startActivity(intent);

        }
        else
        {
            Logger.write(context.getString(R.string.update_error_general), this.context);
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
