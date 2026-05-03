package vyroba;

import sklad.PolozkaMaterialu;

public class VyrobnaUloha {
    private final String nazov;
    private final String operacia;
    private final PolozkaMaterialu polozkaMaterialu;
    private final Pracovnik pracovnik;
    private final Stroj stroj;
    private final boolean cakaNaMaterial;
    private StavUlohy stav;

    public enum StavUlohy {
        NAPLANOVANA,
        VO_VYROBE,
        VYROBENA
    }

    public VyrobnaUloha(String nazov, String operacia, PolozkaMaterialu polozkaMaterialu, Pracovnik pracovnik, Stroj stroj, boolean cakaNaMaterial) {
        this.nazov = nazov;
        this.operacia = operacia;
        this.polozkaMaterialu = polozkaMaterialu;
        this.pracovnik = pracovnik;
        this.stroj = stroj;
        this.cakaNaMaterial = cakaNaMaterial;
        this.stav = StavUlohy.NAPLANOVANA;
    }

    public String getNazov() { return nazov; }
    public String getOperacia() { return operacia; }
    public PolozkaMaterialu getPolozkaMaterialu() { return polozkaMaterialu; }
    public Pracovnik getPracovnik() { return pracovnik; }
    public Stroj getStroj() { return stroj; }
    public StavUlohy getStav() { return stav; }
    public boolean isCakaNaMaterial() { return cakaNaMaterial; }

    // Zavolá sa, keď úloha bude reálne vo výrobe
    public void oznacAkoRozpracovanu() {
        this.stav = StavUlohy.VO_VYROBE;
    }

    // Zavola sa, ked bude vyrobena
    public void dokonciUlohu(int spotrebovanyMaterial){
        if (this.stav != StavUlohy.VO_VYROBE) {
            return;
        }
        if (!this.cakaNaMaterial) {
            this.polozkaMaterialu.spotrebujRezervovane(spotrebovanyMaterial);
        }
        this.stav = StavUlohy.VYROBENA;
    }
}