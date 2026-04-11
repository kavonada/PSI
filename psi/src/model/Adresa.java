package model;

public class Adresa {
    private String ulica;
    private String cisloDomu;
    private String mesto;
    private String psc;

    public Adresa(String ulica, String cisloDomu, String mesto, String psc) {
        this.ulica = ulica;
        this.cisloDomu = cisloDomu;
        this.mesto = mesto;
        this.psc = psc;
    }

    public String getUlica() {
        return ulica;
    }

    public String getCisloDomu() {
        return cisloDomu;
    }

    public String getMesto() {
        return mesto;
    }

    public String getPsc() {
        return psc;
    }

    @Override
    public String toString() {
        return ulica + " " + cisloDomu + ", " + mesto + ", " + psc;
    }
}
