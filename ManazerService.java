import java.util.Scanner;

public class ManazerService {

    public static void manazerMenu() {

        Scanner sc = new Scanner(System.in);

        System.out.print("\nZadaj heslo manažéra: ");
        String heslo = sc.nextLine().trim();

        if (!heslo.equals(DataStore.MANAZER_HESLO)) {
            System.out.println("Chyba: Nesprávne heslo. Prístup zamietnutý.");
            return;
        }

        System.out.println("Prístup povolený. Vitajte, manažér.");

        while (true) {
            System.out.println("\n=== MANAŽÉR ===");
            System.out.println("1 - Objednávky materiálu čakajúce na schválenie");
            System.out.println("2 - Rozvozy čakajúce na schválenie");
            System.out.println("0 - Späť");

            int volba;
            try {
                volba = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Neplatná voľba.");
                continue;
            }

            switch (volba) {
                case 1 -> schvalatObjednavky(sc);
                case 2 -> schvalatRozvozy(sc);
                case 0 -> { return; }
                default -> System.out.println("Neplatná voľba.");
            }
        }
    }

    // ── Objednávky materiálu ──────────────────────────────────────────────────

    private static void schvalatObjednavky(Scanner sc) {

        System.out.println("\n--- Objednávky materiálu čakajúce na schválenie ---");

        boolean najdena = false;
        for (ObjednavkaMaterialu o : DataStore.cakajuceObjednavky) {
            if (o.getStav().equals("Čaká na schválenie")) {
                System.out.println(o);
                najdena = true;
            }
        }

        if (!najdena) {
            System.out.println("Žiadne objednávky nečakajú na schválenie.");
            return;
        }

        System.out.print("\nZadaj ID objednávky na spracovanie (0 = späť): ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Neplatné ID.");
            return;
        }
        if (id == 0) return;

        ObjednavkaMaterialu vybrana = null;
        for (ObjednavkaMaterialu o : DataStore.cakajuceObjednavky) {
            if (o.getId() == id && o.getStav().equals("Čaká na schválenie")) {
                vybrana = o;
                break;
            }
        }

        if (vybrana == null) {
            System.out.println("Objednávka nenájdená alebo už spracovaná.");
            return;
        }

        System.out.println("Objednávka: " + vybrana);
        System.out.print("Schváliť objednávku? (a/n): ");
        String rozhodnutie = sc.nextLine().trim().toLowerCase();

        if (rozhodnutie.equals("a")) {
            vybrana.setStav("Schválená");
            // Pridáme materiál na sklad
            vybrana.getMaterial().pridaj(vybrana.getMnozstvo());
            System.out.println("Objednávka schválená. Materiál pridaný na sklad:");
            System.out.println("  " + vybrana.getMaterial());
        } else {
            vybrana.setStav("Zamietnutá");
            System.out.println("Objednávka zamietnutá.");
        }
    }

    // ── Rozvozy ──────────────────────────────────────────────────────────────

    private static void schvalatRozvozy(Scanner sc) {

        System.out.println("\n--- Rozvozy čakajúce na schválenie ---");

        boolean najdeny = false;
        for (Rozvoz r : DataStore.cakajuceRozvozy) {
            if (r.getStav().equals("Čaká na schválenie")) {
                System.out.println(r);
                najdeny = true;
            }
        }

        if (!najdeny) {
            System.out.println("Žiadne rozvozy nečakajú na schválenie.");
            return;
        }

        System.out.print("\nZadaj ID rozvozu na spracovanie (0 = späť): ");
        int id;
        try {
            id = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Neplatné ID.");
            return;
        }
        if (id == 0) return;

        Rozvoz vybrana = null;
        for (Rozvoz r : DataStore.cakajuceRozvozy) {
            if (r.getId() == id && r.getStav().equals("Čaká na schválenie")) {
                vybrana = r;
                break;
            }
        }

        if (vybrana == null) {
            System.out.println("Rozvoz nenájdený alebo už spracovaný.");
            return;
        }

        System.out.println("Rozvoz: " + vybrana);
        System.out.print("Schváliť rozvoz? (a/n): ");
        String rozhodnutie = sc.nextLine().trim().toLowerCase();

        if (rozhodnutie.equals("a")) {
            vybrana.setStav("Naplánovaný");
            DataStore.rozvozy.add(vybrana);
            // Nastavíme stav zákaziek na "Pripravená na rozvoz"
            for (Zakazka z : vybrana.getZakazky()) {
                z.setStav("Pripravená na rozvoz");
            }
            System.out.println("Rozvoz schválený. Zákazky nastavené na stav 'Pripravená na rozvoz'.");
            System.out.println(vybrana);
        } else {
            vybrana.setStav("Zamietnutý");
            // Resetujeme stav zákaziek
            for (Zakazka z : vybrana.getZakazky()) {
                z.setStav("Vytvorená");
            }
            System.out.println("Rozvoz zamietnutý. Zákazky vrátené do stavu 'Vytvorená'.");
        }
    }
}