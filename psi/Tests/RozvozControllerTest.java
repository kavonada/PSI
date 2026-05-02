import controller.RozvozController;
import controller.RozvozController.VysledokRozvozu;
import model.Adresa;
import model.DataStore;
import model.Zakazka;
import model.Zakaznik;
import model.use_case_3.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RozvozControllerTest {

    private RozvozController controller;
    private Zakazka z1, z2;

    @BeforeEach
    void setUp() {
        // Čistý stav pred každým testom
        DataStore.getZakazky().clear();
        DataStore.cakajuceRozvozy.clear();
        DataStore.rozvozy.clear();

        controller = new RozvozController();

        Zakaznik zakaznik = new Zakaznik("Test", "test@test.sk", "0900000000");
        Adresa adresa = new Adresa("Testova", "1", "Testovo", "00000");
        List<Material> materialy = new ArrayList<>();

        z1 = new Zakazka("Z1", "Popis1", zakaznik, adresa, 100, LocalDate.now(), materialy);
        z2 = new Zakazka("Z2", "Popis2", zakaznik, adresa, 200, LocalDate.now(), materialy);

        DataStore.pridajZakazku(z1);
        DataStore.pridajZakazku(z2);
    }

    // ── UC04 – Vytvorenie rozvozu ─────────────────────────────────────────────

    @Test
    void testVytvorenieRozvozu_uspech() {
        VysledokRozvozu v = controller.vytvorRozvoz(
                "TEST-VOZIDLO", 5, "10.10.2025", List.of(z1.getId(), z2.getId())
        );

        assertEquals(VysledokRozvozu.Typ.USPECH, v.typ);
        assertEquals(1, controller.getCakajuceRozvozy().size());
        assertEquals("Čaká na schválenie", controller.getCakajuceRozvozy().get(0).getStav());
    }

    @Test
    void testVytvorenieRozvozu_prazdneVozidlo() {
        VysledokRozvozu v = controller.vytvorRozvoz(
                "", 5, "10.10.2025", List.of(z1.getId())
        );

        assertEquals(VysledokRozvozu.Typ.CHYBA, v.typ);
        assertEquals(0, controller.getCakajuceRozvozy().size());
    }

    @Test
    void testVytvorenieRozvozu_neplatnyDatum() {
        VysledokRozvozu v = controller.vytvorRozvoz(
                "Auto", 5, "2025-10-10", List.of(z1.getId()) // zlý formát
        );

        assertEquals(VysledokRozvozu.Typ.CHYBA, v.typ);
    }

    @Test
    void testVytvorenieRozvozu_prekrocenaKapacita() {
        // Kapacita 1, ale chceme pridať 2 zákazky
        VysledokRozvozu v = controller.vytvorRozvoz(
                "Auto", 1, "10.10.2025", List.of(z1.getId(), z2.getId())
        );

        assertEquals(VysledokRozvozu.Typ.CHYBA, v.typ);
        assertEquals(0, controller.getCakajuceRozvozy().size());
    }

    @Test
    void testVytvorenieRozvozu_prazdnyZoznamZakaziek() {
        VysledokRozvozu v = controller.vytvorRozvoz(
                "Auto", 5, "10.10.2025", new ArrayList<>()
        );

        assertEquals(VysledokRozvozu.Typ.CHYBA, v.typ);
    }

    // ── UC04 – Schválenie rozvozu ─────────────────────────────────────────────

    @Test
    void testSchvalenieRozvozu_uspech() {
        controller.vytvorRozvoz("Auto", 5, "10.10.2025", List.of(z1.getId(), z2.getId()));

        int id = controller.getCakajuceRozvozy().get(0).getId();
        VysledokRozvozu v = controller.schvalitRozvoz(id);

        assertEquals(VysledokRozvozu.Typ.USPECH, v.typ);
        assertEquals(0, controller.getCakajuceRozvozy().size());
        assertEquals(1, controller.getSchvaleneRozvozy().size());
        assertEquals("Naplánovaný", controller.getSchvaleneRozvozy().get(0).getStav());
    }

    @Test
    void testSchvalenieRozvozu_zakazkyDostanuSpravnyStav() {
        controller.vytvorRozvoz("Auto", 5, "10.10.2025", List.of(z1.getId(), z2.getId()));

        int id = controller.getCakajuceRozvozy().get(0).getId();
        controller.schvalitRozvoz(id);

        // Zákazky musia mať stav VYROBENA po schválení
        assertEquals(Zakazka.StavZakazky.DOKONCENA, z1.getStav());
        assertEquals(Zakazka.StavZakazky.DOKONCENA, z2.getStav());
    }

    @Test
    void testSchvalenieRozvozu_neexistujuceId() {
        VysledokRozvozu v = controller.schvalitRozvoz(9999);

        assertEquals(VysledokRozvozu.Typ.CHYBA, v.typ);
    }

    // ── UC04 – Zamietnutie rozvozu ────────────────────────────────────────────

    @Test
    void testZamietnutieRozvozu_uspech() {
        controller.vytvorRozvoz("Auto", 5, "10.10.2025", List.of(z1.getId(), z2.getId()));

        int id = controller.getCakajuceRozvozy().get(0).getId();
        VysledokRozvozu v = controller.zamietnytRozvoz(id);

        assertEquals(VysledokRozvozu.Typ.USPECH, v.typ);
        assertEquals(0, controller.getCakajuceRozvozy().size());
        assertEquals(0, controller.getSchvaleneRozvozy().size()); // nie je v schválených
    }

    @Test
    void testZamietnutieRozvozu_zakazkyVratenaNaVytvorena() {
        controller.vytvorRozvoz("Auto", 5, "10.10.2025", List.of(z1.getId(), z2.getId()));

        int id = controller.getCakajuceRozvozy().get(0).getId();
        controller.zamietnytRozvoz(id);

        // Zákazky musia byť vrátené na VYTVORENA
        assertEquals(Zakazka.StavZakazky.VYTVORENA, z1.getStav());
        assertEquals(Zakazka.StavZakazky.VYTVORENA, z2.getStav());
    }

    // ── Celý flow naraz ───────────────────────────────────────────────────────

    @Test
    void testVytvorenieASchvalenieRozvozu_celkovyFlow() {
        // Vytvorenie
        VysledokRozvozu vytvorenie = controller.vytvorRozvoz(
                "TEST-VOZIDLO", 5, "10.10.2025", List.of(z1.getId(), z2.getId())
        );
        assertEquals(VysledokRozvozu.Typ.USPECH, vytvorenie.typ);
        assertEquals(1, controller.getCakajuceRozvozy().size());

        // Schválenie
        int id = controller.getCakajuceRozvozy().get(0).getId();
        VysledokRozvozu schvalenie = controller.schvalitRozvoz(id);

        assertEquals(VysledokRozvozu.Typ.USPECH, schvalenie.typ);
        assertEquals(0, controller.getCakajuceRozvozy().size());
        assertEquals(1, controller.getSchvaleneRozvozy().size());
    }
}