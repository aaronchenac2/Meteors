package pckg;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.net.*;
import java.util.Scanner;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;


public class Client extends JFrame implements WindowListener
{
    public static final int WIDTH = 1680; // Make sure to change WIDTH and
    // HEIGHT under also

    public static final int HEIGHT = 1050;

    private Socket socket;

    private PrintStream output;

    private Scanner input;


    public static void main( String args[] ) throws IOException
    {
        new Client();
    }


    public Client() throws IOException
    {
        Board b = new Board();
        add( b );

        this.addMouseListener( b );
        setSize( WIDTH, HEIGHT );
        setResizable( false );

        setTitle( "METEORS!!" );
        setLocationRelativeTo( null );
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setVisible( true );
    }


    public class Board extends JPanel implements ActionListener, MouseListener
    {
        // CHANGABLES-------------------------------------------------------------------------------
        private static final String IP = "99.52.201.167"; // local: 192.168.1.50
        // || public:
        // 99.52.201.167

        static final int WIDTH = 1680;

        static final int HEIGHT = 1050;

        private static final double MET_INIT_VEL = 3; // 8

        private static final int INITLIFE = 5;

        private static final boolean IMMUNE = false;

        private static final int LUSLEEPTIME = 20000; // 20 seconds

        private static final int HPSLEEPTIME = 5000; // 5 seconds

        private static final int AVATARMAXVEL = 8;

        // END CHANGABLES
        // --------------------------------------------------------------------------

        private static final int PORT = 7778;

        private boolean gameStarted = false;

        private Timer tm;

        private static final int DELAY = 50;

        private int highestLife = INITLIFE;

        private int highestLife2 = INITLIFE;

        private static final int AVATARSIZE = 20;

        private static final int SBSIZE = 10;

        private Avatar avatar;

        private Dummytar avatar2;

        private static final int NUMMET = WIDTH / 50;

        private static final int MAXMET = WIDTH * HEIGHT / 32 / AVATARSIZE / AVATARSIZE;

        private static final int INITPACKAGE = WIDTH / 840 * 2;

        private Meteors[] meteors = new Meteors[MAXMET];

        private int plusMets = 0;

        private int lastHit = 0;

        private int timePassed = 0;

        private static final int MAXPACKAGE = WIDTH / 840 * 20;

        private HP[] hp = new HP[MAXPACKAGE];

        private int numHP = INITPACKAGE;

        private LevelUp[] lu = new LevelUp[MAXPACKAGE];

        private int numLU = INITPACKAGE;

        private static final int MAXBULLETS = 5;

        private SmallBullet[] bullets = new SmallBullet[MAXBULLETS];

        private DummyBullet[] bullets2 = new DummyBullet[MAXBULLETS];

        private int message1;

        private int bossSpawnTime;

        //
        // private int lastScore = 0;
        //
        // private int level = 0;
        //
        // private boolean slowAB = true;
        //
        // private int slowed = 0;
        //
        // private boolean freezeAB = true;
        //
        // private int frozen = 0;
        //

        private ClassLoader loader;

        private AudioClip audioClip;


        public Board() throws IOException
        {
            loader = this.getClass().getClassLoader();
            tm = new Timer( DELAY, this );

            addKeyListener( new TAdapter() );
            setFocusable( true );

            setBackground( Color.BLACK );

            for ( int j = 0; j < MAXPACKAGE; j++ )
            {
                hp[j] = new HP();
                lu[j] = new LevelUp();
            }

            for ( int j = 0; j < MAXBULLETS; j++ )
            {
                bullets[j] = new SmallBullet();
                bullets2[j] = new DummyBullet();
            }

            startClient();
        }


