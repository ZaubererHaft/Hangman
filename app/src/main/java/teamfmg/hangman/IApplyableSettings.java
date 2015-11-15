package teamfmg.hangman;

/**
 * Interface witch applies the settings to a activity. <br />
 * Created by Ludwig on 15.11.2015.
 * @since 0.4
 */
public interface IApplyableSettings
{
    /**
     * This method should be implemented to change the background color of an activity<br />
     * Add this method primarily to the constructor or if there were any changes in the settings.
     * @since 0.4
     */
    void changeBackground();
}
