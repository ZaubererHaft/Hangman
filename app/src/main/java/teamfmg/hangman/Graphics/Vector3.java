package teamfmg.hangman.Graphics;

/**
 * Created by Ludwig on 17.11.2015.
 * @since 0.5
 */
public class Vector3
{

    /**
     * Global translate factor.
     */
    public static final float TRANSLATE_FACTOR = 100;

    /**
     * Variables.
     */
    private float x,y,z;

    /**
     * Default constructor.
     */
    public Vector3()
    {
        
    }

    public Vector3(float x, float y, float z)
    {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    public final float getX() {
        return x;
    }

    public final void setX(float x) {
        this.x = x;
    }

    public final float getY() {
        return y;
    }

    public final void setY(float y) {
        this.y = y;
    }

    public final float getZ() {
        return z;
    }

    public final void setZ(float z) {
        this.z = z;
    }
}
