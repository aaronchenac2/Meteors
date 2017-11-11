package pckg;

import java.awt.Image;

import javax.swing.ImageIcon;

public class DummyBullet
{
    private double x;

    private double y;
    
    private boolean on;
   
    private Image image;
    
    public DummyBullet()
    {
        ClassLoader loader = this.getClass().getClassLoader();
        ImageIcon ii = new ImageIcon( loader.getResource( "bigBullet.png" ) );
        image = ii.getImage();
    }
    
    public void setOn (boolean b)
    {
        on = b;
    }
    
    public boolean getOn()
    {
        return on;
    }
    
    public void setX(double x)
    {
        this.x = x;
    }
    
    public void setY(double y)
    {
        this.y = y;
    }
    
    public double getX()
    {
        return x;
    }


    public double getY()
    {
        return y;
    }


    public Image getImage()
    {
        return image;
    }
}
