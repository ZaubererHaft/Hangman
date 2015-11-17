package teamfmg.hangman.Graphics;

/**
 * Created by consult on 17.11.2015.
 */
public class Vector3
{
    /**
     * Variables.
     */
    private float x,y,z;

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
