package teamfmg.hangman;

/**
 * Object to store ach
 * Created by Ludwig on 2/18/16.
 */
public class Achievement
{
    /**
     * ID of the achievement.
     */
    private int id;
    /**
     * Header of the achievement.
     */
    private String header;
    /**
     * Description of the achievement.
     */
    private String description;

    /**
     * Generates a new Achievement.
     * @param id ID to set.
     * @param header as String.
     * @param description as String.
     */
    public Achievement(int id, String header, String description)
    {
        this.id = id;
        this.header = header;
        this.description = description;
    }

    /**
     * Gets the id of the achievement.
     * @return Integer.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets die ID of this Achievement.
     * @param id Integer.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Gets the header of the achievement.
     * @return {@link String}
     */
    public String getHeader()
    {
        return header;
    }

    /**
     * Sets the header of the achievement.
     * @param header {@link String}
     */
    public void setHeader(String header)
    {
        this.header = header;
    }

    /**
     * Gets the description of the achievement.
     * @return String
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description of an achievement.
     * @param description String.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
}
