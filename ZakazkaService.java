import java.util.Scanner;

public class ZakazkaService {

    // UC01 – Prijímanie zákaziek
    public static void vytvorZakazku() {

        Scanner sc = new Scanner(System.in);

        System.out.println("\n--- Vytvorenie zákazky (UC01) ---");

        System.out.print("Zadaj názov produktu: ");
        String nazov = sc.nextLine().trim();
        if (nazov.isEmpty()) {
            System.out.println("Chyba: Názov produktu nesmie byť prázdny.");
            return;
        }

        System.out.print("Zadaj meno zákazníka: ");
        String zakaznik = sc.nextLine().trim();
        if (zakaznik.isEmpty()) {
            System.out.println("Chyba: Meno zákazníka nesmie byť prázdne.");
            return;
        }

        System.out.print("Zadaj cenu (EUR): ");
        double cena;
        try {
            cena = Double.parseDouble(sc.nextLine().trim());
            if (cena <= 0) {
                System.out.println("Chyba: Cena musí byť kladná.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatná cena.");
            return;
        }

        System.out.print("Zadaj termín doručenia (dd.mm.rrrr): ");
        String termin = sc.nextLine().trim();
        if (!termin.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{4}")) {
            System.out.println("Chyba: Neplatný formát dátumu (očakávaný: d.m.rrrr alebo dd.mm.rrrr).");
            return;
        }

        // Overenie dostupnosti materiálu
        if (DataStore.materialy.isEmpty()) {
            System.out.println("Chyba: Žiadny materiál nie je dostupný v systéme.");
            return;
        }

        System.out.println("\nDostupné materiály:");
        for (int i = 0; i < DataStore.materialy.size(); i++) {
            System.out.println(i + " - " + DataStore.materialy.get(i));
        }
        System.out.print("Vyber index materiálu: ");
        int matIndex;
        try {
            matIndex = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatný výber materiálu.");
            return;
        }
        if (matIndex < 0 || matIndex >= DataStore.materialy.size()) {
            System.out.println("Chyba: Neplatný index materiálu.");
            return;
        }

        Material vybranyMaterial = DataStore.materialy.get(matIndex);

        System.out.print("Zadaj požadované množstvo materiálu: ");
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

        // Overenie dostupnosti materiálu na sklade a odpísanie
        if (vybranyMaterial.getMnozstvo() < mnozstvo) {
            System.out.println("Upozornenie: Na sklade je len " + vybranyMaterial.getMnozstvo()
                    + " ks materiálu '" + vybranyMaterial.getNazov() + "'.");
            System.out.println("Zákazka bude vytvorená, ale materiál je nedostatočný – zvážte objednávku materiálu (UC03).");
        } else {
            vybranyMaterial.odober(mnozstvo);
            System.out.println("Materiál odpísaný zo skladu: -" + mnozstvo + " ks '" + vybranyMaterial.getNazov() + "'");
            System.out.println("Zostatok na sklade: " + vybranyMaterial.getMnozstvo() + " ks");
        }

        Zakazka z = new Zakazka(nazov, zakaznik, cena, termin, vybranyMaterial.getNazov(), mnozstvo);
        DataStore.zakazky.add(z);

        System.out.println("\nZákazka úspešne vytvorená (ID pridelené systémom):");
        System.out.println(z);
    }

    public static void vypisZakazky() {
        if (DataStore.zakazky.isEmpty()) {
            System.out.println("Žiadne zákazky.");
            return;
        }
        DataStore.zakazky.forEach(System.out::println);
    }
}