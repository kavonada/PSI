package model.use_case_3;

public class KosikPolozka {
    private final Material material;
    private final int mnozstvo;
    private final double cena;

    public KosikPolozka(Material material, int mnozstvo, double cena) {
        this.material = material;
        this.mnozstvo = mnozstvo;
        this.cena = cena;
    }

    public Material getMaterial() { return material; }
    public int getMnozstvo() { return mnozstvo; }
    public double getCena() { return cena; }
}
