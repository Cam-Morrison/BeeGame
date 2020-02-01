package bees;

/* ---------------- Details ------------------
  * Authors: Cameron Morrison & Ged Robertson
  * Program: Bees
  * Objective: Click all the bees!
  * Year created: 2020   						
  * ------------------------------------------*/

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class Bees {

	public static void main(String[] args) {
		bees();
	} //End of main

	private static Image swat;
	private static int beesClicked;
	private static int delay;
	private static int numBees;

	
	private static void bees() { // Main function to run program

		delay = 60; // Changes Bee movement speed
		numBees = 20; //Changes how many Bees there are
		beesClicked = 0; // How many bees have been clicked
		
		if (numBees % 10 != 0) { // Number of bees has to be a multiple of 10
			numBees = (10 - numBees % 10) + numBees; // Rounds number down to a multiple of 10
		}
		
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
							bees[move].setLocation(numGenerator.nextInt((width - 210) + 1),
									numGenerator.nextInt((height - 150) + 1));
						} // Stops bees from going over the edges ^^
					}
					TimeUnit.MILLISECONDS.sleep(delay); // Delay is changeable at top of document
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} //End of function
	
	
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
	} //End of function

	
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
				//clickSound(); //Plays noise
				
				setTimeout(() -> {
					mainFrame.remove(label); // remove bee
					mainFrame.revalidate();
					mainFrame.repaint();
					beesClicked++; // Add to counter

					if (beesClicked == numBees) { // if bees clicked is the same as number of bees
						
						int score = score(beesClicked); //Saves score
						gameOver(score); // Calls Game menu
					}

				}, 100); // Set effect image invisible after 100Ms
			}
		});

		mainFrame.add(label);
		label.setSize(icon.getIconWidth(), icon.getIconHeight());
		
		return label; // Returns Bee image
	} //End of function

	

//	private static void clickSound() { //Function to play sound when Bee is clicked
		
//	} //End of Function 
	
	private static int score(int beesClicked){ //Function to store score
		
		int score = 0; 
		Path path = Paths.get("score.txt"); //Looking for file

		if (Files.exists(path)) { //If file exists
			
			try {
				Scanner sc = new Scanner(path);
				sc.useDelimiter("\\Z"); 
			    String data = sc.next(); 
			    score = Integer.valueOf(data);
			    sc.close();
			    score += beesClicked; //Adds bees clicked to current number in file
			    PrintWriter writer = new PrintWriter("score.txt", "UTF-8");
    		    writer.print(score); //Outputs score to score.txt
       		    writer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}   

		if (Files.notExists(path)) { //If file does not exist
			
  		    try (PrintWriter writer = new PrintWriter("score.txt", "UTF-8")) {
        		 writer.print(beesClicked); //Outputs score to score.txt
           		 writer.close();
           		 score = beesClicked;
           		    
  		    } catch (FileNotFoundException | UnsupportedEncodingException e) {
				e.printStackTrace();
       		}
		}   
		
		return score; //Return score
	} //End of function 
	
	
	private static void gameOver(int score) { //Function to display options once a game is complete

		int menuChoice = JOptionPane.showConfirmDialog(null,
				"You have clicked all the bees away!\n" + "Would you like to play another?", "Congratulations!",
				JOptionPane.YES_NO_OPTION);

		if (menuChoice == 0) { // If the user wants to play another game
			bees(); 
		}

		else { //Exit
			JOptionPane.showMessageDialog(null, "Thank you for playing!\n You have clicked " + score + " bees in total!", "You have chosen to exit", 1);
			System.exit(0);
		}
	} //End of function
	

	public static void setTimeout(Runnable runnable, int delay) { // Delay
		new Thread(() -> {
			try {
				Thread.sleep(delay);
				runnable.run();
			} catch (Exception e) {
				System.err.println(e);
			}
		}).start();
	} //End of function
	
} // End of Program