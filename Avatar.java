package pckg;

import java.awt.Image;
import java.awt.event.KeyEvent;
import java.net.URL;

import javax.swing.ImageIcon;

public class Avatar {
	public static final int INITVEL = 6;

	private int avatarVel = INITVEL;

	private double dx;

	private double dy;

	private double x;

	private double y;

	private Image image;

	private int name;

	private int lives = 5;

	public Avatar(URL file, double x, double y) {
		initCraft(file, x, y);
	}

	private void initCraft(URL file, double x, double y) {
		ImageIcon ii = new ImageIcon(file);
		image = ii.getImage();
		this.x = x;
		this.y = y;
	}

	public void move() {
		x += dx;
		y += dy;
	}

	public int menuMove(int width, int height, int angle, int speed) {
		x += speed * Math.cos(Math.toRadians(angle));
		y += speed * Math.sin(Math.toRadians(angle));
		if (y > height * 4 / 7 + height / 15 && x < width / 2) {
			angle = 0;
		} else if (y >= height * 4 / 7 + height / 15 && x > width * 3 / 4) {
			angle = 270;
		} else if (y < 0) {
			x = 10;
			y = 10;
			angle = 90;
		}
		return angle;
	}

	public void setLives(int l) {
		lives = l;
	}

	public int getLives() {
		return lives;
	}

	public void setName(int n) {
		name = n;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setdx(int dx) {
		this.dx = dx;
	}

	public void setdy(int dy) {
		this.dy = dy;
	}

	public int getName() {
		return name;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public int getVel() {
		return avatarVel;
	}

	public void setVel(int v) {
		avatarVel = v;
	}

	public Image getImage() {
		return image;
	}

	public void keyPressed(KeyEvent e) {

		int key = e.getKeyCode();
		if (key == KeyEvent.VK_A) {
			dx = -avatarVel;
		}

		if (key == KeyEvent.VK_D) {
			dx = avatarVel;
		}

		if (key == KeyEvent.VK_W) {
			dy = -avatarVel;
		}

		if (key == KeyEvent.VK_S) {
			dy = avatarVel;
		}
	}

	public void keyReleased(KeyEvent e) {

		int key = e.getKeyCode();
		if (key == KeyEvent.VK_A) {
			dx = 0;
		}

		if (key == KeyEvent.VK_D) {
			dx = 0;
		}

		if (key == KeyEvent.VK_W) {
			dy = 0;
		}

		if (key == KeyEvent.VK_S) {
			dy = 0;
		}
	}

}
