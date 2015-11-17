package teamfmg.hangman.Graphics;

/**
 * Component class of a mesh
 * Created by Ludwig on 17.11.2015.
 * @since 0.5
 */
public class Component
{
    /**
     * Position of this Component. <br />
     * Note, that there is a translate factor to make the position easier to handle- <br />
     * Normally, the coordinate system of the display is from <b>0 to 1</b>, but it should be
     * multiplied the position with the factor in <class>Vector3</class> to receive the same values.
     * <br />
     * <br />
     * Eg. positions the component at the half of the screen: position.setY(50)
     */
    public Vector3 position = new Vector3();
    /**
     * Color of the mesh. <br />
     * Use this to color the shader.
     */
    public MeshColor color = new MeshColor();
    /**
     * Scale of this component.
     */
    public Vector3 scale = new Vector3(1,1,1);

}
