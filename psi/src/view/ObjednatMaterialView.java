package view;

import controller.InventarController;
import controller.InventarController.VysledokObjednavky;
import model.use_case_3.KosikPolozka;
import model.use_case_3.Material;
import model.use_case_3.Dodavatel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * View pre UC03 – Objednanie materiálu.
 * Zobrazuje sklad, formulár objednávky a históriu objednávok.
 */
public class ObjednatMaterialView extends JPanel {

    private final InventarController controller;

    // --- Sklad ---
    private DefaultTableModel skladTableModel;
    private JTable skladTable;

    // --- Formulár ---
    private JComboBox<String> materialCombo;
    private JSpinner mnozstvoSpinner;
    private JComboBox<String> dodavatelCombo;
    private JLabel cenaLabel;

    // --- Košík ---
    private DefaultTableModel cartTableModel;
    private JLabel totalCartPriceLabel;

    // --- Status ---
    private JLabel statusLabel;

    public ObjednatMaterialView(InventarController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        add(buildNadpis(),      BorderLayout.NORTH);
        add(buildStred(),       BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);

        refreshSklad();
        refreshCart();
    }

    // Stavebne prvky
    private JLabel buildNadpis() {
        JLabel lbl = new JLabel("UC03 – Objednanie materiálu", SwingConstants.LEFT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setForeground(new Color(40, 70, 120));
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    private JSplitPane buildStred() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildHornaPolovica(), buildCartPanel());
        split.setResizeWeight(0.4);
        split.setBorder(null);
        return split;
    }

