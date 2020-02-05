package bees;

import static bees.Bees.setTimeout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Image;

public class Bee {

    private JLabel label;
    private boolean dead = false;

    public Bee(JFrame mainFrame, Image swatImage, Bees bees) {
        ImageIcon icon;

        try {
            icon = new ImageIcon(ImageIO.read(Bees.class.getResource("bee.png")));
        } catch (IOException ex) {
            return;
        }

        label = new JLabel(icon);

        label.addMouseListener(new MouseAdapter() { // When Image is clicked
            @Override
            public void mousePressed(MouseEvent e) {
                dead = true;
                label.removeMouseListener(this); // Removes mouse listener so game wont finish before ALL bees are
                // clicked
                icon.setImage(swatImage);
                label.repaint(); // Change Image to effect
                bees.clickSound(); //Plays noise

                setTimeout(() -> {
                    mainFrame.remove(label); // remove bee
                    mainFrame.revalidate();
                    mainFrame.repaint();
                    bees.beeClicked(); // Add to counter
                }, 100); // Set effect image invisible after 100Ms
            }
        });

        mainFrame.add(label);
        label.setSize(icon.getIconWidth(), icon.getIconHeight());
    }

    public void setLocation(int x, int y) {
        if (dead == false) {
            label.setLocation(x, y);
        }
    }

}
