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
    
    public int menuMove(int width, int height, int angle, int speed)
    {
		x += speed * Math.cos(Math.toRadians(angle));
		y += speed * Math.sin(Math.toRadians(angle));
		if (y > height * 4 / 7 + height / 15 && x < width / 2) {
			angle = 0;
		}
		else if (y >= height * 4 / 7 + height / 15 && x > width * 3 / 4) {
			angle = 270;
		}
		else if (y < 0)
		{
			x = 10;
			y = 10;
			angle = 90;
		}
		return angle;
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
