public class Zakazka {

    private static int counter = 1;

    private int id;
    private String nazov;
    private String stav;

    // UC01 – additional fields
    private String zakaznik;
    private double cena;
    private String terminDoručenia;
    private String materialNazov;
    private int materialMnozstvo;

    public Zakazka(String nazov, String zakaznik, double cena, String terminDoručenia,
                   String materialNazov, int materialMnozstvo) {
        this.id = counter++;
        this.nazov = nazov;
        this.zakaznik = zakaznik;
        this.cena = cena;
        this.terminDoručenia = terminDoručenia;
        this.materialNazov = materialNazov;
        this.materialMnozstvo = materialMnozstvo;
        this.stav = "Vytvorená";
    }

    public int getId() { return id; }
    public String getNazov() { return nazov; }
    public String getStav() { return stav; }
    public void setStav(String stav) { this.stav = stav; }
    public String getZakaznik() { return zakaznik; }
    public double getCena() { return cena; }
    public String getTerminDoručenia() { return terminDoručenia; }
    public String getMaterialNazov() { return materialNazov; }
    public int getMaterialMnozstvo() { return materialMnozstvo; }

    @Override
    public String toString() {
        return "ID: " + id +
                " | Produkt: " + nazov +
                " | Zákazník: " + zakaznik +
                " | Cena: " + cena + " EUR" +
                " | Termín: " + terminDoručenia +
                " | Materiál: " + materialNazov + " (" + materialMnozstvo + " ks)" +
                " | Stav: " + stav;
    }
}