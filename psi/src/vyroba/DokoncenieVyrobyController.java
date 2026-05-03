package vyroba;

import obchod.Zakazka;

public class DokoncenieVyrobyController {

    public boolean oznacZakazkuAkoDokoncenu(Zakazka zakazka) {
        if (zakazka == null) {
            return false;
        }

        if (zakazka.getStav() != Zakazka.StavZakazky.NAPLANOVANA) {
            return false;
        }

        zakazka.setStav(Zakazka.StavZakazky.DOKONCENA);
        return true;
    }
}
