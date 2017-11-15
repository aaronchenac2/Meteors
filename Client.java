package pckg;

//import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class Client extends JFrame implements WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 1680 / 2; // Make sure to change WIDTH and
	// HEIGHT under also

	public static final int HEIGHT = 1050 / 2;

	private Socket socket;

	private PrintStream output;

	private Scanner input;
	
	private JTextField tf;

	 public Client(JTextField textField) throws IOException {
	 tf = textField;
	 Board b = new Board();
	 add(b);
	
	 this.addMouseListener(b);
	 setSize(WIDTH, HEIGHT);
	 setResizable(false);
	
	 setTitle("METEORS!!");
	 setLocationRelativeTo(null);
	 setVisible(true);
	 }

	public class Board extends JPanel implements ActionListener, MouseListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// CHANGABLES-------------------------------------------------------------------------------
		private static final String IP = "localhost"; // local: 192.168.1.50
		// || public:
		// 99.52.201.167

		static final int WIDTH = 1680 / 2;

		static final int HEIGHT = 1050 / 2;

		private static final double MET_INIT_VEL = 8;

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

		private static final int MAXMET = WIDTH * HEIGHT / 32 / AVATARSIZE / AVATARSIZE;

		private static final int NUMMET = MAXMET - 1;

		private static final int INITPACKAGE = WIDTH / 840 * 2;

		private Meteors[] meteors = new Meteors[MAXMET];

		private int plusMets = 0;

		private int metSurvived = 20;

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

		private int numBossBullets = 20;

		private BossBullets[] bBullets = new BossBullets[numBossBullets];

		private BOSS boss = new BOSS(WIDTH, HEIGHT);

		private static final int bossShootIntervals = 600;

		private int bossKillTime = 120000; // 60 seconds to kill boss

		private int bossMaxHP;

		private int message1;

		private int bossSpawnTime = 1000000;

		private boolean changed = false;

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
		private BufferedReader scanner;

		public Board() throws IOException {
			loader = this.getClass().getClassLoader();
			tm = new Timer(DELAY, this);

			addKeyListener(new TAdapter());
			setFocusable(true);

			setBackground(Color.BLACK);

			for (int j = 0; j < MAXPACKAGE; j++) {
				hp[j] = new HP();
				lu[j] = new LevelUp();
			}

			for (int j = 0; j < MAXBULLETS; j++) {
				bullets[j] = new SmallBullet();
				bullets2[j] = new DummyBullet();
			}

			for (int j = 0; j < numBossBullets; j++) {
				bBullets[j] = new BossBullets(HEIGHT);
			}

			startClient();
		}

		public void startClient() {
			try {
				setClient();
				setStreams();
				createAvatar();
				initMeteors();
				whilePlaying();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void setClient() throws IOException {
			System.out.println("Connecting to Server...");
			try {
				socket = new Socket(IP, PORT);
				System.out.println("Connected to Server!");
				tf.setText("Connected to Server!");
			} catch(Exception e)
			{
				tf.setText("The Server is not available");
				setVisible(false);
				dispose();
			}
		}

		private void setStreams() throws IOException {
			System.out.println("Creating streams...");
			output = new PrintStream(socket.getOutputStream());
			output.flush();
			input = new Scanner(socket.getInputStream());
			System.out.println("Stream has been created!");
		}

		public void createAvatar() {
			System.out.println("Creating avatars...");
			String avatarName = input.nextLine();
			avatar = new Avatar(loader.getResource(avatarName), WIDTH / 2, HEIGHT / 2);
			if (avatarName.equals("knight.png")) {
				avatar2 = new Dummytar(loader.getResource("knight2.png"), WIDTH / 2, HEIGHT / 2);
				avatar.setName(1);
			} else if (avatarName.equals("knight2.png")) {
				avatar2 = new Dummytar(loader.getResource("knight.png"), WIDTH / 2, HEIGHT / 2);
				avatar.setName(2);
			}
			System.out.println("Avatars in Client " + avatar.getName() + " created!");
		}

		public void initMeteors() throws IOException {
			FileReader fr = new FileReader("numbers.txt");
			scanner = new BufferedReader(fr);

			System.out.println("Initializing a long ass list of meteors...");
			for (int j = 0; j < MAXMET; j++) {
				meteors[j] = new Meteors();
				Meteors work = meteors[j];

				work.setX(Double.parseDouble(scanner.readLine()) * WIDTH);
				work.setY(Double.parseDouble(scanner.readLine()) * HEIGHT);
				work.setAngle(Double.parseDouble(scanner.readLine()) * WIDTH);
				work.setVelocity(MET_INIT_VEL);
			}
			System.out.println("Initialized the long ass list of meteors!!!");

			System.out.println("Now initializing packages...");
			for (int j = 0; j < MAXPACKAGE; j++) {
				Double x = Double.parseDouble(scanner.readLine()) * (WIDTH - 2 * AVATARSIZE);
				Double y = Double.parseDouble(scanner.readLine()) * (HEIGHT - 2 * AVATARSIZE);
				hp[j].setName("hp" + j);
				hp[j].setX(x);
				hp[j].setY(y);
				updatePackage(hp[j].getName(), x, y, 0);

				x = Double.parseDouble(scanner.readLine()) * (WIDTH - 2 * AVATARSIZE);
				y = Double.parseDouble(scanner.readLine()) * (HEIGHT - 2 * AVATARSIZE);
				lu[j].setName("lu" + j);
				lu[j].setX(x);
				lu[j].setY(y);
				updatePackage(lu[j].getName(), x, y, 0);
			}
			System.out.println("Successfully initialized packages!!!");
		}

		public void whilePlaying() {
			ListenThread lt = new ListenThread();
			lt.start();
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			doDrawing(g);
			Toolkit.getDefaultToolkit().sync();
		}

		private synchronized void doDrawing(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			// Waiting screen for P1
			if (avatar.getName() == 1 && timePassed == 0) {
				Font memeable = new Font("Ariel", Font.BOLD, 30);
				g2d.setFont(memeable);
				g2d.setColor(Color.RED);
				g2d.drawString("Waiting for Client 2 Connection...", 10, HEIGHT - 50);
			}

			// timer
			if (tm.isRunning()) {
				Font memeable = new Font("Ariel", Font.BOLD, 30);
				g2d.setFont(memeable);
				g2d.setColor(Color.RED);
				if ((int) (1.0 * timePassed / 1000 % 60) < 10) {
					g2d.drawString(Integer.toString((int) (1.0 * timePassed / 1000 / 60)) + ":0"
							+ Integer.toString((int) (1.0 * timePassed / 1000 % 60)), 50, 50);
				} else {
					g2d.drawString(Integer.toString((int) (1.0 * timePassed / 1000 / 60)) + ":"
							+ Integer.toString((int) (1.0 * timePassed / 1000 % 60)), 50, 50);
				}
			}

			// initialize boss information
			if (NUMMET + plusMets == MAXMET - 1) {
				message1 = timePassed;
				bossSpawnTime = timePassed + 20000; // 20 secs
			}

			// message 1
			if (NUMMET + plusMets == MAXMET && timePassed - message1 < 10000) // 10000

			{
				Font memeable = new Font("Ariel", Font.BOLD, 20);
				String text = "MAX METEORS REACHED!! YOU CAN NOW PERMANENTLY KILL THEM!!!";
				g2d.setFont(memeable);
				g2d.setColor(Color.RED);
				g2d.drawString(text, 200, 50);
			}

			// message 2
			if (NUMMET + plusMets == MAXMET && timePassed - message1 >= 10000 // Change
					&& timePassed < bossSpawnTime) {
				String time;
				if ((int) (1.0 * bossSpawnTime / 1000 % 60) < 10) {
					time = Integer.toString((int) (1.0 * bossSpawnTime / 1000 / 60)) + ":0"
							+ Integer.toString((int) (1.0 * bossSpawnTime / 1000 % 60));
				} else {
					time = Integer.toString((int) (1.0 * bossSpawnTime / 1000 / 60)) + ":"
							+ Integer.toString((int) (1.0 * bossSpawnTime / 1000 % 60));
				}
				Font memeable = new Font("Ariel", Font.BOLD, 20);
				String text = "THE BOSS WILL SPAWN IN 10 SECONDS!!!! " + "(" + time + ")";
				g2d.setFont(memeable);
				g2d.setColor(Color.RED);
				g2d.drawString(text, 200, 50);
			}

			System.out.println("Time Passed: " + timePassed);
			System.out.println("BossSpawnTime: " + bossSpawnTime);

			// initialize boss stats
			if (timePassed == bossSpawnTime - DELAY) {
				if (!changed && avatar.getName() == 1) {
					metSurvived = 0;
					for (int j = 0; j < MAXMET; j++) {
						if (meteors[j].getEnabled()) {
							metSurvived++;
							meteors[j].setEnabled(false);
						}
					}
					output.println("updms" + " " + metSurvived);
					bossMaxHP = metSurvived * 3;
					changed = true;
					for (int j = 0; j < bBullets.length; j++) {
						bBullets[j] = new BossBullets(HEIGHT / 5);
						bBullets[j].setDamage((int) (3 * metSurvived / MAXMET));
					}
					boss.setLives(metSurvived * 3);
					boss.setVel(WIDTH / (bossKillTime / DELAY));
					boss.setEnabled(true);
				}
			}

			if (boss.getEnabled() && boss.getLives() <= 0) {
				tm.stop();
				Font memeable = new Font("Ariel", Font.BOLD, 30);
				g2d.setFont(memeable);
				g2d.setColor(Color.RED);
				g2d.drawString("gj", WIDTH / 2, HEIGHT / 2);
				// g2d.drawString( "I <3 VH and Pusheens", WIDTH / 2, HEIGHT / 2
				// );
			}

			if (boss.getEnabled() && boss.getLives() > 0) {
				boss.move();
				g2d.drawImage(boss.getImage(), (int) boss.getX(), (int) boss.getY(), this);
				for (int j = 0; j < bBullets.length; j++) {
					BossBullets bb = bBullets[j];
					Rectangle bbs = new Rectangle(bb.getSize(), bb.getSize());
					bbs.setLocation((int) bb.getX(), (int) bb.getY());
					if (bb.getInAir()) {
						g2d.setColor(Color.RED);
						g2d.drawRect((int) bb.getX(), (int) bb.getY(), bb.getSize(), bb.getSize());
						g2d.drawImage(bb.getImage(), (int) bb.getX(), (int) bb.getY(), this);
					}
				}
			}

			// draws bullets
			for (int j = 0; j < MAXBULLETS; j++) {
				SmallBullet work = bullets[j];
				if (work.isInAir()) {
					g2d.drawImage(work.getImage(), (int) work.getX(), (int) work.getY(), this);
				}
				DummyBullet work2 = bullets2[j];
				if (work2.getOn()) {
					g2d.drawImage(work2.getImage(), (int) work2.getX(), (int) work2.getY(), this);
				}
			}

			// draws boss
			if (boss.getEnabled() && bossMaxHP > 0) {
				drawHP(g, boss);
			}

			// Draws meteors
			for (int j = 0; j < NUMMET + plusMets; j++) {
				Meteors work = meteors[j];
				if (avatar.getName() == 1 && work.getEnabled()) {
					Rectangle avatar1s = new Rectangle(AVATARSIZE * 3, AVATARSIZE * 3);
					if (avatar.getLives() > 0) {
						avatar1s.setLocation((int) avatar.getX() - AVATARSIZE, (int) avatar.getY() - AVATARSIZE);
					} else {
						avatar1s.setLocation(WIDTH + 100, HEIGHT + 100);
					}

					Rectangle avatar2s = new Rectangle(AVATARSIZE * 3, AVATARSIZE * 3);
					if (avatar.getLives() > 0) {
						avatar2s.setLocation((int) avatar2.getX() - AVATARSIZE, (int) avatar2.getY() - AVATARSIZE);
					} else {
						avatar2s.setLocation(WIDTH + 100, HEIGHT + 100);
					}

					Rectangle mtr = new Rectangle(AVATARSIZE, AVATARSIZE);
					mtr.setLocation((int) work.getX(), (int) work.getY());

					// relocates meteors in need
					if ((work.getSS() > work.getSSS() - 50 && (overlaps(mtr, avatar1s) || overlaps(mtr, avatar2s)))
							|| (timePassed < 100 && overlaps(mtr, avatar1s))) {
						if (Math.random() > .5) {
							if (Math.random() > .5) // Quad 1
							{
								work.setX(Math.random() * WIDTH / 2 + 50 + avatar.getX());
								work.setY(avatar.getY() - 50 - Math.random() * HEIGHT / 2);
							} else // Quad 2
							{
								work.setX(avatar.getX() - 50 - Math.random() * WIDTH / 2);
								work.setY(Math.random() * HEIGHT / 2 + 50 + avatar.getY());
							}
						} else {
							if (Math.random() > .5) // Quad 3
							{
								work.setX(avatar.getX() - 50 - Math.random() * WIDTH / 2);
								work.setY(Math.random() * HEIGHT / 2 + HEIGHT / 2);
							} else // Quad 4
							{
								work.setX(Math.random() * WIDTH / 2 + 50 + avatar.getX());
								work.setY(Math.random() * HEIGHT / 2 + HEIGHT / 2);
							}
						}
						if (timePassed < 100 && gameStarted) {
							tm.start();
							output.println("xpaus");
							output.flush();
						}
					}
				}
				if (work.getEnabled()) {
					g2d.drawImage(work.getImage(), (int) work.getX(), (int) work.getY(), this);
				}
			}

			// Draws packages
			for (int j = 0; j < numHP; j++) {
				HP work = (HP) hp[j];
				if (work.getSleep() <= 0) {
					g2d.drawImage(work.getImage(), (int) work.getX(), (int) work.getY(), this);
				}
			}

			for (int j = 0; j < numLU; j++) {
				LevelUp work = (LevelUp) lu[j];
				if (work.getSleep() <= 0) {
					g2d.drawImage(work.getImage(), (int) work.getX(), (int) work.getY(), this);
				}
			}

			// Only draw if player is alive
			if (avatar.getLives() > 0) {
				g2d.drawImage(avatar.getImage(), (int) avatar.getX(), (int) avatar.getY(), this);
				drawHP(g, avatar);
				drawMana(g, avatar);
			}
			if (avatar2.getLives() > 0) {
				g2d.drawImage(avatar2.getImage(), (int) avatar2.getX(), (int) avatar2.getY(), this);
				drawHP(g, avatar2);
				drawMana(g, avatar2);
			}
		}

		public void drawHP(Graphics g, BOSS b) {
			g.setColor(Color.RED);
			g.fillRect(WIDTH - boss.getSize() - 40, 10, (int) (boss.getSize() * (1.0 * boss.getLives() / bossMaxHP)),
					WIDTH / 20);
			g.setColor(Color.GRAY);
			g.fillRect(WIDTH - boss.getSize() - 40 + (int) (boss.getSize() * (1.0 * boss.getLives() / bossMaxHP)), 10,
					(int) (boss.getSize() * (1 - 1.0 * boss.getLives() / bossMaxHP)), WIDTH / 20);
		}

		public void drawHP(Graphics g, Avatar a) {
			g.setColor(Color.GREEN);
			int lives = a.getLives();
			if (lives > highestLife) {
				highestLife = lives;
			}
			g.fillRect((int) a.getX(), (int) a.getY() - 10, (int) (AVATARSIZE * 2 * (1.0 * lives / highestLife)),
					AVATARSIZE / 4);

			g.setColor(Color.RED);
			g.fillRect((int) (a.getX() + AVATARSIZE * 2 * (1.0 * lives / highestLife)), (int) a.getY() - 10,
					(int) (AVATARSIZE * 2 * (1 - 1.0 * lives / highestLife)), AVATARSIZE / 4);
		}

		public void drawHP(Graphics g, Dummytar a) {
			g.setColor(Color.GREEN);
			int lives = a.getLives();
			if (lives > highestLife2) {
				highestLife2 = lives;
			}
			g.fillRect((int) a.getX(), (int) a.getY() - 10, (int) (AVATARSIZE * 2 * (1.0 * lives / highestLife2)),
					AVATARSIZE / 4);

			g.setColor(Color.RED);
			g.fillRect((int) (a.getX() + AVATARSIZE * 2 * (1.0 * lives / highestLife2)), (int) a.getY() - 10,
					(int) (AVATARSIZE * 2 * (1 - 1.0 * lives / highestLife2)), AVATARSIZE / 4);
		}

		public void drawMana(Graphics g, Avatar a) {
			g.setColor(Color.BLUE);
			int mana = 0;
			for (int j = 0; j < MAXBULLETS; j++) {
				if (!bullets[j].isInAir() && bullets[j].getCD() <= 0) {
					mana++;
				}
			}
			g.fillRect((int) a.getX(), (int) a.getY() - 5, (int) (AVATARSIZE * 2 * (1.0 * mana / MAXBULLETS)),
					AVATARSIZE / 4);

			g.setColor(Color.GRAY);
			g.fillRect((int) a.getX() + (int) (AVATARSIZE * 2 * (1.0 * mana / MAXBULLETS)), (int) a.getY() - 5,
					(int) (AVATARSIZE * 2 * (1 - 1.0 * mana / MAXBULLETS)), AVATARSIZE / 4);
		}

		public void drawMana(Graphics g, Dummytar a) {
			g.setColor(Color.BLUE);
			int mana = 0;
			for (int j = 0; j < MAXBULLETS; j++) {
				if (!bullets2[j].getOn() && bullets2[j].getCD() <= 0) {
					mana++;
				}
			}
			g.fillRect((int) a.getX(), (int) a.getY() - 5, (int) (AVATARSIZE * 2 * (1.0 * mana / MAXBULLETS)),
					AVATARSIZE / 3);

			g.setColor(Color.GRAY);
			g.fillRect((int) a.getX() + (int) (AVATARSIZE * 2 * (1.0 * mana / MAXBULLETS)), (int) a.getY() - 5,
					(int) (AVATARSIZE * 2 * (1 - 1.0 * mana / MAXBULLETS)), AVATARSIZE / 3);
		}

		private class TAdapter extends KeyAdapter {
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();

				if (gameStarted) {
					if (key == KeyEvent.VK_ESCAPE) {
						try {
							output.println("quitt");
							output.flush();
							socket.close();
							output.close();
							input.close();
							setVisible(false);
							dispose();
						} catch (IOException e1) {
							e1.printStackTrace();
						}

					}
					if (key == KeyEvent.VK_P) // pauses
					{
						if (tm.isRunning()) {
							tm.stop();
							output.println("pause");
							output.flush();
						} else {
							tm.start();
							output.println("xpaus");
							output.flush();
						}
					}

					else if (key == KeyEvent.VK_R) {
						if (avatar.getLives() > 1 && avatar2.getLives() <= 0) {
							int lifeSacrificed = avatar.getLives() / 2;
							avatar.setLives(avatar.getLives() - lifeSacrificed);
							avatar2.setLives(lifeSacrificed);
							avatar2.setX(avatar.getX());
							avatar2.setY(avatar.getY());
							output.println("rvive" + " " + avatar.getLives() + " " + lifeSacrificed);
							output.flush();
						}
					}

					else if (key == KeyEvent.VK_0) // restarts
					{
						changed = false;
						boss.setX(WIDTH);
						bossSpawnTime = 1000000;
						message1 = 0;
						boss.setEnabled(false);
						numHP = INITPACKAGE;
						numLU = INITPACKAGE;
						timePassed = 0;
						lastHit = 0;
						avatar.setVel(Avatar.INITVEL);
						avatar.setLives(INITLIFE);
						avatar2.setLives(INITLIFE);
						highestLife = INITLIFE;
						highestLife2 = INITLIFE;
						avatar.setY(HEIGHT / 2);
						avatar.setX(WIDTH / 2);
						for (int j = 0; j < NUMMET + plusMets; j++) {
							meteors[j].setEnabled(true);
							meteors[j].setVelocity(MET_INIT_VEL);
							if (avatar.getName() == 2) {
								output.println("updmt" + " " + j + " " + meteors[j].getX() + " " + meteors[j].getY()
										+ " " + meteors[j].getEnabled() + " " + meteors[j].getSS());
							}
						}
						plusMets = 0;
						tm.start();
						output.println("rstrt");
						output.flush();
					}

					avatar.keyPressed(e);
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				avatar.keyReleased(e);
			}
		}

		@Override
		public synchronized void actionPerformed(ActionEvent e) // timer
		{
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					timePassed += DELAY;

					// bullet calculations
					for (int j = 0; j < MAXBULLETS; j++) {
						SmallBullet sb = bullets[j];
						// bullet hit walls
						if (sb.getX() > WIDTH || sb.getX() < 0 || sb.getY() > HEIGHT || sb.getY() < 0) {
							if (sb.isInAir()) {
								sb.setCD(SmallBullet.SBCD);
								sb.setHP(SmallBullet.SBHP);
								sb.setX(5000);
								sb.setY(5000);
								sb.setInAir(false);
							}
							sb.setInAir(false);
						}

						// bullet mechanics
						if (sb.isInAir() && sb.getCD() <= 0) {
							sb.move();
						} else {
							int mana = 0;
							// CD shorter if not empty
							for (int i = 0; i < MAXBULLETS; i++) {
								if (bullets[i].getCD() <= 0 && !bullets[j].isInAir()) {
									mana++;
								}
							}
							if (mana == 0) {
								sb.setCD(sb.getCD() - DELAY);
							} else {
								sb.setCD(sb.getCD() - 2 * DELAY);
							}

						}
						updateBullet(j, sb.getX(), sb.getY(), sb.isInAir(), sb.getCD());
					}

					if (timePassed > bossSpawnTime) {
						// shoot from boss
						if (timePassed % bossShootIntervals == 0 && avatar.getName() == 1) {
							for (int j = 0; j < bBullets.length; j++) {
								BossBullets bb = bBullets[j];
								if (!bb.getInAir()) {
									bb.setInAir(true);
									double x = boss.getX();
									double y = WIDTH / 2;
									double ax = 0;
									double ay = 0;
									if (avatar.getLives() > 0 && avatar2.getLives() > 0) {
										if (Math.random() > .5) {
											ax = avatar.getX();
											ay = avatar.getY();
										} else {
											ax = avatar2.getX();
											ay = avatar2.getY();
										}
									} else if (avatar.getLives() <= 0) {
										ax = avatar2.getX();
										ay = avatar2.getY();
									} else if (avatar2.getLives() <= 0) {
										ax = avatar.getX();
										ay = avatar.getY();
									}

									double angle = 0;
									if (x > ax) {
										angle = Math.atan((y - ay) / (x - ax)) + Math.PI;
									} else {
										angle = Math.atan((y - ay) / (x - ax));
									}
									bb.setX(x);
									bb.setY(y);
									bb.setAngle(angle);
									break;
								}
							}
						}

						for (int j = 0; j < bBullets.length; j++) {
							BossBullets bb = bBullets[j];
							if (avatar.getName() == 1 && bb.getInAir()) {
								bb.move();
							}
							if (bb.getX() > WIDTH || bb.getX() < 0 || bb.getY() > HEIGHT || bb.getY() < 0) {
								bb.setInAir(false);
								bb.setX(5000);
								bb.setY(5000);
							}
						}
					}

					if (avatar.getName() == 1 && timePassed % 1000 == 0) {
						if (NUMMET + plusMets < MAXMET) {
							plusMets += 1;
						}
						if (plusMets % 2 == 0 && numHP < MAXPACKAGE) {
							numHP++;
							output.println("hporb" + " " + numHP);
							output.flush();
						}
						// no problems
						output.println("addmt" + " " + plusMets);
						output.flush();
					}

					// HITS WALL
					if (!boss.getEnabled()) {
						if (avatar.getX() > WIDTH) {
							avatar.setX(0);
						} else if (avatar.getX() < 0) {
							avatar.setX(WIDTH);
						} else if (avatar.getY() > HEIGHT) {
							avatar.setY(0);
						} else if (avatar.getY() < 0) {
							avatar.setY(HEIGHT);
						}
					}

					// Cannot go through walls when boss spawns
					else {
						if (avatar.getX() > WIDTH) {
							avatar.setX(WIDTH);
						} else if (avatar.getX() < 0) {
							avatar.setX(0);
						} else if (avatar.getY() + AVATARSIZE > HEIGHT) {
							avatar.setY(HEIGHT - 2 * AVATARSIZE);
						} else if (avatar.getY() < 0) {
							avatar.setY(10);
						}
					}

					// Moves avatar no problems
					avatar.move();
					output.println("avatx" + " " + avatar.getX());
					output.flush();
					output.println("avaty" + " " + avatar.getY());
					output.flush();

					// Sets rectangles to check for collision
					Rectangle avatar1s = new Rectangle(AVATARSIZE, AVATARSIZE);
					avatar1s.setLocation((int) avatar.getX(), (int) avatar.getY());

					Rectangle bosss = new Rectangle(999999, boss.getSize());
					bosss.setLocation((int) boss.getX(), (int) boss.getY());

					// Avatar hits boss
					if (boss.getLives() > 0 && overlaps(avatar1s, bosss)) {
						avatar.setLives(0);
						output.println("avthp" + " " + 0);
						output.flush();
					}

					// Checks package hits
					for (int j = 0; j < numHP; j++) {
						HP work = hp[j];
						work.setSleep(work.getSleep() - DELAY);
						Rectangle hps = new Rectangle(AVATARSIZE, AVATARSIZE);
						hps.setLocation((int) work.getX(), (int) work.getY());
						if (overlaps(avatar1s, hps)) {
							avatar.setLives(avatar.getLives() + 1);
							output.println("avthp" + " " + avatar.getLives());
							output.flush();
							work.setSleep(HPSLEEPTIME);

							Double x = Math.random() * (WIDTH - 2 * AVATARSIZE);
							Double y = Math.random() * (HEIGHT - 2 * AVATARSIZE);
							int sleep = HPSLEEPTIME;
							work.setX(x);
							work.setY(y);
							updatePackage(work.getName(), x, y, sleep);
						} else if (work.getEnabled() && boss.getLives() > 0 && overlaps(bosss, hps)) {
							work.setEnabled(false);
							boss.setLives(boss.getLives() + 1);
							if (boss.getLives() > bossMaxHP) {
								bossMaxHP = boss.getLives();
							}
							updateBossHP(boss.getLives(), bossMaxHP);

							Double x = Math.random() * (WIDTH - 2 * AVATARSIZE);
							Double y = Math.random() * (HEIGHT - 2 * AVATARSIZE);
							int sleep = HPSLEEPTIME;
							work.setX(x);
							work.setY(y);
							updatePackage(work.getName(), x, y, sleep);
						}
					}

					for (int j = 0; j < numLU; j++) {
						LevelUp work = lu[j];
						work.setSleep(work.getSleep() - DELAY);
						Rectangle lus = new Rectangle(AVATARSIZE, AVATARSIZE);
						lus.setLocation((int) work.getX(), (int) work.getY());
						if (overlaps(avatar1s, lus)) {
							levelUp();
							work.setSleep(LUSLEEPTIME);

							Double x = Math.random() * (WIDTH - 2 * AVATARSIZE);
							Double y = Math.random() * (HEIGHT - 2 * AVATARSIZE);
							int sleep = LUSLEEPTIME;
							work.setX(x);
							work.setY(y);
							updatePackage(work.getName(), x, y, sleep);
						} else if (work.getEnabled() && boss.getLives() > 0 && overlaps(bosss, lus)) {
							work.setEnabled(false);
							boss.setLives(boss.getLives() + 2);
							if (boss.getLives() > bossMaxHP) {
								bossMaxHP = boss.getLives();
							}
							updateBossHP(boss.getLives(), bossMaxHP);

							Double x = Math.random() * (WIDTH - 2 * AVATARSIZE);
							Double y = Math.random() * (HEIGHT - 2 * AVATARSIZE);
							int sleep = LUSLEEPTIME;
							work.setX(x);
							work.setY(y);
							updatePackage(work.getName(), x, y, sleep);
						}
					}

					// Client 1 meteor management
					for (int j = 0; j < NUMMET + plusMets; j++) {
						Meteors m = meteors[j];
						if (avatar.getName() == 1 && m.getSS() <= 0) {
							m.move();
						}
						if (avatar.getName() == 1 && m.getX() > WIDTH || m.getX() < 0 || m.getY() > HEIGHT
								|| m.getY() < 0) {
							double x = Math.random() * WIDTH;
							double y = Math.random() * HEIGHT;
							m.setSS(m.getSSS());
							m.setX(x);
							m.setY(y);
						}
						m.setSS(m.getSS() - 10);
					}

					// Updating client 2 meteors
					if (avatar.getName() == 1 && timePassed % (DELAY) == 0) {
						for (int j = 0; j < NUMMET + plusMets; j++) {
							Meteors m = meteors[j];
							int meteorID = j;
							double x = m.getX();
							double y = m.getY();
							output.println("updmt" + " " + meteorID + " " + x + " " + y + " " + m.getEnabled() + " "
									+ m.getSS());
							output.flush();
						}
						if (timePassed > bossSpawnTime && metSurvived != 0) {
							for (int j = 0; j < bBullets.length; j++) {
								BossBullets bb = bBullets[j];
								output.println(
										"updbb" + " " + j + " " + bb.getX() + " " + bb.getY() + " " + bb.getInAir());
								output.flush();
							}
						}
					}

					// meteor impacts
					for (int j = 0; j < NUMMET + plusMets; j++) {
						Meteors m = meteors[j];
						Rectangle meteorss = new Rectangle(AVATARSIZE, AVATARSIZE);
						meteorss.setLocation((int) m.getX(), (int) m.getY());

						if (m.getEnabled()) {
							for (int i = 0; i < MAXBULLETS; i++) {
								SmallBullet sb = bullets[i];
								Rectangle sbs = new Rectangle(SBSIZE, SBSIZE);
								sbs.setLocation((int) sb.getX() + SBSIZE, (int) sb.getY() + SBSIZE);

								// BULLET HITS METEOR
								if (overlaps(sbs, meteorss)) {
									if (NUMMET + plusMets == MAXMET) {
										m.setEnabled(false);
									}
									sb.setHP(sb.getHP() - 1);
									if (sb.getHP() == 0) {
										sb.setHP(SmallBullet.SBHP);
										sb.setCD(SmallBullet.SBCD);
										sb.setX(5000);
										sb.setY(5000);
										sb.setInAir(false);
									}
									double x = Math.random() * WIDTH;
									double y = Math.random() * HEIGHT;
									m.setSS(m.getSSS());
									m.setX(x);
									m.setY(y);
									if (avatar.getName() == 2) {
										int meteorID = j;
										output.println("updmt" + " " + meteorID + " " + x + " " + y + " "
												+ m.getEnabled() + " " + m.getSS());
										output.flush();
									}
								}
							}

							// GETS HIT BY METEOR
							if ((overlaps(avatar1s, meteorss)) && !IMMUNE) {
								if (timePassed - lastHit > DELAY + 1) {
									avatar.setLives(avatar.getLives() - 1);
								}
								if (avatar.getLives() <= 0 && avatar2.getLives() <= 0) {
									tm.stop();
									output.println("tookL");
									output.flush();
								} else {
									int meteorID = j;
									double x = Math.random() * WIDTH;
									double y = Math.random() * HEIGHT;
									m.setSS(m.getSSS());
									m.setX(x);
									m.setY(y);
									if (avatar.getName() == 2) {
										output.println("updmt" + " " + meteorID + " " + x + " " + y + " "
												+ m.getEnabled() + " " + m.getSS());
										output.flush();
									}
									output.println("avthp" + " " + avatar.getLives());
									output.flush();
								}
								lastHit = timePassed;
							}
						}
					}

					// bullet hits Boss
					if (boss.getLives() > 0) {
						for (int j = 0; j < MAXBULLETS; j++) {
							SmallBullet sb = bullets[j];
							Rectangle sbs = new Rectangle(SBSIZE, SBSIZE);
							sbs.setLocation((int) sb.getX() + SBSIZE, (int) sb.getY() + SBSIZE);
							if (overlaps(bosss, sbs) && sb.isInAir()) {
								sb.setHP(SmallBullet.SBHP);
								sb.setCD(SmallBullet.SBCD);
								sb.setX(5000);
								sb.setY(5000);
								sb.setInAir(false);

								boss.setLives(boss.getLives() - sb.getHP());
								updateBossHP(boss.getLives(), bossMaxHP);
							}
							for (int i = 0; i < bBullets.length; i++) {
								BossBullets bb = bBullets[i];
								Rectangle bbs = new Rectangle(bb.getSize(), bb.getSize());
								bbs.setLocation((int) bb.getX(), (int) bb.getY());
								if (overlaps(bbs, sbs)) {
									sb.setInAir(false);
									sb.setX(5000);
									sb.setY(5000);
								}
							}
						}

					}

					// boss bullet hits avatar
					if (boss.getLives() > 0) {

						for (int j = 0; j < bBullets.length; j++) {
							BossBullets bb = bBullets[j];
							Rectangle bbs = new Rectangle(bb.getSize(), bb.getSize());
							bbs.setLocation((int) bb.getX(), (int) bb.getY());
							if (bb.getInAir() && overlaps(bbs, avatar1s)) {
								avatar.setLives(avatar.getLives() - bb.getDamage());
								bb.setInAir(false);
								bb.setX(5000);
								bb.setY(5000);
								if (avatar.getName() == 2) {
									output.println("updbb" + " " + j + " " + bb.getX() + " " + bb.getY() + " "
											+ bb.getInAir());
									output.flush();
								}
								output.println("avthp" + " " + avatar.getLives());
								if (avatar.getLives() <= 0) {
									avatar.setX(5000);
									avatar.setY(5000);
								}
							}
						}
					}

					if (avatar.getLives() <= 0 && avatar2.getLives() <= 0) {
						tm.stop();
						output.println("tookL");
						output.flush();
					}

					return null;
				}

				@Override
				protected void done() {
					repaint();
				}
			};
			worker.execute();
		}

		public ArrayList<String> decode(String s) {
			ArrayList<String> answer = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(s);
			while (st.hasMoreTokens()) {
				answer.add(st.nextToken());
			}
			return answer;
		}

		public class ListenThread extends Thread // listen for message from
													// server
		{
			public synchronized void run() {
				while (true) {
					String in = input.nextLine();
					ArrayList<String> msg = decode(in);
					String message = msg.get(0);

					if (in.equals("go")) {
						gameStarted = true;
						tm.start();
					} else if (message.equals("tookL")) {
						avatar.setX(5000);
						avatar.setY(5000);
						avatar2.setX(5000);
						avatar2.setY(5000);
						tm.stop();
					} else if (message.equals("pause")) {
						tm.stop();
					} else if (message.equals("xpaus")) {
						tm.start();
					} else if (message.equals("rstrt")) {
						changed = false;
						boss.setX(WIDTH);
						bossSpawnTime = 1000000;
						message1 = 0;
						boss.setEnabled(false);
						numHP = INITPACKAGE;
						numLU = INITPACKAGE;
						timePassed = 0;
						lastHit = 0;
						avatar.setVel(Avatar.INITVEL);
						avatar.setY(HEIGHT / 2);
						avatar.setX(WIDTH / 2);
						highestLife = INITLIFE;
						highestLife2 = INITLIFE;
						avatar.setLives(INITLIFE);
						avatar2.setLives(INITLIFE);
						plusMets = 0;
						for (int j = 0; j < NUMMET + plusMets; j++) {
							meteors[j].setVelocity(MET_INIT_VEL);
						}
						tm.start();
					} else if (message.equals("quitt")) {
						try {
							input.close();
							output.close();
							socket.close();
							setVisible(false);
							dispose();
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else if (message.equals("avatx")) {
						avatar2.setX(Double.parseDouble(msg.get(1)));
					} else if (message.equals("avaty")) {
						avatar2.setY(Double.parseDouble(msg.get(1)));
					} else if (message.equals("avthp")) {
						avatar2.setLives(Integer.parseInt(msg.get(1)));
					} else if (message.equals("addmt")) {
						plusMets = Integer.parseInt(msg.get(1));
					} else if (message.equals("hporb")) {
						numHP = Integer.parseInt(msg.get(1));
					} else if (message.equals("updbh")) {
						boss.setLives(Integer.parseInt(msg.get(1)));
						bossMaxHP = Integer.parseInt(msg.get(2));
					} else if (message.equals("updms")) {
						metSurvived = Integer.parseInt(msg.get(1));
						bossMaxHP = metSurvived * 3;
						boss.setLives(bossMaxHP);
						for (int j = 0; j < bBullets.length; j++) {
							bBullets[j] = new BossBullets(HEIGHT / 5);
							bBullets[j].setDamage((int) (3 * metSurvived / MAXMET));
						}
						boss.setVel(WIDTH / (bossKillTime / DELAY));
						boss.setEnabled(true);
					} else if (message.equals("rvive")) {
						avatar2.setLives(Integer.parseInt(msg.get(1)));
						avatar.setLives(Integer.parseInt(msg.get(2)));
						avatar.setX(avatar2.getX());
						avatar.setY(avatar2.getY());
					} else if (message.equals("updmt")) {
						Meteors m = meteors[Integer.parseInt(msg.get(1))];
						m.setX(Double.parseDouble(msg.get(2)));
						m.setY(Double.parseDouble(msg.get(3)));
						m.setEnabled(Boolean.parseBoolean(msg.get(4)));
						m.setSS(Integer.parseInt(msg.get(5)));
					} else if (message.equals("updbb")) {
						if (!msg.get(1).equals(0)) {
							BossBullets b = bBullets[Integer.parseInt(msg.get(1))];
							b.setX(Double.parseDouble(msg.get(2)));
							b.setY(Double.parseDouble(msg.get(3)));
							b.setInAir(Boolean.parseBoolean(msg.get(4)));
						}
					} else if (message.equals("updpk")) {
						String name = msg.get(1);
						if (name.substring(0, 2).equals("hp")) {
							int num = Integer.parseInt(name.substring(2));
							HP h = hp[num];
							h.setName(name);
							h.setX(Double.parseDouble(msg.get(2)));
							h.setY(Double.parseDouble(msg.get(3)));
							h.setSleep(Integer.parseInt(msg.get(4)));
						} else if (name.substring(0, 2).equals("lu")) {
							int num = Integer.parseInt(name.substring(2));
							LevelUp l = lu[num];
							l.setName(name);
							l.setX(Double.parseDouble(msg.get(2)));
							l.setY(Double.parseDouble(msg.get(3)));
							l.setSleep(Integer.parseInt(msg.get(4)));
						}
					} else if (message.equals("updbt")) {
						int ID = Integer.parseInt(msg.get(1));
						DummyBullet db = bullets2[ID];
						db.setX(Double.parseDouble(msg.get(2)));
						db.setY(Double.parseDouble(msg.get(3)));
						db.setOn(Boolean.parseBoolean(msg.get(4)));
						db.setCD(Integer.parseInt(msg.get(5)));
					}
				}
			}
		}

		public boolean overlaps(Rectangle r, Rectangle r2) {
			return r2.x < r.x + r.width && r2.x + r2.width > r.x && r2.y < r.y + r.height && r2.y + r2.height > r.y;
		}

		public void updatePackage(String name, double x, double y, int sleep) {
			output.println("updpk" + " " + name + " " + x + " " + y + " " + sleep);
			output.flush();
		}

		public void updateBullet(int ID, double x, double y, boolean on, int CD) {
			output.println("updbt" + " " + ID + " " + x + " " + y + " " + on + " " + CD);
			output.flush();
		}

		public void updateBossHP(int hp, int maxhp) {
			output.println("updbh" + " " + hp + " " + maxhp);
			output.flush();
		}

		public void levelUp() {
			if (avatar.getVel() < AVATARMAXVEL) {
				avatar.setVel(avatar.getVel() + 1);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					if (SwingUtilities.isLeftMouseButton(e) && avatar.getLives() > 0) {
						double x = e.getX();
						double y = e.getY();
						double ax = avatar.getX();
						double ay = avatar.getY();

						for (int j = 0; j < MAXBULLETS; j++) {
							SmallBullet work = bullets[j];
							if (!work.isInAir() && work.getCD() <= 0) {
								work.setInAir(true);
								work.setX(ax);
								work.setY(ay);
								double angle = 0;
								if (x > ax) {
									angle = Math.atan((y - ay) / (x - ax));
								} else {
									angle = Math.atan((y - ay) / (x - ax)) + Math.PI;
								}
								work.setAngle(angle);
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
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		try {
			System.out.println("Closing streams");
			socket.close();
			output.close();
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		System.out.println("hi this works");
	}
}