    private JPanel buildHornaPolovica() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 0));
        panel.setBackground(Color.WHITE);
        panel.add(buildSkladPanel());
        panel.add(buildFormularPanel());
        return panel;
    }

    private JPanel buildSkladPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(buildTitledBorder("Aktuálny stav skladu"));

        String[] cols = {"Materiál", "Množstvo (ks)", "Stav"};
        skladTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        skladTable = new JTable(skladTableModel);
        skladTable.setRowHeight(24);
        skladTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        skladTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Klik na riadok → prefilluje formulár
        skladTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) prefillZTabulky();
        });

        p.add(new JScrollPane(skladTable), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildFormularPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(buildTitledBorder("Nová objednávka"));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(6, 6, 6, 6);

        GridBagConstraints fc = new GridBagConstraints();
        fc.fill    = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;
        fc.insets  = lc.insets;

        int row = 0;

        // Materiál
        lc.gridx = 0; lc.gridy = row;
        fc.gridx  = 1; fc.gridy  = row++;
        p.add(new JLabel("Materiál:"), lc);
        materialCombo = new JComboBox<>();
        materialCombo.addActionListener(e -> aktualizujCenu());
        p.add(materialCombo, fc);

        // Množstvo
        lc.gridy = row; fc.gridy = row++;
        p.add(new JLabel("Množstvo (ks):"), lc);
        mnozstvoSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        ((JSpinner.DefaultEditor) mnozstvoSpinner.getEditor()).getTextField().setColumns(6);
        mnozstvoSpinner.addChangeListener(e -> aktualizujCenu());
        p.add(mnozstvoSpinner, fc);

        // Celková cena
        lc.gridy = row; fc.gridy = row++;
        p.add(new JLabel("Celková cena:"), lc);
        cenaLabel = new JLabel("0.00 EUR");
        cenaLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        cenaLabel.setForeground(new Color(0, 100, 0));
        p.add(cenaLabel, fc);

        // Info limit
        lc.gridy = row; fc.gridy = row++;
        lc.gridwidth = 2;
        JLabel limitInfo = new JLabel("⚠ Objednávky nad " + (int) controller.getLimitObjednavky()
                + " EUR vyžadujú schválenie manažéra.");
        limitInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        limitInfo.setForeground(new Color(150, 80, 0));
        p.add(limitInfo, lc);

        // Tlacidlo pridat do kosika
        lc.gridy = row; fc.gridy = row++;
        lc.gridwidth = 2;
        JButton addToCartBtn = createButton("Pridať do košíka", this::spracujPridanieProduktu);
        p.add(addToCartBtn, lc);

        // Dodavatel
        lc.gridy = row; fc.gridy = row++;
        p.add(new JLabel("Dodávateľ:"), lc);
        dodavatelCombo = new JComboBox<>(
                controller.getDodavatelia()
                        .stream()
                        .map(Dodavatel::getNazov)
                        .toArray(String[]::new)
        );
        p.add(dodavatelCombo, fc);

        // Tlačidlo objednat
        lc.gridy = row;
        lc.gridwidth = 2;
        JButton objednatBtn = createButton("✔ Objednať materiál", this::spracujObjednavku);
        p.add(objednatBtn, lc);

        return p;
    }

    private JPanel buildCartPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(buildTitledBorder("Aktuálna objednávka"));

        String[] cols = {"Materiál", "Množstvo", "Cena (EUR)"};
        cartTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(cartTableModel);
        tbl.setRowHeight(22);
        tbl.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);

        totalCartPriceLabel = new JLabel("Celkom: 0.00 EUR");
        totalCartPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        bottom.add(totalCartPriceLabel);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(245, 245, 245));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        statusLabel = new JLabel("Pripravený.");
        statusLabel.setBorder(new EmptyBorder(4, 8, 4, 8));
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }


    public void refreshSklad() {
        // Tabuľka skladu
        skladTableModel.setRowCount(0);
        for (Material m : controller.getMaterials()) {
            skladTableModel.addRow(new Object[]{m.getNazov(), m.getMnozstvo(), m.getStav()});
        }

        // ComboBox materiálov
        String selected = (String) materialCombo.getSelectedItem();
        materialCombo.removeAllItems();
        for (Material m : controller.getMaterials()) {
            materialCombo.addItem(m.getNazov() + "  (" + m.getMnozstvo() + " ks)");
        }
        if (selected != null) materialCombo.setSelectedItem(selected);

        aktualizujCenu();
    }

    private void refreshCart() {
        cartTableModel.setRowCount(0);

        double totalPrice = 0;

        for (KosikPolozka o : controller.getKosik()) {
            cartTableModel.addRow(new Object[]{
                    o.getMaterial().getNazov(),
                    o.getMnozstvo(),
                    String.format("%.2f", o.getCena())
            });
            totalPrice += o.getCena();
        }
        totalCartPriceLabel.setText(String.format("Celkom: %.2f EUR", totalPrice));
    }

    private void aktualizujCenu() {
        int mnozstvo = (Integer) mnozstvoSpinner.getValue();
        int materialIndex  = materialCombo.getSelectedIndex();
        double cena = 0;

        if (materialIndex >= 0) {
            Material vybrany = controller.getMaterials().get(materialIndex);
            cena  = controller.vypocitajCenu(mnozstvo, vybrany);
        }

        cenaLabel.setText(String.format("%.2f EUR", cena));
        cenaLabel.setForeground(new Color(0, 100, 0));
    }

    /** Klik na riadok skladu → predvyplní index materiálu vo formulári. */
    private void prefillZTabulky() {
        int row = skladTable.getSelectedRow();
        if (row >= 0 && row < materialCombo.getItemCount()) {
            materialCombo.setSelectedIndex(row);
        }
    }

    private void spracujPridanieProduktu() {
        int materialIndex  = materialCombo.getSelectedIndex();
        int mnozstvo       = (Integer) mnozstvoSpinner.getValue();

        VysledokObjednavky vysledok = controller.pridatDoKosika(materialIndex, mnozstvo);

        switch (vysledok.typ) {
            case USPECH -> {
                setStatus("✔ Položka pridaná do košíka", new Color(0, 120, 0));
            }
            case CHYBA -> {
                JOptionPane.showMessageDialog(this, vysledok.sprava,
                        "Chyba", JOptionPane.ERROR_MESSAGE);
                setStatus("✗ " + vysledok.sprava, Color.RED.darker());
                return;
            }
        }

        refreshCart();
    }

    private void spracujObjednavku() {

        int dodavatelIndex = dodavatelCombo.getSelectedIndex();

        VysledokObjednavky vysledok = controller.objednatKosik(dodavatelIndex);

        switch (vysledok.typ) {
            case USPECH -> {
                JOptionPane.showMessageDialog(this, vysledok.sprava,
                        "Hotovo", JOptionPane.INFORMATION_MESSAGE);
                setStatus("✔ Objednávka vytvorená", new Color(0, 120, 0));
            }
            case CHYBA -> {
                JOptionPane.showMessageDialog(this, vysledok.sprava,
                        "Chyba", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        refreshCart();
        refreshSklad();
    }


    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    private javax.swing.border.Border buildTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 230)),
                title,
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 12),
                new Color(40, 70, 120)),
            new EmptyBorder(6, 8, 8, 8));
    }

    private JButton createButton(String text, Runnable action) {
        JButton btn = new JButton(text);

        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(new Color(40, 100, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> action.run());

        return btn;
    }
}
