package teamfmg.hangman;

/**
 * Class to store statistics
 * Created by Ludwig on 19.02.2016.
 */
public class Statistics
{
    protected int wins, perfects, losses, correctLetters, wrongLetters, scoreHardcore, scoreSpeed;

    /**
     * Returns the statistics as list.
     * @return integer[]
     */
    public Integer[] asList()
    {
        Integer[] list = new Integer[7];

        list[0] = wins;
        list[1] = perfects;
        list[2] = losses;
        list[3] = correctLetters;
        list[4] = wrongLetters;
        list[5] = scoreHardcore;
        list[6] = scoreSpeed;

        return list;
    }
}
