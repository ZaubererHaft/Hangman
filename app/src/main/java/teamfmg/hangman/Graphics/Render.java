package teamfmg.hangman.Graphics;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ludwig on 16.11.2015.
 * @since 0.5
 */
public class Render implements GLSurfaceView.Renderer
{

    //shapes to draw
    private Triangle t;
    private Square s;

    //when the renderer is created.
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        t = new Triangle();
        s = new Square();
    }

    //when the sze of he renderer is changing
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        /**
         * Draw shapes
         */
        t.draw();
        s.draw();
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
