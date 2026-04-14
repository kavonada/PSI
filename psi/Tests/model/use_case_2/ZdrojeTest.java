package model.use_case_2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ZdrojeTest {

    @Test
    void testPracovnikGetters() {
        Pracovnik p = new Pracovnik("Jozef", true, false);
        assertEquals("Jozef", p.getMeno());
        assertTrue(p.jeDostupny());
        assertFalse(p.jeExterny());
    }

    @Test
    void testPracovnikToString_Interny() {
        Pracovnik interny = new Pracovnik("Jozef", true, false);
        assertEquals("Jozef", interny.toString());
    }

    @Test
    void testPracovnikToString_Externy() {
        Pracovnik externy = new Pracovnik("Firma DrevStav", true, true);
        assertEquals("Firma DrevStav (externý)", externy.toString());
    }

    @Test
    void testStrojGetters() {
        Stroj s = new Stroj("CNC", false);
        assertEquals("CNC", s.getNazov());
        assertFalse(s.jeDostupny());
    }

    @Test
    void testStrojToString() {
        Stroj s = new Stroj("Píla", true);
        assertEquals("Píla", s.toString());
    }
}