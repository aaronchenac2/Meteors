package pckg;

import java.awt.Image;

import javax.swing.ImageIcon;


public class Meteors
{
    private double vel = 2;

    private double angle;

    private double x;

    private double y;
    
    private static final int SPAWNSLEEPS = 500;
    
    private int spawnSleep = 750;
    
    private Image image;
    
    private boolean enabled = true;


    public Meteors()
    {
        initCraft();
    }


    private void initCraft()
    {
        ClassLoader loader = this.getClass().getClassLoader();
        ImageIcon ii = new ImageIcon( loader.getResource( "meteor.png" ));
        image = ii.getImage();
    }

    public boolean getEnabled()
    {
        return enabled;
    }
    
    public void setEnabled(boolean b)
    {
        enabled = b;
    }

    public double getX()
    {
        return x;
    }
    
    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }
    
    public void setY(double y)
    {
        this.y = y;
    }
    
    public double getAngle()
    {
        return angle;
    }
    
    public void setAngle(double a)
    {
        angle = a;
    }
    
    public void setSS(int s)
    {
        spawnSleep = s;
    }
    
    public int getSS()
    {
        return spawnSleep;
    }
    
    public int getSSS()
    {
        return SPAWNSLEEPS;
    }


    public Image getImage()
    {
        return image;
    }
    
    public void setVelocity(double v)
    {
        vel = v;
    }
    
    public double getVelocity()
    {
        return vel;
    }

    public void move()
    {
        if (spawnSleep >= 0)
        {
            return;
        }
        x += vel * Math.cos( angle );
        y += vel * Math.sin( angle );
    }
}
