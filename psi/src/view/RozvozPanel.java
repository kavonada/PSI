package view;

import controller.RozvozController;
import controller.RozvozController.VysledokRozvozu;
import model.Rozvoz;
import model.Zakazka;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * View pre UC04 – Plánovanie rozvozu.
 *
 * Rozloženie (tri karty pomocou JTabbedPane):
 *   1. Nový rozvoz   – formulár + výber zákaziek
 *   2. Čakajúce      – zoznam rozvozov čakajúcich na schválenie manažéra
 *   3. Schválené     – história schválených rozvozov
 */
public class RozvozPanel extends JPanel {

    private final RozvozController controller;

    // ── Tab 1 – Nový rozvoz ───────────────────────────────────────────────────
    private JTextField vozidloField;
    private JSpinner   kapacitaSpinner;
    private JTextField datumField;

    // Tabuľka dostupných zákaziek (ľavá strana)
    private DefaultTableModel dostupneModel;
    private JTable             dostupneTable;

    // Tabuľka vybraných zákaziek (pravá strana – poradie zastávok)
    private DefaultTableModel vybranteModel;
    private JTable             vybranteTable;

    // ── Tab 2 – Čakajúce rozvozy ──────────────────────────────────────────────
    private DefaultTableModel cakajuceModel;
    private JTable             cakajuceTable;

    // ── Tab 3 – Schválené rozvozy ─────────────────────────────────────────────
    private DefaultTableModel schvaleneModel;

    // ── Statusbar ─────────────────────────────────────────────────────────────
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
        tabs.addTab("⏳  Čakajúce",       buildCakajuceTab());
        tabs.addTab("✔  Schválené",       buildSchvaleneTab());

        // Pri prepnutí na kartu obnovíme dáta
        tabs.addChangeListener(e -> refreshAktualnaKarta());

        refreshAll();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  NADPIS & STATUSBAR
    // ═════════════════════════════════════════════════════════════════════════

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

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB 1 – NOVÝ ROZVOZ
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel buildNovyRozvozTab() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Horná časť: formulár + tabuľky vedľa seba
        JPanel horna = new JPanel(new GridLayout(1, 2, 12, 0));
        horna.setBackground(Color.WHITE);
        horna.add(buildFormularPanel());
        horna.add(buildVyberZakaziekPanel());

        root.add(horna, BorderLayout.CENTER);
        root.add(buildOdoslatPanel(), BorderLayout.SOUTH);

