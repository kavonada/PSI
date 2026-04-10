package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Placeholder panel pre moduly, ktoré ešte nie sú implementované.
 * Každý člen tímu nahradí tento panel svojím skutočným View.
 */
public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String nazovUC, String popis) {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(new Color(248, 250, 255));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 200, 230), 2),
            new EmptyBorder(30, 40, 30, 40)));

        JLabel title = new JLabel(nazovUC);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(40, 70, 120));
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel sub = new JLabel(popis);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 14));
        sub.setForeground(Color.GRAY);
        sub.setAlignmentX(CENTER_ALIGNMENT);

        JLabel hint = new JLabel("[ Panel bude implementovaný príslušným členom tímu ]");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hint.setForeground(new Color(160, 160, 160));
        hint.setAlignmentX(CENTER_ALIGNMENT);

        box.add(title);
        box.add(Box.createVerticalStrut(10));
        box.add(sub);
        box.add(Box.createVerticalStrut(16));
        box.add(hint);

        add(box);
    }
}
