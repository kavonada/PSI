package view;

import controller.InventarController;
import controller.RozvozController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Hlavné okno aplikácie.
 * Ľavý sidebar s navigáciou, pravá časť zobrazuje aktívny panel (CardLayout).
 */
public class MainView extends JFrame {

    // Card mená – každý panel má unikátny kľúč
    public static final String CARD_UVOD    = "uvod";
    public static final String CARD_UC01    = "uc01";
    public static final String CARD_UC02    = "uc02";
    public static final String CARD_UC03    = "uc03";
    public static final String CARD_UC04    = "uc04";
    public static final String CARD_ZAKAZKY = "zakazky";
    public static final String CARD_MANAZER = "manazer";

    private final CardLayout cardLayout  = new CardLayout();
    private final JPanel     contentPane = new JPanel(cardLayout);

    private final ObjednatMaterialView objednatPanel;
    private final RozvozPanel rozvozPanel;
    private final VyrobaPanel vyrobaPanel;

    public MainView() {
        setTitle("Výrobný systém PSISKO");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(850, 540));
        setLocationRelativeTo(null);

        // --- Controller pre UC03 ---
        InventarController inventarCtrl = new InventarController();
        objednatPanel = new ObjednatMaterialView(inventarCtrl);
        vyrobaPanel = new VyrobaPanel();

        RozvozController rozvozCtrl   = new RozvozController();
        rozvozPanel   = new RozvozPanel(rozvozCtrl);

        // Zostavenie UI
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(),  BorderLayout.CENTER);
        setContentPane(root);
    }

    // Sidebar
    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(new Color(28, 50, 90));
        side.setPreferredSize(new Dimension(200, 0));
        side.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Logo / názov
        JLabel logo = new JLabel("⚙ WoodFlow", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(10, 0, 20, 0));
        side.add(logo);

        side.add(buildNavBtn("🏠  Úvod",                CARD_UVOD));
        side.add(Box.createVerticalStrut(4));

        JLabel sep = sectionLabel("  USE CASES");
        side.add(sep);

        side.add(buildNavBtn("1  Zákazky (UC01)",    CARD_UC01));
        side.add(buildNavBtn("2  Výroba (UC02)",      CARD_UC02));
        side.add(buildNavBtn("3  Inventár (UC03)",    CARD_UC03));
        side.add(buildNavBtn("4  Rozvoz (UC04)",      CARD_UC04));

        side.add(Box.createVerticalStrut(4));
        side.add(sectionLabel("  OSTATNÉ"));

        side.add(buildNavBtn("📋  Zoznam zákaziek",  CARD_ZAKAZKY));
        side.add(buildNavBtn("🔐  Manažér",          CARD_MANAZER));

        side.add(Box.createVerticalGlue());
        return side;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(new Color(120, 150, 200));
        lbl.setBorder(new EmptyBorder(8, 14, 4, 0));
        return lbl;
    }

    private JButton buildNavBtn(String text, String card) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(new Color(210, 225, 255));
        btn.setBackground(new Color(28, 50, 90));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 18, 10, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(50, 80, 130)); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(new Color(28, 50, 90));  }
        });

        btn.addActionListener(e -> {
            cardLayout.show(contentPane, card);
            if (CARD_UC02.equals(card)) vyrobaPanel.refreshMaterials();
            if (CARD_UC03.equals(card)) objednatPanel.refreshSklad();
            if (CARD_UC04.equals(card)) rozvozPanel.refreshAll();
        });

        return btn;
    }

    private JPanel buildContent() {
        JPanel uvod = new JPanel(new GridBagLayout());
        uvod.setBackground(Color.WHITE);
        JLabel uvLabel = new JLabel("Vitajte v systéme WoodFlow", SwingConstants.CENTER);
        uvLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        uvod.add(uvLabel);

        ZakazkyPanel zakazkyPanel = new ZakazkyPanel(z -> {
            this.vyrobaPanel.setZakazka(z);
            cardLayout.show(contentPane, CARD_UC02);
        });

        contentPane.add(uvod,                                                                          CARD_UVOD);
        contentPane.add(new PlaceholderPanel("UC01 – Prijímanie zákaziek", "Vytvorenie a správa zákaziek"), CARD_UC01);
        contentPane.add(vyrobaPanel,                                                                   CARD_UC02);
        contentPane.add(objednatPanel,                                                                 CARD_UC03);
        contentPane.add(rozvozPanel,                                                                   CARD_UC04);
        contentPane.add(zakazkyPanel,                                                               CARD_ZAKAZKY);
        contentPane.add(new PlaceholderPanel("Manažér",                    "Schvaľovanie objednávok"),       CARD_MANAZER);

        cardLayout.show(contentPane, CARD_UVOD);
        return contentPane;
    }
}
