package vyroba;

public class Pracovnik {

    private final String meno;
    private final boolean dostupny;
    private final boolean externy;

    public Pracovnik(String meno, boolean dostupny, boolean externy) {
        this.meno = meno;
        this.dostupny = dostupny;
        this.externy = externy;
    }

    public String getMeno() { return meno; }
    public boolean jeDostupny() { return dostupny; }
    public boolean jeExterny() { return externy; }

    @Override
    public String toString() {
        return meno + (externy ? " (externý)" : "");
    }
}