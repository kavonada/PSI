package model.use_case_3;

public class Dodavatel {

    private final String name;
    private final int deliveryDays;
    private final double price;

    public Dodavatel(String name, int deliveryDays, double price) {
        this.name = name;
        this.deliveryDays = deliveryDays;
        this.price = price;
    }

    public String getName() { return name; }
    public int getDeliveryDays() { return deliveryDays; }
    public double getPrice() { return price; }
}
