public class ObjednavkaMaterialu {

    private static int counter = 1;

    private int id;
    private Material material;
    private int mnozstvo;
    private String dodavatel;
    private double celkovaCena;
    private String stav;

    public ObjednavkaMaterialu(Material material, int mnozstvo, String dodavatel, double celkovaCena) {
        this.id = counter++;
        this.material = material;
        this.mnozstvo = mnozstvo;
        this.dodavatel = dodavatel;
        this.celkovaCena = celkovaCena;
        this.stav = "Čaká na schválenie";
    }

    public int getId() { return id; }
    public Material getMaterial() { return material; }
    public int getMnozstvo() { return mnozstvo; }
    public String getDodavatel() { return dodavatel; }
    public double getCelkovaCena() { return celkovaCena; }
    public String getStav() { return stav; }
    public void setStav(String stav) { this.stav = stav; }

    @Override
    public String toString() {
        return "ObjednávkaID: " + id +
                " | Materiál: " + material.getNazov() +
                " | Množstvo: " + mnozstvo + " ks" +
                " | Dodávateľ: " + dodavatel +
                " | Cena: " + celkovaCena + " EUR" +
                " | Stav: " + stav;
    }
}