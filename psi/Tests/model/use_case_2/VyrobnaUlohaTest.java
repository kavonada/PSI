package model.use_case_2;

import model.PolozkaMaterialu;
import model.use_case_3.Material;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VyrobnaUlohaTest {

    @Test
    void testVyrobnaUlohaGetters() {
        Material m = new Material("Doska", 50, 5, 100);
        PolozkaMaterialu pm = new PolozkaMaterialu(m, 5);
        Pracovnik p = new Pracovnik("Jano", true, false);
        Stroj s = new Stroj("Píla", true);

        VyrobnaUloha uloha = new VyrobnaUloha("Narezanie", "Rezanie", pm, p, s, true);

        assertEquals("Narezanie", uloha.getNazov());
        assertEquals("Rezanie", uloha.getOperacia());
        assertEquals(m, uloha.getPolozkaMaterialu());
        assertEquals(5, uloha.getPolozkaMaterialu().getPozadovaneMnozstvo());
        assertEquals(p, uloha.getPracovnik());
        assertEquals(s, uloha.getStroj());
        assertTrue(uloha.isCakaNaMaterial());
    }
}