package teamfmg.hangman.Graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * TODO: Rename class
 * Created by consult on 16.11.2015.
 */
public class SurfaceView extends GLSurfaceView
{
    private final Renderer mRenderer;

    public SurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new teamfmg.hangman.Graphics.Renderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }
}
