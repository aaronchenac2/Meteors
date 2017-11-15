package pckg;

//import java.applet.Applet;
//import java.applet.AudioClip;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.Timer;

public class Menu extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	// private AudioClip audioClip;

	public static void main(String args[]) throws IOException {
		new Menu();
	}

	public Menu() throws IOException {
		// audioClip =
		// Applet.newAudioClip(this.getClass().getResource("starbound.wav"));
		// audioClip.loop();
		setLayout(new GridLayout(1, 1));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth() * 3 / 4;
		height = (int) screenSize.getHeight() * 3 / 4;
		setSize(width, height);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle("Meteors Game!");

		Functions f = new Functions();
		add(f);

		setVisible(true);
	}

	public class Functions extends JPanel implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Button htp;
		private Button play;
		private Button server;
		private JTextField tf;
		private Timer tm;
		private Image bimage;
		private Image a1image;
		private Image a2image;
		private BOSS b;
		private Avatar a1;
		private Dummytar a2;
		private int speed = height / 200;
		private int bangle = 90;
		private int a1angle = 0;
		private int a2angle = 0;

		public Functions() {
			ClassLoader loader = this.getClass().getClassLoader();

			b = new BOSS(width, height);
			bimage = b.getImage();
			bimage = bimage.getScaledInstance(width / 4, width / 4, Image.SCALE_DEFAULT);
			b.setX(10);
			b.setY(10);

			a1 = new Avatar(loader.getResource("menuKnight.png"), (int) width / 2, (int) height * 4 / 7 + height / 15);
			a1image = a1.getImage();
			a1image = a1image.getScaledInstance(width / 9, width / 9, Image.SCALE_DEFAULT);

			a2 = new Dummytar(loader.getResource("menuKnight2.png"), (int) width / 2 - width / 5 - 10,
					height * 4 / 7 + height / 15);
			a2image = a2.getImage();
			a2image = a2image.getScaledInstance(width / 9, width / 9, Image.SCALE_DEFAULT);

			tm = new Timer(25, this);
			tm.start();
			tm.addActionListener(this);

			Font font = new Font("Ariel", Font.BOLD, width / 30);
			setBackground(Color.BLUE);
			Font text = new Font("Ariel", Font.PLAIN, width / 45);

			htp = new Button("How to play");
			htp.setFont(font);
			htp.setPreferredSize(new Dimension(width / 2, height / 7));
			htp.addActionListener(this);
			add(htp);

			play = new Button("Play");
			play.setFont(font);
			play.setPreferredSize(new Dimension(width / 2, height / 7));
			play.addActionListener(this);
			add(play);

			server = new Button("Start Server");
			server.setFont(font);
			server.setPreferredSize(new Dimension(width / 2, height / 7));
			server.addActionListener(this);
			add(server);

			tf = new JTextField();
			tf.setFont(text);
			tf.addActionListener(this);
			tf.setPreferredSize(new Dimension(width / 2, height / 7));
			add(tf);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(bimage, (int) b.getX(), (int) b.getY(), this);
			g2d.drawImage(a1image, (int) a1.getX(), (int) a1.getY(), this);
			g2d.drawImage(a2image, (int) a2.getX(), (int) a2.getY(), this);

			Toolkit.getDefaultToolkit().sync();
		}

		public void move() {
			bangle = b.menuMove(width, height, bangle, speed);
			a1angle = a1.menuMove(width, height, a1angle, speed);
			a2angle = a2.menuMove(width, height, a2angle, speed);
			this.repaint();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand() == null) {
				move();
			} else {
				if (e.getActionCommand().equals("i<3vh")) {
					tf.setText("Server is already on!");
					SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws IOException, InterruptedException {
							Server s = new Server(tf);
							s.startServer();
							return null;
						}
					};
					worker.execute();
				}
				else if (e.getActionCommand().equals("How to play")) {
					HTP htp;
					try {
						htp = new HTP();
						htp.setVisible(true);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
				}
				else if (e.getActionCommand().equals("Play")) {
					tf.setText("Connecting to the server...");
					SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground()
						{
							try {
								new Client(tf);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							return null;
						}
					};
					worker.execute();
				}
				else if (e.getActionCommand().equals("Start Server")) {
					tf.setText("What is the password?");
					tf.addFocusListener(new FocusListener() {
						public void focusGained(final FocusEvent pE) {
			                tf.selectAll();
						}
						@Override
						public void focusLost(FocusEvent arg0) {							
						}
					});
				}
				else
				{
					tf.setText("Wrong!");
				}
				
			}
		}
	}

}
