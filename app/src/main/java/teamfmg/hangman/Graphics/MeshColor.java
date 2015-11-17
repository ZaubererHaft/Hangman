package teamfmg.hangman.Graphics;

import teamfmg.hangman.Logger;

/**
 * Sets a mesh color.
 * Created by Ludwig on 17.11.2015.
 */
public class MeshColor
{
    /**
     * Color variables.
     */
    private float r,g,b,a;

    public MeshColor()
    {
        final int defValue = 1;

        this.setR(defValue);
        this.setA(defValue);
        this.setB(defValue);
        this.setG(defValue);
    }

    public MeshColor(float r, float g, float b, float a)
    {
        this.setR(r);
        this.setG(g);
        this.setB(b);
        this.setA(a);
    }

    public float[] getColorArray()
    {
        float[] f =
        {
            this.getR(),
            this.getB(),
            this.getB(),
            this.getA()
        };

        return f;
    }

    public final float getR()
    {
        return r;
    }

    public final void setR(float r)
    {
        if(r >= 0 && r <= 1)
        {
            this.r = r;
        }
        else
        {
            Logger.logOnlyError("Value(r) must be between 0 and 1");
        }
    }

    public final float getG()
    {
        return g;
    }

    public final void setG(float g)
    {
        if(g >= 0 && g <= 1)
        {
            this.g = g;
        }
        else
        {
            Logger.logOnlyError("Value(g) must be between 0 and 1");
        }
    }

    public final float getB()
    {
        return b;
    }

    public final void setB(float b)
    {
        if(b >= 0 && b <= 1)
        {
            this.b = b;
        }
        else
        {
            Logger.logOnlyError("Value(b) must be between 0 and 1");
        }
    }

    public final float getA()
    {
        return a;
    }

    public final void setA(float a)
    {
        if(a >= 0 && a <= 1)
        {
            this.a = a;
        }
        else
        {
            Logger.logOnlyError("Value(a) must be between 0 and 1");
        }
    }

    public static MeshColor BLACK  = new MeshColor(0,0,0,1);
    public static MeshColor RED    = new MeshColor(1,0,0,1);
    public static MeshColor GREEN  = new MeshColor(0,1,0,1);
    public static MeshColor BLUE   = new MeshColor(0,0,1,1);
    public static MeshColor WHITE  = new MeshColor(1,1,1,1);
}
