package pckg;

import java.awt.Image;

import javax.swing.ImageIcon;


public class BOSS
{   
    private int size;
    
    private double vel;

    private double x;

    private double y;
    
    private int lives = - 1;

    private Image image;

    private boolean enabled = false;
    
    private int bullets;


    public BOSS( int w, int h)
    {
        size = h;
        ClassLoader loader = this.getClass().getClassLoader();
        ImageIcon ii = new ImageIcon( loader.getResource( "pusheen.png" ) ); // BOSS.png
        image = ii.getImage();
        image = image.getScaledInstance(h, h, Image.SCALE_DEFAULT);
        x = w - 10;
        y = 0;
    }
    
    public int getBullets()
    {
        return bullets;
    }
    
    public void setBullets(int b)
    {
        bullets = b;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public void move()
    {
        x -= (vel + .5);
    }
    
    public int menuMove(int width, int height, int angle, int speed)
    {
		x += speed * Math.cos(Math.toRadians(angle));
		y += speed * Math.sin(Math.toRadians(angle));
		if (y > height * 4 / 7 && x < width / 2) {
			angle = 0;
		}
		else if (y > height * 4 / 7 && x > width * 3 / 4) {
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
    
    public void setEnabled(boolean b)
    {
        enabled = b;
    }
    
    public boolean getEnabled()
    {
        return enabled;
    }
    
    public void setVel( double l )
    {
        vel = l;
    }

    public double getVel()
    {
        return vel;
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
