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

    private static final List<Objednavka> orders = new ArrayList<>();
    private static final List<Material> materials = new ArrayList<>();
    private static final List<KosikPolozka> cart = new ArrayList<>();
    private static final double LIMIT_ORDER = 1000.0;
    private static final List<Dodavatel> suppliers = new ArrayList<>();

    // Deklaracia vsetkych materialov
    static {
        materials.add(new Material("Masív dub",  50, 15.00, 20));
        materials.add(new Material("DTD doska",  15, 10.00, 20));
        materials.add(new Material("MDF doska",   5, 12.00, 20));

        suppliers.add(new Dodavatel("WoodSupply s.r.o.", 2, 3.99));
        suppliers.add(new Dodavatel("DrevMateriál a.s.", 3, 2.99));
        suppliers.add(new Dodavatel("EuroDrev Ltd.", 5, 0.99));
    }

    public VysledokObjednavky objednatKosik(int supplierIndex) {
        if (getCart().isEmpty()) {
            return VysledokObjednavky.chyba("Košík je prázdny.");
        }

        String supplier = getSupplier(supplierIndex);
        Objednavka order = new Objednavka(getCart(), supplier);

        // prida sa na sklad hned pri objednani ZATIAL, neskor to zmenim
        for (KosikPolozka o : getCart()) {
            o.getMaterial().pridaj(o.getQuantity());
        }

        getOrders().add(order);
        getCart().clear();

        return VysledokObjednavky.uspech(
                String.format(
                        "✔ Objednávka č.%d bola úspešne vytvorená so stavom %s\nDodávateľ: %s\nCelková cena: %.2f EUR",
                        order.getId(),
                        order.getStatus(),
                        order.getSupplier(),
                        order.getCost()
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

        getCart().add(polozka);

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
    public List<KosikPolozka> getCart() { return cart; }
    public static List<Objednavka> getOrders() { return orders; }
    public double getLimitOrder() { return LIMIT_ORDER; }
    public static List<Dodavatel> getSuppliers() { return suppliers; }
    public static String getSupplier(int index) { return suppliers.get(index).getName(); }
}
