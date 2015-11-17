package teamfmg.hangman.Graphics;

/**
 * Created by consult on 17.11.2015.
 */
public class Square extends Mesh
{

    private short drawOrder[] = { 0, 1, 2, 0, 2, 3 };

    public Square()
    {
        super();
        this.setVertexCoordinates();
        this.setColor(0,1,0,0.5f);
        this.generate();
    }

    private float squareCoords[] =
    {   // in counterclockwise order:
            -0.5f, -0.5f,  0.0f,  // 0. left-bottom
            0.5f, -0.5f,  0.0f,  // 1. right-bottom
            -0.5f,  0.5f,  0.0f,  // 2. left-top
            0.5f,  0.5f,  0.0f   // 3. right-top
    };

    @Override
    protected void setVertexCoordinates()
    {
        this.setCoordinates(this.squareCoords);
    }


}
