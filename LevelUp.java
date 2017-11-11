package pckg;

import java.net.URL;

import javax.swing.ImageIcon;


public class LevelUp extends Package
{    
    public LevelUp()
    {
        ClassLoader loader = this.getClass().getClassLoader();
        ImageIcon ii = new ImageIcon( loader.getResource( "lu.png" ) );
        image = ii.getImage();
    }

    @Override
    public void execute()
    {
    }
}
