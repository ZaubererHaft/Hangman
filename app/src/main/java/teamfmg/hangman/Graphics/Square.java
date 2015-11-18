package teamfmg.hangman.Graphics;

/**
 * Default class to define a square.
 * Created by Ludwig on 17.11.2015.
 * @since 0.5
 * TODO: Fix position
 */
public class Square extends Mesh
{
    /**
     * Creates a default square.
     */
    public Square()
    {
        super();

        final Vector3[] vertices = new Vector3[4];

        vertices[0] = new Vector3 ( 1f, -1f, 0.0f); // 0. left-bottom
        vertices[1] = new Vector3 ( 0f, -1f, 0.0f); // 1. right-bottom
        vertices[2] = new Vector3 ( 1f,  0f, 0.0f); // 2. left-top
        vertices[3] = new Vector3 ( 0f,  0f, 0.0f); // 3. right-top

        this.setVertices(vertices);
        this.generate();
    }
}
