package model;

import java.util.ArrayList;
import java.util.List;

public class Rozvoz {

    private static int counter = 1;

    private int           id;
    private String        vozidlo;
    private int           kapacita;
    private String        datum;
    private List<Zakazka> zakazky;
    private String        stav;

    public Rozvoz(String vozidlo, int kapacita, String datum) {
        this.id       = counter++;
        this.vozidlo  = vozidlo;
        this.kapacita = kapacita;
        this.datum    = datum;
        this.zakazky  = new ArrayList<>();
        this.stav     = "Vytvorený";
    }

    public boolean pridajZakazku(Zakazka z) {
        if (zakazky.size() < kapacita) { zakazky.add(z); return true; }
        return false;
    }

    public int getVolnaKapacita() { return kapacita - zakazky.size(); }

    public int           getId()      { return id; }
    public List<Zakazka> getZakazky() { return zakazky; }
    public void          setStav(String stav) { this.stav = stav; }
    public String        getStav()    { return stav; }

    @Override
    public String toString() {
        return "Rozvoz ID: " + id + " | Vozidlo: " + vozidlo +
               " | Kapacita: " + kapacita + " | Dátum: " + datum +
               " | Počet zákaziek: " + zakazky.size() + " | Stav: " + stav;
    }
}
