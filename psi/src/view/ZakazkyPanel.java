package view;

import model.DataStore;
import model.Zakazka;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ZakazkyPanel extends JPanel {

    private final JTable table;
    private final DefaultTableModel model;
    public static ZakazkyPanel instance;

    public interface OnZakazkaSelected {
        void vybrana(Zakazka z);
    }

    public ZakazkyPanel(OnZakazkaSelected listener) {
        instance = this;
        setLayout(new BorderLayout());

        String[] cols = {"ID","Názov","Zákazník","Stav"};
        model = new DefaultTableModel(cols,0);

        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton openBtn = new JButton("➡ Otvoriť vo výrobe");
        add(openBtn, BorderLayout.SOUTH);

        openBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                Zakazka z = DataStore.zakazky.get(row);
                listener.vybrana(z);
            }
        });

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    Zakazka z = DataStore.zakazky.get(row);

                    JOptionPane.showMessageDialog(null, z.toString());
                }
            }
        });

        refresh();
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