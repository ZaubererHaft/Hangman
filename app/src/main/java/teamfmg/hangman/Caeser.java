package teamfmg.hangman;

/**
 * Class to encrypt strings.
 * Created by Ludwig on 14.11.2015.
 */
public class Caeser
{

    /**
     * En/decrypts a string.
     * @param text string to encrypt.
     * @param offset Offset.
     * @return encrypted/decrypted string
     * @since 0.4
     */
    public static String encrypt(String text, int offset)
    {

        if(offset > 127 || offset < 1)
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

        return String.copyValueOf(cryptArray);

    }
}
