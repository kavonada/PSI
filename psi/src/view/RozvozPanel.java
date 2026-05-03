package view;

import rozvoz.RozvozController;
import rozvoz.RozvozController.VysledokRozvozu;
import obchod.Zakazka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import rozvoz.Vozidlo;
import rozvoz.Rozvoz;

/**
 * View pre UC04 – Plánovanie rozvozu.
 */
public class RozvozPanel extends JPanel {

    private final RozvozController controller;

    private JComboBox<Vozidlo> vozidloCombo;
    private JLabel kapacitaLabel;
    private JTextField datumField;

    private DefaultTableModel dostupneModel;
    private JTable             dostupneTable;

    private DefaultTableModel vybranteModel;
    private JTable             vybranteTable;
    private DefaultTableModel schvaleneModel;

    private JLabel statusLabel;

    private final JTabbedPane tabs = new JTabbedPane();

    public RozvozPanel(RozvozController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        add(buildNadpis(),    BorderLayout.NORTH);
        add(tabs,             BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        tabs.addTab("🚚  Nový rozvoz",   buildNovyRozvozTab());
        tabs.addTab("✔  Schválené rozvozy", buildSchvaleneTab());


        tabs.addChangeListener(e -> refreshAktualnaKarta());

        refreshAll();
    }

    private JLabel buildNadpis() {
        JLabel lbl = new JLabel("UC04 – Plánovanie rozvozu", SwingConstants.LEFT);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        lbl.setForeground(new Color(40, 70, 120));
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        return lbl;
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

    private JPanel buildNovyRozvozTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel horna = new JPanel(new BorderLayout(12, 0));
        horna.setBackground(Color.WHITE);

        JPanel formularPanel = buildFormularPanel();
        formularPanel.setPreferredSize(new Dimension(330, 0));

        JPanel vyberPanel = buildVyberZakaziekPanel();

        horna.add(formularPanel, BorderLayout.WEST);
        horna.add(vyberPanel, BorderLayout.CENTER);

        root.add(horna, BorderLayout.CENTER);
        root.add(buildOdoslatPanel(), BorderLayout.SOUTH);

        return root;
    }

    private JPanel buildFormularPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(buildTitledBorder("Údaje rozvozu"));

        GridBagConstraints lc = labelC();
        GridBagConstraints fc = fieldC();

        int row = 0;

        // Vozidlo
        lc.gridy = row; fc.gridy = row++;
        p.add(new JLabel("Vozidlo:"), lc);
        vozidloCombo = new JComboBox<>();
        for (Vozidlo v : controller.getVozidla()) {
            vozidloCombo.addItem(v);
        }
        p.add(vozidloCombo, fc);

        lc.gridy = row; fc.gridy = row++;
        p.add(new JLabel("Kapacita vozidla:"), lc);
        kapacitaLabel = new JLabel();
        p.add(kapacitaLabel, fc);

        vozidloCombo.addActionListener(e -> aktualizujKapacituLabel());
        aktualizujKapacituLabel();

        // Dátum
        lc.gridy = row; fc.gridy = row++;
        p.add(new JLabel("Dátum (d.m.rrrr):"), lc);
        datumField = new JTextField(10);
        p.add(datumField, fc);

        // Info kapacita
        lc.gridy = row;
        lc.gridwidth = 2;
        JLabel info = new JLabel("Vybrané zákazky nesmú presiahnuť kapacitu.");
        info.setFont(new Font("SansSerif", Font.ITALIC, 11));
        info.setForeground(new Color(120, 120, 120));
        p.add(info, lc);

        return p;
    }

