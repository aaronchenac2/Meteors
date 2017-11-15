package pckg;

import java.awt.Image;

import javax.swing.ImageIcon;


public class BossBullets
{
    protected double x = 5000;

    protected double y = 5000;

    protected int vel = 10;

    protected int damage;

    protected boolean inAir = false;

    protected double angle;

    protected Image image;

    protected int size;


    public BossBullets( int h )
    {
        size = h;
        ClassLoader loader = this.getClass().getClassLoader();
        ImageIcon ii = new ImageIcon( loader.getResource( "pusheenBullet.png" ) ); // BOSS.png
        image = ii.getImage();
        image = image.getScaledInstance( h, h, Image.SCALE_DEFAULT );
    }


    public void move()
    {
        x += vel * Math.cos( angle );
        y += vel * Math.sin( angle );
    }


    public int getSize()
    {
        return size;
    }


    public void setAngle( double a )
    {
        angle = a;
    }


    public double getAngle()
    {
        return angle;
    }


    public void setInAir( boolean b )
    {
        inAir = b;
    }


    public boolean getInAir()
    {
        return inAir;
    }


    public void setDamage( int l )
    {
        damage = l;
    }


    public int getDamage()
    {
        return damage;
    }


    public void setX( double x )
    {
        this.x = x;
    }


    public void setY( double y )
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
