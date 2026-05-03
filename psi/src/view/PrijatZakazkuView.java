package view;

import obchod.ZakazkaController;
import sklad.InventarController;
import model.Zakazka;
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
    private JComboBox<Integer> denCombo;
    private JComboBox<Integer> mesiacCombo;
    private JComboBox<Integer> rokCombo;

    private JTextField menoField;
    private JTextField emailField;
    private JTextField telefonField;

    private JTextField ulicaField;
    private JTextField cisloDomuField;
    private JTextField mestoField;
    private JTextField pscField;

    private JLabel statusLabel;

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

        lc.gridx = 0;
        lc.gridy = row;
        form.add(new JLabel("Názov produktu:"), lc);

        nazovField = new JTextField();
        fc.gridx = 0;
        fc.gridy = ++row;
        form.add(nazovField, fc);

        lc.gridy = ++row;
        form.add(new JLabel("Materiály:"), lc);

        materialyPanel = new JPanel();
        materialyPanel.setLayout(new BoxLayout(materialyPanel, BoxLayout.Y_AXIS));
        materialyPanel.setBackground(Color.WHITE);

        fc.gridy = ++row;
        form.add(materialyPanel, fc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buttons.setBackground(Color.WHITE);

        JButton pridatMaterialButton = createButton("+ Pridať materiál", this::pridajMaterialovyRiadok);
        JButton overitMaterialyButton = createButton("Skontrolovať materiály", this::skontrolujMaterialy);

        buttons.add(pridatMaterialButton);
        buttons.add(overitMaterialyButton);

        fc.gridy = ++row;
        form.add(buttons, fc);

        materialStatusLabel = new JLabel(" ");
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

        int row = 0;

        lc.gridx = 0;
        lc.gridy = row;
        panel.add(new JLabel("Cena:"), lc);

        cenaField = new JTextField();
        fc.gridx = 1;
        fc.gridy = row++;
        panel.add(cenaField, fc);

        lc.gridx = 0;
        lc.gridy = row;
        panel.add(new JLabel("Najneskorší termín doručenia:"), lc);

        JPanel datePanel = new JPanel(new GridLayout(1, 3, 6, 0));
        datePanel.setBackground(Color.WHITE);

        denCombo = new JComboBox<>();
        mesiacCombo = new JComboBox<>();
        rokCombo = new JComboBox<>();

        for (int i = 1; i <= 31; i++) {
            denCombo.addItem(i);
        }

        for (int i = 1; i <= 12; i++) {
            mesiacCombo.addItem(i);
        }

        int aktualnyRok = LocalDate.now().getYear();
        for (int i = aktualnyRok; i <= aktualnyRok + 5; i++) {
            rokCombo.addItem(i);
        }

        datePanel.add(denCombo);
        datePanel.add(mesiacCombo);
        datePanel.add(rokCombo);

        fc.gridx = 1;
        fc.gridy = row;
        panel.add(datePanel, fc);

        return panel;
    }

    private JPanel buildZakaznikPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(buildTitledBorder("Údaje o zákazníkovi"));

        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();

        int row = 0;

        lc.gridx = 0;
        lc.gridy = row;
        panel.add(new JLabel("Meno zákazníka:"), lc);

        menoField = new JTextField();
        fc.gridx = 1;
        fc.gridy = row++;
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

        JButton zrusitButton = createSecondaryButton("Zrušiť", this::zrusFormular);
        JButton potvrditButton = createButton("Potvrdiť", this::ulozZakazku);

        buttons.add(zrusitButton);
        buttons.add(potvrditButton);

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(buttons, BorderLayout.EAST);

        return panel;
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
        List<Material> materialy = getVybraneMaterialy();

        List<Material> nedostatkove = controller.overMaterialy(materialy);

        if (nedostatkove.isEmpty()) {
            materialStatusLabel.setForeground(new Color(0, 120, 0));
            materialStatusLabel.setText("Všetky vybrané materiály sú dostupné.");
            setStatus("Materiály boli skontrolované.", new Color(0, 120, 0));
            return;
        }

        StringBuilder sprava = new StringBuilder("Nedostupné materiály: ");
        for (Material material : nedostatkove) {
            sprava.append(material.getNazov()).append(" ");
        }

        materialStatusLabel.setForeground(Color.RED.darker());
        materialStatusLabel.setText(sprava.toString());
        setStatus("Niektoré materiály nie sú dostupné.", Color.RED.darker());
    }

    private void ulozZakazku() {
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
                    nacitajCenu(),
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

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Chyba",
                    JOptionPane.ERROR_MESSAGE
            );
            setStatus("Chyba: " + ex.getMessage(), Color.RED.darker());
        }
    }

    private double nacitajCenu() {
        try {
            return Double.parseDouble(cenaField.getText().replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Cena musí byť číslo.");
        }
    }

    private LocalDate nacitajTermin() {
        int den = (Integer) denCombo.getSelectedItem();
        int mesiac = (Integer) mesiacCombo.getSelectedItem();
        int rok = (Integer) rokCombo.getSelectedItem();

        try {
            return LocalDate.of(rok, mesiac, den);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Zadaný termín doručenia nie je platný.");
        }
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

        materialyPanel.removeAll();
        materialComboBoxy.clear();
        pridajMaterialovyRiadok();

        denCombo.setSelectedIndex(0);
        mesiacCombo.setSelectedIndex(0);
        rokCombo.setSelectedIndex(0);

        materialStatusLabel.setText(" ");
        setStatus("Formulár bol vymazaný.", new Color(80, 80, 80));
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
