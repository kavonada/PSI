package view;

import sklad.InventarController;
import sklad.Material;
import sklad.Objednavka;
import sklad.StavObjednavky;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Hlavný panel pre UC03 – Inventár.
 * Obsahuje tri záložky: Sklad | Objednávanie | Zaevidovanie.
 */
public class InventarView extends JPanel {

    private static final String SUB_SKLAD        = "sklad";
    private static final String SUB_OBJEDNAVANIE = "objednavanie";
    private static final String SUB_ZAEVIDOVANIE = "zaevidovanie";

    private final InventarController    controller;
    private final CardLayout            subCard    = new CardLayout();
    private final JPanel                subContent = new JPanel(subCard);
    private final ObjednatMaterialView  objednatPanel;

    // Sklad
    private DefaultTableModel skladModel;

    // Zaevidovanie
    private DefaultTableModel objednavkyModel;
    private JTable objednavkyTable;
    private JButton dorucitBtn;
    private JButton vybalitBtn;

    private JButton btnSklad, btnObjednavanie, btnZaevidovanie;

    public InventarView(InventarController controller) {
        this.controller = controller;
        this.objednatPanel = new ObjednatMaterialView(controller);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(buildHeader(), BorderLayout.NORTH);

        subContent.add(buildSkladPanel(), SUB_SKLAD);
        subContent.add(objednatPanel, SUB_OBJEDNAVANIE);
        subContent.add(buildZaevidovaniePanel(), SUB_ZAEVIDOVANIE);

        add(subContent, BorderLayout.CENTER);

        showCard(SUB_SKLAD);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(15, 15, 0, 15));