        public void startClient()
        {
            try
            {
                setClient();
                setStreams();
                createAvatar();
                initMeteors();
                whilePlaying();
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }


        public void setClient() throws IOException
        {
            System.out.println( "Connecting to Server..." );
            socket = new Socket( IP, PORT );
            System.out.println( "Connected to Server!" );
        }


        private void setStreams() throws IOException
        {
            System.out.println( "Creating streams..." );
            output = new PrintStream( socket.getOutputStream() );
            output.flush();
            input = new Scanner( socket.getInputStream() );
            System.out.println( "Stream has been created!" );
        }


        public void createAvatar()
        {
            System.out.println( "Creating avatars..." );
            String avatarName = input.nextLine();
            avatar = new Avatar( loader.getResource( avatarName ), WIDTH / 2, HEIGHT / 2 );
            if ( avatarName.equals( "knight.png" ) )
            {
                avatar2 = new Dummytar( loader.getResource( "knight2.png" ),
                    WIDTH / 2,
                    HEIGHT / 2 );
                avatar.setName( 1 );
            }
            else if ( avatarName.equals( "knight2.png" ) )
            {
                avatar2 = new Dummytar( loader.getResource( "knight.png" ), WIDTH / 2, HEIGHT / 2 );
                avatar.setName( 2 );
            }
            System.out.println( "Avatars created!" );
        }


        // public void initBullets()
        // {
        // System.out.println( "Initializing bullets..." );
        //
        // for (int j = 0; j < MAXBULLETS; j++)
        // {
        // bullets[j].setName( "sb" + j );
        // }
        // System.out.println( "Initialized bullets!" );
        // }

        public void initMeteors()
        {
            System.out.println( "Initializing a long ass list of meteors..." );
            System.out.println( "" );
            if ( avatar.getName() == 1 )
            {
                for ( int j = 0; j < MAXMET; j++ )
                {
                    meteors[j] = new Meteors();
                    Meteors work = meteors[j];
                    output.println( "initMeteors" );
                    output.flush();
                    for ( int i = 0; i < 3; i++ )
                    {
                        String message = input.nextLine();
                        System.out.println( message );
                        if ( message.substring( 0, 1 ).equals( "a" ) )
                        {
                            work.setAngle( Double.parseDouble( message.substring( 1 ) ) );
                        }
                        if ( message.substring( 0, 1 ).equals( "x" ) )
                        {
                            work.setX( Double.parseDouble( message.substring( 1 ) ) );
                        }
                        if ( message.substring( 0, 1 ).equals( "y" ) )
                        {
                            work.setY( Double.parseDouble( message.substring( 1 ) ) );
                        }
                    }
                    work.setVelocity( MET_INIT_VEL );
                }
                output.println( "finished" );
                output.flush();
                System.out.println( "" );
                System.out.println( "Initialized first long ass list of meteors!!!" );

                System.out.println( "Now initializing packages..." );
                // Init Packages
                for ( int j = 0; j < MAXPACKAGE; j++ )
                {
                    Double x = Math.random() * ( WIDTH - 2 * AVATARSIZE );
                    Double y = Math.random() * ( HEIGHT - 2 * AVATARSIZE );
                    hp[j].setName( "hp" + j );
                    hp[j].setX( x );
                    hp[j].setY( y );
                    updatePackage( hp[j].getName(), x, y, 0 );

                    x = Math.random() * ( WIDTH - 2 * AVATARSIZE );
                    y = Math.random() * ( HEIGHT - 2 * AVATARSIZE );
                    lu[j].setName( "lu" + j );
                    lu[j].setX( x );
                    lu[j].setY( y );
                    updatePackage( lu[j].getName(), x, y, 0 );
                }
                System.out.println( "Successfully initialized packages!!!" );
            }
            else
            {
                do
                {
                    for ( int j = 0; j < MAXMET; j++ )
                    {
                        meteors[j] = new Meteors();
                        Meteors work = meteors[j];
                        for ( int i = 0; i < 3; i++ )
                        {
                            Double message = input.nextDouble();
                            System.out.println( message );
                            if ( i == 0 )
                            {
                                work.setAngle( message );
                            }
                            else if ( i == 1 )
                            {
                                work.setX( message );
                            }
                            else if ( i == 2 )
                            {
                                work.setY( message );
                            }
                        }
                        work.setVelocity( MET_INIT_VEL );
                    }
                } while ( input.hasNextDouble() );
                System.out.println( "" );
                System.out.println( "Initialized second long ass list of meteors!!!" );

                System.out.println( "Start Problem 1" );
                tm.start();
                gameStarted = true;
                // audioClip = Applet.newAudioClip( this.getClass().getResource(
                // "starbound.wav" ) );
                // audioClip.loop();
            }
        }


        public void whilePlaying()
        {
            ListenThread lt = new ListenThread();
            lt.start();
        }


        public void paintComponent( Graphics g )
        {
            super.paintComponent( g );
            doDrawing( g );
            Toolkit.getDefaultToolkit().sync();
        }


        private synchronized void doDrawing( Graphics g )
        {
            Graphics2D g2d = (Graphics2D)g;

            // Waiting screen for P1
            if ( avatar.getName() == 1 && timePassed == 0 )
            {
                Font memeable = new Font( "Ariel", Font.BOLD, 30 );
                g2d.setFont( memeable );
                g2d.setColor( Color.RED );
                g2d.drawString( "Waiting for Client 2 Connection...", 10, HEIGHT - 50 );
            }

            // timer
            if ( tm.isRunning() )
            {
                Font memeable = new Font( "Ariel", Font.BOLD, 30 );
                g2d.setFont( memeable );
                g2d.setColor( Color.RED );
                if ( (int)( 1.0 * timePassed / 1000 % 60 ) < 10 )
                {
                    g2d.drawString( Integer.toString( (int)( 1.0 * timePassed / 1000 / 60 ) ) + ":0"
                        + Integer.toString( (int)( 1.0 * timePassed / 1000 % 60 ) ), 50, 50 );
                }
                else
                {
                    g2d.drawString( Integer.toString( (int)( 1.0 * timePassed / 1000 / 60 ) ) + ":"
                        + Integer.toString( (int)( 1.0 * timePassed / 1000 % 60 ) ), 50, 50 );
                }
            }

            if ( NUMMET + plusMets == MAXMET - 1 )
            {
                message1 = timePassed;
                bossSpawnTime = timePassed + 60000; // 1 min
            }

            if ( NUMMET + plusMets == MAXMET && timePassed - message1 < 10000 )
            {
                Font memeable = new Font( "Ariel", Font.BOLD, 20 );
                String text = "MAX METEORS REACHED!! YOU CAN NOW PERMANENTLY KILL THEM!!!";
                int size = (int)( memeable.getSize() * 1.5 );
                g2d.setFont( memeable );
                g2d.setColor( Color.RED );
                g2d.drawString( text, 0, HEIGHT - size );
            }
            if ( NUMMET + plusMets == MAXMET && timePassed - message1 >= 10000
                && timePassed < bossSpawnTime )
            {
                String time;
                if ( (int)( 1.0 * bossSpawnTime / 1000 % 60 ) < 10 )
                {
                    time = Integer.toString( (int)( 1.0 * bossSpawnTime / 1000 / 60 ) ) + ":0"
                        + Integer.toString( (int)( 1.0 * bossSpawnTime / 1000 % 60 ) );
                }
                else
                {
                    time = Integer.toString( (int)( 1.0 * bossSpawnTime / 1000 / 60 ) ) + ":"
                        + Integer.toString( (int)( 1.0 * bossSpawnTime / 1000 % 60 ) );
                }
                Font memeable = new Font( "Ariel", Font.BOLD, 20 );
                String text = "THE BOSS WILL SPAWN IN 50 SECONDS!!!! " + "(" + time + ")";
                int size = (int)( memeable.getSize() * 1.5 );
                g2d.setFont( memeable );
                g2d.setColor( Color.RED );
                g2d.drawString( text, 0, HEIGHT - size );
            }

            // draws bullets
            for ( int j = 0; j < MAXBULLETS; j++ )
            {
                SmallBullet work = bullets[j];
                if ( work.isInAir() )
                {
                    g2d.drawImage( work.getImage(), (int)work.getX(), (int)work.getY(), this );
                }
                DummyBullet work2 = bullets2[j];
                if ( work2.getOn() )
                {
                    g2d.drawImage( work2.getImage(), (int)work2.getX(), (int)work2.getY(), this );
                }
            }

            // Only draw if player is alive
            if ( avatar.getLives() > 0 )
            {
                g2d.drawImage( avatar.getImage(), (int)avatar.getX(), (int)avatar.getY(), this );
                drawHP( g, avatar );
            }
            if ( avatar2.getLives() > 0 )
            {
                g2d.drawImage( avatar2.getImage(), (int)avatar2.getX(), (int)avatar2.getY(), this );
                drawHP( g, avatar2 );
            }

            // Draws meteors
            for ( int j = 0; j < NUMMET + plusMets; j++ )
            {
                Meteors work = meteors[j];
                if ( avatar.getName() == 1 && work.getEnabled() )
                {
                    Rectangle avatar1s = new Rectangle( AVATARSIZE * 3, AVATARSIZE * 3 );
                    if ( avatar.getLives() > 0 )
                    {
                        avatar1s.setLocation( (int)avatar.getX() - AVATARSIZE,
                            (int)avatar.getY() - AVATARSIZE );
                    }
                    else
                    {
                        avatar1s.setLocation( WIDTH + 100, HEIGHT + 100 );
                    }

                    Rectangle avatar2s = new Rectangle( AVATARSIZE * 3, AVATARSIZE * 3 );
                    if ( avatar.getLives() > 0 )
                    {
                        avatar2s.setLocation( (int)avatar2.getX() - AVATARSIZE,
                            (int)avatar2.getY() - AVATARSIZE );
                    }
                    else
                    {
                        avatar2s.setLocation( WIDTH + 100, HEIGHT + 100 );
                    }

                    Rectangle mtr = new Rectangle( AVATARSIZE, AVATARSIZE );
                    mtr.setLocation( (int)work.getX(), (int)work.getY() );

                    // relocates meteors in need
                    if ( ( work.getSS() > work.getSSS() - 50
                        && ( overlaps( mtr, avatar1s ) || overlaps( mtr, avatar2s ) ) )
                        || ( timePassed < 100 && overlaps( mtr, avatar1s ) ) )
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
                        if ( timePassed < 100 && gameStarted )
                        {
                            System.out.println( "Start Problem 2" );
                            tm.start();
                            output.println( "unpause" );
                        }
                    }
                }
                if ( work.getEnabled() )
                {
                    g2d.drawImage( work.getImage(), (int)work.getX(), (int)work.getY(), this );
                }
            }

            // Draws packages
            for ( int j = 0; j < numHP; j++ )
            {
                HP work = (HP)hp[j];
                if ( work.getSleep() <= 0 )
                {
                    g2d.drawImage( work.getImage(), (int)work.getX(), (int)work.getY(), this );
                }
            }

            for ( int j = 0; j < numLU; j++ )
            {
                LevelUp work = (LevelUp)lu[j];
                if ( work.getSleep() <= 0 )
                {
                    g2d.drawImage( work.getImage(), (int)work.getX(), (int)work.getY(), this );
                }
            }

        }


