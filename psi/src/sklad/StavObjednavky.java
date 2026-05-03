package sklad;

public enum StavObjednavky {
    VYTVORENA("Vytvorena"),
    CAKA_NA_SCHVALENIE("Caka na schvalenie"),
    DORUCENA("Dorucena"),
    VYBAVENA("Vybavena");

    private final String popis;

    StavObjednavky(String popis) {
        this.popis = popis;
    }

    public String getPopis() { return popis; }

    @Override
    public String toString() { return popis; }
}