package bees;

/* ---------------- Details ------------------
  * Authors: Cameron Morrison & Ged Robertson
  * Program: The Bee Game
  * Objective: Save the bees!
  * Year created: 2020   	
  * ------------------------------------------*/
import static bees.Game.setTimeout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Image;
import java.awt.Point;

public class Bee {

    private JLabel label; //The Bee
    private boolean dead = false; //Has the bee been clicked?
    private int currentX; //Current X location
    private int currentY; //Current Y location
    private int goingX = 0; //New X location
    private int goingY = 0; //New Y location
    private final Image swatImage; //Image when bee is clicked
    private final Image beeImage; //Image of bee
    private final Image beeFlippedImage; //Reversed image of bee
    private int currentIcon = 1; //Icon to display
    private final int frameWidth; //width of screen
    private final int frameHeight; //height of screen
    private final Random numGenerator; //Random (for new location coordinates)
    private final JFrame mainFrame; //JFrame container of program
    private final int difficulty; //Difficulty settings
    private Scores score = new Scores(); //Instance of score class
    ImageIcon icon; 
  
    public Bee(JFrame mainFrame, Game bees, Random numGenerator, int frameWidth, int frameHeight, Image swatImage, Image beeImage, Image beeFlippedImage, int difficulty) {

        this.numGenerator = numGenerator;
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;
        this.mainFrame = mainFrame;
        this.swatImage = swatImage;
        this.beeImage = beeImage;
        this.beeFlippedImage = beeFlippedImage;
        this.difficulty = difficulty;

        currentX = numGenerator.nextInt((frameWidth - 210) + 1);
        currentY = numGenerator.nextInt((frameHeight - 150) + 1);
        goingX = numGenerator.nextInt((frameWidth - 210) + 1);
        goingY = numGenerator.nextInt((frameHeight - 150) + 1);

        icon = new ImageIcon(beeImage);
        label = new JLabel(icon);   
        label.addMouseListener(new MouseAdapter() { // When Image is clicked
            @Override
            public void mousePressed(MouseEvent e) {
                dead = true; //Stops bee from moving
                bees.clickSound(); //Plays noise
                label.removeMouseListener(this); // Removes mouse listener so game wont finish before ALL bees are
                setIcon(0);// Change Image to effect
                setTimeout(() -> {
                    mainFrame.remove(label);
                    mainFrame.revalidate();
                    mainFrame.repaint();             
                }, 200); // Click animation duration
                
                score.beeClicked(); // Add to counter 
                //beesClicked() added after click animation to stop final bee image transition from freezing when gameOver() is called.
            }
        });
        mainFrame.add(label);
        label.setVisible(true);
        label.setSize(icon.getIconWidth(), icon.getIconHeight());
    } //End Function Bee


    private void setIcon(int iconNumber) { //Change labels image
        if (iconNumber != currentIcon) {
            currentIcon = iconNumber;
            switch (iconNumber) {
                case 0:
                    icon.setImage(swatImage); //When clicked
                    break;
                case 1:
                    icon.setImage(beeImage); //Original
                    break;
                case 2:
                    icon.setImage(beeFlippedImage); //Reversed
                    break;
            }
            label.repaint();
        }
    }

    public void kill() { //Kill label when clicked
        dead = true;
        mainFrame.remove(label);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void tick(Point mouseLocation) { //Function to smoothly move Bees
        if (dead == false) {
            int multiplier = 1;
            if (mouseLocation != null && (Math.abs(mouseLocation.x - (currentX + 75)) <= 100 && Math.abs(mouseLocation.y - (currentY + 75)) <= 100)) {
                multiplier = difficulty + 1;
            }
            if (Math.abs(currentX - goingX) <= multiplier) {
                currentX = goingX;
            }
            if (Math.abs(currentY - goingY) <= multiplier) {
                currentY = goingY;
            }
            if (currentY == goingY && currentX == goingX) {
                goingX = numGenerator.nextInt((frameWidth - 210) + 1);
                goingY = numGenerator.nextInt((frameHeight - 150) + 1);
            }
            if (currentX > goingX) {
                currentX -= 1 * multiplier;
                setIcon(2);
            } else if (currentX < goingX) {
                currentX += 1 * multiplier;
                setIcon(1);
            }
            if (currentY > goingY) {
                currentY -= 1 * multiplier;
            } else if (currentY < goingY) {
                currentY += 1 * multiplier;
            }
            label.setLocation(currentX, currentY);       
        }
    } //End Function tick
    
} //End Class Bee
