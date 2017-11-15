package pckg;

import java.awt.Image;

public abstract class Package
{
    private double x;

    private double y;
    
    protected String name;

    protected Image image;
    
    private int sleep = 0;
    
    private boolean enabled = true;
    
    public Package()
    {
    }
    
    public void setEnabled(boolean b)
    {
        enabled = b;
    }
    
    public boolean getEnabled()
    {
        return enabled;
    }
    
    public void setSleep( int s)
    {
        sleep = s;
    }
    
    public int getSleep()
    {
        return sleep;
    }
    
    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }
    
    public void setX(double newX)
    {
        x = newX;
    }
    
    public void setY(double newY)
    {
        y = newY;
    }
    
    public void setName(String n)
    {
        name = n;
    }
    
    public String getName()
    {
        return name;
    }
    public Image getImage()
    {
        return image;
    }

    public abstract void execute();
    
}
