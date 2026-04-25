package model.use_case_2;

import model.Zakazka;
import model.use_case_3.Material;

public class VyrobnaUloha {
    private final String nazov;
    private final String operacia;
    private final Material material;
    private final int mnozstvo;
    private final Pracovnik pracovnik;
    private final Stroj stroj;
    private final boolean cakaNaMaterial;
    private StavUlohy stav;

    public enum StavUlohy {
        NAPLANOVANA,
        VO_VYROBE
    }

    public VyrobnaUloha(String nazov, String operacia, Material material, int mnozstvo, Pracovnik pracovnik, Stroj stroj, boolean cakaNaMaterial) {
        this.nazov = nazov;
        this.operacia = operacia;
        this.material = material;
        this.mnozstvo = mnozstvo;
        this.pracovnik = pracovnik;
        this.stroj = stroj;
        this.cakaNaMaterial = cakaNaMaterial;
        this.stav = StavUlohy.NAPLANOVANA;
    }

    public String getNazov() { return nazov; }
    public String getOperacia() { return operacia; }
    public Material getMaterial() { return material; }
    public int getMnozstvo() { return mnozstvo; }
    public Pracovnik getPracovnik() { return pracovnik; }
    public Stroj getStroj() { return stroj; }
    public StavUlohy getStav() { return stav; }
    public boolean isCakaNaMaterial() { return cakaNaMaterial; }

    // Zavolá sa, keď úloha bude reálne vo výrobe
    public void oznacAkoRozpracovanu() {
        if (!this.cakaNaMaterial) {
            this.material.spotrebujRezervovane(this.mnozstvo);
        }
        this.stav = StavUlohy.VO_VYROBE;
    }
}