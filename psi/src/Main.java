import view.MainView;
import javax.swing.*;

/**
 * Vstupný bod aplikácie – spustí GUI na EDT.
 */
public class Main {
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MainView okno = new MainView();
            okno.setVisible(true);
        });
    }
}