        public void drawHP( Graphics g, Avatar a )
        {
            g.setColor( Color.GREEN );
            int lives = a.getLives();
            if ( lives > highestLife )
            {
                highestLife = lives;
            }
            g.fillRect( (int)a.getX(),
                (int)a.getY() - 5,
                (int)( AVATARSIZE * 2 * ( 1.0 * lives / highestLife ) ),
                AVATARSIZE / 3 );

            g.setColor( Color.RED );
            g.fillRect( (int)( a.getX() + AVATARSIZE * 2 * ( 1.0 * lives / highestLife ) ),
                (int)a.getY() - 5,
                (int)( AVATARSIZE * 2 * ( 1 - 1.0 * lives / highestLife ) ),
                AVATARSIZE / 3 );
        }


        public void drawHP( Graphics g, Dummytar a )
        {
            g.setColor( Color.GREEN );
            int lives = a.getLives();
            if ( lives > highestLife2 )
            {
                highestLife2 = lives;
            }
            g.fillRect( (int)a.getX(),
                (int)a.getY() - 5,
                (int)( AVATARSIZE * 2 * ( 1.0 * lives / highestLife2 ) ),
                AVATARSIZE / 3 );

            g.setColor( Color.RED );
            g.fillRect( (int)( a.getX() + AVATARSIZE * 2 * ( 1.0 * lives / highestLife2 ) ),
                (int)a.getY() - 5,
                (int)( AVATARSIZE * 2 * ( 1 - 1.0 * lives / highestLife2 ) ),
                AVATARSIZE / 3 );
        }


