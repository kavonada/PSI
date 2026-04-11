import model.Zakaznik;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZakaznikTest {

    @Test
    void konstruktor_gettery_funguju() {
        Zakaznik zakaznik = new Zakaznik(
                "Jozef Mrkvička",
                "jozko@stuba.sk",
                "0900123456"
        );

        assertEquals("Jozef Mrkvička", zakaznik.getMeno());
        assertEquals("jozko@stuba.sk", zakaznik.getEmail());
        assertEquals("0900123456", zakaznik.getTelefon());
    }

    @Test
    void toString_vratiSpravnyFormat() {
        Zakaznik zakaznik = new Zakaznik(
                "Jozef Mrkvička",
                "jozko@stuba.sk",
                "0900123456"
        );

        String expected = "Jozef Mrkvička | jozko@stuba.sk | 0900123456";

        assertEquals(expected, zakaznik.toString());
    }
}
