package model;

import java.util.ArrayList;
import java.util.List;


/* TOTO BUDE NAJLEPSIE KED VYMAZEME CELU TRIEDU - podla mna to nemozeme vymazat, niekam musime ukladat zakazky*/
public class DataStore {

    public static List<Zakazka> zakazky = new ArrayList<>();
    public static List<Rozvoz> rozvozy = new ArrayList<>();
    public static List<Rozvoz> cakajuceRozvozy = new ArrayList<>();

    public static final String MANAZER_HESLO = "manazer123";

    public static void pridajZakazku(Zakazka zakazka) {
        zakazky.add(zakazka);
    }

    public static List<Zakazka> getZakazky() {
        return zakazky;
    }
}
