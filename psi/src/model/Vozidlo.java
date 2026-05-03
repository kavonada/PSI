package model;

public class Vozidlo {

    private static int counter = 1;

    private final int id;
    private String nazov;
    private String spz;
    private int kapacita;

    public Vozidlo(String nazov, String spz, int kapacita) {
        this.id = counter++;
        this.nazov = nazov;
        this.spz = spz;
        this.kapacita = kapacita;
    }

    public int getId() {
        return id;
    }

    public String getNazov() {
        return nazov;
    }

    public String getSpz() {
        return spz;
    }

    public int getKapacita() {
        return kapacita;
    }

    @Override
    public String toString() {
        return nazov + " (" + spz + ")";
    }
}