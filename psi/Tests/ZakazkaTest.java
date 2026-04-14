import model.Adresa;
import model.PolozkaMaterialu;
import model.Zakazka;
import model.Zakaznik;
import model.use_case_2.VyrobnaUloha;
import model.use_case_3.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZakazkaTest {

    private Zakaznik zakaznik;
    private Adresa adresa;
    private List<Material> materialy;
    private Zakazka zakazka;

    @BeforeEach
    void setUp() {
        // Zresetujeme counter pred každým testom, aby sme s istotou vedeli, aké ID čakáme
        Zakazka.setCounter(1);

        zakaznik = new Zakaznik("Jozef Mrkvička", "jozko@stuba.sk", "0900123456");
        // Ak má Adresa iný konštruktor, uprav si tieto parametre
        adresa = new Adresa("Pekná", "12", "Bratislava", "84105");

        materialy = new ArrayList<>();
        // Ak má Material iný konštruktor (napr. 4 parametre ako v tvojom ControllerTeste), uprav to
        materialy.add(new Material("Drevo", 10, 5.0, 1));

        zakazka = new Zakazka("Stôl", "Masívny dubový stôl", zakaznik, adresa,
                250.0, LocalDate.now().plusDays(10), materialy);
    }

    @Test
    void testKonstruktorAPociatocnyStav() {
        assertEquals(1, zakazka.getId(), "Prvé ID po resete by malo byť 1");
        assertEquals("Stôl", zakazka.getNazov());
        assertEquals("Masívny dubový stôl", zakazka.getPopis());
        assertEquals(zakaznik, zakazka.getZakaznik());
        assertEquals(adresa, zakazka.getDodaciaAdresa());
        assertEquals(250.0, zakazka.getCena());
        assertNotNull(zakazka.getTerminDorucenia());
        assertEquals(materialy, zakazka.getMaterialy());

        // Test inicializácie defaultných hodnôt a prázdnych listov v konštruktore
        assertEquals(Zakazka.StavZakazky.VYTVORENA, zakazka.getStav());
        assertNotNull(zakazka.getPolozkyMaterialu());
        assertTrue(zakazka.getPolozkyMaterialu().isEmpty());
        assertNotNull(zakazka.getVyrobneUlohy());
        assertTrue(zakazka.getVyrobneUlohy().isEmpty());
    }

    @Test
    void testGetAndSetCounter_InkrementaciaID() {
        Zakazka z2 = new Zakazka("Stolička", "Popis", zakaznik, adresa, 50.0, LocalDate.now(), materialy);
        assertEquals(2, z2.getId(), "Druhá vytvorená zákazka musí mať ID 2");
        assertEquals(3, Zakazka.getCounter(), "Counter musí byť pripravený na 3");

        // Test manuálneho nastavenia countera
        Zakazka.setCounter(100);
        assertEquals(100, Zakazka.getCounter());
    }

    @Test
    void testVsetkySettery() {
        zakazka.setNazov("Nový názov");
        assertEquals("Nový názov", zakazka.getNazov());

        zakazka.setPopis("Nový popis");
        assertEquals("Nový popis", zakazka.getPopis());

        Zakaznik novyZakaznik = new Zakaznik("Fero", "fero@mail.com", "111");
        zakazka.setZakaznik(novyZakaznik);
        assertEquals(novyZakaznik, zakazka.getZakaznik());

        Adresa novaAdresa = new Adresa("Iná", "1", "Mesto", "000");
        zakazka.setDodaciaAdresa(novaAdresa);
        assertEquals(novaAdresa, zakazka.getDodaciaAdresa());

        zakazka.setCena(300.0);
        assertEquals(300.0, zakazka.getCena());

        LocalDate novyTermin = LocalDate.now().plusDays(5);
        zakazka.setTerminDorucenia(novyTermin);
        assertEquals(novyTermin, zakazka.getTerminDorucenia());

        List<Material> noveMaterialy = new ArrayList<>();
        zakazka.setMaterialy(noveMaterialy);
        assertEquals(noveMaterialy, zakazka.getMaterialy());

        List<PolozkaMaterialu> novePolozky = new ArrayList<>();
        zakazka.setPolozkyMaterialu(novePolozky);
        assertEquals(novePolozky, zakazka.getPolozkyMaterialu());

        List<VyrobnaUloha> noveUlohy = new ArrayList<>();
        zakazka.setVyrobneUlohy(noveUlohy);
        assertEquals(noveUlohy, zakazka.getVyrobneUlohy());

        zakazka.setStav(Zakazka.StavZakazky.DOKONCENA);
        assertEquals(Zakazka.StavZakazky.DOKONCENA, zakazka.getStav());
    }

    @Test
    void testGetDisplayName() {
        assertEquals("#1 - Stôl", zakazka.getDisplayName());

        zakazka.setNazov("Skriňa");
        assertEquals("#1 - Skriňa", zakazka.getDisplayName());
    }

    @Test
    void testToString() {
        String str = zakazka.toString();

        assertTrue(str.contains("Zákazka #1"));
        assertTrue(str.contains("Názov: Stôl"));
        assertTrue(str.contains("Popis: Masívny dubový stôl"));
        assertTrue(str.contains("Cena: 250.0 €"));
        assertTrue(str.contains("Stav: VYTVORENA"));
    }
}