public class Material {

    private String nazov;
    private int mnozstvo;
    private String dodavatel;

    public Material(String nazov, int mnozstvo) {
        this.nazov = nazov;
        this.mnozstvo = mnozstvo;
        this.dodavatel = "Neznámy";
    }

    public Material(String nazov, int mnozstvo, String dodavatel) {
        this.nazov = nazov;
        this.mnozstvo = mnozstvo;
        this.dodavatel = dodavatel;
    }

    public String getNazov() { return nazov; }
    public int getMnozstvo() { return mnozstvo; }
    public String getDodavatel() { return dodavatel; }

    public void pridaj(int ks) {
        mnozstvo += ks;
    }

    public boolean odober(int ks) {
        if (mnozstvo >= ks) {
            mnozstvo -= ks;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return nazov + " | Množstvo: " + mnozstvo + " | Dodávateľ: " + dodavatel;
    }
}