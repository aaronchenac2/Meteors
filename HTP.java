package pckg;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HTP extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private JLabel tf;

	public HTP() throws FileNotFoundException {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		width = (int) screenSize.getWidth() * 3 / 4;
		height = (int) screenSize.getHeight() * 3 / 4;
		setSize(width + 100, height + 100);
		setResizable(false);
		setLocationRelativeTo(null);
		setTitle("Instructions");

		Functions f = new Functions();
		add(f);

		setVisible(true);
	}

	public class Functions extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Functions() throws FileNotFoundException {
			tf = new JLabel();
			tf.setPreferredSize(new Dimension(width, height));
			Font font = new Font("Ariel", Font.PLAIN, width / 40);
			tf.setFont(font);
			File file = new File("Instructions.txt");
			Scanner scan = new Scanner(file);
			String s = scan.nextLine();
			tf.setText("<html>" + s);
			add(tf);
			scan.close();
		}
	}
}
