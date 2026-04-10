package model;

import java.util.ArrayList;
import java.util.List;

public class DataStore {

    public static List<Zakazka>            zakazky           = new ArrayList<>();
    public static List<Material> materialy = new ArrayList<>();
    public static List<Rozvoz>             rozvozy           = new ArrayList<>();
    public static List<ObjednavkaMaterialu> objednavky = new ArrayList<>();
    public static List<KosikPolozka> kosik = new ArrayList<>();
    public static List<Rozvoz>             cakajuceRozvozy   = new ArrayList<>();

    public static final String MANAZER_HESLO = "manazer123";

    static {
        materialy.add(new Material("Masív dub",  50, 15.00, 20));
        materialy.add(new Material("DTD doska",  15, 10.00, 20));
        materialy.add(new Material("MDF doska",   5, 12.00, 20));
    }
}
