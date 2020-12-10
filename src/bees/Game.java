package bees;

/* ---------------- Details ------------------
 * Authors: Cameron Morrison & Ged Robertson
 * Program: The Bee Game
 * Objective: Save the bees!
 * Year created: 2020   						
 * ------------------------------------------*/
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game {

	private static JFrame mainFrame; // Game background
	private static JPanel settingsFrame; // Settings GUI
	private static JLabel settings; // Displays settings when clicked
	private static JLabel exitLabel; // button to exit program
	private static Clip clip; // Audio file that is played when bee is clicked
	private static int difficulty = 1; // Default difficulty
	private static JButton numBeeBtn, difficultyBtn, resetBtn, closeBtn, closeMenu; // Settings Menu buttons
	public static JLabel scoreLabel; // Displays score top left
	private Image swatImage; // Image when bees are clicked
	private Image beeImage; // Bee image
	private Image beeFlippedImage;// Reverse bee image when bee is moving backwards
	private int numBees = 0; // Change number of bees per game
	private int delay = 5; // Speed of bee movement
	private int width; // JFrame width
	private int height; // JFrame height
	private int newNumBees = 20; // Default value of bees to re-spawn
	private boolean pause; // Whilst pause is true, you can not loose score for clicking
	private boolean isSettingsMenuOpen = false; // When pressing escape twice it causes glitch, adding fail safe
	private boolean settingsMenuLock = false; // If true settings menu cannot be called
	private final Scores SCORE_CLASS = new Scores(); // Global instance of score class
	private Bee[] bees; // Stores the Bees

	public static void main(String[] args) {
		Game bees = new Game();
		bees.start();
	}

	public void start() {
		SCORE_CLASS.readScore(); // Reads in score
		settingsMenuLock = true; // Settings menu cannot be called during introAnimation
		mainFrame(); // Creates JFrame
		introAnimation(); // Shows animation for introduction
		settingsMenuLock = false; // Now settings menu can be called
		bees(); // Spawns and controls bees
	}

	private void mainFrame() { // Function to make and return JFrame

		mainFrame = new JFrame();
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(Game.class.getResource("/resources/beeIcon.png")));
		mainFrame.setLayout(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Gets Devices resolution
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(screenSize);
		mainFrame.setUndecorated(true);

		width = mainFrame.getWidth();
		height = mainFrame.getHeight();

		// Background Image for the game
		try {
			mainFrame.setContentPane(
					new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/resources/background.png")))));
		} catch (IOException e) {
		}

		// Score Label
		scoreLabel = new JLabel("Your score: " + SCORE_CLASS.getScore());
		scoreLabel.setFont(new Font("Sans Serif", Font.BOLD, 38));
		scoreLabel.setForeground(Color.white);
		scoreLabel.setBounds(35, 9, 400, 55);
		mainFrame.add(scoreLabel);

		// KeyListener for JFrame
		mainFrame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				int keyCode = event.getKeyCode();
				switch (keyCode) {
				case 27: // Escape keyCode
					controlSettingsMenu();
					break;
				case 91: // Windows button key code
					System.exit(0);
					break;
				}
			}
		});

		ImageIcon icon = null;
		try {
			icon = new ImageIcon(ImageIO.read(Game.class.getResource("/resources/plate.png")));
		} catch (IOException ex) {
			System.out.println(ex);
			return;
		}
		JLabel plateLabel = new JLabel(icon);
		plateLabel.setBounds(0, 0, 400, 70);
		mainFrame.add(plateLabel);
		// End Score Label

		// Exit Label
		ImageIcon exitImg;
		try {
			exitImg = new ImageIcon(ImageIO.read(Game.class.getResource("/resources/exit.png")));
		} catch (IOException ex) {
			System.out.println(ex);
			return;
		}
		exitLabel = new JLabel(exitImg);
		exitLabel.setBounds(0, 0, 200, 60);
		exitLabel.setLocation(width - 200, height - 60);
		mainFrame.add(exitLabel);
		exitLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				removeBees();
				SCORE_CLASS.writeScore();
				exitMessage(); // writes score and displays message
			}
		});
		// End Exit Label

		// Settings icon
		ImageIcon settingsIcon = null;
		try {
			settingsIcon = new ImageIcon(ImageIO.read(Game.class.getResource("/resources/settings.png")));
		} catch (IOException ex) {
			System.out.println(ex);
		}

		settings = new JLabel(settingsIcon);
		settings.setLayout(null);
		settings.setBounds(80, 0, 80, 80);
		settings.setLocation(width - 100, 10);
		settings.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				controlSettingsMenu();
			}
		});
		mainFrame.add(settings);
		// End of settings Icon

		mainFrame.setVisible(true);
		mainFrame.repaint();

		mainFrame.addMouseListener(new MouseAdapter() { // When Image is clicked
			@Override
			public void mousePressed(MouseEvent e) {
				if (pause == false) {
					SCORE_CLASS.deductScore();
				}
			}
		});
	} // End of function

	public void introAnimation() { // Displays save the bees badge before game start
		JLabel label = null;
		try {
			Icon icon = new ImageIcon(ImageIO.read(Game.class.getResource("/resources/saveTheBees.png")));
			label = new JLabel(icon);
			label.setSize(width, height);
			label.setVisible(true);
			mainFrame.add(label);
			mainFrame.pack();
		} catch (IOException ex) {
			System.out.println(ex);
		}
		try {
			Thread.sleep(2000); // 2000 millisecond delay before removing label
			mainFrame.remove(label);
		} catch (InterruptedException ex) {
			System.out.println(ex);
		}
	}

	public void controlSettingsMenu() { // Checks if menu is open
		if (isSettingsMenuOpen == false) { // If Settings isn't open
			settingsMenu(); // Open menu
		} else { // If settings IS open
			isSettingsMenuOpen = false; // Close settings menu
			mainFrame.remove(settingsFrame);
			mainFrame.repaint();
			setBees(newNumBees);
			pause = false; // unpause the miss click listener
		}
	}

	public void settingsMenu() { // Settings Menu
		if (settingsMenuLock == false) { // If Intro animation isn't in progress
			removeBees(); // Removes bees
			isSettingsMenuOpen = true; // Tells the program the menu is already open

			settingsFrame = new JPanel(new GridLayout(5, 1)); // Button grid (Change number for more buttons)

			// Creating JButtons
			numBeeBtn = new JButton("Number of Bees"); // button to change the number of bees
			difficultyBtn = new JButton("Change Difficulty"); // button to change the difficulty
			resetBtn = new JButton("Reset High Score"); // button to reset score
			closeBtn = new JButton("Exit Game"); // button to exit game
			closeMenu = new JButton("Continue"); // button to close menu

			// Button to change number of Bees
			numBeeBtn.setSize(400, 100);
			numBeeBtn.setForeground(Color.WHITE);
			numBeeBtn.setFont(new Font("Arial", Font.BOLD, 30));
			numBeeBtn.setBackground(Color.DARK_GRAY);
			numBeeBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						String[] dropDownMenu = { "10", "20", "30" };
						String option = (String) JOptionPane.showInputDialog(null,
								"Please enter the number of bees you would like", "Number of Bees",
								JOptionPane.PLAIN_MESSAGE, null, dropDownMenu, newNumBees);
						SCORE_CLASS.resetScore();
						if (option != null) {
							newNumBees = Integer.parseInt(option);
						} else {
							newNumBees = 20;
						}
					} catch (HeadlessException | NumberFormatException ex) {
					}
				}
			});

			// Button to change the difficulty
			difficultyBtn.setSize(400, 100);
			difficultyBtn.setForeground(Color.WHITE);
			difficultyBtn.setFont(new Font("Arial", Font.BOLD, 30));
			difficultyBtn.setBackground(Color.DARK_GRAY);
			difficultyBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						String optionsList[] = { "Easy", "Normal", "Hard" };
						String option = (String) JOptionPane.showInputDialog(null,
								"Please enter the difficulty of bees you would like", "Difficulty",
								JOptionPane.PLAIN_MESSAGE, null, optionsList, optionsList[difficulty]);
						if (option == null) {
							option = "Normal";
						}
						switch (option) {
						case "Easy":
							difficulty = 0;
							delay = 7;
							break;
						case "Normal":
							difficulty = 1;
							delay = 5;
							break;
						case "Hard":
							difficulty = 2;
							delay = 3;
							break;
						}
					} catch (HeadlessException | NumberFormatException ex) {
					}
				}
			});

			// Reset score Buttons
			resetBtn.setSize(400, 100);
			resetBtn.setForeground(Color.WHITE);
			resetBtn.setFont(new Font("Arial", Font.BOLD, 30));
			resetBtn.setBackground(Color.DARK_GRAY);
			resetBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SCORE_CLASS.resetHighScore();
					SCORE_CLASS.resetScore();
					SCORE_CLASS.repaintScore();
				}
			});

			// Close game button
			closeBtn.setSize(400, 100);
			closeBtn.setForeground(Color.WHITE);
			closeBtn.setFont(new Font("Arial", Font.BOLD, 30));
			closeBtn.setBackground(Color.red);
			closeBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					SCORE_CLASS.writeScore();
					exitMessage();
				}
			});

			// Close menu button
			closeMenu.setSize(400, 100);
			closeMenu.setForeground(Color.DARK_GRAY);
			closeMenu.setFont(new Font("Arial", Font.BOLD, 30));
			closeMenu.setBackground(Color.green);
			closeMenu.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					mainFrame.remove(settingsFrame);
					mainFrame.repaint();
					setBees(newNumBees);
					pause = false; // unpause the miss click listener
					isSettingsMenuOpen = false;
				}
			});

			// Settings Frame dimensions and customisations
			settingsFrame.setSize(400, 500);
			settingsFrame.setLocation((width - 400) / 2, (height - 500) / 2);
			settingsFrame.setBackground(Color.GRAY);

			settingsFrame.add(numBeeBtn);
			settingsFrame.add(difficultyBtn);
			settingsFrame.add(resetBtn);
			settingsFrame.add(closeBtn);
			settingsFrame.add(closeMenu);

			settingsFrame.setVisible(true);
			mainFrame.setFocusable(true);
			settingsFrame.setFocusable(true);
			settingsFrame.requestFocus();
			mainFrame.add(settingsFrame);
			mainFrame.revalidate();
			mainFrame.repaint();
		}
	}

	private void bees() { // Main function to run program
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(Game.class.getResource("/resources/pop.wav")));
			clip.setMicrosecondPosition(1000000000);
			clip.start();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			System.out.println(e);
		}

		swatImage = null;
		try {
			ImageIcon swatIcon = new ImageIcon(ImageIO.read(Game.class.getResource("/resources/click.png")));
			swatImage = swatIcon.getImage();
		} catch (IOException e1) {
			System.out.println(e1);
		} // Image that is displayed when bee is clicked
		swatImage = swatImage.getScaledInstance((width / 15), (height / 10), Image.SCALE_DEFAULT);
		beeImage = null;
		try {
			ImageIcon beeIcon = new ImageIcon(ImageIO.read(Game.class.getResource("/resources/bee.png")));
			beeImage = beeIcon.getImage();
		} catch (IOException e1) {
			System.out.println(e1);
		} // Image for bee
		beeImage = beeImage.getScaledInstance(width / 10, height / 10, Image.SCALE_DEFAULT); 
		beeFlippedImage = null;
		try {
			ImageIcon beeFlippedIcon = new ImageIcon(ImageIO.read(Game.class.getResource("/resources/beeFlip.png")));
			beeFlippedImage = beeFlippedIcon.getImage();
		} catch (IOException e1) {
			System.out.println(e1);
		} // Image for bee facing opposite direction
		beeFlippedImage = beeFlippedImage.getScaledInstance(width / 10, height / 10, Image.SCALE_DEFAULT);
		this.setBees(10);
		this.runGame();
	}

	private void setBees(int amount) { // Sets the number of Bees
		for (int i = 0; i < numBees; i++) { // For number of Bees
			bees[i].kill();
		}

		numBees = -1;
		SCORE_CLASS.resetClicked();
		Random numGenerator = new Random(); // Random number
		Bee[] newBees = new Bee[amount];
		for (int i = 0; i < amount; i++) { // For number of Bees
			newBees[i] = new Bee(mainFrame, this, numGenerator, width, height, swatImage, beeImage, beeFlippedImage,
					difficulty);
		}

		bees = newBees;
		numBees = amount;
		SCORE_CLASS.beeCount(numBees);
		mainFrame.repaint();
	}

	private void tick() { // Bee refresh rate (movement mechanics)
		Point mouseLocation = mainFrame.getMousePosition();
		for (int i = 0; i < numBees; i++) {
			bees[i].tick(mouseLocation);
		}
	}

	private void runGame() { // Function keeps the bees moving
		long lastTime = 0;
		long timeNow;

		while (true) {
			// timeNow = System.nanoTime();
			timeNow = System.currentTimeMillis();
			if (timeNow > (lastTime + (delay / 2))) { // Speed of bee movement (difficulty)
				tick(); // Add more to change speed
				lastTime = timeNow;
			}
		}
	}

	public void clickSound() { // Plays a sound when bees are clicked
		clip.stop();
		clip.setFramePosition(0); // Resetting clip position
		clip.start();
	}

	public void removeBees() { // removes bees
		for (int i = 0; i < numBees; i++) { // For number of Bees
			bees[i].kill();
		}
	}

	public int returnDifficulty() { // Other classes use this to gain the difficulty settings
		return this.difficulty + 1;
	}

	public void gameOver() { // Function to display options once a game is complete

		pause = true;
		removeBees();

		if (bees == null) { // If no bees left:
			int menuChoice = JOptionPane.showConfirmDialog(null,
					"You have clicked all the bees away!\n" + "Would you like to play another?", "Congratulations!",
					JOptionPane.YES_NO_OPTION);

			if (menuChoice == 0) { // If the user wants to play another game
				try {
					SCORE_CLASS.writeScore(); // Checks the score for a new record and if true it saves it.
					SCORE_CLASS.resetScore(); // Reset current games score and repaints label

					Robot robot = new Robot();
					robot.keyPress(KeyEvent.VK_ESCAPE); // Program presses escape to call settings menu rather than
														// creating a new menu
					robot.keyRelease(KeyEvent.VK_ESCAPE);
				} catch (AWTException ex) {
					System.out.println("Error with robot automatic key press in line 424 \n" + ex);
				}
			} else { // Exit
				SCORE_CLASS.writeScore(); // Checks the score for a new record and if true it saves it.
				exitMessage();
			}
		}
	} // End of function

	public void exitMessage() {
		pause = true; // Pauses score deduction listener
		removeBees(); // Removes Bees from frame

		new java.util.Timer().schedule( // Scheduling system exit for 3 seconds from now.
				new java.util.TimerTask() {
					@Override
					public void run() {
						System.exit(0);
					}
				}, 3000 // 3 second delay before shutdown
		);
		// Notification pop-up before shutdown schedule
		JOptionPane optionPane = new JOptionPane("Thank you for playing!\n Automatic shutdown shortly",
				JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[] {}, null);
		JDialog dialog = new JDialog();
		dialog.setModal(true);
		dialog.setSize(300, 300);
		dialog.setContentPane(optionPane);
		dialog.setLocationRelativeTo(null);
		dialog.pack();
		dialog.setVisible(true);
	}

	public static void setTimeout(Runnable runnable, int delay) { // Delay
		new Thread(() -> {
			try {
				Thread.sleep(delay);
				runnable.run();
			} catch (InterruptedException e) {
				System.err.println("Error with timeout \n" + e);
			}
		}).start();
	} // End of function
} // End of Game Class
