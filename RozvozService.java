import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RozvozService {

    // UC04 – Naplánovať rozvoz
    public static void planovatRozvoz() {

        System.out.println("\n--- Plánovanie rozvozu (UC04) ---");

        List<Zakazka> dostupne = new ArrayList<>();
        for (Zakazka z : DataStore.zakazky) {
            String stav = z.getStav();
            if (stav.equals("Naplánovaná") || stav.equals("Vytvorená") || stav.equals("Čiastočne naplánovaná")) {
                dostupne.add(z);
            }
        }

        if (dostupne.isEmpty()) {
            System.out.println("Žiadne zákazky nie sú dostupné na rozvoz.");
            return;
        }

        System.out.println("Dostupné zákazky:");
        dostupne.forEach(System.out::println);

        Scanner sc = new Scanner(System.in);

        System.out.print("\nZadaj dátum rozvozu (dd.mm.rrrr): ");
        String datum = sc.nextLine().trim();
        if (!datum.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{4}")) {
            System.out.println("Chyba: Neplatný formát dátumu.");
            return;
        }

        System.out.print("Zadaj vozidlo: ");
        String vozidlo = sc.nextLine().trim();
        if (vozidlo.isEmpty()) {
            System.out.println("Chyba: Vozidlo nesmie byť prázdne.");
            return;
        }

        System.out.print("Zadaj kapacitu vozidla (max počet zákaziek): ");
        int kapacita;
        try {
            kapacita = Integer.parseInt(sc.nextLine().trim());
            if (kapacita <= 0) {
                System.out.println("Chyba: Kapacita musí byť kladná.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Chyba: Neplatná kapacita.");
            return;
        }

        Rozvoz rozvoz = new Rozvoz(vozidlo, kapacita, datum);

        System.out.println("\nNavrhované poradie zastávok (podľa dostupnosti, max " + kapacita + "):");
        List<Zakazka> navrh = new ArrayList<>();
        for (int i = 0; i < Math.min(kapacita, dostupne.size()); i++) {
            navrh.add(dostupne.get(i));
            System.out.println("  " + (i + 1) + ". " + dostupne.get(i));
        }

        System.out.print("\nChceš upraviť poradie zastávok? (a/n): ");
        String uprava = sc.nextLine().trim().toLowerCase();

        List<Zakazka> finalneZakazky = new ArrayList<>(navrh);

        if (uprava.equals("a")) {
            finalneZakazky.clear();
            System.out.println("Zadávaj ID zákaziek v požadovanom poradí (0 = koniec):");
            while (true) {
                System.out.print("  ID zákazky: ");
                int zid;
                try {
                    zid = Integer.parseInt(sc.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("Neplatné ID, preskočené.");
                    continue;
                }
                if (zid == 0) break;
                boolean najdena = false;
                for (Zakazka z : navrh) {
                    if (z.getId() == zid) {
                        finalneZakazky.add(z);
                        najdena = true;
                        break;
                    }
                }
                if (!najdena) System.out.println("Zákazka ID " + zid + " nie je v navrhovanom zozname.");
                if (finalneZakazky.size() >= kapacita) {
                    System.out.println("Kapacita vozidla dosiahnutá.");
                    break;
                }
            }
        }

        boolean kapacitaPrekrocena = false;
        for (Zakazka z : finalneZakazky) {
            if (!rozvoz.pridajZakazku(z)) {
                kapacitaPrekrocena = true;
                System.out.println("Upozornenie: Zákazka ID " + z.getId() + " sa nezmestila – kapacita vozidla je plná.");
            } else {
                z.setStav("Čaká na schválenie rozvozu");
            }
        }

        if (kapacitaPrekrocena) {
            System.out.println("Niektoré zákazky neboli zahrnuté kvôli kapacite vozidla.");
        }

        rozvoz.setStav("Čaká na schválenie");
        DataStore.cakajuceRozvozy.add(rozvoz);
        System.out.println("\nPlán rozvozu odoslaný na schválenie manažérovi (možnosť 6).");
        System.out.println(rozvoz);
    }
}