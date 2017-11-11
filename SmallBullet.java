package pckg;

public class SmallBullet extends Bullets
{   
    public static int SBCD = 5000; //5000
    public static int SBHP = 2;
    public int hp = SBHP;

    public SmallBullet()
    {
        super( "bigBullet.png" );
        CD = 0;
        speed = 20;
    }
    
    public void setHP (int h)
    {
        hp = h;
    }
    
    public int getHP()
    {
        return hp;
    }
    
    public void move()
    {
        x += speed * Math.cos( angle );
        y += speed * Math.sin( angle );
    }
}
