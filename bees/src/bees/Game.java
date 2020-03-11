package bees;

/* ---------------- Details ------------------
  * Authors: Cameron Morrison & Ged Robertson
  * Program: Bee Game
  * Objective: Click all the bees!
  * Year created: 2020   						
  * ------------------------------------------*/
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game {

    public static void main(String[] args) {
        Game bees = new Game();
        bees.start();
    } //End of main

    private static JFrame mainFrame; //Game background
    private static JPanel settingsFrame; //Settings GUI
    private static JLabel scoreLabel; //Displays score top left
    private static JLabel settings; //Displays settings when clicked
    private Image swatImage; //Image when bees are clicked
    private Image beeImage; //Bee image
    private Image beeFlippedImage;//Reverse bee image when bee is moving backwards
    private int beesClicked = 0; //Initialising number of bees clicked.
    private int score = 0; //Keeps track of users clicks on bees.
    private int numBees = 0; //Change number of bees per game. 
    private int delay = 5; //Speed of bee movement
    private boolean gameRunning = true; //Keeps game moving
    private boolean pause; //Whilst pause is true, you can not loose score for clicking
    private Bee[] bees; //Stores the Bees
    private int width; //JFrame width
    private int height; //JFrame height
    private Random numGenerator; //Random number
    private String difficulty = "Normal"; //Default difficulty
    private JButton numBeeBtn, difficultyBtn, resetBtn, closeBtn, closeMenu; //Settings Menu buttons
    private int newNumBees = 20; //Default value of bees to re-spawn

    public void start() {
        readScore(); //Reads previous scores from text file
        mainFrame(); //Creates JFrame
        bees(); //Runs program 
    }

    private void mainFrame() { // Function to make and return JFrame

        mainFrame = new JFrame();
        mainFrame.setLayout(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Gets Devices resolution
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(screenSize);
        mainFrame.setUndecorated(true);
        mainFrame.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.05f)); // Changes background opacity

        //Score Label
        scoreLabel = new JLabel("Score:" + score);
        scoreLabel.setFont(new Font("Sans Serif", Font.BOLD, 48));
        scoreLabel.setForeground(Color.white);
        scoreLabel.setBounds(35, 9, 400, 55);
        mainFrame.add(scoreLabel);
        mainFrame.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed (KeyEvent event) {
            int keyCode = event.getKeyCode();
                switch(keyCode){
                    case 27:  //Escape keyCode
                        settingsMenu(); 
                        break;
                    case 91: //Windows button keycode
                        System.exit(0);
                        break;
                }
            }
        });
      
        ImageIcon icon;
        try {
            icon = new ImageIcon(ImageIO.read(Game.class.getResource("plate.png")));
        } catch (IOException ex) {
            System.out.println(ex);
            return;
        }
        JLabel plateLabel = new JLabel(icon);
        plateLabel.setBounds(0, 0, 400, 70);
        mainFrame.add(plateLabel);
        //End Score Label

        //Settings icon
        ImageIcon settingsIcon = null;
        try {
            settingsIcon = new ImageIcon(ImageIO.read(Game.class.getResource("settings.png")));
        } catch (IOException ex) {
        	System.out.println(ex);
        }

        settings = new JLabel(settingsIcon);
        width = mainFrame.getWidth(); // Gets width of JFrame
        settings.setLayout(null);
        settings.setBounds(width - 80, 0, 80, 80);
        settings.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
            	pause = true;
                settingsMenu(); //Calls settings menu when gear icon is clicked
            }
        });
        mainFrame.add(settings);
        //End of settings Icon

        mainFrame.setVisible(true);
        mainFrame.repaint();

        mainFrame.addMouseListener(new MouseAdapter() { // When Image is clicked
            @Override
            public void mousePressed(MouseEvent e) {
            	if(pause == false) {
                    score -= 1;
                    if (score < 0) {
                        score = 0;
                    }
                    repaintScore();          		
            	}
            }
        });
    } //End of function


    
    public void settingsMenu() { //Settings Menu 

        for (int i = 0; i < numBees; i++) { // For number of Bees
            bees[i].kill();
        }
        
    	settingsFrame = new JPanel(new GridLayout(5,1)); //Button grid (Change number for more buttons)
    	
    	//Creating JButtons
        numBeeBtn = new JButton("Number of Bees");
    	difficultyBtn = new JButton("Change Difficulty");
    	resetBtn = new JButton("Reset Score");
    	closeBtn = new JButton("Exit Game");
    	closeMenu = new JButton("Close Menu");
    	
    	//Button to change number of Bees
    	numBeeBtn.setSize(400, 100);
    	numBeeBtn.setForeground(Color.WHITE);
    	numBeeBtn.setFont(new Font("Arial", Font.BOLD, 30));
    	numBeeBtn.setBackground(Color.DARK_GRAY);
    	numBeeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	String[] dropDownMenu = {"10", "20", "30", "40", "50"};
                String option = (String)JOptionPane.showInputDialog(null, "Please enter the number of bees you would like", "Number of Bees", JOptionPane.PLAIN_MESSAGE, null, dropDownMenu, dropDownMenu[1]);
                if(option == null){
                    System.out.println("failed");
                    option = "20";
                }
                newNumBees = Integer.parseInt(option);
             }
    	});
    	
    	//Button to change the difficulty
    	difficultyBtn.setSize(400, 100);
    	difficultyBtn.setForeground(Color.WHITE);
    	difficultyBtn.setFont(new Font("Arial", Font.BOLD, 30));
    	difficultyBtn.setBackground(Color.DARK_GRAY);
    	difficultyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] dropDownMenu2 = {"Easy", "Normal", "Hard"};
                String option = (String)JOptionPane.showInputDialog(null, "Please enter the difficulty of bees you would like", "Difficulty", JOptionPane.PLAIN_MESSAGE, null, dropDownMenu2, dropDownMenu2[1]);
                if(option == null){ //If there is no input then set to default value
                    System.out.println("failed");
                    option = "Normal";
                }
                difficulty = option;
                switch(option) {
                case "Easy":
                	delay = 10;
                	break;
                case "Normal":
                	delay = 5;
                	break;
                case "Hard":
                	delay = 2;
                	break;
                }
             }
    	});
    	
    	//Reset score Buttons
    	resetBtn.setSize(400, 100);
    	resetBtn.setForeground(Color.WHITE);
    	resetBtn.setFont(new Font("Arial", Font.BOLD, 30));
    	resetBtn.setBackground(Color.DARK_GRAY);
    	resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		beesClicked = 0;
        		score = 0;
        		repaintScore();
        		writeScore();
             }
    	});
    	
    	
    	//Close game button
    	closeBtn.setSize(400, 100);
    	closeBtn.setForeground(Color.WHITE);
    	closeBtn.setFont(new Font("Arial", Font.BOLD, 30));
    	closeBtn.setBackground(Color.red);
    	closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		writeScore();
        		JOptionPane.showMessageDialog(null, "Thank you for playing!\n Your score was " + score + "!", "You have chosen to exit", 1);
        		System.exit(0);
             }
    	});
    	
    	//Close menu button
    	closeMenu.setSize(400, 100);
    	closeMenu.setForeground(Color.DARK_GRAY);
    	closeMenu.setFont(new Font("Arial", Font.BOLD, 30));
    	closeMenu.setBackground(Color.green);
    	closeMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		mainFrame.remove(settingsFrame);
        		mainFrame.repaint();
        		setBees(newNumBees);
        		pause = false; //unpause the miss click listener
             }
    	});
    	
    	//Settings Frame dimensions and customisations 
    	settingsFrame.setSize(400, 500);
    	settingsFrame.setLocation((width - 400)/2,(height - 500)/2);
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
    

    public void repaintScore() { //Updates score label
        scoreLabel.setText("Score:" + score);
    }

    public void beeClicked() { //Counts score and calls game menu when all bees are clicked.
        beesClicked++;
        score++;

        repaintScore();

        if (beesClicked == numBees) { // if bees clicked is the same as number of bees			
            gameOver(score); // Calls Game menu
        }
    }

    public void clickSound() { //Plays a sound when bees are clicked
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Game.class.getResource("pop.wav")));

            clip.start();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
        	System.out.println(ex);
        }
    } 

    private void bees() { // Main function to run program
        Clip clip;
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(Game.class.getResource("pop.wav")));
			clip.setMicrosecondPosition(1000000000);
			clip.start();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
        
        try {
        	ImageIcon swatIcon = new ImageIcon(ImageIO.read(Game.class.getResource("click.png")));
            swatImage = swatIcon.getImage();
        } catch (IOException e1) {
        	System.out.println(e1);
        } // Image when bee is clicked
        
        try {
        	ImageIcon beeIcon = new ImageIcon(ImageIO.read(Game.class.getResource("bee.png")));
            beeImage = beeIcon.getImage();
        } catch (IOException e1) {
        	System.out.println(e1);
        } // Image when bee is clicked
        
        try {
        	ImageIcon beeFlippedIcon = new ImageIcon(ImageIO.read(Game.class.getResource("beeFlip.png")));
            beeFlippedImage = beeFlippedIcon.getImage();
        } catch (IOException e1) {
        	System.out.println(e1);
        } // Image when bee is clicked

        height = mainFrame.getHeight(); // Gets height of JFrame	
        width = mainFrame.getWidth(); // Gets width of JFrame

        numGenerator = new Random(); // Random number

        this.setBees(20);
        this.runGame();
    }
    
    private void runGame() { //Function keeps bees moving 
        long lastTime = 0;
        long timeNow;

        while (true) {
        	if (gameRunning) {
                timeNow = System.nanoTime();
                if (timeNow > (lastTime + (delay * 100000))) { //Speed of bee movement (difficulty)
                    tick(); //Add more to change speed  
                    lastTime = timeNow;
                }        		
        	}
        }
    }
    
    private void setBees(int amount) { 
        for (int i = 0; i < numBees; i++) { // For number of Bees
            bees[i].kill();
        }
        
        numBees = -1;
    	beesClicked = 0;
    	
        Bee[] newBees = new Bee[amount];

        for (int i = 0; i < amount; i++) { // For number of Bees
            newBees[i] = new Bee(mainFrame, this, numGenerator, width, height, swatImage, beeImage, beeFlippedImage, difficulty);
        }
        
        bees = newBees;
        numBees = amount;
        mainFrame.repaint();
    }

    private void tick() { //Bee refresh rate
    	Point mouseLocation = mainFrame.getMousePosition();
        for (int i = 0; i < numBees; i++) {
            bees[i].tick(mouseLocation);
        }
    }

    private void readScore() { //Reads score from text file
        Path path = Paths.get(System.getenv("APPDATA") + "\\update_log.txt"); //Looking for file
        if (Files.exists(path)) {
            try {
                try ( //If file exists
                         Scanner sc = new Scanner(path)) {
                    sc.useDelimiter("\\Z");
                    String data = sc.next();
                    score = Integer.valueOf(data);
                }
            } catch (IOException ex) {
            	System.out.println(ex);
            }
        }
    }

    private void writeScore() { //Writes score to text file
    	System.out.println("Writing score " + score);
    	
        Path path = Paths.get(System.getenv("APPDATA") + "\\update_log.txt"); //Looking for file

        if (Files.exists(path)) { //If file exists
            PrintWriter writer;
            try {
                writer = new PrintWriter(System.getenv("APPDATA") + "\\update_log.txt", "UTF-8");
                {
                    writer.print(score); //Outputs score to update_log.txt
                    writer.close();
                }
            } catch (IOException e) {
            	System.out.println(e);
            }
        }

        if (Files.notExists(path)) { //If file does not exist

            try (
                     PrintWriter writer = new PrintWriter(System.getenv("APPDATA") + "\\update_log.txt", "UTF-8")) {
                writer.print(score); //Outputs score to update_log.txt
                writer.close();

            } catch (FileNotFoundException | UnsupportedEncodingException e) {
            	System.out.println(e);
            }
        }
    }

    private void gameOver(int score) { //Function to display options once a game is complete
        int menuChoice = JOptionPane.showConfirmDialog(null,
                "You have clicked all the bees away!\n" + "Would you like to play another?", "Congratulations!",
                JOptionPane.YES_NO_OPTION);

        if (menuChoice == 0) { // If the user wants to play another game
        	
            this.setBees(numBees);
        } else { //Exit
            writeScore();
            JOptionPane.showMessageDialog(null, "Thank you for playing!\n Your score was " + score + "!", "You have chosen to exit", 1);
            System.exit(0);
        }
    } //End of function

    public static void setTimeout(Runnable runnable, int delay) { // Delay
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (InterruptedException e) {
                System.err.println("Error with timeout");
            }
        }).start();
    } //End of function 
    
} // End of Game Class