package model;

import model.use_case_3.Material;

/* TOTO JE PRE UC02 */
public class PolozkaMaterialu {

    private final Material material;
    private final int mnozstvo;
    private int rezervovane;
    private boolean dostatok;
    private final String nazov;

    public PolozkaMaterialu(Material material, int pozadovaneMnozstvo) {
        this.material = material;
        this.nazov = material.getNazov();
        this.mnozstvo = pozadovaneMnozstvo;
        this.dostatok = material.getMnozstvo() >= pozadovaneMnozstvo;
        this.rezervovane = 0;
    }

    public boolean jeDostatok() {
        return dostatok;
    }

    public boolean rezervuj(int ks) {
        if (material.getMnozstvo() < ks) return false;
        if (this.rezervovane + ks > this.mnozstvo) return false;

        this.rezervovane += ks;
        material.zmenitMnozstvo(-ks);
        return true;

    }

    public void zrusRezervaciu(int ks) {
        if (this.rezervovane >= ks) {
            material.zmenitMnozstvo(this.rezervovane);
            this.rezervovane = 0;
        }
    }

    // Zavolá sa, keď zákazka/úloha bude reálne vo výrobe a materiál sa fyzicky minie + vracia prebytok materialu ak sa nespotreboval cely
    public void spotrebujRezervovane(int spotrebovane) {
        if (spotrebovane > this.rezervovane) return;
        int zvysok = this.rezervovane - spotrebovane;
        material.zmenitMnozstvo(zvysok);
        this.rezervovane = 0;
    }

    public String getNazov() { return nazov; }
    public Material getMaterial() { return material; }
    public int getPozadovaneMnozstvo() { return mnozstvo; }
}
