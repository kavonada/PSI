package view;

import sklad.InventarController;
import sklad.InventarController.VysledokObjednavky;
import sklad.KosikPolozka;
import sklad.Material;
import sklad.Dodavatel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.EventObject;

public class ObjednatMaterialView extends JPanel {

    private final InventarController controller;

    private DefaultTableModel skladTableModel;
    private JTable skladTable;

    private JComboBox<String> materialCombo;
    private JSpinner mnozstvoSpinner;
    private JComboBox<String> dodavatelCombo;
    private JLabel cenaLabel;

    private DefaultTableModel cartTableModel;
    private JLabel totalCartPriceLabel;

    private JLabel statusLabel;

    public ObjednatMaterialView(InventarController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        add(buildNadpis(),    BorderLayout.NORTH);
        add(buildStred(),     BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        refreshSklad();
        refreshCart();
    }

    private JLabel buildNadpis() {
        JLabel lbl = new JLabel("Objednanie materiálu", SwingConstants.LEFT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setForeground(new Color(40, 70, 120));
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
    }

    private JSplitPane buildStred() {
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildHornaPolovica(), buildCartPanel());
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

        String[] cols = {"Materiál", "Množstvo (ks)", "Min. množstvo", "Stav"};
        skladTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        skladTable = new JTable(skladTableModel);
        skladTable.setRowHeight(24);
        skladTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        skladTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        lc.gridx = 0; lc.gridy = row;
        fc.gridx  = 1; fc.gridy  = row++;
        p.add(new JLabel("Materiál:"), lc);
        materialCombo = new JComboBox<>();
        materialCombo.addActionListener(e -> aktualizujCenu());
        p.add(materialCombo, fc);

        lc.gridy = row; fc.gridy = row++;
        p.add(new JLabel("Množstvo (ks):"), lc);
        mnozstvoSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        ((JSpinner.DefaultEditor) mnozstvoSpinner.getEditor()).getTextField().setColumns(6);
        mnozstvoSpinner.addChangeListener(e -> aktualizujCenu());
        p.add(mnozstvoSpinner, fc);

        lc.gridy = row; fc.gridy = row++;
        p.add(new JLabel("Celková cena:"), lc);
        cenaLabel = new JLabel("0.00 EUR");
        cenaLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        cenaLabel.setForeground(new Color(0, 100, 0));
        p.add(cenaLabel, fc);

        lc.gridy = row; lc.gridwidth = 2; fc.gridy = row++;
        JLabel limitInfo = new JLabel("⚠ Objednávky nad " + (int) controller.getLimitObjednavky()
                + " EUR vyžadujú schválenie manažéra.");
        limitInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));
        limitInfo.setForeground(new Color(150, 80, 0));
        p.add(limitInfo, lc);

        lc.gridy = row; lc.gridwidth = 2; fc.gridy = row++;
        p.add(createButton("Pridať do košíka", this::spracujPridanieProduktu), lc);

        lc.gridy = row; lc.gridwidth = 1; fc.gridy = row++;
        p.add(new JLabel("Dodávateľ:"), lc);
        dodavatelCombo = new JComboBox<>(
                controller.getDodavatelia().stream()
                        .map(Dodavatel::getNazov).toArray(String[]::new));
        p.add(dodavatelCombo, fc);

        lc.gridy = row; lc.gridwidth = 2;
        p.add(createButton("✔ Objednať materiál", this::spracujObjednavku), lc);

