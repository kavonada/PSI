package controller;

import model.DataStore;
import model.KosikPolozka;
import model.Material;
import model.ObjednavkaMaterialu;

import java.util.List;

/**
 * Controller pre UC03 – Objednanie materiálu.
 * Prepája View s modelom (DataStore).
 */
public class InventarController {

    private static final double LIMIT_SCHVALENIA = 1000.0;

    public static final String[] DODAVATELIA = {
        "WoodSupply s.r.o.",
        "DrevMateriál a.s.",
        "EuroDrev Ltd."
    };

    // Gettery
    public List<Material> getMaterialy() {
        return DataStore.materialy;
    }
    public double getLimitSchvalenia() {
        return LIMIT_SCHVALENIA;
    }
    public double vypocitajCenu(int mnozstvo, Material vybrany) {
        return mnozstvo * vybrany.getCena();
    }

    public VysledokObjednavky objednatKosik(int dodavatelIndex) {
        if (DataStore.kosik.isEmpty()) {
            return VysledokObjednavky.chyba("Košík je prázdny.");
        }

        String dodavatel = DODAVATELIA[dodavatelIndex];
        ObjednavkaMaterialu objednavka = new ObjednavkaMaterialu(DataStore.kosik, dodavatel);

        // pridaj na sklad - zatial hned pri objednani sa prida, neskor to zmenim
        for (KosikPolozka o : DataStore.kosik) {
            o.getMaterial().pridaj(o.getMnozstvo());
        }

        DataStore.objednavky.add(objednavka);
        DataStore.kosik.clear();

        return VysledokObjednavky.uspech(
                String.format(
                        "✔ Objednávka č.%d bola úspešne vytvorená so stavom %s\nDodávateľ: %s\nCelková cena: %.2f EUR",
                        objednavka.getId(),
                        objednavka.getStav(),
                        objednavka.getDodavatel(),
                        objednavka.getCelkovaCena()
                )
        );
    }

    public VysledokObjednavky pridatDoKosika(int materialIndex, int mnozstvo) {
        if (materialIndex < 0 || materialIndex >= DataStore.materialy.size()) {
            return VysledokObjednavky.chyba("Chyba: Neplatný výber materiálu.");
        }

        Material vybrany = DataStore.materialy.get(materialIndex);
        double cena = vypocitajCenu(mnozstvo, vybrany);

        KosikPolozka polozka = new KosikPolozka(vybrany, mnozstvo, cena);

        DataStore.kosik.add(polozka);

        return VysledokObjednavky.uspech("Položka pridaná do košíka.");
    }


    // Vnútorná trieda pre výsledok
    public static class VysledokObjednavky {

        public enum Typ { USPECH, SCHVALENIE, CHYBA }
        public final Typ typ;
        public final String sprava;

        private VysledokObjednavky(Typ typ, String sprava) {
            this.typ = typ;
            this.sprava = sprava;
        }

        public static VysledokObjednavky uspech(String s) {
            return new VysledokObjednavky(Typ.USPECH, s);
        }

        public static VysledokObjednavky chyba(String s) {
            return new VysledokObjednavky(Typ.CHYBA, s);
        }
    }

    public List<KosikPolozka> getKosik() {
        return DataStore.kosik;
    }
}
