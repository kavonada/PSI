package controller;

import org.junit.jupiter.api.Test;
import sklad.InventarController;

import static org.junit.jupiter.api.Assertions.*;

class InventarControllerTest {

    @Test
    public void testUspesnePridatDoKosika() {
        InventarController inventar = new InventarController();
        InventarController.VysledokObjednavky vysledok = inventar.pridatDoKosika(1, 10);

        assertEquals(InventarController.VysledokObjednavky.Typ.USPECH, vysledok.typ);
    }

    @Test
    public void testNeuspesnePridatDoKosika() {
        InventarController inventar = new InventarController();
        InventarController.VysledokObjednavky vysledok = inventar.pridatDoKosika(-999, 10);

        assertEquals(InventarController.VysledokObjednavky.Typ.CHYBA, vysledok.typ);
    }
}