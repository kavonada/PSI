package model;

public class Zakaznik {

    private String meno;
    private String email;
    private String telefon;

    public Zakaznik(String meno, String email, String telefon) {
        this.meno = meno;
        this.email = email;
        this.telefon = telefon;
    }


    @Override
    public String toString() {
        return meno + " | " + email + " | " + telefon;
    }
}