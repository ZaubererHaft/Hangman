package teamfmg.hangman.Graphics;

import android.graphics.Color;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Ludwig on 17.11.2015.
 * @since 0.5
 */
public abstract class Mesh implements IDrawable
{
    /**
     * Color of the Sape
     */
    private float color[] = { 1f, 0.0f, 0.0f, 1.0f };

    private int positionHandle;
    private int colorHandle;

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    private int program;

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;

    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;

    private float[] coordinates = null;

    protected abstract void setVertexCoordinates();

    /**
     * Sets the coordinates of the vertices.
     * @param coords
     */
    public void setCoordinates(float[] coords)
    {
       this.coordinates = coords;
    }

    public float[] getColor() {
        return color;
    }

    public void setColor(float r, float g, float b, float a) {

        this.color = new float[4];

        this.color[0] = r;
        this.color[1] = g;
        this.color[2] = b;
        this.color[3] = a;
    }

    /**
     * Creates a new Mesh
     */
    public Mesh()
    {

    }

    public void generate()
    {
        this.setVertexCoordinates();

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                coordinates.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        this.vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        this.vertexBuffer.put(this.coordinates);
        // set the buffer to read the first coordinate
        this.vertexBuffer.position(0);

        int vertexShader = Render.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = Render.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        this.program = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(program, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(program, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program);
    }

    @Override
    public void draw()
    {
        //amount of vertices
        final int vertexCount = coordinates.length / COORDS_PER_VERTEX;

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(this.program);

        // get handle to vertex shader's vPosition member
        this.positionHandle = GLES20.glGetAttribLocation(this.program, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(this.positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(this.positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        this.colorHandle = GLES20.glGetUniformLocation(this.program, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(this.colorHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(this.positionHandle);
    }
}
