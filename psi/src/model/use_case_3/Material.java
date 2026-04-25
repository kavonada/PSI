package model.use_case_3;

public class Material {

    private String nazov;
    private int mnozstvo;
    private int rezervovane;
    private String dodavatel;
    private double cena;
    private int limit;
    private enum stav {OK, POD_LIMIT};

    public Material(String nazov, int mnozstvo, double cena, int limit) {
        this.nazov = nazov;
        this.mnozstvo = mnozstvo;
        this.rezervovane = 0;
        this.dodavatel = "Neznámy";
        this.cena = cena;
        this.limit = limit;
    }

    public String getNazov() { return nazov; }
    public int getMnozstvo() { return mnozstvo; }
    public int getLimit() { return limit; }
    public int getDostupneMnozstvo() { return mnozstvo - rezervovane; }

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

    public boolean odober(int ks) {
        if (this.mnozstvo >= ks) {
            this.mnozstvo -= ks;
            return true;
        }
        return false;
    }

    public boolean rezervuj(int ks) {
        if (getDostupneMnozstvo() >= ks) {
            this.rezervovane += ks;
            return true;
        }
        return false;
    }

    public void zrusRezervaciu(int ks) {
        if (this.rezervovane >= ks) {
            this.rezervovane -= ks;
        }
    }

    // Zavolá sa, keď zákazka/úloha bude reálne vo výrobe a materiál sa fyzicky minie
    public void spotrebujRezervovane(int ks) {
        if (this.rezervovane >= ks && this.mnozstvo >= ks) {
            this.rezervovane -= ks;
            this.mnozstvo -= ks;
        }
    }

    @Override
    public String toString() { return nazov + " | Množstvo: " + mnozstvo + " ks | Dodávateľ: " + dodavatel; }
}
