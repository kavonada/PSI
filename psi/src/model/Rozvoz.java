package model;

import java.util.ArrayList;
import java.util.List;

public class Rozvoz {

    private static int counter = 1;

    private int id;
    private Vozidlo vozidlo;
    private String datum;
    private List<Zakazka> zakazky;
    private String stav;

    public Rozvoz(Vozidlo vozidlo, String datum) {
        this.id = counter++;
        this.vozidlo = vozidlo;
        this.datum = datum;
        this.zakazky = new ArrayList<>();
        this.stav = "Vytvorený";
    }

    public boolean pridajZakazku(Zakazka z) {
        if (zakazky.size() < vozidlo.getKapacita()) {
            zakazky.add(z);
            return true;
        }
        return false;
    }

    public int getVolnaKapacita() {
        return vozidlo.getKapacita() - zakazky.size();
    }

    public int getId() {
        return id;
    }

    public Vozidlo getVozidlo() {
        return vozidlo;
    }

    public String getDatum() {
        return datum;
    }

    public int getKapacita() {
        return vozidlo.getKapacita();
    }

    public List<Zakazka> getZakazky() {
        return zakazky;
    }

    public void setStav(String stav) {
        this.stav = stav;
    }

    public String getStav() {
        return stav;
    }

    @Override
    public String toString() {
        return "Rozvoz ID: " + id +
                " | Vozidlo: " + vozidlo +
                " | Kapacita: " + vozidlo.getKapacita() +
                " | Dátum: " + datum +
                " | Počet zákaziek: " + zakazky.size() +
                " | Stav: " + stav;
    }
}