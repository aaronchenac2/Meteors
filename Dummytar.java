package pckg;

import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;


public class Dummytar
{
    private double x;

    private double y;
    
    private int lives = 5;

    private Image image;


    public Dummytar( URL file, int x, int y )
    {
        initCraft( file, x, y );
    }


    private void initCraft( URL file, int x, int y )
    {
        ImageIcon ii = new ImageIcon( file );
        image = ii.getImage();
        this.x = x;
        this.y = y;
    }

    public void setLives( int l )
    {
        lives = l;
    }

    public int getLives()
    {
        return lives;
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
