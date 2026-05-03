package view;

import sklad.InventarController;
import controller.RozvozController;
import controller.ZakazkaController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import model.Rozvoz;
import javax.swing.table.DefaultTableModel;
import model.DataStore;
/**
 * Hlavné okno aplikácie.
 */
public class MainView extends JFrame {

    public static final String CARD_UVOD    = "uvod";
    public static final String CARD_UC01    = "uc01";
    public static final String CARD_UC02    = "uc02";
    public static final String CARD_UC03    = "uc03";
    public static final String CARD_UC04    = "uc04";
    public static final String CARD_ZAKAZKY = "zakazky";
    public static final String CARD_MANAZER = "manazer";

    private final CardLayout cardLayout  = new CardLayout();
    private final JPanel     contentPane = new JPanel(cardLayout);

    private final InventarView inventarView;
    private final RozvozPanel rozvozPanel;
    private final VyrobaPanel vyrobaPanel;
    private final RozvozController rozvozCtrl;
    private boolean manazerPrihlaseny = false;

    public MainView() {
        setTitle("Výrobný systém WoodFlow");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(850, 540));
        setLocationRelativeTo(null);

        InventarController inventarCtrl = new InventarController();
        inventarView = new InventarView(inventarCtrl);  // ⬅ nový view
        vyrobaPanel = new VyrobaPanel();

