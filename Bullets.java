package pckg;

import java.awt.Image;
import javax.swing.ImageIcon;



public class Bullets
{   
    protected double x = 5000;

    protected double y = 5000;

    protected double angle;

    protected Image image;

    protected int CD = 0;
        
    protected int speed;
    
    protected boolean inAir = false;
        
    public Bullets( String name )
    {
        ClassLoader loader = this.getClass().getClassLoader();
        ImageIcon ii = new ImageIcon( loader.getResource( name) );
        image = ii.getImage();
    }
    
    public boolean isInAir()
    {
        return inAir;
    }
    
    public void setInAir(boolean b)
    {
        inAir = b;
    }
    
    public int getSpeed()
    {
        return speed;
    }
    
    public double getX()
    {
        return x;
    }


    public double getY()
    {
        return y;
    }


    public int getCD()
    {
        return CD;
    }


    public void setCD( int cd )
    {
        CD = cd;
    }


    public void setX( double newX )
    {
        x = newX;
    }


    public void setY( double newY )
    {
        y = newY;
    }

    public void setAngle( double a )
    {
        angle = a;
    }


    public Image getImage()
    {
        return image;
    }
}
