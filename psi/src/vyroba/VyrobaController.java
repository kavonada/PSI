package vyroba;

import ulozisko.DataStore;
import model.Zakazka;
import sklad.Material;

public class VyrobaController {

    // Kontrola, či môžeme úlohu naplánovať
    public boolean mozemNaplanovatZdroje(Pracovnik p, Stroj s) {
        if (p == null || !p.jeDostupny()) return false;
        if (s == null || !s.jeDostupny()) return false;
        return true;
    }

    public boolean jeDostatokMaterialu(Material m, int pozadovaneMnozstvo) {
        return m.getMnozstvo() >= pozadovaneMnozstvo;
    }

    public void poziadajOObjednanie(Material m, int chybajuceMnozstvo) {
        PoziadavkaNaMaterial novaPoziadavka = new PoziadavkaNaMaterial(m, chybajuceMnozstvo);
        DataStore.poziadavkyNaMaterial.add(novaPoziadavka);
        System.out.println("INFO PRE SKLAD: Treba doobjednať " + chybajuceMnozstvo + " ks materiálu: " + m.getNazov());
    }

    public void prehodnotStavZakazky(Zakazka z) {
        if (z.getVyrobneUlohy().isEmpty()) {
            z.setStav(Zakazka.StavZakazky.VYTVORENA);
            return;
        }

        boolean vsetkoPripravene = true;

        for (VyrobnaUloha u : z.getVyrobneUlohy()) {
            if (u.isCakaNaMaterial() || u.getPracovnik() == null || u.getStroj() == null) {
                vsetkoPripravene = false;
                break;
            }
        }

        if (vsetkoPripravene) {
            z.setStav(Zakazka.StavZakazky.NAPLANOVANA);
        } else {
            z.setStav(Zakazka.StavZakazky.CIASTOCNE_NAPLANOVANA);
        }
    }

    // Môže byť zavolaná pri zrušení zákazky - vráti rezervovaný materiál ako dostupný pre všetky úlohy, ktoré ešte nie sú rozpracované vo výrobe
    public void uvolniRezervacie(Zakazka z) {
        for (VyrobnaUloha u : z.getVyrobneUlohy()) {
            if (!u.isCakaNaMaterial() && !u.getStav().equals("VO_VYROBE")) {
                u.getPolozkaMaterialu().zrusRezervaciu(u.getPolozkaMaterialu().getPozadovaneMnozstvo());
            }
        }
        z.setStav(Zakazka.StavZakazky.ZRUSENA);
    }
}