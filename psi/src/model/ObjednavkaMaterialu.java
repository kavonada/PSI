package model;

import java.util.List;

public class ObjednavkaMaterialu {

    private static int counter = 1;

    private int id;
    private List<KosikPolozka> polozky;
    private String dodavatel;
    private double celkovaCena;
    private String stav;

    public ObjednavkaMaterialu(List<KosikPolozka> polozky, String dodavatel) {
        this.id = counter++;
        this.polozky = polozky;
        this.dodavatel = dodavatel;
        this.celkovaCena = vypocitajCenu();
        this.stav = setStav();
    }

    private double vypocitajCenu() {
        double sum = 0;
        for (KosikPolozka p : polozky) {
            sum += p.getCena();
        }
        return sum;
    }

    public int getId() { return id; }
    public List<KosikPolozka> getPolozky() { return polozky; }
    public String getDodavatel() { return dodavatel; }
    public double getCelkovaCena() { return celkovaCena; }
    public String getStav() { return stav; }
    public String setStav() {
        if (getCelkovaCena() >= 1000)
            return "'Čaká na schválenie'";
        return "'Vytvorená'";
    }
}