package model.use_case_3;

import java.util.List;

public class Objednavka {

    private static int counter = 1;

    private final int id;
    private final List<KosikPolozka> order_items;
    private final String supplier;
    private final double cost;
    private final String status;

    public Objednavka(List<KosikPolozka> polozky, String dodavatel) {
        this.id = counter++;
        this.order_items = polozky;
        this.supplier = dodavatel;
        this.cost = calculateCost();
        this.status = setStatus();
    }

    private double calculateCost() {
        double sum = 0;
        for (KosikPolozka p : getOrderItems()) {
            sum += p.getPrice();
        }
        return sum;
    }

    public int getId() { return id; }
    public List<KosikPolozka> getOrderItems() { return order_items; }
    public String getSupplier() { return supplier; }
    public double getCost() { return cost; }
    public String getStatus() { return status; }
    public String setStatus() {
        if (getCost() >= 1000)
            return "'Čaká na schválenie'";
        return "'Vytvorená'";
    }
}