package vyroba;

import sklad.Material;

public class PoziadavkaNaMaterial {
    private final Material material;
    private final int chybajuceMnozstvo;
    private boolean vybavene;

    public PoziadavkaNaMaterial(Material material, int chybajuceMnozstvo) {
        this.material = material;
        this.chybajuceMnozstvo = chybajuceMnozstvo;
        this.vybavene = false; // Po vytvoreni to sklad este nevybavil
    }

    public Material getMaterial() { return material; }
    public int getChybajuceMnozstvo() { return chybajuceMnozstvo; }
    public boolean isVybavene() { return vybavene; }
    public void setVybavene(boolean vybavene) { this.vybavene = vybavene; }

    @Override
    public String toString() {
        String stav = vybavene ? "[VYBAVENÉ]" : "[ČAKÁ]";
        return stav + " Treba doobjednať: " + chybajuceMnozstvo + " ks -> " + material.getNazov();
    }
}