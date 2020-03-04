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
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Game {

    public static void main(String[] args) {
        Game bees = new Game();
        bees.start();
    } //End of main

    private static JFrame mainFrame; //Game background
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
    private Bee[] bees; //Bees
    private int width; //JFrame width
    private int height; //JFrame height
    private Random numGenerator; 
    private String difficulty = "Normal";

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
        //mainFrame.addKeyListener(new KeyListener());
        mainFrame.addKeyListener(new KeyAdapter() {
        @Override public void keyPressed (KeyEvent event) {
            int keyCode = event.getKeyCode();
                switch(keyCode){
                    case 27: 
                        settingsMenu(); //Calls settings menu when gear icon is clicked
                        break;
                    case 91:
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

                score -= 1;
                if (score < 0) {
                    score = 0;
                }
                repaintScore();
            }
        });
    } //End of function

        
    public void settingsMenu() { //Settings menu
        
        for (int i = 0; i < numBees; i++) { // For number of Bees
            bees[i].kill();
        }
        
        String[] dropDownMenu = {"10", "20", "30", "40", "50"};
        String option = (String)JOptionPane.showInputDialog(null, "Please enter the number of bees you would like", "Number of Bees", JOptionPane.PLAIN_MESSAGE, null, dropDownMenu, dropDownMenu[1]);
        if(option == null){
            System.out.println("failed");
            option = "20";
        }
        int newNumBees = Integer.parseInt(option);
        
        String[] dropDownMenu2 = {"Easy", "Normal", "Hard"};
        option = (String)JOptionPane.showInputDialog(null, "Please enter the difficulty of bees you would like", "Difficulty", JOptionPane.PLAIN_MESSAGE, null, dropDownMenu2, dropDownMenu2[1]);
       
        if(option == null){
            System.out.println("failed");
            option = "Normal";
        }
                
        this.difficulty = option;
        switch(option) {
        case "Easy":
        	this.delay = 10;
        	break;
        case "Normal":
        	this.delay = 5;
        	break;
        case "Hard":
        	this.delay = 2;
        	break;
        }
        
        this.setBees(newNumBees);
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
    
    private void runGame() {
        long lastTime = 0;
        long timeNow;

        while (true) {
        	if (gameRunning) {
                timeNow = System.nanoTime();
                if (timeNow > (lastTime + (delay * 100000))) {
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

    private void tick() {
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

