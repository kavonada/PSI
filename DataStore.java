import java.util.ArrayList;
import java.util.List;

public class DataStore {

    public static List<Zakazka> zakazky = new ArrayList<>();
    public static List<Material> materialy = new ArrayList<>();
    public static List<Rozvoz> rozvozy = new ArrayList<>();
    public static List<ObjednavkaMaterialu> cakajuceObjednavky = new ArrayList<>();
    public static List<Rozvoz> cakajuceRozvozy = new ArrayList<>();

    public static final String MANAZER_HESLO = "manazer123";

    static {
        materialy.add(new Material("Masív dub", 50, "WoodSupply s.r.o."));
        materialy.add(new Material("DTD doska", 20, "DrevMateriál a.s."));
    }
}