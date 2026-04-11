package controller;

import model.Adresa;
import model.DataStore;
import model.Zakazka;
import model.Zakaznik;
import model.use_case_3.Material;

import java.time.LocalDate;
import java.util.List;

public class ZakazkaController {

    public Zakazka vytvorZakazku(String nazov, String popis,
                                 String menoZakaznika, String email, String telefon,
                                 String ulica, String cisloDomu, String mesto, String psc,
                                 double cena, LocalDate terminDorucenia,
                                 List<Material> materialy) {

        if (nazov == null || nazov.isBlank()) {
            throw new IllegalArgumentException("Názov zákazky je povinný.");
        }

        if (menoZakaznika == null || menoZakaznika.isBlank()) {
            throw new IllegalArgumentException("Meno zákazníka je povinné.");
        }

        if (cena < 0) {
            throw new IllegalArgumentException("Cena nesmie byť záporná.");
        }

        if (terminDorucenia == null) {
            throw new IllegalArgumentException("Termín doručenia je povinný.");
        }

        if (terminDorucenia.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Termín doručenia nemôže byť v minulosti.");
        }

        if (materialy == null || materialy.isEmpty()) {
            throw new IllegalArgumentException("Musí byť zadaný aspoň jeden materiál.");
        }

        Zakaznik zakaznik = new Zakaznik(menoZakaznika, email, telefon);
        Adresa adresa = new Adresa(ulica, cisloDomu, mesto, psc);

        Zakazka zakazka = new Zakazka(nazov, popis, zakaznik, adresa,
                cena, terminDorucenia, materialy);

        DataStore.pridajZakazku(zakazka);
        return zakazka;
    }
}