        return p;
    }

    // Košík s tlačidlom Zrušiť

    private JPanel buildCartPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(buildTitledBorder("Aktuálna objednávka"));

        String[] cols = {"Materiál", "Množstvo", "Cena (EUR)", "Akcia"};
        cartTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 3 ? JButton.class : Object.class;
            }
        };

        JTable tbl = new JTable(cartTableModel);
        tbl.setRowHeight(30);
        tbl.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Stĺpec Akcia
        tbl.getColumnModel().getColumn(3).setPreferredWidth(80);
        tbl.getColumnModel().getColumn(3).setMaxWidth(100);

        // Renderer + Editor
        tbl.getColumnModel().getColumn(3).setCellRenderer(new ZrusitRenderer());
        tbl.getColumnModel().getColumn(3).setCellEditor(new ZrusitEditor());

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        totalCartPriceLabel = new JLabel("Celkom: 0.00 EUR");
        totalCartPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bottom.add(totalCartPriceLabel);
        p.add(bottom, BorderLayout.SOUTH);

        return p;
    }

    // Renderer: zobrazí tlačidlo Zrušiť v každom riadku

    private static class ZrusitRenderer implements TableCellRenderer {
        private final JButton btn = makeBtn();

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            return btn;
        }
    }

    // Editor: klik na tlačidlo → odoberie riadok z košíka

    private class ZrusitEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton btn = makeBtn();
        private int editingRow;

        ZrusitEditor() {
            btn.addActionListener(e -> {
                controller.getKosik().remove(editingRow);
                fireEditingStopped();
                refreshCart();
                setStatus("✗ Položka odstránená z košíka", Color.DARK_GRAY);
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {
            this.editingRow = row;
            return btn;
        }

        @Override public Object getCellEditorValue() { return null; }

        @Override
        public boolean isCellEditable(EventObject e) { return true; }
    }

    /** Vytvorí štýlované tlačidlo Zrušiť pre tabuľku košíka. */
    private static JButton makeBtn() {
        JButton b = new JButton("✕ Zrušiť");
        b.setFont(new Font("SansSerif", Font.BOLD, 11));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(190, 40, 40));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Ostatné metódy (nezmenené)

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
        skladTableModel.setRowCount(0);
        for (Material m : controller.getMaterials()) {
            skladTableModel.addRow(new Object[]{
                    m.getNazov(), m.getMnozstvo(), m.getLimit(), m.getStav()});
        }
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
        double total = 0;
        for (KosikPolozka o : controller.getKosik()) {
            cartTableModel.addRow(new Object[]{
                    o.getMaterial().getNazov(),
                    o.getMnozstvo(),
                    String.format("%.2f", o.getCena()),
                    "zrusit"   // placeholder – stĺpec renderuje button
            });
            total += o.getCena();
        }
        totalCartPriceLabel.setText(String.format("Celkom: %.2f EUR", total));
    }

    private void aktualizujCenu() {
        int idx = materialCombo.getSelectedIndex();
        double cena = idx >= 0
                ? controller.vypocitajCenu((Integer) mnozstvoSpinner.getValue(),
                controller.getMaterials().get(idx))
                : 0;
        cenaLabel.setText(String.format("%.2f EUR", cena));
        cenaLabel.setForeground(new Color(0, 100, 0));
    }

    private void prefillZTabulky() {
        int row = skladTable.getSelectedRow();
        if (row >= 0 && row < materialCombo.getItemCount())
            materialCombo.setSelectedIndex(row);
    }

    private void spracujPridanieProduktu() {
        VysledokObjednavky v = controller.pridatDoKosika(
                materialCombo.getSelectedIndex(),
                (Integer) mnozstvoSpinner.getValue());
        switch (v.typ) {
            case USPECH -> setStatus("✔ Položka pridaná do košíka", new Color(0, 120, 0));
            case CHYBA  -> {
                JOptionPane.showMessageDialog(this, v.sprava, "Chyba", JOptionPane.ERROR_MESSAGE);
                setStatus("✗ " + v.sprava, Color.RED.darker());
                return;
            }
        }
        refreshCart();
    }

    private void spracujObjednavku() {
        VysledokObjednavky v = controller.objednatKosik(dodavatelCombo.getSelectedIndex());
        switch (v.typ) {
            case USPECH -> {
                JOptionPane.showMessageDialog(this, v.sprava, "Hotovo", JOptionPane.INFORMATION_MESSAGE);
                setStatus("✔ Objednávka vytvorená", new Color(0, 120, 0));
            }
            case CHYBA -> {
                JOptionPane.showMessageDialog(this, v.sprava, "Chyba", JOptionPane.ERROR_MESSAGE);
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