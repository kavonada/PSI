package view;

import controller.InventarController;
import controller.VyrobaController;
import model.DataStore;
import model.Zakazka;
import model.use_case_2.*;
import model.use_case_3.Material;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VyrobaPanel extends JPanel {

    private final VyrobaController vyrobaCtrl = new VyrobaController();

    private JComboBox<Zakazka> zakazkaCombo;
    private JTextArea detailArea;

    private JTextField nazovField;
    private JComboBox<String> operaciaCombo;
    private JComboBox<String> materialCombo;
    private JSpinner mnozstvoSpinner;
    private JComboBox<Pracovnik> workerCombo;
    private JComboBox<Stroj> strojCombo;
    private JLabel dostupnostLabel;

    private DefaultTableModel tableModel;
    private JTable table;
    private Zakazka aktualnaZakazka;

    public VyrobaPanel() {
        setLayout(new BorderLayout());
        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
    }

    private JPanel buildTop() {
        JPanel panel = new JPanel(new FlowLayout());
        zakazkaCombo = new JComboBox<>(DataStore.zakazky.toArray(new Zakazka[0]));
        zakazkaCombo.setPreferredSize(new Dimension(750, 25));

        zakazkaCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Zakazka z) value = z.getDisplayName() + " [" + z.getStav() + "]";
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        zakazkaCombo.addActionListener(e -> zobrazDetail());
        panel.add(new JLabel("Zákazka:"));
        panel.add(zakazkaCombo);
        return panel;
    }

    private JSplitPane buildCenter() {
        JSplitPane vertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, buildDetail(), buildMain());
        vertical.setResizeWeight(0.3);
        return vertical;
    }

    private JPanel buildDetail() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Detail zákazky"));
        detailArea = new JTextArea();
        detailArea.setEditable(false);
        p.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        return p;
    }

    private JSplitPane buildMain() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildForm(), buildTable());
        split.setResizeWeight(0.4);
        return split;
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;
        nazovField = new JTextField(15);
        operaciaCombo = new JComboBox<>(new String[]{"Rezanie", "Lakovanie", "Montáž"});

        materialCombo = new JComboBox<>();
        for (Material m : InventarController.getMaterials()) {
            materialCombo.addItem(m.getNazov() + " (Sklad: " + m.getMnozstvo() + ")");
        }

        mnozstvoSpinner = new JSpinner(new SpinnerNumberModel(1,1,1000,1));

        workerCombo = new JComboBox<>();
        workerCombo.addItem(null);
        for (Pracovnik w : DataStore.pracovnici) workerCombo.addItem(w);

        strojCombo = new JComboBox<>();
        strojCombo.addItem(null);
        for (Stroj s : DataStore.stroje) strojCombo.addItem(s);

        ListCellRenderer<Object> nullRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value == null) value = "--- Nenastavené ---";
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        };
        workerCombo.setRenderer(nullRenderer);
        strojCombo.setRenderer(nullRenderer);

        dostupnostLabel = new JLabel("");

        JButton addBtn = new JButton("Pridať úlohu");
        addBtn.addActionListener(e -> pridajUlohu());

        addRow(p, c, y++, "Názov úlohy:", nazovField);
        addRow(p, c, y++, "Operácia:", operaciaCombo);
        addRow(p, c, y++, "Materiál:", materialCombo);
        addRow(p, c, y++, "Množstvo:", mnozstvoSpinner);
        addRow(p, c, y++, "Pracovník:", workerCombo);
        addRow(p, c, y++, "Stroj:", strojCombo);

        c.gridx=0; c.gridy=y++; c.gridwidth=2;
        p.add(dostupnostLabel, c);

        c.gridy=y;
        p.add(addBtn, c);

        workerCombo.addActionListener(e -> kontrolujDostupnost());
        strojCombo.addActionListener(e -> kontrolujDostupnost());

        return p;
    }

    private void addRow(JPanel p, GridBagConstraints c, int y, String label, Component comp) {
        c.gridx=0; c.gridy=y; c.gridwidth=1;
        p.add(new JLabel(label), c);
        c.gridx=1;
        p.add(comp, c);
    }

    private JPanel buildTable() {
        JPanel p = new JPanel(new BorderLayout());
        String[] cols = {"Úloha", "Operácia", "Materiál", "Množ.", "Pracovník", "Stroj", "Stav"};
        tableModel = new DefaultTableModel(cols,0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = getJPanel();
        p.add(btnPanel, BorderLayout.SOUTH);

        return p;
    }

    private JPanel getJPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteBtn = new JButton("❌ Odstrániť");
        deleteBtn.addActionListener(e -> odstranUlohu());

        JButton partialBtn = new JButton("Uložiť rozpracované");
        partialBtn.addActionListener(e -> ulozitCiastocne());

        JButton finalizeBtn = new JButton("✔ Naplánovať zákazku");
        finalizeBtn.addActionListener(e -> finalizujZakazku());

        btnPanel.add(deleteBtn);
        btnPanel.add(partialBtn);
        btnPanel.add(finalizeBtn);
        return btnPanel;
    }

    private void zobrazDetail() {
        aktualnaZakazka = (Zakazka) zakazkaCombo.getSelectedItem();
        if (aktualnaZakazka != null) {
            detailArea.setText(aktualnaZakazka.toString());
            refreshTable();
        }
    }

    private void kontrolujDostupnost() {
        Pracovnik w = (Pracovnik) workerCombo.getSelectedItem();
        Stroj s = (Stroj) strojCombo.getSelectedItem();

        if (w == null || s == null) {
            dostupnostLabel.setText("⚠ Zdroje nenastavené (Čiastočné plánovanie)");
            dostupnostLabel.setForeground(new Color(200, 100, 0));
        } else if (vyrobaCtrl.mozemNaplanovatZdroje(w, s)) {
            dostupnostLabel.setText("✔ Zdroje dostupné");
            dostupnostLabel.setForeground(new Color(0, 150, 0));
        } else {
            dostupnostLabel.setText("❌ Nedostupné (Pracovník alebo Stroj)");
            dostupnostLabel.setForeground(Color.RED);
        }
    }

    private void pridajUlohu() {
        // 1. VALIDÁCIA ZÁKAZKY
        if (aktualnaZakazka == null) {
            JOptionPane.showMessageDialog(this, "Najprv vyberte zákazku zo zoznamu!", "Chyba", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. VALIDÁCIA NÁZVU (nesmie byť null ani prázdny)
        String nazovUlohy = nazovField.getText().trim();
        if (nazovUlohy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Názov výrobnej úlohy nesmie byť prázdny!", "Chýbajúce údaje", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. VALIDÁCIA MATERIÁLU
        int matIndex = materialCombo.getSelectedIndex();
        if (matIndex < 0) {
            JOptionPane.showMessageDialog(this, "Musíte vybrať materiál!", "Chýbajúce údaje", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. ZÍSKANIE ZDROJOV (Môžu byť null, ak používateľ vybral "Nenastavené")
        Pracovnik w = (Pracovnik) workerCombo.getSelectedItem();
        Stroj s = (Stroj) strojCombo.getSelectedItem();

        // 5. KONTROLA DOSTUPNOSTI (IBA AK NIE SÚ NULL)
        // Týmto umožníme zadať len pracovníka, alebo len stroj
        if ((w != null && !w.jeDostupny())){
            JOptionPane.showMessageDialog(this, "Zvolený pracovník je nedostupný. Vyberte iného alebo nechajte nenastavené.", "Nedostupný zdroj", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (s != null && !s.jeDostupny()) {
            JOptionPane.showMessageDialog(this, "Zvolený stroj je nedostupný. Vyberte iný alebo nechajte nenastavené.", "Nedostupný zdroj", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 6. SPRACOVANIE MATERIÁLU
        Material m = controller.InventarController.getMaterials().get(materialCombo.getSelectedIndex());
        int mnozstvo = (Integer) mnozstvoSpinner.getValue();

        boolean dostatok = vyrobaCtrl.jeDostatokMaterialu(m, mnozstvo);
        if (!dostatok) {
            int res = JOptionPane.showConfirmDialog(this,
                    "Na sklade nie je dostatok tohto materiálu. Chcete poslať požiadavku na doobjednanie?",
                    "Nedostatok materiálu",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (res == JOptionPane.YES_OPTION) {
                int chybaMne = mnozstvo - m.getMnozstvo();
                vyrobaCtrl.poziadajOObjednanie(m, chybaMne);
                JOptionPane.showMessageDialog(this, "Požiadavka odoslaná na sklad.");
            }
        } else {
            m.odober(mnozstvo);
        }

        // 7. FINÁLNE ULOŽENIE ÚLOHY
        VyrobnaUloha uloha = new VyrobnaUloha(nazovField.getText(), operaciaCombo.getSelectedItem().toString(), m, mnozstvo, w, s, !dostatok);
        aktualnaZakazka.getVyrobneUlohy().add(uloha);

        nazovField.setText("");
        refreshTable();
        refreshMaterials();
    }

    private void odstranUlohu() {
        int row = table.getSelectedRow();
        if (row >= 0 && aktualnaZakazka != null) {
            VyrobnaUloha vymazavana = aktualnaZakazka.getVyrobneUlohy().remove(row);
            if (!vymazavana.isCakaNaMaterial()) {
                vymazavana.getMaterial().zmenitMnozstvo(vymazavana.getMnozstvo());
            }
            aktualnaZakazka.getVyrobneUlohy().remove(row);
            refreshTable();
            refreshMaterials();
        } else {
            JOptionPane.showMessageDialog(this, "Vyberte úlohu zo zoznamu.");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        if (aktualnaZakazka == null) return;

        for (VyrobnaUloha u : aktualnaZakazka.getVyrobneUlohy()) {
            String stav = u.isCakaNaMaterial() ? "Čaká na materiál" : "Pripravené";
            tableModel.addRow(new Object[]{
                    u.getNazov(), u.getOperacia(), u.getMaterial().getNazov(), u.getMnozstvo(),
                    u.getPracovnik().getMeno(), u.getStroj().getNazov(), stav
            });
        }

        // Ak sa vymažú všetky úlohy, vráti sa stav do "Vytvorená"
        if (aktualnaZakazka.getVyrobneUlohy().isEmpty() && aktualnaZakazka.getStav() != Zakazka.StavZakazky.VYTVORENA) {
            aktualnaZakazka.setStav(Zakazka.StavZakazky.VYTVORENA);
            if (ZakazkyPanel.instance != null) ZakazkyPanel.instance.refresh();
        }
    }

    private void ulozitCiastocne() {
        if (aktualnaZakazka == null) return;
        aktualnaZakazka.setStav(Zakazka.StavZakazky.CIASTOCNE_NAPLANOVANA);
        zakazkaCombo.repaint();
        if (ZakazkyPanel.instance != null) ZakazkyPanel.instance.refresh();
        JOptionPane.showMessageDialog(this, "Zákazka uložená ako rozpracovaná.");
    }

    private void finalizujZakazku() {
        if (aktualnaZakazka == null) return;

        vyrobaCtrl.prehodnotStavZakazky(aktualnaZakazka);
        zakazkaCombo.repaint();

        if (ZakazkyPanel.instance != null) {
            ZakazkyPanel.instance.refresh();
        }

        JOptionPane.showMessageDialog(this, "Plánovanie ukončené. Nový stav: " + aktualnaZakazka.getStav());
    }

    public void setZakazka(Zakazka z) {
        zakazkaCombo.setSelectedItem(z);
        zobrazDetail();
    }

    public void refreshMaterials() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (model.use_case_3.Material m : InventarController.getMaterials()) {
            model.addElement(m.getNazov() + " (Sklad: " + m.getMnozstvo() + " ks)");
        }
        materialCombo.setModel(model);
    }
}