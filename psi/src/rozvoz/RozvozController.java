package rozvoz;

import ulozisko.DataStore;
import obchod.Zakazka;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller pre UC04 – Plánovanie rozvozu.
 */
public class RozvozController {

    private static final List<Zakazka.StavZakazky> DOSTUPNE_STAVY = List.of(
            Zakazka.StavZakazky.DOKONCENA
    );

    // ── Dotazy na dáta ────────────────────────────────────────────────────────

    /** Vráti zákazky, ktoré ešte nie sú priradené na rozvoz. */
    public List<Zakazka> getDostupneZakazky() {
        List<Zakazka> result = new ArrayList<>();

        for (Zakazka z : DataStore.getZakazky()) {
            if (z.getStav() == Zakazka.StavZakazky.DOKONCENA
                    && !z.isPouzitaVRozvoze()) {
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

    public List<Vozidlo> getVozidla() {
        return DataStore.getVozidla();
    }

    /** Vráti všetky schválené rozvozy. */
    public List<Rozvoz> getSchvaleneRozvozy() {
        return DataStore.rozvozy;
    }

    // ── Akcie ────────────────────────────────────────────────────────────────


    public VysledokRozvozu vytvorRozvoz(Vozidlo vozidlo,
                                        String datum,
                                        List<Integer> zakazkyIds) {

        if (vozidlo == null)
            return VysledokRozvozu.chyba("Vyber vozidlo.");

        int kapacita = vozidlo.getKapacita();

        if (datum == null || !datum.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{4}"))
            return VysledokRozvozu.chyba("Neplatný formát dátumu (d.m.rrrr).");

        if (jeVozidloObsadene(vozidlo, datum)) {
            return VysledokRozvozu.chyba(
                    "Vozidlo " + vozidlo + " už má naplánovaný rozvoz na dátum " + datum + "."
            );
        }

        if (zakazkyIds == null || zakazkyIds.isEmpty())
            return VysledokRozvozu.chyba("Vyber aspoň jednu zákazku.");

        if (zakazkyIds.size() > kapacita)
            return VysledokRozvozu.chyba("Počet zákaziek (" + zakazkyIds.size()
                    + ") prekračuje kapacitu vozidla (" + kapacita + ").");

        Rozvoz rozvoz = new Rozvoz(vozidlo, datum);

        List<Zakazka> dostupne = getDostupneZakazky();
        for (int id : zakazkyIds) {
            Zakazka najdena = dostupne.stream()
                    .filter(z -> z.getId() == id)
                    .findFirst().orElse(null);

            if (najdena == null)
                return VysledokRozvozu.chyba("Zákazka ID " + id + " nie je dostupná.");

            rozvoz.pridajZakazku(najdena);
            najdena.setPouzitaVRozvoze(true);
        }

        rozvoz.setStav("Čaká na schválenie");
        DataStore.cakajuceRozvozy.add(rozvoz);

        return VysledokRozvozu.uspech("Rozvoz #" + rozvoz.getId()
                + " odoslaný na schválenie manažérovi.");
    }

    public VysledokRozvozu schvalitRozvoz(int rozvozId) {
        Rozvoz r = najdiCakajuci(rozvozId);
        if (r == null) {
            return VysledokRozvozu.chyba("Rozvoz #" + rozvozId + " nenájdený.");
        }

        r.setStav("Naplánovaný");

        for (Zakazka z : r.getZakazky()) {
            z.setStav(Zakazka.StavZakazky.PRIPRAVENA_NA_ROZVOZ);
            z.setPouzitaVRozvoze(true);
        }

        DataStore.rozvozy.add(r);
        DataStore.cakajuceRozvozy.remove(r);

        return VysledokRozvozu.uspech("Rozvoz #" + rozvozId + " schválený. Zákazky sú pripravené na rozvoz.");
    }

    public VysledokRozvozu zamietnytRozvoz(int rozvozId) {
        Rozvoz r = najdiCakajuci(rozvozId);
        if (r == null) {
            return VysledokRozvozu.chyba("Rozvoz #" + rozvozId + " nenájdený.");
        }

        r.setStav("Zamietnutý");

        for (Zakazka z : r.getZakazky()) {
            z.setPouzitaVRozvoze(false);
            z.setStav(Zakazka.StavZakazky.DOKONCENA);
        }

        DataStore.cakajuceRozvozy.remove(r);

        return VysledokRozvozu.uspech("Rozvoz #" + rozvozId + " zamietnutý. Zákazky boli vrátené do dostupných.");
    }

    private boolean jeVozidloObsadene(Vozidlo vozidlo, String datum) {
        for (Rozvoz r : DataStore.cakajuceRozvozy) {
            if (r.getVozidlo().getId() == vozidlo.getId()
                    && r.getDatum().equals(datum)) {
                return true;
            }
        }

        for (Rozvoz r : DataStore.rozvozy) {
            if (r.getVozidlo().getId() == vozidlo.getId()
                    && r.getDatum().equals(datum)) {
                return true;
            }
        }

        return false;
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