        private class TAdapter extends KeyAdapter
        {
            @Override
            public void keyPressed( KeyEvent e )
            {
                int key = e.getKeyCode();

                if ( gameStarted )
                {
                    if ( key == KeyEvent.VK_ESCAPE )
                    {
                        try
                        {
                            output.println( "quit" );
                            socket.close();
                            output.close();
                            input.close();
                            setVisible( false );
                            dispose();
                        }
                        catch ( IOException e1 )
                        {
                            e1.printStackTrace();
                        }

                    }
                    if ( key == KeyEvent.VK_P ) // pauses
                    {
                        if ( tm.isRunning() )
                        {
                            output.println( "pause" );
                            tm.stop();
                        }
                        else
                        {
                            System.out.println( "Start Problem 3" );
                            output.println( "unpause" );
                            output.flush();
                            tm.start();
                        }
                    }

                    else if ( key == KeyEvent.VK_R )
                    {
                        if ( avatar.getLives() > 1 && avatar2.getLives() <= 0 )
                        {
                            int lifeSacrificed = avatar.getLives() / 2;
                            avatar.setLives( avatar.getLives() - lifeSacrificed );
                            avatar2.setLives( lifeSacrificed );
                            avatar2.setX( WIDTH / 2 );
                            avatar2.setY( HEIGHT / 2 );
                            for ( int j = 0; j < lifeSacrificed; j++ )
                            {
                                output.println( "hitMet" );
                            }
                            output.println( "revive" );
                            output.flush();
                            output.println( lifeSacrificed );
                            output.flush();
                        }
                    }

                    else if ( key == KeyEvent.VK_0 ) // restarts
                    {
                        numHP = INITPACKAGE;
                        numLU = INITPACKAGE;
                        timePassed = 0;
                        lastHit = 0;
                        avatar.setVel( Avatar.INITVEL );
                        avatar.setLives( INITLIFE );
                        avatar2.setLives( INITLIFE );
                        highestLife = INITLIFE;
                        highestLife2 = INITLIFE;
                        avatar.setY( HEIGHT / 2 );
                        avatar.setX( WIDTH / 2 );
                        plusMets = 0;
                        for ( int j = 0; j < NUMMET + plusMets; j++ )
                        {
                            meteors[j].setVelocity( MET_INIT_VEL );
                        }
                        System.out.println( "Start Problem 4" );
                        tm.start();
                        output.println( "restart" );
                        output.flush();
                    }

                    avatar.keyPressed( e );
                }
            }


