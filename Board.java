package platform;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.Timer;


public class Board extends JPanel implements ActionListener
{
    AudioClip audioClip;

    static final int WIDTH = 1680;

    static final int HEIGHT = 1050;

    static final int AVATARSIZE = 20;

    static final int NUMMET = 10;

    static final int MAXMET = 500;

    private Timer timer;

    private Avatar avatar;

    private Avatar avatar2;

    private Meteors[] meteors = new Meteors[MAXMET];

    private Package lu;

    private final int DELAY = 10;

    private int timePassed = 0;

    private int lastScore = 0;

    private int level = 0;

    private boolean immune = false;

    private boolean slowAB = true;

    private int slowed = 0;

    private boolean freezeAB = true;

    private int frozen = 0;

    private double[] velocities = new double[MAXMET];


    public Board()
    {
        initBoard();
    }
    
    private void initBoard()
    {
        ClassLoader loader = this.getClass().getClassLoader();
        
        audioClip = Applet.newAudioClip( this.getClass().getResource("starbound.wav"));
        audioClip.loop();
        
        loader.getResource("knight.png").toString();
        
        addKeyListener( new TAdapter() );
        setFocusable( true );
        setBackground( Color.BLACK );

        for ( int j = 0; j < MAXMET; j++ ) // init meteors
        {
            meteors[j] = new Meteors();
        }

        lu = new LevelUp(); // init level up

        // init avatars
        avatar = new Avatar( loader.getResource( "knight.png"),
            WIDTH / 2 - 10,
            HEIGHT / 2 );
        // avatar2 = new Avatar2( "C:\\Users\\Aaron
        // Chen\\Dropbox\\workspaceAPCS\\MVHacks/knight2.png",
        // WIDTH / 2 + 10,
        // HEIGHT / 2 );

        timer = new Timer( DELAY, this );
        timer.start();
    }
    
    @Override
    public void paintComponent( Graphics g )
    {
        super.paintComponent( g );

        doDrawing( g );

        Toolkit.getDefaultToolkit().sync();
    }


    private void doDrawing( Graphics g )
    {
        if ( !timer.isRunning() ) // end screen
        {
            String text = "You survived for " + ( timePassed / 1000 - lastScore / 1000 )
                + " seconds with " + ( NUMMET + level ) + " meteors!!";
            String text2 = "Press 0 to restart";
            String text3 = "Press 9 to continue";
            Font memeable = new Font( "Ariel", Font.BOLD, 30 );
            g.setFont( memeable );
            g.setColor( Color.RED );
            g.drawString( text, WIDTH / 2 - text.length(), HEIGHT / 2 );
            g.drawString( text2, WIDTH / 2 - text2.length(), HEIGHT / 2 + 30 );
            g.drawString( text3, WIDTH / 2 - text2.length(), HEIGHT / 2 + 60 );
        }

        Graphics2D g2d = (Graphics2D)g;

        // draw images
        g2d.drawImage( lu.getImage(), (int)lu.getX(), (int)lu.getY(), this );
        g2d.drawImage( avatar.getImage(), (int)avatar.getX(), (int)avatar.getY(), this );
        // g2d.drawImage( avatar2.getImage(), (int)avatar2.getX(),
        // (int)avatar2.getY(), this );

        // draws meteors
        for ( int j = 0; j < NUMMET + level; j++ )
        {
            Meteors work = meteors[j];
            Rectangle avatar1s = new Rectangle( AVATARSIZE * 5, AVATARSIZE * 5 );
            avatar1s.setLocation( (int)avatar.getX() - 2 * AVATARSIZE,
                (int)avatar.getY() - 2 * AVATARSIZE );
            // Rectangle avatar2s = new Rectangle( AVATARSIZE * 5, AVATARSIZE *
            // 5 );
            // avatar2s.setLocation( (int)avatar2.getX() - 2 * AVATARSIZE,
            // (int)avatar2.getY() - 2 * AVATARSIZE );
            Rectangle mtr = new Rectangle( AVATARSIZE, AVATARSIZE );
            mtr.setLocation( (int)work.getX(), (int)work.getY() );

            // relocates meteors in need
            if ( work.getSS() > 730 && ( overlaps( mtr, avatar1s ) )
                || ( ( timePassed / 1000 - lastScore / 1000 ) < 1
                    && ( overlaps( mtr, avatar1s ) ) ) )

            // while ( work.getSS() > 730 && ( overlaps( mtr, avatar1s ) ||
            // overlaps( mtr, avatar2s )) // relocates
            // meteors
            // in
            // need || overlaps( mtr, avatar2s
            // || ( ( timePassed / 1000 - lastScore / 1000 ) < 1
            // && ( overlaps( mtr, avatar1s ) || overlaps( mtr, avatar2s ) ) ) )
            {
                if ( Math.random() > .5 )
                {
                    if ( Math.random() > .5 ) // Quad 1
                    {
                        work.setX( Math.random() * WIDTH / 2 + 50 + avatar.getX() );
                        work.setY( avatar.getY() - 50 - Math.random() * HEIGHT / 2 );
                    }
                    else // Quad 2
                    {
                        work.setX( avatar.getX() - 50 - Math.random() * WIDTH / 2 );
                        work.setY( Math.random() * HEIGHT / 2 + 50 + avatar.getY() );
                    }
                }
                else
                {
                    if ( Math.random() > .5 ) // Quad 3
                    {
                        work.setX( avatar.getX() - 50 - Math.random() * WIDTH / 2 );
                        work.setY( Math.random() * HEIGHT / 2 + HEIGHT / 2 );
                    }
                    else // Quad 4
                    {
                        work.setX( Math.random() * WIDTH / 2 + 50 + avatar.getX() );
                        work.setY( Math.random() * HEIGHT / 2 + HEIGHT / 2 );
                    }
                }
                mtr.setLocation( (int)work.getX(), (int)work.getY() );
            }

            g2d.drawImage( work.getImage(), (int)work.getX(), (int)work.getY(), this );
        }
    }


