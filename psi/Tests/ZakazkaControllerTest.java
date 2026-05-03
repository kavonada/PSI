import controller.ZakazkaController;
import model.Zakazka;
import sklad.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ZakazkaControllerTest {

    private ZakazkaController controller;

    @BeforeEach
    void setUp() {
        controller = new ZakazkaController();
    }

    @Test
    void vytvorZakazku_validneUdaje_vytvoriZakazku() {
        List<Material> materialy = List.of(
                new Material("Drevo", 10, 5.0, 1)
        );

        Zakazka zakazka = controller.vytvorZakazku("Stôl", "Masívny dubový stôl",
                "Jozef Mrkvička", "jozko@stuba.sk", "0900123456",
                "Pekná", "12", "Bratislava", "84105",
                250.0,
                LocalDate.now().plusDays(10),
                materialy
        );

        assertNotNull(zakazka);
        assertEquals("Stôl", zakazka.getNazov());
        assertEquals("Masívny dubový stôl", zakazka.getPopis());
        assertEquals(250.0, zakazka.getCena());
        assertEquals(LocalDate.now().plusDays(10), zakazka.getTerminDorucenia());

        assertNotNull(zakazka.getZakaznik());
        assertEquals("Jozef Mrkvička", zakazka.getZakaznik().getMeno());
        assertEquals("jozko@stuba.sk", zakazka.getZakaznik().getEmail());
        assertEquals("0900123456", zakazka.getZakaznik().getTelefon());

        assertNotNull(zakazka.getDodaciaAdresa());
        assertEquals("Pekná", zakazka.getDodaciaAdresa().getUlica());
        assertEquals("12", zakazka.getDodaciaAdresa().getCisloDomu());
        assertEquals("Bratislava", zakazka.getDodaciaAdresa().getMesto());
        assertEquals("84105", zakazka.getDodaciaAdresa().getPsc());

        assertEquals(materialy, zakazka.getMaterialy());
    }

    @Test
    void vytvorZakazku_nullNazov_hodiVynimku() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.vytvorZakazku(null, "Masívny dubový stôl",
                        "Jozef Mrkvička", "jozko@stuba.sk", "0900123456",
                        "Pekná", "12", "Bratislava", "84105",
                        250.0,
                        LocalDate.now().plusDays(10),
                        List.of(new Material("Drevo", 10, 5.0, 1))
                )
        );

        assertEquals("Názov zákazky je povinný.", exception.getMessage());
    }

    @Test
    void vytvorZakazku_blankNazov_hodiVynimku() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.vytvorZakazku(" ", "Masívny dubový stôl",
                        "Jozef Mrkvička", "jozko@stuba.sk", "0900123456",
                        "Pekná", "12", "Bratislava", "84105",
                        250.0,
                        LocalDate.now().plusDays(10),
                        List.of(new Material("Drevo", 10, 5.0, 1))
                )
        );

        assertEquals("Názov zákazky je povinný.", exception.getMessage());
    }

    @Test
    void vytvorZakazku_nullMenoZakaznika_hodiVynimku() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.vytvorZakazku("Stôl", "Masívny dubový stôl",
                        null, "jozko@stuba.sk", "0900123456",
                        "Pekná", "12", "Bratislava", "84105",
                        250.0,
                        LocalDate.now().plusDays(10),
                        List.of(new Material("Drevo", 10, 5.0, 1))
                )
        );

        assertEquals("Meno zákazníka je povinné.", exception.getMessage());
    }

    @Test
    void vytvorZakazku_blankMenoZakaznika_hodiVynimku() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.vytvorZakazku("Stôl", "Masívny dubový stôl",
                        " ", "jozko@stuba.sk", "0900123456",
                        "Pekná", "12", "Bratislava", "84105",
                        250.0,
                        LocalDate.now().plusDays(10),
                        List.of(new Material("Drevo", 10, 5.0, 1))
                )
        );

        assertEquals("Meno zákazníka je povinné.", exception.getMessage());
    }

    @Test
    void vytvorZakazku_zapornaCena_hodiVynimku() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.vytvorZakazku("Stôl", "Masívny dubový stôl",
                        "Jozef Mrkvička", "jozko@stuba.sk", "0900123456",
                        "Pekná", "12", "Bratislava", "84105",
                        -250.0,
                        LocalDate.now().plusDays(10),
                        List.of(new Material("Drevo", 10, 5.0, 1))
                )
        );

        assertEquals("Cena nesmie byť záporná.", exception.getMessage());
    }

    @Test
    void vytvorZakazku_nullTerminDorucenia_hodiVynimku() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.vytvorZakazku("Stôl", "Masívny dubový stôl",
                        "Jozef Mrkvička", "jozko@stuba.sk", "0900123456",
                        "Pekná", "12", "Bratislava", "84105",
                        250.0,
                        null,
                        List.of(new Material("Drevo", 10, 5.0, 1))
                )
        );

        assertEquals("Termín doručenia je povinný.", exception.getMessage());
    }

    @Test
    void vytvorZakazku_terminVMiNulosti_hodiVynimku() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.vytvorZakazku("Stôl", "Masívny dubový stôl",
                        "Jozef Mrkvička", "jozko@stuba.sk", "0900123456",
                        "Pekná", "12", "Bratislava", "84105",
                        250.0,
                        LocalDate.now().minusDays(10),
                        List.of(new Material("Drevo", 10, 5.0, 1))
                )
        );

        assertEquals("Termín doručenia nemôže byť v minulosti.", exception.getMessage());
    }

    @Test
    void vytvorZakazku_nullMaterialy_hodiVynimku() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.vytvorZakazku("Stôl", "Masívny dubový stôl",
                        "Jozef Mrkvička", "jozko@stuba.sk", "0900123456",
                        "Pekná", "12", "Bratislava", "84105",
                        250.0,
                        LocalDate.now().plusDays(10),
                        null
                )
        );

        assertEquals("Musí byť zadaný aspoň jeden materiál.", exception.getMessage());
    }

    @Test
    void vytvorZakazku_prazdneMaterialy_hodiVynimku() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> controller.vytvorZakazku("Stôl", "Masívny dubový stôl",
                        "Jozef Mrkvička", "jozko@stuba.sk", "0900123456",
                        "Pekná", "12", "Bratislava", "84105",
                        250.0,
                        LocalDate.now().plusDays(10),
                        List.of()
                )
        );

        assertEquals("Musí byť zadaný aspoň jeden materiál.", exception.getMessage());
    }
}