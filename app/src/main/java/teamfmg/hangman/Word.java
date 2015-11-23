package teamfmg.hangman;

/**
 * TODO: Comment, categories
 * Created by Vincent on 19.11.2015.
 */
public class Word {

    /**
     * Variables (Strings).
     */
    private String word, category;

    public Word(String word, String category)
    {
        this.setWord(word);
        this.setCategory(category);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
