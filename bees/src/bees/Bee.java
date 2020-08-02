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

    private JLabel label;
    private boolean dead = false;
    private int currentX;
    private int currentY;
    private int goingX = 0;
    private int goingY = 0;
    private final Image swatImage; //Image when bees are clicked
    private final Image beeImage;
    private final Image beeFlippedImage;
    private int currentIcon = 1;
    private final int frameWidth;
    private final int frameHeight;
    private final Random numGenerator;
    private final JFrame mainFrame;
    private final String difficulty;
    private Scores score = new Scores();
    ImageIcon icon;
  
    public Bee(JFrame mainFrame, Game bees, Random numGenerator, int frameWidth, int frameHeight, Image swatImage, Image beeImage, Image beeFlippedImage, String difficulty) {
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
                label.removeMouseListener(this); // Removes mouse listener so game wont finish before ALL bees are
                setIcon(0);// Change Image to effect
                bees.clickSound(); //Plays noise
                
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


    private void setIcon(int iconNumber) {
        if (iconNumber != currentIcon) {
            currentIcon = iconNumber;
            switch (iconNumber) {
                case 0:
                    icon.setImage(swatImage);
                    break;
                case 1:
                    icon.setImage(beeImage);
                    break;
                case 2:
                    icon.setImage(beeFlippedImage);
                    break;
            }
            label.repaint();
        }
    }

    public void kill() {
        dead = true;
        mainFrame.remove(label);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    public void tick(Point mouseLocation) { //Function to smoothly move Bees
        if (dead == false) {
            int multiplier = 1;
            if (mouseLocation != null && (Math.abs(mouseLocation.x - (currentX + 75)) <= 100 && Math.abs(mouseLocation.y - (currentY + 75)) <= 100)) {
                switch (difficulty) {
                    case "Easy":
                        multiplier = 1;
                        break;
                    case "Normal":
                        multiplier = 2;
                        break;
                    case "Hard":
                        multiplier = 3; //Mutilplier controls how fast the bees fly away from you
                        break;
                }
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
