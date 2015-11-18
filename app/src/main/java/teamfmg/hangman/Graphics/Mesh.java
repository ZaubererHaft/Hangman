package teamfmg.hangman.Graphics;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Created by Ludwig on 17.11.2015.
 * @since 0.5
 */
public abstract class Mesh extends Component implements IDrawable
{

    private int positionHandle;
    private int colorHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private int program;

    private FloatBuffer vertexBuffer;

    // number of coordinates per vertex in this array
    private static final int COORDS_PER_VERTEX = 3;

    private float[] coordinates = null;
    private Vector3[] vectors = null;

    // Use to access and set the view transformation
    private int MVPMatrixHandle;

    /**
     * Sets the coordinates of the vertices.
     * @param coords
     */
    public void setVertices(Vector3[] coords)
    {
        this.vectors = coords;
        this.updateVertices();
    }

    public void setVertices(float[] f){
        this.coordinates = f;
    }

    public void updateVertices()
    {
        //we need to split vector into a row of floats
        ArrayList<Float> al = new ArrayList<>();

        for (int i = 0; i <  vectors.length; i++)
        {
            al.add(vectors[i].getX() * (this.scale.getX()) +
                    (this.position.getX() / Vector3.TRANSLATE_FACTOR));

            al.add(vectors[i].getY() * (this.scale.getY()) +
                    (this.position.getY() / Vector3.TRANSLATE_FACTOR));

            al.add(vectors[i].getZ() * (this.scale.getZ()) +
                    (this.position.getZ() / Vector3.TRANSLATE_FACTOR));
        }

        float[] array = new float[al.size()];

        //add values into array
        for (int i = 0; i < array.length; i++)
        {
            array[i] = al.get(i);
        }

        this.coordinates = array;

        //regenerates this mesh
        this.generate();
    }


    /**
     * Creates a new Mesh
     */
    public Mesh()
    {

    }

    public void generate()
    {
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
    public void draw(float[] mvpMatrix)
    {
        //update vertices on draw
        this.updateVertices();

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

        // get handle to shape's transformation matrix
        this.MVPMatrixHandle = GLES20.glGetUniformLocation(this.program, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(this.MVPMatrixHandle, 1, false, mvpMatrix, 0);

        //setting color
        this.colorHandle = GLES20.glGetUniformLocation(this.program, "vColor");
        GLES20.glUniform4fv(this.colorHandle, 1, this.color.getColorArray(), 0);

        // Draw the vertices
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(this.positionHandle);
    }
}
