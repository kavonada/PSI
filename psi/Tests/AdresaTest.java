import obchod.Adresa;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdresaTest {

    @Test
    void konstruktor_gettery_funguju() {
        Adresa zakaznik = new Adresa(
                "Pekna",
                "12",
                "Bratislava",
                "84105"
        );

        assertEquals("Pekna", zakaznik.getUlica());
        assertEquals("12", zakaznik.getCisloDomu());
        assertEquals("Bratislava", zakaznik.getMesto());
        assertEquals("84105", zakaznik.getPsc());
    }

    @Test
    void toString_vratiSpravnyFormat() {
        Adresa zakaznik = new Adresa(
                "Pekna",
                "12",
                "Bratislava",
                "84105"
        );

        String expected = "Pekna 12, Bratislava, 84105";

        assertEquals(expected, zakaznik.toString());
    }
}
