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
    private String word, category, description;
    private int id;

    /**
     * Instantiates a new word.
     * @param word String
     * @param category String
     * @param description String.
     */
    public Word(String word, String category, String description, int id)
    {
        this.setWord(word);
        this.setCategory(category);
        this.setDescription(description);
        this.setId(id);
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

    /**
     * Gets the description.
     * @return String
     */
    public String getDescription() { return description; }

    /**
     * Sets the description.
     * @param description String
     */
    public void setDescription(String description) { this.description = description;}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
