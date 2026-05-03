import sklad.PolozkaMaterialu;
import sklad.Material;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PolozkaMaterialuTest {

    @Test
    void testPolozkaMaterialu_Dostatok() {
        Material m = new Material("Lak", 100, 5, 100);
        PolozkaMaterialu pm = new PolozkaMaterialu(m, 50); // Pýtame 50, máme 100

        assertEquals(m, pm.getMaterial());
        assertEquals(50, pm.getPozadovaneMnozstvo());
        assertTrue(pm.jeDostatok(), "Malo by byť dostatok materiálu");
    }

    @Test
    void testPolozkaMaterialu_Nedostatok() {
        Material m = new Material("Lak", 20, 5, 600);
        PolozkaMaterialu pm = new PolozkaMaterialu(m, 50); // Pýtame 50, máme 20

        assertFalse(pm.jeDostatok(), "Nemalo by byť dostatok materiálu");
    }

    @Test
    void testSkontrolujDostatok() {
        Material m = new Material("Farba", 10, 8, 1000);
        PolozkaMaterialu pm = new PolozkaMaterialu(m, 50);
        assertFalse(pm.jeDostatok()); // Pri vytvorení je nedostatok

        assertFalse(pm.jeDostatok(), "Po kontrole stále nedostatok, lebo stav materiálu sa nezmenil");
    }
}