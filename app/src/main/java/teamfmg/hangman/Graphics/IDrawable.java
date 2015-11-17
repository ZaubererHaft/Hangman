package teamfmg.hangman.Graphics;

/**
 * Created by consult on 17.11.2015.
 */
public interface IDrawable
{
    String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "void main() {" +
                    "  gl_Position = vPosition;" +
                    "}";

    String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "  gl_FragColor = vColor;" +
                    "}";
    void draw();
}
