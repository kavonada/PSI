import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n=== SYSTÉM ===");
            System.out.println("1 - Vytvoriť zákazku       (UC01)");
            System.out.println("2 - Plánovať výrobu        (UC02)");
            System.out.println("3 - Objednať materiál      (UC03)");
            System.out.println("4 - Plánovať rozvoz        (UC04)");
            System.out.println("5 - Vypísať zákazky");
            System.out.println("6 - Manažér (chránené heslom)");
            System.out.println("0 - Koniec");

            int volba;
            try {
                volba = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Neplatná voľba.");
                continue;
            }

            switch (volba) {
                case 1 -> ZakazkaService.vytvorZakazku();
                case 2 -> VyrobaService.planovatVyrobu();
                case 3 -> InventarService.objednatMaterial();
                case 4 -> RozvozService.planovatRozvoz();
                case 5 -> ZakazkaService.vypisZakazky();
                case 6 -> ManazerService.manazerMenu();
                case 0 -> System.exit(0);
                default -> System.out.println("Neplatná voľba.");
            }
        }
    }
}
