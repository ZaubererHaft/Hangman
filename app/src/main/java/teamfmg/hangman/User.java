package teamfmg.hangman;

/**
 *  User Object.<br />
 *  Created by Ludwig 09.11.2015.
 *  @since 0.1
 */
public class User
{
    /**
     * Variables (Strings).
     */
    private String name, password, mail;
    /**
     * ID if the users.
     */
    private final int id;

    /**
     * Creates a new user to be added at the database.
     * @param name Name
     * @param password Password
     * @param mail e mail
     * @since 0.1
     */
    public User(String name, String password, String mail, int id)
    {
        this.setName(name);
        this.setPassword(password);
        this.setMail(mail);
        this.id = id;
    }

    public int getId()
    {
        return id;
    }


    /**
     * Gets the username.
     * @return String
     * @since 0.1
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the username
     * @param name String.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the password.
     * @return String
     * @since 0.1
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password String
     * @since 0.1
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the E-Mail address.
     * @return String.
     * @since 0.1
     */
    public String getMail() {
        return mail;
    }

    /**
     * Sets the E-Mail address.
     * @param mail string
     * @since 0.1
     */
    public void setMail(String mail) {
        this.mail = mail;
    }
}
