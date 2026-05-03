package sklad;

import java.util.List;

public class Objednavka {

    private static int counter = 1;

    private final int id;
    private final List<KosikPolozka> polozky;
    private final String dodavatel;
    private final double celkovaSuma;
    private StavObjednavky stav;

    public Objednavka(List<KosikPolozka> polozky, String dodavatel) {
        this.id = counter++;
        this.polozky = List.copyOf(polozky);
        this.dodavatel = dodavatel;
        this.celkovaSuma = vypocitajSumu();
        this.stav = pociatocnyStav();
    }

    private double vypocitajSumu() {
        double sum = 0;
        for (KosikPolozka p : getOrderItems()) {
            sum += p.getCena();
        }
        return sum;
    }

    private StavObjednavky pociatocnyStav() {
        return (celkovaSuma >= 1000)
                ? StavObjednavky.CAKA_NA_SCHVALENIE
                : StavObjednavky.VYTVORENA;
    }

    public void dorucit()  { this.stav = StavObjednavky.DORUCENA; }
    public void vybalit()  { this.stav = StavObjednavky.VYBAVENA; }

    public StavObjednavky getStav() { return stav; }
    public void setStav(StavObjednavky stav) { this.stav = stav; }
    public int getId() { return id; }
    public List<KosikPolozka> getOrderItems() { return polozky; }
    public String getDodavatel() { return dodavatel; }
    public double getCelkovaSuma() { return celkovaSuma; }
}
