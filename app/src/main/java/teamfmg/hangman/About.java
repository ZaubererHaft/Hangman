package teamfmg.hangman;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import teamfmg.hangman.Graphics.Scene;
import teamfmg.hangman.Graphics.Triangle;

public class About extends Activity
{

    private GLSurfaceView glView;


    private Triangle t;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        this.glView = new Scene(this);
        this.setContentView(glView);
    }

}
