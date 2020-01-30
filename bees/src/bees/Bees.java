package bees;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Bees {

	public static void main(String[] args) {
		bees();
	}

	private static Image swat;
	private static int beesClicked;
	private static int delay;
	private static int numBees;

	private static JFrame honey() { // Function to make and return JFrame

		JFrame honey = new JFrame();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Gets Devices resolution
		honey.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		honey.setSize(screenSize);
		honey.setUndecorated(true);
		honey.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.05f)); // Changes background opacity
		honey.setVisible(true);
		honey.repaint();

		return honey; // Returns JFrame
	}

	private static void bees() { // Main function to run program

		delay = 80; // Changes Bee movement speed
		numBees = 10; // Changes number of Bees
		beesClicked = 0; // How many bees have been clicked

		JFrame mainFrame = honey(); // Returns JFrame
		ImageIcon swatIcon;
		try {
			swatIcon = new ImageIcon(ImageIO.read(Bees.class.getResource("click.png")));
			swat = swatIcon.getImage();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // Image when bee is clicked
		
		int height = mainFrame.getHeight(); // Gets height of JFrame
		int width = mainFrame.getWidth(); // Gets width of JFrame

		if (numBees % 10 != 0) { // Number of bees has to be a multiple of 10
			numBees = (10 - numBees % 10) + numBees; // Rounds number down to a multiple of 10
		}

		Random numGenerator = new Random(); // Random number

		JLabel[] bees = new JLabel[numBees];
		for (int i = 0; i < numBees; i++) { // For number of Bees
			try {
				bees[i] = makeBee(mainFrame);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} // Creates Jlabels of Bee images
		}

		while (true) {
			try {
				for (int i = 0; i < numBees; i++) {
					if (i % 10 == 0) { // If Bees can be divided by 10
						int step = i + 10;
						for (int move = i; move < step; move++) { // Moves the Bees around in groups of 10
							bees[move].setLocation(numGenerator.nextInt(width - 203),
									numGenerator.nextInt(height - 110));
						} // Stops bees from going over the edges ^^
					}
					TimeUnit.MILLISECONDS.sleep(delay); // Delay is changeable at top of document
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static JLabel makeBee(JFrame mainFrame) throws IOException { // Function to make a bee

		ImageIcon icon;
		icon = new ImageIcon(ImageIO.read(Bees.class.getResource("bee.png")));
		JLabel label = new JLabel(icon);

		label.addMouseListener(new MouseAdapter() { // When Image is clicked
			@Override
			public void mousePressed(MouseEvent e) {
				label.removeMouseListener(this); // Removes mouse listener so game wont finish before ALL bees are
													// clicked
				icon.setImage(swat);
				label.repaint(); // Change Image to effect
				setTimeout(() -> {
					mainFrame.remove(label); // remove bee
					mainFrame.revalidate();
					mainFrame.repaint();
					beesClicked++; // Add to counter

					if (beesClicked == numBees) { // if bees clicked is the same as number of bees

						gameOver(); // Calls Game menu
					}

				}, 200); // Set effect image invisible after 200Ms
			}
		});

		mainFrame.add(label);
		label.setSize(icon.getIconWidth(), icon.getIconHeight());
		
		return label; // Returns Bee image
		
	}

	public static void gameOver() { //Function to display options once game is complete

		int menuChoice = JOptionPane.showConfirmDialog(null,
				"You have clicked all the bees away!\n" + "Would you like to play another?", "Congratulations!",
				JOptionPane.YES_NO_OPTION);

		if (menuChoice == 0) { // If the user wants to play another game
			bees();
		}

		else { //Exit
			JOptionPane.showMessageDialog(null, "Thank you for playing!", "You have chosen to exit", 1);
			System.exit(0);
		}
	}

	public static void setTimeout(Runnable runnable, int delay) { // Delay
		new Thread(() -> {
			try {
				Thread.sleep(delay);
				runnable.run();
			} catch (Exception e) {
				System.err.println(e);
			}
		}).start();
	}

} // End of Program
