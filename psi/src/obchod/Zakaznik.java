package obchod;

public class Zakaznik {

    private String meno;
    private String email;
    private String telefon;

    public Zakaznik(String meno, String email, String telefon) {
        this.meno = meno;
        this.email = email;
        this.telefon = telefon;
    }

    public String getMeno() {
        return meno;
    }

    public String getTelefon() {
        return telefon;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return meno + " | " + email + " | " + telefon;
    }
}