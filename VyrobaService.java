import java.util.Scanner;

public class VyrobaService {

    // UC02 – Plánovať výrobu
    public static void planovatVyrobu() {

        if (DataStore.zakazky.isEmpty()) {
            System.out.println("Žiadne zákazky na plánovanie.");
            return;
        }

        System.out.println("\n--- Plánovanie výroby (UC02) ---");
        ZakazkaService.vypisZakazky();

        Scanner sc = new Scanner(System.in);
        System.out.print("Vyber ID zákazky: ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatné ID.");
            return;
        }

        Zakazka vybrana = null;
        for (Zakazka z : DataStore.zakazky) {
            if (z.getId() == id) {
                vybrana = z;
                break;
            }
        }

        if (vybrana == null) {
            System.out.println("Zákazka nenájdená.");
            return;
        }

        // Zobrazenie detailov zákazky
        System.out.println("\nDetail zákazky:");
        System.out.println(vybrana);

        // Rozdelenie na výrobné úlohy
        System.out.println("\nVýrobné úlohy pre zákazku:");
        System.out.println("  1. Príprava materiálu – " + vybrana.getMaterialNazov()
                + " (" + vybrana.getMaterialMnozstvo() + " ks)");
        System.out.println("  2. Výroba komponentov");
        System.out.println("  3. Montáž a kontrola kvality");

        // Materiál bol už odpísaný zo skladu pri vytvorení zákazky – nie je potrebné znova kontrolovať
        System.out.println("  Materiál: rezervovaný pri vytvorení zákazky ✓");

        // Priradenie pracovníka
        System.out.print("\nZadaj meno pracovníka: ");
        String pracovnik = sc.nextLine().trim();
        if (pracovnik.isEmpty()) {
            System.out.println("Upozornenie: Žiadny pracovník nebol zadaný.");
        }

        // Priradenie stroja
        System.out.print("Zadaj stroj/zariadenie: ");
        String stroj = sc.nextLine().trim();
        if (stroj.isEmpty()) {
            System.out.println("Upozornenie: Žiadny stroj nebol zadaný.");
        }

        // Stav závisí len od priradenia zdrojov (pracovník + stroj)
        boolean zdrojeDostupne = !pracovnik.isEmpty() && !stroj.isEmpty();

        if (!zdrojeDostupne) {
            vybrana.setStav("Čiastočne naplánovaná");
            System.out.println("\nPlán uložený so stavom: Čiastočne naplánovaná");
            System.out.println("  – Dôvod: chýbajú zdroje (pracovník/stroj)");
        } else {
            vybrana.setStav("Naplánovaná");
            System.out.println("\nPlán výroby vytvorený:");
            System.out.println("  Zákazka:   " + vybrana.getNazov());
            System.out.println("  Pracovník: " + pracovnik);
            System.out.println("  Stroj:     " + stroj);
            System.out.println("  Stav:      Naplánovaná");
        }
    }
}