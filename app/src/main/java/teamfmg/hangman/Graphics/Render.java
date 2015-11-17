package teamfmg.hangman.Graphics;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ludwig on 16.11.2015.
 * @since 0.5
 */
public class Render implements GLSurfaceView.Renderer
{

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];


    //shapes to draw
    private Triangle t;
    private Square s;

    //when the renderer is created.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        t = new Triangle();
        t.color = MeshColor.GREEN;
        t.position = new Vector3(0,0,0);

        s = new Square();
        s.scale.setY(0.5f);
        s.color = MeshColor.RED;
        s.position = new Vector3(50,50,50);
    }

    //when the sze of he renderer is changing
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        /**
         * Draw shapes
         */
        t.draw(mMVPMatrix);
        s.draw(mMVPMatrix);
    }

    /**
     * Helper class to load shader from the shapes.
     * @param type
     * @param shaderCode
     * @return
     */
    public static int loadShader(int type, String shaderCode)
    {
        // create shader to draw shapes
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
