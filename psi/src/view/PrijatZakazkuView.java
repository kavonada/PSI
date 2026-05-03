package view;

import obchod.ZakazkaController;
import obchod.ValidationException;
import sklad.InventarController;
import obchod.Zakazka;
import sklad.Material;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrijatZakazkuView extends JPanel {

    private final ZakazkaController controller;

    private JTextField nazovField;
    private JTextArea popisArea;

    private JPanel materialyPanel;
    private final List<JComboBox<String>> materialComboBoxy = new ArrayList<>();
    private JLabel materialStatusLabel;

    private JTextField cenaField;
    private JLabel cenaErrorLabel;

    private JComboBox<Integer> denCombo;
    private JComboBox<Integer> mesiacCombo;
    private JComboBox<Integer> rokCombo;
    private JLabel terminErrorLabel;

    private JTextField menoField;
    private JTextField emailField;
    private JTextField telefonField;

    private JTextField ulicaField;
    private JTextField cisloDomuField;
    private JTextField mestoField;
    private JTextField pscField;

    private JLabel statusLabel;

    private static final Color ERROR_BG = new Color(255, 220, 220);
    private static final Color NORMAL_BG = Color.WHITE;

    public PrijatZakazkuView(ZakazkaController controller) {
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        add(buildNadpis(), BorderLayout.NORTH);
        add(buildObsah(), BorderLayout.CENTER);
        add(buildTlacidla(), BorderLayout.SOUTH);

        pridajMaterialovyRiadok();
    }

    private JLabel buildNadpis() {
        JLabel label = new JLabel("UC01 – Prijatie zákazky", SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.BOLD, 20));
        label.setForeground(new Color(40, 70, 120));
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
        return label;
    }

    private JPanel buildObsah() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0));
        panel.setBackground(Color.WHITE);

        panel.add(buildProduktPanel());
        panel.add(buildPravaStrana());

        return panel;
    }

    private JPanel buildProduktPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(buildTitledBorder("Informácie o produkte"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();

        int row = 0;

        lc.gridx = 0; lc.gridy = row;
        form.add(new JLabel("Názov produktu:"), lc);

        nazovField = new JTextField();
        fc.gridx = 0; fc.gridy = ++row;
        form.add(nazovField, fc);

        lc.gridy = ++row;
        form.add(new JLabel("Materiály:"), lc);

        materialyPanel = new JPanel();
        materialyPanel.setLayout(new BoxLayout(materialyPanel, BoxLayout.Y_AXIS));
        materialyPanel.setBackground(Color.WHITE);

        JScrollPane materialyScroll = new JScrollPane(materialyPanel);
        materialyScroll.setPreferredSize(new Dimension(0, 120));
        materialyScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        materialyScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        materialyScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        fc.gridy = ++row;
        fc.weighty = 0;
        form.add(materialyScroll, fc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(Color.WHITE);

        JButton pridatMaterialButton = createButton("+ Pridať materiál", this::pridajMaterialovyRiadok);
        JButton overitMaterialyButton = createButton("Skontrolovať materiály", this::skontrolujMaterialy);

        buttons.add(pridatMaterialButton);
        buttons.add(overitMaterialyButton);

        fc.gridy = ++row;
        form.add(buttons, fc);

        materialStatusLabel = new JLabel("<html>&nbsp;</html>");
        materialStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        materialStatusLabel.setForeground(Color.RED.darker());

        fc.gridy = ++row;
        form.add(materialStatusLabel, fc);

        lc.gridy = ++row;
        form.add(new JLabel("Popis:"), lc);

        popisArea = new JTextArea(12, 30);
        popisArea.setLineWrap(true);
        popisArea.setWrapStyleWord(true);

        fc.gridy = ++row;
        fc.fill = GridBagConstraints.BOTH;
        fc.weighty = 1.0;
        form.add(new JScrollPane(popisArea), fc);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildPravaStrana() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setBackground(Color.WHITE);

        panel.add(buildDetailyZakazkyPanel());
        panel.add(buildZakaznikPanel());

        return panel;
    }

    private JPanel buildDetailyZakazkyPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(buildTitledBorder("Detaily zákazky"));

        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();
        fc.gridx = 1;

        // nech obsah nie je v strede
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = 99;
        filler.gridwidth = 2;
        filler.weighty = 1.0;
        filler.fill = GridBagConstraints.VERTICAL;

        int row = 0;

        lc.gridx = 0; lc.gridy = row;
        panel.add(new JLabel("Cena:"), lc);

        cenaField = new JTextField();
        fc.gridy = row++;
        panel.add(cenaField, fc);

        cenaErrorLabel = buildErrorLabel();
        GridBagConstraints cenaEc = errorLabelConstraints();
        cenaEc.gridy = row++;
        panel.add(cenaErrorLabel, cenaEc);

        lc.gridx = 0; lc.gridy = row;
        panel.add(new JLabel("Termín doručenia:"), lc);

        JPanel datePanel = new JPanel(new GridLayout(1, 3, 6, 0));
        datePanel.setBackground(Color.WHITE);

        denCombo = new JComboBox<>();
        mesiacCombo = new JComboBox<>();
        rokCombo = new JComboBox<>();

        for (int i = 1; i <= 31; i++) denCombo.addItem(i);
        for (int i = 1; i <= 12; i++) mesiacCombo.addItem(i);

        int aktualnyRok = LocalDate.now().getYear();
        for (int i = aktualnyRok; i <= aktualnyRok + 5; i++) rokCombo.addItem(i);

        datePanel.add(denCombo);
        datePanel.add(mesiacCombo);
        datePanel.add(rokCombo);

        fc.gridy = row++;
        panel.add(datePanel, fc);

        terminErrorLabel = buildErrorLabel();
        GridBagConstraints terminEc = errorLabelConstraints();
        terminEc.gridy = row++;
        panel.add(terminErrorLabel, terminEc);

        panel.add(new JLabel(), filler);

        return panel;
    }

    private JPanel buildZakaznikPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(buildTitledBorder("Údaje o zákazníkovi"));

        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();

        int row = 0;

        lc.gridx = 0; lc.gridy = row;
        panel.add(new JLabel("Meno zákazníka:"), lc);
        menoField = new JTextField();
        fc.gridx = 1; fc.gridy = row++;
        panel.add(menoField, fc);

        lc.gridy = row;
        panel.add(new JLabel("E-mail:"), lc);
        emailField = new JTextField();
        fc.gridy = row++;
        panel.add(emailField, fc);

        lc.gridy = row;
        panel.add(new JLabel("Telefón:"), lc);
        telefonField = new JTextField();
        fc.gridy = row++;
        panel.add(telefonField, fc);

        lc.gridy = row;
        panel.add(new JLabel("Ulica:"), lc);
        ulicaField = new JTextField();
        fc.gridy = row++;
        panel.add(ulicaField, fc);

        lc.gridy = row;
        panel.add(new JLabel("Číslo domu:"), lc);
        cisloDomuField = new JTextField();
        fc.gridy = row++;
        panel.add(cisloDomuField, fc);

        lc.gridy = row;
        panel.add(new JLabel("Mesto:"), lc);
        mestoField = new JTextField();
        fc.gridy = row++;
        panel.add(mestoField, fc);

        lc.gridy = row;
        panel.add(new JLabel("PSČ:"), lc);
        pscField = new JTextField();
        fc.gridy = row;
        panel.add(pscField, fc);

        return panel;
    }

    private JPanel buildTlacidla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        statusLabel = new JLabel("Pripravený.");
        statusLabel.setForeground(new Color(80, 80, 80));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBackground(Color.WHITE);

        JButton zrusitButton = createSecondaryButton("Zrušiť", this::potvrdZrusenieFormulara);
        JButton potvrditButton = createButton("Potvrdiť", this::ulozZakazku);

        buttons.add(zrusitButton);
        buttons.add(potvrditButton);

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(buttons, BorderLayout.EAST);

        return panel;
    }

    // Validacia

    private boolean validateForm() {
        // Reset zvyrazneni
        for (JTextField f : List.of(nazovField, menoField, emailField, telefonField,
                ulicaField, cisloDomuField, mestoField, pscField, cenaField)) {
            clearError(f);
        }
        cenaErrorLabel.setText("<html>&nbsp;</html>");
        terminErrorLabel.setText("<html>&nbsp;</html>");
        materialStatusLabel.setText("<html>&nbsp;</html>");

        for (JComboBox<String> combo : materialComboBoxy) {
            combo.setBorder(null);
        }

        boolean prazdnePolia = false;
        boolean ineChyby = false;

        if (nazovField.getText().isBlank()) { markError(nazovField); prazdnePolia = true; }
        if (menoField.getText().isBlank()) { markError(menoField); prazdnePolia = true; }
        if (emailField.getText().isBlank()) { markError(emailField); prazdnePolia = true; }
        if (telefonField.getText().isBlank()) { markError(telefonField); prazdnePolia = true; }
        if (ulicaField.getText().isBlank()) { markError(ulicaField); prazdnePolia = true; }
        if (cisloDomuField.getText().isBlank()) { markError(cisloDomuField); prazdnePolia = true; }
        if (mestoField.getText().isBlank()) { markError(mestoField); prazdnePolia = true; }
        if (pscField.getText().isBlank()) { markError(pscField); prazdnePolia = true; }

        // Cena
        String cenaText = cenaField.getText().replace(",", ".");
        if (cenaText.isBlank()) {
            markError(cenaField);
            cenaErrorLabel.setText("<html>Cena je povinná.</html>");
            prazdnePolia = true;
        } else {
            try {
                double cena = Double.parseDouble(cenaText);
                if (cena < 0) {
                    markError(cenaField);
                    cenaErrorLabel.setText("<html>Cena nesmie byť záporná.</html>");
                    ineChyby = true;
                }
            } catch (NumberFormatException e) {
                markError(cenaField);
                cenaErrorLabel.setText("<html>Cena musí byť číslo.</html>");
                ineChyby = true;
            }
        }

        // Termin
        try {
            int den = (Integer) denCombo.getSelectedItem();
            int mesiac = (Integer) mesiacCombo.getSelectedItem();
            int rok = (Integer) rokCombo.getSelectedItem();
            LocalDate termin = LocalDate.of(rok, mesiac, den);
            if (termin.isBefore(LocalDate.now())) {
                terminErrorLabel.setText("<html>Termín doručenia nemôže byť v minulosti.</html>");
                ineChyby = true;
            }
        } catch (Exception e) {
            terminErrorLabel.setText("<html>Zadaný termín doručenia nie je platný.</html>");
            ineChyby = true;
        }

        // Materialy
        if (getVybraneMaterialy().isEmpty()) {
            materialStatusLabel.setForeground(Color.RED.darker());
            materialStatusLabel.setText("<html>Musí byť zadaný aspoň jeden materiál.</html>");
            prazdnePolia = true;
        }

        if (prazdnePolia && ineChyby) {
            setStatus("Vyplňte všetky povinné polia a opravte chyby vo formulári.", Color.RED.darker());
        } else if (prazdnePolia) {
            setStatus("Vyplňte všetky povinné polia.", Color.RED.darker());
        } else if (ineChyby) {
            setStatus("Opravte chyby vo formulári.", Color.RED.darker());
        }

        return !prazdnePolia && !ineChyby;
    }

    private void ulozZakazku() {
        if (!validateForm()) {
            return;
        }

        try {
            Zakazka zakazka = controller.vytvorZakazku(
                    nazovField.getText(),
                    popisArea.getText(),
                    menoField.getText(),
                    emailField.getText(),
                    telefonField.getText(),
                    ulicaField.getText(),
                    cisloDomuField.getText(),
                    mestoField.getText(),
                    pscField.getText(),
                    Double.parseDouble(cenaField.getText().replace(",", ".")),
                    nacitajTermin(),
                    getVybraneMaterialy()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Zákazka bola úspešne vytvorená. ID: " + zakazka.getId(),
                    "Hotovo",
                    JOptionPane.INFORMATION_MESSAGE
            );

            setStatus("Zákazka bola vytvorená.", new Color(0, 120, 0));
            zrusFormular();

        } catch (ValidationException ex) {
            // Controller zachytil ďalšie chyby (napr. logické) — zobraz ich
            setStatus("Chyba: " + ex.getMessage(), Color.RED.darker());
        }
    }

    private void pridajMaterialovyRiadok() {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JComboBox<String> combo = new JComboBox<>();
        for (Material material : InventarController.getMaterials()) {
            combo.addItem(material.getNazov() + " (" + material.getMnozstvo() + ")");
        }

        JButton removeButton = new JButton("×");
        removeButton.setFocusPainted(false);
        removeButton.setBorderPainted(false);
        removeButton.setContentAreaFilled(false);

        removeButton.addActionListener(e -> {
            materialComboBoxy.remove(combo);
            materialyPanel.remove(row);
            materialyPanel.revalidate();
            materialyPanel.repaint();
        });

        materialComboBoxy.add(combo);

        row.add(combo, BorderLayout.CENTER);
        row.add(removeButton, BorderLayout.EAST);

        materialyPanel.add(row);
        materialyPanel.revalidate();
        materialyPanel.repaint();
    }

    private void skontrolujMaterialy() {
        List<Material> vsetkyMaterialy = InventarController.getMaterials();

        // Reset zvyrazneni
        for (JComboBox<String> combo : materialComboBoxy) {
            combo.setBorder(null);
        }

        List<Material> vybrané = getVybraneMaterialy();
        List<Material> nedostatkove = controller.overMaterialy(vybrané);

        if (nedostatkove.isEmpty()) {
            materialStatusLabel.setForeground(new Color(0, 120, 0));
            materialStatusLabel.setText("<html>Všetky vybrané materiály sú dostupné.</html>");
            setStatus("Materiály boli skontrolované.", new Color(0, 120, 0));
            return;
        }

        // Zvyrazni nedostatok
        for (JComboBox<String> combo : materialComboBoxy) {
            int index = combo.getSelectedIndex();
            if (index >= 0 && index < vsetkyMaterialy.size()) {
                Material m = vsetkyMaterialy.get(index);
                if (m.getMnozstvo() <= 0) {
                    combo.setBorder(BorderFactory.createLineBorder(Color.RED.darker(), 2));
                }
            }
        }

        int pocet = nedostatkove.size();
        String sprava;
        if (pocet == 1) {
            sprava = "1 materiál nie je dostupný.";
        } else if (pocet < 5) {
            sprava = pocet + " materiály nie sú dostupné.";
        } else {
            sprava = pocet + " materiálov nie je dostupných.";
        }

        materialStatusLabel.setForeground(Color.RED.darker());
        materialStatusLabel.setText("<html>" + sprava + "</html>");
        setStatus("Niektoré materiály nie sú dostupné.", Color.RED.darker());
    }

    private void zrusFormular() {
        nazovField.setText("");
        popisArea.setText("");
        cenaField.setText("");
        menoField.setText("");
        emailField.setText("");
        telefonField.setText("");
        ulicaField.setText("");
        cisloDomuField.setText("");
        mestoField.setText("");
        pscField.setText("");

        // Reset zvyrazneni
        for (JTextField f : List.of(nazovField, menoField, emailField, telefonField,
                ulicaField, cisloDomuField, mestoField, pscField, cenaField)) {
            clearError(f);
        }
        cenaErrorLabel.setText("<html>&nbsp;</html>");
        terminErrorLabel.setText("<html>&nbsp;</html>");

        for (JComboBox<String> combo : materialComboBoxy) {
            combo.setBorder(null);
        }

        materialyPanel.removeAll();
        materialComboBoxy.clear();
        pridajMaterialovyRiadok();

        denCombo.setSelectedIndex(0);
        mesiacCombo.setSelectedIndex(0);
        rokCombo.setSelectedIndex(0);

        materialStatusLabel.setText("<html>&nbsp;</html>");
        setStatus("Formulár bol vymazaný.", new Color(80, 80, 80));
    }

    private void potvrdZrusenieFormulara() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Naozaj chceš zrušiť vytvorenie zákazky?",
                "Potvrdenie",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            zrusFormular();
        }
    }


    private LocalDate nacitajTermin() {
        int den = (Integer) denCombo.getSelectedItem();
        int mesiac = (Integer) mesiacCombo.getSelectedItem();
        int rok = (Integer) rokCombo.getSelectedItem();
        return LocalDate.of(rok, mesiac, den);
    }

    private List<Material> getVybraneMaterialy() {
        List<Material> materialy = new ArrayList<>();
        for (JComboBox<String> combo : materialComboBoxy) {
            int index = combo.getSelectedIndex();
            if (index >= 0 && index < InventarController.getMaterials().size()) {
                materialy.add(InventarController.getMaterials().get(index));
            }
        }
        return materialy;
    }

    private void markError(JTextField field) {
        field.setBackground(ERROR_BG);
    }

    private void clearError(JTextField field) {
        field.setBackground(NORMAL_BG);
    }

    private JLabel buildErrorLabel() {
        JLabel label = new JLabel("<html>&nbsp;</html>");
        label.setForeground(Color.RED.darker());
        label.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return label;
    }

    private GridBagConstraints errorLabelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 6, 4, 6);
        c.weightx = 1.0;
        return c;
    }

    private void setStatus(String text, Color color) {
        statusLabel.setText(text);
        statusLabel.setForeground(color);
    }

    private GridBagConstraints labelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(5, 6, 3, 6);
        c.weightx = 0;
        return c;
    }

    private GridBagConstraints fieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 6, 7, 6);
        c.weightx = 1.0;
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
                        new Color(40, 70, 120)
                ),
                new EmptyBorder(8, 10, 10, 10)
        );
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setBackground(new Color(40, 100, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> action.run());
        return button;
    }

    private JButton createSecondaryButton(String text, Runnable action) {
        JButton button = createButton(text, action);
        button.setBackground(new Color(200, 215, 235));
        button.setForeground(new Color(40, 70, 120));
        return button;
    }
}