    @Override
    public void actionPerformed( ActionEvent e )
    {
        timePassed += 10;
        slowed -= 10;
        frozen -= 10;
        if ( slowed <= 2000 && slowed >= 10 )
        {
            for ( int j = 0; j < NUMMET + level; j++ )
            {
                meteors[j].setVelocity( meteors[j].getVelocity() + velocities[j] / 400 );
            }
        }
        if ( frozen <= 2000 && frozen >= 10 )
        {
            for ( int j = 0; j < NUMMET + level; j++ )
            {
                meteors[j].setVelocity( meteors[j].getVelocity() + velocities[j] / 200 );
            }
        }
        avatar.move();
        // avatar2.move();

        // sets rectangles to check for collision
        Rectangle avatar1s = new Rectangle( AVATARSIZE, AVATARSIZE );
        avatar1s.setLocation( (int)avatar.getX(), (int)avatar.getY() );

        // Rectangle avatar2s = new Rectangle( AVATARSIZE, AVATARSIZE );
        // avatar2s.setLocation( (int)avatar2.getX(), (int)avatar2.getY() );

        Rectangle lus = new Rectangle( AVATARSIZE, AVATARSIZE );
        lus.setLocation( (int)lu.getX(), (int)lu.getY() );

        if ( overlaps( avatar1s, lus ) )
        {
            lu.execute( this );
        }

        for ( int j = 0; j < NUMMET + level; j++ )
        {
            Meteors m = meteors[j];
            m.move();
            m.setSS( m.getSS() - 10 );
        }
        for (int j = 0 ; j < NUMMET + level; j++)
        {
            Meteors m = meteors[j];
            Rectangle meteorss = new Rectangle( AVATARSIZE, AVATARSIZE );
            meteorss.setLocation( (int)m.getX(), (int)m.getY() );

            if ( overlaps( avatar1s, meteorss ) && !immune ) // if ( overlaps(
                                                             // avatar1s,
            // meteorss ) || overlaps(
            // avatar2s, meteorss ) )
            {
                timer.stop();
            }
        }
        repaint();
    }


    public boolean overlaps( Rectangle r, Rectangle r2 )
    {
        return r2.x < r.x + r.width && r2.x + r2.width > r.x && r2.y < r.y + r.height
            && r2.y + r2.height > r.y;
    }


    public int getLevel()
    {
        return level;
    }


    public void setLevel( int l )
    {
        level = l;
    }


    private class TAdapter extends KeyAdapter
    {
        @Override
        public void keyReleased( KeyEvent e )
        {
            int key = e.getKeyCode();

            if ( key == KeyEvent.VK_0 ) // restarts
            {
                avatar.setY( HEIGHT / 2 );
                avatar.setX( WIDTH / 2 - 10 );
                // avatar2.setY( HEIGHT / 2 );
                // avatar2.setX( WIDTH / 2 + 10 );
                for ( int j = 0; j < NUMMET + level; j++ )
                {
                    meteors[j].setVelocity( 2 );
                }
                level = 0;
                lastScore = timePassed;
                freezeAB = true;
                slowAB = true;
                timer.start();
            }

            if ( key == KeyEvent.VK_9 ) // continue
            {
                lastScore = timePassed;
                timer.start();
            }

            if ( key == KeyEvent.VK_SLASH && freezeAB)
            {
                for ( int j = 0; j < NUMMET + level; j++ )
                {
                    velocities[j] = meteors[j].getVelocity();
                }
                for ( int j = 0; j < NUMMET + level; j++ )
                {
                    meteors[j].setVelocity( 0 );
                }
                freezeAB = false;
                frozen = 3000;
            }

            if ( key == KeyEvent.VK_PERIOD && slowAB )
            {
                for ( int j = 0; j < NUMMET + level; j++ )
                {
                    velocities[j] = meteors[j].getVelocity();
                }
                for ( int j = 0; j < NUMMET + level; j++ )
                {
                    meteors[j].setVelocity( meteors[j].getVelocity() / 2);
                }
                slowAB = false;
                slowed = 5000;
            }

            avatar.keyReleased( e );
            // avatar2.keyReleased( e );
        }


        @Override
        public void keyPressed( KeyEvent e )
        {
            avatar.keyPressed( e );
            // avatar2.keyPressed( e );
        }
    }
}
