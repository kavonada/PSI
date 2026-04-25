package controller;

import model.use_case_3.Dodavatel;
import model.use_case_3.KosikPolozka;
import model.use_case_3.Material;
import model.use_case_3.Objednavka;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller pre UC03 – Objednanie materiálu.
 */
public class InventarController {

    private static final List<Objednavka> objednavky = new ArrayList<>();
    private static final List<Material> materials = new ArrayList<>();
    private static final List<KosikPolozka> kosik = new ArrayList<>();
    private static final double LIMIT_OBJEDNAVKY = 1000.0;
    private static final List<Dodavatel> dodavatelia = new ArrayList<>();

    // Deklaracia vsetkych materialov
    static {
        materials.add(new Material("Masív dub",  50, 15.00, 20));
        materials.add(new Material("DTD doska",  15, 10.00, 20));
        materials.add(new Material("MDF doska",   5, 12.00, 20));

        dodavatelia.add(new Dodavatel("WoodSupply s.r.o.", 2, 3.99));
        dodavatelia.add(new Dodavatel("DrevMateriál a.s.", 3, 2.99));
        dodavatelia.add(new Dodavatel("EuroDrev Ltd.", 5, 0.99));
    }

    public VysledokObjednavky objednatKosik(int supplierIndex) {
        if (getKosik().isEmpty()) {
            return VysledokObjednavky.chyba("Košík je prázdny.");
        }

        String supplier = getDodavatel(supplierIndex);
        Objednavka order = new Objednavka(getKosik(), supplier);

        // prida sa na sklad hned pri objednani ZATIAL, neskor to zmenim
        for (KosikPolozka o : getKosik()) {
            o.getMaterial().zmenitMnozstvo(o.getMnozstvo());
        }

        getObjednavky().add(order);
        getKosik().clear();

        return VysledokObjednavky.uspech(
                String.format(
                        "✔ Objednávka č.%d bola úspešne vytvorená so stavom %s\nDodávateľ: %s\nCelková cena: %.2f EUR",
                        order.getId(),
                        order.getStav(),
                        order.getDodavatel(),
                        order.getCelkovaSuma()
                )
        );
    }

    public VysledokObjednavky pridatDoKosika(int materialIndex, int mnozstvo) {
        if (materialIndex < 0 || materialIndex >= getMaterials().size()) {
            return VysledokObjednavky.chyba("Chyba: Neplatný výber materiálu.");
        }

        Material vybrany = getMaterials().get(materialIndex);
        double cena = vypocitajCenu(mnozstvo, vybrany);

        KosikPolozka polozka = new KosikPolozka(vybrany, mnozstvo, cena);

        getKosik().add(polozka);

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

        public static VysledokObjednavky uspech(String s) { return new VysledokObjednavky(Typ.USPECH, s); }
        public static VysledokObjednavky chyba(String s) { return new VysledokObjednavky(Typ.CHYBA, s); }
    }


    public double vypocitajCenu(int mnozstvo, Material material) { return mnozstvo * material.getCena(); }
    // Gettery
    public static List<Material> getMaterials() { return materials; }
    public List<KosikPolozka> getKosik() { return kosik; }
    public static List<Objednavka> getObjednavky() { return objednavky; }
    public double getLimitObjednavky() { return LIMIT_OBJEDNAVKY; }
    public static List<Dodavatel> getDodavatelia() { return dodavatelia; }
    public static String getDodavatel(int index) { return dodavatelia.get(index).getNazov(); }
}
