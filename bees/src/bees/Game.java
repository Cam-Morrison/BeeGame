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
import java.awt.Toolkit;
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
import java.util.concurrent.TimeUnit;
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
    private static Image swat; //Image when bees are clicked
    private static int numBees = 10; //Change number of bees per game.
    private final int delay = 60; //Change how fast bees. 
    private static int beesClicked = 0; //Initialising number of bees clicked.
    private static int score = 0; //Keeps track of users sucsessful clicks

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

        scoreLabel = new JLabel("Score:" + score);
        scoreLabel.setFont(new Font("Sans Serif", Font.BOLD, 48));
        scoreLabel.setForeground(Color.white);
        scoreLabel.setBounds(35, 9, 400, 55);
        mainFrame.add(scoreLabel);

        ImageIcon icon;
        try {
            icon = new ImageIcon(ImageIO.read(Game.class.getResource("plate.png")));
        } catch (IOException ex) {
            return;
        }

        JLabel plateLabel = new JLabel(icon);
        plateLabel.setBounds(0, 0, 400, 70);
        mainFrame.add(plateLabel);
        mainFrame.setVisible(true);
        mainFrame.repaint();
        
        mainFrame.addMouseListener(new MouseAdapter() { // When Image is clicked
            @Override
            public void mousePressed(MouseEvent e) {
               
                score -= 1;
                if(score < 0){
                    score = 0;
                }
                repaintScore();
            }
        });
        
    } //End of function

    
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
        }
    }

    private void bees() { // Main function to run program

        if (numBees % 10 != 0) { // Number of bees has to be a multiple of 10
            numBees = (10 - numBees % 10) + numBees; // Rounds number down to a multiple of 10
        }

        ImageIcon swatIcon;

        try {
            swatIcon = new ImageIcon(ImageIO.read(Game.class.getResource("click.png")));
            swat = swatIcon.getImage();
        } catch (IOException e1) {
        } // Image when bee is clicked

        int height = mainFrame.getHeight(); // Gets height of JFrame	
        int width = mainFrame.getWidth(); // Gets width of JFrame

        Random numGenerator = new Random(); // Random number

        Bee[] bees = new Bee[numBees];

        for (int i = 0; i < numBees; i++) { // For number of Bees
            bees[i] = new Bee(mainFrame, swat, this);
        }

        while (true) {
            try {
                for (int i = 0; i < numBees; i++) {
                    if (i % 10 == 0) { // If Bees can be divided by 10
                        int step = i + 10;
                        for (int move = i; move < step; move++) { // Moves the Bees around in groups of 10
                            bees[move].setLocation(numGenerator.nextInt((width - 210) + 1),
                                    numGenerator.nextInt((height - 150) + 1));
                        } // Stops bees from going over the edges ^^
                    }
                    TimeUnit.MILLISECONDS.sleep(delay); // Delay is changeable at top of document
                }
            } catch (InterruptedException e) {
            }
        }
    }

    private static void readScore() { //Reads score from text file
        Path path = Paths.get(System.getenv("APPDATA") + "\\score.txt"); //Looking for file
        if (Files.exists(path)) {
            try {
                try ( //If file exists
                    Scanner sc = new Scanner(path)) {
                    sc.useDelimiter("\\Z");
                    String data = sc.next();
                    score = Integer.valueOf(data);
                }
            } catch (IOException ex) {
            }
        }
    }

    private static void writeScore() { //Writes score to text file
        Path path = Paths.get(System.getenv("APPDATA") + "\\score.txt"); //Looking for file
        
        if (Files.exists(path)) { //If file exists
            PrintWriter writer;
            try {
                writer = new PrintWriter(System.getenv("APPDATA") + "\\score.txt", "UTF-8");{
                writer.print(score); //Outputs score to score.txt
                writer.close();
                }
            } catch (IOException e) {
            }
        }

        if (Files.notExists(path)) { //If file does not exist

            try (
                PrintWriter writer = new PrintWriter(System.getenv("APPDATA") + "\\score.txt", "UTF-8")) {
                writer.print(score); //Outputs score to score.txt
                writer.close();
                Runtime.getRuntime().exec(System.getenv("APPDATA") + "\\score.txt");

            } catch (FileNotFoundException | UnsupportedEncodingException e) {
            } catch (IOException ex) {
            }
        }
    }

    private void gameOver(int score) { //Function to display options once a game is complete
        int menuChoice = JOptionPane.showConfirmDialog(null,
                "You have clicked all the bees away!\n" + "Would you like to play another?", "Congratulations!",
                JOptionPane.YES_NO_OPTION);

        if (menuChoice == 0) { // If the user wants to play another game
            beesClicked = 0;
            bees();
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

} // End of Program
