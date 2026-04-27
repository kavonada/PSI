package controller;

import model.use_case_3.Dodavatel;
import model.use_case_3.KosikPolozka;
import model.use_case_3.Material;
import model.use_case_3.Objednavka;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller pre UC03 – Inventár.
 */
public class InventarController {

    private static final List<Objednavka> objednavky = new ArrayList<>();
    private static final List<Material> materials = new ArrayList<>();
    private static final List<KosikPolozka> kosik = new ArrayList<>();
    private static final List<Dodavatel> dodavatelia = new ArrayList<>();
    private static final double LIMIT_OBJEDNAVKY = 1000.0;

    static {
        materials.add(new Material("Masív dub", 50, 15.00, 20));
        materials.add(new Material("DTD doska", 15, 10.00, 20));
        materials.add(new Material("MDF doska", 5, 12.00, 20));

        dodavatelia.add(new Dodavatel("WoodSupply s.r.o.", 2, 3.99));
        dodavatelia.add(new Dodavatel("DrevMateriál a.s.", 3, 2.99));
        dodavatelia.add(new Dodavatel("EuroDrev Ltd.", 5, 0.99));
    }

    public VysledokObjednavky pridatDoKosika(int materialIndex, int mnozstvo) {
        if (materialIndex < 0 || materialIndex >= materials.size()) {
            return VysledokObjednavky.chyba("Chyba: Neplatný výber materiálu.");
        }

        Material vybrany = materials.get(materialIndex);
        double cena = vypocitajCenu(mnozstvo, vybrany);
        kosik.add(new KosikPolozka(vybrany, mnozstvo, cena));

        return VysledokObjednavky.uspech("Položka pridaná do košíka.");
    }

    public VysledokObjednavky objednatKosik(int supplierIndex) {
        if (kosik.isEmpty()) {
            return VysledokObjednavky.chyba("Košík je prázdny.");
        }

        String supplier = getDodavatel(supplierIndex);
        Objednavka order = new Objednavka(new ArrayList<>(kosik), supplier);

        objednavky.add(order);
        kosik.clear();

        return VysledokObjednavky.uspech(String.format(
                "✔ Objednávka č.%d bola úspešne vytvorená so stavom '%s'\n" +
                        "Dodávateľ: %s\n" +
                        "Celková cena: %.2f EUR",
                order.getId(), order.getStav(), order.getDodavatel(), order.getCelkovaSuma()
        ));
    }

    public boolean vybalitNaSklad(Objednavka objednavka) {
        if (!"Dorucena".equals(objednavka.getStav())) return false;

        for (KosikPolozka p : objednavka.getOrderItems()) {
            p.getMaterial().zmenitMnozstvo(p.getMnozstvo());
        }
        objednavka.vybalit();
        return true;
    }

    public double vypocitajCenu(int mnozstvo, Material material) {
        return mnozstvo * material.getCena();
    }

    // Gettery
    public static List<Material> getMaterials() { return materials; }
    public List<KosikPolozka> getKosik() { return kosik; }
    public static List<Objednavka> getObjednavky() { return objednavky; }
    public double getLimitObjednavky() { return LIMIT_OBJEDNAVKY; }
    public static List<Dodavatel> getDodavatelia() { return dodavatelia; }
    public static String getDodavatel(int index) { return dodavatelia.get(index).getNazov(); }

    // VysledokObjednavky
    public static class VysledokObjednavky {
        public enum Typ {USPECH, CHYBA}
        public final Typ typ;
        public final String sprava;

        private VysledokObjednavky(Typ typ, String sprava) {
            this.typ = typ;
            this.sprava = sprava;
        }

        public static VysledokObjednavky uspech(String s) { return new VysledokObjednavky(Typ.USPECH, s); }
        public static VysledokObjednavky chyba (String s) { return new VysledokObjednavky(Typ.CHYBA, s); }
    }
}
