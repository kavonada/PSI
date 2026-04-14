package controller;

import model.Zakazka;
import model.use_case_2.Pracovnik;
import model.use_case_2.Stroj;
import model.use_case_2.VyrobnaUloha;
import model.use_case_3.Material;

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
        // Tu sa v budúcnosti napojíte na UC03 (Sklad), napr. pridanie do "Wanted" listu.
        System.out.println("INFO PRE SKLAD: Treba doobjednať " + chybajuceMnozstvo + " ks materiálu: " + m.getNazov());
    }

    public void prehodnotStavZakazky(Zakazka z) {
        if (z.getVyrobneUlohy().isEmpty()) {
            z.setStav(Zakazka.StavZakazky.VYTVORENA);
            return;
        }

        boolean vsetkoPripravene = true;

        for (VyrobnaUloha u : z.getVyrobneUlohy()) {
            // Zákazka je ČIASTOČNÁ ak: chýba materiál ALEBO nie je priradený/dostupný pracovník/stroj
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
}