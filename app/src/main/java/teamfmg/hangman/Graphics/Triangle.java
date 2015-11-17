package teamfmg.hangman.Graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle extends Mesh
{

    public Triangle()
    {
        super();
        this.setVertexCoordinates();
        this.setColor(1,0,0,1f);
        this.generate();
    }

    private float triangleCoords[] =
    {   // in counterclockwise order:
        0.0f,  -0.5f, 0.0f, // top
        -1f, -1, 0.0f, // bottom left
        0.0f, -1f, 0.0f  // bottom right
    };

    @Override
    protected void setVertexCoordinates()
    {
        this.setCoordinates(this.triangleCoords);
    }
}