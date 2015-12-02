package teamfmg.hangman;

/**
 * Class for custom words.
 * Created by Vincent on 23.11.2015.
 * @since 0.6
 */
public class Word
{
    /**
     * Variables (Strings).
     */
    private String word, category;

    public Word(String word, String category)
    {
        this.setWord(word);
        this.setCategory(category);
    }

    /**
     * Gets the word itself.
     * @return String
     */
    public String getWord()
    {
        return word;
    }

    /**
     * Sets the word.
     * @param word String
     */
    public void setWord(String word)
    {
        this.word = word;
    }

    /**
     * Gets the category of the word.
     * @return String
     */
    public String getCategory()
    {
        return category;
    }

    /**
     * Sets the category of the word.
     * @param category String.
     */
    public void setCategory(String category)
    {
        this.category = category;
    }
}
