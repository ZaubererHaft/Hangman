package teamfmg.hangman.Graphics;

/**
 * Default class to define a triangle.
 * Created by Ludwig on 17.11.2015.
 * @since 0.5
 */
public class Triangle extends Mesh
{
    /**
     * Creates a default triangle.
     */
    public Triangle()
    {
        super();

        final Vector3[] vertices = new Vector3[3];

        vertices[0] = new Vector3 ( 1f,  0f, 0.0f); // 0. left-top
        vertices[1] = new Vector3 ( 1f, -1f, 0.0f); // 1. left-bottom
        vertices[2] = new Vector3 ( 0f, -1f, 0.0f); // 2. right-bottom

        this.setVertices(vertices);
        this.generate();
    }
}