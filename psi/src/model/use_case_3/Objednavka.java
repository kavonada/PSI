package model.use_case_3;

import java.util.List;

public class Objednavka {

    private static int counter = 1;

    private final int id;
    private final List<KosikPolozka> polozky;
    private final String dodavatel;
    private final double celkovaSuma;
    private final String stav;

    public Objednavka(List<KosikPolozka> polozky, String dodavatel) {
        this.id = counter++;
        this.polozky = polozky;
        this.dodavatel = dodavatel;
        this.celkovaSuma = vypocitajSumu();
        this.stav = setStav();
    }

    private double vypocitajSumu() {
        double sum = 0;
        for (KosikPolozka p : getOrderItems()) {
            sum += p.getCena();
        }
        return sum;
    }

    public int getId() { return id; }
    public List<KosikPolozka> getOrderItems() { return polozky; }
    public String getDodavatel() { return dodavatel; }
    public double getCelkovaSuma() { return celkovaSuma; }
    public String getStav() { return stav; }
    public String setStav() {
        if (getCelkovaSuma() >= 1000)
            return "'Čaká na schválenie'";
        return "'Vytvorená'";
    }
}