package controller;

import model.DataStore;
import model.Rozvoz;
import model.Zakazka;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller pre UC04 – Plánovanie rozvozu.
 */
public class RozvozController {

    /** Stavy zákaziek, ktoré sú považované za dostupné na rozvoz. */
    private static final List<String> DOSTUPNE_STAVY = List.of(
            "VYTVORENA", "NAPLANOVANA", "CIASTOCNE_NAPLANOVANA", "VYROBENA"
    );

    // ── Dotazy na dáta ────────────────────────────────────────────────────────

    /** Vráti zákazky, ktoré ešte nie sú priradené na rozvoz. */
    public List<Zakazka> getDostupneZakazky() {
        List<Zakazka> result = new ArrayList<>();
        for (Zakazka z : DataStore.getZakazky()) {
            String stav = z.getStav().name();
            if (DOSTUPNE_STAVY.contains(stav)) {
                result.add(z);
            }
        }
        return result;
    }

    /** Vráti rozvozy čakajúce na schválenie. */
    public List<Rozvoz> getCakajuceRozvozy() {
        List<Rozvoz> result = new ArrayList<>();
        for (Rozvoz r : DataStore.cakajuceRozvozy) {
            if ("Čaká na schválenie".equals(r.getStav())) {
                result.add(r);
            }
        }
        return result;
    }

    /** Vráti všetky schválené rozvozy. */
    public List<Rozvoz> getSchvaleneRozvozy() {
        return DataStore.rozvozy;
    }

    // ── Akcie ────────────────────────────────────────────────────────────────

    /**
     * Vytvorí nový rozvoz a odošle ho na schválenie.
     *
     * @param vozidlo    identifikácia vozidla
     * @param kapacita   max. počet zákaziek
     * @param datum      dátum rozvozu (dd.mm.rrrr)
     * @param zakazkyIds ID zákaziek v požadovanom poradí
     * @return správa o výsledku
     */
    public VysledokRozvozu vytvorRozvoz(String vozidlo, int kapacita,
                                        String datum, List<Integer> zakazkyIds) {
        if (vozidlo == null || vozidlo.isBlank())
            return VysledokRozvozu.chyba("Zadaj vozidlo.");
        if (kapacita <= 0)
            return VysledokRozvozu.chyba("Kapacita musí byť kladná.");
        if (datum == null || !datum.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{4}"))
            return VysledokRozvozu.chyba("Neplatný formát dátumu (d.m.rrrr).");
        if (zakazkyIds == null || zakazkyIds.isEmpty())
            return VysledokRozvozu.chyba("Vyber aspoň jednu zákazku.");
        if (zakazkyIds.size() > kapacita)
            return VysledokRozvozu.chyba("Počet zákaziek (" + zakazkyIds.size()
                    + ") prekračuje kapacitu vozidla (" + kapacita + ").");

        Rozvoz rozvoz = new Rozvoz(vozidlo, kapacita, datum);

        List<Zakazka> dostupne = getDostupneZakazky();
        for (int id : zakazkyIds) {
            Zakazka najdena = dostupne.stream()
                    .filter(z -> z.getId() == id)
                    .findFirst().orElse(null);
            if (najdena == null)
                return VysledokRozvozu.chyba("Zákazka ID " + id + " nie je dostupná.");
            rozvoz.pridajZakazku(najdena);
            najdena.setStav(Zakazka.StavZakazky.NAPLANOVANA); // označíme ako priradenú
        }

        rozvoz.setStav("Čaká na schválenie");
        DataStore.cakajuceRozvozy.add(rozvoz);

        return VysledokRozvozu.uspech("Rozvoz #" + rozvoz.getId()
                + " odoslaný na schválenie manažérovi.");
    }

    /**
     * Manažér schváli rozvoz → zákazky → stav VYROBENA, rozvoz → Naplánovaný.
     */
    public VysledokRozvozu schvalitRozvoz(int rozvozId) {
        Rozvoz r = najdiCakajuci(rozvozId);
        if (r == null) return VysledokRozvozu.chyba("Rozvoz #" + rozvozId + " nenájdený.");

        r.setStav("Naplánovaný");
        for (Zakazka z : r.getZakazky()) {
            z.setStav(Zakazka.StavZakazky.DOKONCENA);
        }
        DataStore.rozvozy.add(r);
        DataStore.cakajuceRozvozy.remove(r);

        return VysledokRozvozu.uspech("Rozvoz #" + rozvozId + " schválený. Zákazky nastavené na 'Pripravená na rozvoz'.");
    }

    /**
     * Manažér zamietne rozvoz → zákazky vrátené na VYTVORENA.
     */
    public VysledokRozvozu zamietnytRozvoz(int rozvozId) {
        Rozvoz r = najdiCakajuci(rozvozId);
        if (r == null) return VysledokRozvozu.chyba("Rozvoz #" + rozvozId + " nenájdený.");

        r.setStav("Zamietnutý");
        for (Zakazka z : r.getZakazky()) {
            z.setStav(Zakazka.StavZakazky.VYTVORENA);
        }
        DataStore.cakajuceRozvozy.remove(r);

        return VysledokRozvozu.uspech("Rozvoz #" + rozvozId + " zamietnutý. Zákazky vrátené.");
    }

    // ── Pomocné ───────────────────────────────────────────────────────────────

    private Rozvoz najdiCakajuci(int id) {
        return DataStore.cakajuceRozvozy.stream()
                .filter(r -> r.getId() == id)
                .findFirst().orElse(null);
    }

    // ── Výsledok ─────────────────────────────────────────────────────────────

    public static class VysledokRozvozu {
        public enum Typ { USPECH, CHYBA }
        public final Typ typ;
        public final String sprava;

        private VysledokRozvozu(Typ typ, String sprava) {
            this.typ = typ;
            this.sprava = sprava;
        }

        public static VysledokRozvozu uspech(String s) { return new VysledokRozvozu(Typ.USPECH, s); }
        public static VysledokRozvozu chyba(String s)  { return new VysledokRozvozu(Typ.CHYBA, s); }
    }
}