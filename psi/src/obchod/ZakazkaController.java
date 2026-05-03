package obchod;

import ulozisko.DataStore;
import sklad.Material;
import vyroba.VyrobnaUloha;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ZakazkaController {
    private void overPlatnostUdajov(String nazov, String menoZakaznika, String email,
                                    String ulica, String cisloDomu, String mesto, String psc,
                                    double cena, LocalDate terminDorucenia, List<Material> materialy) {

        List<String> chyby = new ArrayList<>();

        if (nazov == null || nazov.isBlank()) {
            chyby.add("Názov zákazky je povinný.");
        }

        if (menoZakaznika == null || menoZakaznika.isBlank()) {
            chyby.add("Meno zákazníka je povinné.");
        }

        if (email == null || email.isBlank()) {
            chyby.add("E-mail je povinný.");
        }

        if (ulica == null || ulica.isBlank()) {
            chyby.add("Ulica je povinná.");
        }

        if (cisloDomu == null || cisloDomu.isBlank()) {
            chyby.add("Číslo domu je povinné.");
        }

        if (mesto == null || mesto.isBlank()) {
            chyby.add("Mesto je povinné.");
        }

        if (psc == null || psc.isBlank()) {
            chyby.add("PSČ je povinné.");
        }

        if (cena < 0) {
            chyby.add("Cena nesmie byť záporná.");
        }

        if (terminDorucenia == null) {
            chyby.add("Termín doručenia je povinný.");
        } else if (terminDorucenia.isBefore(LocalDate.now())) {
            chyby.add("Termín doručenia nemôže byť v minulosti.");
        }

        if (materialy == null || materialy.isEmpty()) {
            chyby.add("Musí byť zadaný aspoň jeden materiál.");
        }

        if (!chyby.isEmpty()) {
            throw new ValidationException(chyby);
        }
    }

    public Zakazka vytvorZakazku(String nazov, String popis,
                                 String menoZakaznika, String email, String telefon,
                                 String ulica, String cisloDomu, String mesto, String psc,
                                 double cena, LocalDate terminDorucenia,
                                 List<Material> materialy) {

        overPlatnostUdajov(
                nazov, menoZakaznika, email,
                ulica, cisloDomu, mesto, psc,
                cena, terminDorucenia, materialy
        );

        Zakaznik zakaznik = new Zakaznik(menoZakaznika, email, telefon);
        Adresa adresa = new Adresa(ulica, cisloDomu, mesto, psc);

        Zakazka zakazka = new Zakazka(nazov, popis, zakaznik, adresa,
                cena, terminDorucenia, materialy);

        DataStore.pridajZakazku(zakazka);
        return zakazka;
    }

    public List<Material> overMaterialy(List<Material> materialy) {
        List<Material> nedostatkove = new ArrayList<>();

        if (materialy == null) {
            return nedostatkove;
        }

        for (Material material : materialy) {
            if (!material.overDostupnost(1)) {
                nedostatkove.add(material);
            }
        }

        return nedostatkove;
    }

    public void zrusZakazku(Zakazka zakazka) {
        if (zakazka == null) {
            return;
        }

        if (zakazka.getStav() == Zakazka.StavZakazky.VYTVORENA) {
            DataStore.zakazky.remove(zakazka);
            return;
        }

        if (zakazka.getStav() == Zakazka.StavZakazky.NAPLANOVANA
                || zakazka.getStav() == Zakazka.StavZakazky.CIASTOCNE_NAPLANOVANA) {

            for (VyrobnaUloha uloha : zakazka.getVyrobneUlohy()) {
                if (!uloha.isCakaNaMaterial()
                        && uloha.getStav() != VyrobnaUloha.StavUlohy.VO_VYROBE
                        && uloha.getStav() != VyrobnaUloha.StavUlohy.VYROBENA) {

                    uloha.getPolozkaMaterialu().zrusRezervaciu(
                            uloha.getPolozkaMaterialu().getPozadovaneMnozstvo()
                    );
                }
            }

            zakazka.setStav(Zakazka.StavZakazky.ZRUSENA);
            return;
        }

        zakazka.setStav(Zakazka.StavZakazky.ZRUSENA);
    }
}

