package model;

import model.use_case_2.Pracovnik;
import model.use_case_2.Stroj;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/* TOTO BUDE NAJLEPSIE KED VYMAZEME CELU TRIEDU - podla mna to nemozeme vymazat, niekam musime ukladat zakazky*/
public class DataStore {

    public static List<Zakazka> zakazky = new ArrayList<>();
    public static List<Rozvoz> rozvozy = new ArrayList<>();
    public static List<Rozvoz> cakajuceRozvozy = new ArrayList<>();
    public static List<Pracovnik> pracovnici = new ArrayList<>();
    public static List<Stroj> stroje = new ArrayList<>();

    public static final String MANAZER_HESLO = "manazer123";

    public static void pridajZakazku(Zakazka zakazka) {
        zakazky.add(zakazka);
    }

    public static List<Zakazka> getZakazky() {
        return zakazky;
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
    }
}