        return root;
    }

    /** Formulár: vozidlo, kapacita, dátum */
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
        vozidloField = new JTextField(15);
        p.add(vozidloField, fc);

        // Kapacita
        lc.gridy = row; fc.gridy = row++;
        p.add(new JLabel("Kapacita vozidla:"), lc);
        kapacitaSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        p.add(kapacitaSpinner, fc);

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

    /** Ľavá tabuľka (dostupné) + tlačidlá Pridať/Odstrániť + pravá tabuľka (vybrané) */
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

        // ── Tlačidlá uprostred ──
        JPanel btnPanel = new JPanel(new GridBagLayout());
        btnPanel.setBackground(Color.WHITE);
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0; bc.gridy = GridBagConstraints.RELATIVE;
        bc.fill = GridBagConstraints.HORIZONTAL;
        bc.insets = new Insets(4, 2, 4, 2);

        JButton pridatBtn   = createSmallBtn("→ Pridať",    this::pridatZakazku);
        JButton odobratBtn  = createSmallBtn("← Odstrániť", this::odobratZakazku);
        JButton horeBtn     = createSmallBtn("▲ Hore",      this::posunHore);
        JButton doleBtn     = createSmallBtn("▼ Dole",      this::posunDole);

        btnPanel.add(pridatBtn,  bc);
        btnPanel.add(odobratBtn, bc);
        bc.insets = new Insets(12, 2, 4, 2);
        btnPanel.add(horeBtn,    bc);
        bc.insets = new Insets(4, 2, 4, 2);
        btnPanel.add(doleBtn,    bc);

        // ── Vybrané ──
        vybranteModel = new DefaultTableModel(new String[]{"#", "ID", "Zákazka"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        vybranteTable = new JTable(vybranteModel);
        styleTable(vybranteTable);
        vybranteTable.getColumnModel().getColumn(0).setMaxWidth(30);

        JPanel rightP = new JPanel(new BorderLayout(0, 4));
        rightP.setBackground(Color.WHITE);
        rightP.add(new JLabel("Vybrané zastávky (poradie):", SwingConstants.CENTER), BorderLayout.NORTH);
        rightP.add(new JScrollPane(vybranteTable), BorderLayout.CENTER);

        // Poskladáme trojicu
        JPanel trio = new JPanel(new GridBagLayout());
        trio.setBackground(Color.WHITE);
        GridBagConstraints tc = new GridBagConstraints();
        tc.fill = GridBagConstraints.BOTH;
        tc.weighty = 1.0;

        tc.gridx = 0; tc.weightx = 1.0; trio.add(leftP,   tc);
        tc.gridx = 1; tc.weightx = 0.0; trio.add(btnPanel, tc);
        tc.gridx = 2; tc.weightx = 1.0; trio.add(rightP,  tc);

        p.add(trio, BorderLayout.CENTER);
        return p;
    }

    /** Tlačidlo Odoslať na schválenie */
    private JPanel buildOdoslatPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(Color.WHITE);
        p.add(createBtn("🚚  Odoslať na schválenie", this::odoslatRozvoz));
        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB 2 – ČAKAJÚCE ROZVOZY (schvaľovanie manažéra)
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel buildCakajuceTab() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));

        cakajuceModel = new DefaultTableModel(
                new String[]{"ID", "Vozidlo", "Dátum", "Počet zákaziek", "Stav"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cakajuceTable = new JTable(cakajuceModel);
        styleTable(cakajuceTable);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.add(createBtn("✔  Schváliť",   this::schvalit));
        btnRow.add(createBtn("✗  Zamietnuť", this::zamietnyt));

        JLabel hint = new JLabel("  Vyber riadok a klikni na akciu.");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(Color.GRAY);
        btnRow.add(hint);

        p.add(new JScrollPane(cakajuceTable), BorderLayout.CENTER);
        p.add(btnRow, BorderLayout.SOUTH);
        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TAB 3 – SCHVÁLENÉ ROZVOZY
    // ═════════════════════════════════════════════════════════════════════════

    private JPanel buildSchvaleneTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));

        schvaleneModel = new DefaultTableModel(
                new String[]{"ID", "Vozidlo", "Dátum", "Počet zákaziek", "Stav"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = new JTable(schvaleneModel);
        styleTable(tbl);

        p.add(new JScrollPane(tbl), BorderLayout.CENTER);
        return p;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  AKCIE (volané z tlačidiel)
    // ═════════════════════════════════════════════════════════════════════════

    /** Presunie označenú zákazku z dostupných → vybrané. */
    private void pridatZakazku() {
        int row = dostupneTable.getSelectedRow();
        if (row < 0) { setStatus("Vyber zákazku zo zoznamu dostupných.", Color.ORANGE.darker()); return; }

        int kapacita = (Integer) kapacitaSpinner.getValue();
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
        String vozidlo = vozidloField.getText().trim();
        int kapacita   = (Integer) kapacitaSpinner.getValue();
        String datum   = datumField.getText().trim();

        // Zozbierame ID zákaziek v poradí
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < vybranteModel.getRowCount(); i++) {
            ids.add((Integer) vybranteModel.getValueAt(i, 1));
        }

        VysledokRozvozu v = controller.vytvorRozvoz(vozidlo, kapacita, datum, ids);

        if (v.typ == VysledokRozvozu.Typ.CHYBA) {
            JOptionPane.showMessageDialog(this, v.sprava, "Chyba", JOptionPane.ERROR_MESSAGE);
            setStatus("✗ " + v.sprava, Color.RED.darker());
            return;
        }

        JOptionPane.showMessageDialog(this, v.sprava, "Hotovo", JOptionPane.INFORMATION_MESSAGE);
        setStatus("✔ " + v.sprava, new Color(0, 120, 0));

        // Resetujeme formulár
        vozidloField.setText("");
        datumField.setText("");
        vybranteModel.setRowCount(0);
        refreshDostupne();

        // Prepneme na kartu Čakajúce
        tabs.setSelectedIndex(1);
        refreshCakajuce();
    }

    /** Schváli vybraný rozvoz v tabuľke čakajúcich. */
    private void schvalit() {
        int row = cakajuceTable.getSelectedRow();
        if (row < 0) { setStatus("Vyber rozvoz zo zoznamu.", Color.ORANGE.darker()); return; }

        int id = (Integer) cakajuceModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Naozaj schváliť rozvoz #" + id + "?", "Potvrdenie",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        VysledokRozvozu v = controller.schvalitRozvoz(id);
        if (v.typ == VysledokRozvozu.Typ.CHYBA) {
            JOptionPane.showMessageDialog(this, v.sprava, "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setStatus("✔ " + v.sprava, new Color(0, 120, 0));
        refreshCakajuce();
        refreshSchvalene();
    }

    /** Zamietne vybraný rozvoz v tabuľke čakajúcich. */
    private void zamietnyt() {
        int row = cakajuceTable.getSelectedRow();
        if (row < 0) { setStatus("Vyber rozvoz zo zoznamu.", Color.ORANGE.darker()); return; }

        int id = (Integer) cakajuceModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Naozaj zamietnuť rozvoz #" + id + "? Zákazky budú vrátené.",
                "Potvrdenie", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        VysledokRozvozu v = controller.zamietnytRozvoz(id);
        if (v.typ == VysledokRozvozu.Typ.CHYBA) {
            JOptionPane.showMessageDialog(this, v.sprava, "Chyba", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setStatus("✔ " + v.sprava, new Color(180, 100, 0));
        refreshCakajuce();
        refreshDostupne();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  REFRESH
    // ═════════════════════════════════════════════════════════════════════════

    public void refreshAll() {
        refreshDostupne();
        refreshCakajuce();
        refreshSchvalene();
    }

    private void refreshAktualnaKarta() {
        switch (tabs.getSelectedIndex()) {
            case 0 -> refreshDostupne();
            case 1 -> refreshCakajuce();
            case 2 -> refreshSchvalene();
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

    private void refreshCakajuce() {
        cakajuceModel.setRowCount(0);
        for (Rozvoz r : controller.getCakajuceRozvozy()) {
            cakajuceModel.addRow(new Object[]{
                    r.getId(), extractVozidlo(r), extractDatum(r),
                    r.getZakazky().size(), r.getStav()
            });
        }
    }

    private void refreshSchvalene() {
        schvaleneModel.setRowCount(0);
        for (Rozvoz r : controller.getSchvaleneRozvozy()) {
            schvaleneModel.addRow(new Object[]{
                    r.getId(), extractVozidlo(r), extractDatum(r),
                    r.getZakazky().size(), r.getStav()
            });
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  POMOCNÉ
    // ═════════════════════════════════════════════════════════════════════════

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

    // Rozvoz nemá public getVozidlo/getDatum – vyťahujeme z toString
    private String extractVozidlo(Rozvoz r) {
        String s = r.toString();
        try { return s.split("\\| Vozidlo: ")[1].split(" \\|")[0].trim(); }
        catch (Exception e) { return ""; }
    }

    private String extractDatum(Rozvoz r) {
        String s = r.toString();
        try { return s.split("\\| Dátum: ")[1].split(" \\|")[0].trim(); }
        catch (Exception e) { return ""; }
    }
}