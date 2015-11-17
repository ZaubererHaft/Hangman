package teamfmg.hangman.Graphics;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Creates a new openGL scene.
 * Created by Ludwig on 16.11.2015.
 */
public class Scene extends GLSurfaceView
{
    private final Renderer mRenderer;

    public Scene(Context context)
    {
        super(context);

        // Create an OpenGL ES 2.0 context
        this.setEGLContextClientVersion(2);
        this.mRenderer = new Render();
        this.setRenderer(mRenderer);
    }
}
