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
    private int currentIcon = 1; //Icon to display
    private final int FRAME_WIDTH; //width of screen
    private final int FRAME_HEIGHT; //height of screen
    private final int DIFFICULTY; //Difficulty settings
    private final Image CLICKED_IMAGE; //Image when bee is clicked
    private final Image BEE_IMAGE; //Image of bee
    private final Image REVERSED_BEE_IMAGE; //Reversed image of bee
    private final Random RANDOM_LOCATION; //Random (for new location coordinates)
    private final JFrame MAIN_FRAME; //JFrame container of program 
    private final ImageIcon ICON; //Image of bee or flower
    private Scores score = new Scores(); //Instance of score class
  
    public Bee(JFrame mainFrame, Game bees, Random numGenerator, int frameWidth, int frameHeight, Image swatImage, Image beeImage, Image beeFlippedImage, int difficulty) {

        this.RANDOM_LOCATION = numGenerator;
        this.FRAME_HEIGHT = frameHeight;
        this.FRAME_WIDTH = frameWidth;
        this.MAIN_FRAME = mainFrame;
        this.CLICKED_IMAGE = swatImage;
        this.BEE_IMAGE = beeImage;
        this.REVERSED_BEE_IMAGE = beeFlippedImage;
        this.DIFFICULTY = difficulty;

        currentX = numGenerator.nextInt((frameWidth - 210) + 1);
        currentY = numGenerator.nextInt((frameHeight - 150) + 1);
        goingX = numGenerator.nextInt((frameWidth - 210) + 1);
        goingY = numGenerator.nextInt((frameHeight - 150) + 1);

        ICON = new ImageIcon(beeImage);
        label = new JLabel(ICON);   
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
        label.setSize(ICON.getIconWidth(), ICON.getIconHeight());
    } //End Function Bee


    private void setIcon(int iconNumber) { //Change labels image
        if (iconNumber != currentIcon) {
            currentIcon = iconNumber;
            switch (iconNumber) {
                case 0:
                    ICON.setImage(CLICKED_IMAGE); //When clicked
                    break;
                case 1:
                    ICON.setImage(BEE_IMAGE); //Original
                    break;
                case 2:
                    ICON.setImage(REVERSED_BEE_IMAGE); //Reversed
                    break;
            }
            label.repaint();
        }
    }

    public void kill() { //Kill label when clicked
        dead = true;
        MAIN_FRAME.remove(label);
        MAIN_FRAME.revalidate();
        MAIN_FRAME.repaint();
    }

    public void tick(Point mouseLocation) { //Function to smoothly move Bees
        if (dead == false) {
            int multiplier = 1;
            if (mouseLocation != null && (Math.abs(mouseLocation.x - (currentX + 75)) <= 100 && Math.abs(mouseLocation.y - (currentY + 75)) <= 100)) {
                multiplier = DIFFICULTY + 1;
            }
            if (Math.abs(currentX - goingX) <= multiplier) {
                currentX = goingX;
            }
            if (Math.abs(currentY - goingY) <= multiplier) {
                currentY = goingY;
            }
            if (currentY == goingY && currentX == goingX) {
                goingX = RANDOM_LOCATION.nextInt((FRAME_WIDTH - 210) + 1);
                goingY = RANDOM_LOCATION.nextInt((FRAME_HEIGHT - 150) + 1);
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
