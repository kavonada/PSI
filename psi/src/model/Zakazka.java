package model;

import model.use_case_2.VyrobnaUloha;
import model.use_case_3.Material;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Zakazka {

    private static int counter = 1;

    private final int id;
    private String nazov;
    private String popis;

    private Zakaznik zakaznik;
    private Adresa dodaciaAdresa;

    private double cena;
    private LocalDate terminDorucenia;

    private List<Material> materialy;
    private List<PolozkaMaterialu> polozkyMaterialu;
    private List<VyrobnaUloha> vyrobneUlohy;

    private StavZakazky stav;

    public enum StavZakazky {
        VYTVORENA,
        NAPLANOVANA,
        CIASTOCNE_NAPLANOVANA,
        DOKONCENA,
        ZRUSENA
    }

    public Zakazka(String nazov, String popis,
                   Zakaznik zakaznik, Adresa dodaciaAdresa,
                   double cena, LocalDate terminDorucenia,
                   List<Material> materialy) {

        this.id = counter++;
        this.nazov = nazov;
        this.popis = popis;
        this.zakaznik = zakaznik;
        this.dodaciaAdresa = dodaciaAdresa;
        this.cena = cena;
        this.terminDorucenia = terminDorucenia;
        this.materialy = materialy;
        this.stav = StavZakazky.VYTVORENA;
        this.polozkyMaterialu = new ArrayList<>();
        this.vyrobneUlohy = new ArrayList<>();
    }


    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Zakazka.counter = counter;
    }

    public int getId() {
        return id;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public Zakaznik getZakaznik() {
        return zakaznik;
    }

    public void setZakaznik(Zakaznik zakaznik) {
        this.zakaznik = zakaznik;
    }

    public double getCena() {
        return cena;
    }

    public void setCena(double cena) {
        this.cena = cena;
    }

    public LocalDate getTerminDorucenia() {
        return terminDorucenia;
    }

    public void setTerminDorucenia(LocalDate terminDorucenia) {
        this.terminDorucenia = terminDorucenia;
    }

    public List<Material> getMaterialy() {
        return materialy;
    }

    public void setMaterialy(List<Material> materialy) {
        this.materialy = materialy;
    }

    public List<PolozkaMaterialu> getPolozkyMaterialu() {
        return polozkyMaterialu;
    }

    public void setPolozkyMaterialu(List<PolozkaMaterialu> polozkyMaterialu) {
        this.polozkyMaterialu = polozkyMaterialu;
    }

    public Adresa getDodaciaAdresa() {
        return dodaciaAdresa;
    }

    public void setDodaciaAdresa(Adresa dodaciaAdresa) {
        this.dodaciaAdresa = dodaciaAdresa;
    }

    public StavZakazky getStav() {
        return stav;
    }

    public void setStav(StavZakazky stav) {
        this.stav = stav;
    }

    public String getDisplayName() {
        return "#" + id + " - " + nazov;
    }

    public List<VyrobnaUloha> getVyrobneUlohy() { return vyrobneUlohy; }

    public void setVyrobneUlohy(List<VyrobnaUloha> vyrobneUlohy) { this.vyrobneUlohy = vyrobneUlohy; }

    @Override
    public String toString() {
        return "Zákazka #" + id + "\n" +
                "Názov: " + nazov + "\n" +
                "Popis: " + popis + "\n" +
                "Zákazník: " + zakaznik + "\n" +
                "Adresa: " + dodaciaAdresa + "\n" +
                "Cena: " + cena + " €\n" +
                "Najneskorší termín doručenia: " + terminDorucenia + "\n" +
                "Materiály: " + materialy + "\n" +
                "Stav: " + stav;
    }

}