            @Override
            public void keyReleased( KeyEvent e )
            {
                avatar.keyReleased( e );
            }
        }


        @Override
        public synchronized void actionPerformed( ActionEvent e ) // timer
        {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
            {
                @Override
                protected Void doInBackground() throws Exception
                {
                    timePassed += DELAY;

                    // bullet calculations
                    for ( int j = 0; j < MAXBULLETS; j++ )
                    {
                        SmallBullet sb = bullets[j];

                        // bullet hit walls
                        if ( sb.getX() > WIDTH || sb.getX() < 0 || sb.getY() > HEIGHT
                            || sb.getY() < 0 )
                        {
                            if ( sb.isInAir() )
                            {
                                sb.setCD( sb.SBCD );
                                sb.setHP( sb.SBHP );
                                updateBullet( j, 5000, 5000, false );
                            }
                            sb.setInAir( false );
                        }

                        // bullet mechanics
                        if ( sb.isInAir() && sb.getCD() <= 0 )
                        {
                            sb.move();
                            updateBullet( j, sb.getX(), sb.getY(), true );
                        }
                        else
                        {
                            sb.setCD( sb.getCD() - DELAY );
                        }
                    }

                    if ( avatar.getName() == 1 && timePassed % 1000 == 0 )
                    {
                        if ( NUMMET + plusMets < MAXMET )
                        {
                            plusMets += 1;
                        }
                        if ( plusMets % 10 == 0 && numHP < MAXPACKAGE )
                        {
                            numHP++;
                            output.println( "hpCountUp" );
                        }
                        output.println( "metUp" );
                        output.flush();
                        output.println( plusMets );
                        output.flush();
                    }

                    if ( avatar.getX() > WIDTH )
                    {
                        avatar.setX( 0 );
                    }
                    else if ( avatar.getX() < 0 )
                    {
                        avatar.setX( WIDTH );
                    }
                    else if ( avatar.getY() > HEIGHT )
                    {
                        avatar.setY( 0 );
                    }
                    else if ( avatar.getY() < 0 )
                    {
                        avatar.setY( HEIGHT );
                    }

                    // Moves avatar
                    output.println( "x" + avatar.getX() );
                    output.flush();
                    output.println( "y" + avatar.getY() );
                    output.flush();
                    avatar.move();

                    // Sets rectangles to check for collision
                    Rectangle avatar1s = new Rectangle( AVATARSIZE, AVATARSIZE );
                    avatar1s.setLocation( (int)avatar.getX(), (int)avatar.getY() );

                    // Checks package hits
                    for ( int j = 0; j < numHP; j++ )
                    {
                        HP work = hp[j];
                        work.setSleep( work.getSleep() - DELAY );
                        Rectangle hps = new Rectangle( AVATARSIZE, AVATARSIZE );
                        hps.setLocation( (int)work.getX(), (int)work.getY() );
                        if ( overlaps( avatar1s, hps ) )
                        {
                            avatar.setLives( avatar.getLives() + 1 );
                            output.println( "gain" );
                            work.setSleep( HPSLEEPTIME );

                            Double x = Math.random() * ( WIDTH - 2 * AVATARSIZE );
                            Double y = Math.random() * ( HEIGHT - 2 * AVATARSIZE );
                            int sleep = HPSLEEPTIME;
                            work.setX( x );
                            work.setY( y );
                            updatePackage( work.getName(), x, y, sleep );
                        }
                    }

                    for ( int j = 0; j < numLU; j++ )
                    {
                        LevelUp work = lu[j];
                        work.setSleep( work.getSleep() - DELAY );
                        Rectangle lus = new Rectangle( AVATARSIZE, AVATARSIZE );
                        lus.setLocation( (int)work.getX(), (int)work.getY() );
                        if ( overlaps( avatar1s, lus ) )
                        {
                            levelUp();
                            work.setSleep( LUSLEEPTIME );

                            Double x = Math.random() * ( WIDTH - 2 * AVATARSIZE );
                            Double y = Math.random() * ( HEIGHT - 2 * AVATARSIZE );
                            int sleep = LUSLEEPTIME;
                            work.setX( x );
                            work.setY( y );
                            updatePackage( work.getName(), x, y, sleep );
                        }
                    }

                    // Client 1 meteor management
                    for ( int j = 0; j < NUMMET + plusMets; j++ )
                    {
                        Meteors m = meteors[j];
                        if ( avatar.getName() == 1 )
                        {
                            m.move();
                        }
                        if ( avatar.getName() == 1 && m.getX() > WIDTH || m.getX() < 0
                            || m.getY() > HEIGHT || m.getY() < 0 )
                        {
                            double x = Math.random() * WIDTH;
                            double y = Math.random() * HEIGHT;
                            m.setX( x );
                            m.setY( y );
                        }
                        m.setSS( m.getSS() - 10 );
                    }

                    // Updating client 2 meteors
                    if ( avatar.getName() == 1 && timePassed % ( DELAY ) == 0 )
                    {
                        for ( int j = 0; j < NUMMET + plusMets; j++ )
                        {
                            Meteors m = meteors[j];
                            int meteorID = j;
                            double x = m.getX();
                            double y = m.getY();
                            output.println( "updateMeteor" );
                            output.flush();
                            output.println( meteorID );
                            output.flush();
                            output.println( x );
                            output.flush();
                            output.println( y );
                            output.flush();
                            output.println( m.getEnabled() );
                            output.flush();
                        }
                    }

                    for ( int j = 0; j < NUMMET + plusMets; j++ )
                    {
                        Meteors m = meteors[j];
                        Rectangle meteorss = new Rectangle( AVATARSIZE, AVATARSIZE );
                        meteorss.setLocation( (int)m.getX(), (int)m.getY() );

                        if ( m.getEnabled() )
                        {
                            // BULLET HITS METEOR
                            for ( int i = 0; i < MAXBULLETS; i++ )
                            {
                                SmallBullet sb = bullets[i];
                                Rectangle sbs = new Rectangle( SBSIZE, SBSIZE );
                                sbs.setLocation( (int)sb.getX() + SBSIZE, (int)sb.getY() + SBSIZE );

                                if ( overlaps( sbs, meteorss ) )
                                {
                                    if ( NUMMET + plusMets == MAXMET )
                                    {
                                        System.out.println( "Event Started" );
                                        m.setEnabled( false );
                                    }
                                    sb.setHP( sb.getHP() - 1 );
                                    if ( sb.getHP() == 0 )
                                    {
                                        sb.setInAir( false );
                                        sb.setHP( sb.SBHP );
                                        sb.setCD( SmallBullet.SBCD );
                                        updateBullet( i, 5000, 5000, false );
                                    }
                                    double x = Math.random() * WIDTH;
                                    double y = Math.random() * HEIGHT;
                                    m.setX( x );
                                    m.setY( y );
                                    if ( avatar.getName() == 2 )
                                    {
                                        int meteorID = j;
                                        output.println( "updateMeteor" );
                                        output.flush();
                                        output.println( meteorID );
                                        output.flush();
                                        output.println( x );
                                        output.flush();
                                        output.println( y );
                                        output.flush();
                                        output.println( m.getEnabled() );
                                        output.flush();
                                    }
                                }

                            }

                            // GETS HIT BY METEOR
                            if ( ( overlaps( avatar1s, meteorss ) ) && !IMMUNE )
                            {
                                if ( timePassed - lastHit > DELAY + 1l )
                                {
                                    avatar.setLives( avatar.getLives() - 1 );
                                }
                                if ( avatar.getLives() <= 0 && avatar2.getLives() <= 0 )
                                {
                                    output.println( "tookTheL" );
                                    tm.stop();
                                }
                                else
                                {
                                    int meteorID = j;
                                    double x = Math.random() * WIDTH;
                                    double y = Math.random() * HEIGHT;
                                    m.setSS( m.getSSS() );
                                    m.setX( x );
                                    m.setY( y );
                                    if ( avatar.getName() == 2 )
                                    {
                                        output.println( "updateMeteor" );
                                        output.flush();
                                        output.println( meteorID );
                                        output.flush();
                                        output.println( x );
                                        output.flush();
                                        output.println( y );
                                        output.flush();
                                        output.println( m.getEnabled() );
                                        output.flush();
                                    }
                                    output.println( "hitMet" );
                                    output.flush();
                                }
                                lastHit = timePassed;
                            }
                        }
                    }
                    return null;
                }


                @Override
                protected void done()
                {
                    repaint();
                }
            };
            worker.execute();
        }


