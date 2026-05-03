package obchod;

import java.util.List;

public class ValidationException extends RuntimeException {

    private final List<String> chyby;

    public ValidationException(List<String> chyby) {
        super(String.join("\n", chyby));
        this.chyby = chyby;
    }

    public List<String> getChyby() {
        return chyby;
    }
}
