package model;

import model.use_case_2.Pracovnik;
import model.use_case_2.Stroj;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class DataStore {

    public static List<Zakazka> zakazky = new ArrayList<>();
    public static List<Rozvoz> rozvozy = new ArrayList<>();
    public static List<Rozvoz> cakajuceRozvozy = new ArrayList<>();
    public static List<Pracovnik> pracovnici = new ArrayList<>();
    public static List<Stroj> stroje = new ArrayList<>();
    public static List<PoziadavkaNaMaterial> poziadavkyNaMaterial = new ArrayList<>();
    public static List<Vozidlo> vozidla = new ArrayList<>();

    public static final String MANAZER_HESLO = "manazer123";

    public static void pridajZakazku(Zakazka zakazka) {
        zakazky.add(zakazka);

        if (view.ZakazkyPanel.instance != null) {
            view.ZakazkyPanel.instance.refresh();
        }
        if (view.VyrobaPanel.instance != null) {
            view.VyrobaPanel.instance.refreshZakazky();
        }
    }

    public static List<Zakazka> getZakazky() {
        return zakazky;
    }

    public static List<Vozidlo> getVozidla() {
        return vozidla;
    }

    static {
        //Dummy data na testovanie UC2
        zakazky.add(new Zakazka("Stôl", "Drevený jedálenský stôl",
                new Zakaznik("Ján Novák", "jan@mail.sk", "123"),
                new Adresa("Hlavná", "1", "BA", "81101"), 200, LocalDate.now().plusDays(10), new ArrayList<>()));

        zakazky.add(new Zakazka("Skrinka", "Kuchynská skrinka",
                new Zakaznik("Peter Mráz", "peter@mail.sk", "456"),
                new Adresa("Vedľajšia", "5", "KE", "04001"), 150, LocalDate.now().plusDays(7), new ArrayList<>()));

        // Pracovníci
        pracovnici.add(new Pracovnik("Jozef (Interný)", true, false));
        pracovnici.add(new Pracovnik("Martin (Interný)", false, false)); // Nedostupný na test blokovania
        pracovnici.add(new Pracovnik("Firma DrevStav (Externý)", true, true)); // Extend: Externý zdroj

        // Stroje
        stroje.add(new Stroj("Formátovacia Píla", true));
        stroje.add(new Stroj("CNC Fréza", false)); // Nedostupný na test blokovania
        stroje.add(new Stroj("Externá Lakovňa", true)); // Extend: Externý stroj

        // Vozidlá
        vozidla.add(new Vozidlo("Dodávka Iveco Daily", "BA-123AB", 5));
        vozidla.add(new Vozidlo("Mercedes Sprinter", "BA-456CD", 8));
        vozidla.add(new Vozidlo("Ford Transit", "TT-789EF", 6));
        vozidla.add(new Vozidlo("Renault Master", "NR-111GH", 7));
        vozidla.add(new Vozidlo("Volkswagen Crafter", "ZA-222IJ", 9));
        vozidla.add(new Vozidlo("Fiat Ducato", "KE-333KL", 4));
        vozidla.add(new Vozidlo("MAN TGE", "PO-444MN", 10));

        // Testovacie zákazky pre UC04 - rozvoz
        Zakazka rozvozZakazka1 = new Zakazka("Komoda", "Hotová dubová komoda",
                new Zakaznik("Lucia Bieliková", "lucia@mail.sk", "0901"),
                new Adresa("Kvetná", "12", "Bratislava", "82101"),
                320, LocalDate.now().plusDays(3), new ArrayList<>());

        rozvozZakazka1.setStav(Zakazka.StavZakazky.DOKONCENA);
        zakazky.add(rozvozZakazka1);

        Zakazka rozvozZakazka2 = new Zakazka("Polica", "Hotová nástenná polica",
                new Zakaznik("Marek Horváth", "marek@mail.sk", "0902"),
                new Adresa("Lesná", "8", "Trnava", "91701"),
                90, LocalDate.now().plusDays(4), new ArrayList<>());

        rozvozZakazka2.setStav(Zakazka.StavZakazky.DOKONCENA);
        zakazky.add(rozvozZakazka2);
    }
}