        public class ListenThread extends Thread // listen for message from
                                                 // server
        {
            public synchronized void run()
            {
                while ( true )
                {
                    String message = input.nextLine();
                    if ( message.length() > 0 && message.substring( 0, 1 ).equals( "x" ) )
                    {
                        double work = Double.parseDouble( message.substring( 1 ) );
                        avatar2.setX( work );
                    }
                    else if ( message.length() > 0 && message.substring( 0, 1 ).equals( "y" ) )
                    {
                        double work = Double.parseDouble( message.substring( 1 ) );
                        avatar2.setY( work );
                    }
                    else if ( message.equals( "go" ) ) // exclusive to client 1
                    {
                        System.out.println( "Start Problem 5" );
                        tm.start();
                        gameStarted = true;
                        // audioClip = Applet
                        // .newAudioClip( this.getClass().getResource(
                        // "starbound.wav" ) );
                        // audioClip.loop();
                    }
                    else if ( message.equals( "pause" ) )
                    {
                        tm.stop();
                    }
                    else if ( message.equals( "unpause" ) )
                    {
                        System.out.println( "Start Problem 6" );
                        tm.start();
                    }
                    else if ( message.equals( "tookTheL" ) )
                    {
                        avatar.setX( 5000 );
                        avatar.setY( 5000 );
                        avatar2.setX( 5000 );
                        avatar2.setY( 5000 );
                        tm.stop();
                    }
                    else if ( message.equals( "hitMet" ) )
                    {
                        avatar2.setLives( avatar2.getLives() - 1 );
                    }
                    else if ( message.equals( "revive" ) )
                    {
                        avatar.setLives( input.nextInt() );
                        avatar.setX( avatar2.getX() );
                        avatar.setY( avatar2.getY() );
                    }
                    else if ( message.equals( "gain" ) )
                    {
                        avatar2.setLives( avatar2.getLives() + 1 );
                    }
                    else if ( message.equals( "metUp" ) )
                    {
                        plusMets = input.nextInt();
                    }
                    else if ( message.equals( "levelUp" ) )
                    {
                        avatar.setVel( avatar.getVel() + 1 );
                    }
                    else if ( message.equals( "hpCountUp" ) )
                    {
                        numHP++;
                    }
                    else if ( message.equals( "updateMeteor" ) )
                    {
                        int id = input.nextInt();
                        double x = input.nextDouble();
                        double y = input.nextDouble();
                        boolean enabled = input.nextBoolean();
                        Meteors m = meteors[id];
                        m.setX( x );
                        m.setY( y );
                        m.setEnabled( enabled );
                    }
                    else if ( message.equals( "updatePackage" ) )
                    {
                        String name = input.nextLine();
                        if ( name.length() > 0 && name.substring( 0, 2 ).equals( "hp" ) )
                        {
                            int num = Integer.parseInt( name.substring( 2 ) );
                            hp[num].setName( name );
                            hp[num].setX( input.nextDouble() );
                            hp[num].setY( input.nextDouble() );
                            hp[num].setSleep( input.nextInt() );
                        }
                        else if ( name.length() > 0 && name.substring( 0, 2 ).equals( "lu" ) )
                        {
                            int num = Integer.parseInt( name.substring( 2 ) );
                            lu[num].setName( name );
                            lu[num].setX( input.nextDouble() );
                            lu[num].setY( input.nextDouble() );
                            lu[num].setSleep( input.nextInt() );
                        }
                        else
                        {
                            input.nextDouble();
                            input.nextDouble();
                            input.nextInt();
                        }
                    }
                    else if ( message.equals( "updateBullet" ) )
                    {
                        int name = input.nextInt();
                        bullets2[name].setX( input.nextDouble() );
                        bullets2[name].setY( input.nextDouble() );
                        bullets2[name].setOn( input.nextBoolean() );
                    }
                    else if ( message.equals( "restart" ) )
                    {
                        numHP = INITPACKAGE;
                        numLU = INITPACKAGE;
                        timePassed = 0;
                        lastHit = 0;
                        avatar.setVel( Avatar.INITVEL );
                        avatar.setY( HEIGHT / 2 );
                        avatar.setX( WIDTH / 2 );
                        highestLife = INITLIFE;
                        highestLife2 = INITLIFE;
                        avatar.setLives( INITLIFE );
                        avatar2.setLives( INITLIFE );
                        plusMets = 0;
                        for ( int j = 0; j < NUMMET + plusMets; j++ )
                        {
                            meteors[j].setVelocity( MET_INIT_VEL );
                        }
                        System.out.println( "Start Problem 7" );
                        tm.start();
                    }
                    else if ( message.equals( "quit" ) )
                    {
                        try
                        {
                            input.close();
                            output.close();
                            socket.close();
                            setVisible( false );
                            dispose();
                        }
                        catch ( IOException e )
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }


        public boolean overlaps( Rectangle r, Rectangle r2 )
        {
            return r2.x < r.x + r.width && r2.x + r2.width > r.x && r2.y < r.y + r.height
                && r2.y + r2.height > r.y;
        }


        public void updatePackage( String name, double x, double y, int sleep )
        {
            output.println( "updatePackage" );
            output.println( name );
            output.println( x );
            output.println( y );
            output.println( sleep );
        }


        public void updateBullet( int name, double x, double y, boolean on )
        {
            output.println( "updateBullet" );
            output.println( name );
            output.println( x );
            output.println( y );
            output.println( on );
        }


        public void levelUp()
        {
            if ( avatar.getVel() < AVATARMAXVEL )
            {
                avatar.setVel( avatar.getVel() + 1 );
                // output.println( "levelUp" );
            }
        }


        @Override
        public void mousePressed( MouseEvent e )
        {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
            {
                @Override
                protected Void doInBackground() throws Exception
                {
                    if ( SwingUtilities.isLeftMouseButton( e ) && avatar.getLives() > 0 )
                    {
                        double x = e.getX();
                        double y = e.getY();
                        double ax = avatar.getX();
                        double ay = avatar.getY();

                        for ( int j = 0; j < MAXBULLETS; j++ )
                        {
                            SmallBullet work = bullets[j];
                            if ( !work.isInAir() && work.getCD() <= 0 )
                            {
                                work.setInAir( true );
                                work.setX( ax );
                                work.setY( ay );
                                double angle = 0;
                                if ( x > ax )
                                {
                                    angle = Math.atan( ( y - ay ) / ( x - ax ) );
                                }
                                else
                                {
                                    angle = Math.atan( ( y - ay ) / ( x - ax ) ) + Math.PI;
                                }
                                work.setAngle( angle );
                                break;
                            }
                        }
                    }
                    return null;
                }
            };
            worker.execute();
        }


        @Override
        public void mouseClicked( MouseEvent e )
        {
            // TODO Auto-generated method stub

        }


        @Override
        public void mouseEntered( MouseEvent e )
        {
            // TODO Auto-generated method stub

        }


        @Override
        public void mouseExited( MouseEvent e )
        {
            // TODO Auto-generated method stub

        }


        @Override
        public void mouseReleased( MouseEvent e )
        {
            // TODO Auto-generated method stub

        }
    }


    @Override
    public void windowClosing( WindowEvent arg0 )
    {
        try
        {
            System.out.println( "Closing streams" );
            socket.close();
            output.close();
            input.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }


    @Override
    public void windowActivated( WindowEvent arg0 )
    {

    }


    @Override
    public void windowClosed( WindowEvent arg0 )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void windowDeactivated( WindowEvent arg0 )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void windowDeiconified( WindowEvent arg0 )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void windowIconified( WindowEvent arg0 )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void windowOpened( WindowEvent arg0 )
    {
        System.out.println( "hi this works" );
    }
}
