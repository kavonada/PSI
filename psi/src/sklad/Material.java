package sklad;

public class Material {

    private String nazov;
    private int mnozstvo;
    private String dodavatel;
    private double cena;
    private int limit;
    private enum stav {OK, POD_LIMIT};

    public Material(String nazov, int mnozstvo, double cena, int limit) {
        this.nazov = nazov;
        this.mnozstvo = mnozstvo;
        this.dodavatel = "Neznámy";
        this.cena = cena;
        this.limit = limit;
    }

    public String getNazov() { return nazov; }
    public int getMnozstvo() { return mnozstvo; }
    public int getLimit() { return limit; }

    public String getStav() {
        if (getMnozstvo() < getLimit()) {
            return stav.POD_LIMIT.toString();
        }
        else {
            return stav.OK.toString();
        }
    }
    public double getCena() { return cena; }

    public void zmenitMnozstvo(int ks) {
        mnozstvo += ks;
    }

    public boolean overDostupnost(int pozadovaneKs) {
        return mnozstvo >= pozadovaneKs;
    }

    @Override
    public String toString() { return nazov + " | Množstvo: " + mnozstvo + " ks | Dodávateľ: " + dodavatel; }
}
