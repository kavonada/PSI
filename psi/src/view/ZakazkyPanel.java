package view;

import ulozisko.DataStore;
import obchod.Zakazka;
import obchod.ZakazkaController;
import vyroba.DokoncenieVyrobyController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ZakazkyPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;
    private final ZakazkaController zakazkaController = new ZakazkaController();
    private final DokoncenieVyrobyController dokoncenieController = new DokoncenieVyrobyController();
    public static ZakazkyPanel instance;

    public interface OnZakazkaSelected {
        void vybrana(Zakazka z);
    }

    public ZakazkyPanel(OnZakazkaSelected listener) {
        instance = this;
        setLayout(new BorderLayout());

        String[] cols = {"ID", "Názov", "Zákazník", "Stav"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton openBtn = new JButton("➡ Otvoriť vo výrobe");
        JButton doneBtn = new JButton("✔ Označiť za dokončenú");
        JButton cancelBtn = new JButton("❌ Zrušiť zákazku");

        Dimension buttonSize = new Dimension(175, 20);

        openBtn.setPreferredSize(buttonSize);
        doneBtn.setPreferredSize(buttonSize);
        cancelBtn.setPreferredSize(buttonSize);

        openBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        doneBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        btnPanel.add(openBtn);
        btnPanel.add(doneBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);

        openBtn.addActionListener(e -> {
            Zakazka z = getSelectedZakazka();

            if (z == null) {
                JOptionPane.showMessageDialog(this, "Vyber zákazku zo zoznamu.");
                return;
            }

            listener.vybrana(z);
        });

        cancelBtn.addActionListener(e -> {
            Zakazka z = getSelectedZakazka();

            if (z == null) {
                JOptionPane.showMessageDialog(this, "Vyber zákazku zo zoznamu.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Naozaj chceš zrušiť zákazku #" + z.getId() + " - " + z.getNazov() + "?",
                    "Potvrdenie zrušenia",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            zakazkaController.zrusZakazku(z);
            refresh();

            if (VyrobaPanel.instance != null) {
                VyrobaPanel.instance.refreshZakazky();
                VyrobaPanel.instance.refreshMaterials();
            }

            JOptionPane.showMessageDialog(this, "Zákazka bola zrušená.");
        });

        doneBtn.addActionListener(e -> {
            Zakazka z = getSelectedZakazka();

            if (z == null) {
                JOptionPane.showMessageDialog(this, "Vyber zákazku zo zoznamu.");
                return;
            }

            if (z.getStav() != Zakazka.StavZakazky.NAPLANOVANA) {
                JOptionPane.showMessageDialog(
                        this,
                        "Dokončiť je možné iba zákazku v stave NAPLANOVANA.",
                        "Nemožno dokončiť",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Naozaj chceš označiť zákazku #" + z.getId() + " - " + z.getNazov() + " ako dokončenú?",
                    "Potvrdenie dokončenia",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            boolean dokoncena = dokoncenieController.oznacZakazkuAkoDokoncenu(z);

            if (!dokoncena) {
                JOptionPane.showMessageDialog(
                        this,
                        "Zákazku sa nepodarilo dokončiť.",
                        "Chyba",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            refresh();

            if (VyrobaPanel.instance != null) {
                VyrobaPanel.instance.refreshZakazky();
            }

            JOptionPane.showMessageDialog(this, "Zákazka bola označená ako dokončená.");
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Zakazka z = getSelectedZakazka();

                    if (z != null) {
                        JOptionPane.showMessageDialog(null, z.toString());
                    }
                }
            }
        });

        refresh();
    }

    private Zakazka getSelectedZakazka() {
        int row = table.getSelectedRow();

        if (row < 0) {
            return null;
        }

        int modelRow = table.convertRowIndexToModel(row);
        int id = (Integer) model.getValueAt(modelRow, 0);

        return DataStore.zakazky.stream()
                .filter(z -> z.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public void refresh() {
        model.setRowCount(0);

        for (Zakazka z : DataStore.zakazky) {
            if (z.getStav() != Zakazka.StavZakazky.ZRUSENA) {
                model.addRow(new Object[]{
                        z.getId(),
                        z.getNazov(),
                        z.getZakaznik().getMeno(),
                        z.getStav()
                });
            }
        }
    }
}