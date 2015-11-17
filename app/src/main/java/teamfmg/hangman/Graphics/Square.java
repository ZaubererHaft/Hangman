package teamfmg.hangman.Graphics;

/**
 * Created by consult on 17.11.2015.
 */
public class Square extends Mesh
{

    public Square()
    {
        super();
        //this.setVertices();
        this.generate();
    }

    private float squareCoords[] =
    {   // in counterclockwise order:
            -0.5f, -0.5f,  0.0f,  // 0. left-bottom
            0.5f, -0.5f,  0.0f,  // 1. right-bottom
            -0.5f,  0.5f,  0.0f,  // 2. left-top
            0.5f,  0.5f,  0.0f   // 3. right-top
    };


}
