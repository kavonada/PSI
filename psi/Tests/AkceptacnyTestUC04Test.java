import controller.RozvozController;
import controller.RozvozController.VysledokRozvozu;
import model.Adresa;
import model.DataStore;
import model.Zakazka;
import model.Zakaznik;
import model.use_case_3.Material;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AkceptacnyTestUC04Test {

    @Test
    void testVytvorenieASchvalenieRozvozu() {
        DataStore.getZakazky().clear();
        DataStore.cakajuceRozvozy.clear();
        DataStore.rozvozy.clear();

        RozvozController controller = new RozvozController();

        Zakaznik zakaznik = new Zakaznik("Test", "test@test.sk", "0900000000");
        Adresa adresa = new Adresa("Test", "1", "Test", "00000");
        List<Material> materialy = new ArrayList<>();

        Zakazka z1 = new Zakazka("Z1", "Popis", zakaznik, adresa, 100, LocalDate.now(), materialy);
        Zakazka z2 = new Zakazka("Z2", "Popis", zakaznik, adresa, 200, LocalDate.now(), materialy);

        DataStore.pridajZakazku(z1);
        DataStore.pridajZakazku(z2);

        VysledokRozvozu vysledok = controller.vytvorRozvoz(
                "TEST-VOZIDLO", 5, "10.10.2025", List.of(z1.getId(), z2.getId())
        );

        assertEquals(VysledokRozvozu.Typ.USPECH, vysledok.typ);
        assertEquals(1, controller.getCakajuceRozvozy().size());

        int id = controller.getCakajuceRozvozy().get(0).getId();
        VysledokRozvozu schvalenie = controller.schvalitRozvoz(id);

        assertEquals(VysledokRozvozu.Typ.USPECH, schvalenie.typ);
        assertEquals(0, controller.getCakajuceRozvozy().size());
        assertEquals(1, controller.getSchvaleneRozvozy().size());
    }
}