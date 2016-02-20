package teamfmg.hangman;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * Class to encrypt strings.
 * Created by Ludwig on 14.11.2015.
 * @since 0.4
 */
public class Caeser
{

    /**
     * En/decrypts a string.
     * @param text string to encrypt.
     * @return encrypted/decrypted string
     * @since 0.4
     */
    public static String encrypt(String text)
    {

        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.update(text.getBytes());
            Formatter formatter = new Formatter();
            for(byte b: digest.digest())
            {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
        catch (NoSuchAlgorithmException ex)
        {
            ex.printStackTrace();
        }

        return null;
        /*if(offset > 127 || offset < 1)
        {
            Logger.logOnly("The offset value is only valid between 1 and 127!");
            return null;
        }

        char[] charArray = text.toCharArray();
        char[] cryptArray = new char[charArray.length];

        for (int i = 0; i < charArray.length; i++) {

            int newSign = (charArray[i] + offset) % 128;
            cryptArray[i] = (char) (newSign);
        }

        return String.copyValueOf(cryptArray);*/
    }

    public static String getSHAValue(String text)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance( "SHA-512" );
            digest.update(text.getBytes());
            Formatter formatter = new Formatter();
            for(byte b: digest.digest())
            {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
        catch (NoSuchAlgorithmException ex)
        {
            ex.printStackTrace();
        }
        return "";
    }
}
