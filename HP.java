package pckg;

import javax.swing.ImageIcon;

public class HP extends Package
{
    public HP()
    {
        ClassLoader loader = this.getClass().getClassLoader();
        ImageIcon ii = new ImageIcon( loader.getResource( "hp.png" ));
        image = ii.getImage();
    }

    @Override
    public void execute()
    {
        
    }
}
