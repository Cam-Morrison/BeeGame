package bees;

/* ---------------- Details ------------------
  * Authors: Cameron Morrison & Ged Robertson
  * Program: The Bee Game
  * Objective: Save the bees!
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
    private static JLabel exitLabel; //button to exit program
    private Image swatImage; //Image when bees are clicked
    private Image beeImage; //Bee image
    private Image beeFlippedImage;//Reverse bee image when bee is moving backwards
    private int beesClicked = 0; //Initialising number of bees clicked.
    private int score = 0; //Keeps track of users clicks on bees.
    private int numBees = 0; //Change number of bees per game. 
    private int delay = 5; //Speed of bee movement
    private boolean gameRunning = true; //Keeps game moving
    private boolean pause; //Whilst pause is true, you can not loose score for clicking
    private boolean isSettingsMenuOpen = false; //When pressing escape twice it causes glitch, adding fail safe
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
        bees(); //Adds Bees and controls movement
    }

    private void mainFrame() { // Function to make and return JFrame

        mainFrame = new JFrame();
        mainFrame.setLayout(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // Gets Devices resolution
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(screenSize);
        mainFrame.setUndecorated(true);
        
        width = mainFrame.getWidth();
        height = mainFrame.getHeight();
        
        //Background Image for the game
        try{ mainFrame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("background.png")))));
        }catch(IOException e){
           e.printStackTrace();}

        //Score Label
        scoreLabel = new JLabel("Bees Saved: " + score);
        scoreLabel.setFont(new Font("Sans Serif", Font.BOLD, 38));
        scoreLabel.setForeground(Color.white);
        scoreLabel.setBounds(35, 9, 400, 55);
        mainFrame.add(scoreLabel);

        //KeyListener for JFrame
        mainFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                int keyCode = event.getKeyCode();
                switch (keyCode) {
                    case 27:  //Escape keyCode
                        if (isSettingsMenuOpen == false) { //If Settings isn't open
                            settingsMenu();  //Open menu
                        } else { //If settings IS open
                            isSettingsMenuOpen = false; //Close settings menu
                            mainFrame.remove(settingsFrame);
                            mainFrame.repaint();
                            setBees(newNumBees);
                            pause = false; //unpause the miss click listener
                        }
                        break;

                    case 91: //Windows button keycode
                        System.exit(0);
                        break;
                }
            }
        });

        ImageIcon icon = null;
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

        //Exit Label
        ImageIcon exitImg;
        try {
            exitImg = new ImageIcon(ImageIO.read(Game.class.getResource("exit.png")));
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
                pause = true;
                removeBees();
                writeScore();
                exitMessage();
                System.exit(0);
            }
        });
        //End Exit Label

        //Settings icon
        ImageIcon settingsIcon = null;
        try {
            settingsIcon = new ImageIcon(ImageIO.read(Game.class.getResource("settings.png")));
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
                if (pause == false) {
                    score -= 1; //If user misses a click on bees, subtract 1 score
                    if (score < 0) {
                        score = 0; //Score cannot go below zero
                    }
                    repaintScore();
                }
            } 
        });
    } //End of function

    public void settingsMenu() { //Settings Menu 

        removeBees(); //Removes bees
        isSettingsMenuOpen = true; //Tells the program the menu is already open

        settingsFrame = new JPanel(new GridLayout(5, 1)); //Button grid (Change number for more buttons)

        //Creating JButtons
        numBeeBtn = new JButton("Number of Bees"); //button to change the number of bees
        difficultyBtn = new JButton("Change Difficulty"); //button to change the difficulty
        resetBtn = new JButton("Reset Score"); //button to reset score
        closeBtn = new JButton("Exit Game"); //button to exit game
        closeMenu = new JButton("Close Menu"); //button to close menu

        //Button to change number of Bees
        numBeeBtn.setSize(400, 100);
        numBeeBtn.setForeground(Color.WHITE);
        numBeeBtn.setFont(new Font("Arial", Font.BOLD, 30));
        numBeeBtn.setBackground(Color.DARK_GRAY);
        numBeeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String[] dropDownMenu = {"10", "20", "30"};
                String option = (String) JOptionPane.showInputDialog(null, "Please enter the number of bees you would like", "Number of Bees", JOptionPane.PLAIN_MESSAGE, null, dropDownMenu, newNumBees);
                if (option == null) {
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
                String optionsList[] = {"Easy", "Normal", "Hard"};
                difficulty = (String) JOptionPane.showInputDialog(null, "Please enter the difficulty of bees you would like", "Difficulty", JOptionPane.PLAIN_MESSAGE, null, optionsList, difficulty);
                if (difficulty == null) { //If there is no input then set to default value
                    difficulty = "Normal";
                }
                switch (difficulty) {
                    case "Easy":
                        delay = 7;
                        break;
                    case "Normal":
                        delay = 5;
                        break;
                    case "Hard":
                        delay = 3;
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
                exitMessage();
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
                isSettingsMenuOpen = false;
            }
        });

        //Settings Frame dimensions and customisations 
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

    public void removeBees() { //removes bees
        for (int i = 0; i < numBees; i++) { // For number of Bees
            bees[i].kill();
        }
    }

    public void repaintScore() { //Updates score label
        scoreLabel.setText("Bees Saved: " + score);
    }

    public void beeClicked() { //Counts score and calls game menu when all bees are clicked.
        beesClicked++;
        score++;

        repaintScore();

        if (beesClicked == numBees) { // if bees clicked is the same as number of bees			
            gameOver(); // Calls Game menu
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
            System.out.println(e);
        }

        swatImage = null;
        try {
            ImageIcon swatIcon = new ImageIcon(ImageIO.read(Game.class.getResource("click.png")));
            swatImage = swatIcon.getImage();
        } catch (IOException e1) {
            System.out.println(e1);
        } // Image that is displayed when bee is clicked
        swatImage = swatImage.getScaledInstance((width/100)*10, (height/100*10), Image.SCALE_DEFAULT); //Smart scaling for swat image
        
        beeImage = null;
        try {
            ImageIcon beeIcon = new ImageIcon(ImageIO.read(Game.class.getResource("bee.png")));
            beeImage = beeIcon.getImage();
        } catch (IOException e1) {
            System.out.println(e1);
        } //Image for bee
        beeImage = beeImage.getScaledInstance((width/100)*10, (height/100*10), Image.SCALE_DEFAULT); //Smart scaling for Bee image
                
        beeFlippedImage = null;
        try {
            ImageIcon beeFlippedIcon = new ImageIcon(ImageIO.read(Game.class.getResource("beeFlip.png")));
            beeFlippedImage = beeFlippedIcon.getImage();
        } catch (IOException e1) {
            System.out.println(e1);
        } //Image for bee facing opposite direction
        beeFlippedImage = beeFlippedImage.getScaledInstance((width/100)*10, (height/100*10), Image.SCALE_DEFAULT); //Smart scaling for Flipped Bee image
        
        numGenerator = new Random(); // Random number
        this.setBees(20);
        this.runGame();
    }

    private void runGame() { //Function keeps the bees moving 
        long lastTime = 0;
        long timeNow;

        while (true) {
            if (gameRunning) {
                //timeNow = System.nanoTime();
                timeNow = System.currentTimeMillis();
                if (timeNow > (lastTime + (delay / 2))) { //Speed of bee movement (difficulty)
                    tick(); //Add more to change speed  
                    lastTime = timeNow;
                }
            }
        }
    }

    private void setBees(int amount) {  //Sets the number of Bees
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

    private void tick() { //Bee refresh rate (movement mechanics)
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

    private void gameOver() { //Function to display options once a game is complete
        int menuChoice = JOptionPane.showConfirmDialog(null,
                "You have clicked all the bees away!\n" + "Would you like to play another?", "Congratulations!",
                JOptionPane.YES_NO_OPTION);

        if (menuChoice == 0) { // If the user wants to play another game

            this.setBees(numBees);

        } else { //Exit
            writeScore();
            exitMessage();
            System.exit(0);
        }
    } //End of function

    public void exitMessage() { //Displays message when game is closed
        JOptionPane.showMessageDialog(null, "Thank you for playing!\n You saved " + score + " bees!", "You have chosen to exit", 1);
    }

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
