import controller.VyrobaController;
import model.use_case_2.PolozkaMaterialu;
import model.Zakazka;
import model.Zakaznik;
import model.Adresa;
import model.use_case_2.Pracovnik;
import model.use_case_2.Stroj;
import model.use_case_2.VyrobnaUloha;
import sklad.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class VyrobaControllerTest {

    private VyrobaController controller;
    private Zakazka zakazka;
    private Material drevo;
    private PolozkaMaterialu pm_drevo;
    private Pracovnik dostupnyPracovnik;
    private Pracovnik nedostupnyPracovnik;
    private Stroj dostupnyStroj;
    private Stroj nedostupnyStroj;

    @BeforeEach
    void setUp() {
        controller = new VyrobaController();
        drevo = new Material("Dubové drevo", 100, 5, 2000);
        pm_drevo = new PolozkaMaterialu(drevo, 100);
        dostupnyPracovnik = new Pracovnik("Jozef", true, false);
        nedostupnyPracovnik = new Pracovnik("Fero", false, false);
        dostupnyStroj = new Stroj("Píla", true);
        nedostupnyStroj = new Stroj("Fréza", false);

        Zakaznik z = new Zakaznik("Test", "test@test.sk", "123");
        Adresa a = new Adresa("Ulica", "1", "Mesto", "00000");
        zakazka = new Zakazka("Testovacia", "Popis", z, a, 100.0, LocalDate.now(), new ArrayList<>());
    }

    @Test
    void testMozemNaplanovat_VsetkoDostupne() {
        assertTrue(controller.mozemNaplanovatZdroje(dostupnyPracovnik, dostupnyStroj));
    }

    @Test
    void testMozemNaplanovat_PracovnikNull() {
        assertFalse(controller.mozemNaplanovatZdroje(null, dostupnyStroj));
    }

    @Test
    void testMozemNaplanovat_PracovnikNedostupny() {
        assertFalse(controller.mozemNaplanovatZdroje(nedostupnyPracovnik, dostupnyStroj));
    }

    @Test
    void testMozemNaplanovat_StrojNull() {
        // Pracovník je OK, ale stroj je null
        assertFalse(controller.mozemNaplanovatZdroje(dostupnyPracovnik, null));
    }

    @Test
    void testMozemNaplanovat_StrojNedostupny() {
        // Pracovník je OK, ale stroj je nedostupný
        assertFalse(controller.mozemNaplanovatZdroje(dostupnyPracovnik, nedostupnyStroj));
    }

    @Test
    void testJeDostatokMaterialu() {
        assertTrue(controller.jeDostatokMaterialu(drevo, 100), "Vetvy: dostatok");
        assertFalse(controller.jeDostatokMaterialu(drevo, 150), "Vetvy: nedostatok");
    }

    @Test
    void testPoziadajOObjednanie() {
        // Metóda len vypisuje do konzoly, testujeme, či nezbehne s výnimkou
        assertDoesNotThrow(() -> controller.poziadajOObjednanie(drevo, 50));
    }

    @Test
    void testPrehodnotStav_PrazdneUlohy() {
        controller.prehodnotStavZakazky(zakazka);
        assertEquals(Zakazka.StavZakazky.VYTVORENA, zakazka.getStav());
    }

    @Test
    void testPrehodnotStav_VsetkoPripravene() {
        zakazka.getVyrobneUlohy().add(new VyrobnaUloha("Rezanie", "Rezanie", pm_drevo, dostupnyPracovnik, dostupnyStroj, false));
        controller.prehodnotStavZakazky(zakazka);
        assertEquals(Zakazka.StavZakazky.NAPLANOVANA, zakazka.getStav());
    }

    @Test
    void testPrehodnotStav_Ciastocne_ChybaMaterial() {
        zakazka.getVyrobneUlohy().add(new VyrobnaUloha("Rezanie", "Rezanie", pm_drevo, dostupnyPracovnik, dostupnyStroj, true));
        controller.prehodnotStavZakazky(zakazka);
        assertEquals(Zakazka.StavZakazky.CIASTOCNE_NAPLANOVANA, zakazka.getStav());
    }

    @Test
    void testPrehodnotStav_Ciastocne_ChybaPracovnik() {
        zakazka.getVyrobneUlohy().add(new VyrobnaUloha("Rezanie", "Rezanie", pm_drevo, null, dostupnyStroj, false));
        controller.prehodnotStavZakazky(zakazka);
        assertEquals(Zakazka.StavZakazky.CIASTOCNE_NAPLANOVANA, zakazka.getStav());
    }
}