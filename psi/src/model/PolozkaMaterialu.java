package model;

import model.use_case_3.Material;

public class PolozkaMaterialu {

    private final Material material;
    private final int pozadovaneMnozstvo;
    private boolean dostatok;

    public PolozkaMaterialu(Material material, int pozadovaneMnozstvo) {
        this.material = material;
        this.pozadovaneMnozstvo = pozadovaneMnozstvo;
        this.dostatok = material.getMnozstvo() >= pozadovaneMnozstvo;
    }

    public Material getMaterial() {
        return material;
    }

    public int getPozadovaneMnozstvo() {
        return pozadovaneMnozstvo;
    }

    public boolean jeDostatok() {
        return dostatok;
    }

    public void skontrolujDostatok() {
        this.dostatok = material.getMnozstvo() >= pozadovaneMnozstvo;
    }
}
