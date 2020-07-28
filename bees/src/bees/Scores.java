package bees;

/* ---------------- Details ------------------
  * Authors: Cameron Morrison & Ged Robertson
  * Program: The Bee Game
  * Objective: Save the bees!
  * Year created: 2020   	
  * ------------------------------------------*/
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Scores {  
   
    private static int beesClicked; //The number of bees clicked
    private static int score; //The user score (bees clicked - missed clicks)
    private static int numberOfBees; //The number of Bees
    
    
    public int readScore() { //Reads score in from text file
        Path path = Paths.get(System.getenv("APPDATA") + "\\update_log.txt"); //Looking for file
        if (Files.exists(path)) {
            try {
                try ( //If file exists
                    Scanner sc = new Scanner(path)) {
                    sc.useDelimiter("\\Z");
                    String data = sc.next();
                    Scores.score = Integer.valueOf(data);
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
        }
        return Scores.score;
    }
    
    public void beeCount(int numBees){
        Scores.numberOfBees = numBees;
    }
    
    public void beeClicked() { //Counts score and calls game menu when all bees are clicked.
        beesClicked++;
        score = score + 1;
        repaintScore();
        
        if (beesClicked == numberOfBees) {  // if bees clicked is the same as number of bees
            Game game = new Game(); //Creating instance of Game class
            game.gameOver(); // Calls Game menu
        } 
    }
    
    public int getScore(){ //Returns score
        return Scores.score;
    }
    
    public void resetScore(){ //Resets score
        Scores.beesClicked = 0;
        Scores.score = 0;
    }
    
    public void resetClicked(){ //Resets bees clicked
        Scores.beesClicked = 0;
    }
    
    public void repaintScore() { //Updates score label
        Game.scoreLabel.setText("Bees Saved: " + score);
    }
        
    public void writeScore() { //Writes score to text file
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

        if (Files.notExists(path)) { 
            try (PrintWriter writer = new PrintWriter(System.getenv("APPDATA") + "\\update_log.txt", "UTF-8")) { //If file does not exist
                    writer.print(score); //Outputs score to update_log.txt
                    writer.close();
                } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            }
        }
    }

    void deductScore() { //Deducts score when user clicks background instead of bee
        score -= 1; //If user misses a click on bees, subtract 1 score
        if (score < 0) {
            score = 0; //Score cannot go below zero
        }
        repaintScore();
    }
} //End of Class Scores