        this.rozvozCtrl = new RozvozController();
        rozvozPanel = new RozvozPanel(this.rozvozCtrl);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(new Color(28, 50, 90));
        side.setPreferredSize(new Dimension(200, 0));
        side.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel logo = new JLabel("⚙ WoodFlow", SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 18));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(10, 0, 20, 0));
        side.add(logo);

        side.add(buildNavBtn("🏠  Úvod",             CARD_UVOD));
        side.add(Box.createVerticalStrut(4));
        side.add(sectionLabel("  USE CASES"));
        side.add(buildNavBtn("1  Zákazky",    CARD_UC01));
        side.add(buildNavBtn("2  Výroba",     CARD_UC02));
        side.add(buildNavBtn("3  Inventár",   CARD_UC03));
        side.add(buildNavBtn("4  Rozvoz",     CARD_UC04));
        side.add(Box.createVerticalStrut(4));
        side.add(sectionLabel("  OSTATNÉ"));
        side.add(buildNavBtn("📋  Zoznam zákaziek",  CARD_ZAKAZKY));
        side.add(buildNavBtn("🔐  Manažér",          CARD_MANAZER));
        side.add(Box.createVerticalGlue());

        JButton quitBtn = new JButton("⏻  Quit");
        quitBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        quitBtn.setForeground(new Color(255, 210, 210));
        quitBtn.setBackground(new Color(28, 50, 90));
        quitBtn.setBorderPainted(false);
        quitBtn.setFocusPainted(false);
        quitBtn.setHorizontalAlignment(SwingConstants.LEFT);
        quitBtn.setBorder(new EmptyBorder(10, 18, 10, 10));
        quitBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        quitBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        quitBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                quitBtn.setBackground(new Color(120, 40, 40));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                quitBtn.setBackground(new Color(28, 50, 90));
            }
        });

        quitBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Naozaj chceš ukončiť aplikáciu?",
                    "Ukončiť",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        side.add(quitBtn);

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
            if (CARD_MANAZER.equals(card)) {
                if (!manazerPrihlaseny) {
                    JPasswordField passwordField = new JPasswordField();

                    int option = JOptionPane.showConfirmDialog(
                            this,
                            passwordField,
                            "Zadaj heslo manažéra",
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.PLAIN_MESSAGE
                    );

                    if (option != JOptionPane.OK_OPTION) {
                        return;
                    }

                    String heslo = new String(passwordField.getPassword());

                    if (!DataStore.MANAZER_HESLO.equals(heslo)) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Nesprávne heslo.",
                                "Prístup zamietnutý",
                                JOptionPane.ERROR_MESSAGE
                        );
                        return;
                    }

                    manazerPrihlaseny = true;
                }
            } else {
                manazerPrihlaseny = false;
            }

            cardLayout.show(contentPane, card);

            if (CARD_UC02.equals(card)) vyrobaPanel.refreshMaterials();
            if (CARD_UC03.equals(card)) inventarView.refreshAll();
            if (CARD_UC04.equals(card)) rozvozPanel.refreshAll();

            if (CARD_ZAKAZKY.equals(card) && ZakazkyPanel.instance != null) {
                ZakazkyPanel.instance.refresh();
            }
        });

        return btn;
    }

    private JPanel buildContent() {
        JPanel uvod = new JPanel(new GridBagLayout());
        uvod.setBackground(Color.WHITE);
        JLabel uvLabel = new JLabel("Vitajte v systéme WoodFlow", SwingConstants.CENTER);
        uvLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        uvod.add(uvLabel);
        ZakazkaController zakazkaController = new ZakazkaController();
        PrijatZakazkuView prijatZakazkuView = new PrijatZakazkuView(zakazkaController);

        ZakazkyPanel zakazkyPanel = new ZakazkyPanel(z -> {
            vyrobaPanel.setZakazka(z);
            cardLayout.show(contentPane, CARD_UC02);
        });

        contentPane.add(uvod, CARD_UVOD);
        contentPane.add(prijatZakazkuView, CARD_UC01);
        contentPane.add(vyrobaPanel, CARD_UC02);
        contentPane.add(inventarView, CARD_UC03);
        contentPane.add(rozvozPanel, CARD_UC04);
        contentPane.add(zakazkyPanel, CARD_ZAKAZKY);
        contentPane.add(buildManazerPanel(rozvozCtrl), CARD_MANAZER);

        cardLayout.show(contentPane, CARD_UVOD);
        return contentPane;
    }

    private JPanel buildManazerPanel(RozvozController rozvozCtrl) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("Manažér – schvaľovanie");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(40, 70, 120));
        panel.add(title, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new String[]{"ID", "Vozidlo", "Dátum", "Počet zákaziek", "Stav"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Runnable refresh = () -> {
            model.setRowCount(0);

            for (Rozvoz r : rozvozCtrl.getCakajuceRozvozy()) {
                model.addRow(new Object[]{
                        r.getId(),
                        r.getVozidlo(),
                        r.getDatum(),
                        r.getZakazky().size(),
                        r.getStav()
                });
            }
        };

        refresh.run();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);

        JButton refreshBtn = new JButton("Obnoviť");
        JButton schvalitBtn = new JButton("Schváliť");
        JButton zamietnutBtn = new JButton("Zamietnuť");

        refreshBtn.addActionListener(e -> refresh.run());

        schvalitBtn.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Vyber rozvoz zo zoznamu.");
                return;
            }

            int id = (Integer) model.getValueAt(row, 0);

            int confirm = JOptionPane.showConfirmDialog(
                    panel,
                    "Naozaj schváliť rozvoz #" + id + "?",
                    "Potvrdenie",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            RozvozController.VysledokRozvozu vysledok = rozvozCtrl.schvalitRozvoz(id);

            JOptionPane.showMessageDialog(panel, vysledok.sprava);
            refresh.run();
            rozvozPanel.refreshAll();
            if (ZakazkyPanel.instance != null) {
                ZakazkyPanel.instance.refresh();
            }
        });

        zamietnutBtn.addActionListener(e -> {
            int row = table.getSelectedRow();

            if (row < 0) {
                JOptionPane.showMessageDialog(panel, "Vyber rozvoz zo zoznamu.");
                return;
            }

            int id = (Integer) model.getValueAt(row, 0);

            int confirm = JOptionPane.showConfirmDialog(
                    panel,
                    "Naozaj zamietnuť rozvoz #" + id + "?",
                    "Potvrdenie",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) return;

            RozvozController.VysledokRozvozu vysledok = rozvozCtrl.zamietnytRozvoz(id);

            JOptionPane.showMessageDialog(panel, vysledok.sprava);
            refresh.run();
            rozvozPanel.refreshAll();

            if (ZakazkyPanel.instance != null) {
                ZakazkyPanel.instance.refresh();
            }
        });

        btnPanel.add(refreshBtn);
        btnPanel.add(schvalitBtn);
        btnPanel.add(zamietnutBtn);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

}
