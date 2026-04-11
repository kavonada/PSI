package model;

import model.use_case_3.Material;

/* TOTO JE PRE UC02 */
public class PolozkaMaterialu {

    private Material material;
    private int pozadovaneMnozstvo;

    public PolozkaMaterialu(Material material, int pozadovaneMnozstvo) {
        this.material = material;
        this.pozadovaneMnozstvo = pozadovaneMnozstvo;
    }

    public Material getMaterial() {
        return material;
    }

    public int getPozadovaneMnozstvo() {
        return pozadovaneMnozstvo;
    }
}