    private JPanel buildSchvaleneTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));

        schvaleneModel = new DefaultTableModel(
                new String[]{"ID", "Vozidlo", "Dátum", "Počet zákaziek", "Stav"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        JTable tbl = new JTable(schvaleneModel);
        styleTable(tbl);

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    private void refreshSchvalene() {
        if (schvaleneModel == null) return;

        schvaleneModel.setRowCount(0);

        for (Rozvoz r : controller.getSchvaleneRozvozy()) {
            schvaleneModel.addRow(new Object[]{
                    r.getId(),
                    r.getVozidlo(),
                    r.getDatum(),
                    r.getZakazky().size(),
                    r.getStav()
            });
        }
    }

    private void aktualizujKapacituLabel() {
        Vozidlo vozidlo = (Vozidlo) vozidloCombo.getSelectedItem();
        if (vozidlo == null) {
            kapacitaLabel.setText("-");
        } else {
            kapacitaLabel.setText(String.valueOf(vozidlo.getKapacita()));
        }
    }

    private JPanel buildVyberZakaziekPanel() {
        JPanel p = new JPanel(new BorderLayout(6, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(buildTitledBorder("Výber zákaziek (poradie = zastávky)"));

        // ── Dostupné ──
        dostupneModel = new DefaultTableModel(new String[]{"ID", "Zákazka", "Zákazník", "Stav"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        dostupneTable = new JTable(dostupneModel);
        styleTable(dostupneTable);

        JPanel leftP = new JPanel(new BorderLayout(0, 4));
        leftP.setBackground(Color.WHITE);
        leftP.add(new JLabel("Dostupné zákazky:", SwingConstants.CENTER), BorderLayout.NORTH);
        leftP.add(new JScrollPane(dostupneTable), BorderLayout.CENTER);

        // ── Vybrané ──
        vybranteModel = new DefaultTableModel(new String[]{"#", "ID", "Zákazka"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        vybranteTable = new JTable(vybranteModel);
        styleTable(vybranteTable);
        vybranteTable.getColumnModel().getColumn(0).setMaxWidth(35);
        vybranteTable.getColumnModel().getColumn(1).setMaxWidth(50);

        JPanel rightP = new JPanel(new BorderLayout(0, 4));
        rightP.setBackground(Color.WHITE);
        rightP.add(new JLabel("Vybrané zastávky (poradie):", SwingConstants.CENTER), BorderLayout.NORTH);
        rightP.add(new JScrollPane(vybranteTable), BorderLayout.CENTER);

        // ── Tabuľky vedľa seba v pomere 2:1 ──
        JPanel tablesPanel = new JPanel(new GridBagLayout());
        tablesPanel.setBackground(Color.WHITE);

        GridBagConstraints tc = new GridBagConstraints();
        tc.gridy = 0;
        tc.fill = GridBagConstraints.BOTH;
        tc.weighty = 1.0;

        // Dostupné zákazky = 2 diely
        tc.gridx = 0;
        tc.weightx = 7.0;
        tc.insets = new Insets(0, 0, 0, 10);
        tablesPanel.add(leftP, tc);

        // Vybrané zákazky = 1 diel
        tc.gridx = 1;
        tc.weightx = 1.0;
        tc.insets = new Insets(0, 0, 0, 0);
        tablesPanel.add(rightP, tc);

        // ── Tlačidlá pod tabuľkami ──
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        btnPanel.setBackground(Color.WHITE);

        JButton pridatBtn   = createSmallBtn("→ Pridať",    this::pridatZakazku);
        JButton odobratBtn  = createSmallBtn("← Odstrániť", this::odobratZakazku);
        JButton horeBtn     = createSmallBtn("▲ Hore",      this::posunHore);
        JButton doleBtn     = createSmallBtn("▼ Dole",      this::posunDole);

        btnPanel.add(pridatBtn);
        btnPanel.add(odobratBtn);
        btnPanel.add(horeBtn);
        btnPanel.add(doleBtn);

        p.add(tablesPanel, BorderLayout.CENTER);
        p.add(btnPanel, BorderLayout.SOUTH);

        return p;
    }

    /** Tlačidlo Odoslať na schválenie */
    private JPanel buildOdoslatPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(Color.WHITE);
        p.add(createBtn("Odoslať na schválenie", this::odoslatRozvoz));
        return p;
    }

    /** Presunie označenú zákazku z dostupných → vybrané. */
    private void pridatZakazku() {
        int row = dostupneTable.getSelectedRow();
        if (row < 0) { setStatus("Vyber zákazku zo zoznamu dostupných.", Color.ORANGE.darker()); return; }

        Vozidlo vozidlo = (Vozidlo) vozidloCombo.getSelectedItem();
        if (vozidlo == null) {
            setStatus("Najskôr vyber vozidlo.", Color.RED.darker());
            return;
        }

        int kapacita = vozidlo.getKapacita();
        if (vybranteModel.getRowCount() >= kapacita) {
            setStatus("Kapacita vozidla je plná (" + kapacita + ").", Color.RED.darker());
            return;
        }

        Object id    = dostupneModel.getValueAt(row, 0);
        Object nazov = dostupneModel.getValueAt(row, 1);
        int poradie  = vybranteModel.getRowCount() + 1;
        vybranteModel.addRow(new Object[]{poradie, id, nazov});
        dostupneModel.removeRow(row);
        setStatus("Zákazka #" + id + " pridaná na zastávku č." + poradie + ".", new Color(0, 120, 0));
    }

    /** Presunie označenú zákazku z vybraných späť do dostupných. */
    private void odobratZakazku() {
        int row = vybranteTable.getSelectedRow();
        if (row < 0) { setStatus("Vyber zákazku z vybraných zastávok.", Color.ORANGE.darker()); return; }

        Object id    = vybranteModel.getValueAt(row, 1);
        Object nazov = vybranteModel.getValueAt(row, 2);
        vybranteModel.removeRow(row);

        // Pridáme späť do dostupných
        Zakazka z = controller.getDostupneZakazky().stream()
                .filter(zz -> zz.getId() == (int) id)
                .findFirst().orElse(null);
        String zakaznik = z != null && z.getZakaznik() != null ? z.getZakaznik().getMeno() : "";
        String stav     = z != null ? z.getStav().name() : "";
        dostupneModel.addRow(new Object[]{id, nazov, zakaznik, stav});

        // Obnovíme číslovanie
        renumberVybrane();
        setStatus("Zákazka #" + id + " odobraná.", Color.ORANGE.darker());
    }

    /** Posunie vybranú zastávku o jedno hore. */
    private void posunHore() {
        int row = vybranteTable.getSelectedRow();
        if (row <= 0) return;
        vybranteModel.moveRow(row, row, row - 1);
        vybranteTable.setRowSelectionInterval(row - 1, row - 1);
        renumberVybrane();
    }

    /** Posunie vybranú zastávku o jedno dole. */
    private void posunDole() {
        int row = vybranteTable.getSelectedRow();
        if (row < 0 || row >= vybranteModel.getRowCount() - 1) return;
        vybranteModel.moveRow(row, row, row + 1);
        vybranteTable.setRowSelectionInterval(row + 1, row + 1);
        renumberVybrane();
    }

    /** Odošle rozvoz na schválenie (zavolá controller). */
    private void odoslatRozvoz() {
        Vozidlo vozidlo = (Vozidlo) vozidloCombo.getSelectedItem();
        String datum = datumField.getText().trim();

        // Zozbierame ID zákaziek v poradí
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < vybranteModel.getRowCount(); i++) {
            ids.add((Integer) vybranteModel.getValueAt(i, 1));
        }

        VysledokRozvozu v = controller.vytvorRozvoz(vozidlo, datum, ids);

        if (v.typ == VysledokRozvozu.Typ.CHYBA) {
            JOptionPane.showMessageDialog(this, v.sprava, "Chyba", JOptionPane.ERROR_MESSAGE);
            setStatus("✗ " + v.sprava, Color.RED.darker());
            return;
        }

        JOptionPane.showMessageDialog(this, v.sprava, "Hotovo", JOptionPane.INFORMATION_MESSAGE);
        setStatus("✔ " + v.sprava, new Color(0, 120, 0));

        // Resetujeme formulár
        vozidloCombo.setSelectedIndex(0);
        aktualizujKapacituLabel();
        datumField.setText("");
        vybranteModel.setRowCount(0);
        refreshDostupne();

        // Prepneme na kartu Čakajúce
        refreshDostupne();
    }

    public void refreshAll() {
        refreshDostupne();
        refreshSchvalene();
    }

    private void refreshAktualnaKarta() {
        switch (tabs.getSelectedIndex()) {
            case 0 -> refreshDostupne();
            case 1 -> refreshSchvalene();
        }
    }

    private void refreshDostupne() {
        // Zistíme, ktoré zákazky sú už vo vybraných, aby sme ich nepridávali znova
        List<Integer> uzVybrane = new ArrayList<>();
        for (int i = 0; i < vybranteModel.getRowCount(); i++) {
            uzVybrane.add((Integer) vybranteModel.getValueAt(i, 1));
        }

        dostupneModel.setRowCount(0);
        for (Zakazka z : controller.getDostupneZakazky()) {
            if (!uzVybrane.contains(z.getId())) {
                String zakaznik = z.getZakaznik() != null ? z.getZakaznik().getMeno() : "";
                dostupneModel.addRow(new Object[]{
                        z.getId(), z.getNazov(), zakaznik, z.getStav().name()
                });
            }
        }
    }

    /** Obnoví číslo zastávky (#) v tabuľke vybraných zákaziek. */
    private void renumberVybrane() {
        for (int i = 0; i < vybranteModel.getRowCount(); i++) {
            vybranteModel.setValueAt(i + 1, i, 0);
        }
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    private void styleTable(JTable tbl) {
        tbl.setRowHeight(24);
        tbl.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.setFillsViewportHeight(true);
    }

    private JButton createBtn(String text, Runnable action) {
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

    private JButton createSmallBtn(String text, Runnable action) {
        JButton btn = createBtn(text, action);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setBackground(new Color(70, 120, 200));
        btn.setPreferredSize(new Dimension(110, 28));
        return btn;
    }

    private GridBagConstraints labelC() {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(6, 6, 6, 6);
        c.gridx  = 0;
        return c;
    }

    private GridBagConstraints fieldC() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets  = new Insets(6, 6, 6, 6);
        c.gridx   = 1;
        return c;
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

}