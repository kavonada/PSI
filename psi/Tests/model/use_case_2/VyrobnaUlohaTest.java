package model.use_case_2;

import model.use_case_3.Material;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VyrobnaUlohaTest {

    @Test
    void testVyrobnaUlohaGetters() {
        Material m = new Material("Doska", 50, 5, 100);
        Pracovnik p = new Pracovnik("Jano", true, false);
        Stroj s = new Stroj("Píla", true);

        VyrobnaUloha uloha = new VyrobnaUloha("Narezanie", "Rezanie", m, 5, p, s, true);

        assertEquals("Narezanie", uloha.getNazov());
        assertEquals("Rezanie", uloha.getOperacia());
        assertEquals(m, uloha.getMaterial());
        assertEquals(5, uloha.getMnozstvo());
        assertEquals(p, uloha.getPracovnik());
        assertEquals(s, uloha.getStroj());
        assertTrue(uloha.isCakaNaMaterial());
    }
}