        JLabel title = new JLabel("Inventár", SwingConstants.LEFT);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(new Color(40, 70, 120));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navBar.setBackground(Color.WHITE);
        navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(40, 100, 180)));

        btnSklad = buildSubNavBtn("📦  Sklad",        SUB_SKLAD);
        btnObjednavanie = buildSubNavBtn("🛒  Objednávanie", SUB_OBJEDNAVANIE);
        btnZaevidovanie = buildSubNavBtn("📋  Zaevidovanie", SUB_ZAEVIDOVANIE);

        navBar.add(btnSklad);
        navBar.add(btnObjednavanie);
        navBar.add(btnZaevidovanie);

        header.add(title,  BorderLayout.NORTH);
        header.add(navBar, BorderLayout.SOUTH);
        return header;
    }

    private JButton buildSubNavBtn(String text, String card) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.BLACK);
        btn.setBackground(new Color(230, 230, 230));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(new Color(40, 100, 180)))
                    btn.setBackground(new Color(220, 232, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!btn.getBackground().equals(new Color(40, 100, 180)))
                    btn.setBackground(Color.WHITE);
            }
        });

        btn.addActionListener(e -> showCard(card));
        return btn;
    }

    private void showCard(String card) {
        subCard.show(subContent, card);

        for (JButton b : new JButton[]{btnSklad, btnObjednavanie, btnZaevidovanie}) {
            b.setBackground(Color.WHITE);
            b.setForeground(new Color(40, 70, 120));
            b.setBorder(new EmptyBorder(8, 18, 8, 18));
        }

        JButton active = switch (card) {
            case SUB_SKLAD        -> btnSklad;
            case SUB_OBJEDNAVANIE -> btnObjednavanie;
            case SUB_ZAEVIDOVANIE -> btnZaevidovanie;
            default               -> null;
        };
        if (active != null) {
            active.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(20, 60, 140)),
                    new EmptyBorder(8, 18, 6, 18)));
        }

        if (SUB_SKLAD.equals(card))        refreshSklad();
        if (SUB_OBJEDNAVANIE.equals(card)) objednatPanel.refreshSklad();
        if (SUB_ZAEVIDOVANIE.equals(card)) refreshZaevidovanie();
    }

    // Sklad panel
    private JPanel buildSkladPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(14, 15, 15, 15));

        JLabel lbl = new JLabel("Aktuálny stav skladu");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(new Color(40, 70, 120));
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));

        String[] cols = {"Materiál", "Množstvo (ks)", "Min. množstvo", "Stav"};
        skladModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(skladModel);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        p.add(lbl, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    // Zaevidovanie panel
    private JPanel buildZaevidovaniePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(14, 15, 15, 15));

        JLabel lbl = new JLabel("Všetky objednávky");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lbl.setForeground(new Color(40, 70, 120));
        lbl.setBorder(new EmptyBorder(0, 0, 6, 0));

        String[] cols = {"ID", "Dodávateľ", "Celková suma (EUR)", "Stav"};
        objednavkyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        objednavkyTable = new JTable(objednavkyModel);
        objednavkyTable.setRowHeight(26);
        objednavkyTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        objednavkyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        objednavkyTable.getSelectionModel().addListSelectionListener(
                e -> { if (!e.getValueIsAdjusting()) aktualizujTlacidla(); }
        );

        // Tlacidla
        dorucitBtn = createActionBtn("📬  Označiť ako doručené", new Color(40, 100, 180));
        vybalitBtn = createActionBtn("📦  Vybalit na sklad",      new Color(0, 140, 60));
        dorucitBtn.setEnabled(false);
        vybalitBtn.setEnabled(false);

        dorucitBtn.addActionListener(e -> {
            int row = objednavkyTable.getSelectedRow();
            if (row < 0) return;
            InventarController.getObjednavky().get(row).dorucit();
            refreshZaevidovanie();
        });

        vybalitBtn.addActionListener(e -> {
            int row = objednavkyTable.getSelectedRow();
            if (row < 0) return;
            Objednavka o = InventarController.getObjednavky().get(row);
            if (controller.vybalitNaSklad(o)) {
                JOptionPane.showMessageDialog(this,
                        "✔ Materiály z objednávky č." + o.getId() + " boli pridané na sklad.",
                        "Vybalené", JOptionPane.INFORMATION_MESSAGE);
                refreshZaevidovanie();
            }
        });

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnBar.setBackground(Color.WHITE);
        btnBar.add(dorucitBtn);
        btnBar.add(vybalitBtn);

        // Info popis
        JLabel info = new JLabel("Kliknite na objednávku a zmeňte jej stav. Objednávky v stave 'Doručené' môžete vybalit na sklad.");
        info.setFont(new Font("SansSerif", Font.PLAIN, 11));
        info.setForeground(Color.GRAY);
        info.setBorder(new EmptyBorder(4, 4, 0, 0));

        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(Color.WHITE);
        south.add(btnBar, BorderLayout.NORTH);
        south.add(info,   BorderLayout.SOUTH);

        p.add(lbl, BorderLayout.NORTH);
        p.add(new JScrollPane(objednavkyTable), BorderLayout.CENTER);
        p.add(south, BorderLayout.SOUTH);
        return p;
    }

    private JButton createActionBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Refresh metody
    public void refreshSklad() {
        if (skladModel == null) return;
        skladModel.setRowCount(0);
        for (Material m : InventarController.getMaterials()) {
            skladModel.addRow(new Object[]{
                    m.getNazov(),
                    m.getMnozstvo(),
                    m.getLimit(),
                    m.getStav()
            });
        }
    }

    private void refreshZaevidovanie() {
        objednavkyModel.setRowCount(0);
        for (Objednavka o : InventarController.getObjednavky()) {
            objednavkyModel.addRow(new Object[]{
                    o.getId(),
                    o.getDodavatel(),
                    String.format("%.2f", o.getCelkovaSuma()),
                    o.getStav()
            });
        }
        aktualizujTlacidla();
    }

    private void aktualizujTlacidla() {
        int row = objednavkyTable.getSelectedRow();
        if (row < 0 || row >= InventarController.getObjednavky().size()) {
            dorucitBtn.setEnabled(false);
            vybalitBtn.setEnabled(false);
            return;
        }
        StavObjednavky stav = InventarController.getObjednavky().get(row).getStav();
        dorucitBtn.setEnabled(stav == StavObjednavky.VYTVORENA);
        vybalitBtn.setEnabled(stav == StavObjednavky.DORUCENA);
    }

    public void refreshAll() {
        refreshSklad();
        refreshZaevidovanie();
        objednatPanel.refreshSklad();
    }
}
