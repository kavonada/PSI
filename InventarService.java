import java.util.Scanner;

public class InventarService {

    private static final double LIMIT_SCHVALENIA = 1000.0;
    private static final double CENA_ZA_KS = 15.0;

    // UC03 – Objednanie materiálu
    public static void objednatMaterial() {

        System.out.println("\n--- Objednanie materiálu (UC03) ---");
        System.out.println("Aktuálny stav skladu:");

        if (DataStore.materialy.isEmpty()) {
            System.out.println("Sklad je prázdny.");
            return;
        }

        for (int i = 0; i < DataStore.materialy.size(); i++) {
            System.out.println(i + " - " + DataStore.materialy.get(i));
        }

        Scanner sc = new Scanner(System.in);

        System.out.print("\nVyber materiál na objednanie (index): ");
        int index;
        try {
            index = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatný index.");
            return;
        }

        if (index < 0 || index >= DataStore.materialy.size()) {
            System.out.println("Chyba: Neplatný výber materiálu.");
            return;
        }

        Material vybrany = DataStore.materialy.get(index);
        System.out.println("Vybraný materiál: " + vybrany.getNazov()
                + " | Aktuálne množstvo: " + vybrany.getMnozstvo());

        System.out.print("Zadaj požadované množstvo na objednanie: ");
        int mnozstvo;
        try {
            mnozstvo = Integer.parseInt(sc.nextLine().trim());
            if (mnozstvo <= 0) {
                System.out.println("Chyba: Množstvo musí byť kladné.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné množstvo.");
            return;
        }

        // Výber dodávateľa
        System.out.println("\nDostupní dodávatelia:");
        String[] dodavatelia = {"WoodSupply s.r.o.", "DrevMateriál a.s.", "EuroDrev Ltd."};
        for (int i = 0; i < dodavatelia.length; i++) {
            System.out.println(i + " - " + dodavatelia[i]);
        }
        System.out.print("Vyber dodávateľa (index): ");
        int dodIndex;
        try {
            dodIndex = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatný výber dodávateľa.");
            return;
        }
        if (dodIndex < 0 || dodIndex >= dodavatelia.length) {
            System.out.println("Chyba: Neplatný dodávateľ.");
            return;
        }

        String dodavatel = dodavatelia[dodIndex];
        double celkovaCena = mnozstvo * CENA_ZA_KS;

        System.out.println("\nSúhrn objednávky:");
        System.out.println("  Materiál:     " + vybrany.getNazov());
        System.out.println("  Množstvo:     " + mnozstvo + " ks");
        System.out.println("  Dodávateľ:    " + dodavatel);
        System.out.println("  Celková cena: " + celkovaCena + " EUR");

        if (celkovaCena > LIMIT_SCHVALENIA) {
            // Uložíme objednávku čakajúcu na schválenie manažérom
            ObjednavkaMaterialu objednavka = new ObjednavkaMaterialu(vybrany, mnozstvo, dodavatel, celkovaCena);
            DataStore.cakajuceObjednavky.add(objednavka);
            System.out.println("\nObjednávka prekračuje limit " + LIMIT_SCHVALENIA
                    + " EUR – odoslaná na schválenie manažérovi.");
            System.out.println("Stav objednávky: Čaká na schválenie");
        } else {
            vybrany.pridaj(mnozstvo);
            System.out.println("\nObjednávka potvrdená a materiál pridaný na sklad.");
            System.out.println("Aktualizovaný stav: " + vybrany);
        }
    }
}