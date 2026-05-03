package sklad;

public class Dodavatel {

    private final String nazov;
    private final int dniDodania;
    private final double cena;

    public Dodavatel(String nazov, int dniDodania, double cena) {
        this.nazov = nazov;
        this.dniDodania = dniDodania;
        this.cena = cena;
    }

    public String getNazov() { return nazov; }
    public int getDniDodania() { return dniDodania; }
    public double getCena() { return cena; }
}
