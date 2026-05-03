package vyroba;

public class Stroj {

    private final String nazov;
    private final boolean dostupny;

    public Stroj(String nazov, boolean dostupny) {
        this.nazov = nazov;
        this.dostupny = dostupny;
    }

    public String getNazov() { return nazov; }
    public boolean jeDostupny() { return dostupny; }

    @Override
    public String toString() {
        return nazov;
    }
}