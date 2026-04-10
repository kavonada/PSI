package model;

public class KosikPolozka {
    private Material material;
    private int mnozstvo;

    public KosikPolozka(Material material, int mnozstvo, double cena) {
        this.material = material;
        this.mnozstvo = mnozstvo;
    }

    public Material getMaterial() { return material; }
    public int getMnozstvo() { return mnozstvo; }
    public double getCena() {
        return material.getCena() * mnozstvo;
    }
}
