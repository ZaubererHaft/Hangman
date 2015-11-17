package teamfmg.hangman.Graphics;

public class Triangle extends Mesh
{

    private Vector3[] verticeCoords = new Vector3[3];

    public Triangle()
    {
        super();

        this.verticeCoords[0] = new Vector3(0.0f,  0.622008459f, 0.0f);
        this.verticeCoords[1] = new Vector3(-0.5f, -0.311004243f, 0.0f);
        this.verticeCoords[2] = new Vector3(0.5f, -0.311004243f, 0.0f);

        this.setVertices(verticeCoords);
        this.generate();
    }
}